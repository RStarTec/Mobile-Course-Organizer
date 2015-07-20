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


import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Message;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;

import java.lang.ref.WeakReference;


public class MasterFragment extends Fragment {
    private static final String TAG = MasterFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final String EXTRA_Type = MasterFragment.class.getSimpleName()+".Type";

    // Make a copy of the types from activity. Use these locally.
    private static final int Type_new = MasterApi.request_mastersignup;
    private static final int Type_existing = MasterApi.request_mastersignin;
    private static final int Type_passwd = MasterApi.request_masterpasswd;
    private static final int Type_reset = MasterApi.request_masterunset;
    private static final int Type_score = MasterApi.request_masterscore;
    private static final int Type_feedback = MasterApi.request_masterfeedback;
    private static final int Type_submitted = MasterApi.request_mastersubmitted;

    public static final int Type_default = MasterApi.request_mastersignup;


    private static final int field_userId = 0;
    private static final int field_email = 1;
    private static final int field_password = 2;
    private static final int field_newpassword = 3;
    private static final int field_module = 4;


    private int mType = Type_default;
    private String mUserId = "";
    private String mEmail = "";
    private String mPassword = "";
    private String mNewPassword = "";
    private int mModuleNumber = Module.DefaultModuleNumber;
    private String mResponse = "";

    private TextView mTypeView = null;
    private TextView mUserIdLabelView = null;
    private TextView mEmailLabelView = null;
    private TextView mPasswordLabelView = null;
    private TextView mNewPasswordLabelView = null;
    private TextView mModuleLabelView = null;
    private TextView mResponseLabelView = null;

    private EditText mUserIdView = null;
    private EditText mEmailView = null;
    private EditText mPasswordView = null;
    private EditText mNewPasswordView = null;
    private EditText mModuleView = null;
    private TextView mResponseView = null;
    private Button mButton = null;

    private FieldTextWatcher mUserIdTextWatcher = null;
    private FieldTextWatcher mEmailTextWatcher = null;
    private FieldTextWatcher mPasswordTextWatcher = null;
    private FieldTextWatcher mNewPasswordTextWatcher = null;
    private FieldTextWatcher mModuleTextWatcher = null;
    private OkButtonClickedListener mButtonClickedListener = null;

    private LoginAsyncTask mLoginAsyncTask = null;

    public static MasterFragment newInstance(int type) {
        Bundle args = new Bundle();

           MasterFragment fragment = new MasterFragment();
        args.putInt(EXTRA_Type, type);
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "onCreate()");

        mType = getArguments().getInt(EXTRA_Type, Type_default);

