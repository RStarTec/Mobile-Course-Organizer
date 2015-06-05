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
import android.preference.PreferenceManager;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.developer.DeveloperOptions;
import com.rstar.mobile.csc205sp2015.developer.DeveloperSettings;
import com.rstar.mobile.csc205sp2015.fields.StudentFields;
import com.rstar.mobile.csc205sp2015.registered.api.Validity;
import com.rstar.mobile.csc205sp2015.registered.login.Student;
import com.rstar.mobile.csc205sp2015.utils.ApiUtils;
import com.rstar.mobile.csc205sp2015.utils.CSCUnitTestCase;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;
import com.rstar.mobile.csc205sp2015.utils.StudentUtils;

import java.io.File;

/**
 * Created by AHui on 1/30/15.
 */
public class StudentTest extends CSCUnitTestCase {
    private static final String TAG = HomeworkTest.class.getSimpleName()+"_class";

    private static final String validEmail = DeveloperSettings.developerEmail;

    Context targetContext = null;
    Context testContext = null;
    StudentFields.Constants constants = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new StudentFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }

    public void testConstructor() {
        // bad
        try {
            Student student = new Student(null);
            fail("expected to fail");
        }
        catch (Exception e) {
            File studentDir = Student.getStudentDir(targetContext);
            assertNotNull(studentDir);
            assertFalse(studentDir.exists());
        }

        try {
            String testAccessCode = "dummy";
            putPreferenceAccessCode(testAccessCode);
            Student student = new Student(targetContext);
            File studentDir = Student.getStudentDir(targetContext);
            assertNotNull(studentDir);
            assertTrue(studentDir.exists());
            assertEquals(testAccessCode, student.getAccessCode());
        }
        catch (Exception e) {
            fail("expected to fail");
        }
    }


    public void testSignupBad() throws Exception {
        StudentUtils.StudentId studentId = new StudentUtils.StudentId(1);
        final String validUserId = studentId.userId;
        String priorAccessCode = "dummy";

        // Do it once for all to minimize server access
        ApiUtils.masterClearSignup(targetContext, validUserId); // only clear server side

        {
            String userId = validUserId;
            String password = "valid";
            String email = validEmail;
            Student student = createAndVerifySignup(null, userId, password, email, priorAccessCode);  // bad context
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = null;
            String password = "valid";
            String email = validEmail;
            Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = null;
            String email = validEmail;
            Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = "valid";
            String email = null;
            Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = "";
            String password = "valid";
            String email = validEmail;
            Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = "";
            String email = validEmail;
            Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = "valid";
            String email = "";
            Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = "bad!"; // no server access
            String password = "valid";
            String email = validEmail;
            Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = "nonexist"; // has server access, userid not exist
            String password = "valid";
            String email = validEmail;
            Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = "!bad";
            String email = validEmail;
            Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = "valid";
            String email = "bad";
            Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
    }


    public void testSignupGood() throws Exception {
        String priorAccessCode = "dummy";

        // Use valid user account 1
        StudentUtils.StudentId studentId = new StudentUtils.StudentId(1);

        String userId = studentId.userId;
        String password = "valid";
        String email = validEmail;
        ApiUtils.masterClearSignup(targetContext, userId); // only clear server side
        Student student = createAndVerifySignup(targetContext, userId, password, email, priorAccessCode);
        assertEquals(studentId.accessCode, student.getAccessCode());
    }



    public void testSigninBad1() throws Exception {
        StudentUtils.StudentId studentId = new StudentUtils.StudentId(1);
        final String validUserId = studentId.userId;
        final String validPassword = "valid";
        final String priorAccessCode = studentId.accessCode;

        // Do it once to minimize server access
        String accessCode = ApiUtils.masterSignupUser(targetContext, studentId.userId, validPassword); // signup first
        assertEquals(priorAccessCode, accessCode); // the one on server must match the one in resource
        putPreferenceAccessCode(accessCode);

        {
            String userId = validUserId;
            String password = validPassword;
            Student student = createAndVerifySignin(null, userId, password, priorAccessCode); // bad context
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = null; // bad
            String password = validPassword;
            Student student = createAndVerifySignin(targetContext, userId, password, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = null; // bad
            Student student = createAndVerifySignin(targetContext, userId, password, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = ""; // bad
            String password = validPassword;
            Student student = createAndVerifySignin(targetContext, userId, password, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = ""; // bad
            Student student = createAndVerifySignin(targetContext, userId, password, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = "bad!"; // bad
            String password = validPassword;
            Student student = createAndVerifySignin(targetContext, userId, password, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = "nonexist"; // has server access, userid not exist
            String password = validPassword;
            Student student = createAndVerifySignin(targetContext, userId, password, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = "!bad"; // bad
            Student student = createAndVerifySignin(targetContext, userId, password, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
    }

    public void testSigninBad2() throws Exception {
        // user not signed up yet

        StudentUtils.StudentId studentId = new StudentUtils.StudentId(1);
        final String validUserId = studentId.userId;
        final String validPassword = "valid";
        final String priorAccessCode = constants.DefaultAccessCode;

        // Do it once for all to minimize server access
        ApiUtils.masterClearSignup(targetContext, validUserId); // only clear server side

        Student student = createAndVerifySignin(targetContext, validUserId, validPassword, priorAccessCode);
        assertEquals(priorAccessCode, student.getAccessCode());
    }


    public void testSigninGood() throws Exception {
        // Use valid user account 1
        StudentUtils.StudentId studentId = new StudentUtils.StudentId(1);
        final String validPassword = "valid";
        final String priorAccessCode = studentId.accessCode;

        // Do it once to minimize server access
        String accessCode = ApiUtils.masterSignupUser(targetContext, studentId.userId, validPassword); // signup first
        assertEquals(priorAccessCode, accessCode); // the one on server must match the one in resource
        putPreferenceAccessCode(accessCode);


        String userId = studentId.userId;
        String password = validPassword;
        Student student = createAndVerifySignin(targetContext, userId, password, priorAccessCode);
        assertEquals(priorAccessCode, student.getAccessCode());
    }


    public void testResetPasswordBad() throws Exception {
        StudentUtils.StudentId studentId = new StudentUtils.StudentId(1);
        final String validUserId = studentId.userId;
        final String validPassword = "valid";
        final String priorAccessCode = studentId.accessCode;

        // Do it once to minimize server access
        String accessCode = ApiUtils.masterSignupUser(targetContext, studentId.userId, validPassword); // signup first
        assertEquals(priorAccessCode, accessCode); // the one on server must match the one in resource
        putPreferenceAccessCode(accessCode);

        final boolean expected = false;
        {
            String userId = validUserId;
            Student student = createAndVerifyReset(null, userId, validPassword, priorAccessCode, expected); // bad context
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = null; // bad
            Student student = createAndVerifyReset(targetContext, userId, validPassword, priorAccessCode, expected);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = ""; // bad
            Student student = createAndVerifyReset(targetContext, userId, validPassword, priorAccessCode, expected);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = "bad!"; // bad
            Student student = createAndVerifyReset(targetContext, userId, validPassword, priorAccessCode, expected);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = "nonexist"; // has server access, userid not exist
            Student student = createAndVerifyReset(targetContext, userId, validPassword, priorAccessCode, expected);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
    }


    public void testResetPasswordGood() throws Exception {
        // Use valid user account 1
        StudentUtils.StudentId studentId = new StudentUtils.StudentId(1);
        final String validPassword = "valid";
        final String priorAccessCode = studentId.accessCode;

        // Do it once to minimize server access
        String accessCode = ApiUtils.masterSignupUser(targetContext, studentId.userId, validPassword); // signup first
        assertEquals(priorAccessCode, accessCode); // the one on server must match the one in resource
        putPreferenceAccessCode(accessCode);

        final boolean expected = true;

        String userId = studentId.userId;
        String password = validPassword;
        Student student = createAndVerifyReset(targetContext, userId, password, priorAccessCode, expected);
    }





    public void testChangePasswordBad1() throws Exception {
        StudentUtils.StudentId studentId = new StudentUtils.StudentId(1);
        final String validUserId = studentId.userId;
        final String validPassword = "valid";
        final String validNewPassword = "new";
        final String priorAccessCode = studentId.accessCode;

        // Do it once to minimize server access
        String accessCode = ApiUtils.masterSignupUser(targetContext, studentId.userId, validPassword); // signup first
        assertEquals(priorAccessCode, accessCode); // the one on server must match the one in resource
        putPreferenceAccessCode(accessCode);

        {
            String userId = validUserId;
            String password = validPassword;
            String newPassword = validNewPassword;
            Student student = createAndVerifyPasswd(null, userId, password, newPassword, priorAccessCode); // bad context
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = null;
            String password = validPassword;
            String newPassword = validNewPassword;
            Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = null;
            String newPassword = validNewPassword;
            Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = validPassword;
            String newPassword = null;
            Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = "";
            String password = validPassword;
            String newPassword = validNewPassword;
            Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = "";
            String newPassword = validNewPassword;
            Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = validPassword;
            String newPassword = "";
            Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = "bad";
            String password = validPassword;
            String newPassword = validNewPassword;
            Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = "!bad";
            String newPassword = validNewPassword;
            Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = validPassword;
            String newPassword = "!bad";
            Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }
        {
            String userId = validUserId;
            String password = validPassword;
            String newPassword = validPassword; // bad: identical
            Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
            assertEquals(priorAccessCode, student.getAccessCode());
        }

    }



    public void testChangePasswordGood() throws Exception {
        // Use valid user account 1
        StudentUtils.StudentId studentId = new StudentUtils.StudentId(1);
        final String validPassword = "valid";
        final String priorAccessCode = studentId.accessCode;

        // Do it once to minimize server access
        String accessCode = ApiUtils.masterSignupUser(targetContext, studentId.userId, validPassword); // signup first
        assertEquals(priorAccessCode, accessCode); // the one on server must match the one in resource
        putPreferenceAccessCode(accessCode);

        String userId = studentId.userId;
        String password = validPassword;
        String newPassword = "new";
        Student student = createAndVerifyPasswd(targetContext, userId, password, newPassword, priorAccessCode);
    }



    private Student createAndVerifySignup(Context context, String userId, String  password, String email, String priorAccessCode) {
        // test return boolean value
        // test stored accessCode
        // test if accessCode is kept when signup fails
        // test isSignedup()

        putPreferenceAccessCode(priorAccessCode);
        Student student = new Student(targetContext);
        assertEquals(priorAccessCode, student.getAccessCode());

        boolean success = student.signup(context, userId, password, email);

        if (success) {
            String storedAccessCode = getPreferenceAccessCode();
            assertNotNull(userId);
            assertTrue(Validity.isValid(userId));
            assertNotNull(password);
            assertTrue(Validity.isValid(password));
            assertNotNull(email);
            assertTrue(Validity.isEmail(email));

            assertEquals(storedAccessCode, student.getAccessCode());
            assertTrue(student.isSignedup());
            assertFalse(student.isLoginExpired(targetContext));

            Student.Login login = student.loadLogin(targetContext);
            assertEquals(userId, login.getUserId());
            assertEquals(password, login.getPassword());

        }
        else {
            if (!student.getAccessCode().equals(constants.DefaultAccessCode)) {
                assertEquals(priorAccessCode, student.getAccessCode());
            }

            // Note when signup is not successful, any old accessCode may remain on device
            if (student.getAccessCode().length()>0)
                assertTrue(student.isSignedup());
            else
                assertFalse(student.isSignedup());

            // cannot conclude anything about whether login has expired.
        }

        checkLoginValidity(targetContext, student);

        return student;
    }



    private Student createAndVerifySignin(Context context, String userId, String  password, String priorAccessCode) {
        // test return boolean value
        // test stored accessCode
        // test if accessCode is kept when signin fails
        // test isSignedup()

        Student student = new Student(targetContext);
        assertEquals(priorAccessCode, student.getAccessCode());

        boolean success = student.signin(context, userId, password);

        if (success) {
            String storedAccessCode = getPreferenceAccessCode();
            assertNotNull(userId);
            assertTrue(Validity.isValid(userId));
            assertNotNull(password);
            assertTrue(Validity.isValid(password));

            assertEquals(storedAccessCode, student.getAccessCode());
            assertTrue(student.isSignedup());
            assertFalse(student.isLoginExpired(targetContext));

            Student.Login login = student.loadLogin(targetContext);
            assertEquals(userId, login.getUserId());
            assertEquals(password, login.getPassword());
        }
        else {
            if (!student.getAccessCode().equals(constants.DefaultAccessCode)) {
                assertEquals(priorAccessCode, student.getAccessCode());
            }

            // Note when signin is not successful, any old accessCode may remain on device
            if (priorAccessCode.length()>0)
                assertTrue(student.isSignedup());
            else
                assertFalse(student.isSignedup());

            // cannot conclude anything about whether login has expired.
        }

        checkLoginValidity(targetContext, student);

        return student;
    }



    private Student createAndVerifyReset(Context context, String userId, String oldPassword, String priorAccessCode , boolean expected) {

        Student student = new Student(targetContext);
        assertEquals(priorAccessCode, student.getAccessCode());

        boolean success = student.resetPassword(context, userId);

        String storedAccessCode = getPreferenceAccessCode();
        assertEquals(storedAccessCode, student.getAccessCode());

        if (!student.getAccessCode().equals(constants.DefaultAccessCode)) {
            assertEquals(priorAccessCode, student.getAccessCode());
        }

        if (priorAccessCode.length()>0)
            assertTrue(student.isSignedup());
        else
            assertFalse(student.isSignedup());

        // cannot conclude anything about whether login has expired.
        boolean result = student.signin(targetContext, userId, oldPassword);
        assertEquals(expected, success);

        if (success) {
            assertNotNull(userId);
            assertTrue(Validity.isValid(userId));
            // Can no longer signin with the old password
            assertFalse(result);
        }

        return student;
    }


    private Student createAndVerifyPasswd(Context context, String userId, String oldPassword, String newPassword, String priorAccessCode) {

        Student student = new Student(targetContext);
        assertEquals(priorAccessCode, student.getAccessCode());

        boolean success = student.changePassword(context, userId, oldPassword, newPassword);

        String storedAccessCode = getPreferenceAccessCode();
        assertEquals(storedAccessCode, student.getAccessCode());

        if (!student.getAccessCode().equals(constants.DefaultAccessCode)) {
            assertEquals(priorAccessCode, student.getAccessCode());
        }

        if (priorAccessCode.length()>0)
            assertTrue(student.isSignedup());
        else
            assertFalse(student.isSignedup());

        // cannot conclude anything about whether login has expired.


        if (success) {
            assertNotNull(userId);
            assertTrue(Validity.isValid(userId));
            // Can signin with the new password
            boolean result = student.signin(targetContext, userId, newPassword);
            assertTrue(result);
        }
        else {
            // Can NOT signin with the new password
            if (oldPassword!=null && !oldPassword.equals(newPassword)) {
                boolean result = student.signin(targetContext, userId, newPassword);
                assertFalse(result);
            }
        }

        return student;
    }




    public void testStudentDir() {
        String dirname = Student.getStudentDirname();
        assertEquals(Student.StudentLabel+constants.dir, dirname);
        File dir = Student.getStudentDir(targetContext);
        assertNotNull(dir);
        assertEquals(dir.getName(), Student.StudentLabel);
    }

    public void testCacheTimeLimit() throws Exception {
        // Make sure the Student class is called first. Then check static
        Student student = new Student(targetContext);
        StudentFields.StaticVars staticVars = new StudentFields.StaticVars();

        if (AppSettings.defaultDebug) {
            DeveloperOptions developerOptions = new DeveloperOptions(targetContext);
            assertEquals(staticVars.LoginCacheTimeLimit, developerOptions.getLoginExpiration() * 60*1000);
        }
        else {
            assertEquals(staticVars.LoginCacheTimeLimit, constants.DefaultLoginCacheTimeLimit);
        }
    }

    public String getPreferenceAccessCode() {
        String accessCode = PreferenceManager.getDefaultSharedPreferences(targetContext.getApplicationContext())
                .getString(constants.PREF_accessCode, constants.DefaultAccessCode);
        return accessCode;
    }

    public void putPreferenceAccessCode(String accessCode) {
        PreferenceManager.getDefaultSharedPreferences(targetContext.getApplicationContext())
                .edit().putString(constants.PREF_accessCode, accessCode).commit();
    }




}
