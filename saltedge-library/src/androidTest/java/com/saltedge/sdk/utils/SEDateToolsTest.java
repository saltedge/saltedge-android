package com.saltedge.sdk.utils;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AGalkin
 * On 1/27/15.
 */
public class SEDateToolsTest extends TestCase {

    @SmallTest
    public void testParseStringToDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date date = sdf.parse("2015-01-23T15:05:13Z");
        assertEquals(date, SEDateTools.parseStringToDate("2015-01-23T15:05:13Z"));
    }

    @SmallTest
    public void testParseShortStringToDate() throws Exception {
        assertEquals("Fri Jan 23 00:00:00 GMT+00:00 2015", SEDateTools.parseShortStringToDate("2015-01-23").toString());
    }

    @SmallTest
    public void testParseDateToShortStrin() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date date = sdf.parse("2015-01-23T15:05:13Z");
        assertEquals("2015-01-23", SEDateTools.parseDateToShortString(date));
    }
}
