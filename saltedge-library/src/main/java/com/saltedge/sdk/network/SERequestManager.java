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
package com.saltedge.sdk.network;

import android.text.TextUtils;

import com.saltedge.sdk.connector.AccountsConnector;
import com.saltedge.sdk.connector.CustomerConnector;
import com.saltedge.sdk.connector.DeleteLoginConnector;
import com.saltedge.sdk.connector.LoginsShowConnector;
import com.saltedge.sdk.connector.ProvidersConnector;
import com.saltedge.sdk.connector.TokenConnector;
import com.saltedge.sdk.connector.TransactionsConnector;
import com.saltedge.sdk.interfaces.CreateCustomerResult;
import com.saltedge.sdk.interfaces.DeleteLoginResult;
import com.saltedge.sdk.interfaces.FetchAccountsResult;
import com.saltedge.sdk.interfaces.FetchLoginsResult;
import com.saltedge.sdk.interfaces.FetchTransactionsResult;
import com.saltedge.sdk.interfaces.ProvidersResult;
import com.saltedge.sdk.interfaces.RefreshLoginResult;
import com.saltedge.sdk.interfaces.TokenConnectionResult;
import com.saltedge.sdk.model.LoginData;
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
     * Result is returned through callback.
     *
     * @param countryCode - code of the provider’s country
     * @param callback - callback for request result
     */
    public void fetchProviders(String countryCode, ProvidersResult callback) {
        new ProvidersConnector(callback).fetchProviders(countryCode);
    }

    /**
     * Allows to create new customer.
     * Result is returned through callback.
     *
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
     * Result is returned through callback.
     *
     * @param providerCode - the code of the desired provider
     * @param scopes - fetching mode, possible values: ['accounts'], ['holder_info'], ['accounts', 'holder_info'], ['accounts', 'transactions'], ['accounts', 'holder_info', 'transactions']
     * @param returnTo - the URL the user will be redirected to
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void createToken(String providerCode,
                            String[] scopes,
                            String returnTo,
                            String customerSecret,
                            TokenConnectionResult callback) {
        new TokenConnector(callback).createToken(providerCode, scopes, returnTo, customerSecret);
    }

    /**
     * Allows you to create a token, which will be used to access Salt Edge Connect for login creation.
     * Result is returned through callback.
     *
     * @param dataMap - custom params map
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void createToken(Map<String, Object> dataMap,
                            String customerSecret,
                            TokenConnectionResult callback) {
        new TokenConnector(callback).createToken(dataMap, customerSecret);
    }

    /**
     * Allows you to create a token, which will be used to access Salt Edge Connect for login reconnect.
     * Result is returned through callback.
     *
     * @param localeCode - the language of the Salt Edge Connect page in the ISO 639-1 format.
     * @param returnTo - the URL the user will be redirected to
     * @param loginSecret - secret of the login which you want to reconnect
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void reconnectToken(String localeCode,
                               String returnTo,
                               String loginSecret,
                               String customerSecret,
                               TokenConnectionResult callback) {
        new TokenConnector(callback).reconnectToken(localeCode, returnTo, loginSecret,
                customerSecret, false);
    }

    /**
     * Allows you to create a token, which will be used to access Salt Edge Connect for login reconnect.
     * Result is returned through callback.
     *
     * @param localeCode - the language of the Salt Edge Connect page in the ISO 639-1 format.
     * @param returnTo - the URL the user will be redirected to
     * @param loginSecret - secret of the login which you want to reconnect
     * @param customerSecret - current customer secret code
     * @param overrideCredentials - override credentials strategy. If true, the new credentials will automatically override the old ones.
     * @param callback - callback for request result
     */
    public void reconnectToken(String localeCode,
                               String returnTo,
                               String loginSecret,
                               String customerSecret,
                               boolean overrideCredentials,
                               TokenConnectionResult callback) {
        new TokenConnector(callback).reconnectToken(localeCode, returnTo, loginSecret,
                customerSecret, overrideCredentials);
    }

    public void refreshToken(String localeCode, String returnTo, String loginSecret, String customerSecret,
                             TokenConnectionResult callback) {
        new TokenConnector(callback).refreshToken(localeCode, returnTo, loginSecret, customerSecret);
    }

    public SERefreshService refreshLoginWithSecret(String customerSecret,
                                                   LoginData loginData,
                                                   String[] refreshScopes,
                                                   RefreshLoginResult callback) {
        return new SERefreshService(callback).startRefresh(customerSecret, loginData, refreshScopes);
    }

    /**
     * Returns a single login object.
     * Result is returned through callback.
     *
     * @param loginSecret - secret of the login which should be returned if exist
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void fetchLogin(String customerSecret,
                           String loginSecret,
                           FetchLoginsResult callback) {
        String[] loginSecrets = { loginSecret };
        fetchLogins(customerSecret, loginSecrets, callback);
    }

    /**
     * Returns login objects collection.
     * Result is returned through callback.
     *
     * @param loginSecretsArray - array of secrets of the logins which should be returned if exist
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void fetchLogins(String customerSecret,
                            String[] loginSecretsArray,
                            FetchLoginsResult callback) {
        new LoginsShowConnector(callback).fetchLogins(customerSecret, loginSecretsArray);
    }

    /**
     * Delete login.
     * Result is returned through callback.
     *
     * @param loginSecret - secret of the login which should be deleted if exist
     * @param customerSecret - current customer secret code
     * @param callback - callback for request result
     */
    public void deleteLogin(String customerSecret,
                            String loginSecret,
                            DeleteLoginResult callback) {
        new DeleteLoginConnector(callback).deleteLogin(customerSecret, loginSecret);
    }

    /**
     * Return the list of accounts of a login.
     * Result is returned through callback.
     *
     * The accounts are sorted in ascending order of their ID, so the newest accounts will come last.
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param callback - callback for request result
     */
    public void fetchAccounts(String customerSecret,
                              String loginSecret,
                              FetchAccountsResult callback) {
        new AccountsConnector(callback).fetchAccounts(customerSecret, loginSecret);
    }

    /**
     * Return the list of all transactions for an account.
     * Result is returned through callback.
     *
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param callback - callback for request result
     */
    public void fetchAllTransactions(String customerSecret,
                                     String loginSecret,
                                     String accountId,
                                     FetchTransactionsResult callback) {
        fetchTransactions(customerSecret, loginSecret, accountId, "", false, true, callback);
    }

    /**
     * Return the list of all transactions for an account from transaction id.
     * Result is returned through callback.
     *
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param fromTransactionId - the id from which the result list should starts
     * @param callback - callback for request result
     */
    public void fetchAllTransactions(String customerSecret,
                                     String loginSecret,
                                     String accountId,
                                     String fromTransactionId,
                                     FetchTransactionsResult callback) {
        fetchTransactions(customerSecret, loginSecret, accountId, fromTransactionId, false, true, callback);
    }

    /**
     * Return the page of transactions for an account from transaction id.
     * Transaction page contains maximum 100 items.
     * Result is returned through callback.
     *
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param fromTransactionId - the ID from which the next page of transactions starts
     * @param callback - callback for request result
     */
    public void fetchTransactions(String customerSecret,
                                  String loginSecret,
                                  String accountId,
                                  String fromTransactionId,
                                  FetchTransactionsResult callback) {
        fetchTransactions(customerSecret, loginSecret, accountId, fromTransactionId, false, false, callback);
    }

    /**
     * Return the list of transactions for an account.
     * Initiate transactions fetch by params.
     * Result is returned through callback.
     *
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param fromTransactionId - the ID from which the next page of transactions starts
     * @param fetchPendingTransactions - flag which indicates what type of transactions should be fetched (Normal or Pending)
     * @param fetchAllTransactionsFromId - flag which indicates that should be fetched one page or all
     * @param callback - callback for request result
     */
    public void fetchTransactions(String customerSecret,
                                  String loginSecret,
                                  String accountId,
                                  String fromTransactionId,
                                  boolean fetchPendingTransactions,
                                  boolean fetchAllTransactionsFromId,
                                  FetchTransactionsResult callback) {
        new TransactionsConnector(callback).fetchTransactions(
                customerSecret,
                loginSecret,
                accountId,
                fromTransactionId,
                fetchPendingTransactions,
                fetchAllTransactionsFromId);
    }

    /**
     * Return the list of all transactions for an account.
     * Result is returned through callback.
     * Method is duplicating fetchAllTransactions(...)
     *
     * @deprecated  Replaced by {@link #fetchAllTransactions}
     *
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param callback - callback for request result
     */
    @Deprecated
    public void fetchTransactionsOfAccount(String customerSecret,
                                           String loginSecret,
                                           String accountId,
                                           FetchTransactionsResult callback) {
        fetchAllTransactions(customerSecret, loginSecret, accountId, callback);
    }

    /**
     * Return the list of all transactions for an account from transaction id.
     * Result is returned through callback.
     *
     * @deprecated  Replaced by {@link #fetchTransactions}
     *
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param fromTransactionId - the id from which the result list should starts
     * @param callback - callback for request result
     */
    @Deprecated
    public void fetchTransactionsOfAccount(String customerSecret,
                                           String loginSecret,
                                           String accountId,
                                           String fromTransactionId,
                                           FetchTransactionsResult callback) {
        fetchTransactions(customerSecret, loginSecret, accountId, fromTransactionId, false, true, callback);
    }

    /**
     * Return the list of all pending transactions for an account.
     * Result is returned through callback.
     *
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param callback - callback for request result
     */
    public void fetchPendingTransactionsOfAccount(String customerSecret,
                                                  String loginSecret,
                                                  String accountId,
                                                  FetchTransactionsResult callback) {
        fetchPendingTransactionsOfAccount(customerSecret, loginSecret, accountId, "", callback);
    }

    /**
     * Return the list of all pending transactions for an account.
     * Result is returned through callback.
     *
     * @param customerSecret - current customer secret code
     * @param loginSecret - secret of the login
     * @param accountId - account ID
     * @param fromTransactionId - the id from which the result list should starts
     * @param callback - callback for request result
     */
    public void fetchPendingTransactionsOfAccount(String customerSecret,
                                                  String loginSecret,
                                                  String accountId,
                                                  String fromTransactionId,
                                                  FetchTransactionsResult callback) {
        fetchTransactions(customerSecret, loginSecret, accountId, fromTransactionId, true, true, callback);
    }
}
