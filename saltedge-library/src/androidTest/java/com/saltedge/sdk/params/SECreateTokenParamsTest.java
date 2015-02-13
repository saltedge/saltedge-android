package com.saltedge.sdk.params;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

/**
 * Created by AGalkin
 * On 1/28/15.
 */
public class SECreateTokenParamsTest extends TestCase {

    @SmallTest
    public void testCreateTokenToJson() throws Exception {
        SECreateTokenParams params = new SECreateTokenParams("US", "USD", "http://back", "customerId");
        String stringParams = "{\"data\":{\"country_code\":\"US\",\"customer_id\":\"customerId\",\"provider_code\":\"USD\",\"return_to\":\"http://back\"}}";
        assertEquals(stringParams, params.toJson().toString());
    }

}
