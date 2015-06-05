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
import android.test.InstrumentationTestCase;

import com.rstar.mobile.csc205sp2015.fields.HomeworkApiFields;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.api.HomeworkApi;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.utils.ApiUtils;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;
import com.rstar.mobile.csc205sp2015.utils.PrivateSiteUtils;

import java.io.File;

/**
 * Created by AHui
 */
public class HomeworkApiTest extends InstrumentationTestCase {
    private static final String TAG = HomeworkApiTest.class.getSimpleName()+"_class";

    private static final String MissingLinkMessage = "Bad site url";

    Context targetContext = null;
    Context testContext = null;
    HomeworkApiFields.Constants constants = null;
    int GoodModuleNumber = ApiUtils.TestModuleNumber;
    int UngradedModuleNumber = 3;
    int BadModuleNumber = -100;
    String GoodFileLabel = ApiUtils.FileType.TXT;
    String BadFileLabel = ApiUtils.FileType.PDF; // not accepted

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new HomeworkApiFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }



    private void runTestBadParameters(Context context, String accessCode, int moduleNumber, File file, String fileType) {
        try {
            new HomeworkApi(context, accessCode, moduleNumber, file, fileType);
            fail("supposed to fail");
        } catch (Exception e) {
            Savelog.i(TAG, "err=" + e.getMessage());
            // should not need to access link
            assertFalse(e.getMessage() != null && e.getMessage().contains(MissingLinkMessage));
        }
    }



    public void testHomeworkConstructorBad() throws Exception {
        ApiUtils.FileType goodType = new ApiUtils.FileType(targetContext, GoodFileLabel);


        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        { // bad context
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            File file = goodType.getFile(targetContext);
            if (!file.exists()) goodType.getResourceAsFile(testContext, targetContext);
            String fileType = goodType.type;
            runTestBadParameters(null, accessCode, moduleNumber, file, fileType);
        }

        {
            String accessCode = null; // bad
            int moduleNumber = GoodModuleNumber;
            File file = goodType.getFile(targetContext);
            if (!file.exists()) goodType.getResourceAsFile(testContext, targetContext);
            String fileType = goodType.type;
            runTestBadParameters(targetContext, accessCode, moduleNumber, file, fileType);
        }

        {
            String accessCode = "good";
            int moduleNumber = BadModuleNumber; // bad
            File file = goodType.getFile(targetContext);
            if (!file.exists()) goodType.getResourceAsFile(testContext, targetContext);
            String fileType = goodType.type;
            runTestBadParameters(targetContext, accessCode, moduleNumber, file, fileType);
        }

        {
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            File file = null; // bad
            String fileType = goodType.type;
            runTestBadParameters(targetContext, accessCode, moduleNumber, file, fileType);
        }

        {
            String accessCode = ""; // bad
            int moduleNumber = GoodModuleNumber;
            File file = goodType.getFile(targetContext);
            if (!file.exists()) goodType.getResourceAsFile(testContext, targetContext);
            String fileType = goodType.type;
            runTestBadParameters(targetContext, accessCode, moduleNumber, file, fileType);
        }

        {
            String accessCode = "?"; // bad
            int moduleNumber = GoodModuleNumber;
            File file = goodType.getFile(targetContext);
            if (!file.exists()) goodType.getResourceAsFile(testContext, targetContext);
            String fileType = goodType.type;
            runTestBadParameters(targetContext, accessCode, moduleNumber, file, fileType);
        }

        {
            String accessCode = "\\"; // bad
            int moduleNumber = GoodModuleNumber;
            File file = goodType.getFile(targetContext);
            if (!file.exists()) goodType.getResourceAsFile(testContext, targetContext);
            String fileType = goodType.type;
            runTestBadParameters(targetContext, accessCode, moduleNumber, file, fileType);
        }

        {
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            File file = goodType.getFile(targetContext);
            if (file.exists()) file.delete(); // bad
            String fileType = goodType.type;
            runTestBadParameters(targetContext, accessCode, moduleNumber, file, fileType);
        }

    }


    private void runTestBadLink(Context context, String accessCode, int moduleNumber, File file, String fileType) {
        try {
            new HomeworkApi(context, accessCode, moduleNumber, file, fileType);
            fail("supposed to fail");
        } catch (Exception e) {
            Savelog.i(TAG, "err=" + e.getMessage());
            // should be caused by the missing link
            assertTrue(e.getMessage() != null && e.getMessage().contains(MissingLinkMessage));
        }
    }

    public void testHomeworkConstructorBad2() throws Exception {
        ApiUtils.FileType goodType = new ApiUtils.FileType(targetContext, GoodFileLabel);
        ApiUtils.FileType badType = new ApiUtils.FileType(targetContext, BadFileLabel);

        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        {
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            File file = badType.getFile(targetContext);
            if (!file.exists()) badType.getResourceAsFile(testContext, targetContext);
            String fileType = goodType.type;  // mismatched type acceptable
            runTestBadLink(targetContext, accessCode, moduleNumber, file, fileType);
        }

        {
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            File file = goodType.getFile(targetContext);
            if (!file.exists()) goodType.getResourceAsFile(testContext, targetContext);
            String fileType = null; // acceptable
            runTestBadLink(targetContext, accessCode, moduleNumber, file, fileType);
        }
    }


    public void testHomeworkConstructorBadLink() throws Exception {
        ApiUtils.FileType goodType = new ApiUtils.FileType(targetContext, GoodFileLabel);

        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        try {  // all parameters are locally good. Missing link
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            File file = goodType.getFile(targetContext);
            if (!file.exists()) goodType.getResourceAsFile(testContext, targetContext);
            String fileType = goodType.type;
            new HomeworkApi(targetContext, accessCode, moduleNumber, file, fileType);
            fail("supposed to fail");
        } catch (Exception e) {
            Savelog.i(TAG, "err=" + e.getMessage());
            // should be caused by the missing link
            assertTrue(e.getMessage() != null && e.getMessage().contains(MissingLinkMessage));
        }
    }


    public void testHomeworkConstructorGoodPatterns() throws Exception {
        ApiUtils.FileType goodType = new ApiUtils.FileType(targetContext, GoodFileLabel);
        ApiUtils.FileType badType = new ApiUtils.FileType(targetContext, BadFileLabel);

        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        boolean privateSiteAvailable = PrivateSiteUtils.setupPrivateSite(targetContext);
        assertTrue(privateSiteAvailable);

        // invalid access code
        try {
            String userId = "nova1";
            String password = "good";
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, password);

            String badAccessCode = "bad";
            int moduleNumber = GoodModuleNumber;
            File file = goodType.getFile(targetContext);
            if (!file.exists()) goodType.getResourceAsFile(testContext, targetContext);
            String fileType = goodType.type;
            HomeworkApi homeworkApi = new HomeworkApi(targetContext, badAccessCode, moduleNumber, file, fileType);

            verifySubmitResults(homeworkApi);
            assertFalse(homeworkApi.isOK());
        } catch (Exception e) {
            fail("supposed to pass");
        }

        // file format not accepted
        try {
            String userId = "nova1";
            String password = "good";
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, password);

            int moduleNumber = GoodModuleNumber;
            File file = badType.getFile(targetContext);
            if (!file.exists()) badType.getResourceAsFile(testContext, targetContext);
            String fileType = badType.type;
            HomeworkApi homeworkApi = new HomeworkApi(targetContext, accessCode, moduleNumber, file, fileType);

            verifySubmitResults(homeworkApi);
            assertFalse(homeworkApi.isOK());
        } catch (Exception e) {
            fail("supposed to pass");
        }

        // file format accepted
        try {
            String userId = "nova1";
            String password = "good";
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, password);

            int moduleNumber = GoodModuleNumber;
            File file = goodType.getFile(targetContext);
            if (!file.exists()) goodType.getResourceAsFile(testContext, targetContext);
            String fileType = goodType.type;
            HomeworkApi homeworkApi = new HomeworkApi(targetContext, accessCode, moduleNumber, file, fileType);

            verifySubmitResults(homeworkApi);
            assertTrue(homeworkApi.isOK());
        } catch (Exception e) {
            fail("supposed to pass");
        }
    }



    private File getDataFile() {
        return new File(HomeworkApi.getApiDir(targetContext), constants.defaultFilename);
    }


    private void verifySubmitResults(HomeworkApi homeworkApi) throws Exception {
        if (homeworkApi==null) return;

        // all api results are stored in a data file.
        File f = getDataFile();
        assertTrue(f.exists());

        HomeworkApiFields.Variables variables = new HomeworkApiFields.Variables(homeworkApi);

        if (homeworkApi.isOK()) {
            assertTrue(variables.data.trim().length()==0);
        }
        else {
            assertTrue(variables.data.contains("error"));
        }
    }



    public void testDownloadGoodLink() throws Exception {
        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        boolean privateSiteAvailable = PrivateSiteUtils.setupPrivateSite(targetContext);
        assertTrue(privateSiteAvailable);

        {   // bad context
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            String link = HomeworkApi.getDownloadLink(null, accessCode, moduleNumber);
            assertEquals("", link);
        }

        {
            String accessCode = null; // bad
            int moduleNumber = GoodModuleNumber;
            String link = HomeworkApi.getDownloadLink(targetContext, accessCode, moduleNumber);
            assertEquals("", link);
        }

        {
            String accessCode = ""; // bad
            int moduleNumber = GoodModuleNumber;
            String link = HomeworkApi.getDownloadLink(targetContext, accessCode, moduleNumber);
            assertEquals("", link);
        }

        {
            String accessCode = "good";
            int moduleNumber = BadModuleNumber;
            String link = HomeworkApi.getDownloadLink(targetContext, accessCode, moduleNumber);
            assertEquals("", link);
        }
    }


    public void testDownloadBadLink() throws Exception {
        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        {   // all parameters are good
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            String link = HomeworkApi.getDownloadLink(targetContext, accessCode, moduleNumber);
            assertEquals("", link);
        }
    }


}
