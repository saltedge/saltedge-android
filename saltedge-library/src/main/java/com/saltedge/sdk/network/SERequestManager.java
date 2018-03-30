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

public class SERequestManager {

    private static SERequestManager instance;

    public static SERequestManager getInstance() {
        if (instance == null) {
            instance = new SERequestManager();
        }
        return instance;
    }

    /**
     *  Customers
     * */
    public void createCustomer(String customerIdentifier, CreateCustomerResult callback) {
        if (customerIdentifier == null || TextUtils.isEmpty(customerIdentifier)) {
            throw new RuntimeException(SEConstants.KEY_SECRET.concat(" " + SEConstants.CANNOT_BE_NULL));
        }
        new CustomerConnector(callback).createCustomer(customerIdentifier);
    }

    /**
     * Tokens
     * */
    public void createToken(String providerCode, String[] scopes, String returnTo, String customerSecret, TokenConnectionResult callback) {
        new TokenConnector(callback).createToken(providerCode, scopes, returnTo, customerSecret);
    }

    public void createToken(Map<String, Object> dataMap, String customerSecret, TokenConnectionResult callback) {
        new TokenConnector(callback).createToken(dataMap, customerSecret);
    }

    public void reconnectToken(String localeCode, String returnTo, String loginSecret, String customerSecret, TokenConnectionResult callback) {
        new TokenConnector(callback).reconnectToken(localeCode, returnTo, loginSecret, customerSecret);
    }

    public void refreshToken(String localeCode, String returnTo, String loginSecret, String customerSecret, TokenConnectionResult callback) {
        new TokenConnector(callback).refreshToken(localeCode, returnTo, loginSecret, customerSecret);
    }

    /**
     * Provider list
     * */
    public void fetchProviders(String countryCode, ProvidersResult callback) {
        new ProvidersConnector(callback).fetchProviders(countryCode);
    }

    /**
     * Logins
     * */
    public void fetchLogin(String loginSecret, String customerSecret, FetchLoginsResult callback) {
        String[] secrets = { loginSecret };
        fetchLogins(secrets, customerSecret, callback);
    }

    public void fetchLogins(String[] loginSecretsArray, String customerSecret, FetchLoginsResult callback) {
        new LoginConnector(callback).fetchLogins(loginSecretsArray, customerSecret);
    }

    public void deleteLogin(String loginSecret, String customerSecret, DeleteLoginResult callback) {
        new DeleteLoginConnector(callback).deleteLogin(loginSecret, customerSecret);
    }

    /**
     * Accounts
     * */
    public void fetchAccounts(String customerSecret, String loginSecret, FetchAccountsResult callback) {
        new AccountsConnector(callback).fetchAccounts(customerSecret, loginSecret);
    }

    /**
     * Transactions
     * */
    public void fetchTransactionsOfAccount(String customerSecret, String loginSecret, String accountId,
                                           FetchTransactionsResult callback) {
        new TransactionsConnector(callback).fetchTransactions(customerSecret, loginSecret, accountId);
    }
}
