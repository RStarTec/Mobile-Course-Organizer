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

import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.fields.CourseFields;
import com.rstar.mobile.csc205sp2015.fields.ModuleFields;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.utils.CSCUnitTestCase;
import com.rstar.mobile.csc205sp2015.utils.CourseUtils;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;
import com.rstar.mobile.csc205sp2015.utils.ModuleUtils;

import java.io.File;

/**
 * Created by AHui
 */
public class ModuleTest extends CSCUnitTestCase {
    private static final String TAG = ModuleTest.class.getSimpleName()+"_class";

    // TODO: later, replace these labels with constants from Module
    // Since the module does not check the page numbers and module numbers, the filenames may contain minus sign
    private static final String patternDir = Module.ModuleLabel + "-*" + "[0-9]+\\/";
    private static final String patternArchive = Module.ModuleLabel + "-*" + "[0-9]+\\.zip";


    Context targetContext = null;
    Context testContext = null;
    ModuleFields.Constants constants = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new ModuleFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }

    public void testConstructor1() {
        try {
            Module module = new Module(targetContext, null);
            assertFalse(module.isInstalled());
            checkValidity(TAG, targetContext, module);
            assertEquals(0, module.getModuleNumber());
            assertEquals(0, module.getNumberOfPages());
            assertEquals("", module.getTitle());
            assertEquals(Header.NoDate, module.getLastUpdateDate());
        }
        catch (Exception e) { fail("Not expected to fail");}
    }

    public void testConstructor2() throws Exception {
        CourseUtils.DataSet dataSet = new CourseUtils.DataSet(2);   // Use set 2 because it has two modules

        // Call course to obtain the info file
        Course course = Course.get(targetContext);
        CourseFields.Variables.setLink(course, dataSet.courseLink);
        course.setup(targetContext);

        // Load a course data file to get one header.
        CourseFields.Constants constants_course = new CourseFields.Constants();
        File destinationFile = IO.getInternalFile(targetContext, constants_course.CourseFilename);
        CourseUtils.CourseFileData courseFileData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        // Load every module in that course
        for (int index=0; index<courseFileData.headerArrayList.size(); index++) {
            Header header = courseFileData.headerArrayList.get(index);
            Module module = new Module(targetContext, header);
            // Check that the module is consistent
            verifyModuleHeader(TAG, module, header);
            checkValidity(TAG, targetContext, module);
        }
        constants_course.detach();
    }



    public void testSetup() throws Exception {
        CourseUtils.DataSet dataSet = new CourseUtils.DataSet(2);   // Use set 2 because it has two modules

        // Call course to obtain the info file
        Course course = Course.get(targetContext);
        CourseFields.Variables.setLink(course, dataSet.courseLink);
        course.setup(targetContext);

        // Load a course data file to get one header.
        CourseFields.Constants constants_course = new CourseFields.Constants();
        File destinationFile = IO.getInternalFile(targetContext, constants_course.CourseFilename);
        CourseUtils.CourseFileData courseFileData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        // Load every module in that course
        for (int index=0; index<courseFileData.headerArrayList.size(); index++) {
            Header header = courseFileData.headerArrayList.get(index);
            Module module = new Module(targetContext, header);
            // Check that the module is consistent
            verifyModuleHeader(TAG, module, header);
            checkValidity(TAG, targetContext, module);

            // make the archive available
            // must download
            module.setup(targetContext);
            checkValidity(TAG, targetContext, module);
            assertTrue(module.isInstalled());
        }
        constants_course.detach();
    }


    public void testModuleFilenames() throws Exception {
        Module module = getSampleModuleNoSetup();
        verifyModuleFilenames(module);
    }



    public void testIsInstalled1() throws Exception {
        // A module that is created without setup is uninstalled.
        // It does not have a directory by default
        Module module = getSampleModuleNoSetup();
        File moduleDir = module.getModuleDir(targetContext);
        assertFalse(moduleDir.exists());
        assertFalse(module.isInstalled());
    }

    public void testIsInstalled2() throws Exception {
        // A module that is created without setup is uninstalled.
        // If a directory happens to pre-exist the module,
        // the content of the directory may or may not agree with the module
        Module module = getSampleModuleNoSetup();
        File moduleDir = module.getModuleDir(targetContext);
        moduleDir.mkdir();
        assertTrue(moduleDir.exists());
        assertFalse(module.checkInstallStatus(targetContext));
        assertFalse(module.isInstalled());
    }

    public void testIsInstalled3() throws Exception {
        Module module = getSampleModuleSetup();
        File moduleDir = module.getModuleDir(targetContext);
        assertTrue(moduleDir.exists());
        assertTrue(module.checkInstallStatus(targetContext));
        assertTrue(module.isInstalled());
    }

    public void testIsInstalled4() throws Exception {
        // Try to delete exactly one type of data file from the package.
        // Check that the module can detect a missing file and report uninstalled
        Module module = getSampleModuleSetup();
        File moduleDir = module.getModuleDir(targetContext);

        // Try removing a slide
        boolean modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File slideFile = module.getSlideFile(targetContext, pageNumber);
            if (slideFile.exists()) {
                slideFile.delete();
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                modified = true;
                break;
            }
        }

        // Try removing an audio
        if (modified) { // re-install everything
            module = getSampleModuleSetup();
        }
        modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File audioFile = module.getAudioFile(targetContext, pageNumber);
            if (audioFile.exists()) {
                audioFile.delete();
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                modified = true;
                break;
            }
        }

        // Try removing a transcript
        if (modified) { // re-install everything
            module = getSampleModuleSetup();
        }
        modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File transcriptFile = module.getTranscriptFile(targetContext, pageNumber);
            if (transcriptFile.exists()) {
                transcriptFile.delete();
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                modified = true;
                break;
            }
        }



        // Try removing an extra
        if (modified) { // re-install everything
            module = getSampleModuleSetup();
        }
        modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File extrasFile = module.getExtrasFile(targetContext, pageNumber);
            if (extrasFile.exists()) {
                extrasFile.delete();
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                modified = true;
                break;
            }
        }


        // Try removing a quiz
        if (modified) { // re-install everything
            module = getSampleModuleSetup();
        }
        modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File quizFile = module.getQuizFile(targetContext, pageNumber);
            if (quizFile.exists()) {
                quizFile.delete();
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                modified = true;
                break;
            }
        }

        // Try removing a video
        // NOTE: that the number of videos do not matter as long as they are consistent
        if (modified) { // re-install everything
            module = getSampleModuleSetup();
        }
        modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File videoFile = module.getVideoFile(targetContext, pageNumber);
            if (videoFile.exists()) {
                videoFile.delete();
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                modified = true;
                break;
            }
        }
    }


    public void testIsInstalled5() throws Exception {
        // Try to add exactly one type of data file into the package.
        // Check that the module can detect a new file and report uninstalled
        Module module = getSampleModuleSetup();
        File moduleDir = module.getModuleDir(targetContext);

        // Try adding a slide
        boolean modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File slideFile = module.getSlideFile(targetContext, pageNumber);
            if (!slideFile.exists()) {
                ModuleUtils.getSampleSlideFromResource(testContext, targetContext, module, pageNumber);
                modified = true;
                // Now there is a new file that does not belong, the module should detect it
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                break;
            }
        }


        // Try adding an audio
        if (modified) { // re-install everything
            module = getSampleModuleSetup();
        }
        modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File audioFile = module.getAudioFile(targetContext, pageNumber);
            if (!audioFile.exists()) {
                ModuleUtils.getSampleAudioFromResource(testContext, targetContext, module, pageNumber);
                modified = true;
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                break;
            }
        }

        // Try adding a transcript
        if (modified) { // re-install everything
            module = getSampleModuleSetup();
        }
        modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File transcriptFile = module.getTranscriptFile(targetContext, pageNumber);
            if (!transcriptFile.exists()) {
                ModuleUtils.getSampleTranscriptFromResource(testContext, targetContext, module, pageNumber);
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                modified = true;
                break;
            }
        }



        // Try adding an extra
        if (modified) { // re-install everything
            module = getSampleModuleSetup();
        }
        modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File extrasFile = module.getExtrasFile(targetContext, pageNumber);
            if (!extrasFile.exists()) {
                ModuleUtils.getSampleExtrasFromResource(testContext, targetContext, module, pageNumber);
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                modified = true;
                break;
            }
        }


        // Try adding a quiz
        if (modified) { // re-install everything
            module = getSampleModuleSetup();
        }
        modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File quizFile = module.getQuizFile(targetContext, pageNumber);
            if (!quizFile.exists()) {
                ModuleUtils.getSampleQuizFromResource(testContext, targetContext, module, pageNumber);
                assertTrue(moduleDir.exists());
                assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                modified = true;
                break;
            }
        }


        // Try adding a video
        // NOTE: that the number of videos do not matter as long as they are consistent
        if (modified) { // re-install everything
            module = getSampleModuleSetup();
        }
        modified = false;
        for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
            File videoFile = module.getVideoFile(targetContext, pageNumber);
            if (!videoFile.exists()) {
                assertTrue(moduleDir.exists());
                assertTrue(module.checkInstallStatus(targetContext)); // status computed on-the-fly
                assertTrue(module.isInstalled()); // the install status is cached is not updated
                modified = true;
                break;
            }
        }
    }


    public void testIsInstalled6() throws Exception {
        // Try to add exactly one type of data file into the package.
        // Check that the module ignores a new file beyond the numberOfPages
        Module module = getSampleModuleSetup();
        File moduleDir = module.getModuleDir(targetContext);
        File newFile;

        // add one at the end
        newFile = new File(moduleDir, Module.getSlideName(module.getNumberOfPages() + 1));
        DataUtils.copyRawFileToInternal(testContext, targetContext, ModuleUtils.Samples.slideId, newFile);
        // Now there is a new file that does not belong, the module does not care about it
        // since it will never access it.
        assertTrue(moduleDir.exists());
        assertTrue(module.checkInstallStatus(targetContext)); // status computed on-the-fly
        assertTrue(module.isInstalled());

        // reset module
        module = getSampleModuleSetup();

        // add one at the end
        newFile = new File(moduleDir, Module.getAudioName(module.getNumberOfPages() + 1));
        DataUtils.copyRawFileToInternal(testContext, targetContext, ModuleUtils.Samples.audioId, newFile);
        // Now there is a new file that does not belong, the module does not care about it
        // since it will never access it.
        assertTrue(moduleDir.exists());
        assertTrue(module.checkInstallStatus(targetContext)); // status computed on-the-fly
        assertTrue(module.isInstalled());


        // reset module
        module = getSampleModuleSetup();

        // add one at the end
        newFile = new File(moduleDir, Module.getTranscriptName(module.getNumberOfPages() + 1));
        DataUtils.copyRawFileToInternal(testContext, targetContext, ModuleUtils.Samples.audioId, newFile);
        // Now there is a new file that does not belong, the module does not care about it
        // since it will never access it.
        assertTrue(moduleDir.exists());
        assertTrue(module.checkInstallStatus(targetContext)); // status computed on-the-fly
        assertTrue(module.isInstalled());


        // reset module
        module = getSampleModuleSetup();

        // add one at the end
        newFile = new File(moduleDir, Module.getExtrasName(module.getNumberOfPages() + 1));
        DataUtils.copyRawFileToInternal(testContext, targetContext, ModuleUtils.Samples.audioId, newFile);
        // Now there is a new file that does not belong, the module does not care about it
        // since it will never access it.
        assertTrue(moduleDir.exists());
        assertTrue(module.checkInstallStatus(targetContext)); // status computed on-the-fly
        assertTrue(module.isInstalled());


        // reset module
        module = getSampleModuleSetup();

        // add one at the end
        newFile = new File(moduleDir, Module.getQuizName(module.getNumberOfPages() + 1));
        DataUtils.copyRawFileToInternal(testContext, targetContext, ModuleUtils.Samples.audioId, newFile);
        // Now there is a new file that does not belong, the module does not care about it
        // since it will never access it.
        assertTrue(moduleDir.exists());
        assertTrue(module.checkInstallStatus(targetContext)); // status computed on-the-fly
        assertTrue(module.isInstalled());
    }


    public void testClear() throws Exception {
        {   // Create a module without calling setup. Then clear it
            Module module = getSampleModuleNoSetup();
            int moduleNumber = module.getModuleNumber();
            File moduleDir = module.getModuleDir(targetContext);
            assertFalse(moduleDir.exists());
            assertFalse(module.isInstalled());
            module.clear(targetContext);
            assertFalse(moduleDir.exists());
            assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
            assertFalse(module.isInstalled()); // install is reset
            ModuleUtils.isModuleInSQLite(targetContext, moduleNumber);
        }

        {   // Set up a module. Then clear it
            Module module = getSampleModuleSetup();
            int moduleNumber = module.getModuleNumber();
            File moduleDir = module.getModuleDir(targetContext);
            assertTrue(moduleDir.exists());
            assertTrue(module.isInstalled());
            module.clear(targetContext);
            assertFalse(moduleDir.exists());
            assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
            assertFalse(module.isInstalled()); // install is reset
            ModuleUtils.isModuleInSQLite(targetContext, moduleNumber);
        }
    }


    public void testDownloadVideoFile1() throws Exception {
        boolean trim = true;
        { // Test: If the module has not been installed (that is no video list), can a video be downloaded?
            Module module = getSampleModuleNoSetup();
            File moduleDir = module.getModuleDir(targetContext);
            assertFalse(moduleDir.exists());
            assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
            assertFalse(module.isInstalled()); // cached install value

            int numberOfPages = module.getNumberOfPages();
            for (int pageNumber = 0; pageNumber <= numberOfPages + 1; pageNumber++) {
                assertFalse(module.isVideoAvailable(pageNumber));
                assertFalse(module.isVideoOnDevice(targetContext, pageNumber));
                boolean result = module.downloadVideoFile(targetContext, pageNumber, trim);
                assertFalse(result);
                File videoFile = module.getVideoFile(targetContext, pageNumber);
                if (videoFile != null) {
                    assertFalse(videoFile.exists());
                }
            }
        }
    }

    public void testDownloadVideoFile2() throws Exception {
        boolean trim = true;
        {   // Test video when the module has been set up
            Module module = getSampleModuleSetup();
            File moduleDir = module.getModuleDir(targetContext);
            assertTrue(moduleDir.exists());
            assertTrue(module.checkInstallStatus(targetContext)); // status computed on-the-fly
            assertTrue(module.isInstalled()); // cached install value

            int numberOfPages = module.getNumberOfPages();
            for (int pageNumber=0; pageNumber<=numberOfPages+1; pageNumber++) {

                // Test: If a video file is not on the videolist, it cannot be downloaded
                // Test: If a video is on the videoList, it must be downloadable

                assertFalse(module.isVideoOnDevice(targetContext, pageNumber));

                if (module.isVideoAvailable(pageNumber)) {
                    File videoFile = module.getVideoFile(targetContext, pageNumber);
                    assertFalse(videoFile.exists());
                    boolean result = module.downloadVideoFile(targetContext, pageNumber, trim);
                    assertTrue(result);
                    assertTrue(videoFile.exists());
                }
                else {
                    File videoFile = module.getVideoFile(targetContext, pageNumber);
                    assertTrue(videoFile == null || !videoFile.exists());
                    boolean result = module.downloadVideoFile(targetContext, pageNumber, trim);
                    assertFalse(result);
                    assertTrue(videoFile == null || !videoFile.exists());
                }
            }
        }
    }



    public void testGetContentDescription1() throws Exception {
        // Test a module that has not been setup
        {
            Module module = getSampleModuleNoSetup();
            File moduleDir = module.getModuleDir(targetContext);
            assertFalse(moduleDir.exists());
            assertFalse(module.checkInstallStatus(targetContext)); // status computed on-the-fly
            assertFalse(module.isInstalled()); // cached install value

            String contentDescription = module.getContentDescription(targetContext);
            assertEquals(constants.Content_NotDownloaded, contentDescription);
        }
    }

    public void testGetContentDescription2() throws Exception {
        // Test a module that has been setup
        {
            Module module = getSampleModuleSetup();
            File moduleDir = module.getModuleDir(targetContext);
            assertTrue(moduleDir.exists());
            assertTrue(module.checkInstallStatus(targetContext)); // status computed on-the-fly
            assertTrue(module.isInstalled()); // cached install value

            String contentDescription = module.getContentDescription(targetContext);

            // get the module's header and compare with the content description
            ModuleFields.Variables variables = new ModuleFields.Variables(module);
            int fileCount = variables.header.getNumberOfPages()
                    + variables.header.getNumberOfVideos()
                    + variables.header.getNumberOfExtras()
                    + variables.header.getNumberOfQuizzes();
            if (fileCount>0) {
                assertTrue(contentDescription.contains(variables.header.getNumberOfPages() + " slide"));
                if (variables.header.getNumberOfVideos()>0)
                    assertTrue(contentDescription.contains(variables.header.getNumberOfVideos() + " video"));
                if (variables.header.getNumberOfExtras()>0)
                    assertTrue(contentDescription.contains(variables.header.getNumberOfExtras() + " additional resource"));
                if (variables.header.getNumberOfQuizzes()>0)
                    assertTrue(contentDescription.contains(variables.header.getNumberOfQuizzes() + " quiz"));
            }
            else {
                assertEquals(constants.Content_Empty, contentDescription);
            }
        }
    }


    private void verifyModuleFilenames(Module module) {
        int moduleNumber = module.getModuleNumber();

        // NOTE: the file names accept bad moduleNumber and pageNumber
        assertTrue(Module.getModuleDirname(moduleNumber).matches(patternDir));
        assertTrue(Module.getArchiveName(moduleNumber).matches(patternArchive));
        assertTrue(Module.getModuleDirname(moduleNumber).contains(Integer.toString(moduleNumber)));
        assertTrue(Module.getArchiveName(moduleNumber).contains(Integer.toString(moduleNumber)));
        String moduleDirPath = module.getModuleDir(targetContext).getAbsolutePath();

        int numberOfPages = module.getNumberOfPages();
        for (int pageNumber=-1; pageNumber<=numberOfPages+1; pageNumber++) {
            assertTrue(Module.getSlideLabel(moduleNumber, pageNumber).equals(Module.ModuleLabel + moduleNumber + Module.SlideLabel + pageNumber));
            assertTrue(Module.getAudioName(pageNumber).equals(Module.SlideLabel + pageNumber + constants.AudioExtension));
            assertTrue(Module.getTranscriptName(pageNumber).equals(Module.SlideLabel + pageNumber + constants.TextExtension));
            assertTrue(Module.getExtrasName(pageNumber).equals(Module.SlideLabel + pageNumber + constants.ExtrasLabel + constants.TextExtension));
            assertTrue(Module.getQuizName(pageNumber).equals(Module.SlideLabel + pageNumber + constants.QuizLabel + constants.TextExtension));
            assertTrue(Module.getVideoName(pageNumber).equals(Module.SlideLabel + pageNumber + constants.VideoLabel + constants.VideoExtension));

            // Note: the file itself may not exist if the page number is bad.
            // If page number is good, it must locate at the right directory

            if (pageNumber>=1 && pageNumber<=numberOfPages) {
                assertTrue(module.getSlideFile(targetContext, pageNumber).getAbsolutePath().contains(moduleDirPath));
                assertTrue(module.getAudioFile(targetContext, pageNumber).getAbsolutePath().contains(moduleDirPath));
                assertTrue(module.getTranscriptFile(targetContext, pageNumber).getAbsolutePath().contains(moduleDirPath));
                assertTrue(module.getExtrasFile(targetContext, pageNumber).getAbsolutePath().contains(moduleDirPath));
                assertTrue(module.getQuizFile(targetContext, pageNumber).getAbsolutePath().contains(moduleDirPath));
                assertTrue(module.getVideoFile(targetContext, pageNumber).getAbsolutePath().contains(moduleDirPath));
            }
            else {
                // If page number is bad, file does not exist
                assertNull(module.getSlideFile(targetContext, pageNumber));
                assertNull(module.getAudioFile(targetContext, pageNumber));
                assertNull(module.getTranscriptFile(targetContext, pageNumber));
                assertNull(module.getExtrasFile(targetContext, pageNumber));
                assertNull(module.getQuizFile(targetContext, pageNumber));
                assertNull(module.getVideoFile(targetContext, pageNumber));
            }
        }
    }



    private Module getSampleModuleNoSetup() throws Exception {
        CourseUtils.DataSet dataSet = new CourseUtils.DataSet(1);   // Use set 1 because it has 1 module
        // Call course to obtain the info file
        Course course = Course.get(targetContext);
        CourseFields.Variables.setLink(course, dataSet.courseLink);
        course.setup(targetContext);


        // Load a course data file to get one header.
        CourseFields.Constants constants_course = new CourseFields.Constants();
        File destinationFile = IO.getInternalFile(targetContext, constants_course.CourseFilename);
        CourseUtils.CourseFileData courseFileData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        int index = 0;
        Header header = courseFileData.headerArrayList.get(index);
        Module module = new Module(targetContext, header);

        constants_course.detach();
        return module;
    }

    private Module getSampleModuleSetup() throws Exception {
        CourseUtils.DataSet dataSet = new CourseUtils.DataSet(1);   // Use set 1 because it has 1 module
        // Call course to obtain the info file
        Course course = Course.get(targetContext);
        CourseFields.Variables.setLink(course, dataSet.courseLink);
        course.setup(targetContext);

        // Load a course data file to get one header.
        CourseFields.Constants constants_course = new CourseFields.Constants();
        File destinationFile = IO.getInternalFile(targetContext, constants_course.CourseFilename);
        CourseUtils.CourseFileData courseFileData = new CourseUtils.CourseFileData(targetContext, destinationFile);

        int index = 0;
        Header header = courseFileData.headerArrayList.get(index);
        Module module = new Module(targetContext, header);

        // make the archive available
        module.setup(targetContext);
        checkValidity(TAG, targetContext, module);
        assertTrue(module.checkInstallStatus(targetContext));
        assertTrue(module.isInstalled());

        constants_course.detach();
        return module;
    }

}
