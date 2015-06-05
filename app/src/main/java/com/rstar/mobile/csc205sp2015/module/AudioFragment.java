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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.io.File;


/* In order for the audio to play smoothly in the event 
 * of orientation change, it is necessary to keep the 
 * audio player intact. 
 * The most effective and efficient way to do this is to 
 * have the audio player in a retained fragment. 
 * This fragment does not need to have a view. 
 * Therefore, there is no need to inflate any views.
 * However, it also means that this fragment cannot
 * be added to the fragment manager through an id.
 * Instead, it should be added using a tag.
 */


/* Three button states:
 * i. Idle
 * ii. Playing
 * iii. Loading
 * 
 * Five main events:
 * 1. Fragment created
 * 2. Fragment destroyed
 * 3. Button pressed
 * 4. Async loader task called back
 * 5. Media player called back
 * 
 * Five actions:
 * a. Play()
 * b. Load()
 * c. Pause()
 * d. Stop()
 * e. Abort() 
 */
public class AudioFragment extends Fragment {
	private static final String TAG = AudioFragment.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;

    public static final String EXTRA_ModuleNumber = AudioFragment.class.getSimpleName()+".ModuleNumber";
    public static final String EXTRA_PageNumber = AudioFragment.class.getSimpleName()+".PageNumber";


	private static final int ButtonStateIdle = 0;
	private static final int ButtonStatePlaying = 2;

    private int mModuleNumber;
    private int mPageNumber;
    private File mDataFile;
	private MediaPlayer mAudioPlayer;
	private int mButtonState = ButtonStateIdle;
	private MenuItem mAudioButton;
	

	// A handler object, used for deferring UI operations.
	private Handler mHandler = new Handler();

	
	// Supply the page number as an argument to the newly created fragment.
	public static AudioFragment newInstance(int moduleNumber, int pageNumber) {
		Bundle args = new Bundle();
        args.putInt(EXTRA_ModuleNumber, moduleNumber);
        args.putInt(EXTRA_PageNumber, pageNumber);

   		AudioFragment fragment = new AudioFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	
	// Event 1: Fragment created 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Savelog.d(TAG, debug, "onCreate()");

        mModuleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        mPageNumber = getArguments().getInt(EXTRA_PageNumber, Module.DefaultPageNumber);

        Course course = Course.get(getActivity());
		mDataFile = course.getModule(mModuleNumber).getAudioFile(getActivity(), mPageNumber);
        Savelog.d(TAG, debug, "Going to obtain audio file: " + mDataFile);

		setRetainInstance(true);
		
		setHasOptionsMenu(true); // will create option menu
		
	} // end to implementing onCreate()
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.audio, menu);
		mAudioButton = menu.findItem(R.id.menu_audio_onoff);
		
		if (mButtonState==ButtonStatePlaying) {
			mAudioButton.setTitle(getString(R.string.menu_audio_pause));
			mAudioButton.setIcon(R.drawable.ic_action_pause);
		}
		else {  // assume idle by default
			mAudioButton.setTitle(getString(R.string.menu_audio_play));
			mAudioButton.setIcon(R.drawable.ic_action_play);
		}
	}

	
	// Event 3. Button pressed
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==R.id.menu_audio_onoff) {
			if (mButtonState==ButtonStatePlaying) { 
				pause();
				showButtonStateIdle();
			}
			else { // assume idle by default
				if (mDataFile!=null && mDataFile.exists()) {
					boolean success = play();
					if (success) {
						showButtonStatePlaying();
					}
					else {
						// button state remains idle. no need to refresh menu
					}
				}
				else {
                    Savelog.w(TAG, "No data file!");
                    showButtonStateIdle();
				}
			}
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	private void showButtonStateIdle() {
		disableActionView();
		mButtonState = ButtonStateIdle;
		refreshMenu();
	}
	
	private void showButtonStatePlaying() {
		disableActionView();
		mButtonState = ButtonStatePlaying;
		refreshMenu();
	}


	private void refreshMenu() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				getActivity().invalidateOptionsMenu();
			}
		});
	}
	
	private void disableActionView() {
		if (mAudioButton.getActionView()!=null) {
			mAudioButton.getActionView().clearAnimation();
			mAudioButton.setActionView(null);
		}
	}

	
	// Action a. Play
	private boolean play() {
		boolean success = false;
		if (mAudioPlayer==null) {  // start anew
			
			// Assume media file is already loaded and everything is ready.
			// If there is any problem with the media file, an exception will be caught
			
			mAudioPlayer = new MediaPlayer();
			try {
				File mediaFile = mDataFile;

				Savelog.d(TAG, debug, "media file " + mediaFile.getPath() + " size=" + mediaFile.length());
				
				mAudioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				
				// Note that mediaplay requires the file to have world-readable permission. So the 
				// media file cannot be stored in internal storage
				mAudioPlayer.setDataSource(mediaFile.getAbsolutePath());
				mAudioPlayer.prepare();
				
				// Once finished, call stop to release the player.
				mAudioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					// Event  5. Media player called back
					@Override
					public void onCompletion(MediaPlayer mp) {
						stop();
						showButtonStateIdle();
					}
				});
				
				
			} catch (Exception e) {
				Savelog.e(TAG, "media file not available or problem with media player.");
				mAudioPlayer.release();
				mAudioPlayer = null;
				// TODO: do we need to clear existing file?
			}
		}
		
		if (mAudioPlayer!=null) { 
			// Resume playing
			mAudioPlayer.start();
			success = true;
		}
		
		return success;
	}
	
	

	// Action c. Pause
	private void pause() {
		if (mAudioPlayer!=null) {
			if (mAudioPlayer.isPlaying()) {
				mAudioPlayer.pause();
			}
		}
	}
	
	// Action d. Stop
	private void stop() {
		if (mAudioPlayer != null) {
			mAudioPlayer.release();
			mAudioPlayer.setOnCompletionListener(null);
			mAudioPlayer = null;
		}
	}
	

	
	// Event 2. Fragment destroyed
	@Override
	public void onDestroy() {
		super.onDestroy();
		disableActionView();
		stop();
	}

}
