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

package com.rstar.mobile.csc205sp2015.utils;

import android.content.Context;

import com.rstar.mobile.csc205sp2015.fields.HomeworkFields;
import com.rstar.mobile.csc205sp2015.fields.QuestionListFields;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.registered.homework.Homework;

import java.io.File;

/**
 * Created by Ahui on 1/16/15.
 */
public class QuestionListUtils {
    public static int TestModuleNumber = 100;


    public static class QuestionListFileData {
        public int numberOfQuestions = 0;
        public boolean textAvailability[];
        public boolean pictureAvailability[];        
        public double points[];
        public String time;

        public QuestionListFileData(Context targetContext, File questionListFile) throws Exception {
            if (targetContext == null || questionListFile == null || !questionListFile.exists())
                throw new Exception("bad parameters");

            QuestionListFields.Constants constants = new QuestionListFields.Constants();
            try {
                String questionData = IO.loadFileAsString(targetContext, questionListFile);

                String[] record = questionData.split(constants.LineDelimiter);

                time = record[constants.RecordTime];

                numberOfQuestions = Integer.valueOf(record[constants.RecordCount]);
                textAvailability = new boolean[numberOfQuestions];
                pictureAvailability = new boolean[numberOfQuestions];
                points = new double[numberOfQuestions];

                if (numberOfQuestions+constants.RecordOffset!=record.length)
                    throw new Exception("Bad number of records");

                for (int entry = constants.RecordOffset; entry < constants.RecordOffset+numberOfQuestions; entry++) {
                    String[] field = record[entry].split(constants.FieldDelimiter);
                    if (field.length!=constants.NumberOfFields
                            || field[constants.FieldScore].length()==0
                            || field[constants.FieldText].length()==0
                            || field[constants.FieldPicture].length()==0)
                        throw new Exception("Bad fields for record.");
                    int index = entry - constants.RecordOffset;
                    textAvailability[index] = Boolean.valueOf(field[constants.FieldText]);
                    pictureAvailability[index] = Boolean.valueOf(field[constants.FieldPicture]);
                    points[index] = Double.valueOf(field[constants.FieldScore]);
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }

    public static class DataSet {
        public int questionListId;
        public File homeworkDir;
        public File questionListFile;

        public DataSet(Context targetContext, int set) throws Exception {
            homeworkDir = IO.getInternalDir(targetContext, Homework.getHomeworkDirname(TestModuleNumber));
            questionListFile = new File(homeworkDir, HomeworkFields.Constants.getQuestionListFilename());

            if (set==0) {
                // Empty one
                questionListId = com.rstar.mobile.csc205sp2015.test.R.raw.questionlist_empty;
            }
            else if (set==1) {
                // Good one
                questionListId = com.rstar.mobile.csc205sp2015.test.R.raw.questionlist_good1;
            }
            else if (set==2) {
                // Good one
                questionListId = com.rstar.mobile.csc205sp2015.test.R.raw.questionlist_good2;
            }
            else if (set==-1) {
                // Bad one
                questionListId = com.rstar.mobile.csc205sp2015.test.R.raw.questionlist_bad1;
            }
            else if (set==-2) {
                // Bad one
                questionListId = com.rstar.mobile.csc205sp2015.test.R.raw.questionlist_bad2;
            }
            else if (set==-3) {
                // Bad one
                questionListId = com.rstar.mobile.csc205sp2015.test.R.raw.questionlist_bad3;
            }
            else if (set==-4) {
                // Bad one
                questionListId = com.rstar.mobile.csc205sp2015.test.R.raw.questionlist_bad4;
            }
            else if (set==-5) {
                // Bad one
                questionListId = com.rstar.mobile.csc205sp2015.test.R.raw.questionlist_bad5;
            }
            else {
                homeworkDir = null;
                questionListFile = null;
            }
        }
    }

}
