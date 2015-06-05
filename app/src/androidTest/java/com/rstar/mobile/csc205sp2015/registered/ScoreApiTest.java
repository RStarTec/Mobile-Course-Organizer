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

import com.rstar.mobile.csc205sp2015.fields.ScoreApiFields;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.registered.api.ScoreApi;
import com.rstar.mobile.csc205sp2015.utils.ApiUtils;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;
import com.rstar.mobile.csc205sp2015.utils.PrivateSiteUtils;

import java.io.File;

/**
 * Created by AHui
 */
public class ScoreApiTest extends InstrumentationTestCase {
    private static final String TAG = ScoreApiTest.class.getSimpleName()+"_class";

    private static final String MissingLinkMessage = "Bad site url";

    Context targetContext = null;
    Context testContext = null;
    ScoreApiFields.Constants constants = null;
    int GoodModuleNumber = ApiUtils.TestModuleNumber;
    int UngradedModuleNumber = 3;
    int BadModuleNumber = -100;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new ScoreApiFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }






    private void runTestBasicConstructorBad(int request) throws Exception {
        // Make sure the course info is available, but the private link is not
        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        {
            String userId = "good";
            String password = "good";
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            runTestBadParameters(null, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = null;
            String password = "good";
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = "good";
            String password = null;
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = "good";
            String password = "good";
            String accessCode = null;
            int moduleNumber = GoodModuleNumber;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = "good";
            String password = "good";
            String accessCode = "good";
            int moduleNumber = BadModuleNumber;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = "";
            String password = "good";
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = "good";
            String password = "";
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = "good";
            String password = "good";
            String accessCode = "";
            int moduleNumber = GoodModuleNumber;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = "<"; // bad
            String password = "good";
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = "good";
            String password = ">"; // bad
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = "good";
            String password = "good";
            String accessCode = "\\"; // bad
            int moduleNumber = GoodModuleNumber;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, request);
        }

        {
            String userId = "good";
            String password = "good";
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            int badRequest = ScoreApi.request_default;
            runTestBadParameters(targetContext, userId, password, accessCode, moduleNumber, badRequest);
        }

    }




    private void runTestBadParameters(Context context, String userId, String password, String accessCode, int moduleNumber, int request) {
        try {
            new ScoreApi(context, userId, password, accessCode, moduleNumber, request);
            fail("supposed to fail");
        } catch (Exception e) {
            Savelog.i(TAG, "err=" + e.getMessage());
            // should not need to access link
            assertFalse(e.getMessage()!=null && e.getMessage().contains(MissingLinkMessage));
        }
    }

    public void testBasicConstructorBadLink() throws Exception {
        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        try {   // all parameters are good
            String userId = "good";
            String password = "good";
            String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            int goodRequest = ScoreApi.request_score;
            new ScoreApi(targetContext, userId, password, accessCode, moduleNumber, goodRequest);
            fail("supposed to fail");
        } catch (Exception e) {
            Savelog.i(TAG, "err=" + e.getMessage());
            // should be caused by the missing link
            assertTrue(e.getMessage()!=null && e.getMessage().contains(MissingLinkMessage));

        }
    }


    public void testScoreConstructorBadParams() throws Exception {
        final int request = ScoreApi.request_score;
        runTestBasicConstructorBad(request);
    }
    public void testSubmittedConstructorBadParams() throws Exception{
        final int request = ScoreApi.request_submitted;
        runTestBasicConstructorBad(request);
    }
    public void testFeedbackConstructorBadParams() throws Exception {
        final int request = ScoreApi.request_feedback;
        runTestBasicConstructorBad(request);
    }



    public void testScoreConstructorGoodPatterns() throws Exception {
        final int request = ScoreApi.request_score;

        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);

        boolean privateSiteAvailable = PrivateSiteUtils.setupPrivateSite(targetContext);
        assertTrue(privateSiteAvailable);

        try {
            String noUserId = "bad";
            boolean result = ApiUtils.masterClearSignup(targetContext, noUserId);
            assertFalse(result);

            String password = "good";
            final String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, noUserId, password, accessCode, moduleNumber, request);
            verifyScoreResult(scoreApi);

            assertFalse(scoreApi.isOK()); // bad userid.
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {
            String userId = "nova1";
            boolean result = ApiUtils.masterClearSignup(targetContext, userId);
            assertTrue(result);

            String password = "good";
            final String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, userId, password, accessCode, moduleNumber, request);
            verifyScoreResult(scoreApi);

            assertFalse(scoreApi.isOK()); // userid exists but user never signed up
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {
            String userId = "nova1";
            String password = "good";
            String rightPassword = password;
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, rightPassword);

            int moduleNumber = GoodModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, userId, password, accessCode, moduleNumber, request);
            verifyScoreResult(scoreApi);

            assertTrue(scoreApi.isOK()); // userid exists and user signed up. accessCode is correct
            double score = scoreApi.getScore(targetContext);
            verifyScoreWithMaster(userId, moduleNumber, score);
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {
            String userId = "nova1";
            String password = "good";
            String rightPassword = password;
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, rightPassword);

            int moduleNumber = UngradedModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, userId, password, accessCode, moduleNumber, request);
            verifyScoreResult(scoreApi);

            assertTrue(scoreApi.isOK()); // userid exists and user signed up. accessCode is correct. No homework. Default grade
            double score = scoreApi.getScore(targetContext);
            verifyScoreWithMaster(userId, moduleNumber, score);
        } catch (Exception e) {
            fail("supposed to pass" + e.getMessage());
        }

    }




    public void testSubmittedConstructorGoodPatterns() throws Exception {
        final int request = ScoreApi.request_submitted;

        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);

        boolean privateSiteAvailable = PrivateSiteUtils.setupPrivateSite(targetContext);
        assertTrue(privateSiteAvailable);

        try {
            String noUserId = "bad";
            boolean result = ApiUtils.masterClearSignup(targetContext, noUserId);
            assertFalse(result);

            String password = "good";
            final String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, noUserId, password, accessCode, moduleNumber, request);
            verifySubmittedResult(scoreApi);

            assertFalse(scoreApi.isOK()); // bad userid.
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {
            String userId = "nova1";
            boolean result = ApiUtils.masterClearSignup(targetContext, userId);
            assertTrue(result);

            String password = "good";
            final String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, userId, password, accessCode, moduleNumber, request);
            verifySubmittedResult(scoreApi);

            assertFalse(scoreApi.isOK()); // userid exists but user never signed up
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {
            String userId = "nova1";
            String password = "good";
            String rightPassword = password;
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, rightPassword);

            int moduleNumber = GoodModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, userId, password, accessCode, moduleNumber, request);
            verifySubmittedResult(scoreApi);

            assertTrue(scoreApi.isOK()); // userid exists and user signed up. accessCode is correct
            File f = scoreApi.getFile(targetContext);
            String submitted = IO.loadFileAsString(targetContext, f);
            verifySubmittedWithMaster(userId, moduleNumber, submitted);
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {
            String userId = "nova1";
            String password = "good";
            String rightPassword = password;
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, rightPassword);

            int moduleNumber = UngradedModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, userId, password, accessCode, moduleNumber, request);
            verifySubmittedResult(scoreApi);

            assertFalse(scoreApi.isOK()); // userid exists and user signed up. accessCode is correct. no homework
        } catch (Exception e) {
            fail("supposed to pass" + e.getMessage());
        }

    }


    public void testFeedbackConstructorGoodPatterns() throws Exception {
        final int request = ScoreApi.request_feedback;

        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);

        boolean privateSiteAvailable = PrivateSiteUtils.setupPrivateSite(targetContext);
        assertTrue(privateSiteAvailable);

        try {
            String noUserId = "bad";
            boolean result = ApiUtils.masterClearSignup(targetContext, noUserId);
            assertFalse(result);

            String password = "good";
            final String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, noUserId, password, accessCode, moduleNumber, request);
            verifyFeedbackResult(scoreApi);

            assertFalse(scoreApi.isOK()); // bad userid.
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {
            String userId = "nova1";
            boolean result = ApiUtils.masterClearSignup(targetContext, userId);
            assertTrue(result);

            String password = "good";
            final String accessCode = "good";
            int moduleNumber = GoodModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, userId, password, accessCode, moduleNumber, request);
            verifyFeedbackResult(scoreApi);

            assertFalse(scoreApi.isOK()); // userid exists but user never signed up
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {
            String userId = "nova1";
            String password = "good";
            String rightPassword = password;
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, rightPassword);

            int moduleNumber = GoodModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, userId, password, accessCode, moduleNumber, request);
            verifyFeedbackResult(scoreApi);

            assertTrue(scoreApi.isOK()); // userid exists and user signed up. accessCode is correct
            File f = scoreApi.getFile(targetContext);
            String feedback = IO.loadFileAsString(targetContext, f);
            verifyFeedbackWithMaster(userId, moduleNumber, feedback);
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {
            String userId = "nova1";
            String password = "good";
            String rightPassword = password;
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, rightPassword);

            int moduleNumber = UngradedModuleNumber;
            ScoreApi scoreApi = new ScoreApi(targetContext, userId, password, accessCode, moduleNumber, request);
            verifyFeedbackResult(scoreApi);

            assertFalse(scoreApi.isOK()); // userid exists and user signed up. accessCode is correct. No homework
        } catch (Exception e) {
            fail("supposed to pass");
        }

    }






    private File getDataFile(ScoreApi scoreApi) throws Exception {
        String filename = "";
        String extension = constants.defaultExtension;

        if (ApiUtils.getRequestType(scoreApi)==ScoreApi.request_score)
            filename = constants.defaultScoreFilename;
        else if (ApiUtils.getRequestType(scoreApi)==ScoreApi.request_feedback)
            filename = constants.defaultFeedbackFilename;
        else if (ApiUtils.getRequestType(scoreApi)==ScoreApi.request_submitted)
            filename = constants.defaultSubmittedFilename;
        return new File(ScoreApi.getApiDir(targetContext), filename+extension);
    }



    private void verifyScoreResult(ScoreApi scoreApi) throws Exception {
        if (scoreApi==null) return;
        assertEquals(ScoreApi.request_score, ApiUtils.getRequestType(scoreApi));

        // all api results are stored in a data file.
        File f = getDataFile(scoreApi);
        assertTrue(f.exists());

        // score request does not result in a file download.
        // So it's not possible to explicitly get the file.
        assertNull(scoreApi.getFile(targetContext));

        ScoreApiFields.Variables variables = new ScoreApiFields.Variables(scoreApi);

        if (scoreApi.isOK()) {
            assertEquals(Double.parseDouble(variables.data.trim()), scoreApi.getScore(targetContext));
        }
        else {
            assertTrue(variables.data.contains("error"));
            assertEquals(constants.defaultScore, scoreApi.getScore(targetContext));
        }
    }

    private void verifyScoreWithMaster(String userId, int moduleNumber, double score) throws Exception {
        double masterscore = ApiUtils.masterGetScore(targetContext, userId, moduleNumber);
        assertEquals(masterscore, score);
    }





    private void verifySubmittedResult(ScoreApi scoreApi) throws Exception {
        if (scoreApi==null) return;
        assertEquals(ScoreApi.request_submitted, ApiUtils.getRequestType(scoreApi));

        // all api results are stored in a data file.
        File f = getDataFile(scoreApi);
        assertTrue(f.exists());

        // score should be the default value.
        assertEquals(constants.defaultScore, scoreApi.getScore(targetContext));

        // score request results in a file download (if succeeded)
        // So it's not possible to explicitly get the file.

        ScoreApiFields.Variables variables = new ScoreApiFields.Variables(scoreApi);
        if (scoreApi.isOK()) {
            assertNotNull(scoreApi.getFile(targetContext));
            assertTrue(scoreApi.getFile(targetContext).exists());
        }
        else {
            assertNull(scoreApi.getFile(targetContext));
            assertTrue(variables.data.contains("error"));
        }
    }

    private void verifySubmittedWithMaster(String userId, int moduleNumber, String submitted) throws Exception {
        String masterSubmitted = ApiUtils.masterGetSubmitted(targetContext, userId, moduleNumber);
        assertEquals(masterSubmitted, submitted);
    }




    private void verifyFeedbackResult(ScoreApi scoreApi) throws Exception {
        if (scoreApi==null) return;
        assertEquals(ScoreApi.request_feedback, ApiUtils.getRequestType(scoreApi));

        // all api results are stored in a data file.
        File f = getDataFile(scoreApi);
        assertTrue(f.exists());

        // score should be the default value.
        assertEquals(constants.defaultScore, scoreApi.getScore(targetContext));

        // score request results in a file download (if succeeded)
        // So it's not possible to explicitly get the file.

        ScoreApiFields.Variables variables = new ScoreApiFields.Variables(scoreApi);
        if (scoreApi.isOK()) {
            assertNotNull(scoreApi.getFile(targetContext));
            assertTrue(scoreApi.getFile(targetContext).exists());
        }
        else {
            assertNull(scoreApi.getFile(targetContext));
            assertTrue(variables.data.contains("error"));
        }
    }

    private void verifyFeedbackWithMaster(String userId, int moduleNumber, String feedback) throws Exception {
        String masterFeedback = ApiUtils.masterGetFeedback(targetContext, userId, moduleNumber);
        assertEquals(masterFeedback, feedback);
    }
}
