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


import com.rstar.mobile.csc205sp2015.registered.homework.Homework;
import com.rstar.mobile.csc205sp2015.registered.homework.QuestionList;

/**
 * Created by AHui on 1/15/15.
 */
public class HomeworkFields {
    public static class Constants extends Fields.Constants {
        private static final String privateField1 = "HomeworkExtension";
        private static final String privateField2 = "TextExtension";
        private static final String privateField3 = "PictureExtension";
        private static final String privateField4 = "QuestionListFilename";
        private static final String privateField5 = "dir";
        private static final String privateField6 = "TimeFilename";


        public String HomeworkExtension = null;
        public String TextExtension = null;
        public String PictureExtension = null;
        public String QuestionListFilename = null;
        public String dir = "/";
        public String TimeFilename = null;


        public Constants() throws Exception {
            get();
        }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            HomeworkExtension = getHomeworkExtension();
            TextExtension = getTextExtension();
            PictureExtension = getPictureExtension();
            QuestionListFilename = getQuestionListFilename();
            dir = getDir();
            TimeFilename = getTimeFilename();
        }

        @Override
        public void detach() {
            HomeworkExtension = null;
            TextExtension = null;
            PictureExtension = null;
            QuestionListFilename = null;
            dir = null;
            TimeFilename = null;
        }


        public static String getHomeworkExtension() throws Exception {
            return RefUtil.getPrivateField(Homework.class, privateField1, String.class);
        }
        public static String getTextExtension() throws Exception {
            return RefUtil.getPrivateField(Homework.class, privateField2, String.class);
        }
        public static String getPictureExtension() throws Exception {
            return RefUtil.getPrivateField(Homework.class, privateField3, String.class);
        }
        public static String getQuestionListFilename() throws Exception {
            return RefUtil.getPrivateField(Homework.class, privateField4, String.class);
        }
        public static String getDir() throws Exception {
            return RefUtil.getPrivateField(Homework.class, privateField5, String.class);
        }
        public static String getTimeFilename() throws Exception {
            return RefUtil.getPrivateField(Homework.class, privateField6, String.class);
        }
    }


    public static class Variables extends Fields.Variables<Homework> {
        private static final String privateField1 = "moduleNumber";
        private static final String privateField2 = "link";
        private static final String privateField3 = "questionList";

        public int moduleNumber = -1;
        public String link = "";
        public QuestionList questionList = null;

        public Variables(Homework homework) throws Exception {
            refresh(homework);
        }

        @Override
        public void refresh(Homework homework) throws Exception {
            moduleNumber = getModuleNumber(homework);
            link = getLink(homework);
            questionList = getQuestionList(homework);
        }
        @Override
        public void detach() {
            moduleNumber = -1;
            link = null;
            questionList = null;
        }

        public static Integer getModuleNumber(Homework homework) throws Exception
            { return RefUtil.getPrivateField(homework, privateField1, Integer.class); }
        public static String getLink(Homework homework) throws Exception
            { return RefUtil.getPrivateField(homework, privateField2, String.class); }
        public static QuestionList getQuestionList(Homework homework) throws Exception
            { return RefUtil.getPrivateField(homework, privateField3, QuestionList.class); }
    }

}
