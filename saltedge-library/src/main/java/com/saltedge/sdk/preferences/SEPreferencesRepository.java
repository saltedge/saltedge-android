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
package com.saltedge.sdk.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.saltedge.sdk.SaltEdgeSDK;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SEPreferencesRepository {

    private static final String KEY_PINS_EXPIRE_AT = "KEY_SE_PINS_EXPIRE_AT";
    private static final String KEY_PINS_SET = "KEY_SE_PINS_SET";
    private static SEPreferencesRepository instance;
    private String[] pinsArray = new String[0];
    private long expireAt = 0L;

    public static SEPreferencesRepository getInstance() {
        if (instance == null) {
            instance = new SEPreferencesRepository();
        }
        return instance;
    }

    private SEPreferencesRepository() {
        loadStoredValues();
    }

    public void updatePinsAndMaxAge(String[] pinsArray, long expireAt) {
        try {
            this.pinsArray = pinsArray == null ? new String[0] : pinsArray;
            this.expireAt = expireAt;
            SharedPreferences.Editor editor = getPreferences(SaltEdgeSDK.getInstance().getContext()).edit();
            editor.putLong(KEY_PINS_EXPIRE_AT, this.expireAt);
            editor.putStringSet(KEY_PINS_SET, new HashSet<>(Arrays.asList(this.pinsArray)));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getPins() {
        return pinsArray;
    }

    public long getExpireAt() {
        return expireAt;
    }

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void loadStoredValues() {
        try {
            SharedPreferences preferences = getPreferences(SaltEdgeSDK.getInstance().getContext());
            Set<String> result = preferences.getStringSet(KEY_PINS_SET, new HashSet<String>());
            pinsArray = result.toArray(new String[result.size()]);
            expireAt = preferences.getLong(KEY_PINS_EXPIRE_AT, 0L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
