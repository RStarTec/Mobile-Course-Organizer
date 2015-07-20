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

package com.rstar.mobile.csc205sp2015.developer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;

public class DeveloperActivity extends Activity {
    public static final String TAG = DeveloperActivity.class.getSimpleName()+"_class";

    public static final String EXTRA_Type = DeveloperActivity.class.getSimpleName()+".Type";

    public static final int Type_post = 1;
    public static final int Type_master = 2;
    public static final int Type_masterpost = 3;
    public static final int Type_default = Type_post;

    private int type = Type_default;
    private Fragment fragment;
    private int fragmentId;
    //OnCreate Method:
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        fragmentId = R.id.activityDeveloper_container;

        type = getIntent().getIntExtra(EXTRA_Type, Type_default);

        // Check if fragment already exists
        FragmentManager fm = getFragmentManager();
        fragment = fm.findFragmentById(fragmentId);
        if (fragment == null) {
            if (type==Type_post) {
                fragment = PostFragment.newInstance();
            }
            else if (type==Type_master) {
                fragment = MasterFragment.newInstance(MasterFragment.Type_default);
            }
            else if (type==Type_masterpost) {
                fragment = MasterPostFragment.newInstance();
            }

            fm.beginTransaction().add(fragmentId, fragment).commit();
        }
    }

    public void refreshMasterFragment(int newMasterType) {
        if (type==Type_master) {
            FragmentManager fm = getFragmentManager();
            Fragment oldFragment = fm.findFragmentById(fragmentId);

            fragment = MasterFragment.newInstance(newMasterType);

            if (oldFragment!=null) {
                fm.beginTransaction().replace(fragmentId, fragment).commit();
            }
            else {
                fm.beginTransaction().add(fragmentId, fragment).commit();
            }
        }
        else {
            Savelog.w(TAG, "Refresh failed. The current fragment not of the same type.");
        }
    }
}
