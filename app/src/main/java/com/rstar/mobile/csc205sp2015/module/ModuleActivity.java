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

package com.rstar.mobile.csc205sp2015.module;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.R;
import com.rstar.mobile.csc205sp2015.course.Course;
import com.rstar.mobile.csc205sp2015.info.HelpActivity;
import com.rstar.mobile.csc205sp2015.download.DownloadActivity;
import com.rstar.mobile.csc205sp2015.io.Savelog;
import com.rstar.mobile.csc205sp2015.registered.homework.Homework;
import com.rstar.mobile.csc205sp2015.registered.homework.HomeworkActivity;
import com.rstar.mobile.csc205sp2015.registered.homework.HomeworkBlankFragment;
import com.rstar.mobile.csc205sp2015.registered.homework.HomeworkFragment;
import com.rstar.mobile.csc205sp2015.search.SearchActivity;
import com.rstar.mobile.csc205sp2015.search.SearchFragment;
import com.rstar.mobile.csc205sp2015.textscreen.TextActivity;
import com.rstar.mobile.csc205sp2015.tools.ToolsActivity;
import com.rstar.mobile.csc205sp2015.video.VideoActivity;


public class ModuleActivity extends DrawerActivity {

    private static final String TAG = ModuleActivity.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final String EXTRA_ModuleNumber = ModuleActivity.class.getSimpleName()+".ModuleNumber";
    public static final String EXTRA_PageNumber = ModuleActivity.class.getSimpleName()+".PageNumber";

    private static final int SearchRequestCode = ModuleActivity.class.hashCode();  //must be unique!
    private static final int HomeworkUpdateRequestCode = ModuleActivity.class.hashCode() + 1;  //must be unique!
    private static final int DownloadModuleRequestCode = ModuleActivity.class.hashCode() + 2;  // must be unique!


    private static final String AudioFragmentTag = ModuleActivity.class.getSimpleName()+".Audiofragment";
    private static final int PageFragmentId = R.id.activityModule_pageFragment_id;

    private static final int DirectionIncr = 1;
    private static final int DirectionDecr = -1;
    private static final int DirectionJump = 0;
    private float lastX = 0;
    private float lastY = 0;
    private static final int DefaultSwipeDistance = 200;
    private int minSwipeDistance = DefaultSwipeDistance;

    // A handler object, used for deferring UI operations.
    private Handler mHandler = new Handler();

    private int mModuleNumber;
    private int mPageNumber;
    private Module mModule;

    private PageFragment mPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Savelog.d(TAG, debug, "onCreate()");


        if (savedInstanceState == null) {
            mModuleNumber = getIntent().getIntExtra(EXTRA_ModuleNumber, Module.DefaultModuleNumber);
            mPageNumber = getIntent().getIntExtra(EXTRA_PageNumber, Module.DefaultPageNumber);

            mModule = Course.get(this).getModule(mModuleNumber);

            refreshSlide(mPageNumber, DirectionJump);
        }
        else {
            mModuleNumber = savedInstanceState.getInt(EXTRA_ModuleNumber);
            mPageNumber = savedInstanceState.getInt(EXTRA_PageNumber);

            mModule = Course.get(this).getModule(mModuleNumber);

            FragmentManager fm = getFragmentManager();
            mPageFragment = (PageFragment) fm.findFragmentById(PageFragmentId);
        }

