package com.saltedge.sdk.params;

import android.test.suitebuilder.annotation.SmallTest;

import com.saltedge.sdk.model.request.CreateCustomerRequest;

import junit.framework.TestCase;

/**
 * Created by AGalkin
 * On 1/28/15.
 */
public class SECreateCustomerParamsTest extends TestCase {

    @SmallTest
    public void testCreateCustomerToJson() throws Exception {
        CreateCustomerRequest params = new CreateCustomerRequest("customerId");
        assertEquals("{\"data\":{\"identifier\":\"customerId\"}}", params.toJson().toString());
    }

}
