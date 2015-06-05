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

package com.rstar.mobile.csc205sp2015.fields;

import com.rstar.mobile.csc205sp2015.textscreen.TextSize;

/**
 * Created by AHui on 1/21/15.
 */
public class TextSizeFields {
    public static class Constants extends Fields.Constants {
        private static final String privateField1 = "PREF_textsize";

        public String PREF_textsize = null;

        public Constants() throws Exception { get(); }

        // Constant fields only need to be copied once
        @Override
        public void get() throws Exception {
            PREF_textsize = getPrefTextsize();
        }
        @Override
        public void detach() {
            PREF_textsize = null;
        }
        public static String getPrefTextsize() throws Exception
            { return RefUtil.getPrivateField(TextSize.class, privateField1, String.class); }
    }

    public static class Variables extends Fields.Variables<TextSize> {
        private static final String privateField1 = "MinTextSize";
        private static final String privateField2 = "MaxTextSize";
        private static final String privateField3 = "DefaultTextSize";
        private static final String privateField4 = "textSize";

        public int MinTextSize = -1;
        public int MaxTextSize = -1;
        public int DefaultTextSize = -1;
        public int textSize = -1;

        public Variables(TextSize textsize) throws Exception {
            refresh(textsize);
        }

        @Override
        public void refresh(TextSize textsize) throws Exception {
            MinTextSize = getMinTextSize(textsize);
            MaxTextSize = getMaxTextSize(textsize);
            DefaultTextSize = getDefaultTextSize(textsize);
            textSize = getTextSize(textsize);

        }
        @Override
        public void detach() {
            MinTextSize = -1;
            MaxTextSize = -1;
            DefaultTextSize = -1;
            textSize = -1;
        }

        public static Integer getMinTextSize(TextSize textsize) throws Exception
            { return RefUtil.getPrivateField(textsize, privateField1, Integer.class); }
        public static Integer getMaxTextSize(TextSize textsize) throws Exception
            { return RefUtil.getPrivateField(textsize, privateField2, Integer.class); }
        public static Integer getDefaultTextSize(TextSize textsize) throws Exception
            { return RefUtil.getPrivateField(textsize, privateField3, Integer.class); }
        public static Integer getTextSize(TextSize textsize) throws Exception
            { return RefUtil.getPrivateField(textsize, privateField4, Integer.class); }

    }
}
