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

import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.module.Module;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by AHui on 1/15/15.
 */
public class CourseFields {
    public static class Constants extends Fields.Constants {

        private static final String privateField1 = "CourseFilename";
        private static final String privateField2 = "LineDelimiter";
        private static final String privateField3 = "DefaultLink";

        public String CourseFilename = null;
        public String LineDelimiter = null;
        public String DefaultLink = null;

        public Constants() throws Exception { get(); }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            CourseFilename = getCourseFilename();
            LineDelimiter = getLineDelimiter();
            DefaultLink = getDefaultLink();
        }
        @Override
        public void detach() {
            CourseFilename = null;
            LineDelimiter = null;
            DefaultLink = null;
        }


        public static String getCourseFilename() throws Exception
            { return RefUtil.getPrivateField(Course.class, privateField1, String.class); }
        public static String getLineDelimiter() throws Exception
            { return RefUtil.getPrivateField(Course.class, privateField2, String.class); }
        public static String getDefaultLink() throws Exception
        { return RefUtil.getPrivateField(Course.class, privateField3, String.class); }
    }

    public static class Variables extends Fields.Variables<Course> {
        private static final String privateField1 = "moduleList";
        private static final String privateField2 = "moduleMap";
        private static final String privateField3 = "serverPasscode";
        private static final String privateField4 = "link";

        public List<Module> moduleList = null;
        public Map<String, Module> moduleMap = null;
        public String serverPasscode = null;
        public String link = null;

        public Variables(Course course) throws Exception {
            refresh(course);
        }

        @Override
        public void refresh(Course course) throws Exception {
            moduleList = getModuleList(course);
            moduleMap = getModuleMap(course);
            serverPasscode = getServerPasscode(course);
            link = getLink(course);
        }
        @Override
        public void detach() {
            moduleList = null;
            moduleMap = null;
            serverPasscode = null;
            link = null;
        }

        public static List<Module> getModuleList(Course course) throws Exception
            { return RefUtil.getPrivateField(course, privateField1, List.class); }
        public static Map<String, Module> getModuleMap(Course course) throws Exception
            { return RefUtil.getPrivateField(course, privateField2, Map.class); }
        public static String getServerPasscode(Course course) throws Exception
            { return RefUtil.getPrivateField(course, privateField3, String.class); }
        public static String getLink(Course course) throws Exception
        { return RefUtil.getPrivateField(course, privateField4, String.class); }

        // Allow to change the value of the link variable
        public static void setLink(Course course, String link) throws Exception {
            Field field = Course.class.getDeclaredField(privateField4);
            field.setAccessible(true);
            field.set(course, link);
        }

    }

}
