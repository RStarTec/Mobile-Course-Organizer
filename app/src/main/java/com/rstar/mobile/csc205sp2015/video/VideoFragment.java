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

package com.rstar.mobile.csc205sp2015.video;


import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;

import java.io.File;


/* In order for the video to play smoothly in the event 
 * of orientation change, it is necessary to keep the 
 * video player intact. 
 * The most effective and efficient way to do this is to 
 * have the video player in a retained fragment. 
 */



public class VideoFragment extends Fragment {
	private static final String TAG = VideoFragment.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;

    public static final String EXTRA_ModuleNumber = VideoFragment.class.getSimpleName()+".ModuleNumber";
    public static final String EXTRA_PageNumber = VideoFragment.class.getSimpleName()+".PageNumber";


	private static final int ButtonStateIdle = 0;
	private static final int ButtonStatePlaying = 2;

    private int mModuleNumber;
    private int mPageNumber;
    private File mDataFile;
    private MediaController mMediaControls;
    private VideoView mVideoView;
    private int position = 0;


	public static VideoFragment newInstance(int moduleNumber, int pageNumber) {
		Bundle args = new Bundle();
        args.putInt(EXTRA_ModuleNumber, moduleNumber);
        args.putInt(EXTRA_PageNumber, pageNumber);

   		VideoFragment fragment = new VideoFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Savelog.d(TAG, debug, "onCreate()");

        mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        mPageNumber = getArguments().getInt(EXTRA_PageNumber, Module.DefaultPageNumber);

        Course course = Course.get(getActivity());

		mDataFile = course.getModule(mModuleNumber).getVideoFile(getActivity(), mPageNumber);

        Savelog.d(TAG, debug, "Going to open video file: " + mDataFile + " size=" + mDataFile.length());
        if (mMediaControls == null) {
            mMediaControls = new MediaController(getActivity().getApplicationContext());
        }

		setRetainInstance(true);

	} // end to implementing onCreate()



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Savelog.d(TAG, debug, "onCreateView()");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        mVideoView = (VideoView) v.findViewById(R.id.fragmentVideo_videoView);

        try {
            //set the media controller in the VideoView
            mVideoView.setMediaController(mMediaControls);

            //set the uri of the video to be played
            Uri uri = Uri.fromFile(mDataFile);

            // TODO: the following is just a test. Load video from resource.
            // Uri uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.slide3v);



            mVideoView.setVideoURI(uri);

        } catch (Exception e) {
            Savelog.e(TAG, "Cannot load video ", e);
        }


        mVideoView.requestFocus();
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                //if we have a position on savedInstanceState, the video playback should start from here
                mVideoView.seekTo(position);
                if (position == 0) {
                    mVideoView.start();
                } else {
                    //if we come from a resumed activity, video playback will be paused
                    Savelog.d(TAG, debug, "called from resumed activity");
                }
            }
        });

        Savelog.d(TAG, debug, "finished createView()");
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        position = mVideoView.getCurrentPosition();
        mVideoView.pause();
    }

    @Override
    public void onResume() {
        mVideoView.seekTo(position);
        mVideoView.start();
        super.onResume();
    }

}
