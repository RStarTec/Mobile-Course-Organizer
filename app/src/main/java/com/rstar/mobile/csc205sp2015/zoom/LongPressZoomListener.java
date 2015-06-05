/*
 * Copyright (c) 2010, Sony Ericsson Mobile Communication AB. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this 
 *      list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 *    * Neither the name of the Sony Ericsson Mobile Communication AB nor the names
 *      of its contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.rstar.mobile.csc205sp2015.zoom;

import android.content.Context;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.io.Savelog;

/**
 * Listener for controlling zoom state through touch events
 */
public class LongPressZoomListener implements View.OnTouchListener {
    private static final String TAG = LongPressZoomListener.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    /**
     * Enum defining listener modes. Before the view is touched the listener is
     * in the UNDEFINED mode. Once touch starts it can enter either one of the
     * other two modes: If the user scrolls over the view the listener will
     * enter PAN mode, if the user lets his finger rest and makes a longpress
     * the listener will enter ZOOM mode.
     */
    private enum Mode {
        UNDEFINED, PAN, ZOOM, RESET
    }

    /** Time of tactile feedback vibration when entering zoom mode */
    private static final long VIBRATE_TIME = 50;
    /** Current listener mode */
    private Mode mMode = Mode.UNDEFINED;
    /** Zoom control to manipulate */
    private ZoomControl mZoomControl;
    /** X-coordinate of previously handled touch event */
    private float mX;
    /** Y-coordinate of previously handled touch event */
    private float mY;
    /** X-coordinate of latest down event */
    private float mDownX;
    /** Y-coordinate of latest down event */
    private float mDownY;
    /** Distance touch can wander before we think it's scrolling */
    private final int mScaledTouchSlop;
    /** Duration in ms before a press turns into a long press */
    private final int mLongPressTimeout;
    /** Vibrator for tactile feedback */
    private final Vibrator mVibrator;
    // Duration in ms between two taps
    private long mDoubleTapTimeout = 0;
    private long mLastUpEventTime;

    /**
     * Creates a new instance
     * 
     * @param context Application context
     */
    public LongPressZoomListener(Context context) {
        mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        mDoubleTapTimeout = ViewConfiguration.getDoubleTapTimeout();
    }


    /**
     * Sets the zoom control to manipulate
     * 
     * @param control Zoom control
     */
    public void setZoomControl(ZoomControl control) {
        mZoomControl = control;
    }

    /**
     * Runnable that enters zoom mode
     */
    private final Runnable mLongPressRunnable = new Runnable() {
        public void run() {
            mMode = Mode.ZOOM;
            mVibrator.vibrate(VIBRATE_TIME);
        }
    };

    // implements View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        Savelog.d(TAG, debug, "onTouch() called");
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Savelog.d(TAG, debug, "action down code:" + action);
                v.postDelayed(mLongPressRunnable, mLongPressTimeout);
                mDownX = x;
                mDownY = y;
                mX = x;
                mY = y;
                return true;

            case MotionEvent.ACTION_MOVE:
            {
                Savelog.d(TAG, debug, "action move code:" + action);
                final float dx = (x - mX) / v.getWidth();
                final float dy = (y - mY) / v.getHeight();

                if (mMode == Mode.ZOOM) { // zoom mode
                    mZoomControl.zoom((float)Math.pow(20, -dy), mDownX / v.getWidth(), mDownY
                            / v.getHeight());
                } else if (mMode == Mode.PAN) {  // pan mode
                    mZoomControl.pan(-dx, -dy);
                } else {  // drag mode
                    final float scrollX = mDownX - x;
                    final float scrollY = mDownY - y;

                    final float dist = (float)Math.sqrt(scrollX * scrollX + scrollY * scrollY);

                    if (dist >= mScaledTouchSlop) {
                        v.removeCallbacks(mLongPressRunnable);
                        mMode = Mode.PAN;
                    }
                }

                mX = x;
                mY = y;

                Savelog.d(TAG, debug, "move event handled.");
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                boolean state;
                Savelog.d(TAG, debug, "action up code:" + action);
                if (mMode!=Mode.ZOOM && mZoomControl.isMinZoom()) {
                    Savelog.d(TAG, debug, "non-pan with min zoom. Unhandled by imageView.");
                    state = false;
                }
                else {
                    state = true;

                    long thisUpEventTime = System.currentTimeMillis();
                    Savelog.d(TAG, debug, "last up=" + mLastUpEventTime + " this up=" + thisUpEventTime );
                    if (thisUpEventTime-mLastUpEventTime<=mDoubleTapTimeout) {
                        mMode =Mode.RESET;
                        mZoomControl.resetZoomState();
                        Savelog.d(TAG, debug, "double tab registered.");
                    }
                    else {
                        mMode = Mode.UNDEFINED;
                    }
                    mLastUpEventTime = thisUpEventTime;
                    Savelog.d(TAG, debug, "mode changed to " + mMode);
                }
                // always remove the callback once the up event is detected
                v.removeCallbacks(mLongPressRunnable);
                return state;
            }

            default: {
                Savelog.d(TAG, debug, "action default=" + action);
                mMode = Mode.UNDEFINED;
                v.removeCallbacks(mLongPressRunnable);
                return true;
            }
        }

    }

}