        if (debug) {
            mEmail = DeveloperSettings.developerEmail;
        }
        setRetainInstance(true);
        setHasOptionsMenu(true);

    } // end to implementing onCreate()



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Savelog.d(TAG, debug, "onCreateView()");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_master, container, false);

        mTypeView = (TextView) v.findViewById(R.id.fragmentMaster_type);
        mUserIdLabelView = (TextView) v.findViewById(R.id.fragmentMaster_userId_label);
        mEmailLabelView = (TextView) v.findViewById(R.id.fragmentMaster_email_label);
        mPasswordLabelView = (TextView) v.findViewById(R.id.fragmentMaster_password_label);
        mNewPasswordLabelView = (TextView) v.findViewById(R.id.fragmentMaster_newpassword_label);
        mModuleLabelView = (TextView) v.findViewById(R.id.fragmentMaster_module_label);
        mResponseLabelView = (TextView) v.findViewById(R.id.fragmentMaster_response_label);

        mUserIdView = (EditText) v.findViewById(R.id.fragmentMaster_userId);
        mEmailView = (EditText) v.findViewById(R.id.fragmentMaster_email);
        mPasswordView = (EditText) v.findViewById(R.id.fragmentMaster_password);
        mNewPasswordView = (EditText) v.findViewById(R.id.fragmentMaster_newpassword);
        mModuleView = (EditText) v.findViewById(R.id.fragmentMaster_module);
        mResponseView = (TextView) v.findViewById(R.id.fragmentMaster_response);

        // Always require userId
        mUserIdView.setText(mUserId);
        mUserIdTextWatcher = new FieldTextWatcher(this, field_userId);
        mUserIdView.addTextChangedListener(mUserIdTextWatcher);
        mUserIdLabelView.setVisibility(View.VISIBLE);

        // Some requires password
        if (mType==Type_new) {
            mPasswordView.setText(mPassword);
            mPasswordTextWatcher = new FieldTextWatcher(this, field_password);
            mPasswordView.addTextChangedListener(mPasswordTextWatcher);
        }
        else {
            mPasswordView.setVisibility(View.GONE);
            mPasswordLabelView.setVisibility(View.GONE);
        }

        // Some requires newPassword
        if (mType==Type_passwd) {
            mNewPasswordView.setText(mNewPassword);
            mNewPasswordTextWatcher = new FieldTextWatcher(this, field_newpassword);
            mNewPasswordView.addTextChangedListener(mNewPasswordTextWatcher);
        }
        else {
            mNewPasswordView.setVisibility(View.GONE);
            mNewPasswordLabelView.setVisibility(View.GONE);
        }

        // Some requires email
        if (mType==Type_new) {
            mEmailView.setText(mEmail);
            mEmailTextWatcher = new FieldTextWatcher(this, field_email);
            mEmailView.addTextChangedListener(mEmailTextWatcher);
        }
        else {
            mEmailView.setVisibility(View.GONE);
            mEmailLabelView.setVisibility(View.GONE);
        }

        // Some requires module
        if (mType==Type_score || mType==Type_feedback || mType==Type_submitted) {
            mModuleView.setText(Integer.toString(mModuleNumber));
            mModuleTextWatcher = new FieldTextWatcher(this, field_module);
            mModuleView.addTextChangedListener(mModuleTextWatcher);
        }
        else {
            mModuleView.setVisibility(View.GONE);
            mModuleLabelView.setVisibility(View.GONE);
        }

        // All use response
        mResponseView.setText(mResponse);

        // set up heading
        if (mType==Type_new) {
            mTypeView.setText("Master sign up and create new account");
        }
        else if (mType==Type_existing) {
            mTypeView.setText("Master sign in to existing account");
        }
        else if (mType== Type_passwd) {
            mTypeView.setText("Master change password");
        }
        else if (mType==Type_reset) {
            mTypeView.setText("Master clear password and email.");
        }
        else if (mType==Type_score) {
            mTypeView.setText("Master check score");
        }
        else if (mType==Type_feedback) {
            mTypeView.setText("Master check feedback");
        }
        else if (mType==Type_submitted) {
            mTypeView.setText("Master check submitted");
        }

        mButton = (Button) v.findViewById(R.id.fragmentMaster_button);
        mButtonClickedListener = new OkButtonClickedListener(this, mType);
        mButton.setOnClickListener(mButtonClickedListener);

        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.master, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_master_new:
            {
                // Do not re-open if already there
                if (mType!=Type_new)
                    ((DeveloperActivity) getActivity()).refreshMasterFragment(Type_new);
                return true;
            }
            case R.id.menu_master_existing:
            {
                // Do not re-open if already there
                if (mType!=Type_existing)
                    ((DeveloperActivity) getActivity()).refreshMasterFragment(Type_existing);
                return true;
            }
            case R.id.menu_master_passwd:
            {
                // Do not re-open if already there
                if (mType!= Type_passwd)
                    ((DeveloperActivity) getActivity()).refreshMasterFragment(Type_passwd);
                return true;
            }
            case R.id.menu_master_reset:
            {
                // Do not re-open if already there
                if (mType!= Type_reset)
                    ((DeveloperActivity) getActivity()).refreshMasterFragment(Type_reset);
                return true;
            }
            case R.id.menu_master_score: {
                // Do not re-open if already there
                if (mType!=Type_score)
                    ((DeveloperActivity) getActivity()).refreshMasterFragment(Type_score);
                return true;
            }
            case R.id.menu_master_feedback: {
                // Do not re-open if already there
                if (mType!=Type_feedback)
                    ((DeveloperActivity) getActivity()).refreshMasterFragment(Type_feedback);
                return true;
            }
            case R.id.menu_master_submitted: {
                // Do not re-open if already there
                if (mType!=Type_submitted)
                    ((DeveloperActivity) getActivity()).refreshMasterFragment(Type_submitted);
                return true;
            }

            // no default action
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUserIdView!=null) {
            if (mUserIdTextWatcher!=null) {
                mUserIdView.removeTextChangedListener(mUserIdTextWatcher);
            }
            mUserIdView = null;
        }
        if (mEmailView!=null) {
            if (mEmailTextWatcher!=null) {
                mEmailView.removeTextChangedListener(mEmailTextWatcher);
            }
            mEmailView = null;
        }
        if (mPasswordView!=null) {
            if (mPasswordTextWatcher!=null) {
                mPasswordView.removeTextChangedListener(mPasswordTextWatcher);
            }
            mPasswordView = null;
        }
        if (mNewPasswordView!=null) {
            if (mNewPasswordTextWatcher!=null) {
                mNewPasswordView.removeTextChangedListener(mNewPasswordTextWatcher);
            }
            mNewPasswordView = null;
        }
        if (mModuleView!=null) {
            if (mModuleTextWatcher!=null) {
                mModuleView.removeTextChangedListener(mModuleTextWatcher);
            }
            mModuleView = null;
        }

        if (mButton!=null) {
            mButton.setOnClickListener(null);
            mButton = null;
        }

        if (mUserIdTextWatcher!=null) {
            mUserIdTextWatcher.cleanup();
            mUserIdTextWatcher = null;
        }
        if (mEmailTextWatcher!=null) {
            mEmailTextWatcher.cleanup();
            mEmailTextWatcher = null;
        }
        if (mPasswordTextWatcher!=null) {
            mPasswordTextWatcher.cleanup();
            mPasswordTextWatcher = null;
        }
        if (mModuleTextWatcher!=null) {
            mModuleTextWatcher.cleanup();
            mModuleTextWatcher = null;
        }
        if (mButtonClickedListener!=null) {
            mButtonClickedListener.cleanup();
            mButtonClickedListener = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLoginAsyncTask!=null) {
            mLoginAsyncTask.cancel(true);
            mLoginAsyncTask = null;
        }
    }


    private void updateResponse(String data) {
        if (data!=null) {
            mResponse = data;
            mResponseView.setText(mResponse);
            mResponseView.setVisibility(View.VISIBLE);
            mResponseLabelView.setVisibility(View.VISIBLE);
        }
    }

    private static class FieldTextWatcher implements TextWatcher {
        MasterFragment hostFragment;
        int field;

        public FieldTextWatcher(MasterFragment hostFragment, int field) {
            super();
            this.hostFragment = hostFragment;
            this.field = field;
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

            if (field==field_userId) {
                hostFragment.mUserId = data;
            }
            else if (field==field_email) {
                hostFragment.mEmail = data;
            }
            else if (field==field_password) {
                hostFragment.mPassword = data;
            }
            else if (field==field_newpassword) {
                hostFragment.mNewPassword = data;
            }
            else if (field==field_module) {
                if (data.length()==0)
                    hostFragment.mModuleNumber = Module.DefaultModuleNumber;
                else
                    hostFragment.mModuleNumber = Integer.valueOf(data);
            }
        }
        public void cleanup() { hostFragment = null; }
    }

    private static class OkButtonClickedListener implements View.OnClickListener {
        MasterFragment hostFragment;
        int type;
        public OkButtonClickedListener(MasterFragment hostFragment, int type) {
            super();
            this.hostFragment = hostFragment;
            this.type = type;
        }
        @Override
        public void onClick(View view) {

            Savelog.d(TAG, debug, "Going to login: userid=" + hostFragment.mUserId
                    + " password="+ hostFragment.mPassword
                    + " newPassword="+ hostFragment.mNewPassword
                    + " email="+ hostFragment.mEmail
                    + " module=" + hostFragment.mModuleNumber
                    + " type="+hostFragment.mType);

            if (hostFragment.mLoginAsyncTask ==null
                    || hostFragment.mLoginAsyncTask.isCancelled()
                    || hostFragment.mLoginAsyncTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                // If not already running, then start it.
                hostFragment.mLoginAsyncTask = new LoginAsyncTask(hostFragment, type);
                hostFragment.mLoginAsyncTask.execute();
            }
        }
        public void cleanup() { hostFragment = null; }
    }



    private static class LoginAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private Context appContext;
        private final WeakReference<MasterFragment> hostFragmentReference;
        int type;
        String userId = "";
        String email = "";
        String password = "";
        String newPassword = "";
        int moduleNumber = Module.DefaultModuleNumber;
        MasterApi masterApi = null;
        Exception err = null;

        public LoginAsyncTask(MasterFragment hostFragment, int type)  {
            super();
            this.appContext = hostFragment.getActivity().getApplicationContext();
            hostFragmentReference = new WeakReference<MasterFragment>(hostFragment);
            this.type = type;
            this.userId = hostFragment.mUserId;
            this.email = hostFragment.mEmail;
            this.password = hostFragment.mPassword;
            this.newPassword = hostFragment.mNewPassword;
            this.moduleNumber = hostFragment.mModuleNumber;
        }

        @Override
        protected Boolean doInBackground(Object... args) {
            try {
                boolean result = false;
                if (type==Type_new || type==Type_existing || type==Type_reset || type==Type_passwd) {
                    masterApi = new MasterApi(appContext, userId, password, newPassword, email, type);
                    result = masterApi.isOK();
                }
                else if (type==Type_score || type==Type_feedback || type==Type_submitted) {
                    masterApi = new MasterApi(appContext, userId, moduleNumber, type);
                    result = masterApi.isOK();
                }
                else {
                    // unrecognized type
                }
                return result;
            }
            catch (Exception e) {
                err = e;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (hostFragmentReference != null) {
                final MasterFragment hostFragment = hostFragmentReference.get();
                if (hostFragment != null) {
                    if (result) {
                        Savelog.d(TAG, debug, "Result: " + result + " access code = " + masterApi.getAccessCode());
                        Toast.makeText(hostFragment.getActivity(), Message.toastRequestSuccess, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (err!=null) {
                            if (err instanceof IO.NetworkUnavailableException) {
                                Toast.makeText(hostFragment.getActivity(), Message.toastNoNetwork, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(hostFragment.getActivity(), Message.toastDataUnavailable, Toast.LENGTH_SHORT).show();
                            }
                            Savelog.e(TAG, "POST failed.\n", err);
                        }
                        else {
                            Toast.makeText(hostFragment.getActivity(), Message.toastRequestFailed, Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (masterApi!=null) {
                        // now, just update the response
                        hostFragment.updateResponse(masterApi.getData());
                    }

                }
            }
            Savelog.d(TAG, debug, "AsyncTask completed.");
        }
    }



}
