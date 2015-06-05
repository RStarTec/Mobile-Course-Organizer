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

package com.rstar.mobile.csc205sp2015.registered;

import android.content.Context;

import com.rstar.mobile.csc205sp2015.fields.HomeworkFields;
import com.rstar.mobile.csc205sp2015.registered.homework.Homework;
import com.rstar.mobile.csc205sp2015.registered.homework.QuestionList;
import com.rstar.mobile.csc205sp2015.utils.CSCUnitTestCase;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;
import com.rstar.mobile.csc205sp2015.utils.QuestionListUtils;

/**
 * Created by AHui
 */
public class HomeworkTest extends CSCUnitTestCase {
    private static final String TAG = HomeworkTest.class.getSimpleName()+"_class";

    // TODO: later, replace these labels with constants from Homework
    // Since the homework does not check the page numbers and homework numbers, the filenames may contain minus sign
    private static final String patternDir = Homework.HomeworkLabel + "-*" + "[0-9]+\\/";
    private static final String patternArchive = Homework.HomeworkLabel + "-*" + "[0-9]+\\.zip";


    Context targetContext = null;
    Context testContext = null;
    HomeworkFields.Constants constants = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new HomeworkFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }

    public void testConstructor1() {
        int moduleNumber = QuestionListUtils.TestModuleNumber;
        // TODO: No student
        try {
            Homework homework = new Homework(targetContext, moduleNumber);
            verifyHomeworkFilenames(homework);
            verifyHomeworkStatus(homework);
            // Expecting not installed
            assertFalse(homework.isInstalled(targetContext));
        }
        catch (Exception e) { fail("Not expected to fail");}

        // TODO: finish this test after student is tested
    }

    public void testConstructor2() throws Exception {
    // TODO: check when student is or is not signed up

    }



    public void testClear() throws Exception {
        //  TODO
        {   // Create a homework without calling setup. Then clear it
        }

        {   // Set up a homework. Then clear it
        }
    }




    private void verifyHomeworkFilenames(Homework homework) {
        int moduleNumber = homework.getModuleNumber();

        // NOTE: the file names accept bad moduleNumber and questionNumber
        assertTrue(Homework.getHomeworkDirname(moduleNumber).matches(patternDir));
        assertTrue(Homework.getArchiveName(moduleNumber).matches(patternArchive));
        assertTrue(Homework.getHomeworkDirname(moduleNumber).contains(Integer.toString(moduleNumber)));
        assertTrue(Homework.getArchiveName(moduleNumber).contains(Integer.toString(moduleNumber)));
        String homeworkDirPath = homework.getHomeworkDir(targetContext).getAbsolutePath();

        int numberOfQuestions = homework.getNumberOfQuestions();
        for (int questionNumber=-1; questionNumber<=numberOfQuestions+1; questionNumber++) {
            assertTrue(Homework.getQuestionLabel(moduleNumber, questionNumber).equals(Homework.HomeworkLabel + moduleNumber + Homework.QuestionLabel + questionNumber));
            assertTrue(Homework.getTextName(questionNumber).equals(Homework.QuestionLabel + questionNumber + constants.TextExtension));
            assertTrue(Homework.getPictureName(questionNumber).equals(Homework.QuestionLabel + questionNumber + constants.PictureExtension));

            // Note: the file itself may not exist if the page number is bad.
            // If page number is good, it must locate at the right directory

            if (questionNumber>=1 && questionNumber<=numberOfQuestions) {
                assertTrue(homework.getTextFile(targetContext, questionNumber).getAbsolutePath().contains(homeworkDirPath));
                assertTrue(homework.getPictureFile(targetContext, questionNumber).getAbsolutePath().contains(homeworkDirPath));
            }
            else {
                // If page number is bad, file does not exist
                assertNull(homework.getTextFile(targetContext, questionNumber));
                assertNull(homework.getPictureFile(targetContext, questionNumber));
            }
        }
    }



    private void verifyHomeworkStatus(Homework homework) throws Exception {
        HomeworkFields.Variables variables = new HomeworkFields.Variables(homework);
        assertEquals(variables.link, homework.getLink());

        assertNotNull(homework.getQuestionListFile(targetContext));
        if (homework.getQuestionListFile(targetContext).exists()) {
            assertTrue(homework.getNumberOfQuestions() > 0);
            assertNotNull(variables.questionList);

            for (int questionNumber = 1; questionNumber <= homework.getNumberOfQuestions(); questionNumber++) {
                assertEquals(variables.questionList.isTextAvailable(questionNumber), homework.isTextAvailable(questionNumber));
                assertEquals(variables.questionList.isPictureAvailable(questionNumber), homework.isPictureAvailable(questionNumber));
                assertEquals(variables.questionList.getPoints(questionNumber), homework.getPoints(questionNumber));
            }
        }

        if (homework.isInstalled(targetContext)) {
            assertTrue(homework.getArchiveFile(targetContext).exists());
            assertTrue(homework.getHomeworkDir(targetContext).exists());

            assertNotNull(variables.questionList);
            assertEquals(variables.questionList.getTime(), homework.getLastUpdateDate());
            assertEquals(variables.moduleNumber, homework.getModuleNumber());
            assertFalse(homework.getInstallTime(targetContext).equals(QuestionList.NoDate));

            for (int questionNumber=1; questionNumber<=homework.getNumberOfQuestions(); questionNumber++) {
                if (homework.isTextAvailable(questionNumber))
                    assertTrue(homework.getTextFile(targetContext, questionNumber).exists());
                if (homework.isPictureAvailable(questionNumber))
                    assertTrue(homework.getPictureFile(targetContext, questionNumber).exists());
            }
        }
        else {
            assertFalse(homework.getHomeworkDir(targetContext).exists());
        }
    }


    private Homework getSampleHomeworkNoSetup() throws Exception {
        int moduleNumber = 1;
        Homework homework = new Homework(targetContext, moduleNumber);
        
        return homework;
    }

    private Homework getSampleHomeworkSetup() throws Exception {
        int moduleNumber = 1;
        Homework homework = new Homework(targetContext, moduleNumber);
        return homework;
    }

}
