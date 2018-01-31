package com.saltedge.sdk.utils;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by AGalkin
 * On 1/28/15.
 */
public class SEJSONToolsTest extends TestCase {

    @SmallTest
    public void testGetString() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "value");

        assertEquals("value", SEJsonTools.getString(jsonObject, "name"));
    }

    @SmallTest
    public void testGetInt() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameInt", 12);

        assertEquals(12, SEJsonTools.getInt(jsonObject, "nameInt"));
    }

    @SmallTest
    public void testGetBoolean() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameBoolean", true);

        assertEquals(true, SEJsonTools.getBoolean(jsonObject, "nameBoolean").booleanValue());
    }

    @SmallTest
    public void testGetDouble() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameDouble", 12.12);

        assertEquals(12.12, SEJsonTools.getDouble(jsonObject, "nameDouble"));
    }

    @SmallTest
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

    @SmallTest
    public void testGetJsonObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameJsonObject", new JSONObject());

        assertEquals(JSONObject.class, SEJsonTools.getObject(jsonObject, "nameJsonObject").getClass());
    }

    @SmallTest
    public void testStringToJSON() throws Exception {

        assertEquals(JSONObject.class, SEJsonTools.stringToJSON("{\"data\":customerId\"}").getClass());
    }

    @SmallTest
    public void testGetErrorMessage() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "error message");

        assertEquals("error message", SEJsonTools.getErrorMessage(jsonObject));
    }

}
