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

import com.saltedge.sdk.model.request.CreateCustomerRequest;
import com.saltedge.sdk.model.request.CreateTokenRequest;
import com.saltedge.sdk.model.request.MappedRequest;
import com.saltedge.sdk.model.request.PutLoginCredentialsRequest;
import com.saltedge.sdk.model.request.RefreshLoginRequest;
import com.saltedge.sdk.model.request.TokenRequest;
import com.saltedge.sdk.model.response.AccountsResponse;
import com.saltedge.sdk.model.response.CreateCustomerResponse;
import com.saltedge.sdk.model.response.CreateTokenResponse;
import com.saltedge.sdk.model.response.DeleteLoginResponse;
import com.saltedge.sdk.model.response.LoginResponse;
import com.saltedge.sdk.model.response.ProvidersResponse;
import com.saltedge.sdk.model.response.TransactionsResponse;
import com.saltedge.sdk.utils.SEConstants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    @HEAD
    Call<Void> getPins(@Url String url);

    @POST(SEApiConstants.API_CUSTOMERS_PATH)
    Call<CreateCustomerResponse> createCustomer(@Body CreateCustomerRequest body);

    @POST(SEApiConstants.API_TOKEN_CREATE_PATH)
    Call<CreateTokenResponse> createToken(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                          @Body CreateTokenRequest body);

    @POST(SEApiConstants.API_TOKEN_CREATE_PATH)
    Call<CreateTokenResponse> createToken(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                          @Body MappedRequest body);

    @POST(SEApiConstants.API_TOKEN_RECONNECT_PATH)
    Call<CreateTokenResponse> reconnectToken(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                             @Header(SEApiConstants.KEY_HEADER_LOGIN_SECRET) String loginSecret,
                                             @Body TokenRequest body);

    @POST(SEApiConstants.API_TOKEN_REFRESH_PATH)
    Call<CreateTokenResponse> refreshToken(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                           @Header(SEApiConstants.KEY_HEADER_LOGIN_SECRET) String loginSecret,
                                           @Body TokenRequest body);

    @GET(SEApiConstants.API_PROVIDERS_PATH)
    Call<ProvidersResponse> getProviders(@Query(SEConstants.KEY_COUNTRY_CODE) String countryCode,
                                         @Query(SEConstants.KEY_INCLUDE_FAKE_PROVIDERS) boolean includeFakeProviders,
                                         @Query(SEConstants.KEY_FROM_ID) String fromId);

    @GET(SEApiConstants.API_LOGIN_PATH)
    Call<LoginResponse> showLogin(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                  @Header(SEApiConstants.KEY_HEADER_LOGIN_SECRET) String loginSecret);

    @PUT(SEApiConstants.API_LOGIN_REFRESH_PATH)
    Call<LoginResponse> refreshLogin(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                     @Header(SEApiConstants.KEY_HEADER_LOGIN_SECRET) String loginSecret,
                                     @Body RefreshLoginRequest body);

    @DELETE(SEApiConstants.API_LOGIN_PATH)
    Call<DeleteLoginResponse> deleteLogin(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                          @Header(SEApiConstants.KEY_HEADER_LOGIN_SECRET) String loginSecret);

    @PUT(SEApiConstants.API_LOGIN_INTERACTIVE_PATH)
    Call<LoginResponse> putInteractiveCredentials(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                                  @Header(SEApiConstants.KEY_HEADER_LOGIN_SECRET) String loginSecret,
                                                  @Body PutLoginCredentialsRequest body);

    @GET(SEApiConstants.API_ACCOUNTS_PATH)
    Call<AccountsResponse> getAccounts(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                       @Header(SEApiConstants.KEY_HEADER_LOGIN_SECRET) String loginSecret,
                                       @Query(SEConstants.KEY_FROM_ID) String fromId);

    @GET(SEApiConstants.API_TRANSACTIONS_PATH)
    Call<TransactionsResponse> getTransactions(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                               @Header(SEApiConstants.KEY_HEADER_LOGIN_SECRET) String loginSecret,
                                               @Query(SEConstants.KEY_ACCOUNT_ID) String accountId,
                                               @Query(SEConstants.KEY_FROM_ID) String fromId);

    @GET(SEApiConstants.API_PENDING_TRANSACTIONS_PATH)
    Call<TransactionsResponse> getPendingTransactions(@Header(SEApiConstants.KEY_HEADER_CUSTOMER_SECRET) String customerSecret,
                                                      @Header(SEApiConstants.KEY_HEADER_LOGIN_SECRET) String loginSecret,
                                                      @Query(SEConstants.KEY_ACCOUNT_ID) String accountId,
                                                      @Query(SEConstants.KEY_FROM_ID) String fromId);
}
