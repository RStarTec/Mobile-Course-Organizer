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

import com.rstar.mobile.csc205sp2015.registered.api.ScoreApi;

/**
 * Created by AHui on 1/15/15.
 */
public class ScoreApiFields {
    public static class Constants extends ApiFields.Constants {

        private static final String privateField1 = "fieldTag";
        private static final String privateField2 = "fieldUserId";
        private static final String privateField3 = "fieldPassword";
        private static final String privateField4 = "fieldAccessCode";        
        private static final String privateField5 = "fieldCourseId";
        private static final String privateField6 = "fieldSemester";
        private static final String privateField7 = "fieldSiteId";
        private static final String privateField8 = "fieldModule";
        private static final String privateField9 = "numberOfFields";
        private static final String privateField10 = "defaultScoreFilename";
        private static final String privateField11 = "defaultFeedbackFilename";
        private static final String privateField12 = "defaultSubmittedFilename";
        private static final String privateField13 = "defaultExtension";
        private static final String privateField14 = "defaultScore";
        private static final String privateField15 = "tag_score";
        private static final String privateField16 = "tag_feedback";
        private static final String privateField17 = "tag_submitted";
        private static final String privateField18 = "tag_default";


        public String fieldTag = null;
        public String fieldUserId = null;
        public String fieldPassword = null;
        public String fieldAccessCode = null;
        public String fieldCourseId = null;
        public String fieldSemester = null;
        public String fieldSiteId = null;
        public String fieldModule = null;
        public int numberOfFields = -1;
        public String defaultScoreFilename = null;
        public String defaultFeedbackFilename = null;
        public String defaultSubmittedFilename = null;
        public String defaultExtension = null;
        public double defaultScore = -1;
        public String tag_score = null;
        public String tag_feedback = null;
        public String tag_submitted = null;
        public String tag_default = null;


        public Constants() throws Exception {
            super();
            this.get();
        }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            super.get();
            fieldTag = getFieldTag();
            fieldUserId = getFieldUserId();
            fieldPassword = getFieldPassword();
            fieldAccessCode = getFieldAccessCode();
            fieldCourseId = getFieldCourseId();
            fieldSemester = getFieldSemester();
            fieldSiteId = getFieldSiteId();
            fieldModule = getFieldModule();
            numberOfFields = getNumberOfFields();
            defaultScoreFilename = getDefaultScoreFilename();
            defaultFeedbackFilename = getDefaultFeedbackFilename();
            defaultSubmittedFilename = getDefaultSubmittedFilename();
            defaultExtension = getDefaultExtension();
            defaultScore = getDefaultScore();
            tag_score = getTagScore();
            tag_feedback = getTagFeedback();
            tag_submitted = getTagSubmitted();
            tag_default = getTagDefault();
        }

        @Override
        public void detach() {
            super.detach();
            fieldTag = null;
            fieldUserId = null;
            fieldPassword = null;
            fieldAccessCode = null;
            fieldCourseId = null;
            fieldSemester = null;
            fieldSiteId = null;
            fieldModule = null;
            numberOfFields = -1;
            defaultScoreFilename = null;
            defaultFeedbackFilename = null;
            defaultSubmittedFilename = null;
            defaultExtension = null;
            defaultScore = -1;
            tag_score = null;
            tag_feedback = null;
            tag_submitted = null;
            tag_default = null;
        }

        public static String getFieldTag() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField1, String.class); }
        public static String getFieldUserId() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField2, String.class); }
        public static String getFieldPassword() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField3, String.class); }
        public static String getFieldAccessCode() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField4, String.class); }
        public static String getFieldCourseId() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField5, String.class); }
        public static String getFieldSemester() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField6, String.class); }
        public static String getFieldSiteId() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField7, String.class); }
        public static String getFieldModule() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField8, String.class); }
        public static Integer getNumberOfFields() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField9, Integer.class); }
        public static String getDefaultScoreFilename() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField10, String.class); }
        public static String getDefaultFeedbackFilename() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField11, String.class); }
        public static String getDefaultSubmittedFilename() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField12, String.class); }
        public static String getDefaultExtension() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField13, String.class); }
        public static Double getDefaultScore() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField14, Double.class); }
        public static String getTagScore() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField15, String.class); }
        public static String getTagFeedback() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField16, String.class); }
        public static String getTagSubmitted() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField17, String.class); }
        public static String getTagDefault() throws Exception
            { return RefUtil.getPrivateField(ScoreApi.class, privateField18, String.class); }
    }



    public static class Variables extends ApiFields.Variables {
        private static final String privateField1 = "userId";
        private static final String privateField2 = "password";
        private static final String privateField3 = "accessCode";
        private static final String privateField4 = "moduleNumber";
        private static final String privateField5 = "postTag";
        private static final String privateField6 = "data";


        public String userId = null;
        public String password = null;
        public String accessCode = null;
        public int moduleNumber = -1;
        public String postTag = null;
        public String data = null;

        public Variables(ScoreApi api) throws Exception {
            super(api);
            this.refresh(api);
        }

        public void refresh(ScoreApi api) throws Exception {
            super.refresh(api);
            userId = getUserId(api);
            password = getPassword(api);
            accessCode = getAccessCode(api);
            moduleNumber = getModuleNumber(api);
            postTag = getPostTag(api);
            data = getData(api);
        }

        @Override
        public void detach() {
            super.detach();
            userId = null;
            password = null;
            accessCode = null;
            moduleNumber = -1;
            postTag = null;
            data = null;
        }

        public static String getUserId(ScoreApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField1, String.class); }
        public static String getPassword(ScoreApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField2, String.class); }
        public static String getAccessCode(ScoreApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField3, String.class); }
        public static Integer getModuleNumber(ScoreApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField4, Integer.class); }
        public static String getPostTag(ScoreApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField5, String.class); }
        public static String getData(ScoreApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField6, String.class); }
    }
}
