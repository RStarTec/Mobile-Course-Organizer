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

package com.rstar.mobile.csc205sp2015.module;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.io.Savelog;

/**
 * Created by AHui on 1/16/15.
 */
public class Header {
    private static final String TAG = Header.class.getSimpleName();
    private static final boolean debug = AppSettings.defaultDebug;

    private static final String patternFieldDelimiter = "\\t";
    private static final String fieldDelimiter = "\t";
    private static final int fieldModuleNumber = 0;
    private static final int fieldTitle = 1;
    private static final int fieldDir = 2;
    private static final int fieldZip = 3;
    private static final int fieldOpts = 4;
    private static final int fieldSlides = 5;
    private static final int fieldAudios = 6;
    private static final int fieldTranscripts = 7;
    private static final int fieldExtras = 8;
    private static final int fieldQuizzes = 9;
    private static final int fieldVideo = 10;
    private static final int fieldTime = 11;
    private static final int TotalFields = 12;


    public static final String NoDate = "";
    private int moduleNumber = 0;
    private String title = "";
    private String onlineDirectory = "";
    private String onlinePackageName = "";
    private String downloadOptions = "";
    private int numberOfPages = 0;
    private int numberOfAudios = 0;
    private int numberOfTranscripts = 0;
    private int numberOfExtras = 0;
    private int numberOfQuizzes = 0;
    private int numberOfVideos = 0;
    private String time = NoDate;


    // Do not accept any bad header because a module is not defined when a header is bad
    public Header(String record) throws Exception {
        Savelog.d(TAG, debug, "Record=" + record);
        String[] field = record.split(patternFieldDelimiter);
        if (field.length != TotalFields) throw new Exception("Number of fields unmatched " + field.length + "!=" + TotalFields);
        setModuleNumber(Integer.parseInt(field[fieldModuleNumber]));
        setTitle(field[fieldTitle]);
        setOnlineDirectory(field[fieldDir]);
        setOnlinePackageName(field[fieldZip]);
        setDownloadOptions(field[fieldOpts]);
        setNumberOfPages(Integer.parseInt(field[fieldSlides]));
        setNumberOfAudios(Integer.parseInt(field[fieldAudios]));
        setNumberOfTranscripts(Integer.parseInt(field[fieldTranscripts]));
        setNumberOfExtras(Integer.parseInt(field[fieldExtras]));
        setNumberOfQuizzes(Integer.parseInt(field[fieldQuizzes]));
        setNumberOfVideos(Integer.parseInt(field[fieldVideo]));
        setTime(field[fieldTime]);
    }

    public Header(Header h) {
        if (h==null) return;
        // When copying, make sure all fields are consistent
        if (h.moduleNumber>=0) this.moduleNumber = h.moduleNumber;
        if (h.title!=null && h.title.length()>0) this.title = h.title;
        if (h.onlineDirectory!=null && h.onlineDirectory.length()>0) this.onlineDirectory = h.onlineDirectory;
        if (h.onlinePackageName!=null && h.onlinePackageName.length()>0) this.onlinePackageName = h.onlinePackageName;
        if (h.downloadOptions!=null && h.downloadOptions.length()>0) this.downloadOptions = h.downloadOptions;
        if (h.numberOfPages>0) this.numberOfPages = h.numberOfPages;
        if (h.numberOfAudios>0) this.numberOfAudios = h.numberOfAudios;
        if (h.numberOfTranscripts>0 && h.numberOfTranscripts<=h.numberOfPages) this.numberOfTranscripts = h.numberOfTranscripts;
        if (h.numberOfExtras>0 && h.numberOfExtras<=h.numberOfPages) this.numberOfExtras = h.numberOfExtras;
        if (h.numberOfQuizzes>0 && h.numberOfQuizzes<=h.numberOfPages) this.numberOfQuizzes = h.numberOfQuizzes;
        if (h.numberOfVideos>0 && h.numberOfVideos<=h.numberOfPages) this.numberOfVideos = h.numberOfVideos;
        if (h.time!=null && h.time.length()>0) this.time = h.time;
    }

