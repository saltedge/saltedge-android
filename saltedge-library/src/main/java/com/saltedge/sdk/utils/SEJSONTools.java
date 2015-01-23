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
package com.saltedge.sdk.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SEJSONTools {

    /**
     * Extract string, int, boolean, double, array, object, stringToJSON value from JSONObject by key.
     *
     * @param jsonObject
     * @param key
     * @return string value by key from json Object or empty string
     */
    public static String getString(JSONObject jsonObject, String key) {
        String result = "";
        return (String) parse(jsonObject, key, result);
    }

    public static int getInt(JSONObject jsonObject, String key) {
        int result = 0;
        return (int) parse(jsonObject, key, result);
    }

    public static Boolean getBoolean(JSONObject jsonObject, String key) {
        Boolean result = null;
        return (Boolean) parse(jsonObject, key, result);
    }

    public static Double getDouble(JSONObject jsonObject, String key) {
        double result = 0d;
        return (Double) parse(jsonObject, key, result);
    }

    public static JSONArray getArray(JSONObject jsonObject, String key) {
        JSONArray result = new JSONArray();
        return (JSONArray) parse(jsonObject, key, result);
    }

    public static JSONObject getObject(JSONObject jsonObject, String key) {
        JSONObject result = new JSONObject();
        return (JSONObject) parse(jsonObject, key, result);
    }

    public static JSONObject stringToJSON(String string) {
        JSONObject result = new JSONObject();
        if (string == null || string.isEmpty()) {
            return result;
        }
        try {
            return new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static String getErrorMessage(JSONObject error) {
        String result = SEConstants.REQUEST_ERROR;
        if (!SEJSONTools.getString(error, SEConstants.KEY_MESSAGE).isEmpty()) {
            result = SEJSONTools.getString(error, SEConstants.KEY_MESSAGE);
        }
        return result;
    }

    private static boolean validation(JSONObject jsonObject, String key) {
        return jsonObject == null || key == null || key.isEmpty() || jsonObject.isNull(key);
    }

    private static Object parse(JSONObject jsonObject, String key, Object result) {
        if (validation(jsonObject, key)) {
            return result;
        }
        try {
            if (result.getClass().equals(JSONObject.class)) {
                return jsonObject.getJSONObject(key);
            } else if (result.getClass().equals(String.class)) {
                return jsonObject.getString(key);
            } else if (result.getClass().equals(JSONArray.class)) {
                return jsonObject.getJSONArray(key);
            } else if (result.getClass().equals(Double.class)) {
                return jsonObject.getDouble(key);
            } else if (result.getClass().equals(Boolean.class)) {
                return jsonObject.getBoolean(key);
            } else if (result.getClass().equals(Integer.class)) {
                return jsonObject.getInt(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}