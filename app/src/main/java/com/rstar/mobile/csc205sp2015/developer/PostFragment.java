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

package com.rstar.mobile.csc205sp2015.developer;


import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Message;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.api.Api;
import com.rstar.mobile.csc205sp2015.registered.api.HomeworkApi;
import com.rstar.mobile.csc205sp2015.registered.api.LoginApi;
import com.rstar.mobile.csc205sp2015.registered.api.ScoreApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

public class PostFragment extends Fragment  {
    private static final String TAG = PostFragment.class.getSimpleName()+"_class";
    private static boolean debug = AppSettings.defaultDebug;

    private static final String type_signup = "signup";
    private static final String type_signin = "signin";
    private static final String type_reset = "reset";
    private static final String type_passwd = "passwd";
    private static final String type_download = "download";
    private static final String type_upload = "upload";
    private static final String type_score = "score";
    private static final String type_feedback = "feedback";
    private static final String type_submitted = "submitted";

    
    private PostAsyncTask mPostAsyncTask;
    private TextView statusView;
    private String data = "";

    // Supply the hymn number as an argument to the newly created fragment.
    public static PostFragment newInstance() {
        Bundle args = new Bundle();
        PostFragment fragment = new PostFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        

        setRetainInstance(true);
        setHasOptionsMenu(true);

    } // end to implementing onCreate()
    
    
    

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v;
        
