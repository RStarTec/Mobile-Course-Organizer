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

public class Zip {
    private static final String TAG = Zip.class.getSimpleName()+"_class";
    private static final boolean debug = false;

    public static final String dirDelimiter1 = "\\/";
    public static final String dirDelimiter2 = "\\\\";
    public static final String dot = ".";
    public static final String underscore = "_";

    public static String getFileName(String filePath) {
        if (filePath==null || filePath.length()==0) return filePath;

        String[] field = filePath.split(dirDelimiter1);
        if (field.length==1) {
            field = filePath.split(dirDelimiter2);
        }


        for (String f : field) {
            Savelog.d(TAG, debug, "split field:" + f + "\n");
        }
        int last = field.length-1;
        return field[last];
    }

    public static boolean isSkippable(String simpleFilename) {
        if (simpleFilename.startsWith(dot)) return true;
        if (simpleFilename.startsWith(underscore)) return true;
        else return false;
    }
}
