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

package com.rstar.mobile.csc205sp2015.info;


import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;


public class HelpActivity extends Activity {
    private static final String TAG = HelpActivity.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;
    
    private HelpFragment fragment;
    private int fragmentId;

    //OnCreate Method:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "onCreate()");

        setContentView(R.layout.activity_help);
        fragmentId = R.id.activityHelp_container;

        // Check if fragment already exists
        FragmentManager fm = getFragmentManager();
        fragment = (HelpFragment) fm.findFragmentById(fragmentId);
        if (fragment == null) {
            fragment = HelpFragment.newInstance();
            fm.beginTransaction().add(fragmentId, fragment).commit();
        }
    }
}
