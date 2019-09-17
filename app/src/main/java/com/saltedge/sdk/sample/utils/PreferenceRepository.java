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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class PreferenceRepository {

    private static SharedPreferences sharedPreferences = null;

    public static void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static void putStringToPreferences(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getStringFromPreferences(String key) {
        return sharedPreferences.getString(key, "");
    }

    public static void removeRecordFromPreferences(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public static void putConnectionSecret(String connectionSecret) {
        putStringToArrayPreferences(Constants.CONNECTIONS_SECRETS_ARRAY, connectionSecret);
    }

    public static String[] getAllConnectionsSecrets() {
        return getArrayFromPreferences(Constants.CONNECTIONS_SECRETS_ARRAY);
    }

    public static void removeConnectionSecret(String connectionSecret) {
        if (!connectionSecret.isEmpty()) {
            removeStringFromArrayPreferences(Constants.CONNECTIONS_SECRETS_ARRAY, connectionSecret);
        }
    }

    public static boolean connectionSecretIsSaved(String connectionSecret) {
        return !connectionSecret.isEmpty()
                && Arrays.asList(getAllConnectionsSecrets()).contains(connectionSecret);
    }

    private static void putStringToArrayPreferences(String arrayKey, String value) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(getArrayFromPreferences(arrayKey)));
        if (!list.contains(value)) list.add(value);
        sharedPreferences.edit().putString(arrayKey, new Gson().toJson(list)).apply();
    }

    private static void removeStringFromArrayPreferences(String arrayKey, String value) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(getArrayFromPreferences(arrayKey)));
        list.remove(value);
        sharedPreferences.edit().putString(arrayKey, new Gson().toJson(list)).apply();
    }

    private static String[] getArrayFromPreferences(String key) {
        String array = sharedPreferences.getString(key, "[]");
        return new Gson().fromJson(array, String[].class);
    }
}
