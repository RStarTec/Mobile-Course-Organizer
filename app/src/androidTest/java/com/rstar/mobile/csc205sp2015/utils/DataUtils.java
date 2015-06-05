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

import java.io.File;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.search.Search;


public class DataUtils {
	public static final String TAG = DataUtils.class.getSimpleName()+"_class";

	public static void copyRawFileToInternal(Context testContext, Context targetContext, int id, File destinationFile) throws Exception {
		InputStream is = testContext.getResources().openRawResource(id);
		Log.i(TAG, "Saving test data file id=" + id + " to " + destinationFile.getAbsolutePath());
		//get the file as a stream from res/raw/
		IO.saveStreamAsFile(is, destinationFile);
		is.close();
	}

    public static void clearAll(Context targetContext) {
        // internal files
        IO.clearInternalFiles(targetContext);
        // Clear sqlite
        Search.clear(targetContext);
        // all default shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(targetContext);
        prefs.edit().clear().commit();
        // Clear all statics
        Course.get(targetContext).clear(targetContext);
        PrivateSite.get(targetContext).clear(targetContext);
    }
}
