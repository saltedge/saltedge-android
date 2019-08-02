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

import com.saltedge.sdk.model.request.ConnectSessionRequest;
import com.saltedge.sdk.model.request.CreateCustomerRequest;
import com.saltedge.sdk.model.request.DeleteTransactionsRequest;
import com.saltedge.sdk.model.request.MappedRequest;
import com.saltedge.sdk.model.request.PutConnectionCredentialsRequest;
import com.saltedge.sdk.model.request.PutTransactionsIdsRequest;
import com.saltedge.sdk.model.request.RefreshConnectionRequest;
import com.saltedge.sdk.model.response.AccountsResponse;
import com.saltedge.sdk.model.response.ConnectSessionResponse;
import com.saltedge.sdk.model.response.ConnectionResponse;
import com.saltedge.sdk.model.response.ConsentResponse;
import com.saltedge.sdk.model.response.ConsentsResponse;
import com.saltedge.sdk.model.response.CreateCustomerResponse;
import com.saltedge.sdk.model.response.CurrenciesResponse;
import com.saltedge.sdk.model.response.DeleteConnectionResponse;
import com.saltedge.sdk.model.response.LeadSessionResponse;
import com.saltedge.sdk.model.response.ProvidersResponse;
import com.saltedge.sdk.model.response.TransactionsResponse;
import com.saltedge.sdk.model.response.UpdateTransactionsResponse;
import com.saltedge.sdk.utils.SEConstants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SEApiInterface {

    @HEAD(SEApiConstants.API_COUNTRIES_PATH)
    Call<Void> getPins();

    @GET(SEApiConstants.API_RATES_PATH)
    Call<CurrenciesResponse> getCurrencies(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Query(SEConstants.KEY_FROM_DATE) String date
    );

    @POST(SEApiConstants.API_CUSTOMERS_PATH)
    Call<CreateCustomerResponse> createCustomer(@Body CreateCustomerRequest body);

    @GET(SEApiConstants.API_PROVIDERS_PATH)
    Call<ProvidersResponse> getProviders(
            @Query(SEConstants.KEY_COUNTRY_CODE) String countryCode,
            @Query(SEConstants.KEY_INCLUDE_FAKE_PROVIDERS) boolean includeFakeProviders,
            @Query(SEConstants.KEY_FROM_ID) String fromId
    );

    @POST(SEApiConstants.API_CONNECT_SESSION_CREATE_PATH)
    Call<ConnectSessionResponse> createConnectSession(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Body ConnectSessionRequest body
    );

    @POST(SEApiConstants.API_CONNECT_SESSION_CREATE_PATH)
    Call<ConnectSessionResponse> createConnectSession(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Body MappedRequest body
    );

    @POST(SEApiConstants.API_CONNECT_SESSION_RECONNECT_PATH)
    Call<ConnectSessionResponse> createReconnectSession(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Body ConnectSessionRequest body
    );

    @POST(SEApiConstants.API_CONNECT_SESSION_REFRESH_PATH)
    Call<ConnectSessionResponse> createRefreshSession(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Body ConnectSessionRequest body
    );

    @POST(SEApiConstants.API_LEAD_SESSION_CREATE_PATH)
    Call<LeadSessionResponse> createLeadSession(@Body ConnectSessionRequest body);

    @POST(SEApiConstants.API_LEAD_SESSION_CREATE_PATH)
    Call<LeadSessionResponse> createLeadSession(@Body MappedRequest body);

    @GET(SEApiConstants.API_CONNECTION_PATH)
    Call<ConnectionResponse> showConnection(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret
    );

    @PUT(SEApiConstants.API_CONNECTION_REFRESH_PATH)
    Call<ConnectionResponse> refreshConnection(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Body RefreshConnectionRequest body
    );

    @DELETE(SEApiConstants.API_CONNECTION_PATH)
    Call<DeleteConnectionResponse> deleteConnection(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret
    );

    @PUT(SEApiConstants.API_CONNECTION_INTERACTIVE_PATH)
    Call<ConnectionResponse> putInteractiveCredentials(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Body PutConnectionCredentialsRequest body
    );

    @GET(SEApiConstants.API_ACCOUNTS_PATH)
    Call<AccountsResponse> getAccounts(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Query(SEConstants.KEY_FROM_ID) String fromId
    );

    @GET(SEApiConstants.API_TRANSACTIONS_PATH)
    Call<TransactionsResponse> getTransactions(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Query(SEConstants.KEY_ACCOUNT_ID) String accountId,
            @Query(SEConstants.KEY_FROM_ID) String fromId
    );

    @GET(SEApiConstants.API_PENDING_TRANSACTIONS_PATH)
    Call<TransactionsResponse> getPendingTransactions(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Query(SEConstants.KEY_ACCOUNT_ID) String accountId,
            @Query(SEConstants.KEY_FROM_ID) String fromId
    );

    @GET(SEApiConstants.API_DUPLICATED_TRANSACTIONS_PATH)
    Call<TransactionsResponse> getDuplicatedTransactions(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Query(SEConstants.KEY_ACCOUNT_ID) String accountId,
            @Query(SEConstants.KEY_FROM_ID) String fromId
    );

    @PUT(SEApiConstants.API_DUPLICATE_TRANSACTIONS_PATH)
    Call<UpdateTransactionsResponse> putDuplicatedTransactions(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Body PutTransactionsIdsRequest body
    );

    @PUT(SEApiConstants.API_UNDUPLICATE_TRANSACTIONS_PATH)
    Call<UpdateTransactionsResponse> putUnduplicatedTransactions(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Body PutTransactionsIdsRequest body
    );

    @DELETE(SEApiConstants.API_TRANSACTIONS_PATH)
    Call<UpdateTransactionsResponse> deleteTransactions(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Body DeleteTransactionsRequest body
    );

    @GET(SEApiConstants.API_CONSENTS_PATH)
    Call<ConsentsResponse> getConsents(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Query(SEConstants.KEY_FROM_ID) String fromId
    );

    @GET(SEApiConstants.API_PARTNER_CONSENTS_PATH)
    Call<ConsentsResponse> getPartnerConsents(
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Query(SEConstants.KEY_FROM_ID) String fromId
    );

    @PUT(SEApiConstants.API_CONSENT_REVOKE_PATH)
    Call<ConsentResponse> revokeConsent(
            @Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
            @Header(SEApiConstants.KEY_HEADER_CONNECTION_SECRET) String connectionSecret,
            @Path(value = SEConstants.KEY_CONSENT_ID, encoded = true) String consentId
    );
}
