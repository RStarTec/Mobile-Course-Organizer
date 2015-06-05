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

import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.search.Search;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ahui on 1/16/15.
 */
public class ModuleUtils {

    public static void getSampleVideoFromResource(Context testContext, Context targetContext, Module module, int pageNumber) throws Exception {
        getVideoFromResource(testContext, targetContext, module, pageNumber, com.rstar.mobile.csc205sp2015.test.R.raw.video);
    }
    public static void getSampleSlideFromResource(Context testContext, Context targetContext, Module module, int pageNumber) throws Exception {
        getSlideFromResource(testContext, targetContext, module, pageNumber, com.rstar.mobile.csc205sp2015.test.R.raw.slide);
    }
    public static void getSampleAudioFromResource(Context testContext, Context targetContext, Module module, int pageNumber) throws Exception {
        getAudioFromResource(testContext, targetContext, module, pageNumber, com.rstar.mobile.csc205sp2015.test.R.raw.audio);
    }
    public static void getSampleTranscriptFromResource(Context testContext, Context targetContext, Module module, int pageNumber) throws Exception {
        getTranscriptFromResource(testContext, targetContext, module, pageNumber, com.rstar.mobile.csc205sp2015.test.R.raw.transcript);
    }
    public static void getSampleExtrasFromResource(Context testContext, Context targetContext, Module module, int pageNumber) throws Exception {
        getExtrasFromResource(testContext, targetContext, module, pageNumber, com.rstar.mobile.csc205sp2015.test.R.raw.extras);
    }
    public static void getSampleQuizFromResource(Context testContext, Context targetContext, Module module, int pageNumber) throws Exception {
        getQuizFromResource(testContext, targetContext, module, pageNumber, com.rstar.mobile.csc205sp2015.test.R.raw.quiz);
    }


    public static void getArchiveFromResource(Context testContext, Context targetContext, int moduleNumber, int id) throws Exception {
        String filename = Module.getArchiveName(moduleNumber);
        File destinationFile = IO.getInternalFile(targetContext, filename);
        DataUtils.copyRawFileToInternal(testContext, targetContext, id, destinationFile);
    }

    public static void getVideoFromResource(Context testContext, Context targetContext, Module module, int pageNumber, int id) throws Exception {
        File destinationFile = module.getVideoFile(targetContext, pageNumber);
        if (destinationFile!=null) {
            DataUtils.copyRawFileToInternal(testContext, targetContext, id, destinationFile);
        }
    }

    public static void getSlideFromResource(Context testContext, Context targetContext, Module module, int pageNumber, int id) throws Exception {
        File destinationFile = module.getSlideFile(targetContext, pageNumber);
        if (destinationFile!=null) {
            DataUtils.copyRawFileToInternal(testContext, targetContext, id, destinationFile);
        }
    }

    public static void getAudioFromResource(Context testContext, Context targetContext, Module module, int pageNumber, int id) throws Exception {
        File destinationFile = module.getAudioFile(targetContext, pageNumber);
        if (destinationFile!=null) {
            DataUtils.copyRawFileToInternal(testContext, targetContext, id, destinationFile);
        }
    }

    public static void getTranscriptFromResource(Context testContext, Context targetContext, Module module, int pageNumber, int id) throws Exception {
        File destinationFile = module.getTranscriptFile(targetContext, pageNumber);
        if (destinationFile!=null) {
            DataUtils.copyRawFileToInternal(testContext, targetContext, id, destinationFile);
        }
    }


    public static void getExtrasFromResource(Context testContext, Context targetContext, Module module, int pageNumber, int id) throws Exception {
        File destinationFile = module.getExtrasFile(targetContext, pageNumber);
        if (destinationFile!=null) {
            DataUtils.copyRawFileToInternal(testContext, targetContext, id, destinationFile);
        }
    }

    public static void getQuizFromResource(Context testContext, Context targetContext, Module module, int pageNumber, int id) throws Exception {
        File destinationFile = module.getQuizFile(targetContext, pageNumber);
        if (destinationFile!=null) {
            DataUtils.copyRawFileToInternal(testContext, targetContext, id, destinationFile);
        }
    }

    public static boolean isModuleInSQLite(Context targetContext, int moduleNumber) {
        Search search = new Search(targetContext);

        // perform 2 searches of some very common pattern. If either returns positive, then module is searchable
        ArrayList<Search.Item> searchData1 = search.getSegmentMatching("a", moduleNumber);
        boolean result1 = searchData1.size()>0;
        ArrayList<Search.Item> searchData2 = search.getSegmentMatching(",", moduleNumber);
        boolean result2 = searchData2.size()>0;
        return result1 || result2;
    }


