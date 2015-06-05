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

import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;

import java.lang.reflect.Field;

/**
 * Created by AHui on 1/15/15.
 */
public class PrivateSiteFields {
    public static class Constants extends Fields.Constants {

        private static final String privateField1 = "SiteFilename";
        private static final String privateField2 = "SiteId";
        private static final String privateField3 = "LoginApi";
        private static final String privateField4 = "HomeworkApi";
        private static final String privateField5 = "ScoreApi";
        private static final String privateField6 = "MasterApi";


        public String SiteFilename = null;
        public String SiteId = null;
        public String LoginApi = null;
        public String HomeworkApi = null;
        public String ScoreApi = null;
        public String MasterApi = null;


        public Constants() throws Exception { get(); }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            SiteFilename = getSiteFilename();
            SiteId = getSiteId();
            LoginApi = getLoginApi();
            HomeworkApi = getHomeworkApi();
            ScoreApi = getScoreApi();
            MasterApi = getMasterApi();
        }
        @Override
        public void detach() {
            SiteFilename = null;
            SiteId = null;
            LoginApi = null;
            HomeworkApi = null;
            ScoreApi = null;
            MasterApi = null;
        }


        public static String getSiteFilename() throws Exception
            { return RefUtil.getPrivateField(PrivateSite.class, privateField1, String.class); }
        public static String getSiteId() throws Exception
            { return RefUtil.getPrivateField(PrivateSite.class, privateField2, String.class); }
        public static String getLoginApi() throws Exception
            { return RefUtil.getPrivateField(PrivateSite.class, privateField3, String.class); }
        public static String getHomeworkApi() throws Exception
            { return RefUtil.getPrivateField(PrivateSite.class, privateField4, String.class); }
        public static String getScoreApi() throws Exception
            { return RefUtil.getPrivateField(PrivateSite.class, privateField5, String.class); }
        public static String getMasterApi() throws Exception
            { return RefUtil.getPrivateField(PrivateSite.class, privateField6, String.class); }
    }

    public static class Variables extends Fields.Variables<PrivateSite> {
        private static final String privateField1 = "publicLink";
        private static final String privateField2 = "privateLink";

        public String publicLink = null;
        public String privateLink = null;

        public Variables(PrivateSite privateSite) throws Exception {
            refresh(privateSite);
        }

        @Override
        public void refresh(PrivateSite privateSite) throws Exception {
            publicLink = getPublicLink(privateSite);
            privateLink = getPrivateLink(privateSite);
        }
        @Override
        public void detach() {
            publicLink = null;
            privateLink = null;
        }

        public static String getPublicLink(PrivateSite privateSite) throws Exception
            { return RefUtil.getPrivateField(privateSite, privateField1, String.class); }
        public static String getPrivateLink(PrivateSite privateSite) throws Exception
            { return RefUtil.getPrivateField(privateSite, privateField2, String.class); }


        // Allow to change the value of the link variable
        public static void setPublicLink(PrivateSite privateSite, String publicLink) throws Exception {
            Field field = PrivateSite.class.getDeclaredField(privateField1);
            field.setAccessible(true);
            field.set(privateSite, publicLink);
        }

    }

}
