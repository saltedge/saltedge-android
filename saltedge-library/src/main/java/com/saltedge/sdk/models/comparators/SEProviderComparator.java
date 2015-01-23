package com.saltedge.sdk.models.comparators;

import com.saltedge.sdk.models.SEProvider;

import java.util.Comparator;

/**

 * On 1/18/15.
 */
public class SEProviderComparator implements Comparator<SEProvider> {

    @Override
    public int compare(SEProvider o1, SEProvider o2) {
        return o1.getName().compareTo(o2.getName());
    }

}