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

package com.rstar.mobile.csc205sp2015.registered.login;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.rstar.mobile.csc205sp2015.app.App;
import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.developer.DeveloperOptions;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.api.LoginApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by AHui on 12/19/14.
 */
public class Student {
    private static final String TAG = Student.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final String PREF_accessCode = Student.class.getSimpleName()+".accessCode";
    private static final String PREF_loginKey = Student.class.getSimpleName()+".loginKey";
    private static final String DefaultAccessCode = LoginApi.DefaultAccessCode;
    private static final String LoginCacheFilename = "cachelogin";
    private static final String dir = "/";
    // login cache is valid for 30 minutes or this many milliseconds
    private static final long DefaultLoginCacheTimeLimit = 30*60*1000;
    // Encrypt cached login information using algorithm
    private static final String ALGO = "AES";
    private static final String RandomSeed = "EA=Q$RQGWuRDgqfggAFtbhqGSQVHH%#$RGHHa#B";

    public static final String StudentLabel = "stu";

    // For debugging purpose, allow developer to tune the amount of time to cache
    private static long LoginCacheTimeLimit = DefaultLoginCacheTimeLimit;

    private String accessCode = DefaultAccessCode;

    // If in debugging mode, then allow developer to tune parameters
    static {
        if (debug) {
            Context context = App.getContext();
            DeveloperOptions developerOptions = new DeveloperOptions(context);
            LoginCacheTimeLimit = developerOptions.getLoginExpiration() * 60*1000; // convert minutes to milli-seconds
        }
    }


    public Student(Context context) {
        load(context);
        File folder = getStudentDir(context);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }


    public String getAccessCode() {
        return accessCode;
    }

    // This requires network access. Therefore, it must be run within an asyncTask
    public boolean signup(Context context, String userId, String password, String email) {
        try {
            LoginApi loginApi = new LoginApi(context, userId, password, null, email, LoginApi.request_signup);

            String newAccessCode = loginApi.getAccessCode();
            if (newAccessCode==null || newAccessCode.length()==0) newAccessCode = DefaultAccessCode;

            if (debug) {
                String data = loginApi.getCommunication();
                Savelog.d(TAG, debug, "Signup data:\n" + data);
            }
            Savelog.d(TAG, debug, "Access code: " + newAccessCode);

            // Remember the registration result only if attempt is successful
            if (newAccessCode!=DefaultAccessCode) {  // use != to make exact comparison
                // Note that access code is saved permanently
                // but login information is only cached for a limited time.
                accessCode = newAccessCode;
                save(context);
                saveLogin(context, userId, password);
                return true;
            }
        } catch (Exception e) {
            Savelog.w(TAG, "Signup failed. Keep old access code.", e);
        }
        return false;
    }

    // This requires network access. Therefore, it must be run within an asyncTask
    public boolean signin(Context context, String userId, String password) {
        try {
            LoginApi loginApi = new LoginApi(context, userId, password, null, null, LoginApi.request_signin);

            String newAccessCode = loginApi.getAccessCode();
            if (newAccessCode==null || newAccessCode.length()==0) newAccessCode = DefaultAccessCode;

            if (debug) {
                String data = loginApi.getCommunication();
                Savelog.d(TAG, debug, "Signin data:\n" + data);
            }
            Savelog.d(TAG, debug, "Access code: " + newAccessCode);

            // Remember the registration result only if attempt is successful
            if (newAccessCode!=DefaultAccessCode) {  // use != to make exact comparison
                // Note that access code is saved permanently
                // but login information is only cached for a limited time.
                accessCode = newAccessCode;
                save(context);
                saveLogin(context, userId, password);
                return true;
            }
        } catch (Exception e) {
            Savelog.w(TAG, "Signin failed. Keep old access code.", e);
        }
        return false;
    }

    // This requires network access. Therefore, it must be run within an asyncTask
    public boolean changePassword(Context context, String userId, String password, String newPassword) {
        try {

            LoginApi loginApi = new LoginApi(context, userId, password, newPassword, null, LoginApi.request_passwd);

            if (debug) {
                String data = loginApi.getCommunication();
                Savelog.d(TAG, debug, "Signin data:\n" + data);
            }
            return loginApi.isOK();
        } catch (Exception e) {
            Savelog.w(TAG, "Change password failed. Keep old password.", e);
        }
        return false;
    }

    // This requires network access. Therefore, it must be run within an asyncTask
    public boolean resetPassword(Context context, String userId) {
        try {
            LoginApi loginApi = new LoginApi(context, userId, null, null, null, LoginApi.request_reset);

            if (debug) {
                String data = loginApi.getCommunication();
                Savelog.d(TAG, debug, "Signin data:\n" + data);
            }
            return loginApi.isOK();
        } catch (Exception e) {
            Savelog.w(TAG, "Reset password failed. Keep old password.", e);
        }
        return false;
    }

    private File getLoginCacheFile(Context context) {
        return new File(getStudentDir(context), LoginCacheFilename);
    }

