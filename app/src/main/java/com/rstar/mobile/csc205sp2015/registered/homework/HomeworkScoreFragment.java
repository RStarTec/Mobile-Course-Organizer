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
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Message;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.registered.api.ScoreApi;
import com.rstar.mobile.csc205sp2015.registered.login.Student;

import java.io.File;
import java.lang.ref.WeakReference;

public class HomeworkScoreFragment extends Fragment {
	private static final String TAG = HomeworkScoreFragment.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;
	
	private static final String EXTRA_ModuleNumber = HomeworkScoreFragment.class.getSimpleName()+".ModuleNumber";

    
	private int mModuleNumber;
    private double mScore = 0;
    private String mSubmittedData = "";
    private String mFeedback = "";

    private TextView mScoreView;
    private TextView mFeedbackView;
    private TextView mSubmittedView;

    private ScoreAsyncTask mScoreAsyncTask = null;

	// Supply the module number as an argument to the newly created hostFragment.
	public static HomeworkScoreFragment newInstance(int moduleNumber) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_ModuleNumber, moduleNumber);

		HomeworkScoreFragment fragment = new HomeworkScoreFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		Savelog.d(TAG, debug, "onCreate() entered");

		mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);

        Student student = new Student(getActivity());

        Student.Login login = student.loadLogin(getActivity());
        String mUserId = login.getUserId();
        String mPassword = login.getPassword();
        String mAccessCode = student.getAccessCode();
        mScoreAsyncTask = new ScoreAsyncTask(this, mUserId, mPassword, mAccessCode, mModuleNumber);
        mScoreAsyncTask.execute();

        setRetainInstance(true);

	} // end to implementing onCreate()
	
	
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v;
		Savelog.d(TAG, debug, "onCreateView() entered");

		v = inflater.inflate(R.layout.fragment_homework_score, parent, false);

        mScoreView = (TextView) v.findViewById(R.id.fragmentHomeworkScore_total_id);
        mFeedbackView = (TextView) v.findViewById(R.id.fragmentHomeworkScore_feedback_id);
        mSubmittedView = (TextView) v.findViewById(R.id.fragmentHomeworkScore_submitted_id);

        if (mScoreAsyncTask!=null && mScoreAsyncTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
            onFetchingCompleted();
        }

        return v;
	} // end to implementing onCreateView() 



    public void onFetchingCompleted() {
        if (mScoreView!=null) {
            mScoreView.setText(Double.toString(mScore));
        }
        if (mFeedbackView!=null) {
            mFeedbackView.setText(mFeedback);
        }
        if (mSubmittedView!=null) {
            mSubmittedView.setText(mSubmittedData);
        }
    }

	
	public int getmoduleNumber() {
		return mModuleNumber;
	}




    public static class ScoreAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private Context appContext;
        private final WeakReference<HomeworkScoreFragment> hostFragmentReference;
        Exception err = null;
        int moduleNumber;
        String userId;
        String password;
        String accessCode;
        double score = 0;
        String feedback = "";
        String submittedData = "";

        public ScoreAsyncTask(HomeworkScoreFragment hostFragment, String userId, String password, String accessCode, int moduleNumber) {
            super();
            this.appContext = hostFragment.getActivity().getApplicationContext();
            hostFragmentReference = new WeakReference<HomeworkScoreFragment>(hostFragment);
            this.moduleNumber = moduleNumber;
            this.userId = userId;
            this.password = password;
            this.accessCode = accessCode;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            boolean result = true;
            try {
                ScoreApi scoreApi;
                scoreApi = new ScoreApi(appContext, userId, password, accessCode, moduleNumber, ScoreApi.request_score);
                score = scoreApi.getScore(appContext);

                scoreApi = new ScoreApi(appContext, userId, password, accessCode, moduleNumber, ScoreApi.request_feedback);
                File feedbackFile = scoreApi.getFile(appContext);
                if (feedbackFile!=null) { // file is null if post is not OK
                    feedback = IO.loadFileAsString(appContext, feedbackFile);
                }
                scoreApi = new ScoreApi(appContext, userId, password, accessCode, moduleNumber, ScoreApi.request_submitted);
                File submittedFile = scoreApi.getFile(appContext);
                if (submittedFile!=null) { // file is null if post is not OK
                    submittedData = IO.loadFileAsString(appContext, submittedFile);
                }
            }
            catch (Exception e) {
                err = e;
                result = false;
            }
            return result;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (isCancelled()) {}

            if (hostFragmentReference != null) {
                final HomeworkScoreFragment hostFragment = hostFragmentReference.get();
                if (hostFragment != null) {

                    if (result==true) {
                        Savelog.d(TAG, debug, "Result:\n" + result);
                        // DONE getting data!!
                        hostFragment.mScore = score;
                        hostFragment.mFeedback = feedback;
                        hostFragment.mSubmittedData = submittedData;
                        hostFragment.onFetchingCompleted();
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
                            Log.e(TAG, "task failed.", err);
                        }
                        else {
                            // ???
                        }

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
