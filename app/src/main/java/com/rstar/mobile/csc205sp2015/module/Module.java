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

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.homework.Homework;
import com.rstar.mobile.csc205sp2015.search.Search;

import java.io.File;

/**
 * Created by AHui
 */
public class Module {
    private static final String TAG = Module.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    // ATTENTION: make sure that the internal directory for the module only contains files from downloaded package. Do not save student work in it.
    public static final String ModuleLabel = "Module";
    public static final String SlideLabel = "Slide";
    private static final String TranscriptLabel = ""; // put nothing for now
    private static final String QuizLabel = "q"; // quiz data are optional
    private static final String ExtrasLabel = "x"; // extras data are optional
    private static final String VideoLabel = "v"; // videos are optional
    private static final String ModuleExtension = ".zip";
    private static final String SlideExtension = ".jpg";
    private static final String AudioExtension = ".mp3";
    private static final String TextExtension = ".txt";
    private static final String VideoExtension = ".mp4";
    private static final String dir = "/";
    private static final String httpOpt = "?";
    private static final String VideoListFilename = "VideoList" + TextExtension;
    private static final String TimeFilename = "Time" + TextExtension; // Time of this package (from header)
    public static final int DefaultModuleNumber = 0;
    public static final int DefaultPageNumber = 1;

    // These are used for reporting the content description of the module
    private static final String Content_Empty = "Content not available.";
    private static final String Content_NotDownloaded = "Not downloaded.";
    private static final String Content_HomeworkOnDevice = "Homework is available.";
    private static final String Content_HomeworkNotAvailable = "Homework not available.";
    private static final String Content_HomeworkOnline = "Check online for homework.";
    private static final String Site = AppSettings.PublicSite;


    private Header header = null; // Create a new header object. Don't point to the header objec that is passed in
    private String link = "";
    private VideoList videoList = null;

    // Need to cache the install status
    private boolean installed = false;

    public Module(Context context, Header h) {
        header = new Header(h);
        link = Site
                + header.getOnlineDirectory()
                + header.getOnlinePackageName()
                + getDownloadOptions();

        installed = checkInstallStatus(context);

        if (isInstalled()) {
            File f = getVideoListFile(context);
            if (f != null && f.exists()) {
                videoList = new VideoList(context, header.getNumberOfPages(), getVideoListFile(context));
                Savelog.d(TAG, debug, "Successfully set up videolist");
            }
        }
    }

    public void setup(Context context) {
        Savelog.d(TAG, debug, "setup() called. Setting up module" + header.getModuleNumber());

        // If archive is not currently on device,
        // download it
        File archiveFile = getArchiveFile(context);
        File targetInternalDir = getModuleDir(context);

        try {
            // If archive already exists, there is no need to download
            if (!archiveFile.exists()) {
                Savelog.d(TAG, debug, "download package from " + link);
                IO.downloadFile(link, archiveFile);
            }

            // If directory exists, it suggests that the module might have been installed before.
            // Clear directory as well as sqlite
            if (targetInternalDir.exists()) {
                IO.clearInternalDirectory(context, targetInternalDir);
                Search.delete(context, this);
            }

            IO.unzip(context, archiveFile.getName(), targetInternalDir);
            Savelog.d(TAG, debug, "Successfully unzip file " + archiveFile.getName());

            // Save the time of this package according to the header's information
            IO.saveStringAsFile(context, getTimeFile(context), header.getTime());

            // Set up videoList
            videoList = new VideoList(context, header.getNumberOfPages(), getVideoListFile(context));
            Savelog.d(TAG, debug, "Successfully set up videolist ");

            Search.setup(context, this);
            Savelog.d(TAG, debug, "Successfully set up sqlite for this module ");
        }
        catch (Exception e) {
            Savelog.w(TAG, "cannot download or unzip archive ", e);
            clear(context);
        }

        if (debug) {
            Savelog.d(TAG, debug, "Displaying internal directory content:");
            File[] files = IO.getInternalFiles(context);
            for (File f : files) {
                Savelog.d(TAG, debug, f.getAbsolutePath() + " is " + (f.isDirectory()?"directory":"file"));
            }
        }

        installed = checkInstallStatus(context);

        Savelog.d(TAG, debug, "Module setup completed");
    }

