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

import android.content.Context;

import com.rstar.mobile.csc205sp2015.app.AppSettings;
import com.rstar.mobile.csc205sp2015.io.IO;
import com.rstar.mobile.csc205sp2015.io.Savelog;

import java.io.File;


public abstract class DeveloperOptionsTemplate {
    private static final String TAG = DeveloperOptionsTemplate.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final int BinaryOptions_count = 4;
    public static final int MultipleChoiceOptions_count = 1;
    public static final int GradientOptions_count = 2;

    // names of fields
    protected static String[] binaryOptionLabels = {"BinOption1" , "BinOption2", "BinOption3", "BinOption4"};
    protected static String[][] multipleChoiceOptionLabels = {{"MultipleChoice1", "MultipleChoice2", "MultipleChoice3"}};
    protected static String[] gradientOptionLabels = {"Gradient1", "Gradient2"};

    // Default values
    protected boolean[] binaryOptions = {false, false, false, false}; // each entry is true/false
    protected int[] multipleChoiceOptions = {0}; // each entry has multiple choices
    protected int[] multipleChoiceOptionsRanges = {1}; // each entry records how many multiple choices
    protected double[] gradientOptions = {0.0, 0.0}; // each entry is a double within a range.
    protected double[][] gradientOptionsRanges = {{0, 1}, {0, 1}}; // each pair defines a range.



    private static final String defaultFilename = "developerOptions.dat";
    private static final String FieldDelimiter = "\n";
    private static final String FieldDelimiterReg = "\\n";

    protected DeveloperOptionsTemplate() {
       // do nothing.
    }

    public static File getFile(Context context, String filename) {
        if (filename==null || filename.length()==0)
            return IO.getInternalFile(context, defaultFilename);
        else
            return IO.getInternalFile(context, filename);
    }

    protected void load(Context context) {
        load(context, defaultFilename);
    }

    protected void load(Context context, String filename) {
        if (filename==null || filename.length()==0) {
            Savelog.d(TAG, debug, "filename not available, use default name.");
            filename = defaultFilename;
        }
        try {
            File file = getFile(context, filename);
            if (file==null || !file.exists()) return; // use default values

            String data = IO.loadFileAsString(context, file);

            String[] fields = data.split(FieldDelimiterReg);
            int pos = 0;
            int total = BinaryOptions_count + MultipleChoiceOptions_count + GradientOptions_count;

            for (int index=0; index<BinaryOptions_count; index++) {
                binaryOptions[index] = Boolean.valueOf(fields[pos]);
                pos++;
            }

            for (int index=0; index<MultipleChoiceOptions_count; index++) {
                multipleChoiceOptions[index] = Integer.valueOf(fields[pos]);
                pos++;
            }

            for (int index=0; index<GradientOptions_count; index++) {
                gradientOptions[index] = Double.valueOf(fields[pos]);
                pos++;
            }
            if (pos!=total) {
                throw new Exception("Read items " + pos + " not equal total " + total);
            }

            Savelog.d(TAG, debug, "Loaded from file " + filename + " data:\n" + toString());
        } catch (Exception e) {
            Savelog.w(TAG, "Failed to load file " + filename, e);
            setDefault();
        }
    }

    public void save(Context context) {
        save(context, defaultFilename);
    }
    public void save(Context context, String filename) {
        if (filename==null || filename.length()==0) {
            Savelog.d(TAG, debug, "filename not available, use default name.");
            filename = defaultFilename;
        }
        try {
            String data = this.toString();
            File file = getFile(context, filename);
            Savelog.d(TAG, debug, "saving file " + filename + " with data:\n" + data);
            IO.saveStringAsFile(context, file, data);
        } catch (Exception e) {
            Savelog.w(TAG, "Failed to save file " + filename, e);
        }
    }

    abstract protected void setDefault();

    private void reset() {
        // Reset everything
        for (int index=0; index<BinaryOptions_count; index++)
            binaryOptions[index] = false;
        for (int index=0; index<MultipleChoiceOptions_count; index++)
            multipleChoiceOptions[index] = 0;
        for (int index=0; index<GradientOptions_count; index++)
            gradientOptions[index] = 0;
    }

    @Override
    public String toString() {
        String data = "";

        for (int index=0; index<BinaryOptions_count; index++) {
            data += Boolean.toString(binaryOptions[index]) + FieldDelimiter;
        }

        for (int index=0; index<MultipleChoiceOptions_count; index++) {
            data += Integer.toString(multipleChoiceOptions[index]) + FieldDelimiter;
        }

        for (int index=0; index<GradientOptions_count; index++) {
            data += Double.toString(gradientOptions[index]) + FieldDelimiter;
        }
        return data;
    }

    public static String[] getBinaryOptionLabels() {
        return binaryOptionLabels;
    }
    public static String[][] getMultipleChoiceOptionLabels() {
        return multipleChoiceOptionLabels;
    }
    public static String[] getGradientOptionLabels() {
        return gradientOptionLabels;
    }
    public boolean[] getBinaryOptions() {
        return binaryOptions;
    }
    public int[] getMultipleChoiceOptions() {
        return multipleChoiceOptions;
    }
    public int[] getMultipleChoiceOptionsRanges() {
        return multipleChoiceOptionsRanges;
    }
    public double[] getGradientOptions() {
        return gradientOptions;
    }
    public double[][] getGradientOptionsRanges() {
        return gradientOptionsRanges;
    }

    public boolean getBinaryOption(int index) {
        if (index>=0 && index<BinaryOptions_count)
            return binaryOptions[index];
        return false; // default
    }
    public int getMultipleChoiceOption(int index) {
        if (index>=0 && index<MultipleChoiceOptions_count)
            return multipleChoiceOptions[index];
        return 0; // default
    }
    public double getGradientOption(int index) {
        if (index>=0 && index<GradientOptions_count)
            return gradientOptions[index];
        return 0; // default
    }

    public void setBinaryOptions(int index, boolean value) {
        if (index>=0 && index<BinaryOptions_count)
            binaryOptions[index] = value;
    }
    public void setMultipleChoiceOptions(int index, int value) {
        if (index>=0 && index<MultipleChoiceOptions_count) {
            if (value>=0 && value<multipleChoiceOptionsRanges[index])
                multipleChoiceOptions[index] = value;
        }
    }
    public void setGradientOptions(int index, double value) {
        if (index>=0 && index<GradientOptions_count) {
            if (value>=gradientOptionsRanges[index][0] && value<=gradientOptionsRanges[index][1])
                gradientOptions[index] = value;
        }
    }

}
