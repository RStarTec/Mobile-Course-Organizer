

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

package com.rstar.mobile.csc205sp2015.course;

import android.content.Context;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Header;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Course {
    private static final String TAG = Course.class.getSimpleName();
    private static final boolean debug = AppSettings.defaultDebug;

    private static final String CourseFilename = "info.txt";
    private static final String LineDelimiter = "\\n";

    private static final String DefaultLink = AppSettings.PublicInfoLink;

    private List<Module> moduleList = null;
    private Map<String, Module> moduleMap = null;
    private String serverPasscode = null;
    private String link = DefaultLink; // allow us to use different link for test purpose

    private static Course sCourse = null;
    /* Note: since sCource is a static variable, it may remind on memory even after the
     * user has closed the app.
     * According to http://stackoverflow.com/questions/1944369/android-static-object-lifecycle-application-act-crazy
     * Static variables are cleared only when one of the following occurs:
     * 1. the class is unloaded
     * 2. the VM shuts down
     * 3. the process dies
     * So it is possible that the app stays on device until some other process needs to claim its memory.
     *
     * In this class, the static course variable sCourse may remain "installed" even if the data file
     * that holds the course info is absent (such as when developer removes all internal files).
     * This does not really affect the integrity of the course variable since the info file does not change.
     * Later on, after the static variable is reclaimed, the app will trigger a new download to obtain a
     * new copy of the data file.
     */

    private Course() {
        // Do not allow the variables to be null.
        moduleList = new ArrayList<Module>();
        moduleMap = new HashMap<String, Module>();
        serverPasscode = "";
    }

    public static Course get(Context context) {
        if (sCourse==null) {
            sCourse = new Course();
        }
        else {
            // course pre-exists as static
        }
        if (!sCourse.isInstalled()) {
            try {
                // If creating the first time, then load data from file if file is available
                sCourse.load(context);
            }
            catch (Exception e) {
                Savelog.w(TAG, "Cannot load course information file.", e);
                // do not clear course. Leave bad file here.
            }
        }
        return sCourse;
    }


    // Setup must be run within an asyncTask
    public void setup(Context context) {
        Savelog.d(TAG, debug, "setup() called.");

        File f = getFile(context);
        try {
            // If file exists, then no need to download again
            if (!f.exists()) {
                Savelog.d(TAG, debug, "download course from " + link);
                IO.downloadFile(link, f);
            }
            load(context);
        }
        catch (Exception e) {
            Savelog.w(TAG, "Cannot setup course information file.", e);
            clear(context);
        }
    }


    private void load(Context context) throws Exception {
        File f = getFile(context);
        String data = IO.loadFileAsString(context, f);

        if (moduleList==null)
            moduleList = new ArrayList<Module>();
        else
            moduleList.clear();

        if (moduleMap==null)
            moduleMap = new HashMap<String, Module>();
        else
            moduleMap.clear();

        String[] record = data.split(LineDelimiter);
        serverPasscode = record[0];  // First line in data is the passcode for server access.

        // All subsequent lines are module descriptions
        for (int entry=1; entry<record.length; entry++) {
            Header header = new Header(record[entry]);
            Savelog.d(TAG, debug, "header=" + header.toString());

            Module module = new Module(context, header);
            moduleList.add(module);
            moduleMap.put(Integer.valueOf(header.getModuleNumber()).toString(), module);
        }
        Savelog.d(TAG, debug, "total modules=" + moduleList.size());
    }

    public void reload(Context context, boolean trim) {
        // Reload basically clear everything and download the data from public server again.
        // There is one catch. If the server passcode has changed, then a new private server
        // is in use. The user is expected to know the new passcode to get access for certain homework functions.

        // First, make a copy of the old items
        String oldServerPasscode = serverPasscode;
        // no need to duplicate every module. Just let the oldModule list point to the existing modules
        List<Module> oldModuleList = new ArrayList<Module>();
        oldModuleList.addAll(moduleList);

        if (trim) {
            clear(context);
        }
        setup(context);

        if (!oldServerPasscode.equals(serverPasscode)) {
            Savelog.d(TAG, debug, "old serverpasscode=" + oldServerPasscode + " new=" + serverPasscode);
            PrivateSite.get(context).clear(context);
        }

        // For each old module, check if it exists unchanged in the new course listing.
        // If an old module has been changed, then completely remove it.
        for (Module oldModule : oldModuleList) {
            if (oldModule.isInstalled()) {
                int moduleNumber = oldModule.getModuleNumber();
                Module updatedModule = getModule(moduleNumber);
                if (updatedModule==null) {
                    Savelog.d(TAG, debug, "module is gone in new course info. Need to clear it.");
                    oldModule.clear(context);
                }
                else if (!oldModule.equals(updatedModule)) {
                    Savelog.d(TAG, debug, "old header=" + oldModule.getHeaderDescription());
                    Savelog.d(TAG, debug, "new header=" + updatedModule.getHeaderDescription());

                    Savelog.d(TAG, debug, "old date=" + oldModule.getLastUpdateDate());
                    Savelog.d(TAG, debug, "new date=" + updatedModule.getLastUpdateDate());
                    Savelog.d(TAG, debug, "module has changed in new course info. Need to clear it.");
                    oldModule.clear(context);

                    // The new module might have hooked up to the old video list in the old directory.
                    // Make sure the new module is up-to-date
                    updatedModule.syncWithDirectory(context);
                    // Right now, not verify other things such as videos, extra etc. Just check the number of pages and title.
                }
            }
        }
    }

    public void clear(Context context) {
        Savelog.d(TAG, debug, "Called clear()");
        // Clear both variable and file. There is only one static variable of this class. So this needs to be cleared.
        if (moduleMap!=null) moduleMap.clear();
        if (moduleList!=null) moduleList.clear();
        serverPasscode = "";
        File f = getFile(context);
        if (f!=null && f.exists()) f.delete();
        // do not reset link because it is supposed to be constant
    }


    private File getFile(Context context) {
        File f = IO.getInternalFile(context, CourseFilename);
        Savelog.d(TAG, debug, "info file: " + f.getAbsolutePath());
        return f;
    }

    public boolean isInstalled(){
        boolean complete = true;
        if (serverPasscode==null || serverPasscode=="") complete = false;
        else if (moduleList==null || moduleList.size()==0) complete = false;
        else if (moduleMap==null || moduleMap.size()==0) complete = false;
        else {
            for (Module module : moduleList) {
                if (module.getNumberOfPages()==0 ||
                        module.getTitle()==null || module.getTitle().length()==0 ||
                        module.getModuleNumber()<=0) {
                    complete = false;
                }
            }
        }
        return complete;
    }

    public List<Module> getModuleList() {
        return moduleList;
    }
    private Map<String, Module> getModuleMap() {
        return moduleMap;
    }

    public int getNumberOfModules() {
        if (moduleList==null) return 0;
        else return moduleList.size();
    }

    public Module getModule(int moduleNumber) {
        if (moduleMap==null) return null;
        return moduleMap.get(Integer.valueOf(moduleNumber).toString());
    }

    public boolean matchPasscode(String guess) {
        if (serverPasscode==null || serverPasscode.length()==0) return false; // never match if serverPasscode is not set
        return serverPasscode.equals(guess);
    }

}
