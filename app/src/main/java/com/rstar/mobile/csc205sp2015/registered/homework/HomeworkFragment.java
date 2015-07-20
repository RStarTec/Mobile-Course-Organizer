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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.module.ModuleActivity;
import com.rstar.mobile.csc205sp2015.registered.login.Student;

import java.io.File;
import java.io.IOException;

public class HomeworkFragment extends Fragment {
    private static final String TAG = HomeworkFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;
    
    private static final String EXTRA_ModuleNumber = HomeworkFragment.class.getSimpleName()+".ModuleNumber";
    private static final String EXTRA_QuestionNumber = HomeworkFragment.class.getSimpleName()+".QuestionNumber";

    public static final int DefaultQuestionNumber = 1;

    private int mModuleNumber;
    private int mNumberOfQuestions;
    private int mQuestionNumber;
    private int mPosition;

    private Homework mHomework;

    private Student mStudent;
    private Answer mAnswer;
    private String mInput;

    private String mQuestionLabels[];
    private ArrayAdapter<String> mQuestionAdapter = null;

    private Bitmap mBitmap = null;
    private AnswerTextWatcher mAnswerTextWatcher = null;
    private QuestionSelectedListener mQuestionSelectedListener = null;
    private SaveOnClickListener mSaveOnClickListener = null;
    private Spinner mSpinner = null;
    private TextView mScoreView = null;
    private TextView mTextView = null;
    private ImageView mImageView = null;
    private EditText mAnswerView = null;
    private Button mButton = null;

