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

package com.rstar.mobile.csc205sp2015.download;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Message;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.registered.homework.Homework;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;

import java.lang.ref.WeakReference;

/*  This fragment is a transient fragment that performs download. It does not have its own layout.
 *
 *
 */

public class DownloadFragment extends Fragment {
	private static final String TAG = DownloadFragment.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;

    private static final String EXTRA_Type = DownloadFragment.class.getSimpleName()+".Type";
	private static final String EXTRA_Trim = DownloadFragment.class.getSimpleName()+".Trim";
    private static final String EXTRA_ModuleNumber = DownloadFragment.class.getSimpleName()+".ModuleNumber";
    private static final String EXTRA_PageNumber = DownloadFragment.class.getSimpleName()+".PageNumber";


	private DownloaderAsyncTask mDownloaderAsyncTask = null;
    private String downloadStatus = "";
	
	public static DownloadFragment newInstance(int type, boolean trim, int moduleNumber, int pageNumber) {
		Bundle args = new Bundle();
        args.putInt(EXTRA_Type, type);
		args.putBoolean(EXTRA_Trim, trim);
        args.putInt(EXTRA_ModuleNumber, moduleNumber);
        args.putInt(EXTRA_PageNumber, pageNumber);

		DownloadFragment fragment = new DownloadFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        int type;
        boolean trim;
        int moduleNumber;
        int pageNumber;

        type = getArguments().getInt(EXTRA_Type, DownloadActivity.Type_default);
		trim = getArguments().getBoolean(EXTRA_Trim, AppSettings.Trim);
        moduleNumber = getArguments().getInt(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
        pageNumber = getArguments().getInt(EXTRA_PageNumber, Module.DefaultPageNumber);

		// Download whatever data in an asynctask as this requires internet too
		mDownloaderAsyncTask = new DownloaderAsyncTask(this, type, trim, moduleNumber, pageNumber);
		mDownloaderAsyncTask.execute();

		// Make sure to retain the fragment so that installation is
		// not restarted at every rotation
		setRetainInstance(true);
		
	} // end to implementing onCreate()
	


	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mDownloaderAsyncTask!=null) {
			mDownloaderAsyncTask.cancel(true);
			mDownloaderAsyncTask = null;
		}
	}


	
	
	
	public static class DownloaderAsyncTask extends AsyncTask<Object, String, Boolean> {
		private Context appContext;
		private final WeakReference<DownloadFragment> hostFragmentReference;
		Exception err = null;
        int type;
        boolean trim;
        int moduleNumber;
        int pageNumber;

		public DownloaderAsyncTask(DownloadFragment hostFragment, int type, boolean trim, int moduleNumber, int pageNumber) {
			super();
			this.appContext = hostFragment.getActivity().getApplicationContext();
			hostFragmentReference = new WeakReference<DownloadFragment>(hostFragment);

            hostFragment.postProgress(Message.toastDownloadInProgress);

            this.type = type;
            this.trim = trim;
            this.moduleNumber = moduleNumber;
            this.pageNumber = pageNumber;
            Savelog.d(TAG, debug, "Download type=" + type + " module=" + moduleNumber + " page=" + pageNumber + " trim=" + trim);
		}

		@Override
		protected Boolean doInBackground(Object... params) {
            boolean result = false;
            if (IO.isNetworkAvailable(appContext)) {
                try {
                    if (type==DownloadActivity.Type_course) {
                        result = downloadCourse(appContext, trim);
                    }
                    else if (type==DownloadActivity.Type_privateSite) {
                        result = downloadPrivateSite(appContext, trim);
                    }
                    else if (type==DownloadActivity.Type_module) {
                        result = downloadModule(appContext, moduleNumber, trim);
                    }
                    else if (type==DownloadActivity.Type_homework) {
                        result = downloadHomework(appContext, moduleNumber, trim);
                    }
                    else if (type==DownloadActivity.Type_video) {
                        result = downloadVideo(appContext, moduleNumber, pageNumber, trim);
                    }
                    else if (type==DownloadActivity.Type_courseReload) {
                        result = downloadCourseReload(appContext, trim);
                    }
                    return result;
                }
                catch (Exception e) {
                    err = e;
                    return false;
                }
            }
            else {
                err = new IO.NetworkUnavailableException("No network. Download canceled");
                return false;
            }

		}

		@Override
		protected void onProgressUpdate(String... progress) {
			final DownloadFragment hostFragment = hostFragmentReference.get();
			if (hostFragment != null) {
				Activity activity = hostFragment.getActivity();
				if (activity!=null && !activity.isFinishing()) {
					((DownloadActivity) activity).postProgress(progress[0]);
				}
			}
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);

			if (hostFragmentReference != null) {
				final DownloadFragment hostFragment = hostFragmentReference.get();
				if (hostFragment != null) {
					Activity activity = hostFragment.getActivity();


                    if (isCancelled()) {
                        Savelog.d(TAG, debug, "onPostExecute(): Download canceled.");
                        ((DownloadActivity)hostFragment.getActivity()).setReturnIntent(false);
                    }
                    else {
                        Savelog.d(TAG, debug, "onPostExecute(): Download result: " + success);
                        ((DownloadActivity)hostFragment.getActivity()).setReturnIntent(success);
                        if (success) {  // is not cancelled and is successful
                            hostFragment.postProgress(Message.toastDownloadCompleted);
                            // DONE!!
                        } else {  // is not cancelled and is unsuccessful
                            if (err!=null) {
                                if (err instanceof IO.NetworkUnavailableException) {
                                    Toast.makeText(hostFragment.getActivity(), Message.toastNoNetwork, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(hostFragment.getActivity(), Message.toastDataUnavailable, Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                // really nothing changed.
                                Toast.makeText(hostFragment.getActivity(), Message.toastDataUnavailable, Toast.LENGTH_SHORT).show();
                            }
                        }
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
            clearDownload(appContext, type, moduleNumber, pageNumber);
            cleanup();
			Savelog.d(TAG, debug, "AsyncTask canceled.");
		}
	}

    public String getProcess() {
        return downloadStatus;
    }
    public void postProgress(String message) {
        downloadStatus = message;
        DownloadActivity activity = (DownloadActivity) getActivity();
        if (activity!=null && !activity.isFinishing()) {
            activity.postProgress(downloadStatus);
        }
    }


    public static void clearDownload(Context context, int type, int moduleNumber, int pageNumber) {
        Savelog.d(TAG, debug, "clearDownload for type " + type);
        if (type==DownloadActivity.Type_course) {
            Course course = Course.get(context);
            course.clear(context);
        }
        else if (type==DownloadActivity.Type_privateSite) {
            PrivateSite.get(context).clear(context);
        }
        else if (type==DownloadActivity.Type_module) {
            Module module = Course.get(context).getModule(moduleNumber);
            if (module!=null) module.clear(context);
        }
        else if (type==DownloadActivity.Type_homework) {
            Homework homework = new Homework(context, moduleNumber);
            homework.clear(context);
        }
        else if (type==DownloadActivity.Type_video) {
            Module module = Course.get(context).getModule(moduleNumber);
            module.clearVideoFile(context, pageNumber);
        }
        else if (type==DownloadActivity.Type_courseReload) {
            Course course = Course.get(context);
            course.clear(context);
        }
    }



    public static boolean downloadCourse(Context context, boolean trim) {
        Course course = Course.get(context);
        if (trim) course.clear(context);
        course.setup(context);
        boolean success = course.isInstalled();
        Savelog.d(TAG, debug, "Course installation result: " + success);
        return success;
    }

    public static boolean downloadCourseReload(Context context, boolean trim) {
        Course course = Course.get(context);
        // If trimming is necessary, it must be done inside course
        // because the old data must be backed up first for comparison
        course.reload(context, trim);
        boolean success = course.isInstalled();
        Savelog.d(TAG, debug, "Course-reload installation result: " + success);
        return success;
    }

    public static boolean downloadPrivateSite(Context context, boolean trim) {
        PrivateSite privateSite = PrivateSite.get(context);
        if (trim) privateSite.clear(context);
        privateSite.setup(context);
        boolean success = privateSite.isInitialized();
        Savelog.d(TAG, debug, "Private site download result: " + success);
        return success;
    }

    public static boolean downloadModule(Context context, int moduleNumber, boolean trim) {
        Module module = Course.get(context).getModule(moduleNumber);
        if (trim) module.clear(context);
        module.setup(context);
        boolean success = module.isInstalled();
        Savelog.d(TAG, debug, "Module installation result: " + success);
        return success;
    }

    public static boolean downloadHomework(Context context, int moduleNumber, boolean trim) {
        Homework homework = new Homework(context, moduleNumber);
        if (trim) homework.clear(context);
        homework.setup(context);
        boolean success = homework.isInstalled(context);
        Savelog.d(TAG, debug, "Homework installation result: " + success);
        return success;
    }


    public static boolean downloadVideo(Context context, int moduleNumber, int pageNumber, boolean trim) {
        Module module = Course.get(context).getModule(moduleNumber);
        if (trim) module.clearVideoFile(context, pageNumber);
        module.downloadVideoFile(context, pageNumber, trim);
        boolean success = module.isVideoOnDevice(context, pageNumber);
        Savelog.d(TAG, debug, "Video download result: " + success);
        return success;
    }


}
