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

import com.rstar.mobile.csc205sp2015.module.Header;

/**
 * Created by AHui on 1/15/15.
 */
public class HeaderFields {
    public static class Constants extends Fields.Constants {
        private static final String privateField2 = "patternFieldDelimiter";
        private static final String privateField3 = "fieldDelimiter";
        private static final String privateField4 = "fieldModuleNumber";
        private static final String privateField5 = "fieldTitle";
        private static final String privateField6 = "fieldDir";
        private static final String privateField7 = "fieldZip";
        private static final String privateField8 = "fieldOpts";
        private static final String privateField9 = "fieldSlides";
        private static final String privateField10 = "fieldAudios";
        private static final String privateField11 = "fieldTranscripts";
        private static final String privateField13 = "fieldExtras";
        private static final String privateField14 = "fieldQuizzes";
        private static final String privateField15 = "fieldVideo";
        private static final String privateField16 = "fieldTime";
        private static final String privateField17 = "TotalFields";

        public String patternFieldDelimiter = null;
        public String fieldDelimiter = null;
        public int fieldModuleNumber = -1;
        public int fieldTitle = -1;
        public int fieldDir = -1;
        public int fieldZip = -1;
        public int fieldOpts = -1;
        public int fieldSlides = -1;
        public int fieldAudios = -1;
        public int fieldTranscripts = -1;
        public int fieldExtras = -1;
        public int fieldQuizzes = -1;
        public int fieldVideo = -1;
        public int fieldTime = -1;
        public int TotalFields = -1;

        public Constants() throws Exception { get(); }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            patternFieldDelimiter = getPatternFieldDelimiter();
            fieldDelimiter = getFieldDelimiter();
            fieldModuleNumber = getFieldModuleNumber();
            fieldTitle = getFieldTitle();
            fieldDir = getFieldDir();
            fieldZip = getFieldZip();
            fieldOpts = getFieldOpts();
            fieldSlides = getFieldSlides();
            fieldAudios = getFieldAudios();
            fieldTranscripts = getFieldTranscripts();
            fieldExtras = getFieldExtras();
            fieldQuizzes = getFieldQuizzes();
            fieldVideo = getFieldVideo();
            fieldTime = getFieldTime();
            TotalFields = getTotalFields();
        }
        @Override
        public void detach() {
            patternFieldDelimiter = null;
            fieldDelimiter = null;
            fieldModuleNumber = -1;
            fieldTitle = -1;
            fieldDir = -1;
            fieldZip = -1;
            fieldOpts = -1;
            fieldSlides = -1;
            fieldAudios = -1;
            fieldTranscripts = -1;
            fieldExtras = -1;
            fieldQuizzes = -1;
            fieldVideo = -1;
            fieldTime = -1;
            TotalFields = -1;
        }
        public static String getPatternFieldDelimiter() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField2, String.class); }
        public static String getFieldDelimiter() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField3, String.class); }
        public static Integer getFieldModuleNumber() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField4, Integer.class); }
        public static Integer getFieldTitle() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField5, Integer.class); }
        public static Integer getFieldDir() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField6, Integer.class); }
        public static Integer getFieldZip() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField7, Integer.class); }
        public static Integer getFieldOpts() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField8, Integer.class); }
        public static Integer getFieldSlides() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField9, Integer.class); }
        public static Integer getFieldAudios() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField10, Integer.class); }
        public static Integer getFieldTranscripts() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField11, Integer.class); }
        public static Integer getFieldExtras() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField13, Integer.class); }
        public static Integer getFieldQuizzes() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField14, Integer.class); }
        public static Integer getFieldVideo() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField15, Integer.class); }
        public static Integer getFieldTime() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField16, Integer.class); }
        public static Integer getTotalFields() throws Exception
            { return RefUtil.getPrivateField(Header.class, privateField17, Integer.class); }
    }

}
