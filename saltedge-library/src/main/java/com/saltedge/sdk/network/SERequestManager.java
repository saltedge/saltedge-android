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
package com.saltedge.sdk.network;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.models.SEAccount;
import com.saltedge.sdk.models.SELogin;
import com.saltedge.sdk.models.SEProvider;
import com.saltedge.sdk.models.SETransaction;
import com.saltedge.sdk.params.SEBaseParams;
import com.saltedge.sdk.params.SECreateCustomerParams;
import com.saltedge.sdk.utils.SEConstants;
import com.saltedge.sdk.utils.SEJSONTools;
import com.saltedge.sdk.utils.SETools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SERequestManager {

    private ArrayList<SEProvider> providerArray;
    private ArrayList<SEAccount> accountsArray;
    private ArrayList<SETransaction> transactionsArray;
    private String nextPageId = "";
    private static SERequestManager instance;
    private FetchListener fetchListener;

    public static SERequestManager getInstance() {
        if (instance == null) {
            instance = new SERequestManager();
        }
        return instance;
    }

/**
 * Parse JSON Interface
 * */
    public interface FetchListener {
        void onFailure(String errorResponse);

        void onSuccess(Object response);
    }

/**
 *  Customers
 * */
    public void createCustomer(String customerIdentifier, final FetchListener listener) {
        if (customerIdentifier == null || TextUtils.isEmpty(customerIdentifier)) {
            throw new RuntimeException(SEConstants.KEY_SECRET.concat(" " + SEConstants.CANNOT_BE_NULL));
        }
        fetchListener = listener;
        SECreateCustomerParams params = new SECreateCustomerParams(customerIdentifier);
        sendPOSTRequest(SEConstants.CUSTOMERS_URL, params, "", "",
                new SEHTTPResponseHandler(new SEHTTPResponseHandler.RestAPIListener() {
                    @Override
                    public void onFailureResponse(int statusCode, JSONObject errorResponse) {
                        Log.v("tag", "errorResponse " + errorResponse);
                        onFail(errorResponse);
                    }

                    @Override
                    public void onSuccessResponse(int statusCode, JSONObject response) {
                        JSONObject dataObject = SEJSONTools.getObject(response, SEConstants.KEY_DATA);
                        String secret = SEJSONTools.getString(dataObject, SEConstants.KEY_SECRET);
                        onSuccess(secret);
                    }
                }));
    }

/**
 * Tokens
 * */
    public void createToken(SEBaseParams params, String customerSecret, FetchListener listener) {
        requestToken(SEConstants.TAIL_CREATE, params, "", customerSecret, listener);
    }

    public void reconnectToken(SEBaseParams params, String loginSecret, String customerSecret, FetchListener listener) {
        requestToken(SEConstants.TAIL_RECONNECT, params, loginSecret, customerSecret, listener);
    }

    public void refreshToken(SEBaseParams params, String loginSecret, String customerSecret, FetchListener listener) {
        requestToken(SEConstants.TAIL_REFRESH, params, loginSecret, customerSecret, listener);
    }

    private void requestToken(String tailUrl, SEBaseParams params, String loginSecret, String customerSecret, FetchListener listener) {
        fetchListener = listener;
        sendPOSTRequest(SEConstants.TOKENS_URL.concat(tailUrl), params, loginSecret, customerSecret,
                new SEHTTPResponseHandler(new SEHTTPResponseHandler.RestAPIListener() {
                    @Override
                    public void onFailureResponse(int statusCode, JSONObject errorResponse) {
                        onFail(errorResponse);
                    }

                    @Override
                    public void onSuccessResponse(int statusCode, JSONObject response) {
                        try {
                            String connect_url = response.getJSONObject(SEConstants.KEY_DATA)
                                    .getString(SEConstants.KEY_CONNECT_URL);
                            onSuccess(connect_url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }));
    }

/**
 * Provider list
 * */
    public void listingProviders(boolean clearArray, FetchListener listener) {
        fetchListener = listener;
        if (providerArray == null || providerArray.isEmpty() || clearArray) {
            providerArray = new ArrayList<>();
        }
        sendGETRequest(SEConstants.PROVIDERS_URL.concat(nextPageId), null, "", "",
                new SEHTTPResponseHandler(new SEHTTPResponseHandler.RestAPIListener() {

                    @Override
                    public void onFailureResponse(int statusCode, JSONObject errorResponse) {
                        onFail(errorResponse);
                    }

                    @Override
                    public void onSuccessResponse(int statusCode, JSONObject response) {
                        Gson gson = new Gson();
                        String providerArrayString = SEJSONTools.getArray(response, SEConstants.KEY_DATA).toString();
                        for (SEProvider entry : gson.fromJson(providerArrayString, SEProvider[].class)) {
                            providerArray.add(entry);
                        }
                        nextPageId = paginationAvailable(response);
                        if (nextPageId.isEmpty()) {
                            onSuccess(providerArray);
                        } else {
                            listingProviders(false, fetchListener);
                        }
                    }
                }));
    }

/**
 * Logins
 * */
    public void fetchLogin(String loginSecret, String customerSecret, FetchListener listener) {
        fetchListener = listener;
        sendGETRequest(SEConstants.LOGIN_URL, null, loginSecret, customerSecret,
                new SEHTTPResponseHandler(new SEHTTPResponseHandler.RestAPIListener() {
                    @Override
                    public void onFailureResponse(int statusCode, JSONObject errorResponse) {
                        onFail(errorResponse);
                    }

                    @Override
                    public void onSuccessResponse(int statusCode, JSONObject response) {
                        Gson gson = new Gson();
                        SELogin login = gson.fromJson(SEJSONTools.getObject(response,
                                SEConstants.KEY_DATA).toString(), SELogin.class);
                        onSuccess(login);
                    }
                }));
    }

    public void deleteLogin(String loginSecret, String customerSecret, FetchListener listener) {
        fetchListener = listener;
        sendDELETERequest(SEConstants.LOGIN_URL, loginSecret, customerSecret,
                new SEHTTPResponseHandler(new SEHTTPResponseHandler.RestAPIListener() {
                    @Override
                    public void onFailureResponse(int statusCode, JSONObject errorResponse) {
                        onFail(errorResponse);
                    }

                    @Override
                    public void onSuccessResponse(int statusCode, JSONObject response) {
                        Gson gson = new Gson();
                        onSuccess(SEJSONTools.getObject(response, SEConstants.KEY_DATA));
                    }
                }));
    }

/**
 * Accounts
 * */
    public void listingAccounts(final String loginSecret, final String customerSecret, boolean clearArray, FetchListener listener) {
        fetchListener = listener;
        if (accountsArray == null || accountsArray.isEmpty() || clearArray) {
            accountsArray = new ArrayList<>();
        }
        sendGETRequest(SEConstants.ACCOUNTS_URL.concat(nextPageId), null, loginSecret, customerSecret,
                new SEHTTPResponseHandler(new SEHTTPResponseHandler.RestAPIListener() {
                    @Override
                    public void onFailureResponse(int statusCode, JSONObject errorResponse) {
                        onFail(errorResponse);
                    }

                    @Override
                    public void onSuccessResponse(int statusCode, JSONObject response) {
                        Gson gson = new Gson();
                        String providerArrayString = SEJSONTools.getArray(response, SEConstants.KEY_DATA).toString();
                        for (SEAccount entry : gson.fromJson(providerArrayString, SEAccount[].class)) {
                            accountsArray.add(entry);
                        }
                        nextPageId = paginationAvailable(response);
                        if (nextPageId.isEmpty()) {
                            onSuccess(accountsArray);
                        } else {
                            listingAccounts(loginSecret, customerSecret, false, fetchListener);
                        }
                    }
                }));
    }

/**
 * Transactions
 * */
    public void listingTransactionsOfAccount(String loginSecret,
                                             String customerSecret,
                                             int accountId,
                                             FetchListener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put(SEConstants.KEY_ACCOUNT_ID, String.valueOf(accountId));
        listtingTransactions(loginSecret, customerSecret, params, listener);
    }

    public void listingPendingTransactionsOfAccount(String loginSecret,
                                                    String customerSecret,
                                                    int accountId,
                                                    FetchListener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put(SEConstants.KEY_ACCOUNT_ID, String.valueOf(accountId));
        listtingPendingTransactions(loginSecret, customerSecret, params, listener);
    }

    public void listtingTransactions(String loginSecret,
                                     String customerSecret,
                                     final HashMap<String, String> params,
                                     FetchListener listener) {
        fetchTransactions(loginSecret, customerSecret, params, SEConstants.TRANSACTIONS_URL, true, listener);
    }

    public void listtingPendingTransactions(String loginSecret,
                                            String customerSecret,
                                            final HashMap<String, String> params,
                                            FetchListener listener) {
        fetchTransactions(loginSecret, customerSecret, params, SEConstants.PENDING_TRANSACTIONS_URL, true, listener);
    }

    public void fetchTransactions(final String loginSecret,
                                  final String customerSecret,
                                  final HashMap<String, String> params,
                                  final String url,
                                  final boolean clearArray,
                                  FetchListener listener) {
        fetchListener = listener;
        if (transactionsArray == null || transactionsArray.isEmpty() || clearArray) {
            transactionsArray = new ArrayList<>();
        }
        sendGETRequest(url.concat(nextPageId), params, loginSecret, customerSecret,
                new SEHTTPResponseHandler(new SEHTTPResponseHandler.RestAPIListener() {
                    @Override
                    public void onFailureResponse(int statusCode, JSONObject errorResponse) {
                        onFail(errorResponse);
                    }

                    @Override
                    public void onSuccessResponse(int statusCode, JSONObject response) {
                        Gson gson = new Gson();
                        String providerArrayString = SEJSONTools.getArray(response, SEConstants.KEY_DATA).toString();
                        for (SETransaction entry : gson.fromJson(providerArrayString, SETransaction[].class)) {
                            transactionsArray.add(entry);
                        }
                        nextPageId = paginationAvailable(response);
                        if (nextPageId.isEmpty()) {
                            onSuccess(transactionsArray);
                        } else {
                            fetchTransactions(loginSecret, customerSecret, params, url, clearArray, fetchListener);
                        }
                    }
                }));
    }

/**
 * GET, POST, DELETE
 * */
    private boolean sendPOSTRequest(String servicePath,
                                    SEBaseParams params,
                                    String loginSecret,
                                    String customerSecret,
                                    SEHTTPResponseHandler handler) {
        HashMap<String, String> headers = headers(loginSecret, customerSecret);
        return SERestClient.post(servicePath, params.toJson(), handler, headers);
    }

    private boolean sendGETRequest(String servicePath,
                                   HashMap<String, String> params,
                                   String loginSecret,
                                   String customerSecret,
                                   SEHTTPResponseHandler handler) {
        HashMap<String, String> headers = headers(loginSecret, customerSecret);
        String url = servicePath;
        if (params != null) {
            url = url.concat(SETools.paramsToString(params));
        }
        return SERestClient.get(url, handler, headers);
    }

    private boolean sendDELETERequest(String servicePath,
                                      String loginSecret,
                                      String customerSecret,
                                      SEHTTPResponseHandler handler) {
        HashMap<String, String> headers = headers(loginSecret, customerSecret);
        return SERestClient.delete(servicePath, handler, headers);
    }

/**
 * Listener null pointer handle
 * */
    private void onFail(JSONObject errorResponse) {
        if (fetchListener != null) {
            String message = SEJSONTools.getErrorMessage(errorResponse);
            fetchListener.onFailure(message);
        }
    }

    private void onSuccess(Object response) {
        if (fetchListener != null) {
            fetchListener.onSuccess(response);
        }
    }

/**
 * Pagination
 */
    private String paginationAvailable(JSONObject response) {
        try {
            if (response.has(SEConstants.KEY_META)) {
                String nextPage = response.getJSONObject(SEConstants.KEY_META).getString(SEConstants.KEY_NEXT_ID);
                if (!nextPage.equals("null")) {
                    return SEConstants.PREFIX_FROM.concat(nextPage);
                } else {
                    return "";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private HashMap<String, String> headers(String loginSecret, String customerSecret) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(SEConstants.KEY_HEADER_APP_SECRET, SaltEdgeSDK.getInstance().getAppSecret());
        headers.put(SEConstants.KEY_HEADER_CLIENT_ID, SaltEdgeSDK.getInstance().getClientId());
        if (!TextUtils.isEmpty(customerSecret)) {
            headers.put(SEConstants.KEY_HEADER_CUSTOMER_SECRET, customerSecret);
        }
        if (!TextUtils.isEmpty(loginSecret)) {
            headers.put(SEConstants.KEY_HEADER_LOGIN_SECRET, loginSecret);
        }
        for (String name: headers.keySet()){

            String key =name.toString();
            String value = headers.get(name).toString();
            Log.v("tag", "key - " + key + "| value - " + value);


        }
        return headers;
    }

}
