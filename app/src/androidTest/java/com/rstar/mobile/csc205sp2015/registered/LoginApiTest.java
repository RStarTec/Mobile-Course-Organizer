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

import com.rstar.mobile.csc205sp2015.developer.DeveloperSettings;
import com.rstar.mobile.csc205sp2015.fields.LoginApiFields;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.api.LoginApi;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.utils.ApiUtils;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;
import com.rstar.mobile.csc205sp2015.utils.PrivateSiteUtils;

import java.io.File;

/**
 * Created by AHui
 */
public class LoginApiTest extends InstrumentationTestCase {
    private static final String TAG = LoginApiTest.class.getSimpleName()+"_class";

    private static final String MissingLinkMessage = "Bad site url";

    Context targetContext = null;
    Context testContext = null;
    LoginApiFields.Constants constants = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new LoginApiFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }


    private void runTestBadParameters(Context context, String userId, String password, String newPassword, String email, int request) {
        try {
            new LoginApi(context, userId, password, newPassword, email, request);
            fail("supposed to fail");
        } catch (Exception e) {
            // should not need to access link
            assertFalse(e.getMessage()!=null && e.getMessage().contains(MissingLinkMessage));
        }
    }

    public void testSignupConstructorBad() throws Exception {
        final String newPassword = null;
        final int request = LoginApi.request_signup;

        // Make sure the course info is available, but the private link is not
        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        // Signup constructor
        {
            String userId = "good";
            String password = "good";
            String email = "good@abc.def";
            runTestBadParameters(null, userId, password, newPassword, email, request); // bad context
        }

        {
            String userId = null;
            String password = "good";
            String email = "good@abc.def";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = null;
            String email = "good@abc.def";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            String email = null;
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "";
            String password = "good";
            String email = "good@abc.def";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "";
            String email = "good@abc.def";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            String email = "";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            String email = "bad";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            String email = "1"; // bad
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "<";  // bad
            String password = "good";
            String email = "good@abc.def";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "\\"; // bad
            String email = "good@abc.def";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            String email = "good@abc.def";
            int badRequest = LoginApi.request_default;
            runTestBadParameters(targetContext, userId, password, newPassword, email, badRequest);
        }

    }

    public void testSignupConstructorGoodPatterns() throws Exception {
        final String newPassword = null;
        final int request = LoginApi.request_signup;

        boolean privateSiteAvailable = PrivateSiteUtils.setupPrivateSite(targetContext);
        assertTrue(privateSiteAvailable);

        try {
            String noUserId = "bad";
            boolean result = ApiUtils.masterClearSignup(targetContext, noUserId);
            assertFalse(result);

            String password = "good";
            String email = "good@abc.def";
            LoginApi loginApi = new LoginApi(targetContext, noUserId, password, newPassword, email, request);
            verifySignupResult(loginApi);

            assertFalse(loginApi.isOK()); // bad userid.
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {
            String userId = "nova1";
            boolean result = ApiUtils.masterClearSignup(targetContext, userId);
            assertTrue(result);

            String password = "good";
            String email = "good@abc.def";
            LoginApi loginApi = new LoginApi(targetContext, userId, password, newPassword, email, request);
            verifySignupResult(loginApi);

            assertTrue(loginApi.isOK()); // expect to be ok
        } catch (Exception e) {
            fail("supposed to pass");
        }

    }





    public void testSigninConstructorBad() throws Exception {
        final String newPassword = null;
        final String email = null;
        final int request = LoginApi.request_signin;

        // Make sure the course info is available, but the private link is not
        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        // Signin constructor
        {
            String userId = "good";
            String password = "good";
            runTestBadParameters(null, userId, password, newPassword, email, request);
        }

        {
            String userId = null;
            String password = "good";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = null;
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "";
            String password = "good";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "<";  // bad
            String password = "good";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "\\"; // bad
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            int badRequest = LoginApi.request_default;
            runTestBadParameters(targetContext, userId, password, newPassword, email, badRequest);
        }

    }


    public void testSigninConstructorGoodPatterns() throws Exception {
        final String newPassword = null;
        final String email = null;
        final int request = LoginApi.request_signin;

        boolean privateSiteAvailable = PrivateSiteUtils.setupPrivateSite(targetContext);
        assertTrue(privateSiteAvailable);

        try {  // No such user
            String noUserId = "bad";
            boolean result = ApiUtils.masterClearSignup(targetContext, noUserId);
            assertFalse(result);

            String password = "good";
            LoginApi loginApi = new LoginApi(targetContext, noUserId, password, newPassword, email, request);
            verifySigninResult(loginApi, null);

            assertFalse(loginApi.isOK()); // no such userid
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {  // User exists but never signedup before
            String userId = "nova1";
            boolean result = ApiUtils.masterClearSignup(targetContext, userId);
            assertTrue(result);

            String password = "good";
            LoginApi loginApi = new LoginApi(targetContext, userId, password, newPassword, email, request);
            verifySigninResult(loginApi, null);

            assertFalse(loginApi.isOK()); // user exists but never signed up
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {  // User exists and has signedup
            String userId = "nova1";
            String password = "good";
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, password);

            String wrongPassword = "wrong";
            LoginApi loginApi = new LoginApi(targetContext, userId, wrongPassword, newPassword, email, request);
            verifySigninResult(loginApi, accessCode);

            assertFalse(loginApi.isOK()); // user signed up. Wrong password
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {  // User exists and has signedup
            String userId = "nova1";
            String password = "good";
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, password);

            LoginApi loginApi = new LoginApi(targetContext, userId, password, newPassword, email, request);
            verifySigninResult(loginApi, accessCode); // user signed up. password correct

            assertTrue(loginApi.isOK());
        } catch (Exception e) {
            fail("supposed to pass");
        }

    }





    public void testResetConstructorBad() throws Exception {
        final String newPassword = null;
        final String password = null;
        final String email = null;
        final int request = LoginApi.request_reset;

        // Make sure the course info is available, but the private link is not
        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        // Reset constructor
        {
            String userId = "good";
            runTestBadParameters(null, userId, password, newPassword, email, request);
        }

        {
            String userId = null;
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "<";  // bad
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "\\"; // bad
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            int badRequest = LoginApi.request_default;
            runTestBadParameters(targetContext, userId, password, newPassword, email, badRequest);
        }

    }


    public void testResetConstructorGoodPatterns() throws Exception {
        final String newPassword = null;
        final String password = null;
        final String email = null;
        final String recipientEmail = DeveloperSettings.developerEmail;
        final int request = LoginApi.request_reset;

        boolean privateSiteAvailable = PrivateSiteUtils.setupPrivateSite(targetContext);
        assertTrue(privateSiteAvailable);

        try {  // No such user
            String noUserId = "bad";
            boolean result = ApiUtils.masterClearSignup(targetContext, noUserId);
            assertFalse(result);

            LoginApi loginApi = new LoginApi(targetContext, noUserId, password, newPassword, email, request);
            verifyResetResult(loginApi, recipientEmail);

            assertFalse(loginApi.isOK()); // no such userid
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {  // User exists but never signedup before
            String userId = "nova1";
            boolean result = ApiUtils.masterClearSignup(targetContext, userId);
            assertTrue(result);

            LoginApi loginApi = new LoginApi(targetContext, userId, password, newPassword, email, request);
            verifyResetResult(loginApi, recipientEmail);

            assertFalse(loginApi.isOK()); // user exists but never signed up
        } catch (Exception e) {
            fail("supposed to pass " + e.getMessage());
        }

        try {  // User exists and has signedup
            String userId = "nova1";
            String goodPassword = "good";
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, goodPassword);

            LoginApi loginApi = new LoginApi(targetContext, userId, password, newPassword, email, request);
            verifyResetResult(loginApi, recipientEmail); // user signed up. password correct

            assertTrue(loginApi.isOK());
        } catch (Exception e) {
            fail("supposed to pass");
        }
    }




    public void testPasswdConstructorBad() throws Exception {
        String email = null;
        final int request = LoginApi.request_passwd;

        // Make sure the course info is available, but the private link is not
        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);
        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        // Passwd constructor
        {
            String userId = "good";
            String password = "good";
            String newPassword = "good2";
            runTestBadParameters(null, userId, password, newPassword, email, request);
        }

        {
            String userId = null;
            String password = "good";
            String newPassword = "good2";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = null;
            String newPassword = "good2";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            String newPassword = null;
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "";
            String password = "good";
            String newPassword = "good2";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "";
            String newPassword = "good2";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            String newPassword = "";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            String newPassword = "good"; // identical
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }


        {
            String userId = "<";  // bad
            String password = "good";
            String newPassword = "good2";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "\\"; // bad
            String newPassword = "good2";
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            String newPassword = ">"; // bad
            runTestBadParameters(targetContext, userId, password, newPassword, email, request);
        }

        {
            String userId = "good";
            String password = "good";
            String newPassword = "good2";
            int badRequest = LoginApi.request_default; // bad
            runTestBadParameters(targetContext, userId, password, newPassword, email, badRequest);
        }
    }



    public void testPasswdConstructorGoodPatterns() throws Exception {
        final String password = "good";
        final String newPassword = "good2";
        final String email = null;
        final int request = LoginApi.request_passwd;

        boolean privateSiteAvailable = PrivateSiteUtils.setupPrivateSite(targetContext);
        assertTrue(privateSiteAvailable);

        try {  // No such user
            String noUserId = "bad";
            boolean result = ApiUtils.masterClearSignup(targetContext, noUserId);
            assertFalse(result);

            LoginApi loginApi = new LoginApi(targetContext, noUserId, password, newPassword, email, request);
            verifyPasswdResult(loginApi);

            assertFalse(loginApi.isOK()); // no such userid
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {  // User exists but never signedup before
            String userId = "nova1";
            boolean result = ApiUtils.masterClearSignup(targetContext, userId);
            assertTrue(result);

            LoginApi loginApi = new LoginApi(targetContext, userId, password, newPassword, email, request);
            verifyPasswdResult(loginApi);

            assertFalse(loginApi.isOK()); // user exists but never signed up
        } catch (Exception e) {
            fail("supposed to pass " + e.getMessage());
        }

        try {  // User exists and has signedup
            String userId = "nova1";
            String wrongPassword = "different";
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, wrongPassword);

            LoginApi loginApi = new LoginApi(targetContext, userId, password, newPassword, email, request);
            verifyPasswdResult(loginApi);

            assertFalse(loginApi.isOK());
        } catch (Exception e) {
            fail("supposed to pass");
        }

        try {  // User exists and has signedup
            String userId = "nova1";
            String rightPassword = password;
            String accessCode = ApiUtils.masterSignupUser(targetContext, userId, rightPassword);

            LoginApi loginApi = new LoginApi(targetContext, userId, password, newPassword, email, request);
            verifyPasswdResult(loginApi);

            assertTrue(loginApi.isOK());
        } catch (Exception e) {
            fail("supposed to pass");
        }
    }


    public void runTestBadLink(int goodRequest) throws Exception {
        // need to define which info set to use for course
        ApiUtils.getCourse(targetContext);

        // Make sure we don't have the link to use
        PrivateSite.get(targetContext).clear(targetContext);

        try {   // all parameters are good
            String userId = "good";
            String password = "good";
            String newPassword = "good2";
            final String email = DeveloperSettings.developerEmail;
            new LoginApi(targetContext, userId, password, newPassword, email, goodRequest);
            fail("supposed to fail");
        } catch (Exception e) {
            Savelog.i(TAG, "err=" + e.getMessage());
            // should be caused by the missing link
            assertTrue(e.getMessage()!=null && e.getMessage().contains(MissingLinkMessage));
        }
    }

    public void testConstructorBadLink() throws Exception {
        runTestBadLink(LoginApi.request_signup);
        runTestBadLink(LoginApi.request_signin);
        runTestBadLink(LoginApi.request_reset);
        runTestBadLink(LoginApi.request_passwd);
    }




    private File getDataFile() {
        return new File(LoginApi.getApiDir(targetContext), constants.defaultLoginFilename);
    }



    private void verifySignupResult(LoginApi loginApi) throws Exception {
        if (loginApi==null) return;
        assertEquals(LoginApi.request_signup, ApiUtils.getRequestType(loginApi));

        File f = getDataFile();
        assertTrue(f.exists());
        assertTrue(f.getName().equals(constants.defaultLoginFilename));

        LoginApiFields.Variables variables = new LoginApiFields.Variables(loginApi);

        if (loginApi.isOK()) {
            assertEquals(loginApi.getAccessCode(), variables.data);
        }
        else {
            assertFalse(loginApi.getAccessCode().equals(variables.data)); // accessCode must not contain error message
            assertEquals(LoginApi.DefaultAccessCode, loginApi.getAccessCode());
            assertTrue(variables.data.contains("error"));
        }
    }


    private void verifySigninResult(LoginApi loginApi, String accessCode) throws Exception {
        if (loginApi==null) return;
        assertEquals(LoginApi.request_signin, ApiUtils.getRequestType(loginApi));

        File f = getDataFile();
        assertTrue(f.exists());
        assertTrue(f.getName().equals(constants.defaultLoginFilename));

        LoginApiFields.Variables variables = new LoginApiFields.Variables(loginApi);

        if (loginApi.isOK()) {
            assertEquals(loginApi.getAccessCode(), variables.data);
            assertEquals(accessCode, loginApi.getAccessCode());
        }
        else {
            assertFalse(loginApi.getAccessCode().equals(variables.data)); // accessCode must not contain error message
            assertEquals(LoginApi.DefaultAccessCode, loginApi.getAccessCode());
            assertTrue(variables.data.contains("error"));
        }
    }


    private void verifyResetResult(LoginApi loginApi, String recipientEmail) throws Exception {
        if (loginApi==null) return;
        assertEquals(LoginApi.request_reset, ApiUtils.getRequestType(loginApi));

        File f = getDataFile();
        assertTrue(f.exists());
        assertTrue(f.getName().equals(constants.defaultLoginFilename));

        // access code must not be available in data
        assertEquals(LoginApi.DefaultAccessCode, loginApi.getAccessCode());

        LoginApiFields.Variables variables = new LoginApiFields.Variables(loginApi);

        if (loginApi.isOK()) {
            assertTrue(variables.data.contains(recipientEmail));
        }
        else {
            assertTrue(variables.data.contains("error"));
        }
    }

    private void verifyPasswdResult(LoginApi loginApi) throws Exception {
        if (loginApi==null) return;
        assertEquals(LoginApi.request_passwd, ApiUtils.getRequestType(loginApi));

        File f = getDataFile();
        assertTrue(f.exists());
        assertTrue(f.getName().equals(constants.defaultLoginFilename));

        // access code must not be available in data
        assertEquals(LoginApi.DefaultAccessCode, loginApi.getAccessCode());

        LoginApiFields.Variables variables = new LoginApiFields.Variables(loginApi);

        if (loginApi.isOK()) {
            assertTrue(variables.data.equals("OK"));
        }
        else {
            assertTrue(variables.data.contains("error"));
        }
    }



}
