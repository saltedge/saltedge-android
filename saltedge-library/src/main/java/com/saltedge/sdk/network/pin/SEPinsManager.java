/*
Copyright © 2018 Salt Edge. https://saltedge.com

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
package com.saltedge.sdk.network.pin;

import com.saltedge.sdk.network.SERestClient;
import com.saltedge.sdk.preferences.SEPreferencesRepository;
import com.saltedge.sdk.utils.SEDateTools;
import com.saltedge.sdk.utils.SEErrorTools;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SEPinsManager implements Callback<Void> {

    private static final String KEY_PUBLIC_KEY_PINS = "Public-Key-Pins";

    private static SEPinsManager instance;
    private PinsLoaderResult pinLoaderCallback;

    public static SEPinsManager getInstance() {
        if (instance == null) {
            instance = new SEPinsManager();
        }
        return instance;
    }

    private final static String MAX_AGE_PREFIX = "max-age=";
    private final static String SHA256_PIN_PREFIX = "pin-sha256=";
    private final static String SHA256_PREFIX = "sha256/";

    public void loadPins(PinsLoaderResult callback) {
        this.pinLoaderCallback = callback;
        SERestClient.getInstance().service.getPins().enqueue(this);
    }

    public void clearPinLoaderCallback() {
        this.pinLoaderCallback = null;
    }

    public static boolean savedPinsExpired() {
        String[] pins = SEPreferencesRepository.getInstance().getPins();
        long expireAt = SEPreferencesRepository.getInstance().getExpireAt();
        return !pinsAreValid(pins, expireAt);
    }

    public static boolean pinsAreValid(String[] pinsArray, long expireAt) {
        long currentTime = System.currentTimeMillis();
        if (pinsArray.length == 0 || expireAt <= currentTime) return false;
        for (String pinItem : pinsArray) {
            if (!pinItem.isEmpty()) return true;
        }
        return false;
    }

    public static String[] extractPins(String pinsHeader) {
        if (pinsHeader == null || pinsHeader.isEmpty()) return new String[0];
        ArrayList<String> result = new ArrayList<>();
        try {
            String[] pinArray = pinsHeader.split(";");
            for (String pinItem : pinArray) {
                String trimmedItem = pinItem.trim();
                if (trimmedItem.startsWith(SHA256_PIN_PREFIX)) {
                    addPinToList(trimmedItem, SHA256_PIN_PREFIX, SHA256_PREFIX, result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toArray(new String[result.size()]);
    }

    public static int extractMaxAge(String pinsHeader) {
        if (pinsHeader == null || pinsHeader.isEmpty()) return 0;
        try {
            String[] pinArray = pinsHeader.split(";");
            for (String pinItem : pinArray) {
                String trimmedItem = pinItem.trim();
                if (trimmedItem.startsWith(MAX_AGE_PREFIX) && trimmedItem.length() > MAX_AGE_PREFIX.length()) {
                    String[] maxAgeKeyValue = pinItem.split("=");
                    return Integer.valueOf(maxAgeKeyValue[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void addPinToList(String item, String initialPrefix, String targetPrefix, ArrayList<String> targetList) {
        String pinValue = item.replace(initialPrefix, "").replace("\"", "").trim();
        if (!pinValue.isEmpty()) {
            targetList.add(targetPrefix + pinValue);
        }
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        String pinsHeader = response.headers().get(KEY_PUBLIC_KEY_PINS);
        if (pinsHeader != null && !pinsHeader.isEmpty()) {
            String[] pinsArray = SEPinsManager.extractPins(pinsHeader);
            int maxAge = SEPinsManager.extractMaxAge(pinsHeader);
            long expireAt = SEDateTools.convertMaxAgeToExpireAt(maxAge);
            if (SEPinsManager.pinsAreValid(pinsArray, expireAt)) {
                SEPreferencesRepository.getInstance().updatePinsAndMaxAge(pinsArray, expireAt);
                SERestClient.getInstance().initService();
                if (pinLoaderCallback != null) pinLoaderCallback.onPinLoadSuccess();
            } else {
                onPinLoadFailure(SEErrorTools.ERROR_INVALID_HPKP);
            }
        } else {
            onPinLoadFailure(SEErrorTools.ERROR_INVALID_HPKP);
        }
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        onPinLoadFailure(t.getLocalizedMessage());
    }

    private void onPinLoadFailure(String message) {
        if (pinLoaderCallback != null) pinLoaderCallback.onPinLoadFailure(message);
    }
}
