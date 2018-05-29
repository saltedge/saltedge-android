## SaltEdge Android

A handful of classes to help you interact with the Salt Edge API from your Android app.

## Requirements

Android 4.0.2+ (minimum 15 sdk version)

### Source

Clone this repository

`$ git clone git@github.com:saltedge/saltedge-android.git`

## Connecting logins using the sample app

1. Replace the `clientAppId`, `clientAppSecret` constants in [StartActivity:43-44](https://github.com/saltedge/saltedge-android/blob/master/app/src/main/java/com/saltedge/sdk/sample/features/StartActivity.java) with your Client ID and corresponding App secret.
2. Add the `customerIdentifier` (that uniquely identifies a customer) to [StartActivity.java:42](https://github.com/saltedge/saltedge-android/blob/master/app/src/main/java/com/saltedge/sdk/sample/features/StartActivity.java)
3. Run the app

*Note*: You can find your Client ID and App secret on your [profile](https://www.saltedge.com/keys_and_secrets) page.

### Usage

* Open Android Studio and create a new app
* Android Studio -> File -> Import Module -> find module named "saltedge-library" -> Import
* Add the clientAppId and clientAppSecret keys to the StartActivity

**Warning!** The SDK doesn't work without clientAppId and clientAppSecret!

* The customerIdentifier is used to [create customers](https://docs.saltedge.com/v4_apps/reference/#customers-create). 

```java
    private void createCustomer() {
        String customerIdentifier = "customerIdentifier"; // Random num, each installation - new
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

* You can also get the list of providers:


```java
    public static void fetchProviders() {
        String countryCode = "XX";//prefered country code or null
        SERequestManager.getInstance().fetchProviders(countryCode, new ProvidersConnector.Result() {
        
            @Override
            public void onSuccess(ArrayList<ProviderData> providersList) {
                //use providers list
            }

            @Override
            public void onFailure(String errorResponse) {
                //process error
            }
        });
    }
```
* To connect to the bank you need a provider code and a country code.
You can obtain these by calling `getCode()` and `getCountryCode()` on an `SEProvider` object. You will also need a `customerId` (obtain after creating the customer) and a `callbackUrl`.

In order to encapsulate these params, you can use the `SECreateTokenParams` class.
One more time, you need:
    - a bit of courage
    - provider_code
    - scopes
    - customerSecret
    - callbackUrl (the URL the user will be redirected to, you can pass an empty string if you developing mobile app)
    
```java
     private void obtainCreateToken(SEProvider selectedProvider, String callbackUrl, String customerSecret) {
         String providerCode = selectedProvider.getCode();
         String[] scopes = {"accounts", "transactions"};
         SERequestManager.getInstance().createToken(providerCode, scopes, callbackUrl, customerSecret,
             new TokenConnectionResult() {
                 @Override
                 public void onSuccess(String connectUrl) {
                     // here is a URL you can use to redirect the user
                 }
        
                 @Override
                 public void onFailure(String errorMessage) {
                     //process error
                 }
             });
    }
```
or call createToken(...) with custom map of params
```java
     private void obtainCreateToken(SEProvider selectedProvider, String callbackUrl, String customerSecret) {
         HashMap<String, Object> dataMap = new HashMap<>();
         String providerCode = selectedProvider.getCode();
         dataMap.put("provider_code", providerCode);
         String[] scopes = {"accounts", "transactions"};
         dataMap.put("scopes", scopes);
         dataMap.put("return_to", "custom return url");
         SERequestManager.getInstance().createToken(dataMap, customerSecret,
             new TokenConnectionResult() {
                 @Override
                 public void onSuccess(String connectUrl) {
                     // here is a URL you can use to redirect the user
                 }

                 @Override
                 public void onFailure(String errorMessage) {
                     //process error
                 }
             });
    }
```

* Using the returned URL, you can let the user import their data. After doing this, you will obtain a `loginSecret`.

```java   
    private void goToURL(String url, View view) {
        WebView webView = (WebView) view.findViewById(R.id.webView);
        SEWebViewTools.getInstance().initializeWithUrl(getActivity(), webView, url, new SEWebViewTools.WebViewRedirectListener() {
            @Override
            public void onLoginSecretFetchSuccess(String responseStatus, String loginSecret) {
                String loginSecret = loginSecret;
            }

            @Override
            public void onLoginSecretFetchError(String errorResponse) {...}
        });
    }
```

* Congratulations, using just a couple of lines you have obtained a `loginSecret`. This can be used to get the status of the login, its accounts and transactions.

```java
    SERequestManager.getInstance().fetchLogin(loginSecret, customerSecret, new FetchLoginsResult() {
                        @Override
                        public void onSuccess(List<LoginData> logins) {
                            //show logins
                        }
    
                        @Override
                        public void onFailure(String errorResponse) {
                            //process error
                        }
                    });
```                

* You can now get all the accounts of the login using the `loginSecret`
    
```java    
    SERequestManager.getInstance().fetchAccounts(customerSecret, loginSecret,
                    new FetchAccountsResult() {
                        @Override
                        public void onSuccess(ArrayList<AccountData> accountsList) {
                            //show accounts
                        }
    
                        @Override
                        public void onFailure(String errorResponse) {
                            //process error
                        }
            });
```
* For each accounts, you can obtain the list of all transactions.

```java
        SERequestManager.getInstance().fetchTransactionsOfAccount(customerSecret, loginSecret, accountId,
                        new FetchTransactionsResult() {
                            @Override
                            public void onSuccess(ArrayList<TransactionData> transactionsList) {
                                //show transactions
                            }
        
                            @Override
                            public void onFailure(String errorResponse) {
                                //process error
                            }
                        });
```

* For each accounts, you can obtain the list of all new transactions from specified ID.

```java
        SERequestManager.getInstance().fetchTransactionsOfAccount(customerSecret, loginSecret, accountId, fromId,
                        new FetchTransactionsResult() {
                            @Override
                            public void onSuccess(ArrayList<TransactionData> transactionsList) {
                                //show transactions
                            }
        
                            @Override
                            public void onFailure(String errorResponse) {
                                //process error
                            }
                        });
```        

* You can refresh the data for each login.
To refresh, you can obtain a token.
To create a `SERefreshToken`, you need:
    - localeCode
    - callbackUrl (the URL the user will be redirected to, you can pass an empty string if you developing mobile app)
    - loginSecret of the Login

* Refresh :

```java
        SERequestManager.getInstance().refreshToken(localeCode, callbackUrl, loginSecret, customerSecret, new TokenConnectionResult() {
        
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
* Reconnect:

```java    
        SERequestManager.getInstance().reconnectToken(localeCode, callbackUrl, loginSecret, customerSecret, new TokenConnectionResult() {
        
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

* To delete a login you'll need a LoginSecret            
    
```java    
        SERequestManager.getInstance().deleteLogin(loginSecret, customerSecret,
                        new DeleteLoginResult() {
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

#### Please consult the Demo app to see how it works

## Models

There are some provided models for serializing the objects received in the API responses. These represent the providers, logins, accounts, transactions, provider fields and their options. Whenever you request a resource that returns one of these types, they will always get serialized into Java classes. (For instance, the `listingTransactionsOfAccount(...)` method has a `ArrayList<>` containing `SETransaction` instances in its success callback.)

Models contained within the components:

* `SEAccount`
* `SELogin`
* `SEProvider`
* `SETransaction`

For a supplementary description of the models listed above that is not included in the sources' docs, feel free to visit the [API Reference](https://docs.saltedge.com/reference/).

## Utilities

A few utility classes are bundled within the components, and are used internally, but you could also use them if you find that necessary.

## Versioning

## License

See the LICENSE file.

## References

1. [Salt Edge Connect Guide](https://docs.saltedge.com/guides/connect/)
2. [Salt Edge API Reference](https://docs.saltedge.com/reference/)
