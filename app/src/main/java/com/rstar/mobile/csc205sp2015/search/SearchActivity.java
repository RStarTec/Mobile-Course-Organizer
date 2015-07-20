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

package com.rstar.mobile.csc205sp2015.search;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;

public class SearchActivity extends Activity {
    private static final String TAG = SearchActivity.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final String EXTRA_ModuleNumber = SearchActivity.class.getSimpleName() + ".ModuleNumber";
    public static final String EXTRA_Keyword = SearchActivity.class.getSimpleName()+"_class" + ".Keyword";

    private Fragment mSearchFragment = null;

    private int mModuleNumber;
    private String mKeyword;

    private int fragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "Starting search activity.");
        
        setContentView(R.layout.activity_search);
        fragmentId = R.id.activitySearch_container;

        mModuleNumber = getIntent().getIntExtra(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        mKeyword = getIntent().getStringExtra(EXTRA_Keyword);
        if (mKeyword==null) mKeyword = "";

        FragmentManager fm = getFragmentManager();

        mSearchFragment = fm.findFragmentById(fragmentId);
        if (mSearchFragment ==null) {
            mSearchFragment = SearchFragment.newInstance(mModuleNumber, mKeyword);
            fm.beginTransaction().add(fragmentId, mSearchFragment).commit();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Savelog.d(TAG, debug, "Destroying search activity.");
    }


}
