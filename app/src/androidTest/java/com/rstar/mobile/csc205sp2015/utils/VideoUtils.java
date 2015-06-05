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

import com.rstar.mobile.csc205sp2015.fields.ModuleFields;
import com.rstar.mobile.csc205sp2015.fields.VideoListFields;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.module.VideoList;

import java.io.File;

/**
 * Created by Ahui on 1/16/15.
 */
public class VideoUtils {
    public static int TestModuleNumber = 100;
    public static int TestNumberOfPages = 10;


    public static class VideoListFileData {
        private static final String TAG = VideoListFileData.class.getSimpleName();
        public int numberOfPages = 0;
        public boolean availability[];
        public String description[];
        public String videoSize[];
        public int numberOfRecords = 0;

        public VideoListFileData(Context targetContext, int numberOfPages, File videoListFile) throws Exception {
            VideoListFields.Constants constants = new VideoListFields.Constants();

            if (numberOfPages>0) this.numberOfPages = numberOfPages;

            availability = new boolean[numberOfPages];
            description = new String[numberOfPages];
            videoSize = new String[numberOfPages];

            if (videoListFile!=null && videoListFile.exists()) {
                try {
                    String videoData = IO.loadFileAsString(targetContext, videoListFile);
                    if (videoData.trim().length() > 0) {
                        String[] record = videoData.split(constants.LineDelimiter);
                        for (int entry = 0; entry < record.length; entry++) {
                            String[] field = record[entry].split(constants.FieldDelimiter);
                            int pageNumber = Integer.valueOf(field[constants.fieldModuleNumber]);
                            int index = pageNumber - 1;
                            availability[index] = true;
                            description[index] = field[constants.fieldDescription];
                            videoSize[index] = field[constants.fieldVideoSize];
                        }
                    }
                } catch (Exception e) {
                    for (int index = 0; index < numberOfPages; index++) {
                        availability[index] = false;
                        description[index] = "";
                        videoSize[index] = VideoList.ZeroSize;
                    }
                }
            }

            numberOfRecords = 0;
            for (int index=0; index<numberOfPages; index++) {
                if (availability[index]) numberOfRecords++;
            }
        }
    }

    public static class DataSet {
        public int videoListId;
        public int numberOfPages;
        public File moduleDir;
        public File videoListFile;

        public DataSet(Context targetContext, int set) throws Exception {
            numberOfPages = TestNumberOfPages;
            moduleDir = IO.getInternalDir(targetContext, Module.getModuleDirname(TestModuleNumber));
            videoListFile = new File(moduleDir, ModuleFields.Constants.getVideoListFilename());

            if (set==0) {
                // Empty one
                videoListId = com.rstar.mobile.csc205sp2015.test.R.raw.videolist_empty;
            }
            else if (set==1) {
                // Good one
                videoListId = com.rstar.mobile.csc205sp2015.test.R.raw.videolist_good1;
            }
            else if (set==2) {
                // Good one
                videoListId = com.rstar.mobile.csc205sp2015.test.R.raw.videolist_good2;
            }
            else if (set==-1) {
                // Bad one
                videoListId = com.rstar.mobile.csc205sp2015.test.R.raw.videolist_bad1;
            }
            else if (set==-2) {
                // Bad one
                videoListId = com.rstar.mobile.csc205sp2015.test.R.raw.videolist_bad2;
            }
            else {
                moduleDir = null;
                videoListFile = null;
            }

        }
    }

}
