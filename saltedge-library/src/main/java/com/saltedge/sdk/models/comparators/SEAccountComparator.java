package com.saltedge.sdk.models.comparators;

import com.saltedge.sdk.models.SEAccount;

import java.util.Comparator;

/**

 * On 1/18/15.
 */
public class SEAccountComparator implements Comparator<SEAccount>{

    @Override
    public int compare(SEAccount o1, SEAccount o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
