/*
 * Copyright (c) 2015. Annie Hui @ RStar Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rstar.mobile.csc205sp2015.edgeEffect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.rstar.mobile.csc205sp2015.io.Savelog;


/**
 * Created by AHui.
 */
public class OverScrollableLayout extends LinearLayout {
    private static final String TAG = OverScrollableLayout.class.getSimpleName()+"_class";
    private static final boolean debug = false;

    private EdgeEffect leftEdgeEffect=null;
    private EdgeEffect rightEdgeEffect=null;

    private static final float delta = 50;

    public OverScrollableLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public OverScrollableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public OverScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    public void setEdgeEffects(Context context, boolean left, boolean right) {
        if (!left && !right)
            setWillNotDraw(true);
        leftEdgeEffect = null;
        rightEdgeEffect = null;
        if (left) {
            Savelog.d(TAG, debug, "set up left edge effect");
            leftEdgeEffect = new EdgeEffect(context);
        }
        if (right) {
            Savelog.d(TAG, debug, "set up right edge effect");
            rightEdgeEffect = new EdgeEffect(context);
        }
    }

    public void windup() {
        Savelog.d(TAG, debug, "Wind up layout now");
        if (leftEdgeEffect!=null) {
            Savelog.d(TAG, debug, "pull on left side");
            leftEdgeEffect.onPull(delta);
        }
        else if (rightEdgeEffect!=null) {
            Savelog.d(TAG, debug, "pull on right side");
            rightEdgeEffect.onPull(delta);
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Savelog.d(TAG, debug, "DRAW NOW!!!");
        boolean needInvalidate = false;
        if (leftEdgeEffect!=null) {
            if (!leftEdgeEffect.isFinished()) {
                final int restoreCount = canvas.save();
                final int height = getHeight() - getPaddingTop() - getPaddingBottom();
                final int width = getWidth();

                Savelog.d(TAG, debug, "draw size="+width+"x"+height);
                canvas.rotate(270);
                canvas.translate(-height,0);

                leftEdgeEffect.setSize(height, width);
                needInvalidate = leftEdgeEffect.draw(canvas);
                canvas.restoreToCount(restoreCount);
                Rect rect = leftEdgeEffect.getBounds(false);
                Savelog.d(TAG, debug, "bounds of leftEdgeEffect: left=" + rect.left + ", top=" + rect.top + ", right=" + rect.right + ", bottom=" + rect.bottom);
            }
            else {
                leftEdgeEffect.finish();
            }
        }
        if (rightEdgeEffect!=null) {
            if (!rightEdgeEffect.isFinished()) {
                final int restoreCount = canvas.save();
                final int height = getHeight() - getPaddingTop() - getPaddingBottom();
                final int width = getWidth();

                Savelog.d(TAG, debug, "draw size="+width+"x"+height);

                canvas.rotate(90);
                canvas.translate(0,-width);

                rightEdgeEffect.setSize(height, width);
                needInvalidate = rightEdgeEffect.draw(canvas);
                canvas.restoreToCount(restoreCount);
                Rect rect = rightEdgeEffect.getBounds(false);
                Savelog.d(TAG, debug, "bounds of leftEdgeEffect: left=" + rect.left + ", top=" + rect.top + ", right=" + rect.right + ", bottom=" + rect.bottom);
            }
            else {
                rightEdgeEffect.finish();
            }
        }


        if (needInvalidate) invalidate();
    }
}
