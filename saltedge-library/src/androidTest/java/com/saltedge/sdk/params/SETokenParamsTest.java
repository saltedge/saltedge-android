package com.saltedge.sdk.params;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by AGalkin
 * On 1/28/15.
 */
public class SETokenParamsTest extends TestCase {

    @SmallTest
    public void testTokenToJson() throws Exception {
        SETokenParams params = new SETokenParams(123, "en-US", "http://back", true, new ArrayList<>(Arrays.asList("one", "two", "three")));
        String stringParams = "{\"data\":{\"locale\":\"en-US\",\"return_to\":\"http://back\",\"provider_modes\":[\"one\",\"two\",\"three\"],\"return_login_id\":true,\"login_id\":123}}";
        assertEquals(stringParams, params.toJson().toString());
    }

}