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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;

import com.rstar.mobile.csc205sp2015.app.App;
import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;


public class DatabaseHelper extends SQLiteOpenHelper {
 
    // Logcat tag
    private static final String TAG = DatabaseHelper.class.getName();
    private static final boolean debug = AppSettings.defaultDebug;
    
    private static final int haloBlue = Color.rgb(51, 181, 229);  // unused

    private static int color_highlight;  // pick a color that does not conflict with the theme

    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "TranscriptManager";
 
    // Table Names
    private static final String TABLE_SEGMENTS = "segments_table";
    private static final String TABLE_MODULEDATES = "moduleDates_table";  // used to record when the module was last updated


    // Segments Table - column names
    private static final String KEY_MODULENUMBER = "moduleNumber";
    private static final String KEY_PAGENUMBER = "slideNumber";
    private static final String KEY_SEGMENT = "segment";

    // Module dates Table - column names
    private static final String KEY_MODULEDATE = "moduleDate";

    static {
        color_highlight = App.getContext().getResources().getColor(R.color.text_highlight);
    }
 
    // Table Create Statements

    // Segments table create statement
    // This table has 3 fields: (moduleNumber, pageNumber, segment)
    private static final String CREATE_TABLE_SEGMENTS = 
            "CREATE TABLE IF NOT EXISTS " + TABLE_SEGMENTS 
            + "(" + KEY_MODULENUMBER + " INTEGER,"
                + KEY_PAGENUMBER + " INTEGER,"
                + KEY_SEGMENT + " TEXT" + ")";

    // Module dates table create statement
    // This table has 2 fields: (moduleNumber, date)
    // where date is a long integer but stored as text
    private static final String CREATE_TABLE_MODULEDATES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MODULEDATES
                    + "(" + KEY_MODULENUMBER + " INTEGER,"
                    + KEY_MODULEDATE + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        Savelog.d(TAG, debug, "called onCreate()");
        
