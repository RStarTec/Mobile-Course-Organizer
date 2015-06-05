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

package com.rstar.mobile.csc205sp2015.registered.api;

/**
 * Created by AHui on 1/25/15.
 */
public class Validity {
    private static final String pattern_slash = ".*\\\\.*"; // contains backslash
    private static final String pattern_tag = ".*[<>].*";   // contains < or > (that is tag)
    private static final String pattern_string = "\\w+";    // must consist of 1 or more word-character
    private static final String pattern_email = "\\w+@\\w+(\\.\\w+){1,3}"; // must consist of word-characters followed by @, more word-characters separated by 1-3 dots


    // Determine if a string is non-empty and php-safe.
    private static boolean isSafe(String string) {
        if (string==null) return false;
        if (string.matches(pattern_slash)) return false;
        else if (string.matches(pattern_tag)) return false;
        return true;
    }

    public static boolean isValid(String string) {
        if (isSafe(string)) {
            if (string.matches(pattern_string)) return true;
            else return false;
        }
        else return false;
    }

    public static boolean isEmail(String string) {
        if (isSafe(string)) {
            if (string.matches(pattern_email)) return true;
            else return false;
        }
        else return false;
    }
}
