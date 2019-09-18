/*
Copyright © 2019 Salt Edge. https://saltedge.com

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
package com.saltedge.sdk.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SEJsonToolsTest {

    @Test
    public void testGetString() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "value");

        assertThat(SEJsonTools.getString(jsonObject, "name"), equalTo("value"));
    }

    @Test
    public void testGetInt() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameInt", 12);

        assertEquals(12, SEJsonTools.getInt(jsonObject, "nameInt"));
    }

    @Test
    public void testGetBoolean() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameBoolean", true);

        assertTrue(SEJsonTools.getBoolean(jsonObject, "nameBoolean"));
    }

    @Test
    public void testGetDouble() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameDouble", 12.12);

        assertThat(SEJsonTools.getDouble(jsonObject, "nameDouble"), equalTo(12.12));
    }

    @Test
    public void testGetJsonArray() throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        array.put("test1");
        array.put("test2");
        array.put("test3");
        jsonObject.put("nameArray", array);

        assertEquals(3, SEJsonTools.getJSONArray(jsonObject, "nameArray").length());

        assertEquals("test2", SEJsonTools.getJSONArray(jsonObject, "nameArray").get(1));
    }

    @Test
    public void testGetJsonObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameJsonObject", new JSONObject());

        assertEquals(JSONObject.class, SEJsonTools.getObject(jsonObject, "nameJsonObject").getClass());
    }

    @Test
    public void testStringToJSON() throws Exception {

        assertEquals(JSONObject.class, SEJsonTools.stringToJSON("{\"data\":customerId\"}").getClass());
    }
}