        // The drawers of slides and homework is not dependent on the pagenumber.
        // So set it up once for all and update when homework changes.
        setupSlidesDrawerFragment(mModuleNumber);
        setupHomeworkDrawerFragment(mModuleNumber);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.module, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.menu_module_zoomtext: {
                Intent intent = new Intent(this, TextActivity.class);
                intent.putExtra(TextActivity.EXTRA_ModuleNumber, mModuleNumber);
                intent.putExtra(TextActivity.EXTRA_PageNumber, mPageNumber);
                startActivity(intent);
                return true;
            }
            case R.id.menu_module_homework: {
                // TODO: check if the homework drawer is open. If answer is not saved, request to save.
                // Must close the homework drawer first.

                closeDrawers();
                Intent intent = new Intent(this, HomeworkActivity.class);
                intent.putExtra(HomeworkActivity.EXTRA_ModuleNumber, mModuleNumber);
                intent.putExtra(HomeworkActivity.EXTRA_Type, HomeworkActivity.Type_default);
                startActivityForResult(intent, HomeworkUpdateRequestCode);
                return true;
            }

            case R.id.menu_module_search:
            {
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra(SearchActivity.EXTRA_ModuleNumber, mModuleNumber);
                startActivityForResult(intent, SearchRequestCode);
                Savelog.d(TAG, debug, "started search with requestCode=" + SearchRequestCode);
                return true;
            }

            case R.id.menu_module_tools:
            {
                Intent intent = new Intent(this, ToolsActivity.class);
                intent.putExtra(ToolsActivity.EXTRA_Type, ToolsActivity.Type_default);
                this.startActivity(intent);
                return true;
            }

