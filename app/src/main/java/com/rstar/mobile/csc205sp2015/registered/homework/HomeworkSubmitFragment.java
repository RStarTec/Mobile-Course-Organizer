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
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Message;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.registered.login.Student;
import com.rstar.mobile.csc205sp2015.registered.login.UnlockActivity;

import java.lang.ref.WeakReference;

public class HomeworkSubmitFragment extends Fragment {
	private static final String TAG = HomeworkSubmitFragment.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;
	
	private static final String EXTRA_ModuleNumber = HomeworkSubmitFragment.class.getSimpleName()+".ModuleNumber";

    
	private int mModuleNumber;
    private int mNumberOfQuestions;


    private Student mStudent;
    private Answer mAnswer;
    private SubmitAsyncTask mSubmitAsyncTask = null;

    private SubmitOnClickListener mSubmitOnClickListener = null;
    private TextView mTextView = null;
    private Button mButton = null;

	// Supply the module number as an argument to the newly created hostFragment.
	public static HomeworkSubmitFragment newInstance(int moduleNumber) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_ModuleNumber, moduleNumber);

		HomeworkSubmitFragment fragment = new HomeworkSubmitFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		Savelog.d(TAG, debug, "onCreate() entered");

		mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);

        mStudent = new Student(getActivity());
        
        Homework homework = new Homework(getActivity(), mModuleNumber);
        if (homework.isInstalled(getActivity())) {
            mNumberOfQuestions = homework.getNumberOfQuestions();
            mAnswer = new Answer(getActivity(), mStudent.getAccessCode(), mModuleNumber, mNumberOfQuestions);
        }

        setRetainInstance(true);

	} // end to implementing onCreate()
	
	
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v;
		Savelog.d(TAG, debug, "onCreateView() entered");

		v = inflater.inflate(R.layout.fragment_homework_submit, parent, false);

        mTextView = (TextView) v.findViewById(R.id.fragmentHomeworkSubmit_text_id);
        mButton = (Button) v.findViewById(R.id.fragmentHomeworkSubmit_button_id);

        mSubmitOnClickListener = new SubmitOnClickListener(this);
        mButton.setOnClickListener(mSubmitOnClickListener);
        if (mAnswer !=null) {
            mTextView.setText(mAnswer.prepareSubmitText(getActivity()));
            mButton.setClickable(true);
        }
        else {
            mTextView.setText("(No data available for submission)");
            mButton.setClickable(false);

        }
        return v;
	} // end to implementing onCreateView() 
	
	




	
	public int getmoduleNumber() {
		return mModuleNumber;
	}

	



    static private class SubmitOnClickListener implements View.OnClickListener {
        HomeworkSubmitFragment hostFragment;
        public SubmitOnClickListener(HomeworkSubmitFragment hostFragment) {
            super();
            this.hostFragment = hostFragment;
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
                // Create asynctask to do the submission
                if (hostFragment.mAnswer != null && hostFragment.mSubmitAsyncTask == null) {
                    hostFragment.mSubmitAsyncTask = new SubmitAsyncTask(hostFragment, hostFragment.mAnswer);
                    hostFragment.mSubmitAsyncTask.execute();
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


        if (mSubmitOnClickListener!=null) {
            mSubmitOnClickListener.cleanup();
            mSubmitOnClickListener = null;
        }
    }






    public static class SubmitAsyncTask extends AsyncTask<Object, Void, String> {
        private Context appContext;
        private final WeakReference<HomeworkSubmitFragment> hostFragmentReference;
        private Answer answer;
        Exception err = null;

        public SubmitAsyncTask(HomeworkSubmitFragment hostFragment, Answer answer) {
            super();
            this.answer = answer;
            this.appContext = hostFragment.getActivity().getApplicationContext();
            hostFragmentReference = new WeakReference<HomeworkSubmitFragment>(hostFragment);
        }

        @Override
        protected String doInBackground(Object... params) {
            String result = null;
            if (IO.isNetworkAvailable(appContext)) {
                try {
                    result = answer.submit(appContext);
                }
                catch (Exception e) {
                    err = e;
                }
            }
            else {
                err = new IO.NetworkUnavailableException("No network. Submit canceled");
            }
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (isCancelled()) {}

            if (hostFragmentReference != null) {
                final HomeworkSubmitFragment hostFragment = hostFragmentReference.get();
                if (hostFragment != null) {
                    Activity activity = hostFragment.getActivity();

                    if (result!=null) {
                        Savelog.d(TAG, debug, "Result from submit:\n" + result);
                        // DONE!!
                        Toast.makeText(hostFragment.getActivity(), Message.toastRequestSuccess, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (err!=null) {
                            if (err instanceof IO.NetworkUnavailableException) {
                                Toast.makeText(hostFragment.getActivity(), Message.toastNoNetwork, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(hostFragment.getActivity(), Message.toastRequestFailed, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(hostFragment.getActivity(), Message.toastRequestFailed, Toast.LENGTH_SHORT).show();
                        }
                        Log.w(TAG, "task failed.");
                    }

                    // make sure to close this activity when all is done.
                    if (activity!=null && !activity.isFinishing()) {
                        activity.finish();
                    }
                }
            }
            cleanup();
            Savelog.d(TAG, debug, "AsyncTask completed.");
        }

        void cleanup() {
            appContext = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cleanup();
            Savelog.d(TAG, debug, "AsyncTask canceled.");
        }
    }
}
