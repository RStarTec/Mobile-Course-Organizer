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
import com.rstar.mobile.csc205sp2015.io.Savelog;


public class DeveloperOptions extends DeveloperOptionsTemplate {
    private static final String TAG = DeveloperOptions.class.getSimpleName() + "_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private static final int Option_1 = 0;
    private static final int Option_2 = 1;
    private static final int Option_3 = 2;
    private static final int Option_4 = 3;
    private static final int Option_loginExpiration = 4;
    private static final int Option_6 = 5;
    private static final int Option_7 = 6;

    public DeveloperOptions(Context context) {
        super();
        // Now we can set new default values here before loading.
        // This is important because if no pre-existing data file exists then the default will be used
        setDefault();
        load(context);
    }
    public DeveloperOptions(Context context, String filename) {
        super();
        setDefault();
        load(context, filename);
    }

    @Override
    protected void setDefault() {
        Savelog.d(TAG, debug, "Called setDefault()");

        // new names of fields
        String[] newBinaryOptionLabels = {"BinOption1", "BinOption2", "BinOption3", "BinOption4"};
        String[][] newMultipleChoiceOptionLabels = {{"MultipleChoice1", "MultipleChoice2", "MultipleChoice3"}};
        String[] newGradientOptionLabels = {"Login expires: (1-30 minutes)", "Gradient2"};
        // new default values
        boolean[] newBinaryOptions = {false, false, false, false}; // each entry is true/false
        int[] newMultipleChoiceOptions = {1}; // each entry has multiple choices
        int[] newMultipleChoiceOptionsRanges = {3}; // each entry records how many multiple choices
        double[] newGradientOptions = {1.0, 0.0}; // each entry is a double within a range.
        double[][] newGradientOptionsRanges = {{1, 30}, {0, 1}}; // each pair defines a range.

        binaryOptionLabels = newBinaryOptionLabels;
        multipleChoiceOptionLabels = newMultipleChoiceOptionLabels;
        gradientOptionLabels = newGradientOptionLabels;

        binaryOptions = newBinaryOptions;
        multipleChoiceOptions = newMultipleChoiceOptions;
        multipleChoiceOptionsRanges = newMultipleChoiceOptionsRanges;
        gradientOptions = newGradientOptions;
        gradientOptionsRanges = newGradientOptionsRanges;
    }

    // Here, define the meaning of each of the options

    private static class Type {
        private static final int type_binaryOption = 0;
        private static final int type_multipleChoiceOption = 1;
        private static final int type_gradientOption = 2;
        int group = 0;
        int member = 0;
    }

    public Type getType(int index) {
        Type type = new Type();

        if (index<BinaryOptions_count) {
            type.group = Type.type_binaryOption;
            type.member = index;
        }
        else if (index<BinaryOptions_count+MultipleChoiceOptions_count) {
            int offset = index-BinaryOptions_count;
            type.group = Type.type_multipleChoiceOption;
            type.member = offset;
        }
        else {
            int offset = index-BinaryOptions_count-MultipleChoiceOptions_count;
            type.group = Type.type_gradientOption;
            type.member = offset;
        }
        return type;
    }

    public int getLoginExpiration() {
        Type type = getType(Option_loginExpiration);
        return (int) getGradientOption(type.member);
    }
}