        v = inflater.inflate(R.layout.fragment_post, parent, false);
        statusView = (TextView) v.findViewById(R.id.fragmentPost_status);
        return v;
    } // end to implementing onCreateView() 


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPostAsyncTask!=null) {
            mPostAsyncTask.cancel(true);
            mPostAsyncTask = null;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.post, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_post_signup:
            {
                try {
                    mPostAsyncTask = new PostAsyncTask(this, type_signup);
                    mPostAsyncTask.execute();
                } catch (Exception e) {
                    Savelog.e(TAG, "Cannot create post\n", e);
                }
                return true;
            }
            case R.id.menu_post_signin:
            {
                try {
                    mPostAsyncTask = new PostAsyncTask(this, type_signin);
                    mPostAsyncTask.execute();
                } catch (Exception e) {
                    Savelog.e(TAG, "Cannot create post\n", e);
                }
                return true;
            }
            case R.id.menu_post_reset:
            {
                try {
                    mPostAsyncTask = new PostAsyncTask(this, type_reset);
                    mPostAsyncTask.execute();
                } catch (Exception e) {
                    Savelog.e(TAG, "Cannot create post\n", e);
                }
                return true;
            }
            case R.id.menu_post_passwd:
            {
                try {
                    mPostAsyncTask = new PostAsyncTask(this, type_passwd);
                    mPostAsyncTask.execute();
                } catch (Exception e) {
                    Savelog.e(TAG, "Cannot create post\n", e);
                }
                return true;
            }

            case R.id.menu_post_download:
            {
                try {
                    mPostAsyncTask = new PostAsyncTask(this, type_download);
                    mPostAsyncTask.execute();
                } catch (Exception e) {
                    Savelog.e(TAG, "Cannot create post\n", e);
                }
                return true;
            }
            case R.id.menu_post_upload:
            {
                try {
                    mPostAsyncTask = new PostAsyncTask(this, type_upload);
                    mPostAsyncTask.execute();
                } catch (Exception e) {
                    Savelog.e(TAG, "Cannot create post\n", e);
                }
                return true;
            }
            case R.id.menu_post_score:
            {
                try {
                    mPostAsyncTask = new PostAsyncTask(this, type_score);
                    mPostAsyncTask.execute();
                } catch (Exception e) {
                    Savelog.e(TAG, "Cannot create post\n", e);
                }
                return true;
            }
            case R.id.menu_post_feedback:
            {
                try {
                    mPostAsyncTask = new PostAsyncTask(this, type_feedback);
                    mPostAsyncTask.execute();
                } catch (Exception e) {
                    Savelog.e(TAG, "Cannot create post\n", e);
                }
                return true;
            }
            case R.id.menu_post_submitted:
            {
                try {
                    mPostAsyncTask = new PostAsyncTask(this, type_submitted);
                    mPostAsyncTask.execute();
                } catch (Exception e) {
                    Savelog.e(TAG, "Cannot create post\n", e);
                }
                return true;
            }
            case R.id.menu_post_export:
            {
                exportFile(data);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    

    private static class PostAsyncTask extends AsyncTask<Object, Void, Boolean> {
        private Context appContext;
        private final WeakReference<PostFragment> hostFragmentReference;
        Exception err = null;
        Api api = null;
        String type;
        String postData;

        public PostAsyncTask(PostFragment hostFragment, String type)  {
            super();
            this.appContext = hostFragment.getActivity().getApplicationContext();
            hostFragmentReference = new WeakReference<PostFragment>(hostFragment);
            this.type = type;
        }

        @Override
        protected Boolean doInBackground(Object... args) {
            boolean forced = true;
            try {
                if (type.equals(type_signup)) {
                    // Put some valid testers here
                    String userId = "nova1";
                    String password = "nova1";
                    String email = DeveloperSettings.developerEmail;

                    postData = LoginApi.class.getSimpleName() + ":\n";
                    api = new LoginApi(appContext, userId, password, null, email, LoginApi.request_signup);
                }
                else if (type.equals(type_signin)) {
                    // Put some valid testers here
                    String userId = "nova1";
                    String password = "nova1";

                    postData = LoginApi.class.getSimpleName() + ":\n";
                    api = new LoginApi(appContext, userId, password, null, null, LoginApi.request_signin);
                }
                else if (type.equals(type_reset)) {
                    // Put some valid testers here
                    String userId = "nova1";

                    postData = LoginApi.class.getSimpleName() + ":\n";
                    api = new LoginApi(appContext, userId, null, null, null, LoginApi.request_reset);
                }
                else if (type.equals(type_passwd)) {
                    // Put some valid testers here
                    String userId = "nova1";
                    String password = "nova1";
                    String newPassword = "new1";

                    postData = LoginApi.class.getSimpleName() + ":\n";
                    api = new LoginApi(appContext, userId, password, newPassword, null, LoginApi.request_passwd);
                }

                else if (type.equals(type_download)) {
                    // Put some valid access code
                    String accessCode = "usr1";
                    int moduleNumber = 1;
                    String link = HomeworkApi.getDownloadLink(appContext, accessCode, moduleNumber);
                    File dummyFile = IO.getInternalFile(appContext, "dummyFile.zip");

                    if (dummyFile!=null && dummyFile.exists()) dummyFile.delete();

                    postData = "trying to download homework for module " + moduleNumber + "\nFrom: " + link + "\nTo temp file: " + dummyFile.getAbsolutePath() +"\n";
                    IO.downloadFile(link, dummyFile);

                    postData += "\nResult: ";
                    if (dummyFile.exists()) {
                        postData += "downloaded size=" + dummyFile.length();
                        dummyFile.delete();
                    }
                    else {
                        postData += "download failed.";
                    }
                    postData += "\n";
                }
                else if (type.equals(type_upload)) {
                    // Put some valid access code
                    String accessCode = "usr1";
                    int moduleNumber = 1;
                    File file = IO.getInternalFile(appContext, "dummy.txt");
                    IO.saveStringAsFile(appContext, file, "dummy data.");
                    String fileType = "TEXT/PLAIN";

                    postData = HomeworkApi.class.getSimpleName() + ":\n";
                    api = new HomeworkApi(appContext, accessCode, moduleNumber, file, fileType);

                }
                else if (type.equals(type_score)) {
                    // Put some valid access code
                    String userId = "nova1";
                    String password = "nova1";
                    String accessCode = "usr1";
                    int moduleNumber = 1;

                    postData = ScoreApi.class.getSimpleName() + ":\n";
                    api = new ScoreApi(appContext, userId, password, accessCode, moduleNumber, ScoreApi.request_score);
                }
                else if (type.equals(type_feedback)) {
                    // Put some valid access code
                    String userId = "nova1";
                    String password = "nova1";
                    String accessCode = "usr1";
                    int moduleNumber = 1;

                    postData = ScoreApi.class.getSimpleName() + ":\n";
                    api = new ScoreApi(appContext, userId, password, accessCode, moduleNumber, ScoreApi.request_feedback);
                }
                else if (type.equals(type_submitted)) {
                    // Put some valid access code
                    String userId = "nova1";
                    String password = "nova1";
                    String accessCode = "usr1";
                    int moduleNumber = 1;

                    postData = ScoreApi.class.getSimpleName() + ":\n";
                    api = new ScoreApi(appContext, userId, password, accessCode, moduleNumber, ScoreApi.request_submitted);
                }

                postData += "\n\n";
                return true;
            }
            catch (Exception e) {
                err = e;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (hostFragmentReference != null) {
                final PostFragment hostFragment = hostFragmentReference.get();
                if (hostFragment != null) {
                    if (type==type_download) {
                        // DONE!! Only GET result.
                        hostFragment.statusView.setText(postData);
                        hostFragment.data = postData;
                    }
                    else if (api !=null) {
                        String data = api.getCommunication();

                        if (data!=null) {
                            // DONE!! Check POST result
                            hostFragment.statusView.setText(postData+data);
                            hostFragment.data = postData+data;

                        }
                    }
                    else {
                        hostFragment.statusView.setText(postData+"failed");
                        hostFragment.data = postData+"failed";
                        if (err!=null) {
                            if (err instanceof IO.NetworkUnavailableException) {
                                Toast.makeText(hostFragment.getActivity(), Message.toastNoNetwork, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(hostFragment.getActivity(), Message.toastDataUnavailable, Toast.LENGTH_SHORT).show();
                            }
                            Savelog.e(TAG, "POST failed.\n", err);
                        }
                    }
                }
            }
            Savelog.d(TAG, debug, "AsyncTask completed.");
        }
    }

    public static void exportFile(String data) {
        String filename = "postExport.txt";
        File f;
        FileOutputStream fout = null;
        PrintWriter pw = null;
        try {
            f = new File(IO.getDefaultExternalPath(), filename);
            fout = new FileOutputStream(f);
            pw = new PrintWriter(fout);
            pw.print(data);
            pw.flush();
            pw.close();
            fout.close();
        }
        catch (Exception e) {
            Savelog.w(TAG, "Cannot save file " + filename + " externally");
            if (fout!=null) try { fout.close(); } catch (IOException e1) {}
            if (pw!=null) pw.close();
        }
    }
}
