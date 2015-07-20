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

import android.content.Context;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.registered.api.Api;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MasterApi extends Api {
    private static final String TAG = MasterApi.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    // The following fields are used for master
    private static final String fieldTag = "tag";
    private static final String fieldUserId = "userId";
    private static final String fieldPassword = "password";
    private static final String fieldEmail = "email";
    private static final String fieldNewPassword = "newPassword";
    private static final String fieldModule = "module";
    private static final String fieldCourseId = "courseId";
    private static final String fieldSemester = "semester";
    private static final String fieldSiteId = "siteId"; // private code to make sure this app matches with the site
    private static final int numberOfFields = 8;
    private static final String defaultFilename = "master.dat";  //save data to this file
    private static final String tag_mastersignup = "mastersignup";
    private static final String tag_mastersignin = "mastersignin";
    private static final String tag_masterunset = "masterunset";  // intentionally use unset to distinguish from user's reset
    private static final String tag_masterpasswd = "masterpasswd";
    private static final String tag_masterscore = "masterscore";
    private static final String tag_masterfeedback = "masterfeedback";
    private static final String tag_mastersubmitted = "mastersubmitted";
    private static final String tag_default = "";
    private static final int DefaultModuleNumber = Module.DefaultModuleNumber;

    public static final int request_mastersignup = 1;
    public static final int request_mastersignin = 2;
    public static final int request_masterunset = 3;
    public static final int request_masterpasswd = 4;
    public static final int request_masterscore = 5;
    public static final int request_masterfeedback = 6;
    public static final int request_mastersubmitted = 7;

    public static final String DefaultAccessCode = "";
    public static final double DefaultScore = 0;

    private String userId = "";
    private String password = "";
    private String newPassword = "";
    private String email = "";
    private String postTag = tag_default;
    private String data = "";
    private int moduleNumber = DefaultModuleNumber;


    // This requires network access. Therefore, it must be run within an asyncTask
    public MasterApi(Context context, String userId, String password, String newPassword, String email, int request) throws Exception {
        super(context, defaultFilename);
        Savelog.d(TAG, debug, "constructor called for master");

        // do the final bit of checking to prevent sending bad data to server
        if (context==null) throw new Exception("invalid parameters");
        if (userId==null || userId.length()==0) throw new Exception("invalid parameters");
        if (request!=request_mastersignup && request!=request_mastersignin && request!=request_masterunset && request!=request_masterpasswd) throw new Exception("invalid parameters");

        if (request==request_mastersignup) {
            if (password==null || password.length()==0) throw new Exception("invalid parameters");
            if (email==null || email.length()==0) throw new Exception("invalid parameters");
        }
        else if (request==request_masterpasswd) {
            if (newPassword==null || newPassword.length()==0) throw new Exception("invalid parameters");
        }

        getFields(userId, password, newPassword, email, DefaultModuleNumber, request);

        // Make sure the master has the private link
        setupMasterPrivateLink(context);

        post(context);
        data = IO.loadFileAsString(context, getSavedFile(context));
        Savelog.d(TAG, debug, "Data:" + data);
    }


    // This requires network access. Therefore, it must be run within an asyncTask
    public MasterApi(Context context, String userId, int moduleNumber, int request) throws Exception {
        super(context, defaultFilename);
        Savelog.d(TAG, debug, "constructor called for master");
        // do the final bit of checking to prevent sending bad data to server
        if (context==null) throw new Exception("invalid parameters");
        if (userId==null || userId.length()==0) throw new Exception("invalid parameters");
        if (request!=request_masterscore && request!=request_masterfeedback && request!=request_mastersubmitted) throw new Exception("invalid parameters");
        if (moduleNumber<1) throw new Exception("invalid parameters");

        getFields(userId, null, null, null, moduleNumber, request);

        // Make sure the master has the private link
        setupMasterPrivateLink(context);

        post(context);
        data = IO.loadFileAsString(context, getSavedFile(context));
        Savelog.d(TAG, debug, "Data:" + data);
    }

    // Must be called in asyncTask
    private void setupMasterPrivateLink(Context context) {
        PrivateSite privateSite = PrivateSite.get(context);
        if (!privateSite.isInitialized()) {
            privateSite.setup(context);
        }
    }

    private void getFields(String userId, String password, String newPassword, String email, int moduleNumber, int request) {
        if (userId!=null) this.userId = userId;
        if (password!=null) this.password = password;
        if (email!=null) this.email = email;
        if (newPassword!=null) this.newPassword = newPassword;
        if (moduleNumber>=1) this.moduleNumber = moduleNumber;
        if (request==request_mastersignup)
            postTag = tag_mastersignup;
        else if (request==request_mastersignin)
            postTag = tag_mastersignin;
        else if (request==request_masterunset)
            postTag = tag_masterunset;
        else if (request==request_masterpasswd)
            postTag = tag_masterpasswd;
        else if (request==request_masterscore)
            postTag = tag_masterscore;
        else if (request==request_masterfeedback)
            postTag = tag_masterfeedback;
        else if (request==request_mastersubmitted)
            postTag = tag_mastersubmitted;
    }

    @Override
    protected List<NameValuePair> setPostData() {
        Savelog.d(TAG, debug, "Creating nameValuePairs of max size " + numberOfFields);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(fieldTag, postTag));
        nameValuePairs.add(new BasicNameValuePair(fieldCourseId, courseId));
        nameValuePairs.add(new BasicNameValuePair(fieldSemester, semester));
        nameValuePairs.add(new BasicNameValuePair(fieldSiteId, siteId));
        if (postTag.equals(tag_mastersignup)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
            nameValuePairs.add(new BasicNameValuePair(fieldPassword, password));
            nameValuePairs.add(new BasicNameValuePair(fieldEmail, email));
        }
        else if (postTag.equals(tag_mastersignin)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
        }
        else if (postTag.equals(tag_masterunset)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
        }
        else if (postTag.equals(tag_masterpasswd)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
            nameValuePairs.add(new BasicNameValuePair(fieldNewPassword, newPassword));
        }
        else if (postTag.equals(tag_masterscore)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
            nameValuePairs.add(new BasicNameValuePair(fieldModule, Integer.toString(moduleNumber)));
        }
        else if (postTag.equals(tag_masterfeedback)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
            nameValuePairs.add(new BasicNameValuePair(fieldModule, Integer.toString(moduleNumber)));
        }
        else if (postTag.equals(tag_mastersubmitted)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
            nameValuePairs.add(new BasicNameValuePair(fieldModule, Integer.toString(moduleNumber)));
        }
        return nameValuePairs;
    }
    

    @Override
    public String getCommunication() {
        return super.getCommunication() + "\nData:\n" + data;
    }


    public String getAccessCode() {
        if (isOK() && (postTag.equals(tag_mastersignin) || postTag.equals(tag_mastersignup)) && data!=null) {
            return data.trim();
        }
        return DefaultAccessCode;
    }

    public double getScore() {
        if (isOK() && postTag.equals(tag_masterscore)) {
            try {
                return Double.parseDouble(data.trim());
            }
            catch (Exception e) {}
        }
        return DefaultScore;
    }


    public File getFile(Context context) {
        File file = null;
        if (isOK() && (postTag.equals(tag_masterfeedback) || postTag.equals(tag_mastersubmitted))) {
            file = getSavedFile(context);
        }
        return file;
    }


    public String getData() {
        return data;
    }
    
    @Override
    protected String getSite(Context context) {
        return PrivateSite.get(context).getMasterApi();
    }

}
