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

import com.rstar.mobile.csc205sp2015.registered.api.Api;

import org.apache.http.Header;

/**
 * Created by AHui on 1/15/15.
 */
public class ApiFields {
    public static class Constants extends Fields.Constants {

        private static final String privateField1 = "contentType";
        private static final String privateField2 = "defaultStatus";
        private static final String privateField3 = "headerStatus";
        private static final String privateField4 = "status_ok";
        private static final String privateField5 = "apiDirname";
        private static final String privateField6 = "dir";


        public String contentType = null;
        public String defaultStatus = null;
        public String headerStatus = null;
        public String status_ok = null;
        public String apiDirname = null;
        public String dir = null;

        public Constants() throws Exception { get(); }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            contentType = getContentType();
            defaultStatus = getDefaultStatus();
            headerStatus = getHeaderStatus();
            status_ok = getStatusOk();
            apiDirname = getApiDirname();
            dir = getDir();
        }
        @Override
        public void detach() {
            contentType = null;
            defaultStatus = null;
            headerStatus = null;
            status_ok = null;
            apiDirname = null;
            dir = null;
        }

        public static String getContentType() throws Exception
            { return RefUtil.getPrivateField(Api.class, privateField1, String.class); }
        public static String getDefaultStatus() throws Exception
            { return RefUtil.getPrivateField(Api.class, privateField2, String.class); }
        public static String getHeaderStatus() throws Exception
            { return RefUtil.getPrivateField(Api.class, privateField3, String.class); }
        public static String getStatusOk() throws Exception
            { return RefUtil.getPrivateField(Api.class, privateField4, String.class); }
        public static String getApiDirname() throws Exception
            { return RefUtil.getPrivateField(Api.class, privateField5, String.class); }
        public static String getDir() throws Exception
            { return RefUtil.getPrivateField(Api.class, privateField6, String.class); }
    }

    public static class Variables extends Fields.Variables<Api> {
        private static final String privateField1 = "dataFilename";
        private static final String privateField2 = "communication";
        private static final String privateField3 = "responseHeader";

        public String dataFilename = null;
        public String communication = null;
        public Header responseHeader[] = null;

        public Variables(Api api) throws Exception {
            refresh(api);
        }

        @Override
        public void refresh(Api api) throws Exception {
            dataFilename = getDataFilename(api);
            communication = getCommunication(api);
            responseHeader = getResponseHeader(api);
        }
        @Override
        public void detach() {
            dataFilename = null;
            communication = null;
            responseHeader = null;
        }

        public static String getDataFilename(Api api) throws Exception
            { return RefUtil.getPrivateField(api, privateField1, String.class); }
        public static String getCommunication(Api api) throws Exception
            { return RefUtil.getPrivateField(api, privateField2, String.class); }
        public static Header[] getResponseHeader(Api api) throws Exception
            { return RefUtil.getPrivateField(api, privateField3, Header[].class); }
    }

    public static class StaticVars extends Fields.StaticVars {
        private static final String privateField1 = "courseId";
        private static final String privateField2 = "semester";
        private static final String privateField3 = "siteId";

        public String courseId = null;
        public String semester = null;
        public String siteId = null;

        @Override
        public void refresh() throws Exception {
            courseId = getCourseId();
            semester = getSemester();
            siteId = getSiteId();
        }

        @Override
        public void detach() {
            courseId = null;
            semester = null;
            siteId = null;
        }

        public static String getCourseId() throws Exception
            { return RefUtil.getPrivateField(Api.class, privateField1, String.class); }
        public static String getSemester() throws Exception
            { return RefUtil.getPrivateField(Api.class, privateField2, String.class); }
        public static String getSiteId() throws Exception
            { return RefUtil.getPrivateField(Api.class, privateField3, String.class); }
    }
}
