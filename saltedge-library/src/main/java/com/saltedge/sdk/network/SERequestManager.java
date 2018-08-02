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
package com.saltedge.sdk.network;

import android.text.TextUtils;

import com.saltedge.sdk.connector.AccountsConnector;
import com.saltedge.sdk.connector.CustomerConnector;
import com.saltedge.sdk.connector.DeleteLoginConnector;
import com.saltedge.sdk.connector.LoginConnector;
import com.saltedge.sdk.connector.ProvidersConnector;
import com.saltedge.sdk.connector.TokenConnector;
import com.saltedge.sdk.connector.TransactionsConnector;
import com.saltedge.sdk.interfaces.CreateCustomerResult;
import com.saltedge.sdk.interfaces.DeleteLoginResult;
import com.saltedge.sdk.interfaces.FetchAccountsResult;
import com.saltedge.sdk.interfaces.FetchLoginsResult;
import com.saltedge.sdk.interfaces.FetchTransactionsResult;
import com.saltedge.sdk.interfaces.ProvidersResult;
import com.saltedge.sdk.interfaces.TokenConnectionResult;
import com.saltedge.sdk.utils.SEConstants;

import java.util.Map;

/**
 * Helper class for Saltedge SDK requests
 */
public class SERequestManager {

    private static SERequestManager instance;

    public static SERequestManager getInstance() {
        if (instance == null) {
            instance = new SERequestManager();
        }
        return instance;
    }

    /**
     * Return list of providers.
     * @param countryCode - code of the provider’s country
     * @param callback - callback for request result
     */
    public void fetchProviders(String countryCode, ProvidersResult callback) {
        new ProvidersConnector(callback).fetchProviders(countryCode);
    }

    /**
     * Allows to create new customer
     * @param customerIdentifier - a unique identifier of the new customer
     * @param callback - callback for request result
     */
    public void createCustomer(String customerIdentifier, CreateCustomerResult callback) {
        if (customerIdentifier == null || TextUtils.isEmpty(customerIdentifier)) {
            throw new RuntimeException(SEConstants.KEY_SECRET.concat(" " + SEConstants.CANNOT_BE_NULL));
        }
        new CustomerConnector(callback).createCustomer(customerIdentifier);
    }

    /**
     * Allows you to create a token, which will be used to access Salt Edge Connect for login creation.
     * @param providerCode - the code of the desired provider
     * @param scopes - fetching mode, possible values: ['accounts'], ['holder_info'], ['accounts', 'holder_info'], ['accounts', 'transactions'], ['accounts', 'holder_info', 'transactions']
     * @param returnTo - the URL the user will be redirected to
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void createToken(String providerCode, String[] scopes, String returnTo, String customerSecret,
                            TokenConnectionResult callback) {
        new TokenConnector(callback).createToken(providerCode, scopes, returnTo, customerSecret);
    }

    /**
     * Allows you to create a token, which will be used to access Salt Edge Connect for login creation.
     * @param dataMap - custom params map
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void createToken(Map<String, Object> dataMap, String customerSecret, TokenConnectionResult callback) {
        new TokenConnector(callback).createToken(dataMap, customerSecret);
    }

    /**
     * Allows you to create a token, which will be used to access Salt Edge Connect for login reconnect.
     * @param localeCode - the language of the Salt Edge Connect page in the ISO 639-1 format.
     * @param returnTo - the URL the user will be redirected to
     * @param loginSecret - secret of the login which you want to reconnect
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void reconnectToken(String localeCode, String returnTo, String loginSecret, String customerSecret,
                               TokenConnectionResult callback) {
        new TokenConnector(callback).reconnectToken(localeCode, returnTo, loginSecret,
                customerSecret, false);
    }

    /**
     * Allows you to create a token, which will be used to access Salt Edge Connect for login reconnect.
     * @param localeCode - the language of the Salt Edge Connect page in the ISO 639-1 format.
     * @param returnTo - the URL the user will be redirected to
     * @param loginSecret - secret of the login which you want to reconnect
     * @param customerSecret - current customer secret code
     * @param overrideCredentials - override credentials strategy. If true, the new credentials will automatically override the old ones.
     * @param callback - callback for request result
     */
    public void reconnectToken(String localeCode, String returnTo, String loginSecret, String customerSecret,
                               boolean overrideCredentials, TokenConnectionResult callback) {
        new TokenConnector(callback).reconnectToken(localeCode, returnTo, loginSecret,
                customerSecret, overrideCredentials);
    }

    public void refreshToken(String localeCode, String returnTo, String loginSecret, String customerSecret,
                             TokenConnectionResult callback) {
        new TokenConnector(callback).refreshToken(localeCode, returnTo, loginSecret, customerSecret);
    }

    /**
     * Returns a single login object.
     * @param loginSecret - secret of the login which should be returned if exist
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void fetchLogin(String loginSecret, String customerSecret, FetchLoginsResult callback) {
        String[] secrets = { loginSecret };
        fetchLogins(secrets, customerSecret, callback);
    }

    /**
     * Returns login objects collection.
     * @param loginSecretsArray - array of secrets of the logins which should be returned if exist
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void fetchLogins(String[] loginSecretsArray, String customerSecret, FetchLoginsResult callback) {
        new LoginConnector(callback).fetchLogins(loginSecretsArray, customerSecret);
    }

    /**
     * Delete login
     * @param loginSecret - secret of the login which should be deleted if exist
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void deleteLogin(String loginSecret, String customerSecret, DeleteLoginResult callback) {
        new DeleteLoginConnector(callback).deleteLogin(loginSecret, customerSecret);
    }

    /**
     * Return the list of accounts of a login.
     * The accounts are sorted in ascending order of their ID, so the newest accounts will come last.
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param callback - callback for request result
     */
    public void fetchAccounts(String customerSecret, String loginSecret, FetchAccountsResult callback) {
        new AccountsConnector(callback).fetchAccounts(customerSecret, loginSecret);
    }

    /**
     * Return the list of transactions of an account.
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param callback - callback for request result
     */
    public void fetchTransactionsOfAccount(String customerSecret, String loginSecret,
                                           String accountId, FetchTransactionsResult callback) {
        fetchTransactionsOfAccount(customerSecret, loginSecret, accountId, "", callback);
    }

    /**
     * Return the list of transactions of an account.
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param fromId - the id from which the next page of transactions starts
     * @param callback - callback for request result
     */
    public void fetchTransactionsOfAccount(String customerSecret, String loginSecret,
                                           String accountId, String fromId, FetchTransactionsResult callback) {
        new TransactionsConnector(callback).fetchTransactions(customerSecret, loginSecret, accountId, fromId, false);
    }

    /**
     * Return the list of pending transactions of an account.
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param callback - callback for request result
     */
    public void fetchPendingTransactionsOfAccount(String customerSecret, String loginSecret,
                                                  String accountId, FetchTransactionsResult callback) {
        fetchPendingTransactionsOfAccount(customerSecret, loginSecret, accountId, "", callback);
    }

    /**
     * Return the list of pending transactions of an account.
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param fromId - the id from which the next page of transactions starts
     * @param callback - callback for request result
     */
    public void fetchPendingTransactionsOfAccount(String customerSecret, String loginSecret,
                                                  String accountId, String fromId, FetchTransactionsResult callback) {
        new TransactionsConnector(callback).fetchTransactions(customerSecret, loginSecret, accountId, fromId, true);
    }
}
