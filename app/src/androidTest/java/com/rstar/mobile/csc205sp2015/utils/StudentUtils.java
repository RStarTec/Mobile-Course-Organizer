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
 * Created by AHui on 1/30/15.
 */
public class StudentUtils {


    public static class StudentId {
        public String userId;
        public String accessCode;

        // 10 valid user accounts on private server
        public StudentId(int set) {
            if (set==0) {
                userId = "";
                accessCode = "";
            }
            else if (set==1) {
                userId = "nova1";
                accessCode = "usr1";
            }
            else if (set==2) {
                userId = "nova2";
                accessCode = "usr2";
            }
            else if (set==3) {
                userId = "nova3";
                accessCode = "usr3";
            }
            else if (set==4) {
                userId = "nova4";
                accessCode = "usr4";
            }
            else if (set==5) {
                userId = "nova5";
                accessCode = "usr5";
            }
            else if (set==6) {
                userId = "nova6";
                accessCode = "usr6";
            }
            else if (set==7) {
                userId = "nova7";
                accessCode = "usr7";
            }
            else if (set==8) {
                userId = "nova8";
                accessCode = "usr8";
            }
            else if (set==9) {
                userId = "nova9";
                accessCode = "usr9";
            }
            else if (set==10) {
                userId = "nova10";
                accessCode = "usr10";
            }
        }
    }
}
