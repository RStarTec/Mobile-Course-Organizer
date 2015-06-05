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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.app.InitActivity;
import com.rstar.mobile.csc205sp2015.developer.DeveloperMenu;
import com.rstar.mobile.csc205sp2015.developer.TesterMenu;
import com.rstar.mobile.csc205sp2015.info.HelpActivity;
import com.rstar.mobile.csc205sp2015.download.DownloadActivity;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.module.Module;
import com.rstar.mobile.csc205sp2015.module.ModuleActivity;
import com.rstar.mobile.csc205sp2015.module.QuizDialogFragment;
import com.rstar.mobile.csc205sp2015.registered.login.LoginActivity;
import com.rstar.mobile.csc205sp2015.registered.login.UnlockActivity;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.search.SearchActivity;
import com.rstar.mobile.csc205sp2015.search.SearchFragment;
import com.rstar.mobile.csc205sp2015.tools.ToolsActivity;


public class ModuleListActivity extends Activity {
    private static final String TAG = ModuleListActivity.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final int SearchRequestCode = ModuleListActivity.class.hashCode();  //must be unique!
    private static final int DownloadModuleRequestCode = ModuleListActivity.class.hashCode() + 1;  // must be unique
    private static final int DownloadCourseReloadRequestCode = InitActivity.class.hashCode() + 2;  // must be unique


    private DeveloperMenu developerMenu = null;
    private TesterMenu testerMenu = null;
    private static final int developerMenuGroupId = 1;
    private static final int testerMenuGroupId = 2;

