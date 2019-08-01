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

import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.connector.AccountsConnector;
import com.saltedge.sdk.connector.ConnectSessionConnector;
import com.saltedge.sdk.connector.ConnectionDeleteConnector;
import com.saltedge.sdk.connector.ConnectionsConnector;
import com.saltedge.sdk.connector.ConsentRevokeConnector;
import com.saltedge.sdk.connector.ConsentsConnector;
import com.saltedge.sdk.connector.CurrenciesConnector;
import com.saltedge.sdk.connector.CustomerConnector;
import com.saltedge.sdk.connector.DuplicatedTransactionsFetchConnector;
import com.saltedge.sdk.connector.LeadSessionConnector;
import com.saltedge.sdk.connector.PartnerConsentsConnector;
import com.saltedge.sdk.connector.PendingTransactionsFetchConnector;
import com.saltedge.sdk.connector.ProvidersConnector;
import com.saltedge.sdk.connector.TransactionsConnector;
import com.saltedge.sdk.connector.TransactionsUpdateConnector;
import com.saltedge.sdk.errors.PartnerSDKFeatureException;
import com.saltedge.sdk.errors.SDKFeatureException;
import com.saltedge.sdk.interfaces.ConnectSessionResult;
import com.saltedge.sdk.interfaces.CreateCustomerResult;
import com.saltedge.sdk.interfaces.DeleteEntryResult;
import com.saltedge.sdk.interfaces.FetchAccountsResult;
import com.saltedge.sdk.interfaces.FetchConnectionsResult;
import com.saltedge.sdk.interfaces.FetchConsentsResult;
import com.saltedge.sdk.interfaces.FetchCurrenciesResult;
import com.saltedge.sdk.interfaces.FetchTransactionsResult;
import com.saltedge.sdk.interfaces.ProvidersResult;
import com.saltedge.sdk.interfaces.RefreshConnectionResult;
import com.saltedge.sdk.interfaces.UpdateTransactionsResult;
import com.saltedge.sdk.model.SEConnection;
import com.saltedge.sdk.utils.SEConstants;

import org.jetbrains.annotations.NotNull;

