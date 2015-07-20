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

package com.rstar.mobile.csc205sp2015.download;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;

public class DownloadActivity extends Activity {
    private static final String TAG = DownloadActivity.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final String EXTRA_Trim = DownloadActivity.class.getSimpleName()+".trim";
    public static final String EXTRA_ModuleNumber = DownloadActivity.class.getSimpleName()+".ModuleNumber";
    public static final String EXTRA_PageNumber = DownloadActivity.class.getSimpleName()+".PageNumber";
    public static final String EXTRA_Type = DownloadActivity.class.getSimpleName()+".Type";

    public static final int Type_course = 1;
    public static final int Type_privateSite = 2;
    public static final int Type_module = 3;
    public static final int Type_homework = 5;
    public static final int Type_video = 6;
    public static final int Type_courseReload = 7;
    public static final int Type_default = Type_module;

    private TextView statusView;

    private Fragment fragment;
    private int fragmentId;

    private int mType = Type_default;
    private boolean mTrim;
    private int mModuleNumber;
    private int mPageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "OnCreate()");

        mType = getIntent().getIntExtra(EXTRA_Type, Type_default);
        mTrim = getIntent().getBooleanExtra(EXTRA_Trim, AppSettings.Trim);
        mModuleNumber = getIntent().getIntExtra(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        mPageNumber = getIntent().getIntExtra(EXTRA_PageNumber, Module.DefaultPageNumber);


        setContentView(R.layout.activity_download);
        fragmentId = R.id.activityDownload_container;

        statusView = (TextView) findViewById(R.id.download_status);
        
        FragmentManager fm = getFragmentManager();

        fragment = fm.findFragmentById(fragmentId);
        if (fragment==null) {
            fragment = DownloadFragment.newInstance(mType, mTrim, mModuleNumber, mPageNumber);
            if (fragment!=null) {
                fm.beginTransaction().add(fragmentId, fragment).commit();
            }
        }
        else {
            // If fragment already exists, then get the progress
            postProgress(((DownloadFragment) fragment).getProcess());
        }
    }


    public void postProgress(String status) {
        if (statusView!=null)
            statusView.setText(status);
    }


    public void setReturnIntent(boolean success) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_Type, mType);
        returnIntent.putExtra(EXTRA_ModuleNumber, mModuleNumber);
        returnIntent.putExtra(EXTRA_PageNumber, mPageNumber);
        Savelog.d(TAG, debug, "Sending returnIntent ");
        if (success) {
            this.setResult(Activity.RESULT_OK, returnIntent);
        }
        else {
            this.setResult(Activity.RESULT_CANCELED, returnIntent);
        }
    }
}