    // Supply the module number as an argument to the newly created hostFragment.
    public static HomeworkFragment newInstance(int moduleNumber, int questionNumber) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ModuleNumber, moduleNumber);
        args.putInt(EXTRA_QuestionNumber, questionNumber);

        HomeworkFragment fragment = new HomeworkFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        Savelog.d(TAG, debug, "onCreate() entered");

        mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        mQuestionNumber = getArguments().getInt(EXTRA_QuestionNumber, DefaultQuestionNumber);

        mPosition = mQuestionNumber - 1;

        mStudent = new Student(getActivity());

        mHomework = new Homework(getActivity(), mModuleNumber);
        mNumberOfQuestions = mHomework.getNumberOfQuestions();

        mQuestionLabels = new String[mNumberOfQuestions];
        for (int index=0; index<mNumberOfQuestions; index++) {
            int questionNumber = index+1;
            mQuestionLabels[index] = Homework.getQuestionLabel(mModuleNumber, questionNumber);
        }


        mAnswer = new Answer(getActivity(), mStudent.getAccessCode(), mModuleNumber, mNumberOfQuestions);
        File answerFile = mAnswer.getTextFile(getActivity(), mQuestionNumber);

        mInput = "";
        if (answerFile!=null && answerFile.exists()) {
            try {
                mInput = IO.loadFileAsString(getActivity(), answerFile);
            } catch (IOException e) {
                Savelog.w(TAG, "Cannot load answer from " + answerFile.getAbsolutePath());
            }
        }

        setRetainInstance(true);

    } // end to implementing onCreate()
    
    
    
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v;
        Savelog.d(TAG, debug, "onCreateView() entered");

        v = inflater.inflate(R.layout.fragment_homework, parent, false);

        mSpinner = (Spinner) v.findViewById(R.id.fragmentHomework_question_id);
        mScoreView = (TextView) v.findViewById(R.id.fragmentHomework_points_id);
        mTextView = (TextView) v.findViewById(R.id.fragmentHomework_text_id);
        mImageView = (ImageView) v.findViewById(R.id.fragmentHomework_bitmap_id);
        mAnswerView = (EditText) v.findViewById(R.id.fragmentHomework_answer_id);
        mButton = (Button) v.findViewById(R.id.fragmentHomework_button_id);


        String questionText;
        if (mHomework.isTextAvailable(mQuestionNumber)) {
            File textFile = mHomework.getTextFile(getActivity(), mQuestionNumber);
            try {
                questionText = IO.loadFileAsString(getActivity(), textFile);
            } catch (Exception e) {
                Savelog.w(TAG, "Cannot load file " + textFile.getAbsolutePath());
                questionText = "";
            }
            mTextView.setText(questionText);
        }
        else {
            mTextView.setVisibility(View.GONE);
        }


        if (mHomework.isPictureAvailable(mQuestionNumber)) {
            String filename = mHomework.getPictureFile(getActivity(), mQuestionNumber).getAbsolutePath();
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            mBitmap = BitmapFactory.decodeFile(filename, bitmapOptions);

            mImageView.setImageBitmap(mBitmap);
            mImageView.setVisibility(View.VISIBLE);
        }
        else {
            mImageView.setVisibility(View.GONE);
        }

        double points = mHomework.getPoints(mQuestionNumber);
        String pointLabel = Double.toString(points) + "pt" + (points>1 ? "s" : "");
        mScoreView.setText(pointLabel);

        mAnswerView.setText(mInput);

        mAnswerTextWatcher = new AnswerTextWatcher(this, mPosition);

        mAnswerView.addTextChangedListener(mAnswerTextWatcher);

        mQuestionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mQuestionLabels);
        // Note on default style of spinner: (see res/values/style_skyblue.xml)
        // The spinner itself uses style=DropDownListView
        // The simple_spinner_dropdown_item uses style=SpinnerItemDropDownItem

        mSpinner.setAdapter(mQuestionAdapter);
        mSpinner.setSelection(mPosition);
        mQuestionSelectedListener = new QuestionSelectedListener(this);
        mSpinner.setOnItemSelectedListener(mQuestionSelectedListener);

        mSaveOnClickListener = new SaveOnClickListener(this);
        mButton.setOnClickListener(mSaveOnClickListener);
        return v;
    } // end to implementing onCreateView() 
    
    




    
    public int getmoduleNumber() {
        return mModuleNumber;
    }

    



    private static class AnswerTextWatcher implements TextWatcher {
        HomeworkFragment hostFragment;
        int position;
        public AnswerTextWatcher(HomeworkFragment fragment, int position) {
            super();
            this.hostFragment = fragment;
            this.position = position;
        }
        public void cleanup() {
            hostFragment = null;
        }
        @Override
        public void afterTextChanged(Editable arg0) {}

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void onTextChanged(CharSequence c, int start, int before, int count) {
            if (c==null) c="";  // do not allow c to be null
            hostFragment.mInput = c.toString().trim();
            Savelog.d(TAG, debug, "New answer for question " + position + ": " + hostFragment.mInput );

        }

    }

    private static class QuestionSelectedListener implements AdapterView.OnItemSelectedListener {
        HomeworkFragment hostFragment;

        public QuestionSelectedListener(HomeworkFragment hostFragment) {
            this.hostFragment = hostFragment;
        }
        public void cleanup() {
            hostFragment = null;
        }
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long itemId) {
            Savelog.d(TAG, debug, "Selected option " + position);
            int oldPos = hostFragment.mPosition;

            if (oldPos!=position) {
                int newQuestionNumber = position+1;
                if (hostFragment.getActivity().getClass().equals(HomeworkActivity.class))
                    ((HomeworkActivity)hostFragment.getActivity()).refreshQuestion(hostFragment.mModuleNumber, newQuestionNumber);
                else if (hostFragment.getActivity().getClass().equals(ModuleActivity.class))
                    ((ModuleActivity)hostFragment.getActivity()).refreshLeftDrawer(hostFragment.mModuleNumber, newQuestionNumber);
            }

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    }


    static private class SaveOnClickListener implements View.OnClickListener {
        HomeworkFragment hostFragment;
        public SaveOnClickListener(HomeworkFragment hostFragment) {
            super();
            this.hostFragment = hostFragment;
        }
        public void cleanup() { hostFragment = null; }

        @Override
        public void onClick(View v) {
            // now need to save the answer to a file
            Context context = hostFragment.getActivity();
            int questionNumber = hostFragment.mQuestionNumber;
            File textFile = hostFragment.mAnswer.getTextFile(context, questionNumber);
            try {
                IO.saveStringAsFile(context, textFile, hostFragment.mInput);
            } catch (IOException e) {
                Savelog.e(TAG, "Cannot save file " + textFile.getAbsolutePath() + " with data: " + hostFragment.mInput, e);
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mImageView!=null) {
            mImageView.setImageBitmap(null);
            mImageView = null;
        }

        if (mSpinner!=null) {
            mSpinner.setOnItemSelectedListener(null);
            mSpinner.setAdapter(null);
            mSpinner = null;
        }

        if (mAnswerView!=null) {
            if (mAnswerTextWatcher!=null) {
                mAnswerView.removeTextChangedListener(mAnswerTextWatcher);
            }
        }
        if (mButton!=null) {
            mButton.setOnClickListener(null);
        }
        mTextView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAnswerTextWatcher!=null) {
            mAnswerTextWatcher.cleanup();
            mAnswerTextWatcher = null;
        }

        if (mQuestionAdapter!=null) {
            mQuestionAdapter = null;
        }

        if (mSaveOnClickListener!=null) {
            mSaveOnClickListener.cleanup();
            mSaveOnClickListener = null;
        }
        if (mQuestionSelectedListener!=null) {
            mQuestionSelectedListener.cleanup();
            mQuestionSelectedListener = null;
        }
        if (mBitmap!=null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }


}
