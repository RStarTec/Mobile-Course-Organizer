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

package com.rstar.mobile.csc205sp2015.registered.homework;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.download.DownloadActivity;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.registered.login.LoginActivity;
import com.rstar.mobile.csc205sp2015.registered.login.Student;
import com.rstar.mobile.csc205sp2015.registered.login.UnlockActivity;


public class HomeworkActivity extends Activity {

    private static final String TAG = HomeworkActivity.class.getSimpleName() + "_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final int LoginRequestCode = HomeworkActivity.class.hashCode();  // must be unique


    public static final String EXTRA_ModuleNumber = HomeworkActivity.class.getSimpleName() + ".ModuleNumber";
    public static final String EXTRA_QuestionNumber = HomeworkActivity.class.getSimpleName() + ".QuestionNumber";
    public static final String EXTRA_Type = HomeworkActivity.class.getSimpleName() + ".Type";
    private static final String EXTRA_Pending = HomeworkActivity.class.getSimpleName() + ".Pending";

    public static final int NoPending = 0;
    public static final int Type_blank = 201;
    public static final int Type_edit = 202;
    public static final int Type_submit = 203;
    public static final int Type_score = 204;
    public static final int Type_default = Type_blank;

    private Fragment fragment;
    private int fragmentId;

    private int mModuleNumber;
    private int mQuestionNumber;
    private int mPending;

