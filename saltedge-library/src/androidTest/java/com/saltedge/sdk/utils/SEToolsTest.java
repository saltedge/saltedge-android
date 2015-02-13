package com.saltedge.sdk.utils;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by AGalkin
 * On 1/28/15.
 */
public class SEToolsTest extends TestCase {

    @SmallTest
    public void testParamsToString() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("one", "value_one");
        map.put("two", "value_two");
        map.put("three", "value_three");
        map.put("four", "value_four");

        assertEquals("?three=value_three&two=value_two&four=value_four&one=value_one", SETools.paramsToString(map));
    }
}
