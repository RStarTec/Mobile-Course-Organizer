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

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.download.DownloadActivity;
import com.rstar.mobile.csc205sp2015.io.Savelog;


/* Use this activity to unlock server access
 *
 */
public class UnlockActivity extends Activity {

    private static final String TAG = UnlockActivity.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final int DownloadPrivateSiteRequestCode = UnlockActivity.class.hashCode();  // must be unique

    private Fragment fragment;
    private int fragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "OnCreate()");

        setContentView(R.layout.activity_unlock);
        fragmentId = R.id.activityUnlock_container;


        FragmentManager fm = getFragmentManager();
        fragment = fm.findFragmentById(fragmentId);
        if (fragment==null) {
            fragment = UnlockFragment.newInstance();
            fm.beginTransaction().add(fragmentId, fragment).commit();
        }
    }


    public void downloadPrivateSite() {
        Intent intent = new Intent(this, DownloadActivity.class);
        intent.putExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_privateSite);
        intent.putExtra(DownloadActivity.EXTRA_Trim, AppSettings.Trim);
        this.startActivityForResult(intent, DownloadPrivateSiteRequestCode);
        Savelog.d(TAG, debug, "started download with requestCode=" + DownloadPrivateSiteRequestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UnlockActivity.DownloadPrivateSiteRequestCode) {
            boolean status = false;
            if (resultCode == RESULT_OK && data!=null) {
                int downloadType = data.getIntExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_default);
                if (downloadType==DownloadActivity.Type_privateSite) {
                    status = true;
                    finish();
                    Savelog.d(TAG, debug, "Success downloading private site ");
                }
            }
            if (!status) {
                Savelog.d(TAG, debug, "Failed to download private site ");
            }
        }
        else {
            // Pass result to parent activity. Very important!!!
            super.onActivityResult(requestCode, resultCode, data);
        }
    }//onActivityResult



    public void setReturnIntent(boolean success) {
        Savelog.d(TAG, debug, "Return to caller unlock result=" + success);
        Intent returnIntent = new Intent();
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
