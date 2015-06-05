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

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.fields.CourseFields;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Header;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by AHui on 1/17/15.
 */
public class CourseUtils {

    public static class CourseFileData {
        private static final String TAG = CourseFileData.class.getSimpleName();

        public String serverPasscode;
        public ArrayList<Header> headerArrayList;
        public CourseFileData() {
            serverPasscode = null;
            headerArrayList = new ArrayList<Header>();
        }
        public void clear() {
            serverPasscode = null;
            headerArrayList.clear();
        }

        public CourseFileData(Context targetContext, File f) throws Exception {
            headerArrayList = new ArrayList<Header>();
            serverPasscode = null;

            CourseFields.Constants constants = new CourseFields.Constants();
            try {
                String data = IO.loadFileAsString(targetContext, f);
                String line[] = data.split(constants.LineDelimiter);
                this.serverPasscode = line[0];
                for (int index=1; index<line.length; index++) {
                    Header header = new Header(line[index]);
                    this.headerArrayList.add(header);
                }
            } catch (Exception e) {
                Savelog.i(TAG, "Cannot open file");
                this.clear();
            }
            constants.detach();
        }
    }






    public static class DataSet {
        public static final String publicTestSite = AppSettings.PublicSite + "sample-data/test/";
        public String courseLink = "";
        public int moduleNumbers[];

        public DataSet(int set) {
            if (set==0) {
                courseLink = publicTestSite + "info_good.txt";
                moduleNumbers = new int[0];
            }
            else if (set==-1) {
                courseLink = publicTestSite + "info_bad.txt";
                moduleNumbers = new int[0];
            }
            else if (set==1) {
                courseLink = publicTestSite + "set1info.txt";
                moduleNumbers = new int[1];
                moduleNumbers[0] = 101;
            }
            else if (set==2) {
                courseLink = publicTestSite + "set2info.txt";
                moduleNumbers = new int[2];
                moduleNumbers[0] = 101;
                moduleNumbers[1] = 102;
            }
            else if (set==3) {
                courseLink = publicTestSite + "set3info.txt";
                moduleNumbers = new int[2];
                moduleNumbers[0] = 101;
                moduleNumbers[1] = 102;
            }
            else if (set==4) {
                courseLink = publicTestSite + "set4info.txt";
                moduleNumbers = new int[1];
                moduleNumbers[0] = 102;
            }
            else if (set==5) {
                courseLink = publicTestSite + "set5info.txt";
                moduleNumbers = new int[1];
                moduleNumbers[0] = 101;
            }

        }
    }

}
