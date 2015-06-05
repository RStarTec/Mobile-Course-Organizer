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

package com.rstar.mobile.csc205sp2015.utils;

/**
 * Created by Ahui on 1/16/15.
 */
public class HomeworkUtils {
    public static class DataSet {
        public int moduleNumber;

        public DataSet(int set) throws Exception {

            if (set==0) {
                // Bad one
                moduleNumber = 101;
            }
            else if (set==0) {
                // Bad one
                moduleNumber = 102;
            }
            else if (set==-1) {
                // Bad one
                moduleNumber = 103;
            }
            else if (set==-2) {
                // Bad one
                moduleNumber = 104;
            }
            else if (set==-3) {
                // Bad one
                moduleNumber = 105;
            }
            else if (set==-4) {
                // Bad one
                moduleNumber = 106;
            }
            else if (set==-5) {
                // Bad one
                moduleNumber = 107;
            }
            else if (set==-6) {
                // Bad one
                moduleNumber = 108;
            }
            else if (set==1) {
                // Godd one
                moduleNumber = 109;
            }
            else if (set==2) {
                // Good one
                moduleNumber = 110;
            }
            else {
            }
        }
    }

}
