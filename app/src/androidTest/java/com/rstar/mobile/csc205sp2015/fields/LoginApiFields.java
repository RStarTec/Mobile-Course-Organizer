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

import com.rstar.mobile.csc205sp2015.registered.api.LoginApi;

/**
 * Created by AHui on 1/15/15.
 */
public class LoginApiFields {
    public static class Constants extends ApiFields.Constants {

        private static final String privateField1 = "fieldTag";
        private static final String privateField2 = "fieldUserId";
        private static final String privateField3 = "fieldPassword";
        private static final String privateField4 = "fieldEmail";
        private static final String privateField5 = "fieldNewPassword";
        private static final String privateField6 = "fieldCourseId";
        private static final String privateField7 = "fieldSemester";
        private static final String privateField8 = "fieldSiteId";
        private static final String privateField9 = "numberOfFields";
        private static final String privateField10 = "defaultLoginFilename";
        private static final String privateField11 = "defaultAccessCodeFilename";
        private static final String privateField12 = "tag_signup";
        private static final String privateField13 = "tag_signin";
        private static final String privateField14 = "tag_reset";
        private static final String privateField15 = "tag_passwd";
        private static final String privateField16 = "tag_default";

        public String fieldTag = null;
        public String fieldUserId = null;
        public String fieldPassword = null;
        public String fieldEmail = null;
        public String fieldNewPassword = null;
        public String fieldCourseId = null;
        public String fieldSemester = null;
        public String fieldSiteId = null;
        public int numberOfFields = -1;
        public String defaultLoginFilename = null;
        public String defaultAccessCodeFilename = null;
        public String tag_signup = null;
        public String tag_signin = null;
        public String tag_reset = null;
        public String tag_passwd = null;
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
            fieldEmail = getFieldEmail();
            fieldNewPassword = getFieldNewPassword();
            fieldCourseId = getFieldCourseId();
            fieldSemester = getFieldSemester();
            fieldSiteId = getFieldSiteId();
            numberOfFields = getNumberOfFields();
            defaultLoginFilename = getDefaultLoginFilename();
            defaultAccessCodeFilename = getDefaultAccessCodeFilename();
            tag_signup = getTagSignup();
            tag_signin = getTagSignin();
            tag_reset = getTagReset();
            tag_passwd = getTagPasswd();
            tag_default = getTagDefault();
        }

        @Override
        public void detach() {
            super.detach();
            fieldTag = null;
            fieldUserId = null;
            fieldPassword = null;
            fieldEmail = null;
            fieldNewPassword = null;
            fieldCourseId = null;
            fieldSemester = null;
            fieldSiteId = null;
            numberOfFields = -1;
            defaultLoginFilename = null;
            defaultAccessCodeFilename = null;
            tag_signup = null;
            tag_signin = null;
            tag_reset = null;
            tag_passwd = null;
            tag_default = null;
        }

        public static String getFieldTag() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField1, String.class); }
        public static String getFieldUserId() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField2, String.class); }
        public static String getFieldPassword() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField3, String.class); }
        public static String getFieldEmail() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField4, String.class); }
        public static String getFieldNewPassword() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField5, String.class); }
        public static String getFieldCourseId() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField6, String.class); }
        public static String getFieldSemester() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField7, String.class); }
        public static String getFieldSiteId() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField8, String.class); }
        public static Integer getNumberOfFields() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField9, Integer.class); }
        public static String getDefaultLoginFilename() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField10, String.class); }
        public static String getDefaultAccessCodeFilename() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField11, String.class); }
        public static String getTagSignup() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField12, String.class); }
        public static String getTagSignin() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField13, String.class); }
        public static String getTagReset() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField14, String.class); }
        public static String getTagPasswd() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField15, String.class); }
        public static String getTagDefault() throws Exception
            { return RefUtil.getPrivateField(LoginApi.class, privateField16, String.class); }
    }



    public static class Variables extends ApiFields.Variables {
        private static final String privateField1 = "userId";
        private static final String privateField2 = "password";
        private static final String privateField3 = "email";
        private static final String privateField4 = "newPassword";
        private static final String privateField5 = "postTag";
        private static final String privateField6 = "data";

        public String userId = null;
        public String password = null;
        public String email = null;
        public String newPassword = null;
        public String postTag = null;
        public String data = null;

        public Variables(LoginApi api) throws Exception {
            super(api);
            this.refresh(api);
        }

        public void refresh(LoginApi api) throws Exception {
            super.refresh(api);
            userId = getUserId(api);
            password = getPassword(api);
            email = getEmail(api);
            newPassword = getNewPassword(api);
            postTag = getPostTag(api);
            data = getData(api);
        }

        @Override
        public void detach() {
            super.detach();
            userId = null;
            password = null;
            email = null;
            newPassword = null;
            postTag = null;
            data = null;
        }

        public static String getUserId(LoginApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField1, String.class); }
        public static String getPassword(LoginApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField2, String.class); }
        public static String getEmail(LoginApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField3, String.class); }
        public static String getNewPassword(LoginApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField4, String.class); }
        public static String getPostTag(LoginApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField5, String.class); }
        public static String getData(LoginApi api) throws Exception
            { return RefUtil.getPrivateField(api, privateField6, String.class); }
    }
}
