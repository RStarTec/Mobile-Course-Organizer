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

package com.rstar.mobile.csc205sp2015.search;

import android.content.Context;

import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Header;
import com.rstar.mobile.csc205sp2015.module.Module;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by AHui
 */
public class Search {
    private static final String TAG = Search.class.getSimpleName()+"_class";
    private static final boolean debug = false;

    private static final String delimiter_newline = "\\n";

    // Database Helper
    DatabaseHelper db;

    public Search(Context context) {
        Savelog.d(TAG, debug, "db create()");
        db = new DatabaseHelper(context.getApplicationContext());
    }

    public static void setup(Context context, Module module) {
        Savelog.d(TAG, debug, "setup()");

        if (module==null) return;
        DatabaseHelper db;
        db = new DatabaseHelper(context.getApplicationContext());

        int moduleNumber = module.getModuleNumber();
        String lastUpdateDate = module.getLastUpdateDate();

        if (!db.isUpToDate(moduleNumber, lastUpdateDate)) {

            Savelog.d(TAG, debug, "module " + moduleNumber + " needs to be updated");
            // Delete all old data first.
            db.deleteEntries(moduleNumber, lastUpdateDate);

            // now see if we need to create or update segments
            for (int pageNumber=1; pageNumber<=module.getNumberOfPages(); pageNumber++) {
                File f = module.getTranscriptFile(context, pageNumber);
                if (f!=null && f.exists()) {
                    try {
                        String data = IO.loadFileAsString(context, f);
                        data = data.trim(); // Remove trailing newlines
                        String segments[] = data.split(delimiter_newline);

                        Savelog.d(TAG, debug, "going to insert " + segments.length + " segments for module " + moduleNumber + " page " + pageNumber);
                        db.updateEntries(moduleNumber, pageNumber, segments, lastUpdateDate);
                    } catch (IOException e) {
                        Savelog.w(TAG, "Cannot set up sqlite search data for module " + moduleNumber + " page " + pageNumber);
                    }

                }
            }
        }
        db.close();
    }

    public static void delete(Context context, Module module) {
        Savelog.d(TAG, debug, "delete()");

        if (module==null) return;
        DatabaseHelper db;
        db = new DatabaseHelper(context.getApplicationContext());

        int moduleNumber = module.getModuleNumber();
        String lastUpdateDate = Header.NoDate;
        Savelog.d(TAG, debug, "module " + moduleNumber + " needs to be deleted");
        db.deleteEntries(moduleNumber, lastUpdateDate);
        db.close();
    }


    public void listSegments() {
        ArrayList<Item> items = db.getAllSegments();
        for (int index=0; index<items.size(); index++) {
            Item item = items.get(index);
            Savelog.d(TAG, debug, "m" + item.getModuleNumber() + " p" + item.getPageNumber() + " : "+item.getSegment());
        }
    }

    public ArrayList<Item> getSegmentMatching(String pattern) {
        if (db==null) return null;
        return db.getSegmentMatching(pattern);
    }

    public ArrayList<Item> getSegmentMatching(String pattern, int moduleNumber) {
        if (db==null) return null;
        return db.getSegmentMatching(moduleNumber, pattern);
    }


    public void close() {
        if (db!=null)
            db.close();
    }

    public static void clear(Context context) {
        // remove file
        File file = DatabaseHelper.getDatabaseFile(context);
        if (file!=null && file.exists())
            file.delete();
    }

    public static class Item {
        private int moduleNumber = 0;
        private int pageNumber = 0;
        private CharSequence segment = null;
        public Item(int moduleNumber, int pageNumber, CharSequence segment) {
            if (moduleNumber>0) {
                this.moduleNumber = moduleNumber;
            }
            if (pageNumber>0) {
                this.pageNumber = pageNumber;
            }
            if (segment!=null) {
                this.segment = segment;
            }
        }

        public int getModuleNumber() {
            return moduleNumber;
        }
        public int getPageNumber() {
            return pageNumber;
        }
        public CharSequence getSegment() {
            return segment;
        }
    }

}