    public static File getStudentDir(Context context) {
        String dirname = getStudentDirname();
        return IO.getInternalDir(context, dirname);
    }

    public static String getStudentDirname() {
        return StudentLabel + dir;
    } // Do not disclose student's appID at the directory level


    public void save(Context context) {
        Savelog.d(TAG, debug, "Saving accessCode=" + accessCode);
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .edit().putString(PREF_accessCode, accessCode).commit();
    }
    public void load(Context context) {
        accessCode = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .getString(PREF_accessCode, DefaultAccessCode);
        Savelog.d(TAG, debug, "Loading accessCode=" + accessCode);
    }
    public void clear(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .edit().remove(PREF_accessCode).commit();
    }

    public boolean isSignedup() {
        return (accessCode!=null && accessCode.length()>0 && !accessCode.equals(DefaultAccessCode));
    }

    public boolean isLoginExpired(Context context) {
        Login login = loadLogin(context);
        if (login==null || !login.isValid()) return true;
        else return false;
    }


    public void clearLogin(Context context) {
        // Remove the key
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .edit().remove(PREF_loginKey).commit();
        File file = getLoginCacheFile(context);
        // Remove the file
        if (file!=null && file.exists()) file.delete();
    }

    private void saveLogin(Context context, String userId, String password) {
        // Does not overwrite existing login if input is bad
        if (context==null) return;
        if (userId==null || userId.length()==0) return;
        if (password==null || password.length()==0) return;

        // Original text
        String originalText = userId + "\n" + password;

        // Set up secret key spec for 128-bit AES encryption and decryption
        SecretKeySpec sks = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(RandomSeed.getBytes());
            KeyGenerator kg = KeyGenerator.getInstance(ALGO);
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), ALGO);
        } catch (Exception e) {
            Savelog.w(TAG, "secret key spec error: ", e);
            return;
        }

        // Encode the original data
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(originalText.getBytes());
        } catch (Exception e) {
            Savelog.w(TAG, "encryption error: ", e);
            return;
        }

        // Now save the string to file
        // Save the secret key to preference
        try {
            File file = getLoginCacheFile(context);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(encodedBytes);
            bos.flush();
            bos.close();

            // Save secret key to a byte array, then convert to a string
            String stringKey = Base64.encodeToString(sks.getEncoded(), Base64.DEFAULT);
            PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                    .edit().putString(PREF_loginKey, stringKey).commit();
        } catch (Exception e) {
            Savelog.w(TAG, "Cannot save data: ", e);
            return;
        }
    }



    public Login loadLogin(Context context) {
        Savelog.d(TAG, debug, "loadLogin() called.");

        Login data = new Login();

        // Check that the login data is still usable.
        File file = getLoginCacheFile(context);
        if (file!=null && file.exists()) {
            long lastuse = file.lastModified();
            long timenow = System.currentTimeMillis();
            long timelapse = timenow - lastuse;
            Savelog.d(TAG, debug, "lastuse=" + lastuse + " timenow=" + timenow + " lapse=" + timelapse);
            if (timelapse>=LoginCacheTimeLimit) {
                Savelog.d(TAG, debug, "login cache expired. Limit=" + LoginCacheTimeLimit);
                clearLogin(context);
                return data;
            }
        }
        else {
            Savelog.d(TAG, debug, "login cache unavailable.");
            return data;
        }

        SecretKeySpec sks = null;
        byte[] encodedBytes = null;

        try {
            // Recover the secret key
            String stringKey = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString(PREF_loginKey, "");
            // If no key found, then there is no prior login.
            if (stringKey.length()==0) return data;

            byte[] encodedKey = Base64.decode(stringKey, Base64.DEFAULT);
            sks = new SecretKeySpec(encodedKey, 0, encodedKey.length, ALGO);

            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
            encodedBytes = new byte[bin.available()];
            bin.read(encodedBytes);
            bin.close();

            // Record the time we load the login data
            file.setLastModified(System.currentTimeMillis());

        } catch (Exception e) {
            Savelog.w(TAG, "Cannot load data.", e);
            clearLogin(context);
            return data;
        }


        // Decode the encoded data
        try {
            byte[] decodedBytes;
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(encodedBytes);

            String originalText = new String(decodedBytes);
            String[] fields = originalText.split("\\n");
            data.setUserId(fields[0]);
            data.setPassword(fields[1]);
        } catch (Exception e) {
            Savelog.e(TAG, "decryption error", e);
            clearLogin(context);
            return data;
        }

        return data;
    }

    public static class Login {
        private String userId = "";
        private String password = "";
        public Login() {}
        public boolean isValid() {
            return (userId!=null && userId.length()>0 && password!=null && password.length()>0);
        }
        public String getUserId() { return userId; }
        public String getPassword() { return password; }
        public void setUserId(String userId) {
            if (userId!=null) this.userId = userId;
        }
        public void setPassword(String password) {
            if (password!=null) this.password = password;
        }
    }
}
