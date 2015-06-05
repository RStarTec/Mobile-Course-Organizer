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

package com.rstar.mobile.csc205sp2015.module;

import android.content.Context;

import com.rstar.mobile.csc205sp2015.fields.HeaderFields;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.utils.CSCUnitTestCase;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;

/**
 * Created by AHui
 */
public class HeaderTest extends CSCUnitTestCase {
    private static final String TAG = HeaderTest.class.getSimpleName()+"_class";
    Context targetContext = null;
    Context testContext = null;
    HeaderFields.Constants constants = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new HeaderFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }


    public void testConstructor() {
        try {
            Header header = new Header("");
            fail("Should fail");
        }
        catch (Exception e) {}

        try {
            Header header = new Header("bad");
            fail("Should fail");
        }
        catch (Exception e) {}
    }



    public void testEquals() {
        try {
            Header h1 = makeHeader1();
            Header h2 = null;
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            assertTrue(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change title
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setTitle("new" + h1.getTitle());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change moduleNumber
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setModuleNumber(1 + h1.getModuleNumber());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change onlineDirectory
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setOnlineDirectory("new" + h1.getOnlineDirectory());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change onlinePackageName
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setOnlinePackageName("new" + h1.getOnlinePackageName());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change downloadOptions
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setDownloadOptions("new" + h1.getDownloadOptions());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change numberOfPages
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfPages(1 + h1.getNumberOfPages());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change numberOfAudios
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfAudios(1 + h1.getNumberOfAudios());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change numberOfTranscripts
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfTranscripts(1 + h1.getNumberOfTranscripts());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change numberOfExtras
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfExtras(1 + h1.getNumberOfExtras());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change numberOfQuizzes
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfQuizzes(1 + h1.getNumberOfQuizzes());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change numberOfVideos
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfVideos(1 + h1.getNumberOfVideos());
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            // change time
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setTime("newTime");
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}

        try {
            Header h1 = makeHeader1();
            Header h2 = makeHeader2();
            assertFalse(h1.equals(h2));
        }
        catch (Exception e) { fail("Should not fail");}
    }



    public void testSetBad() throws Exception {
        {   // bad title
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setTitle(null);
            assertEquals("", h2.getTitle());
        }
        {   // bad moduleNumber (accepted)
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            int anyModuleNumber = -1;
            h2.setModuleNumber(anyModuleNumber);
            assertEquals(anyModuleNumber, h2.getModuleNumber());
        }
        {   // bad onlineDirectory
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setOnlineDirectory(null);
            assertEquals("", h2.getOnlineDirectory());
        }
        {   // bad onlinePackageName
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setOnlinePackageName(null);
            assertEquals("", h2.getOnlinePackageName());
        }
        {   // bad downloadOptions
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setDownloadOptions(null);
            assertEquals("", h2.getDownloadOptions());
        }
        {   // bad numberOfPages
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfPages(-1);
            assertEquals(0, h2.getNumberOfPages());
        }
        {   // bad numberOfAudios
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfAudios(-1);
            assertEquals(0, h2.getNumberOfAudios());
        }
        {   // bad numberOfTranscripts
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfTranscripts(-1);
            assertEquals(0, h2.getNumberOfTranscripts());
        }
        {   // bad numberOfExtras
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfExtras(-1);
            assertEquals(0, h2.getNumberOfExtras());
        }
        {   // bad numberOfQuizzes
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfQuizzes(-1);
            assertEquals(0, h2.getNumberOfQuizzes());
        }
        {   // bad numberOfVideos
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfVideos(-1);
            assertEquals(0, h2.getNumberOfVideos());
        }
        {   // bad time
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setTime(null);
            assertEquals("", h2.getTime());
        }
    }


    public void testSetGood() throws Exception {
        String empty = "";
        int zero = 0;
        {   // good title
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setTitle(empty);
            assertEquals(empty, h2.getTitle());
        }
        {   // good moduleNumber (accepted)
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setModuleNumber(zero);
            assertEquals(zero, h2.getModuleNumber());
        }
        {   // good onlineDirectory
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setOnlineDirectory(empty);
            assertEquals(empty, h2.getOnlineDirectory());
        }
        {   // good onlinePackageName
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setOnlinePackageName(empty);
            assertEquals(empty, h2.getOnlinePackageName());
        }
        {   // good downloadOptions
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setDownloadOptions(empty);
            assertEquals(empty, h2.getDownloadOptions());
        }
        {   // good numberOfPages
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfPages(zero);
            assertEquals(zero, h2.getNumberOfPages());
        }
        {   // good numberOfAudios
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfAudios(zero);
            assertEquals(zero, h2.getNumberOfAudios());
        }
        {   // good numberOfTranscripts
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfTranscripts(zero);
            assertEquals(zero, h2.getNumberOfTranscripts());
        }
        {   // good numberOfExtras
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfExtras(zero);
            assertEquals(zero, h2.getNumberOfExtras());
        }
        {   // good numberOfQuizzes
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfQuizzes(zero);
            assertEquals(zero, h2.getNumberOfQuizzes());
        }
        {   // good numberOfVideos
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setNumberOfVideos(zero);
            assertEquals(zero, h2.getNumberOfVideos());
        }
        {   // good time
            Header h1 = makeHeader1();
            Header h2 = new Header(h1);
            h2.setTime(empty);
            assertEquals(empty, h2.getTime());
        }
    }




    private Header makeHeader1() throws Exception {
        String title = "dummy";
        int moduleNumber = 1;
        String onlineDirectory = "onlineDir";
        String onlinePackageName = "package";
        String downloadOptions = "download";
        int numberOfPages = 20;
        int numberOfAudios = 10;
        int numberOfTranscripts = 10;
        int numberOfExtras = 4;
        int numberOfQuizzes = 5;
        int numberOfVideos = 2;
        String time = "timenow";

        String record = "" + moduleNumber + constants.fieldDelimiter;
        record += title + constants.fieldDelimiter;
        record += onlineDirectory + constants.fieldDelimiter;
        record += onlinePackageName + constants.fieldDelimiter;
        record += downloadOptions + constants.fieldDelimiter;
        record += "" + numberOfPages + constants.fieldDelimiter;
        record += "" + numberOfAudios + constants.fieldDelimiter;
        record += "" + numberOfTranscripts + constants.fieldDelimiter;
        record += "" + numberOfExtras + constants.fieldDelimiter;
        record += "" + numberOfQuizzes + constants.fieldDelimiter;
        record += "" + numberOfVideos + constants.fieldDelimiter;
        record += time;

        Header header = new Header(record);
        Savelog.i(TAG, "Successfully created a dummy header 1");

        assertEquals(title, header.getTitle());
        assertEquals(moduleNumber, header.getModuleNumber());
        assertEquals(onlineDirectory, header.getOnlineDirectory());
        assertEquals(onlinePackageName, header.getOnlinePackageName());
        assertEquals(downloadOptions, header.getDownloadOptions());
        assertEquals(numberOfPages, header.getNumberOfPages());
        assertEquals(numberOfAudios, header.getNumberOfAudios());
        assertEquals(numberOfTranscripts, header.getNumberOfTranscripts());
        assertEquals(numberOfExtras, header.getNumberOfExtras());
        assertEquals(numberOfQuizzes, header.getNumberOfQuizzes());
        assertEquals(numberOfVideos, header.getNumberOfVideos());
        assertEquals(time, header.getTime());
        assertEquals(record, header.toString());
        return header;
    }


    private Header makeHeader2() throws Exception {
        String title = "dummy2";
        int moduleNumber = 200;
        String onlineDirectory = "onlineDir2";
        String onlinePackageName = "package2";
        String downloadOptions = "download2";
        int numberOfPages = 100;
        int numberOfAudios = 14;
        int numberOfTranscripts = 0;
        int numberOfExtras = 2;
        int numberOfQuizzes = 3;
        int numberOfVideos = 4;
        String time = "timenow2";

        String record = "" + moduleNumber + constants.fieldDelimiter;
        record += title + constants.fieldDelimiter;
        record += onlineDirectory + constants.fieldDelimiter;
        record += onlinePackageName + constants.fieldDelimiter;
        record += downloadOptions + constants.fieldDelimiter;
        record += "" + numberOfPages + constants.fieldDelimiter;
        record += "" + numberOfAudios + constants.fieldDelimiter;
        record += "" + numberOfTranscripts + constants.fieldDelimiter;
        record += "" + numberOfExtras + constants.fieldDelimiter;
        record += "" + numberOfQuizzes + constants.fieldDelimiter;
        record += "" + numberOfVideos + constants.fieldDelimiter;
        record += time;

        Header header = new Header(record);

        assertEquals(title, header.getTitle());
        assertEquals(moduleNumber, header.getModuleNumber());
        assertEquals(onlineDirectory, header.getOnlineDirectory());
        assertEquals(onlinePackageName, header.getOnlinePackageName());
        assertEquals(downloadOptions, header.getDownloadOptions());
        assertEquals(numberOfPages, header.getNumberOfPages());
        assertEquals(numberOfAudios, header.getNumberOfAudios());
        assertEquals(numberOfTranscripts, header.getNumberOfTranscripts());
        assertEquals(numberOfExtras, header.getNumberOfExtras());
        assertEquals(numberOfQuizzes, header.getNumberOfQuizzes());
        assertEquals(numberOfVideos, header.getNumberOfVideos());
        assertEquals(time, header.getTime());
        assertEquals(record, header.toString());
        return header;
    }
}
