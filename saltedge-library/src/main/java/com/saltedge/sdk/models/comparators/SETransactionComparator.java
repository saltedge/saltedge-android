package com.saltedge.sdk.models.comparators;

import com.saltedge.sdk.models.SETransaction;

import java.util.Comparator;

/**

 * On 1/19/15.
 */
public class SETransactionComparator implements Comparator<SETransaction> {

    @Override
    public int compare(SETransaction o1, SETransaction o2) {
        return o1.getMadeOn().compareTo(o2.getMadeOn());
    }
}
