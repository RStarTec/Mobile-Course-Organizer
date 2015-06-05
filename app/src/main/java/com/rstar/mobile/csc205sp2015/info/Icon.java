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

package com.rstar.mobile.csc205sp2015.info;



import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.util.ArrayList;


public class Icon  {
	private static final String TAG = Icon.class.getSimpleName()+"_class";
	private static boolean debug = AppSettings.defaultDebug;
	
	private static final int iconIds[] = {
            R.drawable.ic_action_refresh,
            R.drawable.ic_action_search,
            R.drawable.ic_hammar,
            R.drawable.ic_action_about,

            R.drawable.ic_zoomtext,
            R.drawable.ic_action_play,

            R.drawable.ic_action_new_account,
            R.drawable.ic_action_accounts,
            R.drawable.ic_pass_reset,
            R.drawable.ic_passwd,

            R.drawable.ic_action_edit,
            R.drawable.ic_action_upload,
            R.drawable.ic_action_view_as_list,

            R.drawable.ic_tools_tocode,
            R.drawable.ic_tools_tonum,
            R.drawable.ic_tools_calc,
            R.drawable.ic_tools_pipeideal,
            R.drawable.ic_tools_pipeoverhead,
            R.drawable.ic_tools_2l
	};
	private static final int nameIds[] = {
            R.string.menu_modulelist_reload,
            R.string.menu_modulelist_search,
            R.string.menu_modulelist_tools,
            R.string.menu_modulelist_help,

            R.string.menu_module_zoomtext,
            R.string.menu_audio_play,

            R.string.menu_login_new,
            R.string.menu_login_existing,
            R.string.menu_login_reset,
            R.string.menu_login_passwd,

            R.string.menu_homework_edit,
            R.string.menu_homework_submit,
            R.string.menu_homework_score,

            R.string.menu_tools_numToRep,
            R.string.menu_tools_repToNum,
            R.string.menu_tools_arithInt,
            R.string.menu_tools_pipelineIdeal,
            R.string.menu_tools_pipelineOverhead,
            R.string.menu_tools_mem2L
	};
	private static final String descriptions[] = {
            "Check course website for new modules and updates",
            "Keyword search on the text of existing modules",
            "Open toolbox",
            "Display this help",

            "Display the text of the current slide on a separate screen for enlargement",
            "Play the audio recording of the current slide",

            "Sign up for homework access. Requires an existing registered account, and a valid email address. (For registered students only.)",
            "Sign in to access graded homework. (for registered students only)",
            "Reset password for an existing account. A new password will be automatically sent to the email address the user provided at sign-up. (For registered students only)",
            "Change password for an existing account (for registered students only)",

            "Edit homework",
            "Submit homework for grading",
            "Download feedback for a graded homework",

            "Convert a binary, decimal, or hexadecimal number to a computer\'s internal representation. Whole numbers are converted to the IEEE 32-bit int. Fractional numbers are converted to the IEEE 32-bit float.",
            "Convert a 32-bit computer\'s internal representation to its numerical value",
            "Perform number addition in 8-bit 2\'s complement form",
            "Analyze an ideal pipeline. The parameters of the simulation are the number of stages of the pipeline and the number of instructions executed.",
            "Analyze a pipeline with overhead. The parameters of the simulation are the number of stages of the pipeline, the number of instructions executed and the overhead.",
            "Evaluate the effective access time (EAT) and the efficiency of a 2-level memory system."
    };

	private int iconId = 0;
	private int nameId = 0;
	private String description = "";
	public Icon(int iconId, int nameId, String description) throws Exception {
		if (iconId==0 || nameId==0 || description==null) throw new Exception("bad arguments");
		this.iconId = iconId;
		this.nameId = nameId;
		this.description = description;
	}
	public int getIconId() {
		return iconId;
	}
	public int getNameId() {
		return nameId;
	}
	public String getDescription() {
		return description;
	}
	
	public static ArrayList<Icon> makeList() {
		Savelog.d(TAG, debug, "makeList()");
		ArrayList<Icon> list = new ArrayList<Icon>();
		int total = iconIds.length;
		if (total!=nameIds.length || total!=descriptions.length) return list;
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

	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (obj instanceof Icon) {
			Icon icon2 = (Icon) obj;
			if (this.iconId==icon2.getIconId() 
				&& this.nameId==icon2.getNameId()
				&& this.description.equals(icon2.getDescription())) {
				return true;
			}
		}
		return false;
	}
}
