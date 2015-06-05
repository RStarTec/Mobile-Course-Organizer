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

package com.rstar.mobile.csc205sp2015.registered.login;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;


public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final String EXTRA_Type = LoginActivity.class.getSimpleName()+".Type";

    public static final int Type_new = 101;
    public static final int Type_existing = 102;
    public static final int Type_passwd = 103;
    public static final int Type_reset = 104;

    public static final int Type_default = Type_existing;

    private Fragment fragment;
    private int fragmentId;

    private int mType = Type_default;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "OnCreate()");

        if (savedInstanceState == null) {
            mType = getIntent().getIntExtra(EXTRA_Type, Type_default);
        }
        else {
            mType = savedInstanceState.getInt(EXTRA_Type);
        }

        setContentView(R.layout.activity_login);
        fragmentId = R.id.activityLogin_container;


        FragmentManager fm = getFragmentManager();
        fragment = fm.findFragmentById(fragmentId);
        if (fragment==null) {
            fragment = LoginFragment.newInstance(mType);
            fm.beginTransaction().add(fragmentId, fragment).commit();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(EXTRA_Type, mType);
        super.onSaveInstanceState(savedInstanceState);
    }


    public void refreshFragment(int newType) {
        FragmentManager fm = getFragmentManager();
        Fragment oldFragment = fm.findFragmentById(fragmentId);

        mType = newType;
        fragment = LoginFragment.newInstance(mType);

        if (oldFragment!=null) {
            fm.beginTransaction().replace(fragmentId, fragment).commit();
        }
        else {
            fm.beginTransaction().add(fragmentId, fragment).commit();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_login_new:
            {
                // Do not re-open if already there
                if (mType!=Type_new)
                    refreshFragment(Type_new);
                return true;
            }
            case R.id.menu_login_existing:
            {
                // Do not re-open if already there
                if (mType!=Type_existing)
                    refreshFragment(Type_existing);
                return true;
            }
            case R.id.menu_login_passwd:
            {
                // Do not re-open if already there
                if (mType!= Type_passwd)
                    refreshFragment(Type_passwd);
                return true;
            }
            case R.id.menu_login_reset:
            {
                // Do not re-open if already there
                if (mType!= Type_reset)
                    refreshFragment(Type_reset);
                return true;
            }

            // no default action
        }
        return super.onOptionsItemSelected(item);
    }


    public void setReturnIntent(boolean success) {
        Savelog.d(TAG, debug, "Return to caller for login type=" + mType + " result=" + success);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_Type, mType);
        if (success) {
            this.setResult(Activity.RESULT_OK, returnIntent);
        } else {
            this.setResult(Activity.RESULT_CANCELED, returnIntent);
        }
    }



    @Override
    public void onBackPressed() {
        setReturnIntent(false);  // say false because user canceled.
        finish();
    }


}
