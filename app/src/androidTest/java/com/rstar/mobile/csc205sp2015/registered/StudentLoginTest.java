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

import com.rstar.mobile.csc205sp2015.developer.DeveloperSettings;
import com.rstar.mobile.csc205sp2015.fields.StudentFields;
import com.rstar.mobile.csc205sp2015.registered.login.Student;
import com.rstar.mobile.csc205sp2015.utils.CSCUnitTestCase;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;

/**
 * Created by AHui on 1/30/15.
 */
public class StudentLoginTest extends CSCUnitTestCase {
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
        {
            Student.Login login = new Student.Login();
            assertEquals("", login.getUserId());
            assertEquals("", login.getPassword());
            login.setUserId(null);
            assertEquals("", login.getUserId());
            assertEquals("", login.getPassword());
            assertFalse(login.isValid());
        }
        {
            Student.Login login = new Student.Login();
            assertEquals("", login.getUserId());
            assertEquals("", login.getPassword());
            login.setPassword(null);
            assertEquals("", login.getUserId());
            assertEquals("", login.getPassword());
            assertFalse(login.isValid());
        }
        {
            String userId = "good";
            Student.Login login = new Student.Login();
            assertEquals("", login.getUserId());
            assertEquals("", login.getPassword());
            login.setUserId(userId);
            assertEquals(userId, login.getUserId());
            assertEquals("", login.getPassword());
            assertFalse(login.isValid());
        }
        {
            String password = "good";
            Student.Login login = new Student.Login();
            assertEquals("", login.getUserId());
            assertEquals("", login.getPassword());
            login.setPassword(password);
            assertEquals("", login.getUserId());
            assertEquals(password, login.getPassword());
            assertFalse(login.isValid());
        }
        {
            String userId = "good";
            String password = "good";
            Student.Login login = new Student.Login();
            assertEquals("", login.getUserId());
            assertEquals("", login.getPassword());
            login.setUserId(userId);
            login.setPassword(password);
            assertEquals(userId, login.getUserId());
            assertEquals(password, login.getPassword());
            assertTrue(login.isValid());
        }
    }

    public void testSaveLoginBad() throws Exception {
        {
            String userId = "good";
            String password = "good";
            Student student = new Student(targetContext);
            StudentFields.saveLogin(student, null, userId, password);  // bad context

            checkLoginValidity(targetContext, student);

            Student.Login login = student.loadLogin(targetContext);
            assertEquals("", login.getUserId());
            assertEquals("", login.getPassword());
            assertFalse(login.isValid());
        }
        {
            String userId = null;
            String password = "good";
            Student student = new Student(targetContext);
            StudentFields.saveLogin(student, targetContext, userId, password);

            checkLoginValidity(targetContext, student);

            Student.Login login = student.loadLogin(targetContext);
            assertEquals("", login.getUserId());
            assertEquals("", login.getPassword());
            assertFalse(login.isValid());
        }
        {
            String userId = "good";
            String password = null;
            Student student = new Student(targetContext);
            StudentFields.saveLogin(student, targetContext, userId, password);

            checkLoginValidity(targetContext, student);

            Student.Login login = student.loadLogin(targetContext);
            assertEquals("", login.getUserId());
            assertEquals("", login.getPassword());
            assertFalse(login.isValid());
        }
    }

    public void testSaveLoginGood() throws Exception {
        String userId = "good1";
        String password = "good2";
        Student student = new Student(targetContext);
        StudentFields.saveLogin(student, targetContext, userId, password);

        checkLoginValidity(targetContext, student);

        Student.Login login = student.loadLogin(targetContext);
        assertEquals(userId, login.getUserId());
        assertEquals(password, login.getPassword());
        assertTrue(login.isValid());
    }

    public void testClearLogin() throws Exception {
        String userId = "good1";
        String password = "good2";
        Student student = new Student(targetContext);
        StudentFields.saveLogin(student, targetContext, userId, password);

        checkLoginValidity(targetContext, student);

        Student.Login login = student.loadLogin(targetContext);
        assertEquals(userId, login.getUserId());
        assertEquals(password, login.getPassword());
        assertTrue(login.isValid());

        student.clearLogin(targetContext);

        checkLoginValidity(targetContext, student);

        login = student.loadLogin(targetContext);
        assertEquals("", login.getUserId());
        assertEquals("", login.getPassword());
        assertFalse(login.isValid());
    }
}
