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
import com.rstar.mobile.csc205sp2015.registered.login.Student;

import java.io.File;

/**
 * Created by AHui
 */
public class Homework {
    private static final String TAG = Homework.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    // ATTENTION: make sure that the internal directory for the homework is separate from the directory that contains public available materials.

    public static final int DefaultModuleNumber = 0;
    public static final int DefaultQuestionNumber = 1;
    public static final String HomeworkLabel = "hw";
    public static final String QuestionLabel = "Q";
    private static final String HomeworkExtension = ".zip";
    private static final String TextExtension = ".txt";
    private static final String PictureExtension = ".jpg";
    private static final String QuestionListFilename = "QnList" + TextExtension;
    private static final String dir = "/";
    private static final String TimeFilename = "Time" + TextExtension; // Time of this package (from questionList)


    private int moduleNumber = DefaultModuleNumber;
    private String link = "";
    private QuestionList questionList = null;

    public Homework(Context context, int moduleNumber) {
        this.moduleNumber = moduleNumber;

        Student student = new Student(context);
        link = HomeworkApi.getDownloadLink(context, student.getAccessCode(), moduleNumber);

        // Now set up question list.
        try {
            questionList = new QuestionList(context, getQuestionListFile(context));
        } catch (Exception e) {
            questionList = null;
        }
    }


    // Setup must be run within an asyncTask
    public void setup(Context context) {

        Savelog.d(TAG, debug, "setting up homework for module" + moduleNumber);

        if (link==null || link.length() == 0) {
            Savelog.d(TAG, debug, "link is undefined.");
            return;
        }

        File archiveFile = getArchiveFile(context);
        File targetInternalDir = getHomeworkDir(context);

        // If archive is not currently on device,
        // download it
        try {
            if (!archiveFile.exists()) {
                Savelog.d(TAG, debug, "download package from " + link);
                IO.downloadFile(link, archiveFile);
            }

            if (targetInternalDir.exists()) {
                IO.clearInternalDirectory(context, targetInternalDir);
            }
            IO.unzip(context, archiveFile.getName(), targetInternalDir);
            Savelog.d(TAG, debug, "Successfully unzip file " + archiveFile.getName());

            // Now set up question list.
            questionList = new QuestionList(context, getQuestionListFile(context));

            // Save the time of this package according to the header's information
            Savelog.d(TAG, debug, "ready to same time " + questionList.getTime() + " to file " + getTimeFile(context));
            IO.saveStringAsFile(context, getTimeFile(context), questionList.getTime());
        }
        catch (Exception e) {
            Savelog.w(TAG, "cannot setup homework" , e);
            IO.clearInternalDirectory(context, targetInternalDir);
            archiveFile.delete();
            return;
        }

        if (debug) {
            Savelog.d(TAG, debug, "Displaying internal directory content:");
            File[] files = IO.getInternalFiles(context);
            for (File f : files) {
                Savelog.d(TAG, debug, f.getAbsolutePath() + " is " + (f.isDirectory()?"directory":"file"));
            }
        }
    }

    public int getModuleNumber() {
        return this.moduleNumber;
    }
    public int getNumberOfQuestions() {
        if (questionList==null) return 0;
        return questionList.getNumberOfQuestions();
    }
    public String getLink() {
        return this.link;
    }
    public String getLastUpdateDate() {
        if (questionList==null) return QuestionList.NoDate;
        else return questionList.getTime();
    }
    public double getPoints(int questionNumber) {
        if (questionList==null) return 0;
        return questionList.getPoints(questionNumber);
    }


    public File getArchiveFile(Context context) {
        return IO.getInternalFile(context, getArchiveName(this.moduleNumber));
    }

    public boolean isTextAvailable(int questionNumber) {
        return (questionList != null) && questionList.isTextAvailable(questionNumber);
    }

    public boolean isPictureAvailable(int questionNumber) {
        return (questionList != null) && questionList.isPictureAvailable(questionNumber);
    }

    public File getTextFile(Context context, int questionNumber) {
        if (questionList==null) return null;
        if (questionNumber<=0 || questionNumber> questionList.getNumberOfQuestions()) return null;
        return new File(getHomeworkDir(context), getTextName(questionNumber));
    }

