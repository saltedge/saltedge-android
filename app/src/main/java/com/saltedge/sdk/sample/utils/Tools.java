/*
Copyright © 2015 Salt Edge. https://saltedge.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.saltedge.sdk.sample.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Tools {

    public static void addStringToPreferences(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(key, value).commit();
    }

    public static String getStringFromPreferences(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public static String[] getArrayFromPreferences(Context context, String key) {
        Set set = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(key, new HashSet<String>());
        return (String[])set.toArray(new String[set.size()]);
    }

    public static void addStringToArrayPreferences(Context context, String key, String value) {
        Set<String> set = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(key, new HashSet<String>());
        set.add(value);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(key, set).commit();
    }

    public static String parseDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        try {
            return sdf.format(date);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return "";
    }
}


