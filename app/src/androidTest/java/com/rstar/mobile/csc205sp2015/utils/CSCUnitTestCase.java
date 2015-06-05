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

package com.rstar.mobile.csc205sp2015.utils;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.fields.ModuleFields;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Header;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.registered.login.Student;
import com.rstar.mobile.csc205sp2015.search.Search;

import java.util.ArrayList;

/**
 * Created by AHui on 1/16/15.
 */
public abstract class CSCUnitTestCase extends InstrumentationTestCase {

    // Put here all the functions that require instrumentation (such as all the assert statements)


    protected void checkValidity(String TAG, Header header) {
        assertNotNull(header);
        assertNotNull(header.getTitle());
        assertNotNull(header.getOnlineDirectory());
        assertNotNull(header.getOnlinePackageName());
        assertNotNull(header.getDownloadOptions());
        assertTrue(header.getNumberOfPages()>=0);
        assertTrue(header.getNumberOfAudios()>=0);
        assertTrue(header.getNumberOfTranscripts()>=0);
        assertTrue(header.getNumberOfExtras()>=0);
        assertTrue(header.getNumberOfQuizzes()>=0);
        assertTrue(header.getNumberOfVideos()>=0);
        assertNotNull(header.getTime());
        Savelog.i(TAG, "header is valid");
    }



    protected void verifyModuleHeader(String TAG, Module module, Header header) throws Exception {
        ModuleFields.Variables variables = new ModuleFields.Variables(module);
        assertNotNull(variables.header);
        checkValidity(TAG, variables.header);
        assertTrue(variables.header.equals(header));

        assertEquals(header.getModuleNumber(), module.getModuleNumber());
        assertEquals(header.getNumberOfPages(), module.getNumberOfPages());
        assertEquals(header.getTitle(), module.getTitle());
        assertEquals(header.getTime(), module.getLastUpdateDate());
        assertEquals(header.toString(), module.getHeaderDescription());
    }

    protected void checkValidity(String TAG, Context targetContext, Module module) throws Exception {
        ModuleFields.Variables variables = new ModuleFields.Variables(module);

        assertNotNull(variables.header);

        // Check the privateLink
        assertNotNull(variables.link);
        assertTrue(variables.link.startsWith(AppSettings.PublicSite));
        assertTrue(variables.link.contains(variables.header.getOnlineDirectory()));
        assertTrue(variables.link.contains(variables.header.getOnlinePackageName()));
        assertTrue(variables.link.contains(variables.header.getDownloadOptions()));

        // Check the videolist
        if (module.isInstalled()) {
            Savelog.i(TAG, "module " + module.getModuleNumber() + " is installed. Expecting videolist.");
            assertNotNull(variables.videoList);
        }
        else {
            Savelog.i(TAG, "module " + module.getModuleNumber() + " is not installed. Expecting NO videolist.");
            assertNull(variables.videoList);
        }

        // Check the header
        Header header = variables.header;
        assertNotNull(header);

        assertEquals(header.getModuleNumber(), module.getModuleNumber());
        assertEquals(header.getNumberOfPages(), module.getNumberOfPages());
        assertEquals(header.getTitle(), module.getTitle());
        assertEquals(header.getTime(), module.getLastUpdateDate());
        assertEquals(header.toString(), module.getHeaderDescription());

        // if the module has not been installed, the module's directory should not exist
        ModuleUtils.ModuleFiles moduleFiles = new ModuleUtils.ModuleFiles(targetContext, module);
        assertEquals(moduleFiles.isDirExists(), module.isInstalled());

        // if the module is installed, then the number of files in directory should be consistent with header
        if (moduleFiles.isDirExists()) {

            assertEquals(header.getNumberOfPages(), moduleFiles.getNumberOfSlides());
            assertEquals(header.getNumberOfAudios(), moduleFiles.getNumberOfAudios());
            assertEquals(header.getNumberOfTranscripts(), moduleFiles.getNumberOfTranscripts());
            assertEquals(header.getNumberOfQuizzes(), moduleFiles.getNumberOfQuizzes());
            assertEquals(header.getNumberOfExtras(), moduleFiles.getNumberOfExtras());

            Savelog.i(TAG, "Module " + module.getModuleNumber() + " is installed with contents consistent with: " + header.toString());

            if (header.getNumberOfTranscripts()>0) {
                // sqlite exists. Try to find something. Expect result.
                Search search = new Search(targetContext);
                ArrayList<Search.Item> result = search.getSegmentMatching("a", module.getModuleNumber());
                assertTrue(result.size()>0);
            }

            // Video may not be installed.
            if (moduleFiles.getNumberOfVideos()!=0) {
                assertTrue(header.getNumberOfVideos() >= moduleFiles.getNumberOfVideos());
                // TODO: check the videolist to verify the files are correct
            }
            Savelog.i(TAG, "Module " + module.getModuleNumber()  + " has videos: " + moduleFiles.getNumberOfVideos());
        }
        else {
            Savelog.i(TAG, "Module " + module.getModuleNumber() + " is NOT installed");
        }
    }





    protected void checkLoginValidity(Context targetContext, Student student) {
        Student.Login login = student.loadLogin(targetContext);
        if (login==null) {
            assertTrue(student.isLoginExpired(targetContext));
        }
        else {
            assertNotNull(login.getUserId());
            assertNotNull(login.getPassword());
            if (login.isValid())
                assertFalse(student.isLoginExpired(targetContext));
            else
                assertTrue(student.isLoginExpired(targetContext));
        }
    }

}
