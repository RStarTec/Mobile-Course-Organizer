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

package com.rstar.mobile.csc205sp2015.textscreen;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;


public class TextActivity extends Activity {

    private static final String TAG = TextActivity.class.getSimpleName() + "_class";
    private static final boolean debug = true;

    public static final String EXTRA_ModuleNumber = TextActivity.class.getSimpleName() + ".ModuleNumber";
    public static final String EXTRA_PageNumber = TextActivity.class.getSimpleName() + ".PageNumber";

    private int mModuleNumber;
    private int mPageNumber;

    private int fragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        fragmentId = R.id.activityText_container;

        Savelog.d(TAG, debug, "onCreate()");

        mModuleNumber = getIntent().getIntExtra(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        mPageNumber = getIntent().getIntExtra(EXTRA_PageNumber, Module.DefaultPageNumber);

        // Check if fragment already exists
        FragmentManager fm = getFragmentManager();
        Fragment fragment;
        fragment = fm.findFragmentById(fragmentId);
        if (fragment == null) {
            fragment = TextFragment.newInstance(mModuleNumber, mPageNumber);
            fm.beginTransaction().add(fragmentId, fragment).commit();
        }

    }

}