    public static class ModuleFiles {
        // TODO: later, replace these labels with constants from Module
        private static final String patternSlide = Module.SlideLabel + "[0-9]+\\.jpg";
        private static final String patternAudio = Module.SlideLabel + "[0-9]+\\.mp3";
        private static final String patternTranscript = Module.SlideLabel + "[0-9]+\\.txt";
        private static final String patternQuiz = Module.SlideLabel + "[0-9]+q\\.txt";
        private static final String patternExtra = Module.SlideLabel + "[0-9]+x\\.txt";
        private static final String patternVideo = Module.SlideLabel + "[0-9]+\\.mp4";
        private static final String patternVideoList = "VideoList\\.txt";

        File moduleDir = null;
        File slideFiles[] = null;
        File audioFiles[] = null;
        File transcriptFiles[] = null;
        File parseFiles[] = null;
        File quizFiles[] = null;
        File extrasFiles[] = null;
        File videoFiles[] = null;
        File videoList = null;

        public ModuleFiles(Context context, Module module) {
            if (module==null) return;
            moduleDir = module.getModuleDir(context);
            if (moduleDir==null || !moduleDir.exists()) return;

            int numberOfSlides = 0;
            int numberOfAudios = 0;
            int numberOfTranscripts = 0;
            int numberOfParses = 0;
            int numberOfQuizzes = 0;
            int numberOfExtras = 0;
            int numberOfVideos = 0;

            File[] allFiles = IO.getInternalFiles(context, moduleDir);
            for (File f : allFiles) {
                if (f.getName().matches(patternSlide))
                    numberOfSlides++;
                else if (f.getName().matches(patternAudio))
                    numberOfAudios++;
                else if (f.getName().matches(patternTranscript))
                    numberOfTranscripts++;
                else if (f.getName().matches(patternQuiz))
                    numberOfQuizzes++;
                else if (f.getName().matches(patternExtra))
                    numberOfExtras++;
                else if (f.getName().matches(patternVideo))
                    numberOfVideos++;
            }

            slideFiles = new File[numberOfSlides];
            audioFiles = new File[numberOfAudios];
            transcriptFiles = new File[numberOfTranscripts];
            parseFiles = new File[numberOfParses];
            quizFiles = new File[numberOfQuizzes];
            extrasFiles = new File[numberOfExtras];
            videoFiles = new File[numberOfVideos];

            int indexOfSlides = 0;
            int indexOfAudios = 0;
            int indexOfTranscripts = 0;
            int indexOfParses = 0;
            int indexOfQuizzes = 0;
            int indexOfExtras = 0;
            int indexOfVideos = 0;

            for (File f : allFiles) {
                if (f.getName().matches(patternSlide))
                    slideFiles[indexOfSlides++] = f;
                else if (f.getName().matches(patternAudio))
                    audioFiles[indexOfAudios++] = f;
                else if (f.getName().matches(patternTranscript))
                    transcriptFiles[indexOfTranscripts++] = f;
                else if (f.getName().matches(patternQuiz))
                    quizFiles[indexOfQuizzes++] = f;
                else if (f.getName().matches(patternExtra))
                    extrasFiles[indexOfExtras++] = f;
                else if (f.getName().matches(patternVideo))
                    videoFiles[indexOfVideos++] = f;
                else if (f.getName().matches(patternVideoList))
                    videoList = f;
            }

        }
        public boolean isDirExists() {
            return moduleDir!=null && moduleDir.exists();
        }
        public int getNumberOfSlides() {
            return slideFiles==null ? 0 : slideFiles.length;
        }
        public int getNumberOfAudios() { return audioFiles==null ? 0 : audioFiles.length; }
        public int getNumberOfTranscripts() { return transcriptFiles==null ? 0 : transcriptFiles.length; }
        public int getNumberOfQuizzes() {
            return quizFiles==null ? 0 : quizFiles.length;
        }
        public int getNumberOfExtras() {
            return extrasFiles==null ? 0 : extrasFiles.length;
        }
        public int getNumberOfVideos() {
            return videoFiles==null ? 0 : videoFiles.length;
        }
    }


    public static class Samples {
        public static final int slideId= com.rstar.mobile.csc205sp2015.test.R.raw.slide;
        public static final int audioId= com.rstar.mobile.csc205sp2015.test.R.raw.audio;
        public static final int transcriptId= com.rstar.mobile.csc205sp2015.test.R.raw.transcript;
        public static final int videoId= com.rstar.mobile.csc205sp2015.test.R.raw.video;
    }
}