    public File getPictureFile(Context context, int questionNumber) {
        if (questionList==null) return null;
        if (questionNumber<=0 || questionNumber> questionList.getNumberOfQuestions()) return null;
        return new File(getHomeworkDir(context), getPictureName(questionNumber));
    }

    public File getHomeworkDir(Context context) {
        String dirname = getHomeworkDirname(this.moduleNumber);
        return IO.getInternalDir(context, dirname);
    }

    public static String getQuestionLabel(int moduleNumber, int questionNumber) {
        return HomeworkLabel + moduleNumber + QuestionLabel + questionNumber;
    }

    public static String getArchiveName(int moduleNumber) {
        return HomeworkLabel + moduleNumber + HomeworkExtension;
    }
    public static String getHomeworkDirname(int moduleNumber) {
        return HomeworkLabel + moduleNumber + dir;
    }
    public static String getPictureName(int questionNumber) {
        return QuestionLabel + questionNumber + PictureExtension;
    }

    public static String getTextName(int questionNumber) {
        return QuestionLabel + questionNumber + TextExtension;
    }

    public File getQuestionListFile(Context context) {
        return new File(getHomeworkDir(context), QuestionListFilename);
    }

    private File getTimeFile(Context context) {
        return new File(getHomeworkDir(context), TimeFilename);
    }
    public String getInstallTime(Context context) {
        String installTime = QuestionList.NoDate;
        try {
            File f = getTimeFile(context);
            if (f!=null && f.exists()) installTime = IO.loadFileAsString(context, f);
        } catch (Exception e) {}
        return installTime;
    }


    @Override
    public String toString() {
        int numberOfQuestions = 0;
        if (questionList!=null) numberOfQuestions = questionList.getNumberOfQuestions();
        String data = "";
        data += moduleNumber + ";";
        data += numberOfQuestions + ";";
        data += link;
        return data;
    }

    public boolean isInstalled(Context context) {
        if (questionList==null) return false;

        int numberOfQuestions = questionList.getNumberOfQuestions();
        if (numberOfQuestions==0) return true;

        boolean complete = true;
        File targetInternalDir = getHomeworkDir(context);
        if (targetInternalDir.exists()) {
            for (int questionNumber=1; questionNumber<=numberOfQuestions && complete; questionNumber++) {
                File f;
                if (questionList.isTextAvailable(questionNumber)) {
                    f = getTextFile(context, questionNumber);
                    if (f == null || !f.exists()) complete = false;
                }
                if (questionList.isPictureAvailable(questionNumber)) {
                    f = getPictureFile(context, questionNumber);
                    if (f == null || !f.exists()) complete = false;
                }
            }
        }
        else {
            complete = false;
        }
        return complete;
    }


    public static class Status {
        public static final int OnDevice = 1001;
        public static final int NeedDownload = 1002;
        public static final int NeedLogin = 1003;
        public static final int NotAvailable = 1004;
        public static final int Default = NeedLogin;

        public static int get(Context context, int moduleNumber) {
            Student student = new Student(context);
            if (student.isSignedup()) {
                Homework homework = new Homework(context, moduleNumber);

                if (homework.isInstalled(context) && homework.getNumberOfQuestions()>0) {
                    // student has access and homework is on device
                    return OnDevice;
                }
                else if (homework.isInstalled(context) && homework.getNumberOfQuestions()==0) {
                    // student has access and homework is empty
                    return NotAvailable;
                }

                else {
                    // student has access but homework is not yet on device
                    return NeedDownload;
                }
            }
            else {
                // student does not have access, regardless of whether homework is available
                return NeedLogin;
            }
        }
    }

    public void clear(Context context) {
        if (questionList==null) return;

        File targetInternalDir = getHomeworkDir(context);
        if (targetInternalDir!=null && targetInternalDir.exists()) {
            IO.clearInternalDirectory(context, targetInternalDir);
        }

        // delete archive
        File f = getArchiveFile(context);
        if (f != null && f.exists()) {
            Savelog.d(TAG, debug, "Deleting " + f.getAbsolutePath());
            f.delete();
        }
        questionList = null;
        // do not clear link
    }
}
