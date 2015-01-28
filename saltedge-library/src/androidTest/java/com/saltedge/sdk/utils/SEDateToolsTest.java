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
        assertEquals(1421989200000L, SEDateTools.parseShortStringToDate("2015-01-23").getTime());
    }

}
