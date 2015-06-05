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

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.rstar.mobile.csc205sp2015.fields.VideoListFields;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;
import com.rstar.mobile.csc205sp2015.utils.VideoUtils;

/**
 * Created by AHui on 1/21/15.
 */
public class VideoListTest extends InstrumentationTestCase {
    private static final String TAG = VideoListTest.class.getSimpleName()+"_class";
    Context targetContext = null;
    Context testContext = null;
    VideoListFields.Constants constants = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new VideoListFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }

    public void testGoodConstructor() throws Exception {
        {   // Set 0
            VideoUtils.DataSet dataSet = new VideoUtils.DataSet(targetContext, 0);
            if (dataSet.moduleDir!=null && !dataSet.moduleDir.exists()) dataSet.moduleDir.mkdir();
            DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.videoListId, dataSet.videoListFile);
            VideoList videoList = new VideoList(targetContext, dataSet.numberOfPages, dataSet.videoListFile);
            verifyVideoList(videoList);

            VideoUtils.VideoListFileData fileData = new VideoUtils.VideoListFileData(targetContext, dataSet.numberOfPages, dataSet.videoListFile);
            assertEquals(fileData.numberOfRecords, 0);

            DataUtils.clearAll(targetContext);
        }
        {   // Set 1 (has duplicates)
            VideoUtils.DataSet dataSet = new VideoUtils.DataSet(targetContext, 1);
            if (dataSet.moduleDir!=null && !dataSet.moduleDir.exists()) dataSet.moduleDir.mkdir();
            DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.videoListId, dataSet.videoListFile);
            VideoList videoList = new VideoList(targetContext, dataSet.numberOfPages, dataSet.videoListFile);
            verifyVideoList(videoList);

            VideoUtils.VideoListFileData fileData = new VideoUtils.VideoListFileData(targetContext, dataSet.numberOfPages, dataSet.videoListFile);
            assertEquals(fileData.numberOfRecords, 1);

            DataUtils.clearAll(targetContext);
        }
        {   // Set 2
            VideoUtils.DataSet dataSet = new VideoUtils.DataSet(targetContext, 2);
            if (dataSet.moduleDir!=null && !dataSet.moduleDir.exists()) dataSet.moduleDir.mkdir();
            DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.videoListId, dataSet.videoListFile);
            VideoList videoList = new VideoList(targetContext, dataSet.numberOfPages, dataSet.videoListFile);
            verifyVideoList(videoList);

            VideoUtils.VideoListFileData fileData = new VideoUtils.VideoListFileData(targetContext, dataSet.numberOfPages, dataSet.videoListFile);
            assertEquals(fileData.numberOfRecords, 2);

            DataUtils.clearAll(targetContext);
        }
    }

    public void testBadConstructor() throws Exception {
        {   // Set -1
            VideoUtils.DataSet dataSet = new VideoUtils.DataSet(targetContext, -1);
            if (dataSet.moduleDir!=null && !dataSet.moduleDir.exists()) dataSet.moduleDir.mkdir();
            DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.videoListId, dataSet.videoListFile);
            VideoList videoList = new VideoList(targetContext, dataSet.numberOfPages, dataSet.videoListFile);
            verifyVideoList(videoList);

            VideoUtils.VideoListFileData fileData = new VideoUtils.VideoListFileData(targetContext, dataSet.numberOfPages, dataSet.videoListFile);
            assertEquals(fileData.numberOfRecords, 0);

            DataUtils.clearAll(targetContext);
        }
        {   // Set -2
            VideoUtils.DataSet dataSet = new VideoUtils.DataSet(targetContext, -2);
            if (dataSet.moduleDir!=null && !dataSet.moduleDir.exists()) dataSet.moduleDir.mkdir();
            DataUtils.copyRawFileToInternal(testContext, targetContext, dataSet.videoListId, dataSet.videoListFile);
            VideoList videoList = new VideoList(targetContext, dataSet.numberOfPages, dataSet.videoListFile);
            verifyVideoList(videoList);

            VideoUtils.VideoListFileData fileData = new VideoUtils.VideoListFileData(targetContext, dataSet.numberOfPages, dataSet.videoListFile);
            assertEquals(fileData.numberOfRecords, 0);

            DataUtils.clearAll(targetContext);
        }
    }


    private void verifyVideoList(VideoList videoList) throws Exception {
        VideoListFields.Variables variables = new VideoListFields.Variables(videoList);
        for (int pageNumber=1; pageNumber<=variables.numberOfPages; pageNumber++) {
            int index = pageNumber-1;
            assertEquals(variables.availability[index], videoList.isVideoAvailable(pageNumber));
            assertEquals(variables.description[index], videoList.getDescription(pageNumber));
            assertEquals(variables.videoSize[index], videoList.getVideoSize(pageNumber));
        }
    }
}