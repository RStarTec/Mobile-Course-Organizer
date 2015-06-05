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

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.fields.PrivateSiteFields;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;

import java.io.File;

/**
 * Created by AHui on 1/17/15.
 */
public class PrivateSiteUtils {

    public static boolean setupPrivateSite(Context targetContext) throws Exception {
        PrivateSite privateSite = PrivateSite.get(targetContext);
        DataSet dataSet = new DataSet(targetContext, 0);
        PrivateSiteFields.Variables.setPublicLink(privateSite, dataSet.privateSiteLink);
        privateSite.clear(targetContext); // must clear first to force download
        privateSite.setup(targetContext);
        return privateSite.isInitialized();
    }

    public static class PrivateSiteFileData {
        private static final String TAG = PrivateSiteFileData.class.getSimpleName();
        public String privateLink = "";

        public void clear() {
            privateLink = "";
        }

        public PrivateSiteFileData(Context targetContext) throws Exception {
            PrivateSiteFields.Constants constants = new PrivateSiteFields.Constants();
            try {
                File f = IO.getInternalFile(targetContext, constants.SiteFilename);
                String data = IO.loadFileAsString(targetContext, f);
                privateLink = data.trim();
            } catch (Exception e) {
                Savelog.i(TAG, "Cannot open file");
                this.clear();
            }
            constants.detach();
        }
    }






    public static class DataSet {
        public static final String publicTestSite = AppSettings.PublicSite + "sitetest/";
        public String privateSiteLink = "";
        public File privateSiteFile;

        public DataSet(Context targetContext, int set) throws Exception {
            if (set==0) {
                privateSiteLink = AppSettings.PrivateServerLink; // The only correct one
                privateSiteFile = IO.getInternalFile(targetContext, PrivateSiteFields.Constants.getSiteFilename());
            }
            else if (set==-1) {
                privateSiteLink = publicTestSite + "site_bad1.txt";
                privateSiteFile = IO.getInternalFile(targetContext, PrivateSiteFields.Constants.getSiteFilename());
            }
            else if (set==-2) {
                privateSiteLink = publicTestSite + "site_bad2.txt";
                privateSiteFile = IO.getInternalFile(targetContext, PrivateSiteFields.Constants.getSiteFilename());
            }
            else if (set==-3) {
                privateSiteLink = publicTestSite + "site_bad3.txt";
                privateSiteFile = IO.getInternalFile(targetContext, PrivateSiteFields.Constants.getSiteFilename());
            }
        }
    }

}
