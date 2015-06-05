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

package com.rstar.mobile.csc205sp2015.module;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.io.File;
import java.io.IOException;

public class QuizDialogFragment extends DialogFragment {
	private static final String TAG = QuizDialogFragment.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;

	public static final String dialogTag = QuizDialogFragment.class.getSimpleName()+"_tag";
    private static final String EXTRA_ModuleNumber = QuizDialogFragment.class.getSimpleName()+".ModuleNumber";
    private static final String EXTRA_PageNumber = QuizDialogFragment.class.getSimpleName()+".PageNumber";
    private static final String EXTRA_studentAnswer = QuizDialogFragment.class.getSimpleName()+".StudentAnswer";
    private static final String EXTRA_checked = QuizDialogFragment.class.getSimpleName()+".Checked";

    private int mModuleNumber;
    private int mPageNumber;

    private String mQuestion = "";
    private String mStudentAnswer = "";
    private String mModelAnswer = "";
    private boolean mChecked = false;

    private TextView mQuestionView;
    private EditText mStudentAnswerView;
    private TextView mModelAnswerView;
    private Button mButton;
    QuizTextWatcher mTextWatcher;
    OnButtonClickedListener mButtonClickedListener;

    private Button mOkButton = null;

    public static QuizDialogFragment newInstance(int moduleNumber, int pageNumber) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ModuleNumber, moduleNumber);
        args.putInt(EXTRA_PageNumber, pageNumber);
        args.putString(EXTRA_studentAnswer, "");
        args.putBoolean(EXTRA_checked, false);
        
		QuizDialogFragment fragment = new QuizDialogFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Savelog.d(TAG, debug, "onCreate() entered");

        mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        mPageNumber = getArguments().getInt(EXTRA_PageNumber, Module.DefaultPageNumber);
        mStudentAnswer = getArguments().getString(EXTRA_studentAnswer, "");
        mChecked = getArguments().getBoolean(EXTRA_checked, false);


        // Load everything in the quiz file
        Module module = Course.get(getActivity()).getModule(mModuleNumber);
        File quizFile = module.getQuizFile(getActivity(), mPageNumber);
        try {
            // TODO: be more specific in the structure of the quiz file
            String data = IO.loadFileAsString(getActivity(), quizFile);
            String parts[] = data.split("\\n"); // For now assume first line is question and second line is answer
            mQuestion = parts[0];
            mModelAnswer = parts[1];
        } catch (IOException e) {
            Savelog.w(TAG, "Cannot load " + quizFile.getAbsolutePath());
            mQuestion = "(empty)";
            mModelAnswer = "(empty)";
        }

		Savelog.d(TAG, debug, "This dialog fragment is NOT retained.");
	}

	
	/* This dialog has a title, a TextView and one button (OK).
	 */
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_quiz, null);

        mQuestionView = (TextView) v.findViewById(R.id.dialogQuiz_question);
        mButton = (Button) v.findViewById(R.id.dialogQuiz_checkButton);
        mStudentAnswerView = (EditText) v.findViewById(R.id.dialogQuiz_studentAnswer);
        mModelAnswerView = (TextView) v.findViewById(R.id.dialogQuiz_modelAnswer);

        mQuestionView.setText(mQuestion);
        mModelAnswerView.setText(mModelAnswer);
        if (!mChecked) mModelAnswerView.setVisibility(View.INVISIBLE);
        else mModelAnswerView.setVisibility(View.VISIBLE);

        if (mStudentAnswer!=null && mStudentAnswer.length()>0)
            mStudentAnswerView.setText(mStudentAnswer);

        mTextWatcher = new QuizTextWatcher(this);
        mModelAnswerView.addTextChangedListener(mTextWatcher);

        mButton.setOnClickListener(null);
        OnButtonClickedListener buttonClickedListener = new OnButtonClickedListener(this);
        mButton.setOnClickListener(buttonClickedListener);

		/* Use the Builder class for convenient dialog construction.
		 * The dialog builder just needs to handle OK.
		 */
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v)
			.setPositiveButton(R.string.button_OK, null);
		
		Dialog dialog = builder.create();
		return dialog;
		
	} // end to onCreateDialog()
	
	
	@Override
	public void onStart() {
		super.onStart();
		AlertDialog d = (AlertDialog) getDialog();
		if (d!=null) {
			mOkButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
		}
	}

	@Override
	public void onDestroyView() {
		/* As of Aug 2013, Dialog Fragment has a bug with its 
		 * SetRetainedInstance() method. Therefore, the following
		 * need to be done to retain the dialog fragment
		 */
		if (getDialog()!=null && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();
        if (mModelAnswerView!=null && mTextWatcher!=null)
            mModelAnswerView.removeTextChangedListener(mTextWatcher);

        mTextWatcher = null;
        mModelAnswerView = null;
        if (mButton!=null)
            mButton.setOnClickListener(null);
        if (mButtonClickedListener!=null)
            mButtonClickedListener=null;
        if (mOkButton!=null) {
            mOkButton.setOnClickListener(null);
            mOkButton = null;
        }

    }

	@Override
	public void onDestroy() {
		super.onDestroy();
	}



    private static class QuizTextWatcher implements TextWatcher {
        QuizDialogFragment hostFragment;
        public QuizTextWatcher(QuizDialogFragment hostFragment) {
            super();
            this.hostFragment = hostFragment;
        }
        @Override
        public void afterTextChanged(Editable arg0) {}
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        @Override
        public void onTextChanged(CharSequence c, int start, int before, int count) {
            if (c!=null && c.toString().trim().length()>0) {
                hostFragment.mStudentAnswer = c.toString().trim();
            }
            else {
                hostFragment.mStudentAnswer = "";
            }
            // Save the page number after change
            hostFragment.getArguments().putString(EXTRA_studentAnswer, hostFragment.mStudentAnswer);
        }
        public void cleanup() { hostFragment = null; }
    }

    private static class OnButtonClickedListener implements View.OnClickListener {
        QuizDialogFragment hostFragment;
        public OnButtonClickedListener(QuizDialogFragment hostFragment) {
            super();
            this.hostFragment = hostFragment;
        }
        @Override
        public void onClick(View view) {
            // Only check once.
            if (!hostFragment.mChecked) {
                Savelog.d(TAG, debug, "Now disclose answer.");
                hostFragment.mChecked = true;
                hostFragment.mModelAnswerView.setVisibility(View.VISIBLE);
                hostFragment.getArguments().putBoolean(EXTRA_checked, hostFragment.mChecked);
            }
        }
    }
}
