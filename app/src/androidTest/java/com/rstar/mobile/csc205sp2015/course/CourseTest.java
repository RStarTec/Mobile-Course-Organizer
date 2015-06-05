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

package com.rstar.mobile.csc205sp2015.course;

import android.content.Context;

import com.rstar.mobile.csc205sp2015.fields.CourseFields;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Header;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.utils.CSCUnitTestCase;
import com.rstar.mobile.csc205sp2015.utils.CourseUtils;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;
import com.rstar.mobile.csc205sp2015.utils.ModuleUtils;

import java.io.File;

/**
 * Created by AHui on 1/15/15.
 */
public class CourseTest extends CSCUnitTestCase {
    private static final String TAG = CourseTest.class.getSimpleName();

    Context targetContext = null;
    Context testContext = null;
    CourseFields.Constants constants = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new CourseFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }


    public void testGet() throws Exception {
        Course course = Course.get(targetContext);
        CourseFields.Variables variables = new CourseFields.Variables(course);
        verifyFields(variables, course);
    }

    public void testSetup() throws Exception {
        Savelog.i(TAG, "Test Setup");

        assertTrue(IO.isNetworkAvailable(targetContext));
        
        {   // Set up with good data.
            CourseUtils.DataSet info = new CourseUtils.DataSet(0);  // good data
            Course course = setupWithLink(info.courseLink);

            CourseFields.Variables variables = new CourseFields.Variables(course);
            verifyFields(variables, course);

            // Load the data file separately. Expect to get the same result
            File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);
            CourseUtils.CourseFileData fileData = new CourseUtils.CourseFileData(targetContext, destinationFile);

            assertEquals(variables.serverPasscode, fileData.serverPasscode);
            assertTrue(course.matchPasscode(fileData.serverPasscode));
            assertEquals(fileData.headerArrayList.size(), course.getNumberOfModules());
            assertTrue(course.isInstalled());
            verifyInstall(fileData, course);
            variables.detach();
            DataUtils.clearAll(targetContext);
        }

        {   // Set up with bad data.
            CourseUtils.DataSet info = new CourseUtils.DataSet(-1);  // bad data
            Course course = setupWithLink(info.courseLink);

            CourseFields.Variables variables = new CourseFields.Variables(course);
            verifyFields(variables, course);

            // Load the data file separately. Expect to get the same result
            File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);
            CourseUtils.CourseFileData fileData = new CourseUtils.CourseFileData(targetContext, destinationFile);

            assertEquals(variables.serverPasscode, fileData.serverPasscode);
            if (fileData.serverPasscode==null || fileData.serverPasscode.length()==0)
                assertFalse(course.matchPasscode(fileData.serverPasscode));
            else
                assertTrue(course.matchPasscode(fileData.serverPasscode));
            assertEquals(fileData.headerArrayList.size(), course.getNumberOfModules());
            assertFalse(course.isInstalled());
            verifyInstall(fileData, course);
            variables.detach();
            DataUtils.clearAll(targetContext);
        }

    }

    public void testReload1() throws Exception {
        // original has 1 module (A), reload one with 2 modules (A and B)
        // A has not been installed, there is no change because it's the same.
        Savelog.i(TAG, "Test reload. Old course has 1 uninstalled module A. New course has 2 modules A and B");

        boolean trim = true; // force to clear existing data of course
        File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);

        CourseUtils.DataSet oldSet = new CourseUtils.DataSet(1);
        Course course = setupWithLink(oldSet.courseLink);
        CourseUtils.CourseFileData oldData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        // no module installed

        CourseUtils.DataSet newSet = new CourseUtils.DataSet(2);
        CourseFields.Variables.setLink(course, newSet.courseLink);
        course.reload(targetContext, trim);
        CourseUtils.CourseFileData newData = new CourseUtils.CourseFileData(targetContext, destinationFile);
        verifyInstall(newData, course);

        verifyChanges(oldData, newData, course);
    }

    public void testReload2() throws Exception {
        // original has 1 module (A), reload one with 2 modules (A and B)
        // A has been installed without video, but there is no change because it's the same.
        Savelog.i(TAG, "Test reload. Old course has 1 installed module A. New course has 2 modules A and B");

        boolean trim = true; // force to clear existing data of course
        File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);

        CourseUtils.DataSet oldSet = new CourseUtils.DataSet(1);
        Course course = setupWithLink(oldSet.courseLink);
        CourseUtils.CourseFileData oldData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        // Install one module
        if (course.getNumberOfModules()>0) {
            int index = 0;
            int moduleNumber = oldData.headerArrayList.get(index).getModuleNumber();
            Module module = course.getModule(moduleNumber);
            module.setup(targetContext);
            // no video
        }

        CourseUtils.DataSet newSet = new CourseUtils.DataSet(2);
        CourseFields.Variables.setLink(course, newSet.courseLink);
        course.reload(targetContext, trim);
        CourseUtils.CourseFileData newData = new CourseUtils.CourseFileData(targetContext, destinationFile);
        verifyInstall(newData, course);
        verifyChanges(oldData, newData, course);
    }


    public void testReload3() throws Exception {
        // original has 1 module (A), reload one with 2 modules (A' and B)
        // A has not been installed, so there is no directory to be overwritten
        Savelog.i(TAG, "Test reload. Old course has 1 uninstalled module A. New course has 2 modules A' and B");

        boolean trim = true; // force to clear existing data of course
        File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);

        CourseUtils.DataSet oldSet = new CourseUtils.DataSet(1);
        Course course = setupWithLink(oldSet.courseLink);
        CourseUtils.CourseFileData oldData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        // no module installed

        CourseUtils.DataSet newSet = new CourseUtils.DataSet(3);
        CourseFields.Variables.setLink(course, newSet.courseLink);
        course.reload(targetContext, trim);
        CourseUtils.CourseFileData newData = new CourseUtils.CourseFileData(targetContext, destinationFile);
        verifyInstall(newData, course);
        verifyChanges(oldData, newData, course);
    }

    public void testReload4() throws Exception {
       // original has 1 module (A), reload one with 2 modules (A' and B)
        // A has been installed without video, so its directory must be overwritten
        Savelog.i(TAG, "Test reload. Old course has 1 installed module A. New course has 2 modules A' and B");

        boolean trim = true; // force to clear existing data of course
        File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);

        CourseUtils.DataSet oldSet = new CourseUtils.DataSet(1);
        Course course = setupWithLink(oldSet.courseLink);
        CourseUtils.CourseFileData oldData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        // Install one module
        if (course.getNumberOfModules()>0) {
            int index = 0;
            int moduleNumber = oldData.headerArrayList.get(index).getModuleNumber();
            Module module = course.getModule(moduleNumber);
            module.setup(targetContext);
            // no video
        }

        CourseUtils.DataSet newSet = new CourseUtils.DataSet(3);
        CourseFields.Variables.setLink(course, newSet.courseLink);
        course.reload(targetContext, trim);
        CourseUtils.CourseFileData newData = new CourseUtils.CourseFileData(targetContext, destinationFile);
        verifyInstall(newData, course);
        verifyChanges(oldData, newData, course);
    }

    public void testReload5() throws Exception {
        // original has 1 module (A), reload one with 2 modules (A' and B)
        // A has been installed with video, so its directory must be overwritten

        Savelog.i(TAG, "Test reload. Old course has 1 installed module A with video. New course has 2 modules A' and B");

        boolean trim = true; // force to clear existing data of course
        File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);

        CourseUtils.DataSet oldSet = new CourseUtils.DataSet(1);
        Course course = setupWithLink(oldSet.courseLink);
        CourseUtils.CourseFileData oldData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        // Install one module
        if (course.getNumberOfModules()>0) {
            int index = 0;
            int moduleNumber = oldData.headerArrayList.get(index).getModuleNumber();
            Module module = course.getModule(moduleNumber);
            module.setup(targetContext);

            // install video
            for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
                if (module.isVideoAvailable(pageNumber)) {
                    ModuleUtils.getVideoFromResource(testContext, targetContext, module, pageNumber, ModuleUtils.Samples.videoId);
                    Savelog.i(TAG, "obtained video for module " + moduleNumber + " page " + pageNumber);
                    break;
                }
            }
        }

        CourseUtils.DataSet newSet = new CourseUtils.DataSet(3);
        CourseFields.Variables.setLink(course, newSet.courseLink);

        course.reload(targetContext, trim);
        CourseUtils.CourseFileData newData = new CourseUtils.CourseFileData(targetContext, destinationFile);
        verifyInstall(newData, course);
        verifyChanges(oldData, newData, course);
    }

    public void testReload6() throws Exception {
        // original has 1 module (A) installed with video, reload one with 1 module (B)
        // A must be removed


        Savelog.i(TAG, "Test reload. Old course has 1 installed module A with video. New course has 1 module B");

        boolean trim = true; // force to clear existing data of course
        File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);

        CourseUtils.DataSet oldSet = new CourseUtils.DataSet(1);
        Course course = setupWithLink(oldSet.courseLink);
        CourseUtils.CourseFileData oldData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        // Install one module
        if (course.getNumberOfModules()>0) {
            int index = 0;
            int moduleNumber = oldData.headerArrayList.get(index).getModuleNumber();
            Module module = course.getModule(moduleNumber);
            module.setup(targetContext);

            // install video
            for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
                if (module.isVideoAvailable(pageNumber)) {
                    ModuleUtils.getVideoFromResource(testContext, targetContext, module, pageNumber, ModuleUtils.Samples.videoId);
                    Savelog.i(TAG, "obtained video for module " + moduleNumber + " page " + pageNumber);
                    break;
                }
            }
        }

        CourseUtils.DataSet newSet = new CourseUtils.DataSet(4);
        CourseFields.Variables.setLink(course, newSet.courseLink);
        course.reload(targetContext, trim);
        CourseUtils.CourseFileData newData = new CourseUtils.CourseFileData(targetContext, destinationFile);
        verifyInstall(newData, course);
        verifyChanges(oldData, newData, course);
    }

    public void testReload7() throws Exception {
        // original has 1 module (A) installed. Reload has the same module but a different passcode
        // No change to A
        Savelog.i(TAG, "Test reload. Old course has 1 installed module A. New course has 1 module A. Passcode changed");

        boolean trim = true; // force to clear existing data of course
        File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);

        CourseUtils.DataSet oldSet = new CourseUtils.DataSet(1);
        Course course = setupWithLink(oldSet.courseLink);
        CourseUtils.CourseFileData oldData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        // Install one module
        if (course.getNumberOfModules()>0) {
            int index = 0;
            int moduleNumber = oldData.headerArrayList.get(index).getModuleNumber();
            Module module = course.getModule(moduleNumber);
            module.setup(targetContext);
            // no video
        }

        CourseUtils.DataSet newSet = new CourseUtils.DataSet(5);
        CourseFields.Variables.setLink(course, newSet.courseLink);
        course.reload(targetContext, trim);
        CourseUtils.CourseFileData newData = new CourseUtils.CourseFileData(targetContext, destinationFile);
        verifyInstall(newData, course);
        verifyChanges(oldData, newData, course);
    }

    public void testClear() throws Exception {
        Savelog.i(TAG, "Test Clear");

        CourseUtils.DataSet info = new CourseUtils.DataSet(1);
        Course course = setupWithLink(info.courseLink);
        File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);
        CourseUtils.CourseFileData fileData = new CourseUtils.CourseFileData(targetContext, destinationFile);
        verifyInstall(fileData, course);

        course.clear(targetContext);
        CourseFields.Variables variables = new CourseFields.Variables(course);
        assertEquals(0, variables.moduleList.size());
        assertEquals(0, variables.moduleMap.size());
        assertEquals(0, variables.serverPasscode.length());
        assertFalse(destinationFile.exists());
    }

    public void testMatchPasscode() throws Exception {
        Savelog.i(TAG, "Test MatchPasscode");

        CourseUtils.DataSet info = new CourseUtils.DataSet(1);
        Course course = setupWithLink(info.courseLink);
        File destinationFile = IO.getInternalFile(targetContext, constants.CourseFilename);
        CourseUtils.CourseFileData fileData = new CourseUtils.CourseFileData(targetContext, destinationFile);
        verifyInstall(fileData, course);

        // Test when the course is installed
        assertFalse(course.matchPasscode(null));
        assertFalse(course.matchPasscode("bad"));
        assertFalse(course.matchPasscode(""));

        CourseFields.Variables variables = new CourseFields.Variables(course);
        if (variables.serverPasscode!=null && variables.serverPasscode.length()>0)
            assertTrue(course.matchPasscode(variables.serverPasscode));
        else
            assertFalse(course.matchPasscode(variables.serverPasscode));


        // Test when the course is uninstalled
        course.clear(targetContext);
        // Test when the course is installed
        assertFalse(course.matchPasscode(null));
        assertFalse(course.matchPasscode("bad"));
        assertFalse(course.matchPasscode(""));

        variables.refresh(course);
        assertFalse(course.matchPasscode(variables.serverPasscode));
    }




    private void verifyFields(CourseFields.Variables variables, Course course) throws Exception{
        // When the passcode is not set, the match always returns false.
        if (variables.serverPasscode==null || variables.serverPasscode.length()==0)
            assertFalse(course.matchPasscode(variables.serverPasscode));
        else
            assertTrue(course.matchPasscode(variables.serverPasscode));

        assertEquals(variables.moduleList.size(), course.getNumberOfModules());
        for (int index=0; index<variables.moduleList.size(); index++) {
            Module module = variables.moduleList.get(index);
            int moduleNumber = module.getModuleNumber();
            Module mappedModule = course.getModule(moduleNumber);
            assertEquals(module, mappedModule);
        }
    }

    private void verifyInstall(CourseUtils.CourseFileData fileData, Course course) throws Exception {
        Savelog.i(TAG, "verifyInstall now");
        // Compare passcode
        if (fileData.serverPasscode==null || fileData.serverPasscode.length()==0)
            assertFalse(course.matchPasscode(fileData.serverPasscode));
        else
            assertTrue(course.matchPasscode(fileData.serverPasscode));

        // course must follow the fileData if install is successful
        for (int index=0; index<fileData.headerArrayList.size(); index++) {

            // basic information is available from header. Module's header should be consistent with fileData's header
            Header header = fileData.headerArrayList.get(index);
            int moduleNumber = header.getModuleNumber();

            Savelog.i(TAG, "Checking module " + moduleNumber);
            Module module = course.getModule(moduleNumber);

            // First, check that the header inside the module matches the header obtained from file
            verifyModuleHeader(TAG, module, header);
            // Then, check that the module is consistent with its header
            checkValidity(TAG, targetContext, module);
        }
    }

    private void verifyChanges(CourseUtils.CourseFileData oldData, CourseUtils.CourseFileData newData, Course course) {
        // course must follow the fileData if install is successful
        for (int index = 0; index < oldData.headerArrayList.size(); index++) {

            // basic information is available from header. Module's header should be consistent with newData's header
            Header header = oldData.headerArrayList.get(index);
            int moduleNumber = header.getModuleNumber();

            boolean found = false;
            for (int j=0; j<newData.headerArrayList.size(); j++) {
                Header jHeader = newData.headerArrayList.get(j);
                if (jHeader.getModuleNumber()==moduleNumber) {
                    // Found module in newData.
                    found = true;

                    // If the old and the new are different, then expect the module to be uninstalled.
                    Module module = course.getModule(moduleNumber);
                    if (!header.toString().equals(jHeader.toString())) {
                        assertFalse(module.isInstalled());

                        // Not expect to find search result for this module
                        assertFalse(ModuleUtils.isModuleInSQLite(targetContext, moduleNumber));

                        Savelog.i(TAG, "Module " + moduleNumber + " has changed and is cleared.");
                    }
                    else {
                        // We don't care whether the module is installed, since the two headers are the same.
                        Savelog.i(TAG, "Module " + moduleNumber + " has not changed.");
                    }
                }
            }

            if (!found) {
                Module module = course.getModule(moduleNumber);
                assertNull(module);
                // Not expect to find directory for this module.
                File dirname = IO.getInternalDir(targetContext, Module.getModuleDirname(moduleNumber));
                assertTrue(dirname==null || !dirname.exists());

                // Not expect to find search result for this module
                assertFalse(ModuleUtils.isModuleInSQLite(targetContext, moduleNumber));

                Savelog.i(TAG, "Module " + moduleNumber + " has been removed and is cleared.");
            }
        }
    }


    private Course setupWithLink(String link) throws Exception {
        Course course = Course.get(targetContext);
        CourseFields.Variables.setLink(course, link);
        course.setup(targetContext);
        return course;
    }

}
