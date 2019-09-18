# Salt Edge Android SDK Guide
  
* [Init SDK](#init-sdk)
* [Customer](#create-new-customer)
* [Providers](#fetch-list-of-available-providers)
* [Connections](#connections)
  * [Create Connect Session](#create-connect-session)
  * [Create Lead session](#create-lead-session) ***(Only for Partners)***
  * [Salt Edge Connect](#salt-edge-connect)
  * [Show connections](#show-connections)
  * [Show connection](#show-connection)
  * [Remove Connection](#remove-connection)
  * [Reconnect Connection](#reconnect-connection)
  * [Refresh Connection](#refresh-connection)
  * [Refresh Connection through Connect API](#refresh-connection-through-connect-api)
* [Show Accounts](#show-accounts)
* [Transactions](#transactions)
  * [Show Transactions](#show-transactions)
  * [Show Pending Transactions](#show-pending-transactions)
  * [Show Duplicated Transactions](#show-duplicated-transactions)
  * [Mark transactions as duplicated](#mark-transactions-as-duplicated)
  * [Remove duplicated flag from transactions](#remove-duplicated-flag-from-transactions)
  * [Remove transactions](#remove-transactions)
* [Consent](#consent)
  * [Show Consents](#show-consents)
  * [Revoke Consent](#revoke-consent)
  * [Show Partners Consents](#show-partners-consents)
  * [Revoke Partner Consent](#revoke-partner-consent)
* [Models](#models)
* [Utilities](#utilities)
---
  
## Init SDK  

Before any usage of SDK you should provide set clientAppId and clientAppSecret.

#### For initializing access to **Salt Edge API** use:

```java
SaltEdgeSDK.getInstance().init(applicationContext, clientAppId, clientAppSecret, returnToUrl, loggingIsEnabled);
```

#### For initializing access to **Salt Edge Partners API** use:

```java
SaltEdgeSDK.getInstance().initPartner(applicationContext, clientAppId, clientAppSecret, returnToUrl, loggingIsEnabled);
```
  
#### Params:

* `customerSecret` - unique customer secret;
* `selectedProvider` - provider code from `SEProvider`;
* `consentScopes` - application target scopes (e.g. `holder_information`, `account_details`, `transactions_details`);
* `localeCode` - application locale code (by default `en`);
* `returnToUrl` - the custom URL the user will be redirected to on connection finish.
  
---
## [Create new customer](https://docs.saltedge.com/account_information/v5/#customers-create).
**Note: Customer creation is not available for Partners API.**

A customer represents a single end-user of the Salt Edge API. The customer uses the API to create Connections, i.e. bank connections, that are further used to aggregate the customer’s financial data.  
You should create customer and receive customer's secret token.
    
#### Params:
 
* `customerIdentifier` - Random string which identify user on current app instance  .

#### Example:

```java
private void createCustomer() {
    String customerIdentifier = "customerIdentifier";
    SERequestManager.getInstance().createCustomer(customerIdentifier, new CreateCustomerResult() {
        @Override
        public void onSuccess(String secret) {
            //save customer's secret
        }
        
        @Override
        public void onFailure(String errorResponse) {
            //handle error
        }
    });
}
```

---
## [Fetch list of available providers](https://docs.saltedge.com/account_information/v5/#providers-list):

A provider is a source of financial data.  

#### Params:
 
* `countryCode` - preferred country code. `XF` - for Fake providers.

#### Example:

```java
public static void fetchProviders() {
    String countryCode = "XX";
    SERequestManager.getInstance().fetchProviders(countryCode, new ProvidersConnector.Result() {
    
        @Override
        public void onSuccess(ArrayList<SEProvider> providersList) {
            //use providers list
        }

        @Override
        public void onFailure(String errorResponse) {
            //handle error
        }
    });
}
```

---
## [Connections](https://docs.saltedge.com/account_information/v5/#connections)

A Connection represents a permanent connection of a specific customer to a bank. A single end-user represents a single customer in Salt Edge API. When creating a connection you should make sure the customer already exists to avoid creating a new customers with the same connection. Also, we don’t recommend creating multiple connections for the same customer with the same online banking access credentials.

For creating new Connection use
[Create Connect Session for non-Partners applications](#create-connect-session)
or
[Create Lead session for Partners applications](#create-lead-session) 
  
### [Create Connect Session](https://docs.saltedge.com/account_information/v5/#connect_sessions-create)
**Note: `Create Connect Session` feature is not available for Partners API.** 

Allows you to create a connect session, which will be used to access Salt Edge Connect for connection creation. You will receive a connect_url field, which allows you to enter directly to Salt Edge Connect with your newly generated connect session.

#### Params:

* `customerSecret` - unique customer secret;
* `selectedProvider` - provider code from `SEProvider`;
* `consentScopes` - application target scopes (e.g. `holder_information`, `account_details`, `transactions_details`);
* `localeCode` - application locale code (by default `en`);

#### Example:
  
```java
 private void createConnectSession(String customerSecret, SEProvider selectedProvider, String localeCode) {
     String providerCode = selectedProvider.getCode();
     String[] CONSENT_SCOPES = { SEConsent.SCOPE_ACCOUNT_DETAILS, SEConsent.SCOPE_TRANSACTIONS_DETAILS };
     SERequestManager.getInstance().createConnectSession(
             customerSecret,
             providerCode,
             CONSENT_SCOPES,
             localeCode,
             new ConnectSessionResult() {
                 @Override
                 public void onSuccess(String connectUrl) {
                     // connectUrl - This is the url you should visit to create the connection.
                 }
        
                 @Override
                 public void onFailure(String errorMessage) {
                     //handle error
                 }
             });
}
```
or call `createConnectSession(...)` with custom map of params

```java
 private void createConnectSession(String customerSecret, SEProvider selectedProvider, String localeCode, String returnToUrl) {
     HashMap<String, Object> dataMap = new HashMap<>();
     String providerCode = selectedProvider.getCode();
     dataMap.put("provider_code", providerCode);
     ...
     dataMap.put("return_to", returnToUrl);
     SERequestManager.getInstance().createConnectSession(
             customerSecret, 
             dataMap,
             new ConnectSessionResult() {
                 @Override
                 public void onSuccess(String connectUrl) {
                     // connectUrl - This is the url you should show in WebView to create the connection.
                 }

                 @Override
                 public void onFailure(String errorMessage) {
                     //handle error
                 }
             });
}
```

### [Create OAuth Provider Connect Session](https://docs.saltedge.com/account_information/v5/#oauth_providers-create)
**Note: `Create OAuth Connect Session` feature is not available for Partners API.** 

Used to create a connection for an OAuth provider. After receiving the response, the customer will be redirected to return_to URL.

#### Params:

* `customerSecret` - unique customer secret;
* `selectedProvider` - provider code from `SEProvider`;
* `consentScopes` - application target scopes (e.g. `holder_information`, `account_details`, `transactions_details`);
* `localeCode` - application locale code (by default `en`);

#### Example:
  
```java
 private void createOAuthConnectSession(String customerSecret, SEProvider selectedProvider, String localeCode) {
     String providerCode = selectedProvider.getCode();
     String[] CONSENT_SCOPES = { SEConsent.SCOPE_ACCOUNT_DETAILS, SEConsent.SCOPE_TRANSACTIONS_DETAILS };
     SERequestManager.getInstance().createOAuthConnectSession(
             customerSecret,
             providerCode,
             CONSENT_SCOPES,
             localeCode,
             new ConnectSessionResult() {
                 @Override
                 public void onSuccess(String connectUrl) {
                     // connectUrl - This is the url you should visit to create the connection.
                 }
        
                 @Override
                 public void onFailure(String errorMessage) {
                     //handle error
                 }
             });
}
```

### [Create Lead Session](https://docs.saltedge.com/partners/v1/#lead_sessions-create) 
**Note: `Create Lead Session` feature is available only for Partners API.**

Allows you to create a lead session for Partner application, which will be used to access Salt Edge Connect for connection creation. You will receive a connect_url field, which allows you to enter directly to Salt Edge Connect with your newly generated lead session.

#### Params:

* `selectedProvider` - provider code from `SEProvider`;
* `consentScopes` - application target scopes (e.g. `holder_information`, `account_details`, `transactions_details`);
* `localeCode` - application locale code (by default `en`);

#### Example:
  
```java
 private void createLeadSession(SEProvider selectedProvider, String localeCode) {
     String providerCode = selectedProvider.getCode();
     String[] CONSENT_SCOPES = { SEConsent.SCOPE_ACCOUNT_DETAILS, SEConsent.SCOPE_TRANSACTIONS_DETAILS };
     SERequestManager.getInstance().createLeadSession(
             providerCode,
             CONSENT_SCOPES,
             localeCode,
             new ConnectSessionResult() {
                 @Override
                 public void onSuccess(String connectUrl) {
                     // connectUrl - This is the url you should visit to create the connection.
                 }
        
                 @Override
                 public void onFailure(String errorMessage) {
                     //handle error
                 }
             });
}
```
or call `createLeadSession(...)` with custom map of params

```java
 private void createLeadSession(SEProvider selectedProvider, String localeCode, String returnToUrl) {
     HashMap<String, Object> dataMap = new HashMap<>();
     String providerCode = selectedProvider.getCode();
     dataMap.put("provider_code", providerCode);
     ...
     dataMap.put("return_to", returnToUrl);
     SERequestManager.getInstance().createConnectSession(
             dataMap,
             new ConnectSessionResult() {
                 @Override
                 public void onSuccess(String connectUrl) {
                     // connectUrl - This is the url you should show in WebView to create the connection.
                 }

                 @Override
                 public void onFailure(String errorMessage) {
                     //handle error
                 }
             });
}
```

## [Salt Edge Connect](https://docs.saltedge.com/account_information/v5/#salt_edge_connect)
 
After your application has received a connectUrl for connecting or reconnecting a connection, you can redirect your user to the Salt Edge Connect WebView. 
There, they will see a screen that lets them pick a country and a provider. Your user will also be asked to input their credentials and, if needed, any of the interactive credentials.
After doing this, you will obtain a `connectionSecret`. 
  
#### Params:

* `activityContext`
* `selectedProvider` - provider code from `SEProvider`;
* `consentScopes` - application target scopes (e.g. `holder_information`, `account_details`, `transactions_details`);
* `localeCode` - application locale code (by default `en`);

### Callback methods:

* `onConnectSessionSuccessStage()` - called when received SUCCESS stage callback of provider connect flow;
* `onConnectSessionErrorStage()` - called when received ERROR stage callback of provider connect flow;
* `onConnectSessionFetchingStage()` - called when received FETCHING stage callback of provider connect flow;
* `onConnectSessionStageChange()` - called when received new Stage callback of provider connect flow (not `SUCCESS`, not `ERROR`, not `FETCHING`);
* `onRedirectToReturnUrl()` - Connect session redirected to returnUrl. Session is redirected if connection is created or updated. Return URL can contains OAuth authorization query string or connection secret.

#### Example:

```java   
private void goToURL(String connectUrl, View view) {
    WebView webView = (WebView) view.findViewById(R.id.webView);
    SEWebViewTools.getInstance().initializeWithUrl(activityContext, webView, connectUrl, 
        new SEWebViewTools.WebViewRedirectListener() {
        
            @Override
            public void onConnectSessionSuccessStage(String connectionId, String connectionSecret, String rawJsonData) {
                //connection finished sucessfully. save connectionId and connectionSecret.
            }

            @Override
            public void onConnectSessionErrorStage(String rawJsonData) {
                //connection finished with error. Show the error. 
            }
            
            @Override
            public void onConnectSessionFetchingStage(String connectionId, String connectionSecret, String apiStage, String rawJsonData) {
                //connection is on fetching stage. save connectionId and connectionSecret for connection process resume.
            }
            
            @Override
            public void onConnectSessionStageChange(Saltbridge result, String rawJsonData) {
                //connection changed stage but it is not SUCCESS, ERROR or FETCHING stage.
            }
            
            @Override
            public void onRedirectToReturnUrl(String url) {
                //Connection model updated due to some actions 
            }
        }
    );
}
```
Notes:  
1. Possible `stage` values for each callback type are `fetching`, `success` and `error`.  
1. Parameter `api_stage` shows detailed information of the fetching process.  
1. Possible `api_stage` values are attempts stages: `start`, `connect`, `interactive`, `fetch_holder_info`, `fetch_accounts`, `fetch_recent`, `fetch_full`, `disconnect`, `finish`.  
  
### [Show Connections](https://docs.saltedge.com/account_information/v5/#connections-show)

Returns a collection of connections objects.

#### Params:

* `customerSecret` - **optional** unique customer secret. Not required for Partners API;
* `connectionsSecrets` - array of connections secrets which data should be fetched.

#### Example:

```java
SERequestManager.getInstance().fetchConnections(
        customerSecret, 
        connectionsSecrets, 
        new FetchConnectionsResult() {
            @Override
            public void onSuccess(List<SEConnection> connections) {
                //show connections
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```

### [Show Connection](https://docs.saltedge.com/account_information/v5/#connections-show)

Returns an array with single connection object.

#### Params:

* `customerSecret` - **optional** unique customer secret. Not required for Partners API;
* `connectionSecret` - secret of connection which data should be fetched.

#### Example:

```java
SERequestManager.getInstance().fetchConnection(
        customerSecret, 
        connectionSecret, 
        new FetchConnectionsResult() {
            @Override
            public void onSuccess(List<SEConnection> connections) {
                //show connection
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```

### [Remove Connection](https://docs.saltedge.com/account_information/v5/#connections-remove)
**Note: Connect Session is not available for Partners API.**

Removes a connection from our system and revokes the consent. All the associated accounts and transactions to that connection will be destroyed as well.

#### Params:

* `customerSecret` - unique customer secret;
* `connectionSecret` - secret of connection which data should be fetched.

#### Example:
   
```java    
SERequestManager.getInstance().deleteConnection(
        customerSecret, 
        connectionSecret,
        new DeleteEntryResult() {
            @Override
            public void onSuccess(Boolean isRemoved) {
                //show changes
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```

### [Reconnect Connection](https://docs.saltedge.com/account_information/v5/#connections-reconnect)
**Note: Reconnect Session is not available for Partners API.**

You can reconnect the connection. You should obtain a connectUrl of Salt Edge Connect.

#### Params:

* `customerSecret` - unique customer secret;
* `connectionSecret` - secret of connection which data should be fetched;
* `consentScopes` - application target scopes (e.g. `holder_information`, `account_details`, `transactions_details`;
* `localeCode` - application locale code (by default `en`);
* `returnToUrl` - the custom URL the user will be redirected to on connection finish.

#### Example:

```java    
SERequestManager.getInstance().createReconnectSession(
        customerSecret,
        connectionSecret,
        consentScopes, 
        localeCode, 
        returnToUrl,  
        new ConnectSessionResult() {
            @Override
            public void onSuccess(String connectUrl) {
                // here is a URL you can use to redirect the user
            }

            @Override
            public void onFailure(String errorMessage) {
                //handle error
            }
        }
);
```

### [Refresh Connection](https://docs.saltedge.com/account_information/v5/#connections-refresh)
**Note: Connect Session is not available for Partners API.**

You can refresh the data for a connection. To refresh, you should obtain a connectUrl of Salt Edge Connect.

#### Params:

* `customerSecret` - unique customer secret;
* `connectionSecret` - secret of connection which data should be fetched;
* `consentScopes` - application target scopes (e.g. `holder_information`, `account_details`, `transactions_details`);
* `localeCode` - application locale code (by default `en`);
* `returnToUrl` - the custom URL the user will be redirected to on connection finish.

#### Example:

```java
SERequestManager.getInstance().createRefreshSession(
        customerSecret,
        connectionSecret,
        localeCode, 
        returnToUrl,  
        new ConnectSessionResult() {
            @Override
            public void onSuccess(String connectUrl) {
                // here is a URL you can use to redirect the user
            }

            @Override
            public void onFailure(String errorMessage) {
                //handle error
            }
        }
);
```              

### [Refresh Connection through Connect API](https://docs.saltedge.com/account_information/v5/#connections-refresh)
**Note: Refresh thorough Connect API is not available for Partners API.**

If you wish to refresh Connection without Salt Edge Connect WebView, you can use dedicated service `SERefreshService`.

#### Start refresh

* `connectionData` - SEConnection object;
* `consentScopes` - refreshing scopes array (e.g. `[ SEConsent.SCOPE_ACCOUNT_DETAILS, SEConsent.SCOPE_TRANSACTIONS_DETAILS ]` ).

```java
SERefreshService refreshService = SERequestManager.getInstance().refreshConnectionWithSecret(
        customerSecret, 
        connectionData, 
        consentScoopes, 
        refreshResultCallback
);
```  

#### Cancel refresh due to Android components life cycle events

```java
refreshService.cancel();
```

#### SERefreshService result callback

```java
private RefreshConnectionResult callback = new RefreshConnectionResult() {

    @Override
    public void onRefreshSuccess() {
        //refresh finished with success
    }

    @Override
    public void onRefreshFailure(String errorMessage) {
        //refresh finished with error
    }

    @Override
    public void onConnectionStateFetchError(String errorMessage) {
        //login state updte finishe with error. you can ignore it
    }

    @Override
    public void provideInteractiveData(SEStage lastStage) {
        //This callback is called when the currently fetching login requires any interactive credentials for fetching.
        // call `refreshService.sendInteractiveData(credentials)` when credentials are ready
        
    }
}
```
  
#### [Send interactive credentials](https://docs.saltedge.com/account_information/v5/#logins-interactive)

```java
refreshService.sendInteractiveData(credentials);
```

Credentials data type is `Map<String, Object>`, with content like `{"interactive field name from StageData": "input value"}`.

**Saltedge Connect API requires high security standards for data handling on client’s side. This method is only available for the certified and/or selected partners. 
For more information, feel free to [contact us](https://www.saltedge.com/pages/contact)  
Please consult the Demo app to see how it works**
  
---
## [Show Accounts](https://docs.saltedge.com/account_information/v5/#accounts-list)

You can now get all the accounts of the login using the `connectionSecret`.

#### Params:

* `customerSecret` - **optional** unique customer secret. Not required for Partners API. 
* `connectionSecret` - secret of connection which data should be fetched.

#### Example:
    
```java   
SERequestManager.getInstance().fetchAccounts(
        customerSecret, 
        connectionSecret,
        new FetchAccountsResult() {
            @Override
            public void onSuccess(ArrayList<SEAccount> accounts) {
                //show accounts
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```

---
## [Transactions](https://docs.saltedge.com/account_information/v5/#transactions)

A transaction represents a movement of funds. Any transaction can represent a monetary transfer, withdrawal, income or expense interchange. Transactions are retained in a connection’s accounts, and are imported from one of the providers.

### [Show Transactions](https://docs.saltedge.com/account_information/v5/#transactions-list)

For each account of login, you can obtain the list of all posted transactions or you can obtain the list of all new transactions from specified ID.

#### Params:

* `customerSecret` - **optional** unique customer secret. Not required for Partners API;
* `connectionSecret` - secret of connection which data should be fetched;
* `accountId` - ID of account of connection which data should be fetched;
* `fromTransactionId` - ID of transaction from which should be collected result. Can be omitted.

#### Example:

```java
SERequestManager.getInstance().fetchAllTransactions(
        customerSecret, 
        connectionSecret, 
        accountId, 
        fromTransactionId,
        new FetchTransactionsResult() {
            @Override
            public void onSuccess(ArrayList<SETransaction> transactions) {
                //show transactions
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```        

### [Show Pending Transactions](https://docs.saltedge.com/account_information/v5/#transactions-pending).

For each accounts, you can obtain the list of all pending transactions.

#### Params:

* `customerSecret` - **optional** unique customer secret. Not required for Partners API;
* `connectionSecret` - secret of connection which data should be fetched;
* `accountId` - ID of account of connection which data should be fetched;
* `fromTransactionId` - ID of transaction from which should be collected result. Can be omitted.

#### Example:

```java
SERequestManager.getInstance().fetchPendingTransactionsOfAccount(
        customerSecret, 
        connectionSecret, 
        accountId,
        fromTransactionId,
        new FetchTransactionsResult() {
            @Override
            public void onSuccess(ArrayList<SETransaction> transactions) {
                //show transactions
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```

### [Show Duplicated Transactions](https://docs.saltedge.com/account_information/v5/#transactions-duplicates).

For each accounts, you can obtain the list of all duplicated transactions

#### Params:

* `customerSecret` - **optional** unique customer secret. Not required for Partners API;
* `connectionSecret` - secret of connection which data should be fetched;
* `accountId` - ID of account of connection which data should be fetched;
* `fromTransactionId` - ID of transaction from which should be collected result. Can be omitted.

#### Example:

```java
SERequestManager.getInstance().fetchDuplicatedTransactionsOfAccount(
        customerSecret, 
        connectionSecret, 
        accountId,
        fromTransactionId,
        new FetchTransactionsResult() {
            @Override
            public void onSuccess(ArrayList<SETransaction> transactions) {
                //show transactions
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```

### [Mark transactions as duplicated](https://docs.saltedge.com/account_information/v5/#transactions-duplicate).
**Note: `Revoke Consent` feature is not available for Partners API.**

Mark a list of transactions as `duplicated`.

#### Params:

* `customerSecret` - **optional** unique customer secret. Not required for Partners API;
* `connectionSecret` - secret of connection which data should be fetched;
* `transactionsIds` - collection of transactions IDs which should be marked as `duplicated`. 

#### Example:

```java
SERequestManager.getInstance().markTransactionsAsDuplicated(
        customerSecret, 
        connectionSecret,
        transactionsIds,
        new UpdateTransactionsResult() {
            @Override
            public void onUpdateTransactionsSuccess(Boolean success, String operationName) {
                //handle result. operationName - will be "DUPLICATE"
            }

            @Override
            public void onUpdateTransactionsFailure(String errorResponse) {
                //handle error
            }
        }
);
```

### [Remove duplicated flag from transactions](https://docs.saltedge.com/account_information/v5/#transactions-unduplicate).
**Note: `Revoke Consent` feature is not available for Partners API.**

Remove `duplicated` flag from a list of transactions.

#### Params:

* `customerSecret` - **optional** unique customer secret. Not required for Partners API;
* `connectionSecret` - secret of connection which data should be fetched;
* `transactionsIds` - collection of transactions IDs which should be cleared as `duplicated`. 

#### Example:

```java
SERequestManager.getInstance().markTransactionsAsNotDuplicated(
        customerSecret, 
        connectionSecret,
        transactionsIds,
        new UpdateTransactionsResult() {
            @Override
            public void onUpdateTransactionsSuccess(Boolean success, String operationName) {
                //handle result. operationName - will be "UNDUPLICATE"
            }

            @Override
            public void onUpdateTransactionsFailure(String errorResponse) {
                //handle error
            }
        }
);
```

### [Remove transactions](https://docs.saltedge.com/account_information/v5/#transactions-remove).
**Note: `Revoke Consent` feature is not available for Partners API.**

Remove transactions older than a specified period.

#### Params:

* `customerSecret` - **optional** unique customer secret. Not required for Partners API;
* `connectionSecret` - secret of connection which data should be fetched;
* `accountId` - the id of the account;
* `keepDays` - the amount of days for which the transactions will be kept. Transactions older than that will be irreversibly removed. Value should be greater than or equal to 60. 

#### Example:

```java
SERequestManager.getInstance().removeTransactions(
        customerSecret, 
        connectionSecret,
        accountId,
        int keepDays,
        new UpdateTransactionsResult() {
            @Override
            public void onTransactionsCleanupStartedSuccess(Boolean success) {
                //handle result
            }

            @Override
            public void onUpdateTransactionsFailure(String errorResponse) {
                //handle error
            }
        }
);
```

---
## [Consents](https://docs.saltedge.com/account_information/v5/#consents)

A consent represents the access to data and the limits of this access agreed on by a customer and is required to access the accounts, transactions and holder information through a specific provider.

The limits of a consent are represented by:
* data that is allowed to be accessed;
* period of time the data can be accessed for;
* interval of time to which the accessed data belongs to.

### [Show Consents](https://docs.saltedge.com/account_information/v5/#consents-list)

Returns all the consents accessible to your application for certain customer or connection.

#### Params:

* `customerSecret` - **optional** unique customer secret. Not required for Partners API;
* `connectionSecret` - secret of connection which data should be fetched.  

#### Example:
  
```java    
SERequestManager.getInstance().fetchConsents(
        customerSecret, 
        connectionSecret,
        new FetchConsentsResult() {
            @Override
            public void onSuccess(List<SEConsent> consents) {
                //show consents
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```

### [Revoke Consent](https://docs.saltedge.com/account_information/v5/#consents-revoke)
**Note: `Revoke Consent` feature is not available for Partners API.**

Consent revoke is an option that allows you to revoke a consent for a connection of customer.

#### Params:

* `customerSecret` - unique customer secret. Not required for Partners API; 
* `connectionSecret` - secret of connection which data should be fetched.
* `consentId` - unique ID of Consent which should be revoked

#### Example:
   
```java    
SERequestManager.getInstance().revokeConsent(
        customerSecret, 
        connectionSecret,
        consentId,
        new DeleteEntryResult() {
            @Override
            public void onSuccess(Boolean entryIsRemoved, String entryId) {
                //show changes
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```

### [Show Partners Consents](https://docs.saltedge.com/partners/v1/#partner_consents)
**Note: `Get Partners Consent` feature is available only for Partners API.**

Partner consent is a part of combined consent, shown to the customer that allows Salt Edge to share account, transaction and holder info data with the registered partner, based on the right to data portability enforced by the EU General Data Protection Regulation (GDPR)

#### Params:
 
* `connectionSecret` - secret of connection which data should be fetched.

#### Example:
  
```java    
SERequestManager.getInstance().fetchPartnerConsents(
        connectionSecret,
        new FetchConsentsResult() {
            @Override
            public void onSuccess(List<SEConsent> consents) {
                //show consents
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```

### [Revoke Partner Consent](https://docs.saltedge.com/partners/v1/#partner_consents-revoke)
**Note: `Revoke Partner Consent` feature is available only for Partners API.**

Partner Consent revoke is an option that allows you to revoke a consent for a connection of customer (connection).

#### Params:

* `connectionId` - unique ID of connection
* `consentId` - unique ID of Consent which should be revoked

#### Example:
   
```java    
SERequestManager.getInstance().revokePartnerConsent(
        connectionId,
        consentId,
        new DeleteEntryResult() {
            @Override
            public void onSuccess(Boolean entryIsRemoved, String entryId) {
                //show changes
            }

            @Override
            public void onFailure(String errorResponse) {
                //handle error
            }
        }
);
```

---
## Models

There are some provided models for serializing the objects received in the API responses. These represent the providers, logins, accounts, transactions, provider fields and their options. Whenever you request a resource that returns one of these types, they will always get serialized into Java classes. (For instance, the `listingTransactionsOfAccount(...)` method has a `ArrayList<>` containing `SETransaction` instances in its success callback.)

Base models contained within the components:

* [SEProvider](https://docs.saltedge.com/account_information/v5/#providers);
* [SECustomer](https://docs.saltedge.com/account_information/v5/#customers);
* [SEConnection](https://docs.saltedge.com/account_information/v5/#connections);
* [SEAccount](https://docs.saltedge.com/account_information/v5/#accounts);
* [SETransaction](https://docs.saltedge.com/account_information/v5/#transactions);
* [SEConsent](https://docs.saltedge.com/account_information/v5/#consents);
* [SEAttempt](https://docs.saltedge.com/account_information/v5/#attempts);
* [SEStage](https://docs.saltedge.com/account_information/v5/#attempts-stages).
 
Stage contains interactive fields name and options which should be used for credentials input.
Possible values of the name of the stage: `start`, `connect`, `interactive`, `fetch_holder_info`, `fetch_accounts`, `fetch_recent`, `fetch_full`, `disconnect`, `finish`.

For a supplementary description of the models listed above that is not included in the sources' docs, feel free to visit the [API Reference](https://docs.saltedge.com/account_information/v5/).

## Utilities

A few utility classes are bundled within the components, and are used internally, but you could also use them if you find that necessary.

---
Copyright © 2014 - 2019 Salt Edge Inc. https://www.saltedge.com
