/*
Copyright © 2019 Salt Edge. https://saltedge.com

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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreferencesTools {

    public static void putStringToPreferences(Context context, String key, String value) {
        getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String getStringFromPreferences(Context context, String key) {
        return getDefaultSharedPreferences(context).getString(key, "");
    }

    public static void removeRecordFromPreferences(Context context, String key) {
        getDefaultSharedPreferences(context).edit().remove(key).apply();
    }

    public static void putConnectionSecret(Context context, String connectionId, String connectionSecret) {
        putStringToPreferences(context, connectionId, connectionSecret);
        putStringToArrayPreferences(context, Constants.CONNECTIONS_SECRETS_ARRAY, connectionSecret);
    }

    public static String[] getConnectionSecrets(Context context) {
        return getArrayFromPreferences(context, Constants.CONNECTIONS_SECRETS_ARRAY);
    }

    public static void removeConnectionSecret(Context context, String connectionId) {
        String connectionSecret = getStringFromPreferences(context, connectionId);
        if (!connectionSecret.isEmpty()) {
            removeStringFromArrayPreferences(context, Constants.CONNECTIONS_SECRETS_ARRAY, connectionSecret);
        }
        removeRecordFromPreferences(context, connectionId);
    }

    public static boolean connectionSecretIsSaved(Context context, String connectionId, String connectionSecret) {
        return !connectionId.isEmpty() && !connectionSecret.isEmpty()
                && getStringFromPreferences(context, connectionId).equals(connectionSecret)
                && Arrays.asList(getConnectionSecrets(context)).contains(connectionSecret);
    }

    private static void putStringToArrayPreferences(Context context, String arrayKey, String value) {
        SharedPreferences preferences = getDefaultSharedPreferences(context);
        Set<String> set = preferences.getStringSet(arrayKey, new HashSet<>());
        set.add(value);
        preferences.edit().putStringSet(arrayKey, set).apply();
    }

    private static void removeStringFromArrayPreferences(Context context, String arrayKey, String value) {
        SharedPreferences preferences = getDefaultSharedPreferences(context);
        Set<String> set = preferences.getStringSet(arrayKey, new HashSet<>());
        set.remove(value);
        preferences.edit().putStringSet(arrayKey, set).apply();
    }

    private static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    private static String[] getArrayFromPreferences(Context context, String key) {
        Set set = getDefaultSharedPreferences(context).getStringSet(key, new HashSet<>());
        return (String[]) set.toArray(new String[set.size()]);
    }
}


