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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.edgeEffect.OverScrollableLayout;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.zoom.ImageZoomView;
import com.rstar.mobile.csc205sp2015.zoom.LongPressZoomListener;
import com.rstar.mobile.csc205sp2015.zoom.ZoomControl;

import java.io.File;



public class PageFragment extends Fragment {
    private static final String TAG = PageFragment.class.getSimpleName() + "_class";
    private static final boolean debug = true;

    // the fragment initialization parameters
    private static final String EXTRA_ModuleNumber = PageFragment.class.getSimpleName() + ".ModuleNumber";
    private static final String EXTRA_PageNumber = PageFragment.class.getSimpleName() + ".PageNumber";

    private static final String DefaultTranscript = "(empty)";
    private static final int ButtonTypeEmpty = 0;
    private static final int ButtonTypeQuiz = 1;
    private static final int ButtonTypeExtras = 2;
    private static final int ButtonTypeVideo = 3;
    private static final int IconExtras = R.drawable.ic_action_about;
    private static final int IconQuiz = R.drawable.ic_action_help;
    private static final int IconVideo = R.drawable.ic_movie;
    private static final int MaxButtons = 3; // maximum number of buttons

    private int mModuleNumber;
    private int mPageNumber;
    private int mNumberOfPages;

    private File mSlideFile;
    private Bitmap mSlideBitmap = null;
    private ImageZoomView mSlideView = null;

    private String mText;
    private TextView mTextView;
    private ImageButton mButton[] = new ImageButton[MaxButtons];
    private int mButtonType[] = new int[MaxButtons];
    private View.OnClickListener mOnClickListener[] = new View.OnClickListener[MaxButtons];
    private int mButtonCount = 0;


    /**
     * Constant used as menu item id for resetting zoom state
     */
    private static final int MENU_ID_RESET = 0;
    /**
     * Zoom control
     */
    private ZoomControl mZoomControl;
    /**
     * On touch listener for zoom view
     */
    private LongPressZoomListener mZoomListener;
    private OverScrollableLayout mPageLayout;


