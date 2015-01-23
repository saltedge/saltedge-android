package com.saltedge.sdk.models.comparators;

import com.saltedge.sdk.models.SELogin;

import java.util.Comparator;

/**

 * On 1/18/15.
 */
public class SELoginComparator implements Comparator<SELogin> {

    @Override
    public int compare(SELogin o1, SELogin o2) {
        return o1.getProviderName().compareTo(o2.getProviderName());
    }
}
