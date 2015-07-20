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
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.developer.DeveloperSettings;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Message;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.lang.ref.WeakReference;


public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final String EXTRA_Type = LoginFragment.class.getSimpleName()+".Type";

    // Make a copy of the types from activity. Use these locally.
    private static final int Type_new = LoginActivity.Type_new;
    private static final int Type_existing = LoginActivity.Type_existing;
    private static final int Type_passwd = LoginActivity.Type_passwd;
    private static final int Type_reset = LoginActivity.Type_reset;

    private static final int Type_default = LoginActivity.Type_default;


    private static final int field_userId = 0;
    private static final int field_email = 1;
    private static final int field_password = 2;
    private static final int field_newpassword = 3;

    private int mType = LoginActivity.Type_default;
    private Student mStudent = null;
    private String mUserId = "";
    private String mEmail = "";
    private String mPassword = "";
    private String mNewPassword = "";

    private TextView mTypeView = null;
    private TextView mUserIdLabelView = null;
    private TextView mEmailLabelView = null;
    private TextView mPasswordLabelView = null;
    private TextView mNewPasswordLabelView = null;
    private EditText mUserIdView = null;
    private EditText mEmailView = null;
    private EditText mPasswordView = null;
    private EditText mNewPasswordView = null;
    private Button mButton = null;

    private FieldTextWatcher mUserIdTextWatcher = null;
    private FieldTextWatcher mEmailTextWatcher = null;
    private FieldTextWatcher mPasswordTextWatcher = null;
    private FieldTextWatcher mNewPasswordTextWatcher = null;
    private OkButtonClickedListener mButtonClickedListener = null;

    private LoginAsyncTask mLoginAsyncTask = null;

    public static LoginFragment newInstance(int type) {
        Bundle args = new Bundle();

           LoginFragment fragment = new LoginFragment();
        args.putInt(EXTRA_Type, type);
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "onCreate()");

        mType = getArguments().getInt(EXTRA_Type, Type_default);
        mStudent = new Student(getActivity());

        if (debug && AppSettings.developerMode) {
            mEmail = DeveloperSettings.developerEmail;
        }

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
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mTypeView = (TextView) v.findViewById(R.id.fragmentLogin_type);
        mUserIdLabelView = (TextView) v.findViewById(R.id.fragmentLogin_userId_label);
        mEmailLabelView = (TextView) v.findViewById(R.id.fragmentLogin_email_label);
        mPasswordLabelView = (TextView) v.findViewById(R.id.fragmentLogin_password_label);
        mNewPasswordLabelView = (TextView) v.findViewById(R.id.fragmentLogin_newpassword_label);

        mUserIdView = (EditText) v.findViewById(R.id.fragmentLogin_userId);
        mEmailView = (EditText) v.findViewById(R.id.fragmentLogin_email);
        mPasswordView = (EditText) v.findViewById(R.id.fragmentLogin_password);
        mNewPasswordView = (EditText) v.findViewById(R.id.fragmentLogin_newpassword);

        // Always require userId
        mUserIdView.setText(mUserId);
        mUserIdTextWatcher = new FieldTextWatcher(this, field_userId);
        mUserIdView.addTextChangedListener(mUserIdTextWatcher);
        mUserIdLabelView.setVisibility(View.VISIBLE);

        if (mType==Type_new || mType==Type_existing || mType== Type_passwd) {
            mPasswordView.setText(mPassword);
            mPasswordTextWatcher = new FieldTextWatcher(this, field_password);
            mPasswordView.addTextChangedListener(mPasswordTextWatcher);
        }
        else {
            mPasswordView.setVisibility(View.GONE);
            mPasswordLabelView.setVisibility(View.GONE);
        }


        if (mType== Type_passwd) {
            mNewPasswordView.setText(mNewPassword);
            mNewPasswordTextWatcher = new FieldTextWatcher(this, field_newpassword);
            mNewPasswordView.addTextChangedListener(mNewPasswordTextWatcher);
        }
        else {
            mNewPasswordView.setVisibility(View.GONE);
            mNewPasswordLabelView.setVisibility(View.GONE);
        }

        if (mType==Type_new) {
            mEmailView.setText(mEmail);
            mEmailTextWatcher = new FieldTextWatcher(this, field_email);
            mEmailView.addTextChangedListener(mEmailTextWatcher);
        }
        else {
            mEmailView.setVisibility(View.GONE);
            mEmailLabelView.setVisibility(View.GONE);
        }

        if (mType==Type_new) {
            mTypeView.setText("Sign up and create new account");
        }
        else if (mType==Type_existing) {
            mTypeView.setText("Sign in to existing account");
        }
        else if (mType== Type_passwd) {
            mTypeView.setText("Change password");
        }
        else if (mType==Type_reset) {
            mTypeView.setText("Forgot password. Request substitute password through email.");
        }

        mButton = (Button) v.findViewById(R.id.fragmentLogin_button);
        mButtonClickedListener = new OkButtonClickedListener(this, mStudent, mType);
        mButton.setOnClickListener(mButtonClickedListener);

        return v;
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




    private static class FieldTextWatcher implements TextWatcher {
        LoginFragment hostFragment;
        int field;

        public FieldTextWatcher(LoginFragment hostFragment, int field) {
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
        }
        public void cleanup() { hostFragment = null; }
    }

    private static class OkButtonClickedListener implements View.OnClickListener {
        LoginFragment hostFragment;
        Student student;
        int type;
        public OkButtonClickedListener(LoginFragment hostFragment, Student student, int type) {
            super();
            this.hostFragment = hostFragment;
            this.student = student;
            this.type = type;
        }
        @Override
        public void onClick(View view) {

            // TODO: Note that if this device has already been signed up, then might request to delete account first.
            Savelog.d(TAG, debug, "Going to login: userid=" + hostFragment.mUserId +" password="+ hostFragment.mPassword +" newPassword="+ hostFragment.mNewPassword +" email="+ hostFragment.mEmail + " type="+hostFragment.mType);

            if (hostFragment.mLoginAsyncTask ==null) {
                // If not already running, then start it.
                hostFragment.mLoginAsyncTask = new LoginAsyncTask(hostFragment, student, type);
                hostFragment.mLoginAsyncTask.execute();
            }
        }
        public void cleanup() { hostFragment = null; }
    }



    private static class LoginAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private Context appContext;
        private final WeakReference<LoginFragment> hostFragmentReference;
        Student student;
        int type;
        String userId = "";
        String email = "";
        String password = "";
        String newPassword = "";
        Exception err = null;

        public LoginAsyncTask(LoginFragment hostFragment, Student student, int type)  {
            super();
            this.appContext = hostFragment.getActivity().getApplicationContext();
            hostFragmentReference = new WeakReference<LoginFragment>(hostFragment);
            this.student = student;
            this.type = type;
            this.userId = hostFragment.mUserId;
            this.email = hostFragment.mEmail;
            this.password = hostFragment.mPassword;
            this.newPassword = hostFragment.mNewPassword;
        }

        @Override
        protected Boolean doInBackground(Object... args) {
            if (IO.isNetworkAvailable(appContext)) {
                try {
                    boolean result = false;
                    if (type==Type_new)
                        result = student.signup(appContext, userId, password, email);
                    else if (type==Type_existing)
                        result = student.signin(appContext, userId, password);
                    else if (type==Type_passwd)
                        result = student.changePassword(appContext, userId, password, newPassword);
                    else if (type==Type_reset) {
                        result = student.resetPassword(appContext, userId);
                    }

                    return result;
                }
                catch (Exception e) {
                    err = e;
                    return false;
                }
            }
            else {
                err = new IO.NetworkUnavailableException("No network. Login canceled.");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (hostFragmentReference != null) {
                final LoginFragment hostFragment = hostFragmentReference.get();
                if (hostFragment != null) {
                    if (result) {
                        hostFragment.mStudent = student;
                        Savelog.d(TAG, debug, "Result: " + result + " access code = " + student.getAccessCode());
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

                    // Pass the result to the
                    LoginActivity activity = (LoginActivity) hostFragment.getActivity();
                    if (activity!=null && !activity.isFinishing()) {
                        activity.setReturnIntent(result);
                        activity.finish();
                    }
                }
            }
            Savelog.d(TAG, debug, "AsyncTask completed.");
        }
    }


}
