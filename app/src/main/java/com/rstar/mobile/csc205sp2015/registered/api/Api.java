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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class Api {
    private static final String TAG = Api.class.getSimpleName() + "_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final String contentType = "application/x-www-form-urlencoded";
    private static final String defaultStatus = "No POST made";
    private static final String headerStatus = "Status";
    private static final String status_ok = "OK";
    private static final String apiDirname = "api";
    private static final String dir = "/";

    // The following need to be tuned.
    protected static String courseId = AppSettings.API_courseId;
    protected static String semester = AppSettings.API_semester;
    protected static String siteId = PrivateSite.getSiteId();

    private String dataFilename = "";
    private String communication = defaultStatus;
    private Header responseHeader[] = null;


    protected abstract String getSite(Context context);

    protected abstract List<NameValuePair> setPostData();


    protected Api(Context context, String dataFilename) throws Exception {
        this.dataFilename = dataFilename;

        File dir = getApiDir(context);
        if (dir==null || dataFilename==null || dataFilename.length()==0) throw new Exception("Cannot create directory");
        // prepare directory
        if (dir!=null && !dir.exists()) dir.mkdir();
    }

    // This requires network access. Therefore, it must be run within an asyncTask
    protected void post(Context appContext) throws IOException {
        Savelog.d(TAG, debug, "post()");

        // reset communication
        communication = "";

        String site = getSite(appContext);
        if (site == null || site.length() == 0) {
            Savelog.d(TAG, debug, "Site not available.");
            throw new IOException("Bad site url: " + (site == null ? "(null)" : site));
        }

        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(site);
        httpPost.setHeader("Content-type", contentType);

        // Set the fields, depending on the type of operation requested
        List<NameValuePair> nameValuePairs = setPostData();


        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = client.execute(httpPost);

        File destinationFile = getSavedFile(appContext);
        try {
            IO.saveStreamAsFile(response.getEntity().getContent(), destinationFile);
            Savelog.d(TAG, debug, "data obtained and saved to file " + destinationFile.getName());
        } catch (Exception e) {
            Savelog.e(TAG, "Error saving file ", e);
            destinationFile.delete();
        }


        communication = "Sending:\n";
        for (NameValuePair arg : nameValuePairs) {
            communication += arg.getName() + "=" + arg.getValue() + "\n";
        }

        communication += "\nResponse:\n";
        responseHeader = response.getAllHeaders();
        for (Header h : responseHeader) {
            communication += h.toString() + "\n";
        }

        Savelog.d(TAG, debug, "POST Status:\n" + communication);
    }

    // If a subclass override post(), then it must use this function to pass back the post results
    protected void setPostResults(String communication, Header[] responseHeader) {
        if (communication != null) {
            this.communication = communication;
        }
        this.responseHeader = responseHeader;
    }


    public boolean isOK() {
        if (responseHeader == null) return false;
        for (Header h : responseHeader) {
            if (h.getName().equals(headerStatus)) {
                if (h.getValue().equals(status_ok))
                    return true;
            }
        }
        return false;
    }



    private static String getApiDirname() {
        return apiDirname + dir;
    }

    public static File getApiDir(Context context) {
        return IO.getInternalDir(context, getApiDirname());
    }

    protected File getSavedFile(Context context) {
        return new File(getApiDir(context), dataFilename);
    }

    protected long getFileTimeStamp(Context context) {
        // If file does not exist or bad context, return 0. Else, return its last modified time
        long timeStamp = 0;
        if (context == null) return timeStamp;

        File file = getSavedFile(context);
        if (file != null && file.exists())
            timeStamp = file.lastModified();
        return timeStamp;
    }

    protected boolean isCached(Context context) {
        if (context == null) return false;
        File f = getSavedFile(context);
        return (f != null && f.exists());
    }


    public String getCommunication() {
        return communication;
    }

    public void clear(Context context) {
        if (context == null) return;
        File f = getSavedFile(context);
        if (f != null && f.exists()) f.delete();
        // Do not clear directory
    }
}
