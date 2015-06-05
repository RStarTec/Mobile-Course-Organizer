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

package com.rstar.mobile.csc205sp2015.tools;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.tools.arithmetic.ArithIntFragment;
import com.rstar.mobile.csc205sp2015.tools.memory.Mem2LFragment;
import com.rstar.mobile.csc205sp2015.tools.pipeline.PipelineFragment;
import com.rstar.mobile.csc205sp2015.tools.representation.NumToRepFragment;
import com.rstar.mobile.csc205sp2015.tools.representation.RepToNumFragment;

public class ToolsActivity extends Activity {
	public static final String TAG = ToolsActivity.class.getSimpleName()+"_class";

    public static final String EXTRA_Type = ToolsActivity.class.getSimpleName()+".Type";

    public static final int Type_numToRep = 1;
    public static final int Type_repToNum = 2;
    public static final int Type_arithInt = 3;
    public static final int Type_pipelineIdeal = 4;
    public static final int Type_pipelineReal = 5;
    public static final int Type_2l = 6;

    public static final int Type_default = Type_numToRep;

    private int mType = Type_default;
    private Fragment fragment;
    private int fragmentId;

	//OnCreate Method:
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tools);
        fragmentId = R.id.activityTools_container;

        mType = getIntent().getIntExtra(EXTRA_Type, Type_default);

        // Check if fragment already exists
		FragmentManager fm = getFragmentManager();
		fragment = fm.findFragmentById(fragmentId);
		if (fragment == null) {
            if (mType == Type_numToRep) {
                fragment = NumToRepFragment.newInstance();
            }
            else if (mType ==Type_repToNum) {
                fragment = RepToNumFragment.newInstance();
            }
            else if (mType ==Type_arithInt) {
                fragment = ArithIntFragment.newInstance();
            }
            else if (mType == Type_pipelineIdeal) {
                fragment = PipelineFragment.newInstance(PipelineFragment.type_idealPipeline);
            }
            else if (mType == Type_pipelineReal) {
                fragment = PipelineFragment.newInstance(PipelineFragment.type_realPipeline);
            }
            else if (mType ==Type_2l) {
                fragment = Mem2LFragment.newInstance();
            }

			fm.beginTransaction().add(fragmentId, fragment).commit();
		}
	}



    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(EXTRA_Type, mType);
        super.onSaveInstanceState(savedInstanceState);
    }


    public void refreshFragment(int newType) {
        FragmentManager fm = getFragmentManager();
        Fragment oldFragment = fm.findFragmentById(fragmentId);

        mType = newType;
        if (mType == Type_numToRep) {
            hideSoftKeyboard();
            fragment = NumToRepFragment.newInstance();
        }
        else if (mType == Type_repToNum) {
            hideSoftKeyboard();
            fragment = RepToNumFragment.newInstance();
        }
        else if (mType == Type_arithInt) {
            fragment = ArithIntFragment.newInstance();
        }
        else if (mType == Type_pipelineIdeal) {
            hideSoftKeyboard();
            fragment = PipelineFragment.newInstance(PipelineFragment.type_idealPipeline);
        }
        else if (mType == Type_pipelineReal) {
            hideSoftKeyboard();
            fragment = PipelineFragment.newInstance(PipelineFragment.type_realPipeline);
        }
        else if (mType ==Type_2l) {
            hideSoftKeyboard();
            fragment = Mem2LFragment.newInstance();
        }

        if (oldFragment!=null) {
            fm.beginTransaction().replace(fragmentId, fragment).commit();
        }
        else {
            fm.beginTransaction().add(fragmentId, fragment).commit();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tools, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_tools_numToRep:
            {
                // Do not re-open if already there
                if (mType!=Type_numToRep)
                    refreshFragment(Type_numToRep);
                return true;
            }
            case R.id.menu_tools_repToNum:
            {
                // Do not re-open if already there
                if (mType!=Type_repToNum)
                    refreshFragment(Type_repToNum);
                return true;
            }
            case R.id.menu_tools_arithInt:
            {
                // Do not re-open if already there
                if (mType!=Type_arithInt)
                    refreshFragment(Type_arithInt);
                return true;
            }
            case R.id.menu_tools_pipelineIdeal:
            {
                // Do not re-open if already there
                if (mType!= Type_pipelineIdeal)
                    refreshFragment(Type_pipelineIdeal);
                return true;
            }
            case R.id.menu_tools_pipelineOverhead:
            {
                // Do not re-open if already there
                if (mType!= Type_pipelineReal)
                    refreshFragment(Type_pipelineReal);
                return true;
            }
            case R.id.menu_tools_2l:
            {
                // Do not re-open if already there
                if (mType!=Type_2l)
                    refreshFragment(Type_2l);
                return true;
            }

            // no default action
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
