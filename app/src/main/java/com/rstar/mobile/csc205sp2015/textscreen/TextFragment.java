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

package com.rstar.mobile.csc205sp2015.textscreen;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;

import java.io.File;


public class TextFragment extends Fragment {
    private static final String TAG = TextFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    // the fragment initialization parameters
    private static final String EXTRA_ModuleNumber = TextFragment.class.getSimpleName()+".ModuleNumber";
    private static final String EXTRA_PageNumber = TextFragment.class.getSimpleName()+".PageNumber";


    private int mModuleNumber;
    private int mPageNumber;

    private File mTextFile;
    private String mText;
    private TextView mTextView;
    private SeekBar mTextSizeSeekbar;
    private SeekBarOnChangedListener mSeekbarChangedListener;
    private TextSize mTextSize;

    public static TextFragment newInstance(int moduleNumber, int pageNumber) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_ModuleNumber, moduleNumber);
        args.putInt(EXTRA_PageNumber, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
            mPageNumber = getArguments().getInt(EXTRA_PageNumber, Module.DefaultPageNumber);

            Context context = getActivity();
            Module module = Course.get(context).getModule(mModuleNumber);

            mTextFile = module.getTranscriptFile(context, mPageNumber);
            try {
                mText = IO.loadFileAsString(context, mTextFile);
            }
            catch (Exception e) {
                Savelog.w(TAG, "No transcript found for Module " + mModuleNumber + " slide " + mPageNumber + ":" + mTextFile.getAbsolutePath());
                mText = ""; // empty
            }
            Savelog.d(TAG, debug, "Got transcript " + mTextFile.getAbsolutePath());

            mTextSize = new TextSize(getActivity());
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_text, container, false);

        mTextView = (TextView) v.findViewById(R.id.fragmentText_content);
        mTextView.setText(mText);
        mTextView.setVisibility(View.VISIBLE);

        final View view = inflater.inflate(R.layout.action_seekbar, container, false);
        mTextSizeSeekbar = (SeekBar) view.findViewById(R.id.action_seekbar_id);
        mSeekbarChangedListener = new SeekBarOnChangedListener(this, mTextSize.getMin(), mTextSize.getMax());
        mTextSizeSeekbar.setOnSeekBarChangeListener(mSeekbarChangedListener);
        mTextSizeSeekbar.setProgress(textSizeToProgress(mTextSize.get(), mTextSize.getMin(), mTextSize.getMax()));

        ActionBar actionBar = getActivity().getActionBar();

        actionBar.setCustomView(view, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();

        mTextSize.save(getActivity());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mTextSizeSeekbar!=null) {
            mTextSizeSeekbar.setOnSeekBarChangeListener(null);
            mTextSizeSeekbar = null;
        }
        if (mSeekbarChangedListener!=null) {
            mSeekbarChangedListener.cleanup();
            mSeekbarChangedListener = null;
        }
    }


    private static class SeekBarOnChangedListener implements SeekBar.OnSeekBarChangeListener {
        Context appContext;
        TextFragment hostFragment;
        int min;
        int max;
        public SeekBarOnChangedListener(TextFragment hostFragment, int min, int max) {
            this.hostFragment = hostFragment;
            this.min = min;
            this.max = max;
            this.appContext = hostFragment.getActivity().getApplicationContext();
        }
        public void cleanup() {
            hostFragment = null;
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            hostFragment.mTextSize.set(progressToTextSize(progress, min, max));
            hostFragment.mTextView.setTextSize(hostFragment.mTextSize.get());
            Savelog.d(TAG, debug, "Textsize=" + hostFragment.mTextSize.get());
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }



    private int textSizeToProgress(int textSize, int min, int max) {
        double percentage = (double)(textSize-min)/(max-min)*100;
        return (int)Math.round(percentage);
    }

    private static int progressToTextSize(int progress, int min, int max) {
        double value = (double)(progress)*0.01*(max-min) + min;
        return (int) Math.round(value);
    }


}
