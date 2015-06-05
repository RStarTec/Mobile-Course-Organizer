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

import com.rstar.mobile.csc205sp2015.registered.homework.QuestionList;

/**
 * Created by AHui on 1/21/15.
 */
public class QuestionListFields {
    public static class Constants extends Fields.Constants {
        private static final String privateField1 = "LineDelimiter";
        private static final String privateField2 = "FieldDelimiter";
        private static final String privateField3 = "RecordTime";
        private static final String privateField4 = "RecordCount";
        private static final String privateField5 = "RecordOffset";
        private static final String privateField6 = "FieldScore";
        private static final String privateField7 = "FieldText";
        private static final String privateField8 = "FieldPicture";
        private static final String privateField9 = "NumberOfFields";


        public String LineDelimiter = null;
        public String FieldDelimiter = null;
        public int RecordTime = -1;
        public int RecordCount = -1;
        public int RecordOffset = -1;
        public int FieldScore = -1;
        public int FieldText = -1;
        public int FieldPicture = -1;
        public int NumberOfFields = -1;

        public Constants() throws Exception { get(); }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            LineDelimiter = getLineDelimiter();
            FieldDelimiter = getFieldDelimiter();
            RecordTime = getRecordTime();
            RecordCount = getRecordCount();
            RecordOffset = getRecordOffset();
            FieldScore = getFieldScore();
            FieldText = getFieldText();
            FieldPicture = getFieldPicture();
            NumberOfFields = getNumberOfFields();
        }

        @Override
        public void detach() {
            LineDelimiter = null;
            FieldDelimiter = null;
            RecordTime = -1;
            RecordCount = -1;
            RecordOffset = -1;
            FieldScore = -1;
            FieldText = -1;
            FieldPicture = -1;
            NumberOfFields = -1;
        }

        public static String getLineDelimiter() throws Exception
            { return RefUtil.getPrivateField(QuestionList.class, privateField1, String.class); }
        public static String getFieldDelimiter() throws Exception
            { return RefUtil.getPrivateField(QuestionList.class, privateField2, String.class); }
        public static Integer getRecordTime() throws Exception
            { return RefUtil.getPrivateField(QuestionList.class, privateField3, Integer.class); }
        public static Integer getRecordCount() throws Exception
            { return RefUtil.getPrivateField(QuestionList.class, privateField4, Integer.class); }
        public static Integer getRecordOffset() throws Exception
            { return RefUtil.getPrivateField(QuestionList.class, privateField5, Integer.class); }
        public static Integer getFieldScore() throws Exception
            { return RefUtil.getPrivateField(QuestionList.class, privateField6, Integer.class); }
        public static Integer getFieldText() throws Exception
            { return RefUtil.getPrivateField(QuestionList.class, privateField7, Integer.class); }
        public static Integer getFieldPicture() throws Exception
            { return RefUtil.getPrivateField(QuestionList.class, privateField8, Integer.class); }
        public static Integer getNumberOfFields() throws Exception
        { return RefUtil.getPrivateField(QuestionList.class, privateField9, Integer.class); }
    }

    public static class Variables extends Fields.Variables<QuestionList> {
        private static final String privateField1 = "textAvailability";
        private static final String privateField2 = "pictureAvailability";
        private static final String privateField3 = "points";
        private static final String privateField4 = "numberOfQuestions";
        private static final String privateField5 = "time";


        public boolean textAvailability[] = null;
        public boolean pictureAvailability[] = null;
        public double points[] = null;
        public int numberOfQuestions = -1;
        public String time = null;


        public Variables(QuestionList questionList) throws Exception {
            refresh(questionList);
        }

        @Override
        public void refresh(QuestionList questionList) throws Exception {
            textAvailability = getTextAvailability(questionList);
            pictureAvailability = getPictureAvailability(questionList);
            points = getPoints(questionList);
            numberOfQuestions = getNumberOfQuestions(questionList);
            time = getTime(questionList);
        }

        @Override
        public void detach() {
            textAvailability = null;
            pictureAvailability = null;
            points = null;
            numberOfQuestions = -1;
            time = null;
        }

        public static boolean[] getTextAvailability(QuestionList questionList) throws Exception
            { return RefUtil.getPrivateField(questionList, privateField1, boolean[].class); }
        public static boolean[] getPictureAvailability(QuestionList questionList) throws Exception
            { return RefUtil.getPrivateField(questionList, privateField2, boolean[].class); }
        public static double[] getPoints(QuestionList questionList) throws Exception
            { return RefUtil.getPrivateField(questionList, privateField3, double[].class); }
        public static Integer getNumberOfQuestions(QuestionList questionList) throws Exception
            { return RefUtil.getPrivateField(questionList, privateField4, Integer.class); }
        public static String getTime(QuestionList questionList) throws Exception
            { return RefUtil.getPrivateField(questionList, privateField5, String.class); }
    }
}