    public int getModuleNumber() { return moduleNumber; }
    public String getTitle() { return title; }
    public String getOnlineDirectory() { return onlineDirectory; }
    public String getOnlinePackageName() { return onlinePackageName; }
    public String getDownloadOptions() { return downloadOptions; }
    public int getNumberOfPages() { return numberOfPages; }
    public int getNumberOfAudios() { return numberOfAudios; }
    public int getNumberOfTranscripts() { return numberOfTranscripts; }
    public int getNumberOfExtras() { return numberOfExtras; }
    public int getNumberOfQuizzes() { return numberOfQuizzes; }
    public int getNumberOfVideos() { return numberOfVideos; }
    public String getTime() { return time; }

    // Allow resetting here.
    // Cannot constrain the numbers to be less than numberOfPages
    public void setModuleNumber(int moduleNumber)
    { this.moduleNumber = moduleNumber; }
    public void setTitle(String title)
    { this.title = title!=null ? title : ""; }
    public void setOnlineDirectory(String onlineDirectory)
    { this.onlineDirectory = onlineDirectory!=null ? onlineDirectory : "" ; }
    public void setOnlinePackageName(String onlinePackageName)
    { this.onlinePackageName = onlinePackageName!=null ? onlinePackageName : ""; }
    public void setDownloadOptions(String downloadOptions)
    { this.downloadOptions = downloadOptions!=null ? downloadOptions : ""; }
    public void setNumberOfPages(int numberOfPages)
    { this.numberOfPages = numberOfPages>=0 ? numberOfPages : 0; }
    public void setNumberOfAudios(int numberOfAudios)
    { this.numberOfAudios = numberOfAudios>=0 ? numberOfAudios : 0; }
    public void setNumberOfTranscripts(int numberOfTranscripts)
    { this.numberOfTranscripts = numberOfTranscripts>=0 ? numberOfTranscripts : 0; }
    public void setNumberOfExtras(int numberOfExtras)
    { this.numberOfExtras = numberOfExtras>=0 ? numberOfExtras : 0; }
    public void setNumberOfQuizzes(int numberOfQuizzes)
    { this.numberOfQuizzes = numberOfQuizzes>=0 ? numberOfQuizzes : 0; }
    public void setNumberOfVideos(int numberOfVideos)
    { this.numberOfVideos = numberOfVideos>=0 ? numberOfVideos : 0; }
    public void setTime(String time)
    { this.time = time!=null ? time : ""; }

    @Override
    public String toString() {
        String data = "";
        data += moduleNumber + fieldDelimiter;
        data += title + fieldDelimiter;
        data += onlineDirectory + fieldDelimiter;
        data += onlinePackageName + fieldDelimiter;
        data += downloadOptions + fieldDelimiter;
        data += numberOfPages + fieldDelimiter;
        data += numberOfAudios + fieldDelimiter;
        data += numberOfTranscripts + fieldDelimiter;
        data += numberOfExtras + fieldDelimiter;
        data += numberOfQuizzes + fieldDelimiter;
        data += numberOfVideos + fieldDelimiter;
        data += time;
        return data;
    }

    public void clear() {
        moduleNumber = 0;
        title = "";
        onlineDirectory = "";
        onlinePackageName = "";
        downloadOptions = "";
        numberOfPages = 0;
        numberOfAudios = 0;
        numberOfTranscripts = 0;
        numberOfExtras = 0;
        numberOfQuizzes = 0;
        numberOfVideos = 0;
        time = NoDate;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Header) {
            Header h = (Header) obj;
            if (this.moduleNumber==h.moduleNumber
                    && this.title!=null && this.title.equals(h.title)
                    && this.onlineDirectory!=null && this.onlineDirectory.equals(h.onlineDirectory)
                    && this.onlinePackageName!=null && this.onlinePackageName.equals(h.onlinePackageName)
                    && this.downloadOptions!=null && this.downloadOptions.equals(h.downloadOptions)
                    && this.numberOfPages==h.numberOfPages
                    && this.numberOfAudios==h.numberOfAudios
                    && this.numberOfTranscripts==h.numberOfTranscripts
                    && this.numberOfExtras==h.numberOfExtras
                    && this.numberOfQuizzes==h.numberOfQuizzes
                    && this.numberOfVideos==h.numberOfVideos
                    && this.time.equals(h.time))
                return true;
        }
        return false;
    }
}
