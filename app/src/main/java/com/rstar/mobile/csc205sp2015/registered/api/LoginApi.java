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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class LoginApi extends Api {
    private static final String TAG = LoginApi.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    // The following fields are used for login
    private static final String fieldTag = "tag";
    private static final String fieldUserId = "userId";
    private static final String fieldPassword = "password";
    private static final String fieldEmail = "email";
    private static final String fieldNewPassword = "newPassword";
    private static final String fieldCourseId = "courseId";
    private static final String fieldSemester = "semester";
    private static final String fieldSiteId = "siteId"; // private code to make sure this app matches with the site
    private static final int numberOfFields = 8;
    private static final String defaultLoginFilename = "login.dat";  // save all data to this file
    private static final String defaultAccessCodeFilename = "access.dat"; // might be used for a different purpose later
    private static final String tag_signup = "signup";
    private static final String tag_signin = "signin";
    private static final String tag_reset = "reset";
    private static final String tag_passwd = "passwd";
    private static final String tag_default = "";

    public static final String DefaultAccessCode = "";
    public static final int request_signup = 1001;
    public static final int request_signin = 1002;
    public static final int request_reset = 1003;
    public static final int request_passwd = 1004;
    public static final int request_default = 0;

    private String userId = "";
    private String password = "";
    private String email = "";
    private String newPassword = "";
    private String postTag = tag_default;
    private String data = "";



    // This requires network access. Therefore, it must be run within an asyncTask
    public LoginApi(Context context, String userId, String password, String newPassword, String email, int request) throws Exception {
        super(context, defaultLoginFilename);
        Savelog.d(TAG, debug, "constructor called for LoginApi");

        // do the final bit of checking to prevent sending bad data to server
        if (context==null) throw new Exception("invalid parameters");
        if (request!=request_signup && request!=request_signin && request!=request_reset && request!=request_passwd) throw new Exception("invalid parameters");

        if (request==request_signup) {
            if (!Validity.isValid(userId)) throw new Exception("invalid parameters");
            if (!Validity.isValid(password)) throw new Exception("invalid parameters");
            if (!Validity.isEmail(email)) throw new Exception("invalid parameters");
        }
        else if (request==request_signin) {
            if (!Validity.isValid(userId)) throw new Exception("invalid parameters");
            if (!Validity.isValid(password)) throw new Exception("invalid parameters");
        }
        else if (request==request_reset) {
            if (!Validity.isValid(userId)) throw new Exception("invalid parameters");
        }
        else if (request==request_passwd) {
            if (!Validity.isValid(userId)) throw new Exception("invalid parameters");
            if (!Validity.isValid(password)) throw new Exception("invalid parameters");
            if (!Validity.isValid(newPassword)) throw new Exception("invalid parameters");
            if (password.equals(newPassword)) throw new Exception("identical passwords");
        }

        getFields(userId, password, newPassword, email, request);
        post(context);
        data = IO.loadFileAsString(context, getSavedFile(context));
        Savelog.d(TAG, debug, "Data:" + data);
    }



    private void getFields(String userId, String password, String newPassword, String email, int request) {
        if (userId!=null) this.userId = userId;
        if (password!=null) this.password = password;
        if (email!=null) this.email = email;
        if (newPassword!=null) this.newPassword = newPassword;
        if (request==request_signup)
            postTag = tag_signup;
        else if (request==request_signin)
            postTag = tag_signin;
        else if (request==request_reset)
            postTag = tag_reset;
        else if (request==request_passwd)
            postTag = tag_passwd;
    }

    @Override
    protected List<NameValuePair> setPostData() {
        Savelog.d(TAG, debug, "Creating nameValuePairs of max size " + numberOfFields);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(fieldTag, postTag));
        nameValuePairs.add(new BasicNameValuePair(fieldCourseId, courseId));
        nameValuePairs.add(new BasicNameValuePair(fieldSemester, semester));
        nameValuePairs.add(new BasicNameValuePair(fieldSiteId, siteId));

        if (postTag.equals(tag_signup)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
            nameValuePairs.add(new BasicNameValuePair(fieldPassword, password));
            nameValuePairs.add(new BasicNameValuePair(fieldEmail, email));
        }
        else if (postTag.equals(tag_signin)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
            nameValuePairs.add(new BasicNameValuePair(fieldPassword, password));
        }
        else if (postTag.equals(tag_reset)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
        }
        else if (postTag.equals(tag_passwd)) {
            nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
            nameValuePairs.add(new BasicNameValuePair(fieldPassword, password));
            nameValuePairs.add(new BasicNameValuePair(fieldNewPassword, newPassword));
        }
        return nameValuePairs;
    }



    @Override
    public String getCommunication() {
        return super.getCommunication() + "\nData:\n" + data;
    }


    public String getAccessCode() {
        if (isOK() && (postTag.equals(tag_signin) || postTag.equals(tag_signup)) && data!=null) {
            return data.trim();
        }
        return DefaultAccessCode;
    }

    @Override
    protected String getSite(Context context) {
        return PrivateSite.get(context).getLoginApi();
    }

}
