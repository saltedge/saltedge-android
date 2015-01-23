package com.saltedge.sdk.models;

import android.test.ActivityTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by AGalkin
 * On 1/21/15.
 */
public class SEAccountTest extends ActivityTestCase {

    @SmallTest
    public void testCreatedAt() throws Exception {
        SEAccount account = new SEAccount();

        account.setCreatedAt("21313");
        Calendar c = Calendar.getInstance();

        assertThat(account.getCreatedAt(), not(equalTo(c.getTime())));

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        account.setCreatedAt(formattedDate);

        assertEquals(account.getCreatedAt(), not(new Date().getTime()));
    }

}
