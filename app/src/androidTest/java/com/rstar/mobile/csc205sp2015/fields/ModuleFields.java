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

package com.rstar.mobile.csc205sp2015.fields;

import com.rstar.mobile.csc205sp2015.module.Header;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.module.VideoList;

/**
 * Created by AHui on 1/15/15.
 */
public class ModuleFields {
    public static class Constants extends Fields.Constants {

        private static final String privateField1 = "TranscriptLabel";
        private static final String privateField3 = "QuizLabel";
        private static final String privateField4 = "ExtrasLabel";
        private static final String privateField5 = "VideoLabel";
        private static final String privateField6 = "ModuleExtension";
        private static final String privateField7 = "SlideExtension";
        private static final String privateField8 = "AudioExtension";
        private static final String privateField9 = "TextExtension";
        private static final String privateField10 = "VideoExtension";
        private static final String privateField11 = "dir";
        private static final String privateField12 = "httpOpt";
        private static final String privateField13 = "VideoListFilename";
        private static final String privateField14 = "TimeFilename";

        private static final String privateField15 = "Content_Empty";
        private static final String privateField16 = "Content_NotDownloaded";
        private static final String privateField17 = "Content_HomeworkOnDevice";
        private static final String privateField18 = "Content_HomeworkNotAvailable";
        private static final String privateField19 = "Content_HomeworkOnline";



        public String TranscriptLabel = null;
        public String QuizLabel = null;
        public String ExtrasLabel = null;
        public String VideoLabel = null;
        public String ModuleExtension = null;
        public String SlideExtension = null;
        public String AudioExtension = null;
        public String TextExtension = null;
        public String VideoExtension = null;
        public String dir = null;
        public String httpOpt = null;
        public String VideoListFilename = null;
        public String TimeFilename = null;
        public String Content_Empty = null;
        public String Content_NotDownloaded = null;
        public String Content_HomeworkOnDevice = null;
        public String Content_HomeworkNotAvailable = null;
        public String Content_HomeworkOnline = null;


        public Constants() throws Exception { get(); }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            TranscriptLabel = getTranscriptLabel();
            QuizLabel = getQuizLabel();
            ExtrasLabel = getExtrasLabel();
            VideoLabel = getVideoLabel();
            ModuleExtension = getModuleExtension();
            SlideExtension = getSlideExtension();
            AudioExtension = getAudioExtension();
            TextExtension = getTextExtension();
            VideoExtension = getVideoExtension();
            dir = getDir();
            httpOpt = getHttpOpt();
            VideoListFilename = getVideoListFilename();
            TimeFilename = getTimeFilename();
            Content_Empty = getContentEmpty();
            Content_NotDownloaded = getContentNotDownloaded();
            Content_HomeworkOnDevice = getContentHomeworkOnDevice();
            Content_HomeworkNotAvailable = getContentHomeworkNotAvailable();
            Content_HomeworkOnline = getContentHomeworkOnline();
        }
        @Override
        public void detach() {
            TranscriptLabel = null;
            QuizLabel = null;
            ExtrasLabel = null;
            VideoLabel = null;
            ModuleExtension = null;
            SlideExtension = null;
            AudioExtension = null;
            TextExtension = null;
            VideoExtension = null;
            dir = null;
            httpOpt = null;
            VideoListFilename = null;
            TimeFilename = null;
            Content_Empty = null;
            Content_NotDownloaded = null;
            Content_HomeworkOnDevice = null;
            Content_HomeworkNotAvailable = null;
            Content_HomeworkOnline = null;
        }


        public static String getTranscriptLabel() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField1, String.class); }
        public static String getQuizLabel() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField3, String.class); }
        public static String getExtrasLabel() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField4, String.class); }
        public static String getVideoLabel() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField5, String.class); }
        public static String getModuleExtension() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField6, String.class); }
        public static String getSlideExtension() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField7, String.class); }
        public static String getAudioExtension() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField8, String.class); }
        public static String getTextExtension() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField9, String.class); }
        public static String getVideoExtension() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField10, String.class); }
        public static String getDir() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField11, String.class); }
        public static String getHttpOpt() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField12, String.class); }
        public static String getVideoListFilename() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField13, String.class); }
        public static String getTimeFilename() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField14, String.class); }
        public static String getContentEmpty() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField15, String.class); }
        public static String getContentNotDownloaded() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField16, String.class); }
        public static String getContentHomeworkOnDevice() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField17, String.class); }
        public static String getContentHomeworkNotAvailable() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField18, String.class); }
        public static String getContentHomeworkOnline() throws Exception
            { return RefUtil.getPrivateField(Module.class, privateField19, String.class); }
    }

    public static class Variables extends Fields.Variables<Module> {
        private static final String privateField1 = "header";
        private static final String privateField2 = "link";
        private static final String privateField3 = "videoList";

        public Header header = null;
        public String link = null;
        public VideoList videoList = null;


        public Variables(Module module) throws Exception {
            refresh(module);
        }

        @Override
        public void refresh(Module module) throws Exception {
            header = getHeader(module);
            link = getLink(module);
            videoList = getVideoList(module);
        }
        @Override
        public void detach() {
            header = null;
            link = null;
            videoList = null;
        }

        public static Header getHeader(Module module) throws Exception
            { return RefUtil.getPrivateField(module, privateField1, Header.class); }
        public static String getLink(Module module) throws Exception
            { return RefUtil.getPrivateField(module, privateField2, String.class); }
        public static VideoList getVideoList(Module module) throws Exception
            { return RefUtil.getPrivateField(module, privateField3, VideoList.class); }
    }

}
