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

import com.rstar.mobile.csc205sp2015.fields.QuestionListFields;
import com.rstar.mobile.csc205sp2015.registered.homework.QuestionList;
import com.rstar.mobile.csc205sp2015.utils.CSCUnitTestCase;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;
import com.rstar.mobile.csc205sp2015.utils.QuestionListUtils;

import java.io.File;

/**
 * Created by AHui
 */
public class QuestionListTest extends CSCUnitTestCase {
    private static final String TAG = QuestionListTest.class.getSimpleName()+"_class";
    Context targetContext = null;
    Context testContext = null;
    QuestionListFields.Constants constants = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new QuestionListFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }


    public void testBadConstructor1() {
        try {
            QuestionList questionList = new QuestionList(null, null);
            fail("Should fail");
        }
        catch (Exception e) {}

        try {
            File f = new File("dummy");
            QuestionList questionList = new QuestionList(null, f);
            fail("Should fail");
        }
        catch (Exception e) {}
        try {
            QuestionList questionList = new QuestionList(targetContext, null);
            fail("Should fail");
        }
        catch (Exception e) {}

        try {
            File f = new File("dummy");
            QuestionList questionList = new QuestionList(targetContext, f);
            fail("Should fail");
        }
        catch (Exception e) {}
    }


    public void testBadConstructor2() throws Exception {
        {   // set 0
            QuestionListUtils.DataSet dataSet = new QuestionListUtils.DataSet(targetContext, 0);
            try {
                if (dataSet.homeworkDir!=null && !dataSet.homeworkDir.exists()) dataSet.homeworkDir.mkdir();
                DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.questionListId, dataSet.questionListFile);
                QuestionList questionList = new QuestionList(targetContext, dataSet.questionListFile);
                fail("Should fail");
            }
            catch (Exception e) {}
            DataUtils.clearAll(targetContext);
        }

        {   // set -1
            QuestionListUtils.DataSet dataSet = new QuestionListUtils.DataSet(targetContext, -1);
            try {
                if (dataSet.homeworkDir!=null && !dataSet.homeworkDir.exists()) dataSet.homeworkDir.mkdir();
                DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.questionListId, dataSet.questionListFile);
                QuestionList questionList = new QuestionList(targetContext, dataSet.questionListFile);
                fail("Should fail");
            }
            catch (Exception e) {}
            DataUtils.clearAll(targetContext);
        }

        {   // set -2
            QuestionListUtils.DataSet dataSet = new QuestionListUtils.DataSet(targetContext, -2);
            try {
                if (dataSet.homeworkDir!=null && !dataSet.homeworkDir.exists()) dataSet.homeworkDir.mkdir();
                DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.questionListId, dataSet.questionListFile);
                QuestionList questionList = new QuestionList(targetContext, dataSet.questionListFile);
                fail("Should fail");
            }
            catch (Exception e) {}
            DataUtils.clearAll(targetContext);
        }

        {   // set -3
            QuestionListUtils.DataSet dataSet = new QuestionListUtils.DataSet(targetContext, -3);
            try {
                if (dataSet.homeworkDir!=null && !dataSet.homeworkDir.exists()) dataSet.homeworkDir.mkdir();
                DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.questionListId, dataSet.questionListFile);
                QuestionList questionList = new QuestionList(targetContext, dataSet.questionListFile);
                fail("Should fail");
            }
            catch (Exception e) {}
            DataUtils.clearAll(targetContext);
        }

        {   // set -4
            QuestionListUtils.DataSet dataSet = new QuestionListUtils.DataSet(targetContext, -4);
            try {
                if (dataSet.homeworkDir!=null && !dataSet.homeworkDir.exists()) dataSet.homeworkDir.mkdir();
                DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.questionListId, dataSet.questionListFile);
                QuestionList questionList = new QuestionList(targetContext, dataSet.questionListFile);
                fail("Should fail");
            }
            catch (Exception e) {}
            DataUtils.clearAll(targetContext);
        }
        {   // set -5
            QuestionListUtils.DataSet dataSet = new QuestionListUtils.DataSet(targetContext, -5);
            try {
                if (dataSet.homeworkDir!=null && !dataSet.homeworkDir.exists()) dataSet.homeworkDir.mkdir();
                DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.questionListId, dataSet.questionListFile);
                QuestionList questionList = new QuestionList(targetContext, dataSet.questionListFile);
                fail("Should fail");
            }
            catch (Exception e) {}
            DataUtils.clearAll(targetContext);
        }
    }

    public void testGoodConstructor() throws Exception {
        {   // set 1
            QuestionListUtils.DataSet dataSet = new QuestionListUtils.DataSet(targetContext, 1);
            try {
                if (dataSet.homeworkDir!=null && !dataSet.homeworkDir.exists()) dataSet.homeworkDir.mkdir();
                DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.questionListId, dataSet.questionListFile);
                QuestionList questionList = new QuestionList(targetContext, dataSet.questionListFile);
                verifyQuestionList(questionList);
                QuestionListUtils.QuestionListFileData fileData = new QuestionListUtils.QuestionListFileData(targetContext, dataSet.questionListFile);
                assertEquals(fileData.numberOfQuestions, 2);
            }
            catch (Exception e) {
                fail("Should pass " + e.getMessage());
            }
            DataUtils.clearAll(targetContext);
        }

        {   // set 2
            QuestionListUtils.DataSet dataSet = new QuestionListUtils.DataSet(targetContext, 2);
            try {
                if (dataSet.homeworkDir!=null && !dataSet.homeworkDir.exists()) dataSet.homeworkDir.mkdir();
                DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.questionListId, dataSet.questionListFile);
                QuestionList questionList = new QuestionList(targetContext, dataSet.questionListFile);
                verifyQuestionList(questionList);
                QuestionListUtils.QuestionListFileData fileData = new QuestionListUtils.QuestionListFileData(targetContext, dataSet.questionListFile);
                assertEquals(fileData.numberOfQuestions, 8);
            }
            catch (Exception e) {
                fail("Should pass " + e.getMessage());
            }
            DataUtils.clearAll(targetContext);
        }
    }


    private void verifyQuestionList(QuestionList questionList) throws Exception {
        QuestionListFields.Variables variables = new QuestionListFields.Variables(questionList);

        for (int questionNumber=1; questionNumber<=variables.numberOfQuestions; questionNumber++) {
            int index = questionNumber-1;
            assertEquals(variables.textAvailability[index], questionList.isTextAvailable(questionNumber));
            assertEquals(variables.pictureAvailability[index], questionList.isPictureAvailable(questionNumber));
            assertEquals(variables.points[index], questionList.getPoints(questionNumber));
        }
    }

}
