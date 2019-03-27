# SaltEdge Android SDK & Sample application

A handful of classes to help you interact with the Salt Edge API from your Android app.

**Warning!** The SDK doesn't work without Client ID and App Secret!

*Note*: You can find your Client ID and App Secret on your [profile](https://www.saltedge.com/keys_and_secrets) page.

## Requirements

* Android 4.0.2+ (minimum 15 sdk version) with Play Services installed
* Account on saltedge.com

## Source

Clone this repository

`$ git clone git@github.com:saltedge/saltedge-android.git`

## Setting up the `build.gradle`

Add Java 8 support  
```groovy
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```
  
Add maven repository    
```groovy
repositories {
    maven {
        url 'https://raw.github.com/saltedge/saltedge-android/master/repo/'
    }
}
```

Add SDK dependency  
```groovy
implementation 'com.saltedge.sdk:saltedge-library:2.0.0@aar'
```

Saltedge SDK library require extra dependencies:
```groovy
implementation 'com.google.android.gms:play-services-safetynet:16.0.0'//Security utils
implementation 'com.squareup.retrofit2:retrofit:2.5.0'//Network communication
implementation 'com.squareup.retrofit2:converter-gson:2.5.0'//model serializer/deserializer
implementation 'com.squareup.okhttp3:logging-interceptor:3.12.2'//Network communication logging
```

## Using the Sample application

1. Replace the `clientAppId`, `clientAppSecret` constants in [StartActivity:43-44](https://github.com/saltedge/saltedge-android/blob/master/app/src/main/java/com/saltedge/sdk/sample/features/StartActivity.java) with your Client ID and corresponding App secret.
2. Add the `customerIdentifier` (that uniquely identifies a customer) to [StartActivity.java:42](https://github.com/saltedge/saltedge-android/blob/master/app/src/main/java/com/saltedge/sdk/sample/features/StartActivity.java)
3. Run the app

*Note: You can find your Client ID and App secret on your [profile](https://www.saltedge.com/keys_and_secrets) page.*

## Library Usage

### Init application 

Open Android Studio and create a new app.  
Add module (Android Studio -> File -> Import Module -> find module named "saltedge-library" -> Import) 
or add external dependency (`implementation 'com.saltedge.sdk:saltedge-library:x.x.x@aar'`)

### Init SDK.  
Before any usage of SDK you should provide clientAppId and clientAppSecret.  
```java
    SaltEdgeSDK.getInstance().init(applicationContext, clientAppId, clientAppSecret);
```

or provide clientAppId and clientAppSecret and set http logging is enabled or not
```java
    SaltEdgeSDK.getInstance().init(applicationContext, clientAppId, clientAppSecret, loggingIsEnabled);
```
  
### Create customer
You should create customer and get customer secret token.   
[Documentation](https://docs.saltedge.com/account_information/v5/#customers-create).  
Params: 
* customerIdentifier - Random string which identify user on current app instance  

```java
    private void createCustomer() {
        String customerIdentifier = "customerIdentifier";
        SERequestManager.getInstance().createCustomer(customerIdentifier, new CreateCustomerResult() {
            @Override
            public void onSuccess(String secret) {
                //save customer secret
            }
            
            @Override
            public void onFailure(String errorResponse) {
                //process error
            }
        });
    }
```

### Get list of available providers
[Documentation](https://docs.saltedge.com/account_information/v5/#providers-list):

```java
    public static void fetchProviders() {
        String countryCode = "XX";//prefered country code or null
        SERequestManager.getInstance().fetchProviders(countryCode, new ProvidersConnector.Result() {
        
            @Override
            public void onSuccess(ArrayList<SEProvider> providersList) {
                //use providers list
            }

            @Override
            public void onFailure(String errorResponse) {
                //process error
            }
        });
    }
```
### Initiate Provider Connection 
Once we have the Customer Secret, we can create a connection using Salt Edge Connect, which already implemented the interface in a fast and secure way
Params:
* customerSecret
* provider code (SEProvider)
* localeCode (by default "en")
* returnUrl - the custom URL the user will be redirected to on connect finish
  
```java
     private void createConnectSession(String customerSecret, SEProvider selectedProvider, String localeCode, String returnUrl) {
         String providerCode = selectedProvider.getCode();
         String[] CONSENT_SCOPES = { SEConsent.SCOPE_ACCOUNT_DETAILS, SEConsent.SCOPE_TRANSACTIONS_DETAILS };
         SERequestManager.getInstance().createConnectSession(
                 customerSecret,
                 providerCode,
                 CONSENT_SCOPES,
                 localeCode,
                 returnUrl,
                 new ConnectSessionResult() {
                     @Override
                     public void onSuccess(String connectUrl) {
                         // connectUrl - This is the url you should visit to create the connection.
                     }
            
                     @Override
                     public void onFailure(String errorMessage) {
                         //process error
                     }
                 });
    }
```
or call [createConnectSession(...)](https://docs.saltedge.com/account_information/v5/#connect_sessions-create) with custom map of params
```java
     private void createConnectSession(String customerSecret, SEProvider selectedProvider, String localeCode, String returnUrl) {
         HashMap<String, Object> dataMap = new HashMap<>();
         String providerCode = selectedProvider.getCode();
         dataMap.put("provider_code", providerCode);
         ...
         dataMap.put("return_to", "custom return url");
         SERequestManager.getInstance().createConnectSession(
                 customerSecret, 
                 dataMap,
                 new ConnectSessionResult() {
                     @Override
                     public void onSuccess(String connectUrl) {
                         // connectUrl - This is the url you should visit to create the connection.
                     }
    
                     @Override
                     public void onFailure(String errorMessage) {
                         //process error
                     }
                 });
    }
```

### Show Salt Edge Connect Web Page
Using the returned URL, you can let the user import their data. After doing this, you will obtain a `connectionSecret`.

```java   
    private void goToURL(String connectUrl, String returnUrl, View view) {
        WebView webView = (WebView) view.findViewById(R.id.webView);
        SEWebViewTools.getInstance().initializeWithUrl(activityContext, webView, connectUrl, returnUrl, 
            new SEWebViewTools.WebViewRedirectListener() {
                @Override
                public void onConnectionSecretFetchSuccess(String responseStatus, String connectionId, String connectionSecret) {
                    //connection finished sucessfully. save connectionId and connectionSecret.
                }
    
                @Override
                public void onConnectionSecretFetchError(String errorResponse) {
                    //connection finished with error. Show the error. 
                }
                
                @Override
                public void onConnectionRefreshSuccess() {
                    //Connection model updated due to some actions 
                }
                
                @Override
                public void onLoginFetchingStage(String connectionId, String connectionSecret) {
                    //connection is on FETCH stage. save connectionId and connectionSecret for connection process resume.
                }
            });
    }
```

### Get Connection status
`connectionSecret` can be used to get the status of the Connection, its accounts and transactions.

```java
    SERequestManager.getInstance().fetchConnection(
            customerSecret, 
            connectionSecret, 
            new FetchConnectionsResult() {
                @Override
                public void onSuccess(List<SEConnection> connections) {
                    //show connections
                }

                @Override
                public void onFailure(String errorResponse) {
                    //process error
                }
            });
```                

### Get Accounts of Connection
You can now get all the accounts of the login using the `connectionSecret`.
    
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
                    //process error
                }
            });
```

### Get Transactions of Account of Connection
For each accounts, you can obtain the list of all transactions.

```java
        SERequestManager.getInstance().fetchAllTransactions(
                customerSecret, 
                connectionSecret, 
                accountId,
                new FetchTransactionsResult() {
                    @Override
                    public void onSuccess(ArrayList<SETransaction> transactions) {
                        //show transactions
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        //process error
                    }
                });
```

For each accounts, you can obtain the list of all new transactions from specified ID.

```java
        SERequestManager.getInstance().fetchAllTransactions(
                customerSecret, 
                connectionSecret, 
                accountId, 
                fromId,
                new FetchTransactionsResult() {
                    @Override
                    public void onSuccess(ArrayList<SETransaction> transactions) {
                        //show transactions
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        //process error
                    }
                });
```        

For each accounts, you can obtain the list of all pending transactions.

```java
        SERequestManager.getInstance().fetchPendingTransactionsOfAccount(
                customerSecret, 
                connectionSecret, 
                accountId,
                new FetchTransactionsResult() {
                    @Override
                    public void onSuccess(ArrayList<SETransaction> transactions) {
                        //show transactions
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        //process error
                    }
                });
```

### Refresh data of Connection
You can refresh the data for a connection. To refresh, you should obtain a connectUrl of Salt Edge Connect.

```java
        SERequestManager.getInstance().createRefreshSession(
                customerSecret,
                connectionSecret,
                localeCode, 
                returnUrl,  
                new ConnectSessionResult() {
                    @Override
                    public void onSuccess(String connectUrl) {
                        // here is a URL you can use to redirect the user
                    }
        
                    @Override
                    public void onFailure(String errorMessage) {
                        //process error
                    }
                });
```

### Reconnect Connection    
You can reconnect the connection. You should obtain a connectUrl of Salt Edge Connect.
```java    
        SERequestManager.getInstance().createReconnectSession(
                customerSecret,
                connectionSecret,
                consentScopes, 
                localeCode, 
                callbackUrl,  
                new ConnectSessionResult() {
                    @Override
                    public void onSuccess(String connectUrl) {
                        // here is a URL you can use to redirect the user
                    }
        
                    @Override
                    public void onFailure(String errorMessage) {
                        //process error
                    }
                });
```           

### Remove Connection   
```java    
        SERequestManager.getInstance().deleteConnection(
                customerSecret, 
                connectionSecret,
                new DeleteConnectionResult() {
                    @Override
                    public void onSuccess(Boolean isRemoved) {
                        //show changes
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        //process error
                    }
                });
```   

### Refresh Connection through Connect API
If you wish to refresh Connection without Salt Edge Connect Form, you can special service `SERefreshService`.

#### Start refresh
* connectionData - SEConnection object
* consentScopes - refreshing scopes array (e.g. { SEConsent.SCOPE_ACCOUNT_DETAILS, SEConsent.SCOPE_TRANSACTIONS_DETAILS } )
```java
    SERefreshService refreshService = SERequestManager.getInstance().refreshConnectionWithSecret(
            customerSecret, 
            connectionData, 
            consentScoopes, 
            refreshResultCallback);
```  

#### Canceling refresh due to Android components life cycle changes
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

[StageData](https://docs.saltedge.com/account_information/v5/#attempts-stages) contains interactive fields name and options which should be used for credentials input  
  
#### [Sending interactive credentials](https://docs.saltedge.com/account_information/v5/#logins-interactive)
```java
    refreshService.sendInteractiveData(credentials);
```
Credentials is `Map<String, Object>` type with content like `"interactive field name from StageData": "input value"`.

**Saltedge Connect API requires high security standards for data handling on client’s side. This method is only available for the certified and/or selected partners. For more information, feel free to [contact us](https://www.saltedge.com/pages/contact)  
Please consult the Demo app to see how it works**

## Models

There are some provided models for serializing the objects received in the API responses. These represent the providers, logins, accounts, transactions, provider fields and their options. Whenever you request a resource that returns one of these types, they will always get serialized into Java classes. (For instance, the `listingTransactionsOfAccount(...)` method has a `ArrayList<>` containing `SETransaction` instances in its success callback.)
Base models contained within the components:

* `SEProvider`
* `SEProvider`
* `SEAccount`
* `SETransaction`
* `SEConsent`
* `SEAttempt`
* `SEStage`

For a supplementary description of the models listed above that is not included in the sources' docs, feel free to visit the [API Reference](https://docs.saltedge.com/account_information/v5/).

## Utilities

A few utility classes are bundled within the components, and are used internally, but you could also use them if you find that necessary.

## Changelog

## License

See the LICENSE file.

## References

1. [Salt Edge Connect Guide](https://docs.saltedge.com/account_information/v5/#salt_edge_connect)
2. [Salt Edge API Reference](https://docs.saltedge.com/account_information/v5/)
