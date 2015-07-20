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

package com.rstar.mobile.csc205sp2015.module;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.homework.Homework;
import com.rstar.mobile.csc205sp2015.registered.homework.HomeworkBlankFragment;
import com.rstar.mobile.csc205sp2015.registered.homework.HomeworkFragment;

public abstract class DrawerActivity extends Activity {
    private static final String TAG = DrawerActivity.class.getSimpleName()+"_class";
    private static final boolean debug = true;

    private static final double leftFraction = 2/3.0; // The left drawer should be 2/3 the width of screen
    private static final double rightFraction = 1/3.0; // The right drawer should be 1/3 the width of screen

    protected static final int leftFragmentId = R.id.activityDrawer_leftFragment;
    protected static final int rightFragmentId = R.id.activityDrawer_rightFragment;


    private int rightDrawerWidth = 0;
    private int leftDrawerWidth = 0;

    private DrawerLayout mDrawerLayout = null;

    Handler handlerTimer = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activityDrawers_layout);

        // Reset the width of the right drawer
        rightDrawerWidth = (int) (getResources().getDisplayMetrics().widthPixels*rightFraction);
        FrameLayout rightDrawerLayout = (FrameLayout) findViewById(rightFragmentId);
        ViewGroup.LayoutParams rightParams = rightDrawerLayout.getLayoutParams();
        rightParams.width = rightDrawerWidth;
        rightDrawerLayout.setLayoutParams(rightParams);

        leftDrawerWidth = (int) (getResources().getDisplayMetrics().widthPixels*leftFraction);
        FrameLayout leftDrawerLayout = (FrameLayout) findViewById(leftFragmentId);
        ViewGroup.LayoutParams leftParams = leftDrawerLayout.getLayoutParams();
        leftParams.width = leftDrawerWidth;
        leftDrawerLayout.setLayoutParams(leftParams);

    }




    
    @Override
    public void onDestroy() {

        if (mDrawerLayout!=null) {
            mDrawerLayout.setDrawerListener(null);
            mDrawerLayout = null;
        }
        super.onDestroy();
    }




    public int getLeftDrawerWidth() {
        return leftDrawerWidth;
    }
    public int getRightDrawerWidth() {
        return rightDrawerWidth;
    }

    protected void closeDrawers() {
        mDrawerLayout.closeDrawers();
    }

    // Used by the right drawer. When a slide has been selected in that fragment, the
    // pageNumber needs to be passed back to the activity.
    abstract void onFinishSlideDrawerSelection(int newPageNumber);

    public void refreshLeftDrawer(int newModuleNumber, int newQuestionNumber) {
        FragmentManager fm = getFragmentManager();
        int id = leftFragmentId;

        Fragment oldFragment = fm.findFragmentById(id);
        Fragment newFragment;

        int status = Homework.Status.get(this, newModuleNumber);
        Savelog.d(TAG, debug, "refreshLeftDrawer status=" + status);
        if (status==Homework.Status.OnDevice) {
            Savelog.d(TAG, debug, "refresh to homework");
            newFragment = HomeworkFragment.newInstance(newModuleNumber, newQuestionNumber);
        }
        else {
            Savelog.d(TAG, debug, "refresh to blank");
            newFragment = HomeworkBlankFragment.newInstance(newModuleNumber);
        }
        if (oldFragment!=null) {
            fm.beginTransaction().replace(id, newFragment).commit();
        }
        else {
            fm.beginTransaction().add(id, newFragment).commit();
        }
    }


}
