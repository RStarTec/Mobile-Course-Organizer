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

package com.rstar.mobile.csc205sp2015.app;


import android.app.Fragment;
import android.os.Bundle;

import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;

/*  This fragment is a transient fragment that performs download. It does not have its own layout.
 */

public class InitFragment extends Fragment {
    private static final String TAG = InitFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static InitFragment newInstance() {
        Bundle args = new Bundle();
        InitFragment fragment = new InitFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Put the course on a fragment so that checking status is done only once,
        // independent of configuration change.
        Savelog.d(TAG, debug, "InitFragment created(). App opening.");

        if (PrivateSite.get(getActivity()).isInitialized()) {
            Savelog.d(TAG, debug, "Private site initialization attempted.");
        }
        Course course = Course.get(getActivity());
        if (course.isInstalled()) {
            // No need to download.
            ((InitActivity) getActivity()).splash();
        }
        else {
            // If course is not already installed, then download course (which also includes downloading private site)
            // trigger a download and wait for result
            ((InitActivity) getActivity()).downloadCourse();
            Savelog.d(TAG, debug, "course needs download.");
        }


        // Make sure to retain the fragment so that installation is
        // not restarted at every rotation
        setRetainInstance(true);
        
    } // end to implementing onCreate()
    

}
