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

package com.rstar.mobile.csc205sp2015.io;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.util.Log;

import com.rstar.mobile.csc205sp2015.app.App;
import com.rstar.mobile.csc205sp2015.app.AppSettings;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class IO {
    private static final String TAG = IO.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final String ALL_EXTENSIONS = ".*";
    private static final String externalFolder = AppSettings.defaultFolder;

    public static final int timeout = 30000;  // connection timeout in 30 seconds


    public static File getInternalFile(Context context, String filename) {
        File f = context.getFileStreamPath(filename);
        return f;
    }

    private static File getInternalFileIfExists(Context context, String filename) {
        if (filename==null) return null;
        File f = context.getFileStreamPath(filename);
        if (f!=null && f.exists()) return f;
        else return null;
    }


    public static File[] getInternalFiles(Context context) {
        File internalDirectory = context.getFilesDir();
        return getInternalFiles(context, internalDirectory);
    }


    public static File[] getInternalFiles(Context context, File internalDirectory) {
        // Return empty files if input is invalid
        if (internalDirectory==null || !internalDirectory.exists()) return new File[0];

        ArrayList<File> output = new ArrayList<File>();
        output = recurGetFiles(internalDirectory.getAbsolutePath(), output);
        File files[] = new File[output.size()];
        for (int index=0; index<output.size(); index++) {
            files[index] = output.get(index);
        }
        return files;
    }

    private static ArrayList<File> recurGetFiles( String path, ArrayList<File>output ) {
        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return output;
        for ( File f : list ) {
            if ( f.isDirectory() ) {
                output = recurGetFiles(f.getAbsolutePath(), output);
                // Savelog.d(TAG, debug, "Dir:" + f.getAbsoluteFile() );
            }
            else {
                // Savelog.d(TAG, debug, "File:" + f.getAbsoluteFile());
            }
            output.add(f); // include f after traversal. This way, if a directory needs to be cleared, it will be cleared after it's emptied.
        }
        return output;
    }

    public static void clearInternalDirectory(Context context, File dir) {
        if (dir==null) return;
        File[] internalFiles = getInternalFiles(context, dir);
        for (File f : internalFiles) {
            Savelog.d(TAG, debug, "Deleting internal file " + f.getName() + " in " + dir.getName());
            f.delete();
        }
        if (dir.exists()) dir.delete(); // lastly, delete directory itself
    }

    public static void clearInternalFiles(Context context) {
        File[] internalFiles = getInternalFiles(context);
        for (File f : internalFiles) {
            Savelog.d(TAG, debug, "Deleting internal file " + f.getAbsolutePath());
            f.delete();
        }
    }

    public static File getInternalDir(Context context, String dirname) {
        if (dirname==null) return null;
        File internalSubDir = new File(context.getFilesDir(), dirname);
        return internalSubDir;
    }

    public static void saveStreamAsFile(InputStream in, File destinationFile) throws Exception {
        if (destinationFile==null) return;
        // This method assumes not specific file format. Everything is copied as bytes.
        FileOutputStream out = new FileOutputStream(destinationFile);
        byte[] buff = new byte[1024];
        int read;
        int totalRead = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
                totalRead += read;
            }
        }
        catch (Exception e) {
            Savelog.e(TAG, "cannot save stream as file " + destinationFile.getName(), e);
        }
        finally {
            Savelog.d(TAG, debug, "Read size=" + totalRead);
            in.close();
            out.close();
        }
    }


    /* Attention: Do not use the following code for reading strings because char may have different
     * sizes on different machines.
    public static String getRawResourceAsString(Context context, int resourceId) throws IOException {
        InputStream in = context.getResources().openRawResource(resourceId);
        InputStreamReader isr = new InputStreamReader(in);
        char inputBuffer[] = new char[in.available()];
        isr.read(inputBuffer);
        String data = new String(inputBuffer);
        isr.close();
        in.close();
        return data;
    }
    */

    private static String readStreamAsString(InputStream is) throws IOException {
        //create a buffer that has the same size as the InputStream
        byte[] buffer = new byte[is.available()];
        //read the text file as a stream, into the buffer
        is.read(buffer);
        //create a output stream to write the buffer into
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //write this buffer to the output stream
        os.write(buffer);
        //Close the Input and Output streams
        os.close();
        is.close();

        String content = os.toString();
        return content;
    }


    public static String loadFileAsString(Context context, String filename) throws IOException {
        //get the file as a stream from internal directory
        InputStream is = context.openFileInput(filename);
        return readStreamAsString(is);
    }

    public static String loadFileAsString(Context context, File file) throws IOException {
        //get the file as a stream from internal directory
        InputStream is = new FileInputStream(file);
        return readStreamAsString(is);
    }


    public static String getRawResourceAsString(Context context, int id) throws IOException {
        InputStream is;
        //get the file as a stream from res/raw/
        is = context.getResources().openRawResource(id);
        return readStreamAsString(is);
    }



    public static void saveStringAsFile(Context context, String filename, String data) throws IOException {
        Savelog.d(TAG, debug, "saving string as file " + filename);
        // Open file for output
        OutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
        PrintWriter pw = new PrintWriter(out);
        // Do the writing here!
        pw.print(data);
        pw.flush();
        pw.close();
        out.close();
    }

    public static void saveStringAsFile(Context context, File internalFile, String data) throws IOException {
        Savelog.d(TAG, debug, "saving string as file " + internalFile.getAbsolutePath());
        // Open file for output
        OutputStream out = new FileOutputStream(internalFile);
        PrintWriter pw = new PrintWriter(out);
        // Do the writing here!
        pw.print(data);
        pw.flush();
        pw.close();
        out.close();
    }


    // Export an internal file to cache directory.
     private static void exportInternalFile(Context context, File file) {
         String filename = file.getName();
         try {
             InputStream is = new FileInputStream(file);
             //create a buffer that has the same size as the InputStream
             byte[] buffer = new byte[is.available()];
             //read the text file as a stream, into the buffer
             is.read(buffer);
             FileOutputStream fout = new FileOutputStream(new File(getExternalCacheDirectory(context), filename));
             BufferedOutputStream bout = new BufferedOutputStream(fout);
             bout.write(buffer);
             bout.close();
             fout.close();
             is.close();
             Savelog.d(TAG, debug, "Exported " + filename + " to " + getExternalCacheDirectory(context));

         } catch (FileNotFoundException e) {
             Savelog.e(TAG, "Fail to export " + filename + e.getMessage());
         } catch (IOException e) {
             Savelog.e(TAG, "Fail to export " + filename + e.getMessage());
         }
    }

    private static void exportResourceAsFile(Context context, int id, String destinationFilename) {
        File dest = Environment.getExternalStorageDirectory();
        InputStream in = context.getResources().openRawResource(id);
        // Used the File-constructor
        OutputStream out = null;

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        try {
            out = new FileOutputStream(new File(dest, destinationFilename));
            // A little more explicit
            while ( (len = in.read(buf, 0, buf.length)) != -1){
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            Savelog.w(TAG, "Failed to export resource to file" + destinationFilename, e);
        } finally {
            // Ensure the Streams are closed:
            try { in.close(); } catch (Exception e) {}
            try { out.close(); } catch (Exception e) {}
        }
    }

    public static void unzip(Context context, String zipFilename, File internalSubDir) throws Exception {
        Savelog.d(TAG, debug, "Calling unzip()");
        InputStream is = context.openFileInput(zipFilename);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        try {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                Savelog.d(TAG, debug, "unzip: " + ze.getName());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;
                while ((count = zis.read(buffer)) != -1) {
                    baos.write(buffer, 0, count);
                }

                byte[] bytes = baos.toByteArray();

                // Here, we do not keep the original file structure.
                // All files are to be put under targetDirname in internal directory.
                String originalFilename = ze.getName();
                String shortFilename = Zip.getFileName(originalFilename);

                // Check if a file is skippable.
                if (Zip.isSkippable(shortFilename)) {
                    Savelog.d(TAG, debug, "discard: " + shortFilename);
                }
                else {

                    if (internalSubDir!=null && !internalSubDir.exists()) {
                        // Do not create any internal subdirectory until we are certain to have something to put in there.
                        internalSubDir.mkdir();
                    }

                    File dataFile = new File(internalSubDir, shortFilename);
                    // Savelog.d(TAG, debug, "accept: " + shortFilename + " as " + dataFile.getTitle());

                    // Now save the file into the internal subdirectory.
                    FileOutputStream out = new FileOutputStream(dataFile);
                    try {
                        out.write(bytes, 0, bytes.length);
                    } catch (Exception e) {
                        Savelog.w(TAG, "failed: saving" + shortFilename);
                        throw e;
                    }
                    finally {
                        out.close();
                        Savelog.d(TAG, debug, "status: " + dataFile.getAbsolutePath() + (dataFile.exists() ? " created" : " not created."));
                    }
                }
            }
        } catch (Exception e) {
            Savelog.w(TAG, "error: ", e);
            throw e;
        } finally {
            zis.close();
        }
    }



    public static File downloadFile(String url, File destinationFile) throws Exception  {
        Savelog.d(TAG, debug, "Ready to download file " + destinationFile.getPath() + " from url: " + urlSample(url));

        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        HttpClientParams.setRedirecting(client.getParams(), true);
        HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout);
        HttpConnectionParams.setSoTimeout(client.getParams(), timeout);
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Savelog.w(TAG, "Error " + statusCode + " while retrieving file from url: " + urlSample(url));
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();

                    if (destinationFile!=null && destinationFile.exists()) destinationFile.delete();
                    saveStreamAsFile(inputStream, destinationFile);
                    Savelog.d(TAG, debug, "Download succeeded");
                    if (debug) {
                        exportInternalFile(App.getContext(), destinationFile);
                    }
                    return destinationFile;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
            Savelog.w(TAG, "Error while retrieving file from url: " + urlSample(url));
            Savelog.e(TAG, "exception: ", e);
            throw e;
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }



    // An alternative that's not currently used.
    private static File download2(String urlString, File destinationFile) throws Exception {
        URL url = new URL(urlString);

        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        urlConnection = (HttpURLConnection) url.openConnection();
        in = new BufferedInputStream(urlConnection.getInputStream());

        // download the data now
        IO.saveStreamAsFile(in, destinationFile);
        return destinationFile;
    }





    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static class NetworkUnavailableException extends IOException {
        private static final long serialVersionUID = 1328837993888456823L;
        public NetworkUnavailableException() { super();}
        public NetworkUnavailableException(String message) { super(message); }
    }




    // The MediaPlayer used by the audio fragment requires its files to have
    // world-readable permission.
    private static File getExternalCacheFilePath(Context context, String filename) {
        File cacheDir = getExternalCacheDirectory(context);
        if (cacheDir!=null)
            return new File(cacheDir, filename);
        else
            return null;
    }

    private static File getExternalCacheDirectory(Context context) {
        File cacheDir = null;
        if (checkExternalMedia()) {
            cacheDir = context.getExternalCacheDir();
            if (!cacheDir.exists()) // if cacheDir is null throws NullPointerException
                cacheDir.mkdirs();
            return cacheDir;
        }
        return null;
    }

    public static File getDefaultExternalPath() {
        // if the default directory does not exist, create it!
        File defaultPath = null;
        File root;
        if (checkExternalMedia()) {
            root = Environment.getExternalStorageDirectory();
        }
        else {
            root = Environment.getDownloadCacheDirectory();
        }


        if (root.getAbsolutePath()!=null) {
            defaultPath = new File (root.getAbsolutePath() + externalFolder);
            if (!defaultPath.exists()) {
                Log.i(TAG, "Creating directory "+defaultPath);  // Since the external directory is not yet available, we cannot call Savelog here!!!
                defaultPath.mkdirs();
            }
            else {  // there is something at the location.
                // If that something is a file, remove it and replace it by
                // a directory.
                if (!defaultPath.isDirectory()) {
                    boolean success = defaultPath.delete();
                    if (success) {
                        defaultPath.mkdirs();
                    }
                    else {
                        // Unable to remove file. Return null.
                        defaultPath = null;
                    }
                }
                else {
                    // Default external directory already exists. No need to do anything.
                }
            }
        }
        else {
            // No external media available.
            // Do nothing. Return null.
            defaultPath = null;
        }

        return defaultPath;
    }

    private static boolean checkExternalMedia(){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        return (mExternalStorageAvailable && mExternalStorageWriteable);
    }



    private static String urlSample(String url) {
        final int limit = 20;
        // Only show the first 20 characters in url for identification. Used only in error reports.
        // return url;

        if (debug)
            return url;
        else
            return url.substring(0, limit) + "...";
    }
}