    public static PageFragment newInstance(int moduleNumber, int pageNumber) {
        PageFragment fragment = new PageFragment();
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
            mNumberOfPages = module.getNumberOfPages();

            mSlideFile = module.getSlideFile(context, mPageNumber);
            Savelog.d(TAG, debug, "Got slide " + mSlideFile.getAbsolutePath());
            mSlideBitmap = BitmapFactory.decodeFile(mSlideFile.getAbsolutePath());

            // Transcript file may or may not exist.
            mText = DefaultTranscript; // empty
            File transcriptFile = module.getTranscriptFile(context, mPageNumber);
            if (transcriptFile != null && transcriptFile.exists()) {
                try {
                    mText = IO.loadFileAsString(context, transcriptFile);
                    Savelog.d(TAG, debug, "Got transcript " + transcriptFile.getAbsolutePath());
                } catch (Exception e) {
                    Savelog.w(TAG, "No transcript found for Module " + mModuleNumber + " slide " + mPageNumber + ":" + transcriptFile.getAbsolutePath());
                    mText = DefaultTranscript; // empty
                }
            }


            // Not allow to have multiple items for a button. Priority: Video>Extra>Quiz.
            File quizFile = module.getQuizFile(context, mPageNumber);
            File extrasFile = module.getExtrasFile(context, mPageNumber);

            if (module.isVideoAvailable(mPageNumber)) {
                Savelog.d(TAG, debug, "Video exists in for page " + mPageNumber);
                mButtonType[mButtonCount] = ButtonTypeVideo;
                mButtonCount++;
            }
            if (extrasFile != null && extrasFile.exists()) {
                Savelog.d(TAG, debug, "Extras exists in " + extrasFile.getAbsolutePath());
                mButtonType[mButtonCount] = ButtonTypeExtras;
                mButtonCount++;
            }
            if (quizFile != null && quizFile.exists()) {
                Savelog.d(TAG, debug, "Quiz exists in " + quizFile.getAbsolutePath());
                mButtonType[mButtonCount] = ButtonTypeQuiz;
                mButtonCount++;
            }
            // All unused buttons are empty
            for (int index = mButtonCount; index < MaxButtons; index++)
                mButtonType[index] = ButtonTypeEmpty;
        }

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_page, container, false);

        mPageLayout = (OverScrollableLayout) v.findViewById(R.id.fragmentPage_overscrollable);

        Context appContext = getActivity().getApplicationContext();

        if (mNumberOfPages == 1)
            mPageLayout.setEdgeEffects(appContext, true, true);  // only one page available. Turn both edges on
        else if (mPageNumber == 1)
            mPageLayout.setEdgeEffects(appContext, true, false); // at first page. Turn left edge on.
        else if (mPageNumber == mNumberOfPages)
            mPageLayout.setEdgeEffects(appContext, false, true); // at last page. Turn right edge on.
        else
            mPageLayout.setEdgeEffects(appContext, false, false); // in the middle. Turn nothing on.

        mTextView = (TextView) v.findViewById(R.id.fragmentPage_transcript_content);
        mTextView.setText(mText);

        mButton[0] = (ImageButton) v.findViewById(R.id.fragmentPage_button1);
        mButton[1] = (ImageButton) v.findViewById(R.id.fragmentPage_button2);
        mButton[2] = (ImageButton) v.findViewById(R.id.fragmentPage_button3);

        for (int index = 0; index < MaxButtons; index++) {
            if (mButtonType[index] == ButtonTypeExtras) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), IconExtras);
                mButton[index].setImageBitmap(bitmap);
                mButton[index].setVisibility(View.VISIBLE);
                mOnClickListener[index] = new OnExtrasButtonClickedListener(this);
                mButton[index].setOnClickListener(mOnClickListener[index]);
            } else if (mButtonType[index] == ButtonTypeQuiz) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), IconQuiz);
                mButton[index].setImageBitmap(bitmap);
                mButton[index].setVisibility(View.VISIBLE);
                mOnClickListener[index] = new OnQuizButtonClickedListener(this);
                mButton[index].setOnClickListener(mOnClickListener[index]);
            } else if (mButtonType[index] == ButtonTypeVideo) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), IconVideo);
                mButton[index].setImageBitmap(bitmap);
                mButton[index].setVisibility(View.VISIBLE);
                mOnClickListener[index] = new OnVideoButtonClickedListener(this);
                mButton[index].setOnClickListener(mOnClickListener[index]);
            } else {
                mButton[index].setImageBitmap(null);
                mButton[index].setVisibility(View.GONE);
            }
        }

        mSlideView = (ImageZoomView) v.findViewById(R.id.fragmentPage_slide);
        displayBitmap();

        return v;
    }

    private void displayBitmap() {
        if (mSlideBitmap != null && mSlideView != null) {
            Savelog.d(TAG, debug, "displaying bitmap");
            mSlideView.setImage(mSlideBitmap);

            Savelog.d(TAG, debug, "bitmap size" + mSlideView.getHeight() + "x" + mSlideView.getWidth());

            mZoomControl = new ZoomControl();
            mZoomListener = new LongPressZoomListener(getActivity().getApplicationContext());
            mZoomListener.setZoomControl(mZoomControl);

            mSlideView.setZoomState(mZoomControl.getZoomState());
            mSlideView.setOnTouchListener(mZoomListener);

            mZoomControl.setAspectQuotient(mSlideView.getAspectQuotient());
            mZoomControl.resetZoomState();
        }
    }


    public void windupLayout() {
        if (mPageLayout != null) {
            mPageLayout.windup();
        }
    }

    public void showLayoutEdgeEffect() {
        Savelog.d(TAG, debug, "Invalidate page layout now.");
        mPageLayout.invalidate();
    }


    private static class OnExtrasButtonClickedListener implements View.OnClickListener {
        PageFragment hostFragment;

        public OnExtrasButtonClickedListener(PageFragment hostFragment) {
            super();
            this.hostFragment = hostFragment;
        }

        @Override
        public void onClick(View view) {
            FragmentManager fm = hostFragment.getActivity().getFragmentManager();
            ExtrasDialogFragment dialog = ExtrasDialogFragment.newInstance(hostFragment.mModuleNumber, hostFragment.mPageNumber);
            dialog.show(fm, ExtrasDialogFragment.dialogTag);
        }
    }

    private static class OnQuizButtonClickedListener implements View.OnClickListener {
        PageFragment hostFragment;

        public OnQuizButtonClickedListener(PageFragment hostFragment) {
            super();
            this.hostFragment = hostFragment;
        }

        @Override
        public void onClick(View view) {
            FragmentManager fm = hostFragment.getActivity().getFragmentManager();
            QuizDialogFragment dialog = QuizDialogFragment.newInstance(hostFragment.mModuleNumber, hostFragment.mPageNumber);
            dialog.show(fm, QuizDialogFragment.dialogTag);
        }
    }

    private static class OnVideoButtonClickedListener implements View.OnClickListener {
        PageFragment hostFragment;

        public OnVideoButtonClickedListener(PageFragment hostFragment) {
            super();
            this.hostFragment = hostFragment;
        }

        @Override
        public void onClick(View view) {
            FragmentManager fm = hostFragment.getActivity().getFragmentManager();
            VideoDialogFragment dialog = VideoDialogFragment.newInstance(hostFragment.mModuleNumber, hostFragment.mPageNumber);
            dialog.show(fm, VideoDialogFragment.dialogTag);
        }
    }

}