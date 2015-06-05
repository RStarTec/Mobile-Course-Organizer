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


import android.content.Context;

import com.rstar.mobile.csc205sp2015.registered.login.Student;

import java.lang.reflect.Method;

/**
 * Created by AHui on 1/15/15.
 */
public class StudentFields {
    public static class Constants extends Fields.Constants {

        private static final String privateField1 = "PREF_accessCode";
        private static final String privateField2 = "PREF_loginKey";
        private static final String privateField3 = "DefaultAccessCode";
        private static final String privateField4 = "LoginCacheFilename";
        private static final String privateField5 = "dir";
        private static final String privateField6 = "DefaultLoginCacheTimeLimit";
        private static final String privateField7 = "ALGO";
        private static final String privateField8 = "RandomSeed";


        public String PREF_accessCode = null;
        public String PREF_loginKey = null;
        public String DefaultAccessCode = null;
        public String LoginCacheFilename = null;
        public String dir = null;
        public long DefaultLoginCacheTimeLimit = -1;
        public String ALGO = null;
        public String RandomSeed = null;


        public Constants() throws Exception {
            get();
        }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            PREF_accessCode = getPrefAccessCode();
            PREF_loginKey = getPrefLoginKey();
            DefaultAccessCode = getDefaultAccessCode();
            LoginCacheFilename = getLoginCacheFilename();
            dir = getDir();
            DefaultLoginCacheTimeLimit = getDefaultLoginCacheTimeLimit();
            ALGO = getAlgo();
            RandomSeed = getRandomSeed();
        }

        @Override
        public void detach() {
            PREF_accessCode = null;
            PREF_loginKey = null;
            DefaultAccessCode = null;
            LoginCacheFilename = null;
            dir = null;
            DefaultLoginCacheTimeLimit = -1;
            ALGO = null;
            RandomSeed = null;
        }


        public static String getPrefAccessCode() throws Exception {
            return RefUtil.getPrivateField(Student.class, privateField1, String.class);
        }
        public static String getPrefLoginKey() throws Exception {
            return RefUtil.getPrivateField(Student.class, privateField2, String.class);
        }
        public static String getDefaultAccessCode() throws Exception {
            return RefUtil.getPrivateField(Student.class, privateField3, String.class);
        }
        public static String getLoginCacheFilename() throws Exception {
            return RefUtil.getPrivateField(Student.class, privateField4, String.class);
        }
        public static String getDir() throws Exception {
            return RefUtil.getPrivateField(Student.class, privateField5, String.class);
        }
        public static Long getDefaultLoginCacheTimeLimit() throws Exception {
            return RefUtil.getPrivateField(Student.class, privateField6, Long.class);
        }
        public static String getAlgo() throws Exception {
            return RefUtil.getPrivateField(Student.class, privateField7, String.class);
        }
        public static String getRandomSeed() throws Exception {
            return RefUtil.getPrivateField(Student.class, privateField8, String.class);
        }
    }


    public static class Variables extends Fields.Variables<Student> {
        private static final String privateField1 = "accessCode";

        private String accessCode = null;

        public Variables(Student student) throws Exception {
            refresh(student);
        }

        @Override
        public void refresh(Student student) throws Exception {
            accessCode = getAccessCode(student);
        }
        @Override
        public void detach() {
            accessCode = null;
        }

        public static String getAccessCode(Student student) throws Exception
            { return RefUtil.getPrivateField(student, privateField1, String.class); }
    }


    public static class StaticVars extends Fields.StaticVars {
        private static final String privateField1 = "LoginCacheTimeLimit";

        public long LoginCacheTimeLimit = -1;

        public StaticVars() throws Exception {
            refresh();
        }

        @Override
        public void refresh() throws Exception {
            LoginCacheTimeLimit = getLoginCacheTimeLimit();
        }

        @Override
        public void detach() {
            LoginCacheTimeLimit = -1;
        }

        public static Long getLoginCacheTimeLimit() throws Exception {
            return RefUtil.getPrivateField(Student.class, privateField1, Long.class);
        }
    }


    public static void saveLogin(Student student, Context context, String userId, String password) throws Exception {
        String methodName = "saveLogin";
        Class<?> parameterTypes[] = new Class[3];
        parameterTypes[0] = Context.class;
        parameterTypes[1] = String.class;
        parameterTypes[2] = String.class;
        Object parameters[] = new Object[3];
        parameters[0] = context;
        parameters[1] = userId;
        parameters[2] = password;
        Method method = Student.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        // no return value
        method.invoke(student, parameters);

    }
}
