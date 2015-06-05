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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.SubMenu;
import android.widget.Toast;

import com.rstar.mobile.csc205sp2015.app.App;
import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.info.Icon;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Message;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.io.File;
import java.util.ArrayList;

public class TesterMenu {
	private static final String TAG = TesterMenu.class.getSimpleName()+"_class";
	private static final boolean debug = AppSettings.defaultDebug;

	public static final String developerEmail = DeveloperSettings.developerEmail;
	private static final String sendby = "Send by";


	private static final int iconIds[] = {
		R.drawable.ic_action_send_now,
		R.drawable.ic_action_discard
	};
	private static final int nameIds[] = {
		R.string.menu_tester_reportbug, 
		R.string.menu_tester_clear
	};
	private static final String descriptions[] = {
		"Send a bug report along with the log file, to the developer through email. Please provide the following details about this bug. "
		+ "(1) Does the app crash (ie. close)? (2) On what part of the app does this bug appear? (3) What user actions lead to this bug? "
		+ "(4) If possible, please provide a screen capture of the bug.",
		"Clear data in internal and external storage on device (including login, sqlite, modules, access code, and log)."
	};
	
	
	private int ids[];
	private boolean inUse[]; // This flag is set when the ids of the menu are set
	
	public TesterMenu() {
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
				if (optionName==R.string.menu_tester_reportbug) {
					// send an email with user description
					File file = Savelog.getLogFile();
					String subject = "Bug report from beta tester";
					SendFileThroughEmail(activity, file, subject);
					return true;
				}
				else if (optionName==R.string.menu_tester_clear) {
                    App.clear();
                    activity.finish(); // Since course is reset, the app needs to close. Otherwise, Module list will crash.

                    return true;
				}
			}
			else {} // no action
		}
		return false;
	}
	

	
	public static void SendFileThroughEmail(Context context, File file, String messageSubject) {
		if (file==null || messageSubject==null) return;
		
		String addresses[] = {developerEmail};

		{
			String allAddress = "";
			for (String a : addresses)  allAddress += (a + " ");
			Savelog.d(TAG, debug, "Sending log file " + file.getName() + " to " + allAddress);
		}

		Uri messageUri = null;
		messageUri = Uri.fromFile(file);
		

		if ( IO.isNetworkAvailable(context) ) {
			// AND make sure that there is a valid email service
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/html");
			
			intent.putExtra(Intent.EXTRA_EMAIL, addresses);
			intent.putExtra(Intent.EXTRA_SUBJECT, messageSubject);
			intent.putExtra(Intent.EXTRA_STREAM, messageUri);
	
			// Allow user to choose an app to do the sending.
			// If there is only one, then no choice is needed.
			intent = Intent.createChooser(intent, sendby);

			// Since there is always a candidate app for send on all android
			// devices, starting the activity won't crash in this case.							

			context.startActivity(intent);					
		}
		else {
			Toast.makeText(context, Message.toastNoNetwork, Toast.LENGTH_SHORT).show();
		}
    }

    public static ArrayList<Icon> addList(ArrayList<Icon> list) {
        Savelog.d(TAG, debug, "makeList()");
        if (list==null) {
            list = new ArrayList<Icon>();
        }
        int total = nameIds.length;
        if (total!=iconIds.length || total!=descriptions.length) return list;
        for (int index=0; index<total; index++) {
            try {
                Icon icon = new Icon(iconIds[index], nameIds[index], descriptions[index]);
                list.add(icon);
            }
            catch (Exception e) {
                Savelog.w(TAG, "cannot create icon at " + index + "\n" + e.getMessage());
            }
        }
        return list;
    }
}
