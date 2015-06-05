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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.download.DownloadActivity;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.video.VideoActivity;

import java.io.File;

public class VideoDialogFragment extends DialogFragment {
	private static final String TAG = VideoDialogFragment.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;

	public static final String dialogTag = VideoDialogFragment.class.getSimpleName()+"_tag";
    private static final String EXTRA_ModuleNumber = VideoDialogFragment.class.getSimpleName()+".ModuleNumber";
    private static final String EXTRA_PageNumber = VideoDialogFragment.class.getSimpleName()+".PageNumber";

    public static final int DownloadVideoRequestCode = VideoDialogFragment.class.hashCode();  // must be unique

    private static final int Type_watch = 1;
    private static final int Type_reload = 2;

    private int mModuleNumber;
    private int mPageNumber;
    File videoFile = null;
    private String mDescription = "";  // use this to describe the video's content and size

    private TextView mDescriptionView;
    private Button mWatchButton;
    private Button mReloadButton;

	private Button mOkButton = null;

	
	public static VideoDialogFragment newInstance(int moduleNumber, int pageNumber) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ModuleNumber, moduleNumber);
        args.putInt(EXTRA_PageNumber, pageNumber);

		VideoDialogFragment fragment = new VideoDialogFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Savelog.d(TAG, debug, "onCreate() entered");

        mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        mPageNumber = getArguments().getInt(EXTRA_PageNumber, Module.DefaultPageNumber);

        videoFile = Course.get(getActivity()).getModule(mModuleNumber).getVideoFile(getActivity(), mPageNumber);

		setRetainInstance(true);
		Savelog.d(TAG, debug, "This dialog fragment is retained.");
	}

	
	/* This dialog has a title, a TextView and one button (OK).
	 */
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_video, null);

        if (videoFile!=null) {
            Module module = Course.get(getActivity()).getModule(mModuleNumber);
            mDescription = "Module "+mModuleNumber+ module.getVideoDescription(mPageNumber) + "\n\nsize: " + module.getVideoSize(mPageNumber) + "\n\n";
            if (videoFile.exists())
                mDescription += videoFile.getAbsolutePath() + " is available.";
            else
                mDescription += videoFile.getName() + " needs to be downloaded";
        }
        else {
            mDescription += "Cannot find video file";
        }

        mDescriptionView = (TextView) v.findViewById(R.id.dialogVideo_description);
        mDescriptionView.setText(mDescription);
        mWatchButton = (Button) v.findViewById(R.id.dialogVideo_watchButton);
        mReloadButton = (Button) v.findViewById(R.id.dialogVideo_reloadButton);

        mWatchButton.setOnClickListener(null);
        mWatchButton.setOnClickListener(new OnButtonClickedListener(this, Type_watch));

        mReloadButton.setOnClickListener(null);
        mReloadButton.setOnClickListener(new OnButtonClickedListener(this, Type_reload));

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

        if (mWatchButton !=null)
            mWatchButton.setOnClickListener(null);

        if (mOkButton!=null) {
			mOkButton.setOnClickListener(null);
			mOkButton = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

    private static class OnButtonClickedListener implements View.OnClickListener {
        VideoDialogFragment hostFragment;
        int type;
        public OnButtonClickedListener(VideoDialogFragment hostFragment, int type) {
            super();
            this.hostFragment = hostFragment;
            this.type = type;
        }
        @Override
        public void onClick(View view) {
            Savelog.d(TAG, debug, "Now download / show video.");
            ModuleActivity activity = ((ModuleActivity)hostFragment.getActivity());
            // Close the drawer and start the video on a different activity
            activity.closeDrawers();

            if (type==Type_watch) {
                Intent intent;
                if (hostFragment.videoFile.exists()) {
                    intent = new Intent(activity, VideoActivity.class);
                    intent.putExtra(VideoActivity.EXTRA_ModuleNumber, hostFragment.mModuleNumber);
                    intent.putExtra(VideoActivity.EXTRA_PageNumber, hostFragment.mPageNumber);
                    // Make sure that to start the new activity from hostActivity, not hostFragment
                    hostFragment.getActivity().startActivity(intent);
                }
                else {
                    // NOTE: This requires the host activity to handle the result returned from download.
                    intent = new Intent(activity, DownloadActivity.class);
                    intent.putExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_video);
                    intent.putExtra(DownloadActivity.EXTRA_ModuleNumber, hostFragment.mModuleNumber);
                    intent.putExtra(DownloadActivity.EXTRA_PageNumber, hostFragment.mPageNumber);
                    intent.putExtra(DownloadActivity.EXTRA_Trim, AppSettings.Trim);
                    // Make sure that to start the new activity from hostActivity, not hostFragment.
                    // Otherwise, the hostActivity will never get back any result!!!
                    hostFragment.getActivity().startActivityForResult(intent, DownloadVideoRequestCode);
                }
            }
            else if (type==Type_reload) {
                Intent intent;
                // NOTE: This requires the host activity to handle the result returned from download.
                intent = new Intent(activity, DownloadActivity.class);
                intent.putExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_video);
                intent.putExtra(DownloadActivity.EXTRA_ModuleNumber, hostFragment.mModuleNumber);
                intent.putExtra(DownloadActivity.EXTRA_PageNumber, hostFragment.mPageNumber);
                intent.putExtra(DownloadActivity.EXTRA_Trim, AppSettings.Trim);
                // Make sure that to start the new activity from hostActivity, not hostFragment.
                // Otherwise, the hostActivity will never get back any result!!!
                hostFragment.getActivity().startActivityForResult(intent, DownloadVideoRequestCode);

            }

            // Need to close this dialog
            hostFragment.dismiss();
        }
    }
}