import java.util.List;
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
     * Returns Currencies Rates objects collection for a desired date.
     *
     * Result is returned through callback (`onFetchCurrenciesSuccess(List<SECurrency> list)`).
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret [optional] - current Customer secret code
     * @param ratesDate - date for which collection of currencies rates should be returned
     * @param callback - callback for request result
     */
    public void fetchCurrenciesRates(
            String customerSecret,
            String ratesDate,
            FetchCurrenciesResult callback
    ) {
        new CurrenciesConnector(callback).fetchCurrenciesRates(customerSecret, ratesDate);
    }

    /**
     * Allows to create new Customer.
     *
     * Result is returned through callback (`onCreateCustomerSuccess(String customerSecret)`).
     *
     * Feature is not available for Partner Application.
     *
     * @param customerIdentifier - a unique identifier of the new Customer
     * @param callback - callback for request result
     */
    public void createCustomer(
            @NotNull String customerIdentifier,
            CreateCustomerResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        if (TextUtils.isEmpty(customerIdentifier)) {
            throw new RuntimeException(SEConstants.ERROR_CUSTOMER_IDENTIFIER_IS_NULL);
        }
        new CustomerConnector(callback).createCustomer(customerIdentifier);
    }

    /**
     * Return list of available Providers.
     * Result is returned through callback.
     *
     * @param countryCode - code of the provider’s country
     * @param callback - callback for request result
     */
    public void fetchProviders(@NotNull String countryCode, ProvidersResult callback) {
        new ProvidersConnector(callback).fetchProviders(countryCode);
    }

    /**
     * Allows you to create a connect session, which will be used to access Salt Edge Connect for Connection creation.
     * You will receive a connect_url, which allows you to enter directly to Salt Edge Connect with your newly generated connect session.
     * Result is returned through callback.
     *
     * Feature is not available for Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param providerCode - the code of the desired Provider
     * @param consentScopes - fetching mode, possible values: ['holder_information], ['account_details'], ['transactions_details'] or combinations
     * @param localeCode - the language of the Salt Edge Connect page in the ISO 639-1 format.
     * @param returnToUrl - the URL the user will be redirected to. The return_to URL should not exceed 2040 characters.
     * @param callback - callback for request result
     */
    public void createConnectSession(
            @NotNull String customerSecret,
            String providerCode,
            String[] consentScopes,
            String localeCode,
            @NotNull String returnToUrl,
            ConnectSessionResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        new ConnectSessionConnector(callback).createConnectSession(
                customerSecret,
                providerCode,
                consentScopes,
                localeCode,
                returnToUrl
        );
    }

    /**
     * Allows you to create a connect session, which will be used to access Salt Edge Connect for Connection creation.
     * You will receive a connect_url, which allows you to enter directly to Salt Edge Connect with your newly generated connect session.
     * Result is returned through callback.
     *
     * Feature is not available for Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param dataMap - custom params map
     * @param callback - callback for request result
     */
    public void createConnectSession(
            @NotNull String customerSecret,
            Map<String, Object> dataMap,
            ConnectSessionResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        new ConnectSessionConnector(callback).createConnectSession(customerSecret, dataMap);
    }

    /**
     * Allows you to create a connect session, which will be used to access Salt Edge Connect to reconnect a connection.
     * You will receive a connect_url, which allows you to enter directly to Salt Edge Connect with your newly generated connect session.
     * Result is returned through callback.
     *
     * Feature is not available for Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret of the Connection which you want to reconnect
     * @param consentScopes - fetching mode, possible values: ['holder_information], ['account_details'], ['transactions_details'] or combinations
     * @param localeCode - the language of the Salt Edge Connect page in the ISO 639-1 format.
     * @param returnToUrl - the URL the user will be redirected to. The return_to URL should not exceed 2040 characters.
     * @param callback - callback for request result
     */
    public void createReconnectSession(
            @NotNull String customerSecret,
            String connectionSecret,
            String[] consentScopes,
            String localeCode,
            @NotNull String returnToUrl,
            ConnectSessionResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        new ConnectSessionConnector(callback).createReconnectSession(
                customerSecret,
                connectionSecret,
                consentScopes,
                localeCode,
                returnToUrl,
                false
        );
    }

    /**
     * Allows you to create a connect session, which will be used to access Salt Edge Connect to reconnect a connection.
     * You will receive a connect_url, which allows you to enter directly to Salt Edge Connect with your newly generated connect session.
     * Result is returned through callback.
     *
     * Feature is not available for Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret of the Connection which you want to reconnect
     * @param consentScopes - fetching mode, possible values: ['holder_information], ['account_details'], ['transactions_details'] or combinations
     * @param localeCode - the language of the Salt Edge Connect page in the ISO 639-1 format.
     * @param returnToUrl - the URL the user will be redirected to. The return_to URL should not exceed 2040 characters.
     * @param overrideCredentials - override credentials strategy. If true, the new credentials will automatically override the old ones.
     * @param callback - callback for request result
     */
    public void createReconnectSession(
            @NotNull String customerSecret,
            String connectionSecret,
            String[] consentScopes,
            String localeCode,
            @NotNull String returnToUrl,
            boolean overrideCredentials,
            ConnectSessionResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        new ConnectSessionConnector(callback).createReconnectSession(
                customerSecret,
                connectionSecret,
                consentScopes,
                localeCode,
                returnToUrl,
                overrideCredentials
        );
    }

    /**
     * Allows you to create a connect session, which will be used to access Salt Edge Connect to refresh a connection.
     * You will receive a connect_url, which allows you to enter directly to Salt Edge Connect with your newly generated connect session.
     * Result is returned through callback.
     *
     * Feature is not available for Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret code of the Connection which you want to reconnect
     * @param localeCode - the language of the Salt Edge Connect page in the ISO 639-1 format.
     * @param returnToUrl - the URL the user will be redirected to. The return_to URL should not exceed 2040 characters.
     * @param callback - callback for request result
     */
    public void createRefreshSession(
            @NotNull String customerSecret,
            String connectionSecret,
            String localeCode,
            @NotNull String returnToUrl,
            ConnectSessionResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        new ConnectSessionConnector(callback).createRefreshSession(
                customerSecret,
                connectionSecret,
                localeCode,
                returnToUrl
        );
    }

    /**
     * Created SERefreshService and starts refresh through Salt Edge API
     * Result is returned through callback.
     *
     * Feature is not available for Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionData - Connection data for refresh
     * @param refreshScopes - array of refresh scopes, e.g. {"accounts", "transactions"}
     * @param callback - callback for request result
     * @return SERefreshService - refresh service object
     */
    public SERefreshService refreshConnectionWithSecret(
            @NotNull String customerSecret,
            SEConnection connectionData,
            String[] refreshScopes,
            RefreshConnectionResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        return new SERefreshService(callback).startRefresh(customerSecret, connectionData, refreshScopes);
    }

    /**
     * Allows you to create a lead session, which will be used to access Salt Edge Connect for Connection creation.
     * You will receive a redirect_url, which allows you to enter directly to Salt Edge Connect with your newly generated lead session.
     * Result is returned through callback.
     *
     * Feature is available only for Partner Application.
     *
     * @param providerCode - the code of the desired Provider
     * @param consentScopes - fetching mode, possible values: ['holder_information], ['account_details'], ['transactions_details'] or combinations
     * @param localeCode - the language of the Salt Edge Connect page in the ISO 639-1 format.
     * @param returnToUrl - the URL the user will be redirected to. The return_to URL should not exceed 2040 characters.
     * @param callback - callback for request result
     */
    public void createLeadSession(
            String providerCode,
            @NotNull String[] consentScopes,
            String localeCode,
            @NotNull String returnToUrl,
            ConnectSessionResult callback
    ) {
        featureIsAvailableForPartnerOnly();
        new LeadSessionConnector(callback).createLeadSession(
                providerCode,
                consentScopes,
                localeCode,
                returnToUrl
        );
    }

    /**
     * Allows you to create a lead session, which will be used to access Salt Edge Connect for Connection creation.
     * You will receive a redirect_url, which allows you to enter directly to Salt Edge Connect with your newly generated lead session.
     * Result is returned through callback.
     *
     * Feature is available only for Partner Application.
     *
     * @param dataMap - custom params map
     * @param callback - callback for request result
     */
    public void createLeadSession(
            Map<String, Object> dataMap,
            ConnectSessionResult callback
    ) {
        featureIsAvailableForPartnerOnly();
        new LeadSessionConnector(callback).createLeadSession(dataMap);
    }

    /**
     * Returns a single Connection object.
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret code of the Connection which should be returned if exist
     * @param callback - callback for request result
     */
    public void fetchConnection(
            String customerSecret,
            @NotNull String connectionSecret,
            FetchConnectionsResult callback
    ) {
        String[] secrets = { connectionSecret };
        fetchConnections(customerSecret, secrets, callback);
    }

    /**
     * Returns Connections objects collection.
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionsSecretsArray - array of secrets of the Connections which should be returned if exist
     * @param callback - callback for request result
     */
    public void fetchConnections(
            String customerSecret,
            @NotNull String[] connectionsSecretsArray,
            FetchConnectionsResult callback) {
        new ConnectionsConnector(callback).fetchConnections(customerSecret, connectionsSecretsArray);
    }

    /**
     * Delete Connection.
     * Result is returned through callback.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret code of the Connection which should be deleted if exist
     * @param callback - callback for request result
     *
     * Feature is not available for Partners Applications.
     */
    public void deleteConnection(
            @NotNull String customerSecret,
            @NotNull String connectionSecret,
            DeleteEntryResult callback
    ) throws PartnerSDKFeatureException {
        featureIsAvailableForNonPartnerOnly();
        new ConnectionDeleteConnector(callback).deleteConnection(customerSecret, connectionSecret);
    }

    /**
     * You can see the list of accounts of a Connection.
     * The accounts are sorted in ascending order of their ID, so the newest accounts will come last.
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret code of the Connection
     * @param callback - callback for request result
     */
    public void fetchAccounts(
            String customerSecret,
            @NotNull String connectionSecret,
            FetchAccountsResult callback
    ) {
        new AccountsConnector(callback).fetchAccounts(customerSecret, connectionSecret);
    }

    /**
     * Return the list of all non duplicated  transactions for an Account of a Connection.
     * The list not includes pending transactions
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret code of the Connection
     * @param accountId - Account ID
     * @param callback - callback for request result
     */
    public void fetchAllTransactions(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull String accountId,
            FetchTransactionsResult callback
    ) {
        fetchTransactions(
                customerSecret,
                connectionSecret,
                accountId,
                "",
                false,
                true,
                callback
        );
    }

    /**
     * Return the list of all transactions from transactionId for an Account of a Connection.
     * The list not includes pending transactions
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret code of the Connection
     * @param accountId - Account ID
     * @param fromTransactionId - the id from which the result list should starts
     * @param callback - callback for request result
     */
    public void fetchAllTransactions(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull String accountId,
            @NotNull String fromTransactionId,
            FetchTransactionsResult callback
    ) {
        fetchTransactions(
                customerSecret,
                connectionSecret,
                accountId,
                fromTransactionId,
                false,
                true,
                callback
        );
    }

    /**
     * Return the single page of transactions from transactionId for an Account of a Connection.
     * Transaction page contains maximum 100 items.
     * The list not includes pending transactions
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret code of the Connection
     * @param accountId - Account ID
     * @param fromTransactionId - the ID from which the next page of transactions starts
     * @param callback - callback for request result
     */
    public void fetchTransactions(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull String accountId,
            @NotNull String fromTransactionId,
            FetchTransactionsResult callback
    ) {
        fetchTransactions(customerSecret,
                connectionSecret,
                accountId,
                fromTransactionId,
                false,
                false,
                callback);
    }

    /**
     * Return the list of transactions for an Account of a Connection.
     * Initiate transactions fetch by params.
     * The list includes posted or pending transactions.
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret code of the Connection
     * @param accountId - Account ID
     * @param fromTransactionId - the ID from which the next page of transactions starts
     * @param fetchPendingTransactions - flag which indicates what type of transactions should be fetched (Normal or Pending)
     * @param fetchAllTransactionsFromId - flag which indicates that should be fetched one page or all
     * @param callback - callback for request result
     */
    public void fetchTransactions(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull String accountId,
            @NotNull String fromTransactionId,
            boolean fetchPendingTransactions,
            boolean fetchAllTransactionsFromId,
            FetchTransactionsResult callback
    ) {
        if (fetchPendingTransactions) {
            new PendingTransactionsFetchConnector(callback).fetchTransactions(
                    customerSecret,
                    connectionSecret,
                    accountId,
                    fromTransactionId,
                    fetchAllTransactionsFromId
            );
        } else {
            new TransactionsConnector(callback).fetchTransactions(
                    customerSecret,
                    connectionSecret,
                    accountId,
                    fromTransactionId,
                    fetchAllTransactionsFromId
            );
        }
    }

    /**
     * Return the list of all pending transactions for an Account of a Connection.
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret of the Connection
     * @param accountId - Account ID
     * @param callback - callback for request result
     */
    public void fetchPendingTransactionsOfAccount(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull String accountId,
            FetchTransactionsResult callback
    ) {
        fetchPendingTransactionsOfAccount(
                customerSecret,
                connectionSecret,
                accountId,
                "",
                callback);
    }

    /**
     * Return the list of all pending transactions from transactionId for an Account of a Connection.
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret of the Connection
     * @param accountId - Account ID
     * @param fromTransactionId - the id from which the result list should starts
     * @param callback - callback for request result
     */
    public void fetchPendingTransactionsOfAccount(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull String accountId,
            @NotNull String fromTransactionId,
            FetchTransactionsResult callback
    ) {
        new PendingTransactionsFetchConnector(callback).fetchTransactions(
                customerSecret,
                connectionSecret,
                accountId,
                fromTransactionId,
                true
        );
    }

    /**
     * Return the list of all duplicated transactions for an Account of a Connection.
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret of the Connection
     * @param accountId - Account ID
     * @param callback - callback for request result
     */
    public void fetchDuplicatedTransactionsOfAccount(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull String accountId,
            FetchTransactionsResult callback
    ) {
        fetchDuplicatedTransactionsOfAccount(
                customerSecret,
                connectionSecret,
                accountId,
                "",
                callback);
    }

    /**
     * Return the list of all duplicated transactions from transactionId for an Account of a Connection.
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret of the Connection
     * @param accountId - Account ID
     * @param fromTransactionId - the id from which the result list should starts
     * @param callback - callback for request result
     */
    public void fetchDuplicatedTransactionsOfAccount(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull String accountId,
            @NotNull String fromTransactionId,
            FetchTransactionsResult callback
    ) {
        new DuplicatedTransactionsFetchConnector(callback).fetchTransactions(
                customerSecret,
                connectionSecret,
                accountId,
                fromTransactionId,
                true
        );
    }

    /**
     * Mark a list of transactions as duplicated.
     * Result is returned through callback (`onUpdateTransactionsSuccess(Boolean success, "DUPLICATE")`)
     *
     * Feature is not available for Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret of the Connection
     * @param transactionsIds - the ids of transactions, that should be marked as duplicated
     * @param callback - callback for request result
     */
    public void markTransactionsAsDuplicated(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull List<String> transactionsIds,
            UpdateTransactionsResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        new TransactionsUpdateConnector(callback).markTransactionsAsDuplicated(
                customerSecret,
                connectionSecret,
                transactionsIds
        );
    }

    /**
     * Remove duplicated flag from a list of transactions.
     * Result is returned through callback (`onUpdateTransactionsSuccess(Boolean success, "UNDUPLICATE")`)
     *
     * Feature is not available for Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret of the Connection
     * @param transactionsIds - the ids of transactions, that should be marked as not-duplicated
     * @param callback - callback for request result
     */
    public void markTransactionsAsNotDuplicated(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull List<String> transactionsIds,
            UpdateTransactionsResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        new TransactionsUpdateConnector(callback).markTransactionsAsNotDuplicated(
                customerSecret,
                connectionSecret,
                transactionsIds
        );
    }

    /**
     * Remove transactions older than a specified period.
     * Result is returned through callback (`onTransactionsCleanupStartedSuccess(Boolean success)`)
     *
     * Feature is not available for Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret of the Connection
     * @param accountId - the id of the account
     * @param keepDays - the amount of days for which the transactions will be kept. Transactions older than that will be irreversibly removed. Value should be greater than or equal to 60.
     * @param callback - callback for request result
     */
    public void removeTransactions(
            String customerSecret,
            @NotNull String connectionSecret,
            @NotNull String accountId,
            @NotNull int keepDays,
            UpdateTransactionsResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        new TransactionsUpdateConnector(callback).removeTransactions(
                customerSecret,
                connectionSecret,
                accountId,
                keepDays
        );
    }

    /**
     * You can see the list of consents of Connection.
     * The consents are sorted in ascending order of their ID, so the newest consents will come last.
     *
     * Result is returned through callback.
     *
     * Feature is available for Partners and non-Partner Applications.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret code of the Connection
     * @param callback - callback for request result
     */
    public void fetchConsents(
            String customerSecret,
            @NotNull String connectionSecret,
            FetchConsentsResult callback
    ) {
        new ConsentsConnector(callback).fetchConsents(customerSecret, connectionSecret);
    }

    /**
     * Consent revoke is an option that allows you to revoke a consent.
     * Result is returned through callback.
     *
     * Feature is not available for Partner Application.
     *
     * @param customerSecret - current Customer secret code
     * @param connectionSecret - secret code of the Connection which should be deleted if exist
     * @param consentId - id of Consent which should be revoked
     * @param callback - callback for request result
     */
    public void revokeConsent(
            @NotNull String customerSecret,
            @NotNull String connectionSecret,
            @NotNull String consentId,
            DeleteEntryResult callback
    ) {
        featureIsAvailableForNonPartnerOnly();
        new ConsentRevokeConnector(callback).revokeConsent(customerSecret, connectionSecret, consentId);
    }

    /**
     * You can see the list of partners consents of Connection.
     * The consents are sorted in ascending order of their ID, so the newest consent will come last.
     * Result is returned through callback.
     *
     * Feature is not available for Partner Application.
     *
     * @param connectionSecret - secret code of the Connection
     * @param callback - callback for request result
     */
    public void fetchPartnerConsents(
            @NotNull String connectionSecret,
            FetchConsentsResult callback
    ) {
        featureIsAvailableForPartnerOnly();
        new PartnerConsentsConnector(callback).fetchPartnerConsents(connectionSecret);
    }

    private void featureIsAvailableForPartnerOnly() {
        if (SaltEdgeSDK.isNotPartner()) throw new SDKFeatureException();
    }

    private void featureIsAvailableForNonPartnerOnly() {
        if (SaltEdgeSDK.isPartner()) throw new PartnerSDKFeatureException();
    }
}