    public int getModuleNumber() {
        return header.getModuleNumber();
    }
    public int getNumberOfPages() {
        return header.getNumberOfPages();
    }
    public String getTitle() {
        return header.getTitle();
    }

    private String getLink() {
        return link;
    }
    public String getLastUpdateDate() { return header.getTime(); }
    private String getDownloadOptions() {
        return  header.getDownloadOptions().length()>0? (httpOpt+header.getDownloadOptions()):"";
    }
    public File getArchiveFile(Context context) {
        return IO.getInternalFile(context, getArchiveName(header.getModuleNumber()));
    }

    public File getSlideFile(Context context, int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return null;
        return new File(getModuleDir(context), getSlideName(pageNumber));
    }
    public File getAudioFile(Context context, int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return null;
        return new File(getModuleDir(context), getAudioName(pageNumber));
    }
    public File getTranscriptFile(Context context, int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return null;
        return new File(getModuleDir(context), getTranscriptName(pageNumber));
    }
    public File getQuizFile(Context context, int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return null;
        return new File(getModuleDir(context), getQuizName(pageNumber));
    }
    public File getExtrasFile(Context context, int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return null;
        return new File(getModuleDir(context), getExtrasName(pageNumber));
    }
    public File getVideoFile(Context context, int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return null;
        return new File(getModuleDir(context), getVideoName(pageNumber));
    }
    private File getVideoListFile(Context context) {
        return new File(getModuleDir(context), VideoListFilename);
    }
    public void clearVideoFile(Context context, int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return;
        File videoFile = getVideoFile(context, pageNumber);
        if (videoFile!=null && videoFile.exists()) videoFile.delete();
    }

    private File getTimeFile(Context context) {
        return new File(getModuleDir(context), TimeFilename);
    }
    String getInstallTime(Context context) {
        String installTime = Header.NoDate;
        try {
            File f = getTimeFile(context);
            if (f!=null && f.exists()) installTime = IO.loadFileAsString(context, f);
        } catch (Exception e) {}
        return installTime;
    }

