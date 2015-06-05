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
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ScoreApi extends Api {
	private static final String TAG = ScoreApi.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;

    // The following fields are used for verifying access permission
    private static final String fieldTag = "tag";
    private static final String fieldUserId = "userId";
    private static final String fieldPassword = "password";
    private static final String fieldAccessCode = "accessCode";
    private static final String fieldCourseId = "courseId";
    private static final String fieldSemester = "semester";
    private static final String fieldSiteId = "siteId"; // private code to make sure this app matches with the site
    private static final String fieldModule = "module";
	private static final int numberOfFields = 8;
    private static final String defaultScoreFilename = "Score";
    private static final String defaultFeedbackFilename = "Feedback";
    private static final String defaultSubmittedFilename = "Submitted";
    private static final String defaultExtension = ".dat";
    private static final double defaultScore = 0;
    private static final String tag_score = "score";
    private static final String tag_feedback = "feedback";
    private static final String tag_submitted = "submitted";
    private static final String tag_default = "";

    public static final int request_score = 2001;
    public static final int request_feedback = 2002;
    public static final int request_submitted = 2003;
    public static final int request_default = 0;

    private String userId = "";
    private String password = "";
    private String accessCode = LoginApi.DefaultAccessCode;
    private int moduleNumber = Module.DefaultModuleNumber;
    private String postTag = tag_default;
    private String data = "";


    // This requires network access. Therefore, it must be run within an asyncTask
	public ScoreApi(Context context, String userId, String password, String accessCode, int moduleNumber, int request) throws Exception {
        // Intentionally do not associate the user's id and the module number with the data obtained.
        // Do not cache this data on device. User is required to login for every access.
		super(context, getDefaultFilename(request));
		Savelog.d(TAG, debug, "constructor called");

		// do the final bit of checking to prevent sending bad data to server
        if (context==null) throw new Exception("invalid parameters");
        if (!Validity.isValid(userId)) throw new Exception("invalid parameters");
        if (!Validity.isValid(password)) throw new Exception("invalid parameters");
        if (!Validity.isValid(accessCode)) throw new Exception("invalid parameters");

        int numberOfModules = Course.get(context).getNumberOfModules();
        if (moduleNumber<1 || moduleNumber>numberOfModules) throw new Exception("invalid parameters #modules=" + numberOfModules);

        if (request!=request_score && request!=request_feedback && request!=request_submitted) throw new Exception("invalid parameters");

        getFields(userId, password, accessCode, moduleNumber, request);

        post(context);
        data = IO.loadFileAsString(context, getSavedFile(context)); // saved file contains just the score
	}


    private void getFields(String userId, String password, String accessCode, int moduleNumber, int request) {
        if (userId!=null) this.userId = userId;
        if (password!=null) this.password = password;
        if (accessCode!=null) this.accessCode = accessCode;
        this.moduleNumber = moduleNumber;
        if (request==request_score) {
            this.postTag = tag_score;
        }
        else if (request==request_feedback) {
            this.postTag = tag_feedback;
        }
        else if (request==request_submitted) {
            this.postTag = tag_submitted;
        }
    }

	@Override
	protected List<NameValuePair> setPostData() {
		Savelog.d(TAG, debug, "Creating nameValuePairs of size " + numberOfFields);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(fieldCourseId, courseId));
        nameValuePairs.add(new BasicNameValuePair(fieldSemester, semester));
        nameValuePairs.add(new BasicNameValuePair(fieldSiteId, siteId));
        nameValuePairs.add(new BasicNameValuePair(fieldTag, postTag));
        nameValuePairs.add(new BasicNameValuePair(fieldUserId, userId));
        nameValuePairs.add(new BasicNameValuePair(fieldPassword, password));
        nameValuePairs.add(new BasicNameValuePair(fieldAccessCode, accessCode));
        nameValuePairs.add(new BasicNameValuePair(fieldModule, Integer.toString(moduleNumber)));
		return nameValuePairs;
	}

    private static String getDefaultFilename(int request) {
        String filename = "";
        if (request==request_score)
            filename = defaultScoreFilename;
        else if (request==request_feedback)
            filename = defaultFeedbackFilename;
        else if (request==request_submitted)
            filename = defaultSubmittedFilename;
        return filename + defaultExtension;
    }

    @Override
	public String getCommunication() {
        return super.getCommunication() + "\nData:\n" + data;
	}


    public double getScore(Context context) {
        double score = defaultScore;
        if (isOK() && postTag.equals(tag_score)) {
            try {
                String result = IO.loadFileAsString(context, getSavedFile(context));
                score = Double.parseDouble(result.trim());
            }
            catch (Exception e) {
                Savelog.w(TAG, "Score not found");
            }
        }
        return score;
    }

    public File getFile(Context context) {
        File file = null;
        if (isOK() && (postTag.equals(tag_feedback) || postTag.equals(tag_submitted))) {
            file = getSavedFile(context);
        }
        return file;
    }

	@Override
	protected String getSite(Context context) {
		return PrivateSite.get(context).getScoreApi();
	}

}