            case R.id.menu_module_help:
            {
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            }
            // no default action
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();
        minSwipeDistance = ViewConfiguration.get(this).getScaledPagingTouchSlop();
        Savelog.d(TAG, debug, "minSwipeDistance="+minSwipeDistance);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(EXTRA_ModuleNumber, mModuleNumber);
        savedInstanceState.putInt(EXTRA_PageNumber, mPageNumber);
        super.onSaveInstanceState(savedInstanceState);
    }


    private void refreshSlide(int newPageNumber, final int direction) {

        Savelog.d(TAG, debug, "ready to refresh fragments with newPageNumber="+newPageNumber
                + " dirn="+direction);

        // Force a change of fragments

        // First, check if there are already fragments
        FragmentManager fm = getFragmentManager();

        PageFragment oldPageFragment = (PageFragment) fm.findFragmentById(PageFragmentId);
        AudioFragment oldAudioFragment = (AudioFragment) fm.findFragmentByTag(AudioFragmentTag);


        boolean isAudioAvailable = mModule.isAudioAvailable(this, newPageNumber);

        PageFragment newPageFragment = PageFragment.newInstance(mModuleNumber, newPageNumber);


        // Now hook up the fragments through a fragment transaction
        FragmentTransaction ft = fm.beginTransaction();


        // If any old audiofragment exists, remove it.
        if (oldAudioFragment!=null) {
            ft.remove(oldAudioFragment);
        }
        else {} // no need to clean up.

        if (isAudioAvailable) {
            AudioFragment newAudioFragment = null;
            newAudioFragment = AudioFragment.newInstance(mModuleNumber, newPageNumber);
            ft.add(newAudioFragment, AudioFragmentTag);
        }


        // tackle the visible module fragment
        // If an old fragment already exists, then we need to replace it.
        // Else just add the new one.
        if (oldPageFragment!=null) {

            // This is the place where an animation may come in
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                if (direction==DirectionIncr) {
                    // animate a flip right to left
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                } else if (direction==DirectionDecr) {
                    // animate a flip left to right
                    ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    // It's a jump. No flipping needed.
                }
            }
            else {
                // it's an old version. Cannot support animation.
            }
            ft.replace(PageFragmentId, newPageFragment);
        }
        else {
            ft.add(PageFragmentId, newPageFragment);
        }


        // Finalize the fragment change
        ft.commitAllowingStateLoss();

        // update information
        mPageNumber = newPageNumber;
        mPageFragment = newPageFragment;


        // Defer an invalidation of the options menu (on modern devices, the action bar). This
        // can't be done immediately because the transaction may not yet be committed. Commits
        // are asynchronous in that they are posted to the main thread's message loop.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
            }
        });
    }

    // Method to handle touch event like left to right swap and right to left swap
    @Override
    public boolean dispatchTouchEvent(MotionEvent touchevent) {
        //Savelog.d(TAG, debug, "dispatchTouchEvent() called");
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN: {    // when user first touches the screen, record location
                Savelog.d(TAG, debug, "action down");
                lastX = touchevent.getX();
                lastY = touchevent.getY();
                mPageFragment.windupLayout(); // no effect if fragment is not on the border
                break;
            }
        }
        return super.dispatchTouchEvent(touchevent);
    }

    // Method to handle touch event like left to right swap and right to left swap
    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        Savelog.d(TAG, debug, "onTouchEvent() called");
        int action = touchevent.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            {
                Savelog.d(TAG, debug, "action up code:" + action);
                float currentX = touchevent.getX();
                float currentY = touchevent.getY();
                if (swipeDirection(lastX, lastY, currentX, currentY)==DirectionDecr) { // left to right swipe made on screen
                    if (mPageNumber>1) {
                        refreshSlide(mPageNumber - 1, DirectionDecr);
                    }
                    else {
                        mPageFragment.showLayoutEdgeEffect();
                    }
                    return true;

                }
                else if (swipeDirection(lastX, lastY, currentX, currentY)==DirectionIncr) { // if right to left swipe made on screen
                    int numberOfSlides = Course.get(this).getModule(mModuleNumber).getNumberOfPages();
                    if (mPageNumber<numberOfSlides) {
                        refreshSlide(mPageNumber + 1, DirectionIncr);
                    }
                    else {
                        mPageFragment.showLayoutEdgeEffect();
                    }
                    return true;
                }
                break;
            }
            default: {
                Savelog.d(TAG, debug, "action default=" + action);
            }
        }
        return super.onTouchEvent(touchevent);
    }

    private int swipeDirection(float startX, float startY, float endX, float endY) {
        // Return DirectionIncr if screen swipe goes right
        // Return DirectionDecr if screen swipe goes left
        if (endX>startX && endX-startX>minSwipeDistance) {  // endX is to the right of startX. Swipe motion is to right
            // now check if the movement is predominantly horizontal or vertical
            if (Math.abs(endY-startY) >= Math.abs(endX-startX))
                return 0;
            else
                return DirectionDecr;
        }
        else if (endX<startX && endX-startX<-minSwipeDistance) {  // endX is to the left of startX. Swipe motion is to left
            // now check if the movement is predominantly horizontal or vertical
            if (Math.abs(endY-startY) >= Math.abs(endX-startX))
                return 0;
            else
                return DirectionIncr;
        }
        else
            return 0;
    }



    @Override
    void onFinishSlideDrawerSelection(int newPageNumber) {
        closeDrawers();
        refreshSlide(newPageNumber, DirectionJump);
    }


    private void openModule(Module newModule, int pageNumber) {
        if (newModule==null) return;
        if (newModule.isInstalled()) {
            // if module already installed, then go directly to module
            Intent intent = new Intent(this, ModuleActivity.class);
            intent.putExtra(ModuleActivity.EXTRA_ModuleNumber, newModule.getModuleNumber());
            intent.putExtra(ModuleActivity.EXTRA_PageNumber, pageNumber);
            startActivity(intent);
            finish();
        }
        else {
            // if module not installed, then need to install it first.
            // Then go to default page.
            Intent intent = new Intent(this, DownloadActivity.class);
            intent.putExtra(DownloadActivity.EXTRA_ModuleNumber, DownloadActivity.Type_module);
            intent.putExtra(DownloadActivity.EXTRA_ModuleNumber, newModule.getModuleNumber());
            intent.putExtra(DownloadActivity.EXTRA_PageNumber, pageNumber);
            intent.putExtra(DownloadActivity.EXTRA_Trim, AppSettings.Trim);
            startActivityForResult(intent, DownloadModuleRequestCode);
            // Wait for result from download. Then start a new ModuleActivity and close current one.
        }
    }




    // this is called by the sub-class that controls not only the drawer fragments, but also the main fragment
    protected void setupSlidesDrawerFragment(int moduleNumber) {
        Savelog.d(TAG, debug, "Calling create SlidesDrawer for 1 pane display.");

        FragmentManager fm = getFragmentManager();

        // Right drawer is always there.
        Fragment rightFragment = fm.findFragmentById(rightFragmentId);
        if (rightFragment==null) {
            Savelog.d(TAG, debug, "Creating new slides drawer fragment");
            rightFragment = SlidesDrawerFragment.newInstance(moduleNumber);
            fm.beginTransaction().add(rightFragmentId, rightFragment).commit();
        }
        else {
            // do nothing. Already there.
        }

    }

    // this is called by the sub-class that controls not only the drawer fragments, but also the main fragment
    protected void setupHomeworkDrawerFragment(final int moduleNumber) {
        Savelog.d(TAG, debug, "Calling create HomeworkDrawer.");

        FragmentManager fm = getFragmentManager();
        // Right drawer is always there.
        Fragment leftFragment = fm.findFragmentById(leftFragmentId);

        int status = Homework.Status.get(this, moduleNumber);
        if (status==Homework.Status.OnDevice) {
            if (leftFragment==null) {
                Savelog.d(TAG, debug, "Creating new homework drawer fragment");
                leftFragment = HomeworkFragment.newInstance(moduleNumber, HomeworkFragment.DefaultQuestionNumber);
                fm.beginTransaction().add(leftFragmentId, leftFragment).commit();
            }
            else {
                // something is already there
            }
        }
        else {
            // Homework not available.
            // Right drawer is always there.
            if (leftFragment==null) {
                Savelog.d(TAG, debug, "Creating new empty homework drawer fragment");

                leftFragment = HomeworkBlankFragment.newInstance(moduleNumber);
                fm.beginTransaction().add(leftFragmentId, leftFragment).commit();
            }
            else {
                // something is already there
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ModuleActivity.SearchRequestCode) {
            if(resultCode == RESULT_OK && data!=null){
                int newModuleNumber = data.getIntExtra(SearchFragment.EXTRA_newModuleNumber, mModuleNumber);
                int newPageNumber = data.getIntExtra(SearchFragment.EXTRA_newPageNumber, mPageNumber);

                if (mModuleNumber==newModuleNumber) {
                    refreshSlide(newPageNumber, DirectionJump);
                }
                else {
                    if (newModuleNumber!=Module.DefaultModuleNumber) {
                        Module module = Course.get(this).getModule(newModuleNumber);
                        openModule(module, newPageNumber);
                        Savelog.d(TAG, debug, "Jump to another module. Finish current activity.");
                    }
                }
            }
        }


        // This activity has started a HomeworkActivity and has requested update on homework.
        // The resultCode comes from HomeworkActivity.
        else if (requestCode == ModuleActivity.HomeworkUpdateRequestCode) {
            Savelog.d(TAG, debug, "get back resultCode for update request from homework");
            if(resultCode == RESULT_OK && data!=null) { // change occurred
                int newModuleNumber = data.getIntExtra(HomeworkActivity.EXTRA_ModuleNumber, mModuleNumber);
                int newQuestionNumber = data.getIntExtra(HomeworkActivity.EXTRA_QuestionNumber, Homework.DefaultQuestionNumber);
                int newType = data.getIntExtra(HomeworkActivity.EXTRA_Type, HomeworkActivity.Type_default);
                refreshLeftDrawer(newModuleNumber, newQuestionNumber);
            }
        }


        // This activity has started a DownloadActivity and has requested for module download
        else if (requestCode == ModuleActivity.DownloadModuleRequestCode) {
            boolean status = false;
            if (resultCode == RESULT_OK && data!=null) {
                int downloadType = data.getIntExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_default);
                int newModuleNumber = data.getIntExtra(DownloadActivity.EXTRA_ModuleNumber, Module.DefaultModuleNumber);
                if (downloadType == DownloadActivity.Type_module) {
                    // Right after download, open the default page
                    Intent intent = new Intent(this, ModuleActivity.class);
                    intent.putExtra(ModuleActivity.EXTRA_ModuleNumber, newModuleNumber);
                    intent.putExtra(ModuleActivity.EXTRA_PageNumber, Module.DefaultPageNumber);
                    startActivity(intent);
                    finish(); // close current ModuleActivity
                    status = true;
                }
            }
            if (!status) {
                Savelog.d(TAG, debug, "Failed to download module");
            }
        }

        // Request code was sent out from VideoDialogFragment
        // But when result comes back, the extra is from DownloadActivity
        else if (requestCode == VideoDialogFragment.DownloadVideoRequestCode) {
            boolean status = false;
            if (resultCode==RESULT_OK && data!=null) {
                int downloadType = data.getIntExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_default);
                int moduleNumber = data.getIntExtra(DownloadActivity.EXTRA_ModuleNumber, Module.DefaultModuleNumber);
                int pageNumber = data.getIntExtra(DownloadActivity.EXTRA_PageNumber, Module.DefaultPageNumber);
                if (moduleNumber!=mModuleNumber || pageNumber!=mPageNumber) {
                    Savelog.w(TAG, "downloaded M" + moduleNumber + "P" + pageNumber + " not equal current M" + mModuleNumber + "P" + mPageNumber);
                }
                Savelog.d(TAG, debug, "get back resultCode for download request from video dialog");
                if (downloadType==DownloadActivity.Type_video) {
                    Intent intent = new Intent(this, VideoActivity.class);
                    intent.putExtra(VideoActivity.EXTRA_ModuleNumber, moduleNumber);
                    intent.putExtra(VideoActivity.EXTRA_PageNumber, pageNumber);
                    startActivity(intent);
                    status = true;
                }
            }
            if (!status) {
                Savelog.d(TAG, debug, "Failed to download video");
            }
        }

        // Request code was sent out from HomeworkBlankFragment
        // But when result comes back, the extra is from DownloadActivity
        else if (requestCode == HomeworkBlankFragment.DownloadHomeworkRequestCode) {
            boolean status = false;
            if (resultCode==RESULT_OK && data!=null) {
                int downloadType = data.getIntExtra(DownloadActivity.EXTRA_Type, DownloadActivity.Type_default);
                int moduleNumber = data.getIntExtra(DownloadActivity.EXTRA_ModuleNumber, Module.DefaultModuleNumber);
                if (moduleNumber!=mModuleNumber) {
                    Savelog.w(TAG, "download homework module " + moduleNumber + " not equal current module " + mModuleNumber);
                }
                Savelog.d(TAG, debug, "get back resultCode for download request from blank homework");
                if (downloadType==DownloadActivity.Type_homework) {
                    // Refresh the left fragment.
                    refreshLeftDrawer(moduleNumber, HomeworkFragment.DefaultQuestionNumber);
                    status = true;
                }
            }
            if (!status) {
                Savelog.d(TAG, debug, "Failed to download homework");
            }
        }

        // Request code was sent out from HomeworkBlankFragment
        // But when result comes back, the extra is from LoginActivity
        else if (requestCode == HomeworkBlankFragment.LoginRequestCode) {
            Savelog.d(TAG, debug, "get back resultCode for login request from blank homework");
            if(resultCode == RESULT_OK){
                // Refresh the left fragment.
                refreshLeftDrawer(mModuleNumber, HomeworkFragment.DefaultQuestionNumber);
            }
        }

        else {
            Savelog.d(TAG, debug, "passing result to parent activity DrawerActivity");

            // Pass result to parent activity. Very important!!!
            super.onActivityResult(requestCode, resultCode, data);
        }
    }//onActivityResult
}
