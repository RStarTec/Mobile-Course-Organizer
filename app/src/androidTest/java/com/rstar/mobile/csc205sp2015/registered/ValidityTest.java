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

package com.rstar.mobile.csc205sp2015.registered;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.rstar.mobile.csc205sp2015.fields.LoginApiFields;
import com.rstar.mobile.csc205sp2015.registered.api.Validity;
import com.rstar.mobile.csc205sp2015.utils.DataUtils;

/**
 * Created by AHui
 */
public class ValidityTest extends InstrumentationTestCase {
    private static final String TAG = ValidityTest.class.getSimpleName()+"_class";

    Context targetContext = null;
    Context testContext = null;
    LoginApiFields.Constants constants = null;

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        testContext = getInstrumentation().getContext();
        constants = new LoginApiFields.Constants();
        DataUtils.clearAll(targetContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        constants.detach();
        DataUtils.clearAll(targetContext);
    }

    public void testStringsValidity() {
        {   // empty string
            String data = null;
            assertFalse(Validity.isValid(data));
        }
        {   // empty string
            String data = "";
            assertFalse(Validity.isValid(data));
        }
        {   // simple string
            String data = "a";
            assertTrue(Validity.isValid(data));
        }
        {   // simple string
            String data = "abc123def0_";
            assertTrue(Validity.isValid(data));
        }
        {   // complex string
            String data = "abc123def={}[]()|-+*/&:;?!,.`~";
            assertFalse(Validity.isValid(data));
        }
        {   // complex string
            String data = "\'\"";
            assertFalse(Validity.isValid(data));
        }

        {   // bad string
            String data = "a\\b";
            assertFalse(Validity.isValid(data));
        }
        {   // bad string
            String data = "a<b";
            assertFalse(Validity.isValid(data));
        }
        {   // bad string
            String data = "a>b";
            assertFalse(Validity.isValid(data));
        }
        {   // bad string
            String data = "\\";
            assertFalse(Validity.isValid(data));
        }
        {   // bad string
            String data = "<";
            assertFalse(Validity.isValid(data));
        }
        {   // bad string
            String data = ">";
            assertFalse(Validity.isValid(data));
        }

    }


    public void testEmail() {
        {   // empty string
            String data = "";
            assertFalse(Validity.isEmail(data));
        }

        {   // simple string
            String data = "abc";
            assertFalse(Validity.isEmail(data));
        }
        {   // simple string
            String data = "abc@def";
            assertFalse(Validity.isEmail(data));
        }
        {   // good string
            String data = "abc@def.123";
            assertTrue(Validity.isEmail(data));
        }
        {   // good string
            String data = "abc@def.ghi";
            assertTrue(Validity.isEmail(data));
        }
        {   // good string
            String data = "abc@def.123.ghi";
            assertTrue(Validity.isEmail(data));
        }
        {   // good string
            String data = "aD_0@def.123.ghi";
            assertTrue(Validity.isEmail(data));
        }

        {   // good string
            String data = "aD_0@def.123";
            assertTrue(Validity.isEmail(data));
        }

        {   // good string
            String data = "aD_0@def.123.ghi.456";
            assertTrue(Validity.isEmail(data));
        }

        {   // bad string
            String data = "aD_0@def.123.ghi.456.jkl";
            assertFalse(Validity.isEmail(data));
        }

        {   // bad string
            String data = "aD_0@def.123.gh!";
            assertFalse(Validity.isEmail(data));
        }

        {   // bad string
            String data = "aD_0@def!";
            assertFalse(Validity.isEmail(data));
        }

        {   // bad string
            String data = "#@def!";
            assertFalse(Validity.isEmail(data));
        }

        {   // bad string
            String data = "-@def!";
            assertFalse(Validity.isEmail(data));
        }

        {   // bad string
            String data = "@abc.def.ghi";
            assertFalse(Validity.isEmail(data));
        }

        {   // bad string
            String data = "abc@";
            assertFalse(Validity.isEmail(data));
        }
        {   // bad string
            String data = "abc.d12@";
            assertFalse(Validity.isEmail(data));
        }

    }

}
