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


import android.content.Context;

import com.rstar.mobile.csc205sp2015.BuildConfig;
import com.rstar.mobile.csc205sp2015.R;

public class AppSettings {
    private static final String TAG = AppSettings.class.getSimpleName()+"_class";

    public static final boolean defaultDebug = false;  // Generate debug messages if set
    public static final boolean developerMode = false;  // Turn on all developer's control functions if set
    public static final boolean testerEnabled = true; // Save the log file in external folder if set, allow tester to report bugs to developer

    public static final boolean Trim = true; // Set to true if any existing data is to be erased before download.

    public static final String defaultFolder = "/CSC205";

    // Default slide image size. Use fixed size for efficiency
    public static final int defaultSlideWidth = 720;
    public static final int defaultSlideHeight = 540;

    // Parameters specific to the current semester. Needed for private data access
    public static final String API_courseId = "CSC205M";
    public static final String API_semester = "Spring2015";


    // Location and course info obtainable from public site.
    public static final String PublicSite = "https://sites.google.com/site/mobilecourseorganizer/";
    public static final String PublicInfoLink = PublicSite + "sample-data/CSC205info-demo.txt?attredirects=0&d=1";
    public static final String PrivateServerLink = PublicSite + "sample-data/private/CSC205server-demo.txt?attredirects=0&d=1";

    public static String getTitle(Context context) {
        return context.getResources().getString(R.string.app_name) + "(" + BuildConfig.VERSION_NAME + ")";
    }

}