    private int fragmentId;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "onCreate() called.");

        setContentView(R.layout.activity_module_list);
        fragmentId = R.id.activityModuleList_container;

        FragmentManager fm = getFragmentManager();
        if (savedInstanceState == null) {
            fragment = ModuleListFragment.newInstance();
            fm.beginTransaction().add(fragmentId, fragment).commit();
        }
        else {
            fragment = fm.findFragmentById(fragmentId);
            ((ModuleListFragment) fragment).refreshList();
        }
    }


    // when return from other activities, check to see if data has changed.
    @Override
    protected void onRestart() {
        super.onRestart();
        ((ModuleListFragment) fragment).refreshList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.module_list, menu);

        if (AppSettings.developerMode) {
            developerMenu = new DeveloperMenu();
            int groupId = developerMenuGroupId;
            menu = developerMenu.addSubmenu(menu, R.string.menu_developerOptions, groupId);
        }
        if (AppSettings.testerEnabled) {
            testerMenu = new TesterMenu();
            int groupId = testerMenuGroupId;
            menu = testerMenu.addSubmenu(menu, R.string.menu_testerOptions, groupId);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.menu_moduleList_search:
            {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivityForResult(intent, SearchRequestCode);
                Savelog.d(TAG, debug, "started search with requestCode=" + SearchRequestCode);
                return true;
            }
            case R.id.menu_moduleList_login:
            {
                if (PrivateSite.get(this).isInitialized()) {
                    // no need to get result for this login
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra(LoginActivity.EXTRA_Type, LoginActivity.Type_existing);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(this, UnlockActivity.class);
                    startActivity(intent);
                }
                return true;
            }
            case R.id.menu_moduleList_reload:
            {
                Intent intent = new Intent(this, DownloadActivity.class);
                intent.putExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_courseReload);
                intent.putExtra(DownloadActivity.EXTRA_Trim, AppSettings.Trim);
                this.startActivityForResult(intent, DownloadCourseReloadRequestCode);
                return true;
            }
            case R.id.menu_moduleList_tools:
            {
                Intent intent = new Intent(this, ToolsActivity.class);
                intent.putExtra(ToolsActivity.EXTRA_Type, ToolsActivity.Type_default);
                this.startActivity(intent);
                return true;
            }
            case R.id.menu_moduleList_help:
            {
                Intent intent = new Intent(this, HelpActivity.class);
                this.startActivity(intent);
                return true;
            }
            case R.id.menu_moduleList_copyright:
            {
                CopyrightDialogFragment dialog = CopyrightDialogFragment.newInstance();
                dialog.show(getFragmentManager(), CopyrightDialogFragment.dialogTag);
            }
            // no default action
        }

        if (developerMenu!=null && item.getGroupId()==developerMenuGroupId && developerMenu.act(item.getItemId(), this)) {
            return true;
        }
        else if (testerMenu!=null && item.getGroupId()==testerMenuGroupId && testerMenu.act(item.getItemId(), this)) {
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }



    public void openModule(Module module, int pageNumber) {
        if (module==null) return;
        if (module.isInstalled()) {
            // if module already installed, then go directly to module
            Intent intent = new Intent(this, ModuleActivity.class);
            intent.putExtra(ModuleActivity.EXTRA_ModuleNumber, module.getModuleNumber());
            intent.putExtra(ModuleActivity.EXTRA_PageNumber, pageNumber);
            startActivity(intent);
        }
        else {
            // if module not installed, then need to install it first.
            // Then go to default page.
            Intent intent = new Intent(this, DownloadActivity.class);
            intent.putExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_module);
            intent.putExtra(DownloadActivity.EXTRA_ModuleNumber, module.getModuleNumber());
            intent.putExtra(DownloadActivity.EXTRA_PageNumber, pageNumber);
            intent.putExtra(DownloadActivity.EXTRA_Trim, AppSettings.Trim);
            startActivityForResult(intent, DownloadModuleRequestCode);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Request code was sent out from HomeworkBlankFragment
        // But when result comes back, the extra is from HomeworkSetupFragment
        if (requestCode == ModuleListActivity.SearchRequestCode) {
            if(resultCode == RESULT_OK && data!=null){
                int newModuleNumber = data.getIntExtra(SearchFragment.EXTRA_newModuleNumber, Module.DefaultModuleNumber);
                int newPageNumber = data.getIntExtra(SearchFragment.EXTRA_newPageNumber, Module.DefaultPageNumber);

                if (newModuleNumber!=Module.DefaultModuleNumber) {
                    Module module = Course.get(this).getModule(newModuleNumber);
                    openModule(module, newPageNumber);
                }
            }
        }
        else if (requestCode == ModuleListActivity.DownloadModuleRequestCode) {
            boolean status = false;
            if (resultCode == RESULT_OK && data!=null) {
                int downloadType = data.getIntExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_default);
                int moduleNumber = data.getIntExtra(DownloadActivity.EXTRA_ModuleNumber, Module.DefaultModuleNumber);
                if (downloadType == DownloadActivity.Type_module) {
                    // Update the information shown on the module list
                    ((ModuleListFragment) fragment).refreshList();

                    // Right after download, open the default page
                    Intent intent = new Intent(this, ModuleActivity.class);
                    intent.putExtra(ModuleActivity.EXTRA_ModuleNumber, moduleNumber);
                    intent.putExtra(ModuleActivity.EXTRA_PageNumber, Module.DefaultPageNumber);
                    startActivity(intent);
                    status = true;
                }
            }
            if (!status) {
                Savelog.d(TAG, debug, "Failed to download module ");
            }
        }

        else if (requestCode == ModuleListActivity.DownloadCourseReloadRequestCode) {
            boolean status = false;
            if (resultCode==RESULT_OK && data!=null) {
                int downloadType = data.getIntExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_default);
                if (downloadType==DownloadActivity.Type_courseReload) {
                    // Update the information shown on the module list
                    ((ModuleListFragment) fragment).refreshList();
                    status = true;
                }
            }
            if (!status) {
                Savelog.d(TAG, debug, "Failed to reload course ");
            }
        }

        else {
            // Pass result to parent activity. Very important!!!
            super.onActivityResult(requestCode, resultCode, data);
        }
    }//onActivityResult
}
