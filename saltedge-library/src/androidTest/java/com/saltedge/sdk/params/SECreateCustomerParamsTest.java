package com.saltedge.sdk.params;

import android.test.suitebuilder.annotation.SmallTest;

import com.saltedge.sdk.utils.SEDateTools;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AGalkin
 * On 1/28/15.
 */
public class SECreateCustomerParamsTest extends TestCase {

    @SmallTest
    public void testCreateCustomerToJson() throws Exception {
        SECreateCustomerParams params = new SECreateCustomerParams("customerId");
        assertEquals("{\"data\":{\"identifier\":\"customerId\"}}", params.toJson().toString());
    }

}
