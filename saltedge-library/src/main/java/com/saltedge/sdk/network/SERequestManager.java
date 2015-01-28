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
import com.saltedge.sdk.utils.SEDateTools;
import com.saltedge.sdk.utils.SEJSONTools;
import com.saltedge.sdk.utils.SETools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
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
    public void createCustomer(String customerId, final FetchListener listener) {
        if (customerId == null || customerId.isEmpty()) {
            throw new RuntimeException(SEConstants.KEY_CUSTOMER_ID.concat(" " + SEConstants.CANNOT_BE_NULL));
        }
        fetchListener = listener;
        SECreateCustomerParams params = new SECreateCustomerParams(customerId);
        sendPOSTRequest(SEConstants.CUSTOMERS_URL, params, "",
                new SEHTTPResponseHandler(new SEHTTPResponseHandler.RestAPIListener() {
            @Override
            public void onFailureResponse(int statusCode, JSONObject errorResponse) {
                onFail(errorResponse);
            }

            @Override
            public void onSuccessResponse(int statusCode, JSONObject response) {
                JSONObject dataObject = SEJSONTools.getObject(response, SEConstants.KEY_DATA);
                String customerId = SEJSONTools.getString(dataObject, SEConstants.KEY_CUSTOMER_ID);
                onSuccess(customerId);
            }
        }));
    }

/**
 * Tokens
 * */
    public void createToken(SEBaseParams params, FetchListener listener) {
        requestToken(SEConstants.TAIL_CREATE, params, "", listener);
    }

    public void reconnectToken(SEBaseParams params, String loginSecret, FetchListener listener) {
        requestToken(SEConstants.TAIL_RECONNECT, params, loginSecret, listener);
    }

    public void refreshToken(SEBaseParams params, String loginSecret, FetchListener listener) {
        requestToken(SEConstants.TAIL_REFRESH, params, loginSecret, listener);
    }

    private void requestToken(String tailUrl, SEBaseParams params, String loginSecret, FetchListener listener) {
        fetchListener = listener;
        sendPOSTRequest(SEConstants.TOKENS_URL.concat(tailUrl), params, loginSecret,
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

    public void listingProviders(FetchListener listener) {
        providerList(null, listener);
    }

    public void listingProvidersFromDate(Date date, FetchListener listener) {
        providerList(date, listener);
    }

    private void providerList(final Date date, FetchListener listener) {
        fetchListener = listener;
        HashMap<String, String> params = new HashMap<>();
        if (date != null) {
            params.put(SEConstants.KEY_FROM_DATE, SEDateTools.parseDateToShortString(date));
        }
        if (providerArray == null || providerArray.isEmpty()) {
            providerArray = new ArrayList<>();
        }
        sendGETRequest(SEConstants.PROVIDERS_URL.concat(nextPageId), params, "",
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
                    providerArray.clear();
                    onSuccess(providerArray);
                } else {
                    providerList(date, fetchListener);
                }
            }
        }));
    }

/**
 * Logins
 * */
    public void fetchLogin(String loginSecret, FetchListener listener) {
        fetchListener = listener;
        sendGETRequest(SEConstants.LOGIN_URL, null, loginSecret,
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

    public void deleteLogin(String loginSecret, FetchListener listener) {
        fetchListener = listener;
        sendDELETERequest(SEConstants.LOGIN_URL, loginSecret,
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

    public void listingAccountsWithParams(String loginSecret, int loginId, FetchListener listener) {
        accountsList(loginSecret, loginId, listener);
    }

    public void listingAccounts(String loginSecret, FetchListener listener) {
        accountsList(loginSecret, 0, listener);
    }

    public void accountsList(final String loginSecret, final int loginId, FetchListener listener) {
        fetchListener = listener;
        HashMap<String, String> params = new HashMap<>();
        if (loginId != 0) {
            params.put(SEConstants.KEY_FROM_DATE, String.valueOf(loginId));
        }
        if (accountsArray == null || accountsArray.isEmpty()) {
            accountsArray = new ArrayList<>();
        }
        sendGETRequest(SEConstants.ACCOUNTS_URL.concat(nextPageId), params, loginSecret,
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
                    accountsArray.clear();
                    onSuccess(accountsArray);
                } else {
                    accountsList(loginSecret, loginId, fetchListener);
                }
            }
        }));
    }

/**
 * Transactions
 * */
    public void listingTransactionsOfAccount(String loginSecret,
                                             int accountId,
                                             FetchListener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put(SEConstants.KEY_ACCOUNT_ID, String.valueOf(accountId));
        listtingTransactions(loginSecret, params, listener);
    }

    public void listingPendingTransactionsOfAccount(String loginSecret,
                                                    int accountId,
                                                    FetchListener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put(SEConstants.KEY_ACCOUNT_ID, String.valueOf(accountId));
        listtingPendingTransactions(loginSecret, params, listener);
    }

    public void listtingTransactions(String loginSecret,
                                     final HashMap<String, String> params,
                                     FetchListener listener) {
        fetchTransactions(loginSecret, params, SEConstants.TRANSACTIONS_URL, listener);
    }

    public void listtingPendingTransactions(String loginSecret,
                                            final HashMap<String, String> params,
                                            FetchListener listener) {
        fetchTransactions(loginSecret, params, SEConstants.PENDING_TRANSACTIONS_URL, listener);
    }

    public void fetchTransactions(final String loginSecret,
                                  final HashMap<String, String> params,
                                  final String url,
                                  FetchListener listener) {
        fetchListener = listener;
        if (transactionsArray == null || transactionsArray.isEmpty()) {
            transactionsArray = new ArrayList<>();
        }
        sendGETRequest(url.concat(nextPageId), params, loginSecret,
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
                    transactionsArray.clear();
                    onSuccess(transactionsArray);
                } else {
                    fetchTransactions(loginSecret, params, url, fetchListener);
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
                                    SEHTTPResponseHandler handler) {
        HashMap<String, String> headers = headers();
        if (!loginSecret.isEmpty()){
            headers = loginSecretHeaders(loginSecret);
        }
        return SERestClient.post(servicePath, params.toJson(), handler, headers);
    }

    private boolean sendGETRequest(String servicePath,
                                   HashMap<String, String> params,
                                   String loginSecret,
                                   SEHTTPResponseHandler handler) {
        HashMap<String, String> headers = headers();
        String url = servicePath;
        if (!loginSecret.isEmpty()){
            headers = loginSecretHeaders(loginSecret);
        }
        if (params != null) {
            url = url.concat(SETools.paramsToString(params));
        }
        Log.v("tag", "url " + url);
        return SERestClient.get(url, handler, headers);
    }

    private boolean sendDELETERequest(String servicePath,
                                      String loginSecret,
                                      SEHTTPResponseHandler handler) {
        HashMap<String, String> headers = headers();
        if (!loginSecret.isEmpty()){
            headers = loginSecretHeaders(loginSecret);
        }
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

    private HashMap<String, String> headers() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(SEConstants.KEY_HEADER_APP_SECRET, SaltEdgeSDK.getInstance().getAppSecret());
        headers.put(SEConstants.KEY_HEADER_CLIENT_ID, SaltEdgeSDK.getInstance().getClientId());
        return headers;
    }

    private HashMap<String, String> loginSecretHeaders(String loginSecret) {
        HashMap<String, String> loginSecretHeaders = headers();
        loginSecretHeaders.put(SEConstants.KEY_HEADER_LOGIN_SECRET, loginSecret);
        return loginSecretHeaders;
    }

}