    private int mType = Type_default;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);
        fragmentId = R.id.activityHomework_container;

        Savelog.d(TAG, debug, "OnCreate()");


        if (savedInstanceState == null) {

            mModuleNumber = getIntent().getIntExtra(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
            mQuestionNumber = getIntent().getIntExtra(EXTRA_QuestionNumber, Homework.DefaultQuestionNumber);
            mType = getIntent().getIntExtra(EXTRA_Type, Type_default);
            mPending = NoPending;

            // When this activity is started from another activity,
            // the caller may not know the status of homework,
            // and may only request to open the default (blank fragment).
            // We override the default whenever homework is accessible
            // on device (also meaning that the user has access code).
            int status = Homework.Status.get(this, mModuleNumber);
            if (status==Homework.Status.OnDevice) mType = Type_edit;

        } else {
            mModuleNumber = savedInstanceState.getInt(EXTRA_ModuleNumber);
            mQuestionNumber = savedInstanceState.getInt(EXTRA_QuestionNumber);
            mType = savedInstanceState.getInt(EXTRA_Type);
            mPending = savedInstanceState.getInt(EXTRA_Pending);
        }

        FragmentManager fm = getFragmentManager();
        fragment = fm.findFragmentById(fragmentId);
        if (fragment == null) {
            if (mType == Type_edit) {
                fragment = HomeworkFragment.newInstance(mModuleNumber, mQuestionNumber);
            } else if (mType == Type_submit) {
                fragment = HomeworkSubmitFragment.newInstance(mModuleNumber);
            } else if (mType == Type_score) {
                fragment = HomeworkScoreFragment.newInstance(mModuleNumber);
            } else {
                fragment = HomeworkBlankFragment.newInstance(mModuleNumber);
            }

            fm.beginTransaction().add(fragmentId, fragment).commit();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(EXTRA_ModuleNumber, mModuleNumber);
        savedInstanceState.putInt(EXTRA_QuestionNumber, mQuestionNumber);
        savedInstanceState.putInt(EXTRA_Type, mType);
        savedInstanceState.putInt(EXTRA_Pending, mPending);
        super.onSaveInstanceState(savedInstanceState);
    }


    public void refreshFragment(int newType, int newModuleNumber, int newQuestionNumber) {
        FragmentManager fm = getFragmentManager();
        Fragment oldFragment = fm.findFragmentById(fragmentId);

        mType = newType;
        mModuleNumber = newModuleNumber;
        mQuestionNumber = newQuestionNumber;

        // Whenever fragment is refreshed, any pending request is cleared
        mPending = NoPending;

        if (mType == Type_edit) {
            fragment = HomeworkFragment.newInstance(mModuleNumber, mQuestionNumber);
        } else if (mType == Type_submit) {
            fragment = HomeworkSubmitFragment.newInstance(mModuleNumber);
        } else if (mType == Type_score) {
            fragment = HomeworkScoreFragment.newInstance(mModuleNumber);
        } else { // default
            fragment = HomeworkBlankFragment.newInstance(mModuleNumber);
        }

        if (oldFragment != null) {
            fm.beginTransaction().replace(fragmentId, fragment).commit();
        } else {
            fm.beginTransaction().add(fragmentId, fragment).commit();
        }
    }

    public void refreshQuestion(int newModuleNumber, int newQuestionNumber) {
        refreshFragment(Type_edit, newModuleNumber, newQuestionNumber);
    }

    public void openBlank() {
        refreshFragment(Type_blank, mModuleNumber, mQuestionNumber);
    }
    public void openHomework() {
        refreshFragment(Type_edit, mModuleNumber, mQuestionNumber);
    }
    public void openSubmit() {
        refreshFragment(Type_submit, mModuleNumber, mQuestionNumber);
    }
    public void openScore() {
        refreshFragment(Type_score, mModuleNumber, mQuestionNumber);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homework, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int status = Homework.Status.get(this, mModuleNumber);

        if (status==Homework.Status.OnDevice) {
            switch (item.getItemId()) {
                case R.id.menu_homework_edit: {
                    // Do not re-open homework if already there
                    if (mType != Type_edit)
                        openHomework();
                    return true;
                }
                case R.id.menu_homework_submit: {
                    // Do not go to submit if already there
                    if (mType != Type_submit)
                        openSubmit();
                    return true;
                }
                case R.id.menu_homework_score: {
                    // Allow user to refresh score multiple times

                    if (!PrivateSite.get(this).isInitialized()) {
                        Intent intent = new Intent(this, UnlockActivity.class);
                        this.startActivity(intent);
                        Savelog.d(TAG, debug, "started unlock. Not waiting for result.");
                    }
                    else {
                        Student student = new Student(this);
                        if (student.isLoginExpired(this)) {
                            Savelog.d(TAG, debug, "Login has expired");

                            mPending = Type_score;
                            // call LoginActivity now
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.putExtra(LoginActivity.EXTRA_Type, LoginActivity.Type_existing);
                            startActivityForResult(intent, LoginRequestCode);
                            Savelog.d(TAG, debug, "started search with requestCode=" + LoginRequestCode);
                        } else {
                            openScore();
                        }
                    }
                    return true;
                }
                // no default action
            }
        }
        else {
            // Freeze the menu and force user to re-login
            if (mType != Type_blank) {
                openBlank();
            }
        }

        return super.onOptionsItemSelected(item);
    }




    public void setReturnIntent(boolean success) {
        Savelog.d(TAG, debug, "Return to caller for homework type=" + mType + " result=" + success);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_Type, mType);
        returnIntent.putExtra(EXTRA_ModuleNumber, mModuleNumber);
        returnIntent.putExtra(EXTRA_QuestionNumber, mQuestionNumber);
        if (success) {
            this.setResult(Activity.RESULT_OK, returnIntent);
        } else {
            this.setResult(Activity.RESULT_CANCELED, returnIntent);
        }
    }


    @Override
    public void onBackPressed() {
        setReturnIntent(true);  // for now, always say true. Can use true/false to indicate whether changes are made
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Savelog.d(TAG, debug, "onActivityResult() requestCode=" + requestCode);

        // a login request has been successful.
        // This request was sent out from the HomeworkBlankFragment hosted on this activity.
        // The result comes back from LoginActivity.
        if (requestCode == HomeworkBlankFragment.LoginRequestCode) {
            if (resultCode==RESULT_OK && data!=null) {
                int loginType = data.getIntExtra(LoginActivity.EXTRA_Type, LoginActivity.Type_default);
                Savelog.d(TAG, debug, "Returned from LoginActivity with result loginType=" + loginType);
                if (loginType==LoginActivity.Type_new || loginType==LoginActivity.Type_existing) {
                    // If currently at a blank fragment and homework is downloaded
                    // and user has access code, then go to edit mode.
                    int status = Homework.Status.get(this, mModuleNumber);
                    if (status==Homework.Status.OnDevice && mType==Type_blank) {
                        mType = Type_edit;
                    }
                    refreshFragment(mType, mModuleNumber, mQuestionNumber);
                } // else, no change.
            }
        }

        // Request code was sent out from HomeworkBlankFragment
        // But when result comes back, the extra is from DownloadActivity
        else if (requestCode == HomeworkBlankFragment.DownloadHomeworkRequestCode) {
            if (resultCode==RESULT_OK && data!=null) {
                int downloadType = data.getIntExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_default);
                int moduleNumber = data.getIntExtra(DownloadActivity.EXTRA_ModuleNumber, Module.DefaultModuleNumber);
                if (moduleNumber!=mModuleNumber) {
                    Savelog.w(TAG, "downloaded homework module " + moduleNumber +" != requested module!" + mModuleNumber);
                }
                Savelog.d(TAG, debug, "get back resultCode for download request from blank homework");
                if(downloadType== DownloadActivity.Type_homework) {
                    int status = Homework.Status.get(this, mModuleNumber);
                    if (status==Homework.Status.OnDevice && mType==Type_blank) {
                        mType = Type_edit;
                    }
                    refreshFragment(mType, mModuleNumber, mQuestionNumber);
                }
            }
        }



        // a login request has been successful.
        // This request was sent out from this activity (when user wants to check score).
        // The result comes back from LoginActivity.
        else if (requestCode == HomeworkActivity.LoginRequestCode) {
            if (resultCode==RESULT_OK && data!=null) {
                int loginType = data.getIntExtra(LoginActivity.EXTRA_Type, LoginActivity.Type_default);
                Savelog.d(TAG, debug, "Returned from LoginActivity with result loginType=" + loginType);
                if (loginType==LoginActivity.Type_new || loginType==LoginActivity.Type_existing) {
                    if (mPending==Type_score) {
                        mType = Type_score;
                        mPending = NoPending;
                    }
                    refreshFragment(mType, mModuleNumber, mQuestionNumber);
                } // else, no change.
            }
        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}