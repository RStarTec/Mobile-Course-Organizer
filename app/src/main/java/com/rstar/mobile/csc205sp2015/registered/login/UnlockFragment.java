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


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Message;
import com.rstar.mobile.csc205sp2015.io.Savelog;


public class UnlockFragment extends Fragment {
    private static final String TAG = UnlockFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private String mPasscode = "";
    private EditText mPasscodeView = null;
    private Button mButton = null;

    private FieldTextWatcher mPasscodeTextWatcher = null;
    private OkButtonClickedListener mButtonClickedListener = null;

    public static UnlockFragment newInstance() {
        Bundle args = new Bundle();

           UnlockFragment fragment = new UnlockFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "onCreate()");

        if (!IO.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), Message.toastNoNetwork, Toast.LENGTH_SHORT).show();
        }

        setRetainInstance(true);

    } // end to implementing onCreate()



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Savelog.d(TAG, debug, "onCreateView()");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_unlock, container, false);
        mPasscodeView = (EditText) v.findViewById(R.id.fragmentUnlock_passcode);

        mPasscodeView.setText(mPasscode);
        mPasscodeTextWatcher = new FieldTextWatcher(this);
        mPasscodeView.addTextChangedListener(mPasscodeTextWatcher);


        mButton = (Button) v.findViewById(R.id.fragmentUnlock_button);
        mButtonClickedListener = new OkButtonClickedListener(this);
        mButton.setOnClickListener(mButtonClickedListener);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mPasscodeView!=null) {
            if (mPasscodeTextWatcher!=null) {
                mPasscodeView.removeTextChangedListener(mPasscodeTextWatcher);
            }
            mPasscodeView = null;
        }

        if (mButton!=null) {
            mButton.setOnClickListener(null);
            mButton = null;
        }

        if (mPasscodeTextWatcher!=null) {
            mPasscodeTextWatcher.cleanup();
            mPasscodeTextWatcher = null;
        }
        if (mButtonClickedListener!=null) {
            mButtonClickedListener.cleanup();
            mButtonClickedListener = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }




    private static class FieldTextWatcher implements TextWatcher {
        UnlockFragment hostFragment;

        public FieldTextWatcher(UnlockFragment hostFragment) {
            super();
            this.hostFragment = hostFragment;
        }
        @Override
        public void afterTextChanged(Editable arg0) {}
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        @Override
        public void onTextChanged(CharSequence c, int start, int before, int count) {

            String data = "";
            if (c!=null && c.toString().trim().length()>0) {
                data = c.toString().trim();
            }
            hostFragment.mPasscode = data;
        }
        public void cleanup() { hostFragment = null; }
    }

    private static class OkButtonClickedListener implements View.OnClickListener {
        UnlockFragment hostFragment;

        public OkButtonClickedListener(UnlockFragment hostFragment) {
            super();
            this.hostFragment = hostFragment;
        }
        @Override
        public void onClick(View view) {

            Savelog.d(TAG, debug, "Now verifying code: " + hostFragment.mPasscode);
            String response = hostFragment.mPasscode;
            
            if (Course.get(hostFragment.getActivity()).matchPasscode(response)) {
                Savelog.d(TAG, debug, "Server access unlocked.");
                ((UnlockActivity) hostFragment.getActivity()).downloadPrivateSite();
            }
            else {
                Savelog.d(TAG, debug, "bad server passcode.");
                Toast.makeText(hostFragment.getActivity(), Message.toastRequestFailed, Toast.LENGTH_SHORT).show();
                hostFragment.getActivity().finish();
            }
        }
        public void cleanup() { hostFragment = null; }
    }



}
