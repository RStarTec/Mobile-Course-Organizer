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

package com.rstar.mobile.csc205sp2015.developer;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.SubMenu;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.api.PrivateSite;
import com.rstar.mobile.csc205sp2015.search.Search;
import com.rstar.mobile.csc205sp2015.tools.ToolsActivity;


public class DeveloperMenu {
    private static final String TAG = DeveloperMenu.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

	private int iconIds[] = {
			R.drawable.ic_action_discard,
            R.drawable.ic_action_discard,
			R.drawable.ic_action_discard,
            R.drawable.ic_action_discard,
            R.drawable.ic_action_discard,
			R.drawable.ic_action_send_now,
            R.drawable.ic_action_send_now,
            R.drawable.ic_action_accounts,
            R.drawable.ic_action_settings,
            R.drawable.ic_action_about
	};
	private int nameIds[] = {
            R.string.menu_developer_clearSQLite,
			R.string.menu_developer_clearInternal,
            R.string.menu_developer_clearPreferences,
			R.string.menu_developer_clearLog,
            R.string.menu_developer_clearStatics,
            R.string.menu_developer_post,
            R.string.menu_developer_masterPost,
            R.string.menu_developer_login,
            R.string.menu_developer_moreOptions,
            R.string.menu_developer_tools
	};
	
	private int ids[];
	private boolean inUse[]; // This flag is set when the ids of the menu are set
	
	public DeveloperMenu() {
		int size = nameIds.length;
		ids = new int[size];
		inUse = new boolean[size];
		for (int index=0; index<size; index++) {
			ids[index] = 0;
			inUse[index] = false;
		}
	}
	
	public int size() { return nameIds.length;}
	public int getLabel(int index) { 
		if (index>=0 && index<nameIds.length)
			return nameIds[index]; 
		else return 0;
	}
	public int getIcon(int index) {
		if (index>=0 && index<nameIds.length)
			return iconIds[index]; 
		else return 0;
	}
	public void setId(int index, int id) {
		if (index>=0 && index<nameIds.length) {
			ids[index] = id; 
			inUse[index] = true;
		}
	}
	public int getId(int index) {
		if (index>=0 && index<nameIds.length)
			return ids[index]; 
		else return 0;
	}
	
	public Menu addSubmenu(Menu menu, int submenuLabel, int groupId) {
		int lastId = menu.size();
		
		SubMenu subMenu = menu.addSubMenu(submenuLabel);
		for (int index=0; index<size(); index++) {
			int newId = lastId+index;
			setId(index, newId);
			subMenu.add(groupId, newId, index, getLabel(index)).setIcon(getIcon(index));
		}
		return menu;
	}
	
	public boolean act(int itemId, Activity activity) {
		
		for (int index=0; index<size(); index++) {
			int optionId = ids[index];
			int optionName = nameIds[index];
			if (itemId==optionId && inUse[index]) {
                if (optionName==R.string.menu_developer_clearSQLite) {
                    Search.clear(activity);
                    return true;
                }
                else if (optionName==R.string.menu_developer_clearPreferences) {
                    PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext())
                            .edit().clear().commit();
                    return true;
                }
				else if (optionName==R.string.menu_developer_clearInternal) {
					IO.clearInternalFiles(activity);
					return true;
				}
                else if (optionName==R.string.menu_developer_clearStatics) {
                    Course.get(activity).clear(activity);
                    PrivateSite.get(activity).clear(activity);
                    activity.finish(); // Since course is reset, the app needs to close. Otherwise, Module list will crash.
                    return true;
                }

				else if (optionName==R.string.menu_developer_clearLog) {
					Savelog.clear();
					return true;
				}
                else if (optionName==R.string.menu_developer_login) {
                    Intent intent = new Intent(activity, DeveloperActivity.class);
                    intent.putExtra(DeveloperActivity.EXTRA_Type, DeveloperActivity.Type_master);
                    activity.startActivity(intent);
                    Savelog.d(TAG, debug, "started master login");
                    return true;
                }
                else if (optionName==R.string.menu_developer_masterPost) {
                    Intent intent = new Intent(activity, DeveloperActivity.class);
                    intent.putExtra(DeveloperActivity.EXTRA_Type, DeveloperActivity.Type_masterpost);
                    activity.startActivity(intent);
                    Savelog.d(TAG, debug, "started master post");
                    return true;
                }
                else if (optionName==R.string.menu_developer_post) {
                    Intent intent = new Intent(activity, DeveloperActivity.class);
                    intent.putExtra(DeveloperActivity.EXTRA_Type, DeveloperActivity.Type_post);
                    activity.startActivity(intent);
                    Savelog.d(TAG, debug, "started post");
                    return true;
                }
                else if (optionName==R.string.menu_developer_tools) {
                    Intent intent = new Intent(activity, ToolsActivity.class);
                    intent.putExtra(ToolsActivity.EXTRA_Type, ToolsActivity.Type_numToRep);
                    activity.startActivity(intent);
                    Savelog.d(TAG, debug, "started base change tool");
                    return true;
                }
                else if (optionName==R.string.menu_developer_moreOptions) {
                    FragmentManager fm = activity.getFragmentManager();
                    DeveloperDialogFragment dialog = DeveloperDialogFragment.newInstance();
                    dialog.show(fm, DeveloperDialogFragment.dialogTag);
                    return true;
				}
			}
			else {} // no action
		}
		return false;
	}
	
	
}
