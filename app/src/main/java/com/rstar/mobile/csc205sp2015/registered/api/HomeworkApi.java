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

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class HomeworkApi extends Api {
	private static final String TAG = HomeworkApi.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;

    // The following fields are used for registration
    private static final String fieldTag = "tag";
	private static final String fieldAccessCode = "accessCode";
    private static final String fieldCourseId = "courseId";
    private static final String fieldSemester = "semester";
    private static final String fieldSiteId = "siteId"; // private code to make sure this app matches with the site
    private static final String fieldModule = "module";
    private static final String fieldFile = "filename";
	private static final int numberOfFields = 7;
	private static final String defaultFilename = "hw.dat";
    private static final String tag_post = "submit";
    private static final String tag_get = "download";

    private String accessCode = LoginApi.DefaultAccessCode;
    private int moduleNumber = Module.DefaultModuleNumber;
    private File uploadFile = null;
    private String fileType = "";
    private String data = "";



    // This requires network access. Therefore, it must be run within an asyncTask
	public HomeworkApi(Context context, String accessCode, int moduleNumber, File uploadFile, String fileType) throws Exception {
		super(context, defaultFilename);
		Savelog.d(TAG, debug, "constructor called");

		// do the final bit of checking to prevent sending bad data to server
        if (context==null) throw new Exception("invalid parameter");
        if (!Validity.isValid(accessCode)) throw new Exception("invalid parameter");

        int numberOfModules = Course.get(context).getNumberOfModules();
		if (moduleNumber<1 || moduleNumber>numberOfModules) throw new Exception("invalid parameters");

        if (uploadFile==null || !uploadFile.exists()) throw new Exception("Bad file cannot be uploaded.");

		getFields(accessCode, moduleNumber, uploadFile, fileType);
        post(context);
        data = IO.loadFileAsString(context, getSavedFile(context));
    }

    @Override
    protected void post(Context appContext) throws IOException {
        Savelog.d(TAG, debug, "post()");

        String site = getSite(appContext);
        if (site == null || site.length() == 0) {
            Savelog.d(TAG, debug, "Site not available.");
            throw new IOException("Bad site url: " + (site == null ? "(null)" : site));
        }

        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(site);

        // clear post results
        setPostResults("", null);

        String communication = "";

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        // Determine the type of the file being uploaded
        Uri uri = Uri.fromFile(uploadFile);
        ContentResolver cR = appContext.getContentResolver();
        String mimeType = cR.getType(uri);
        if (mimeType!=null) {
            Savelog.d(TAG, debug, "mime type is " + mimeType);
            multipartEntityBuilder.addBinaryBody(fieldFile, uploadFile, ContentType.create(mimeType), uploadFile.getName());
        }
        else {
            Savelog.d(TAG, debug, "mime type unknown. Using provided " + fileType);
            multipartEntityBuilder.addBinaryBody(fieldFile, uploadFile, ContentType.create(fileType), uploadFile.getName());
        }

        // Add other fields
        multipartEntityBuilder.addTextBody(fieldTag, tag_post);
        multipartEntityBuilder.addTextBody(fieldAccessCode, accessCode);
        multipartEntityBuilder.addTextBody(fieldCourseId, courseId);
        multipartEntityBuilder.addTextBody(fieldSemester, semester);
        multipartEntityBuilder.addTextBody(fieldModule, Integer.toString(moduleNumber));
        multipartEntityBuilder.addTextBody(fieldSiteId, siteId);

        HttpEntity entity = multipartEntityBuilder.build();
        httpPost.setEntity(entity);

        communication = "Sending: \n";


        try {
            // Just keep a record of what content we are sending.
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            entity.writeTo(bytes);
            String content = bytes.toString();
            Savelog.d(TAG, debug, "Content of entity to post:\n" + content);
            communication += content + "\n";
        } catch (Exception e) {
            Savelog.w(TAG, "Error checking content of entity to post:\n" + e.getMessage());
        }

        HttpResponse response = client.execute(httpPost);


        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String data = "";
        String line = "";


        while ((line = rd.readLine()) != null) {
            data += line + "\n";
        }

        // When done, just save the file
        File destinationFile = getSavedFile(appContext);
        try {
            IO.saveStringAsFile(appContext, destinationFile, data);
            Savelog.d(TAG, debug, "data obtained and saved to file " + destinationFile.getName());
        } catch (IOException e) {
            Savelog.e(TAG, "Error saving file ", e);
            destinationFile.delete();
        }


        communication += "\nResponse:\n";
        Header[] responseHeader = response.getAllHeaders();
        for (Header h : responseHeader) {
            communication += h.toString() + "\n";
        }

        Savelog.d(TAG, debug, "POST Status: " + communication);

        // Pass communication back to parent
        setPostResults(communication, responseHeader);
    }


    private void getFields(String accessCode, int moduleNumber, File uploadFile, String fileType) {
		if (accessCode!=null) this.accessCode = accessCode;
        this.moduleNumber = moduleNumber;
        if (uploadFile!=null && uploadFile.exists()) this.uploadFile = uploadFile;
        if (fileType!=null && fileType.length()!=0) this.fileType = fileType;
	}

	@Override
	protected List<NameValuePair> setPostData() {
        // This is only used by the post method in the parent class. We have redefined post here.
        // So we do not need to use this method. Return null.
		return null;
	}
	
	

	@Override
	public String getCommunication() {
		return super.getCommunication() + "\nData:\n" + data;
	}


	public String getData() { return data; }


    // For posting homework
	@Override
	protected String getSite(Context context) {
		return PrivateSite.get(context).getHomeworkApi();
	}


    // For getting homework
    public static String getDownloadLink(Context context, String accessCode, int moduleNumber) {
        // Check that the parameters are good.
        if (context==null) return "";
        if (accessCode==null || accessCode.length()==0) return "";
        if (moduleNumber<1) return "";
        String site = PrivateSite.get(context).getHomeworkApi();
        if (site==null || site.length()==0) return "";

        String link = site
                + "?" + fieldTag + "=" + tag_get
                + "&" + fieldAccessCode + "=" + accessCode
                + "&" + fieldCourseId + "=" + courseId
                + "&" + fieldSemester + "=" + semester
                + "&" + fieldSiteId + "=" + siteId
                + "&" + fieldModule + "=" + moduleNumber;
        return link;
    }

}