        if (db==null || db.isReadOnly()) {
            db.close();
            db = this.getWritableDatabase();
        }
        if (db!=null) {
            // creating required tables
            Savelog.d(TAG, debug, "calling " + CREATE_TABLE_SEGMENTS);
            Savelog.d(TAG, debug, "calling " + CREATE_TABLE_MODULEDATES);

            db.execSQL(CREATE_TABLE_SEGMENTS);
            db.execSQL(CREATE_TABLE_MODULEDATES);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static File getDatabaseFile(Context context) {
        final String path = context.getDatabasePath(DATABASE_NAME).getPath();
        if (path==null) return null;
        File file = new File(path);
        return file;
    }


    public boolean isUpToDate(int moduleNumber, String moduleDate) {
        if (moduleDate==null) moduleDate = "";

        boolean result = true;
        SQLiteDatabase db = this.getReadableDatabase();
        if (getTableSize(db, TABLE_MODULEDATES)==0) { // no record at all
            result = false;
        }
        else if (!getModuleDate(moduleNumber).equals(moduleDate)) { // recorded date is smaller
            result = false;
        }
        db.close();
        return result;
    }



    private int getTableSize(SQLiteDatabase db, String tableName) {
        int rowCount = 0;
        if (tableName == null) { return rowCount; }
        if (db==null || !db.isOpen()) {
            db = this.getReadableDatabase();
        }

        Cursor cursor;
        
        cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", 
                new String[] {"table", tableName});
        int tableExists = 0;

        if (cursor.moveToFirst()) {
            tableExists = cursor.getInt(0);
        }
        cursor.close();
                
        // Expect tableExists=1 if a table exists, 0 means no table exists by this name.
        if (tableExists>0) {
            int numRows = 0;
            Savelog.d(TAG, debug, "Table " + tableName + " exists");
            // Check if table is empty
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null) ;
            if (cursor.moveToFirst()) {
                numRows = cursor.getInt(0);
            }
            cursor.close();
            rowCount = numRows;
        }
        return rowCount;
    }


    public String getModuleDate(int moduleNumber) {
        String selectQuery = "SELECT "+ KEY_MODULEDATE +" FROM " + TABLE_MODULEDATES
                             + " WHERE " + KEY_MODULENUMBER + " = " + moduleNumber;
        Savelog.d(TAG, debug, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        String dateString = "";
        // looping through the rows. Should really expect 1 row!
        int row = 0;
        if (c.moveToFirst()) {
            do {
                dateString = c.getString((c.getColumnIndex(KEY_MODULEDATE)));
                row++;
            } while (c.moveToNext());
        }
        c.close();
        db.close();

        Savelog.d(TAG, debug, "module date on existing db is: " + dateString);

        return dateString;
    }


    public void deleteEntries(int moduleNumber, String moduleDate) {
        if (moduleDate==null) moduleDate = "";

        SQLiteDatabase db = this.getWritableDatabase();

        Savelog.d(TAG, debug, "called getWritableDatabase()");

        // empty out any existing entries in table first
        String deleteSegments = KEY_MODULENUMBER + " = " + moduleNumber;

        db.delete(TABLE_SEGMENTS, deleteSegments, null);
        Savelog.d(TAG, debug, "deleting entries on module " + moduleNumber);

        // now update the date table
        String deleteDate = KEY_MODULENUMBER + " = " + moduleNumber;
        db.delete(TABLE_MODULEDATES, deleteDate, null);

        ContentValues insertValue = new ContentValues();
        insertValue.put(KEY_MODULENUMBER, moduleNumber);
        insertValue.put(KEY_MODULEDATE, moduleDate);
        db.insert(TABLE_MODULEDATES, null, insertValue);
        Savelog.d(TAG, debug, "updated module date=" + moduleDate);

        db.close();
    }


    public void updateEntries(int moduleNumber, int pageNumber, String segments[], String moduleDate) {
        if (moduleDate==null) moduleDate = "";

        SQLiteDatabase db = this.getWritableDatabase();

        Savelog.d(TAG, debug, "called getWritableDatabase()");
        
        // empty out any existing entries in table first
        String deleteSegments = KEY_MODULENUMBER + " = " + moduleNumber
                + " AND " + KEY_PAGENUMBER + " = " + pageNumber;

        db.delete(TABLE_SEGMENTS, deleteSegments, null);
        Savelog.d(TAG, debug, "deleting entries for module " + moduleNumber + " page " + pageNumber);

        // If segments are provided, then insert them.
        if (segments!=null && segments.length>0) {
            String sql = "INSERT INTO " + TABLE_SEGMENTS + " VALUES (?, ?, ?)";
            db.beginTransaction();

            int count = 0;
            SQLiteStatement stmt = db.compileStatement(sql);
            for (int i = 0; i < segments.length; i++) {
                // Ignore strings of whitespaces.
                if (segments[i].trim().length()>0) {
                    count++;
                    stmt.bindLong(1, moduleNumber);
                    stmt.bindLong(2, pageNumber);
                    stmt.bindString(3, segments[i]);
                    stmt.execute();
                    stmt.clearBindings();
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            Savelog.d(TAG, debug, "finished inserting " + count + " records " + " for module " + moduleNumber + " page " + pageNumber);
        }

        // now update the date table
        String deleteDate = KEY_MODULENUMBER + " = " + moduleNumber;
        db.delete(TABLE_MODULEDATES, deleteDate, null);

        ContentValues insertValue = new ContentValues();
        insertValue.put(KEY_MODULENUMBER, moduleNumber);
        insertValue.put(KEY_MODULEDATE, moduleDate);
        db.insert(TABLE_MODULEDATES, null, insertValue);
        Savelog.d(TAG, debug, "updated module date=" + moduleDate);

        db.close();
    }
    
    

    
    
    public ArrayList<Search.Item> getAllSegments() {
        ArrayList<Search.Item> segmentList = new ArrayList<Search.Item>();
        String selectQuery = "SELECT * FROM " + TABLE_SEGMENTS;
        Savelog.d(TAG, debug, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                int moduleNumber = c.getInt((c.getColumnIndex(KEY_MODULENUMBER)));
                int pageNumber = c.getInt((c.getColumnIndex(KEY_PAGENUMBER)));
                String segment = c.getString(c.getColumnIndex(KEY_SEGMENT));

                // adding to list
                segmentList.add(new Search.Item(moduleNumber, pageNumber, segment));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return segmentList;
    }


    public ArrayList<Search.Item> getSegmentMatching(int moduleNumber, String pattern) {
        ArrayList<Search.Item> resultList = new ArrayList<Search.Item>();
        if (pattern==null || pattern.trim().length()==0) return resultList;

        String selectQuery = "SELECT " + KEY_MODULENUMBER + ", " + KEY_PAGENUMBER + ", " + KEY_SEGMENT
                + " FROM " + TABLE_SEGMENTS + " WHERE " + KEY_MODULENUMBER + " = " + moduleNumber
                + " AND " + KEY_SEGMENT + " LIKE '%" + pattern + "%' "
                + " ORDER BY " + KEY_PAGENUMBER;


        Savelog.d(TAG, debug, "Calling rawQuery with " + selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);
        Savelog.d(TAG, debug, "query completed");

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                int matchedModuleNumber = c.getInt((c.getColumnIndex(KEY_MODULENUMBER)));
                int pageNumber = c.getInt((c.getColumnIndex(KEY_PAGENUMBER)));
                String segment = c.getString(c.getColumnIndex(KEY_SEGMENT));

                // add to list
                CharSequence segmentMatch = showAllMatches(segment, pattern);
                if (segmentMatch==null) segmentMatch = segment;

                if (moduleNumber==matchedModuleNumber) {
                    resultList.add(new Search.Item(moduleNumber, pageNumber, segmentMatch));
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return resultList;
    }


    public ArrayList<Search.Item> getSegmentMatching(String pattern) {
        ArrayList<Search.Item> resultList = new ArrayList<Search.Item>();
        if (pattern==null || pattern.trim().length()==0) return resultList;

        String selectQuery = "SELECT " + KEY_MODULENUMBER + ", " + KEY_PAGENUMBER + ", " + KEY_SEGMENT
                + " FROM " + TABLE_SEGMENTS + " WHERE " + KEY_SEGMENT + " LIKE '%" + pattern + "%' "
                + " ORDER BY " + KEY_MODULENUMBER + ", " + KEY_PAGENUMBER;


        Savelog.d(TAG, debug, "Calling rawQuery with " + selectQuery);
        
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);
        Savelog.d(TAG, debug, "query completed");
        
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                int moduleNumber = c.getInt((c.getColumnIndex(KEY_MODULENUMBER)));
                int pageNumber = c.getInt((c.getColumnIndex(KEY_PAGENUMBER)));
                String segment = c.getString(c.getColumnIndex(KEY_SEGMENT));

                // add to list
                CharSequence segmentMatch = showAllMatches(segment, pattern);
                if (segmentMatch==null) segmentMatch = segment;


                resultList.add(new Search.Item(moduleNumber, pageNumber, segmentMatch));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return resultList;
    }
    

    
    private static CharSequence showAllMatches(String data, String pattern) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        
        String dataLowercase = data.toLowerCase(Locale.getDefault());
        String patternLowercase = pattern.toLowerCase(Locale.getDefault());
        
        int index = dataLowercase.indexOf(patternLowercase);
        while (index>=0) {
            indices.add(index);
            index = dataLowercase.indexOf(patternLowercase, index+patternLowercase.length());
        }
        
        if (indices.size()==0) return null;
        
        SpannableStringBuilder ssb = new SpannableStringBuilder(data);
        CharacterStyle cs;
 
        for (int i=0; i<indices.size(); i++) {
            int start = indices.get(i);
            int end = start + pattern.length();
            cs = new BackgroundColorSpan(color_highlight);  // highlight text
            ssb.setSpan(cs, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return ssb;
    }



}

