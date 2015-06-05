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

import android.content.Context;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.io.File;

/**
 * Created by AHui
 */
public class VideoList {
    private static final String TAG = VideoList.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final String LineDelimiter = "\\n";
    private static final String FieldDelimiter = "\\t";
    public static final String ZeroSize = "0";
    private static final int fieldModuleNumber = 0;
    private static final int fieldDescription = 1;
    private static final int fieldVideoSize = 2;

    private boolean availability[] = null;
    private String description[] = null;
    private String videoSize[] = null;
    private int numberOfPages = 0;

    // Always create a videoList, even if the input parameters are bad.
    // The module must exist even if the videoListFile is corrupted.
    // For corrupted videoListfile, return an empty videoList.
    public VideoList(Context context, int numberOfPages, File videoListFile) {
        if (numberOfPages>0)
            this.numberOfPages = numberOfPages;

        Savelog.d(TAG, debug, "Getting video availability now");
        availability = new boolean[numberOfPages];
        description = new String[numberOfPages];
        videoSize = new String[numberOfPages];

        clearEntries();

        if (videoListFile!=null && videoListFile.exists()) {
            try {
                String videoData = IO.loadFileAsString(context, videoListFile);
                if (videoData.trim().length()>0) {
                    String[] record = videoData.split(LineDelimiter);
                    for (int entry=0; entry<record.length; entry++) {
                        String[] field = record[entry].split(FieldDelimiter);
                        int pageNumber = Integer.valueOf(field[fieldModuleNumber]);
                        int index = pageNumber-1;
                        availability[index] = true;
                        description[index] = field[fieldDescription];
                        videoSize[index] = field[fieldVideoSize];
                        Savelog.d(TAG, debug, "Video available for page " + pageNumber);
                    }
                    Savelog.d(TAG, debug, "Total videos=" + record.length);
                } // else: file is empty. No video available
                else {
                    Savelog.d(TAG, debug, "no video");
                }
            } catch (Exception e) {
                Savelog.w(TAG, "Cannot load videolist from" + (videoListFile==null? "null" : videoListFile.getAbsolutePath()), e);
                clearEntries();
            }
        }
        else {
            Savelog.w(TAG, "video list not available.");
        }

    }


    public boolean isVideoAvailable(int pageNumber) {
        if (pageNumber<=0 || pageNumber>numberOfPages) return false;
        return availability[pageNumber-1];
    }

    public String getDescription(int pageNumber) {
        if (pageNumber<=0 || pageNumber>numberOfPages) return "";
        return description[pageNumber-1];
    }

    public String getVideoSize(int pageNumber) {
        if (pageNumber<=0 || pageNumber>numberOfPages) return ZeroSize;
        return videoSize[pageNumber-1];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VideoList) {
            VideoList v = (VideoList) obj;
            if (this.numberOfPages!=v.numberOfPages) return false;
            if (this.availability.length!=v.availability.length) return false;
            if (this.description.length!=v.description.length) return false;
            if (this.videoSize.length!=v.videoSize.length) return false;
            for (int index=0; index<this.numberOfPages; index++) {
                if (this.availability[index]!=v.availability[index]) return false;
                if (!this.description[index].equals(v.description[index])) return false;
                if (!this.videoSize[index].equals(v.videoSize[index])) return false;
            }
            return true;
        }
        return false;
    }


    private void clearEntries(){
        // Call this to clear array entries without modifying the numberOfPages
        for (int index=0; index<numberOfPages; index++) {
            availability[index]=false;
            description[index]="";
            videoSize[index]= ZeroSize;
        }
    }
}
