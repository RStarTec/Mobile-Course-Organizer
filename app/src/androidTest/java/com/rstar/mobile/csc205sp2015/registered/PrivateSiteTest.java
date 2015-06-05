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

import com.rstar.mobile.csc205sp2015.fields.PrivateSiteFields;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;
import com.rstar.mobile.csc205sp2015.utils.PrivateSiteUtils;

import java.io.File;

/**
 * Created by AHui on 1/22/15.
 */
public class PrivateSiteTest extends InstrumentationTestCase {
    private static final String TAG = PrivateSiteTest.class.getSimpleName();

    Context targetContext = null;
    Context testContext = null;
    PrivateSiteFields.Constants constants = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new PrivateSiteFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }


    public void testGet() throws Exception {
        {   // There is NO file that contains a valid privateLink
            PrivateSite privateSite = PrivateSite.get(targetContext);
            PrivateSiteFields.Variables variables = new PrivateSiteFields.Variables(privateSite);
            verifyFields(variables, privateSite);

            // Check that the PrivateSite is empty
            assertEquals("", variables.privateLink);
            // Check that there is no privateLink file
            File linkFile = IO.getInternalFile(targetContext, constants.SiteFilename);
            assertFalse(linkFile.exists());
            privateSite.clear(targetContext);
        }
    }


    public void testNoDownload() throws Exception {
        {   // PrivateSet is already initialized
            PrivateSiteUtils.DataSet dataSet = new PrivateSiteUtils.DataSet(targetContext, -2);

            // Obtain the PrivateSite. Redirect the publicLink to a test file
            PrivateSite privateSite = PrivateSite.get(targetContext);
            PrivateSiteFields.Variables.setPublicLink(privateSite, dataSet.privateSiteLink);

            PrivateSiteFields.Variables variables = new PrivateSiteFields.Variables(privateSite);
            verifyFields(variables, privateSite);

            // Now download the privateLink file
            PrivateSiteFields.Variables.setPublicLink(privateSite, dataSet.privateSiteLink);
            privateSite.setup(targetContext);
            variables.refresh(privateSite);
            verifyFields(variables, privateSite);

            // Verify that there has been a download
            PrivateSiteUtils.PrivateSiteFileData fileData = new PrivateSiteUtils.PrivateSiteFileData(targetContext);
            assertEquals(fileData.privateLink, variables.privateLink);
            assertTrue(dataSet.privateSiteFile.exists());

            String oldLink = variables.privateLink;

            // Now redirect the publicLink to a different test file
            PrivateSiteUtils.DataSet dataSet2 = new PrivateSiteUtils.DataSet(targetContext, 0);
            PrivateSiteFields.Variables.setPublicLink(privateSite, dataSet2.privateSiteLink);

            // Now call download again without clearing existing data
            privateSite.setup(targetContext);
            variables.refresh(privateSite);
            verifyFields(variables, privateSite);

            // Verify that there has been NO download
            assertEquals(oldLink, variables.privateLink);
            assertTrue(dataSet.privateSiteFile.exists());
            PrivateSiteUtils.PrivateSiteFileData fileData2 = new PrivateSiteUtils.PrivateSiteFileData(targetContext);
            assertEquals(fileData2.privateLink, variables.privateLink);
        }
    }

    public void testDownload() throws Exception {
        // Download requires that the privateSite is not initialized

        {   // set -1
            PrivateSiteUtils.DataSet dataSet = new PrivateSiteUtils.DataSet(targetContext, -1);

            // Obtain the PrivateSite. Redirect the publicLink to a test file
            PrivateSite privateSite = PrivateSite.get(targetContext);
            PrivateSiteFields.Variables.setPublicLink(privateSite, dataSet.privateSiteLink);

            PrivateSiteFields.Variables variables = new PrivateSiteFields.Variables(privateSite);
            verifyFields(variables, privateSite);

            // Now download the privateLink file
            privateSite.clear(targetContext); // must clear first
            privateSite.setup(targetContext);
            variables.refresh(privateSite);
            verifyFields(variables, privateSite);

            PrivateSiteUtils.PrivateSiteFileData fileData = new PrivateSiteUtils.PrivateSiteFileData(targetContext);
            assertEquals(fileData.privateLink, variables.privateLink);
            assertEquals("", variables.privateLink);
            assertTrue(dataSet.privateSiteFile.exists());
        }

        {   // set -2
            PrivateSiteUtils.DataSet dataSet = new PrivateSiteUtils.DataSet(targetContext, -2);

            // Obtain the PrivateSite. Redirect the publicLink to a test file
            PrivateSite privateSite = PrivateSite.get(targetContext);
            PrivateSiteFields.Variables.setPublicLink(privateSite, dataSet.privateSiteLink);

            PrivateSiteFields.Variables variables = new PrivateSiteFields.Variables(privateSite);
            verifyFields(variables, privateSite);

            // Now download the privateLink file
            privateSite.clear(targetContext); // must clear first
            privateSite.setup(targetContext);
            variables.refresh(privateSite);
            verifyFields(variables, privateSite);

            PrivateSiteUtils.PrivateSiteFileData fileData = new PrivateSiteUtils.PrivateSiteFileData(targetContext);
            assertEquals(fileData.privateLink, variables.privateLink);
            assertTrue(dataSet.privateSiteFile.exists());
        }

        {   // set -3
            PrivateSiteUtils.DataSet dataSet = new PrivateSiteUtils.DataSet(targetContext, -3);

            // Obtain the PrivateSite. Redirect the publicLink to a test file
            PrivateSite privateSite = PrivateSite.get(targetContext);
            PrivateSiteFields.Variables.setPublicLink(privateSite, dataSet.privateSiteLink);

            PrivateSiteFields.Variables variables = new PrivateSiteFields.Variables(privateSite);
            verifyFields(variables, privateSite);

            // Now download the privateLink file
            privateSite.clear(targetContext); // must clear first
            privateSite.setup(targetContext);
            variables.refresh(privateSite);
            verifyFields(variables, privateSite);

            PrivateSiteUtils.PrivateSiteFileData fileData = new PrivateSiteUtils.PrivateSiteFileData(targetContext);
            assertEquals(fileData.privateLink, variables.privateLink);
            assertTrue(dataSet.privateSiteFile.exists());
        }

        {   // set 0
            PrivateSiteUtils.DataSet dataSet = new PrivateSiteUtils.DataSet(targetContext, 0);

            // Obtain the PrivateSite. Redirect the publicLink to a test file
            PrivateSite privateSite = PrivateSite.get(targetContext);
            PrivateSiteFields.Variables.setPublicLink(privateSite, dataSet.privateSiteLink);

            PrivateSiteFields.Variables variables = new PrivateSiteFields.Variables(privateSite);
            verifyFields(variables, privateSite);

            // Now download the privateLink file
            privateSite.clear(targetContext); // must clear first
            privateSite.setup(targetContext);
            variables.refresh(privateSite);
            verifyFields(variables, privateSite);

            PrivateSiteUtils.PrivateSiteFileData fileData = new PrivateSiteUtils.PrivateSiteFileData(targetContext);
            assertEquals(fileData.privateLink, variables.privateLink);
            assertTrue(dataSet.privateSiteFile.exists());
        }
    }



    private PrivateSiteUtils.PrivateSiteFileData preloadLinkFile(PrivateSiteUtils.DataSet dataSet) throws Exception {
        File linkFile = IO.getInternalFile(targetContext, constants.SiteFilename);
        IO.downloadFile(dataSet.privateSiteLink, linkFile);
        PrivateSiteUtils.PrivateSiteFileData fileData = new PrivateSiteUtils.PrivateSiteFileData(targetContext);
        return fileData;
    }


    private void verifyFields(PrivateSiteFields.Variables variables, PrivateSite privateSite) throws Exception{
        String linkData = "";

        File dataFile = IO.getInternalFile(targetContext, constants.SiteFilename);
        if (dataFile!=null && dataFile.exists()) {
            linkData = IO.loadFileAsString(targetContext, dataFile);
            assertEquals(linkData.trim(), variables.privateLink);
        }
        else {
            assertEquals(0, variables.privateLink.length());
        }

        if (variables.privateLink !=null && variables.privateLink.length()>0) {
            assertEquals(variables.privateLink +constants.HomeworkApi, privateSite.getHomeworkApi());
            assertEquals(variables.privateLink +constants.LoginApi, privateSite.getLoginApi());
            assertEquals(variables.privateLink +constants.ScoreApi, privateSite.getScoreApi());
            assertEquals(variables.privateLink +constants.MasterApi, privateSite.getMasterApi());
            assertTrue(privateSite.isInitialized());
        }
        else {
            assertEquals("", privateSite.getHomeworkApi());
            assertEquals("", privateSite.getLoginApi());
            assertEquals("", privateSite.getScoreApi());
            assertEquals("", privateSite.getMasterApi());
            assertFalse(privateSite.isInitialized());
        }

        // A static
        assertEquals(constants.SiteId, PrivateSite.getSiteId());
    }

}
