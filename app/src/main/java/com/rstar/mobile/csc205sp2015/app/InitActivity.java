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

package com.rstar.mobile.csc205sp2015.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.ModuleListActivity;
import com.rstar.mobile.csc205sp2015.download.DownloadActivity;
import com.rstar.mobile.csc205sp2015.io.Savelog;


public class InitActivity extends Activity {
    private static final String TAG = InitActivity.class.getSimpleName() + "_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private final int SPLASH_DISPLAY_LENGHT = 2000;  // default uses 2000
    private static final int DownloadCourseRequestCode = InitActivity.class.hashCode();  // must be unique

    private static final String FragmentTag = InitActivity.class.getSimpleName()+".Initfragment";

    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        Savelog.d(TAG, debug, "onCreate()");

        title = getString(R.string.cover_title);

        // Simulate the title
        TextView titleBinaryView = (TextView) findViewById(R.id.activityInit_title_binary);
        TextView titleView = (TextView) findViewById(R.id.activityInit_title);
        titleView.setText(title);

        // For small screen, no need to repeat
        int repetition = 4;
        if (getResources().getBoolean(R.bool.isTablet)) repetition = 10;
        titleBinaryView.setText(getBinaryTitle(repetition));

        ImageView imageView = (ImageView) findViewById(R.id.activityInit_diagram);

        // If activity not pre-exists, then start animation
        if (savedInstanceState == null) {
            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.scroll_up);
            titleBinaryView.startAnimation(animation1);
            Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.scroll_left);
            imageView.startAnimation(animation2);
        }


        // Check if fragment already exists
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FragmentTag);
        if (fragment == null) {
            fragment = InitFragment.newInstance();
            fm.beginTransaction().add(fragment, FragmentTag).commit();
        }
    }


    public void downloadCourse() {
        Intent intent = new Intent(this, DownloadActivity.class);
        intent.putExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_course);
        intent.putExtra(DownloadActivity.EXTRA_Trim, AppSettings.Trim);
        this.startActivityForResult(intent, DownloadCourseRequestCode);
        Savelog.d(TAG, debug, "started download with requestCode=" + DownloadCourseRequestCode);
    }

    public void splash() {
        // New Handler to start the main activity
        // and close this screen after some seconds.
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
            /* Create an Intent that will start the main activity. */
                Intent intent = new Intent(InitActivity.this, ModuleListActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGHT);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == InitActivity.DownloadCourseRequestCode) {
            boolean status = false;
            if (resultCode == RESULT_OK && data!=null) {
                int downloadType = data.getIntExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_default);
                if (downloadType == DownloadActivity.Type_course) {
                    status = true;
                    // Right after download, open the default page regardless of success or failure
                    Intent intent = new Intent(this, ModuleListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            if (!status) {
                Savelog.d(TAG, debug, "Failed to download course ");
            }
        }
        else {
            // Pass result to parent activity. Very important!!!
            super.onActivityResult(requestCode, resultCode, data);
        }

    }//onActivityResult


    private String getBinaryTitle(int repetition) {
        char chars[] = title.toCharArray();
        String data = "";
        for (char c : chars) {
            // Convert a character to integer with a mask. Then patch any missing zeros at the start.
            int intvalue = c & 0xffffffff;
            String stringValue = Integer.toBinaryString(intvalue);
            Savelog.d(TAG, debug, "char=" + c + " intvalue=" + intvalue + " ascii=" + stringValue);
            String padding = "";
            for (int count=stringValue.length(); count<8; count++) { padding += "0"; }
            data += padding + stringValue;
        }
        // Now duplicate the string many times to fill the space
        String repeatData = data;
        for (int repeat=0; repeat<repetition; repeat++) {
            repeatData += data;
        }
        return repeatData;
    }
}
