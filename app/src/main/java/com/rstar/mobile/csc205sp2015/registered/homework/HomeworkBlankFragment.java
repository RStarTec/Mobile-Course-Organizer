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


import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.download.DownloadActivity;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.registered.login.LoginActivity;
import com.rstar.mobile.csc205sp2015.registered.login.UnlockActivity;

public class HomeworkBlankFragment extends Fragment {
    private static final String TAG = HomeworkBlankFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;
    
    private static final String EXTRA_ModuleNumber = HomeworkBlankFragment.class.getSimpleName()+".ModuleNumber";
    private static final String EXTRA_Status = HomeworkBlankFragment.class.getSimpleName()+".Status";

    public static final int DownloadHomeworkRequestCode = HomeworkBlankFragment.class.hashCode();  // must be unique
    public static final int LoginRequestCode = HomeworkBlankFragment.class.hashCode() + 1;  // must be unique

    private static final int IconDownload = R.drawable.ic_action_download;
    private static final int IconLogin = R.drawable.ic_action_accounts;

    private static final int Status_OnDevice = Homework.Status.OnDevice;
    private static final int Status_NeedDownload = Homework.Status.NeedDownload;
    private static final int Status_NeedLogin = Homework.Status.NeedLogin;
    private static final int Status_NotAvailable = Homework.Status.NotAvailable;
    private static final int Status_Default = Homework.Status.Default;

    private int mModuleNumber;
    private int mStatus = Status_Default;

    private ButtonOnClickListener mButtonOnClickListener = null;
    private TextView mTextView = null;
    private ImageButton mButton = null;

    // Supply the module number as an argument to the newly created hostFragment.
    public static HomeworkBlankFragment newInstance(int moduleNumber) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ModuleNumber, moduleNumber);

        HomeworkBlankFragment fragment = new HomeworkBlankFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        Savelog.d(TAG, debug, "onCreate() entered");

        mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        mStatus = Homework.Status.get(getActivity(), mModuleNumber);

        setRetainInstance(true);

    } // end to implementing onCreate()
    
    
    
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v;
        Savelog.d(TAG, debug, "onCreateView() entered");

        v = inflater.inflate(R.layout.fragment_homework_blank, parent, false);

        mTextView = (TextView) v.findViewById(R.id.fragmentHomeworkBlank_text_id);
        mButton = (ImageButton) v.findViewById(R.id.fragmentHomeworkBlank_button_id);

        if (mStatus==Status_NeedLogin) {
            mTextView.setText(getString(R.string.homework_login));
            mButtonOnClickListener = new ButtonOnClickListener(this, mStatus);
            mButton.setOnClickListener(mButtonOnClickListener);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), IconLogin);
            mButton.setImageBitmap(bitmap);
            mButton.setVisibility(View.VISIBLE);
        }
        else if (mStatus==Status_NeedDownload) {
            mTextView.setText(getString(R.string.homework_download));
            mButtonOnClickListener = new ButtonOnClickListener(this, mStatus);
            mButton.setOnClickListener(mButtonOnClickListener);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), IconDownload);
            mButton.setImageBitmap(bitmap);
            mButton.setVisibility(View.VISIBLE);
        }
        else {
            mTextView.setText(getString(R.string.homework_unavail));
            mButton.setVisibility(View.GONE);
        }
        return v;
    } // end to implementing onCreateView() 
    
    


    public int getModuleNumber() {
        return mModuleNumber;
    }


    static private class ButtonOnClickListener implements View.OnClickListener {
        HomeworkBlankFragment hostFragment;
        int type;

        public ButtonOnClickListener(HomeworkBlankFragment hostFragment, int type) {
            super();
            this.hostFragment = hostFragment;
            this.type = type;
        }
        public void cleanup() { hostFragment = null; }

        @Override
        public void onClick(View v) {
            if (!PrivateSite.get(hostFragment.getActivity()).isInitialized()) {
                Intent intent = new Intent(hostFragment.getActivity(), UnlockActivity.class);
                hostFragment.getActivity().startActivity(intent);
                Savelog.d(TAG, debug, "started unlock. Not waiting for result.");
            }
            else {
                if (type==Status_NeedLogin) {
                    Intent intent = new Intent(hostFragment.getActivity(), LoginActivity.class);
                    intent.putExtra(LoginActivity.EXTRA_Type, LoginActivity.Type_new);
                    hostFragment.getActivity().startActivityForResult(intent, LoginRequestCode);
                    Savelog.d(TAG, debug, "started login with requestCode=" + LoginRequestCode);
                }
                else if (type==Status_NeedDownload) {
                    // NOTE: This requires the host activity to handle the result returned from download.
                    Intent intent = new Intent(hostFragment.getActivity(), DownloadActivity.class);
                    intent.putExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_homework);
                    intent.putExtra(DownloadActivity.EXTRA_ModuleNumber, hostFragment.getModuleNumber());
                    intent.putExtra(DownloadActivity.EXTRA_Trim, AppSettings.Trim);
                    hostFragment.getActivity().startActivityForResult(intent, DownloadHomeworkRequestCode);
                    Savelog.d(TAG, debug, "started download with requestCode=" + DownloadHomeworkRequestCode);
                }
                else {
                    Savelog.w(TAG, "Unexpected status type " + type);
                }
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mButton!=null) {
            mButton.setOnClickListener(null);
        }
        mTextView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mButtonOnClickListener !=null) {
            mButtonOnClickListener.cleanup();
            mButtonOnClickListener = null;
        }
    }


}
