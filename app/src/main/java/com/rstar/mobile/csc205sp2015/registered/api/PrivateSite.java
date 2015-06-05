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

package com.rstar.mobile.csc205sp2015.registered.api;


import android.content.Context;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.io.File;

public class PrivateSite {
    private static final String TAG = PrivateSite.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final String SiteFilename = "site.dat";
    private static final String DefaultPublicLink = AppSettings.PrivateServerLink;

    // TODO: encrypt these before distribution
    private static final String SiteId = "A1B3C5-CSC205m-2015Spring";
    private static final String LoginApi = "login.php";
    private static final String HomeworkApi = "homework.php";
    private static final String ScoreApi = "score.php";
    private static final String MasterApi = "master.php";

    // Use the publicLink to obtain the file that contains the privateLink to the private site.
    // Once this object has been initialized, use the privateLink to access the private server.

    private String publicLink = DefaultPublicLink; // use dynamic assignment only for test purposes
    private String privateLink = "";  // dynamically assigned.

    private static PrivateSite sPrivateSite = null;

    private PrivateSite(Context context) {
        privateLink = "";
        load(context);
    }

    public static PrivateSite get(Context context) {
        if (sPrivateSite==null) {
            sPrivateSite = new PrivateSite(context);
        }
        return sPrivateSite;
    }


    // When this class is called the first time, the private privateLink is initialized
    // (if it has been stored in a data file earlier)
    private void load(Context context) {
        File f = getFile(context);
        try {
            String data = IO.loadFileAsString(context, f);
            privateLink = data.trim();
            Savelog.d(TAG, debug, "privateLink is " + privateLink);
        } catch (Exception e) {
            Savelog.w(TAG, "cannot load server privateLink file. User may not be registered.");
            privateLink = "";
        }
    }


    public void setup(Context context) {
        Savelog.d(TAG, debug, "init() called.");

        // If file already exist, there is no need to download.
        if (isInitialized()) return;

        File f = getFile(context);
        try {
            IO.downloadFile(publicLink, f);
            load(context);
        } catch (Exception e) {
            Savelog.w(TAG, "cannot download or load server privateLink file from " + publicLink, e);
            this.privateLink = "";
        }
    }


    private File getFile(Context context) {
        File f = IO.getInternalFile(context, SiteFilename);
        Savelog.d(TAG, debug, "site file: " + f.getAbsolutePath());
        return f;
    }


    public void clear(Context context) {
        File f = getFile(context);
        if (f!=null && f.exists()) f.delete();
        privateLink = "";
        // do not reset publicLink because it's supposed to be constant
    }

    public boolean isInitialized() {
        return (privateLink !=null && privateLink.length()>0);
    }
    public static String getSiteId() {
        return SiteId;
    }
    public String getLoginApi() {
        return isInitialized() ? (privateLink + LoginApi) : "";
    }
    public String getHomeworkApi() {
        return isInitialized() ? (privateLink + HomeworkApi) : "";
    }
    public String getScoreApi() {
        return isInitialized() ? (privateLink + ScoreApi) : "";
    }
    public String getMasterApi() {
        return isInitialized() ? (privateLink + MasterApi) : "";
    }

}
