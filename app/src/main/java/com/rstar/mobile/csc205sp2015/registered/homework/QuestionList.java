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

package com.rstar.mobile.csc205sp2015.registered.homework;

import android.content.Context;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.io.File;

/**
 * Created by AHui
 */
public class QuestionList {
    private static final String TAG = QuestionList.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final String LineDelimiter = "\\n";
    private static final String FieldDelimiter = "\\t";
    private static final int RecordTime = 0;
    private static final int RecordCount = 1;
    private static final int RecordOffset = 2;
    private static final int FieldScore = 0;
    private static final int FieldText = 1;
    private static final int FieldPicture = 2;
    private static final int NumberOfFields = 3;
    public static final String NoDate = "";

    private boolean textAvailability[] = null;
    private boolean pictureAvailability[] = null;
    private double points[] = null;
    private int numberOfQuestions = 0;
    private String time = NoDate;

    // Do not accept any bad questionList because a homework is not defined if the questionList is corrupted
    public QuestionList(Context context, File questionListFile) throws Exception {
        Savelog.d(TAG, debug, "Getting question list now");
        if (context==null || questionListFile==null || !questionListFile.exists()) throw new Exception("Bad parameters");

        // Force an exception to occur when the questionData is empty or corrupted
        try {
            String questionData = IO.loadFileAsString(context, questionListFile);

            String[] record = questionData.split(LineDelimiter);

            time = record[RecordTime];
            numberOfQuestions = Integer.valueOf(record[RecordCount]);
            textAvailability = new boolean[numberOfQuestions];
            pictureAvailability = new boolean[numberOfQuestions];
            points = new double[numberOfQuestions];

            for (int index=0; index<numberOfQuestions; index++) {
                textAvailability[index]=false;
                pictureAvailability[index]=false;
                points[index]=0;
            }
            if (numberOfQuestions+RecordOffset!=record.length)
                throw new Exception("Bad number of records");

            for (int entry= RecordOffset; entry<RecordOffset+numberOfQuestions; entry++) {
                String[] field = record[entry].split(FieldDelimiter);
                if (field.length!=NumberOfFields
                    || field[FieldScore].length()==0
                    || field[FieldText].length()==0
                    || field[FieldPicture].length()==0)
                    throw new Exception("Bad fields for record.");
                int index = entry - RecordOffset;
                int questionNumber = entry;
                points[index] = Double.valueOf(field[FieldScore]);
                textAvailability[index] = Boolean.valueOf(field[FieldText]);
                pictureAvailability[index] = Boolean.valueOf(field[FieldPicture]);
                Savelog.d(TAG, debug, "Question " + questionNumber + " text=" + textAvailability[index] + " pic=" + pictureAvailability[index]);
            } // else: file is empty. No question available

            Savelog.d(TAG, debug, "Total questions listed=" + (record.length- RecordOffset));

        } catch (Exception e) {
            Savelog.w(TAG, "Cannot load questions from " + questionListFile.getAbsolutePath(), e);
            throw e;
        }
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public boolean isTextAvailable(int questionNumber) {
        if (questionNumber<=0 || questionNumber>numberOfQuestions) return false;
        return textAvailability[questionNumber-1];
    }

    public boolean isPictureAvailable(int questionNumber) {
        if (questionNumber<=0 || questionNumber>numberOfQuestions) return false;
        return pictureAvailability[questionNumber-1];
    }

    public double getPoints(int questionNumber) {
        if (questionNumber<=0 || questionNumber>numberOfQuestions) return 0;
        return points[questionNumber-1];
    }
    public String getTime() { return time; }

}
