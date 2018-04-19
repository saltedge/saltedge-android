/*
Copyright © 2018 Salt Edge. https://saltedge.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.saltedge.sdk.pin;

import android.test.suitebuilder.annotation.SmallTest;

import com.saltedge.sdk.network.pin.SEPinsManager;

import junit.framework.TestCase;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SEPinsHelperTest extends TestCase {

    private String testHeader = "pin-sha256=\"AAAAAAAAAAAAAAAAAAAAAAA=\"; pin-sha256=\"BBBBBBBBBBBBBBBBBBBB=\"; pin-sha256=\"CCCCCCCCCCCCCCCCCC=\"; pin-sha256=\"DDDDDDDDDDDDDDDD=\"; max-age=604800; includeSubDomains";

    @SmallTest
    public void testPinsAreValid() throws Exception {
        String[] pins = new String[0];

        assertFalse(SEPinsManager.pinsAreValid(pins, 0L));

        pins = new String[] {""};

        assertFalse(SEPinsManager.pinsAreValid(pins, 0L));

        assertFalse(SEPinsManager.pinsAreValid(pins, System.currentTimeMillis() + 1000));

        pins = new String[] {"hash1", ""};

        assertTrue(SEPinsManager.pinsAreValid(pins, System.currentTimeMillis() + 1000));
    }

    @SmallTest
    public void testExtractMaxAge() throws Exception {
        assertThat(SEPinsManager.extractMaxAge(null), equalTo(0));
        assertThat(SEPinsManager.extractMaxAge(""), equalTo(0));
        assertThat(SEPinsManager.extractMaxAge("max-age="), equalTo(0));
        assertThat(SEPinsManager.extractMaxAge("max-age=604800"), equalTo(604800));
        assertThat(SEPinsManager.extractMaxAge(testHeader), equalTo(604800));
    }

    @SmallTest
    public void testExtractPins() throws Exception {
        String[] emptyPinsArray = new String[0];

        assertThat(SEPinsManager.extractPins(null), equalTo(emptyPinsArray));
        assertThat(SEPinsManager.extractPins(""), equalTo(emptyPinsArray));

        String[] testPins = new String[] {
                "sha256/AAAAAAAAAAAAAAAAAAAAAAA=",
                "sha256/BBBBBBBBBBBBBBBBBBBB=",
                "sha256/CCCCCCCCCCCCCCCCCC=",
                "sha256/DDDDDDDDDDDDDDDD="
        };

        assertThat(SEPinsManager.extractPins(testHeader), equalTo(testPins));
    }
}
