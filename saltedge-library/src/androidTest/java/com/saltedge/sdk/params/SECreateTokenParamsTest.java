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
        String[] allowedCountries = {"US", "GE", "RU"};
        SECreateTokenParams params = new SECreateTokenParams(allowedCountries, "fakebank_simple_xf", "http://back");
        String stringParams = "{\"data\":{\"allowed_countries\":[\"US\",\"GE\",\"RU\"]],\"provider_code\":\"fakebank_simple_xf\",\"return_to\":\"http://back\"}}";
        assertEquals(stringParams, params.toJson().toString());
    }

}
