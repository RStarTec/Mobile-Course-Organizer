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

import com.rstar.mobile.csc205sp2015.registered.api.HomeworkApi;

import java.io.File;

/**
 * Created by AHui on 1/15/15.
 */
public class HomeworkApiFields {
    public static class Constants extends ApiFields.Constants {

        private static final String privateField1 = "fieldTag";
        private static final String privateField2 = "fieldAccessCode";
        private static final String privateField3 = "fieldCourseId";
        private static final String privateField4 = "fieldSemester";
        private static final String privateField5 = "fieldSiteId";
        private static final String privateField6 = "fieldModule";
        private static final String privateField7 = "fieldFile";
        private static final String privateField8 = "numberOfFields";
        private static final String privateField9 = "defaultFilename";
        private static final String privateField10 = "tag_post";
        private static final String privateField11 = "tag_get";


        public String fieldTag = null;
        public String fieldAccessCode = null;
        public String fieldCourseId = null;
        public String fieldSemester = null;
        public String fieldSiteId = null; 
        public String fieldModule = null;
        public String fieldFile = null;
        public int numberOfFields = -1;
        public String defaultFilename = null;
        public String tag_post = null;
        public String tag_get = null;


        public Constants() throws Exception {
            super();
            this.get();
        }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            super.get();
            fieldTag = getFieldTag();
            fieldAccessCode = getFieldAccessCode();
            fieldCourseId = getFieldCourseId();
            fieldSemester = getFieldSemester();
            fieldSiteId = getFieldSiteId();
            fieldModule = getFieldModule();
            fieldFile = getFieldFile();
            numberOfFields = getNumberOfFields();
            defaultFilename = getDefaultFilename();
            tag_post = getTagPost();
            tag_get = getTagGet();
        }

        @Override
        public void detach() {
            super.detach();
            fieldTag = null;
            fieldAccessCode = null;
            fieldCourseId = null;
            fieldSemester = null;
            fieldSiteId = null;
            fieldModule = null;
            fieldFile = null;
            numberOfFields = -1;
            defaultFilename = null;
            tag_post = null;
            tag_get = null; 
        }

        public static String getFieldTag() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField1, String.class); }
        public static String getFieldAccessCode() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField2, String.class); }
        public static String getFieldCourseId() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField3, String.class); }
        public static String getFieldSemester() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField4, String.class); }
        public static String getFieldSiteId() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField5, String.class); }
        public static String getFieldModule() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField6, String.class); }
        public static String getFieldFile() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField7, String.class); }
        public static Integer getNumberOfFields() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField8, Integer.class); }
        public static String getDefaultFilename() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField9, String.class); }
        public static String getTagPost() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField10, String.class); }
        public static String getTagGet() throws Exception
            { return RefUtil.getPrivateField(HomeworkApi.class, privateField11, String.class); }
    }



    public static class Variables extends ApiFields.Variables {
        private static final String privateField1 = "accessCode";
        private static final String privateField2 = "moduleNumber";
        private static final String privateField3 = "uploadFile";
        private static final String privateField4 = "fileType";
        private static final String privateField5 = "data";


        public String accessCode = null;
        public int moduleNumber = -1;
        public File uploadFile = null;
        public String fileType = null;
        public String data = null;


        public Variables(HomeworkApi api) throws Exception {
            super(api);
            this.refresh(api);
        }

        public void refresh(HomeworkApi api) throws Exception {
            super.refresh(api);
            accessCode = getAccessCode(api);
            moduleNumber = getModuleNumber(api);
            uploadFile = getUploadFile(api);
            fileType = getFileType(api);
            data = getData(api);
        }

        @Override
        public void detach() {
            super.detach();
            accessCode = null;
            moduleNumber = -1;
            uploadFile = null;
            fileType = null;
            data = null;
        }

        public static String getAccessCode(HomeworkApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField1, String.class); }
        public static Integer getModuleNumber(HomeworkApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField2, Integer.class); }
        public static File getUploadFile(HomeworkApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField3, File.class); }
        public static String getFileType(HomeworkApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField4, String.class); }
        public static String getData(HomeworkApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField5, String.class); }
    }
}