    // This method must be called within an AsyncTask
    // video file is big. It does not come with the module package. User must explicitly request for it
    public boolean downloadVideoFile(Context context, int pageNumber, boolean trim) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return false;
        String videoUrl = getVideoLink(pageNumber);
        File videoFile = getVideoFile(context, pageNumber);
        Savelog.d(TAG, debug, "Ready to download " + videoFile.getAbsolutePath() + " from " + videoUrl);
        if (videoFile!=null) {
            if (trim) {
                if (videoFile.exists()) videoFile.delete();
            }
            try {
                IO.downloadFile(videoUrl, videoFile);
                boolean success = videoFile!=null && videoFile.exists();
                Savelog.d(TAG, debug, "Video download result: " + success);
                return success;
            }
            catch (Exception e) {
                Savelog.d(TAG, debug, "Failed to download video from " + videoUrl + " to " + videoFile.getAbsolutePath());
                return false;
            }
       }
        else {
            return false;
        }
    }

    public boolean isVideoOnDevice(Context context, int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return false;
        if (videoList==null) return false;
        if (!videoList.isVideoAvailable(pageNumber)) return false;
        // VideoList shows that video is available for this page. Check if it's on device
        File videoFile = getVideoFile(context, pageNumber);
        return (videoFile!=null && videoFile.exists());
    }

    public boolean isVideoAvailable(int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return false;
        if (videoList==null) return false;
        return videoList.isVideoAvailable(pageNumber);
    }
    public boolean isAudioAvailable(Context context, int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return false;
        File f = getAudioFile(context, pageNumber);
        return (f!=null && f.exists());
    }
    public boolean isTranscriptAvailable(Context context, int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return false;
        File f = getTranscriptFile(context, pageNumber);
        return (f!=null && f.exists());
    }


    public String getVideoDescription(int pageNumber) {
        if (videoList==null) return "";
        return videoList.getDescription(pageNumber);
    }
    public String getVideoSize(int pageNumber) {
        if (videoList==null) return VideoList.ZeroSize;
        return videoList.getVideoSize(pageNumber);
    }

    private String getVideoLink(int pageNumber) {
        if (pageNumber<=0 || pageNumber>header.getNumberOfPages()) return null;
        return Site + this.header.getOnlineDirectory() + getVideoName(pageNumber) + getDownloadOptions();
    }

    public File getModuleDir(Context context) {
        String dirname = getModuleDirname(this.header.getModuleNumber());
        return IO.getInternalDir(context, dirname);
    }


    public static String getArchiveName(int moduleNumber) {
        return ModuleLabel + moduleNumber + ModuleExtension;
    }
    public static String getModuleDirname(int moduleNumber) {
        return ModuleLabel + moduleNumber + dir;
    }
    public static String getSlideName(int pageNumber) {
        return SlideLabel + pageNumber + SlideExtension;
    }
    public static String getAudioName(int pageNumber) {
        return SlideLabel + pageNumber + AudioExtension;
    }
    public static String getTranscriptName(int pageNumber) {
        return SlideLabel + pageNumber + TranscriptLabel + TextExtension;
    }
    public static String getQuizName(int pageNumber) {
        return SlideLabel + pageNumber + QuizLabel + TextExtension;
    }
    public static String getExtrasName(int pageNumber) {
        return SlideLabel + pageNumber + ExtrasLabel + TextExtension;
    }
    public static String getVideoName(int pageNumber) {
        return SlideLabel + pageNumber + VideoLabel + VideoExtension;
    }

    // For unique identification of a slide
    public static String getSlideLabel(int moduleNumber, int pageNumber) {
        return ModuleLabel + moduleNumber + SlideLabel + pageNumber;
    }

    public String getHeaderDescription() {
        return header.toString();
    }


            
    public String getContentDescription(Context context) {
        String data = "";
        if (isInstalled()) {

            data += Integer.toString(header.getNumberOfPages()) + " slide" + (header.getNumberOfPages()>1?"s":"");
            if (header.getNumberOfVideos()>0) {
                data += ", " + header.getNumberOfVideos() + " video" + (header.getNumberOfVideos()>1?"s":"");
            }
            if (header.getNumberOfQuizzes()>0) {
                data += ", " + header.getNumberOfQuizzes() + " quiz" + (header.getNumberOfQuizzes()>1?"zes":"");
            }
            if (header.getNumberOfExtras()>0) {
                data += ", " + header.getNumberOfExtras() + " additional resource" + (header.getNumberOfExtras()>1?"s":"");
            }

            if (header.getNumberOfPages()==0 && header.getNumberOfExtras()==0 && header.getNumberOfQuizzes()==0 && header.getNumberOfVideos()==0) {
                // Reset data and report content not available.
                data = Content_Empty;
            }
            else {
                // Add description
                data = "Content includes " + data + ".";
            }

            // check homework status
            int homeworkStatus = Homework.Status.get(context, header.getModuleNumber());
            if (homeworkStatus==Homework.Status.OnDevice) {
                data += " " + Content_HomeworkOnDevice;
            }
            else if (homeworkStatus==Homework.Status.NotAvailable) {
                data += " " + Content_HomeworkNotAvailable;
            }
            else { // all other status requires login and/or download
                data += " " + Content_HomeworkOnline;
            }

        }
        else {
            data += Content_NotDownloaded;
        }
        return data;
    }

    @Override
    public String toString() {
        String data = "";
        data += header.toString() + ";";
        data += link;
        return data;
    }

    // cached value
    public boolean isInstalled() { return installed; }

    // live check
    public boolean checkInstallStatus(Context context) {
        boolean complete = true;
        File targetInternalDir = getModuleDir(context);

        if (targetInternalDir.exists()) {

            String installTime = getInstallTime(context);
            Savelog.d(TAG, debug, "installTime=" + installTime + " headerTime=" + header.getTime());
            if (installTime.equals(Header.NoDate) || !installTime.equals(header.getTime())) {
                complete = false;
            }

            int numberOfSlides = 0;
            for (int pageNumber=1; pageNumber<=header.getNumberOfPages() && complete; pageNumber++) {
                File f = getSlideFile(context, pageNumber);
                if (f!=null && f.exists()) numberOfSlides++;
            }
            if (numberOfSlides != header.getNumberOfPages()) {
                Savelog.d(TAG, debug, "missing slides " + numberOfSlides + "!=" + header.getNumberOfPages());
                complete = false;
            }


            int numberOfAudios = 0;
            for (int pageNumber=1; pageNumber<=header.getNumberOfPages() && complete; pageNumber++) {
                File f = getAudioFile(context, pageNumber);
                if (f!=null && f.exists()) numberOfAudios++;
            }
            if (numberOfAudios != header.getNumberOfAudios()) {
                Savelog.d(TAG, debug, "missing audios " + numberOfAudios + "!=" + header.getNumberOfAudios());
                complete = false;
            }

            int numberOfTranscripts = 0;
            for (int pageNumber=1; pageNumber<=header.getNumberOfPages() && complete; pageNumber++) {
                File f = getTranscriptFile(context, pageNumber);
                if (f!=null && f.exists()) numberOfTranscripts++;
            }
            if (numberOfTranscripts != header.getNumberOfTranscripts()) {
                Savelog.d(TAG, debug, "missing transcripts " + numberOfTranscripts + "!=" + header.getNumberOfTranscripts());
                complete = false;
            }

            int numberOfExtras = 0;
            for (int pageNumber=1; pageNumber<=header.getNumberOfPages() && complete; pageNumber++) {
                File f = getExtrasFile(context, pageNumber);
                if (f!=null && f.exists()) numberOfExtras++;
            }
            if (numberOfExtras != header.getNumberOfExtras()) {
                Savelog.d(TAG, debug, "missing extras " + numberOfExtras + "!=" + header.getNumberOfExtras());
                complete = false;
            }

            int numberOfQuizzes = 0;
            for (int pageNumber=1; pageNumber<=header.getNumberOfPages() && complete; pageNumber++) {
                File f = getQuizFile(context, pageNumber);
                if (f!=null && f.exists()) numberOfQuizzes++;
            }
            if (numberOfQuizzes != header.getNumberOfQuizzes()) {
                Savelog.d(TAG, debug, "missing quizzes " + numberOfQuizzes + "!=" + header.getNumberOfQuizzes());
                complete = false;
            }

            // VideoListFile must exist
            File f = getVideoListFile(context);
            if (f==null || !f.exists()) {
                Savelog.d(TAG, debug, "missing video list");
                complete = false;
            }
            // No need to check video because they might not be in the default package

        }
        else {
            complete = false;
        }
        Savelog.d(TAG, debug, "module " + getModuleNumber() + " isInstalled? " + complete);
        return complete;
    }


    public void clear(Context context) {
        Savelog.d(TAG, debug, "going to clear module " + header.getModuleNumber());
        File targetInternalDir = getModuleDir(context);
        if (targetInternalDir!=null && targetInternalDir.exists()) {
            IO.clearInternalDirectory(context, targetInternalDir);
            // Once the directory is gone, videoList is no longer valid
            videoList = null;
            Savelog.d(TAG, debug, "Deleting videoList for module " + header.getModuleNumber());
        }
        // delete archive
        File f = getArchiveFile(context);
        if (f != null && f.exists()) {
            Savelog.d(TAG, debug, "Deleting " + f.getAbsolutePath());
            f.delete();
        }
        // Update database
        Search.delete(context, this);
        installed = false;
    }

    public void syncWithDirectory(Context context) {
        // Sync all the local variables with the directory.
        // As the directory might be deleted, the videoList may need update
        videoList = null;
        // The header and the link do not depend on the directory. So they should be ok.
        File f = getVideoListFile(context);
        if (f!=null && f.exists()) {
            videoList = new VideoList(context, header.getNumberOfPages(), getVideoListFile(context));
            Savelog.d(TAG, debug, "Successfully reset videolist");
        }
        installed = checkInstallStatus(context); // double-check the installation status
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Module) {
            Module m = (Module) obj;
            if (this.header!=null && this.header.equals(m.header)
                && this.link!=null && this.link.equals(m.link)
                && ((this.videoList==null && m.videoList==null)
                    || this.videoList.equals(m.videoList)))
                return true;
        }
        return false;
    }
}
