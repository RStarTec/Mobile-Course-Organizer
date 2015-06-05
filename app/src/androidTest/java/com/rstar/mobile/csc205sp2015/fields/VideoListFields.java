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

package com.rstar.mobile.csc205sp2015.fields;

import com.rstar.mobile.csc205sp2015.module.VideoList;

/**
 * Created by AHui on 1/21/15.
 */
public class VideoListFields {
    public static class Constants extends Fields.Constants {
        private static final String privateField1 = "LineDelimiter";
        private static final String privateField2 = "FieldDelimiter";
        private static final String privateField3 = "fieldModuleNumber";
        private static final String privateField4 = "fieldDescription";
        private static final String privateField5 = "fieldVideoSize";


        public String LineDelimiter = null;
        public String FieldDelimiter = null;
        public int fieldModuleNumber = -1;
        public int fieldDescription = -1;
        public int fieldVideoSize = -1;

        public Constants() throws Exception { get(); }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            LineDelimiter = getLineDelimiter();
            FieldDelimiter = getFieldDelimiter();
            fieldModuleNumber = getFieldModuleNumber();
            fieldDescription = getFieldDescription();
            fieldVideoSize = getFieldVideoSize();
        }
        @Override
        public void detach() {
            LineDelimiter = null;
            FieldDelimiter = null;
            fieldModuleNumber = -1;
            fieldDescription = -1;
            fieldVideoSize = -1;
        }

        public static String getLineDelimiter() throws Exception
            { return RefUtil.getPrivateField(VideoList.class, privateField1, String.class); }
        public static String getFieldDelimiter() throws Exception
            { return RefUtil.getPrivateField(VideoList.class, privateField2, String.class); }
        public static Integer getFieldModuleNumber() throws Exception
            { return RefUtil.getPrivateField(VideoList.class, privateField3, Integer.class); }
        public static Integer getFieldDescription() throws Exception
            { return RefUtil.getPrivateField(VideoList.class, privateField4, Integer.class); }
        public static Integer getFieldVideoSize() throws Exception
            { return RefUtil.getPrivateField(VideoList.class, privateField5, Integer.class); }
    }

    public static class Variables extends Fields.Variables<VideoList> {
        private static final String privateField1 = "availability";
        private static final String privateField2 = "description";
        private static final String privateField3 = "videoSize";
        private static final String privateField4 = "numberOfPages";

        public boolean availability[] = null;
        public String description[] = null;
        public String videoSize[] = null;
        public int numberOfPages = 0;

        public Variables(VideoList videoList) throws Exception {
            refresh(videoList);
        }

        @Override
        public void refresh(VideoList videoList) throws Exception {
            availability = getAvailability(videoList);
            description = getDescription(videoList);
            videoSize = getVideoSize(videoList);
            numberOfPages = getNumberOfPages(videoList);
        }
        @Override
        public void detach() {
            availability = null;
            description = null;
            videoSize = null;
            numberOfPages = 0;
        }

        public static boolean[] getAvailability(VideoList videoList) throws Exception
            { return RefUtil.getPrivateField(videoList, privateField1, boolean[].class); }
        public static String[] getDescription(VideoList videoList) throws Exception
            { return RefUtil.getPrivateField(videoList, privateField2, String[].class); }
        public static String[] getVideoSize(VideoList videoList) throws Exception
            { return RefUtil.getPrivateField(videoList, privateField3, String[].class); }
        public static Integer getNumberOfPages(VideoList videoList) throws Exception
            { return RefUtil.getPrivateField(videoList, privateField4, Integer.class); }
    }
}
