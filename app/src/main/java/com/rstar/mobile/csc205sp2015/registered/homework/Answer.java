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
import com.rstar.mobile.csc205sp2015.registered.api.HomeworkApi;
import com.rstar.mobile.csc205sp2015.registered.api.LoginApi;
import com.rstar.mobile.csc205sp2015.registered.login.Student;

import java.io.File;

/**
 * Created by AHui
 */
public class Answer {
    private static final String TAG = Answer.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final String StudentLabel = Student.StudentLabel;
    private static final String HomeworkLabel = Homework.HomeworkLabel;
    private static final String SubmitTextFileType = "TEXT/PLAIN";

    public static final String AnswerLabel = "A";
    private static final String TextExtension = ".txt";
    private static final String PictureExtension = ".jpg";




    // ATTENTION: make sure that the internal directory for the student's work is separate from the directory that contains publicly available materials.

    public static final int DefaultModuleNumber = 0;
    public static final int DefaultNumberOfQuestions = 0;

    private String studentAccessCode = LoginApi.DefaultAccessCode;
    private int moduleNumber = DefaultModuleNumber;
    private int numberOfQuestions = DefaultNumberOfQuestions;


    private boolean completed = false;
    private long lastUpdateDate = 0; // Date of the last update or upload. 0 when homework is not on device.

    public Answer(Context context, String studentAccessCode, int moduleNumber, int numberOfQuestions) {
        this.studentAccessCode = studentAccessCode;
        this.moduleNumber = moduleNumber;
        this.numberOfQuestions = numberOfQuestions;

        File folder = getStudentDir(context);
        if (!folder.exists()) {
            folder.mkdir();
        }
        Savelog.d(TAG, debug, "Created StudentAnswer for " + studentAccessCode + ", HW" + moduleNumber + " with " + numberOfQuestions + " qns");
    }

    public int getModuleNumber() {
        return this.moduleNumber;
    }

    // Use the directory defined in the Student class
    private File getStudentDir(Context context) {
        return Student.getStudentDir(context);
    }

    public boolean isTextAvailable(Context context, int questionNumber) {
        File file = getTextFile(context, questionNumber);
        return file!=null && file.exists();
    }

    public boolean isPictureAvailable(Context context, int questionNumber) {
        File file = getPictureFile(context, questionNumber);
        return file!=null && file.exists();
    }

    public File getTextFile(Context context, int questionNumber) {
        if (questionNumber<=0 || questionNumber>numberOfQuestions) return null;
        return new File(getStudentDir(context), getTextName(studentAccessCode, moduleNumber, questionNumber));
    }

    public File getPictureFile(Context context, int questionNumber) {
        if (questionNumber<=0 || questionNumber>numberOfQuestions) return null;
        return new File(getStudentDir(context), getPictureName(studentAccessCode, moduleNumber, questionNumber));
    }



    public static String getPictureName(String studentAccessCode, int moduleNumber, int questionNumber) {
        return StudentLabel + studentAccessCode + HomeworkLabel + moduleNumber +  AnswerLabel + questionNumber + PictureExtension;
    }

    public static String getTextName(String studentAccessCode, int moduleNumber, int questionNumber) {
        return StudentLabel + studentAccessCode + HomeworkLabel + moduleNumber +  AnswerLabel + questionNumber + TextExtension;
    }

    public File getSubmitTextFile(Context context) {
        String filename = StudentLabel + studentAccessCode + HomeworkLabel + moduleNumber + TextExtension;
        return new File(getStudentDir(context), filename);
    }

    public String prepareSubmitText(Context context) {
        String data = "";
        for (int questionNumber=1; questionNumber<=numberOfQuestions; questionNumber++) {
            data += "\nModule" + moduleNumber + " HW-Q" + questionNumber + " answer:\n\n";
            if (isTextAvailable(context, questionNumber)) {
                File answerFile = getTextFile(context, questionNumber);
                try {
                    String answer = IO.loadFileAsString(context, answerFile);
                    data += answer;
                }
                catch (Exception e) {
                    Savelog.w(TAG, "Cannot read from file " + answerFile.getName());
                }
            }
            data += "\n\n";
        }

        return data;

    }

    // The following must be called in an asyncTask.
    public String submit(Context context) {
        // Currently only handle text submission.
        // Concatenate all text answers into one file and submit.
        File targetInternalDir = getStudentDir(context);
        if (!targetInternalDir.exists()) { return null; }

        String data = prepareSubmitText(context);
        File submitTextFile = getSubmitTextFile(context);

        try {
            IO.saveStringAsFile(context, submitTextFile, data);
            HomeworkApi homeworkApi = new HomeworkApi(context, studentAccessCode, moduleNumber, submitTextFile, SubmitTextFileType);

            if (homeworkApi.isOK()) {
                Savelog.d(TAG, debug, "Post succeeded!");
            }
            else {
                Savelog.d(TAG, debug, "Post response: " + homeworkApi.getCommunication());
            }

            return homeworkApi.getData();

        } catch (Exception e) {
            Savelog.w(TAG, "Cannot submit homework ", e);
            return null;
        }

    }

    @Override
    public String toString() {
        String data = "";
        data += studentAccessCode + ";";
        data += moduleNumber + ";";
        data += numberOfQuestions;
        return data;
    }

    public boolean isCompleted(Context context) {

        boolean complete = true;
        File targetInternalDir = getStudentDir(context);
        if (targetInternalDir.exists()) {

            for (int questionNumber=1; questionNumber<=numberOfQuestions && complete; questionNumber++) {

                if (!isTextAvailable(context, questionNumber) && !isPictureAvailable(context, questionNumber)) {
                    complete = false;
                }
            }
        }
        else {
            complete = false;
        }
        completed = complete;
        return completed;
    }
}
