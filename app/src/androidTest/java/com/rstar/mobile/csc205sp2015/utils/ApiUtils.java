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

package com.rstar.mobile.csc205sp2015.utils;

import android.content.Context;

import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.developer.DeveloperSettings;
import com.rstar.mobile.csc205sp2015.developer.MasterApi;
import com.rstar.mobile.csc205sp2015.fields.CourseFields;
import com.rstar.mobile.csc205sp2015.fields.LoginApiFields;
import com.rstar.mobile.csc205sp2015.fields.ScoreApiFields;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.registered.login.Student;
import com.rstar.mobile.csc205sp2015.registered.api.LoginApi;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.registered.api.ScoreApi;

import java.io.File;

/**
 * Created by Ahui on 1/16/15.
 */
public class ApiUtils {
    // private site limits number of module to 3
    public static int TestModuleNumber = 1;

    public static int getRequestType(LoginApi loginApi) throws Exception {
        if (loginApi==null) return LoginApi.request_default; // no such request type
        LoginApiFields.Constants constants = new LoginApiFields.Constants();
        LoginApiFields.Variables variables = new LoginApiFields.Variables(loginApi);

        if (variables.postTag.equals(constants.tag_signup)) return LoginApi.request_signup;
        else if (variables.postTag.equals(constants.tag_signin)) return LoginApi.request_signin;
        else if (variables.postTag.equals(constants.tag_reset)) return LoginApi.request_reset;
        else if (variables.postTag.equals(constants.tag_passwd)) return LoginApi.request_passwd;
        else return LoginApi.request_default;
    }


    public static int getRequestType(ScoreApi scoreApi) throws Exception {
        if (scoreApi==null) return 0; // no such request type
        ScoreApiFields.Constants constants = new ScoreApiFields.Constants();
        ScoreApiFields.Variables variables = new ScoreApiFields.Variables(scoreApi);

        if (variables.postTag.equals(constants.tag_score)) return ScoreApi.request_score;
        else if (variables.postTag.equals(constants.tag_feedback)) return ScoreApi.request_feedback;
        else if (variables.postTag.equals(constants.tag_submitted)) return ScoreApi.request_submitted;
        else return ScoreApi.request_default;
    }


    public static String masterSignupUser(Context targetContext, String userId, String password) throws Exception {
        String email = DeveloperSettings.developerEmail;
        MasterApi masterApi = new MasterApi(targetContext, userId, password, null, email, MasterApi.request_mastersignup);
        if (!masterApi.isOK()) {
            throw new Exception("Cannot master signup");
        }
        else {
            return masterApi.getAccessCode();
        }
    }

    public static boolean masterClearSignup(Context targetContext, String userId) throws Exception {
        MasterApi masterApi = new MasterApi(targetContext, userId, null, null, null, MasterApi.request_masterunset);
        return masterApi.isOK();
    }


    public static double masterGetScore(Context targetContext, String userId, int moduleNumber) throws Exception {
        MasterApi masterApi = new MasterApi(targetContext, userId, moduleNumber, MasterApi.request_masterscore);
        if (masterApi!=null)
            return  masterApi.getScore();
        else
            return MasterApi.DefaultScore;
    }

    public static String masterGetSubmitted(Context targetContext, String userId, int moduleNumber) throws Exception {
        MasterApi masterApi = new MasterApi(targetContext, userId, moduleNumber, MasterApi.request_mastersubmitted);
        if (masterApi!=null && masterApi.isOK()) {
            File f = masterApi.getFile(targetContext);
            String data = IO.loadFileAsString(targetContext, f);
            return data;
        }
        return "";
    }

    public static String masterGetFeedback(Context targetContext, String userId, int moduleNumber) throws Exception {
        MasterApi masterApi = new MasterApi(targetContext, userId, moduleNumber, MasterApi.request_masterfeedback);
        if (masterApi!=null && masterApi.isOK()) {
            File f = masterApi.getFile(targetContext);
            String data = IO.loadFileAsString(targetContext, f);
            return data;
        }
        return "";
    }

    public static Course getCourse(Context targetContext) throws Exception {
        // Choose set 0, which is a good set
        CourseUtils.DataSet dataSet = new CourseUtils.DataSet(0);
        Course course = Course.get(targetContext);
        CourseFields.Variables.setLink(course, dataSet.courseLink);
        course.setup(targetContext);
        return course;
    }

    private void setupPrivateLink(Context context) {
        PrivateSite privateSite = PrivateSite.get(context);
        if (!privateSite.isInitialized()) {
            privateSite.setup(context);
        }
    }


    public static class FileType {
        public static final String JPG = "jpg";
        public static final String PNG = "png";
        public static final String TIF = "tif";
        public static final String TXT = "txt";
        public static final String PDF = "pdf";

        public static final String SampleFilename = "sample";
        public String type = "unknown";
        public String label = "unknown";
        public int sampleId = 0;
        public File sampleFile = null;
        public String filename = null;

        public FileType(Context targetContext, String label) {

            if (label.equals(JPG)) {
                this.label = label;
                this.type = "image/jpeg";
                this.sampleId = com.rstar.mobile.csc205sp2015.test.R.raw.sample_jpg;
                this.filename = getFilename();
                this.sampleFile = getFile(targetContext);
            }
            else if (label.equals(PNG)) {
                this.label = label;
                this.type = "image/png";
                this.sampleId = com.rstar.mobile.csc205sp2015.test.R.raw.sample_png;
                this.filename = getFilename();
                this.sampleFile = getFile(targetContext);
            }
            else if (label.equals(TIF)) {
                this.label = label;
                this.type = "image/tiff";
                this.sampleId = com.rstar.mobile.csc205sp2015.test.R.raw.sample_tif;
                this.filename = getFilename();
                this.sampleFile = getFile(targetContext);
            }
            else if (label.equals(TXT)) {
                this.label = label;
                this.type = "text/plain";
                this.sampleId = com.rstar.mobile.csc205sp2015.test.R.raw.sample_txt;
                this.filename = getFilename();
                this.sampleFile = getFile(targetContext);
            }
            else if (label.equals(PDF)) {
                this.label = label;
                this.type = "pdf";
                this.sampleId = com.rstar.mobile.csc205sp2015.test.R.raw.sample_pdf;
                this.filename = getFilename();
                this.sampleFile = getFile(targetContext);
            }
            else {
                // something unknown
            }
        }

        private String getFilename() {
            return SampleFilename + "." + label;
        }
        public File getFile(Context targetContext) {
            if (filename==null) filename = getFilename();
            return new File(Student.getStudentDir(targetContext), filename);
        }
        public void getResourceAsFile(Context testContext, Context targetContext) throws Exception {
            if (sampleFile==null) sampleFile = getFile(targetContext);
            File dir = Student.getStudentDir(targetContext);
            if (!dir.exists()) dir.mkdir();
            DataUtils.copyRawFileToInternal(testContext, targetContext, sampleId, sampleFile);
        }
    }
}
