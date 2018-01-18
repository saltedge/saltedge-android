## SaltEdge Android

A handful of classes to help you interact with the Salt Edge API from your Android app.

## Requirements

Android 4.0.2+ (minimum 15 sdk version)

### Source

Clone this repository

`$ git clone git@github.com:saltedge/saltedge-android.git`

## Connecting logins using the sample app

1. Replace the `clientId`, `appSecret` constants in [AndroidManifest.xml:16-17](https://github.com/saltedge/saltedge-android/blob/master/app/src/main/AndroidManifest.xml) with your Client ID and corresponding App secret.
2. Add the `customerIdentifier` (that uniquely identifies a customer) to [StartActivity.java:35](https://github.com/saltedge/saltedge-android/blob/master/app/src/main/java/com/saltedge/sdk/sample/StartActivity.java)
3. Run the app

*Note*: You can find your Client ID and App secret on your [profile](https://www.saltedge.com/clients/profile/settings) page.

### Usage

* Open Android Studio and create a new app
* Android Studio -> File -> Import Module -> find module named "saltedge-library" -> Import
* Add the following keys to the manifest:
        <meta-data android:name="App-secret" android:value="YOUR APP SECRET" />
        <meta-data android:name="Client-Id" android:value="YOUR CLIENT ID" />

**Warning!** The SDK doesn't work without these settings!

* The customerIdentifier is used to [create customers](https://docs.saltedge.com/guides/customers/#creating_customers). 

```java
    private void createCustomer() {
        String customerIdentifier = "customerIdentifier"; // Random num, each installation - new
         SERequestManager.getInstance().createCustomer(customerIdentifier, new SERequestManager.FetchListener() {
        @Override
        public void onFailure(String errorResponse) {...}
            @Override
            public void onSuccess(Object response) {
                String customerId = (String) response;
            }
        });
    }
```

* You can also get the list of providers:


```java
    public static void fetchProviders(final Context context,
                                        final DialogInterface.OnClickListener listener) {
            SERequestManager.getInstance().listingProviders(
            new SERequestManager.FetchListener() {
                @Override
                public void onFailure(String errorResponse) {...}

                @Override
                public void onSuccess(Object response) {
                    ArrayList<SEProvider> providers = (ArrayList<SEProvider>) response;
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
    - country_code
    - customerId
    - callbackUrl (the URL the user will be redirected to, you can pass an empty string if you developing mobile app)
    
```java
     private void obtainCreateToken(SEProvider selectedProvider, String customerId) { 
     SECreateTokenParams params = new SECreateTokenParams(selectedProvider.getCountryCode(),
     selectedProvider.getCode(), callbackUrl, customerId);
        SERequestManager.getInstance().createToken(params,
                new SERequestManager.FetchListener() {
                    @Override
                    public void onFailure(String errorResponse) {...}

                    @Override
                    public void onSuccess(Object response) {
                        // here is a URL you can use to redirect the user
                        String bankUrl = (String) response;
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
            public void onLoadingFinished(String responseStatus, String loginSecret) {
                String loginSecret = loginSecret;
            }

            @Override
            public void onLoadingFinishedWithError(String errorResponse) {...}
        });
    }
```

* Congratulations, using just a couple of lines you have obtained a `loginSecret`. This can be used to get the status of the login, its accounts and transactions.

```java
    SERequestManager.getInstance().fetchLogin(loginSecret,
                new SERequestManager.FetchListener() {
                    @Override
                    public void onFailure(String errorResponse) {...}

                    @Override
                    public void onSuccess(Object response) {
                        SELogin login = (SELogin) response;
                    }
                });
```                

* You can now get all the accounts of the login using the `loginSecret`
    
```java    
    SERequestManager.getInstance().listingAccounts(loginSecret, new SERequestManager.FetchListener() {
            @Override
            public void onFailure(String errorResponse) {...}

            @Override
            public void onSuccess(Object response) {
                ArrayList<SEAccount> accounts = (ArrayList<SEAccount>) response;
            }
        });
```
* For each accounts, you can obtain the list of transactions.

```java
        SERequestManager.getInstance().listingTransactionsOfAccount(loginSecret, accountId, 
        new SERequestManager.FetchListener() {
            @Override
            public void onFailure(String errorResponse) {...}

            @Override
            public void onSuccess(Object response) {
                ArrayList<SETransaction> transactions = (ArrayList<SETransaction>) response;
            }
        });
```        

* You can refresh the data for each login.
To refresh, you can obtain a token.
To create a `SERefreshToken`, you need:
    - the ID of the Login (taken from an `SELogin`)
    - callbackUrl (the URL the user will be redirected to, you can pass an empty string if you developing mobile app)
    - loginSecret of the Login

* Refresh :

```java
        SETokenParams params = new SETokenParams(loginId, "", callbackUrl, false, null);
        SERequestManager.getInstance().refreshToken(params, loginSecret,
                new SERequestManager.FetchListener() {

                    @Override
                    public void onFailure(String errorResponse) {...}

                    @Override
                    public void onSuccess(Object response) {
                           String url = (String) response);
                    }
                });
```    
* Reconnect:

```java    
        SETokenParams params = new SETokenParams(loginId, "", Constants.CALLBACK_URL, false, null);
        SERequestManager.getInstance().refreshToken(params, loginSecret,
                new SERequestManager.FetchListener() {

                    @Override
                    public void onFailure(String errorResponse) {
                        UITools.destroyAlertDialog(progressDialog);
                        UITools.failedParsing(getActivity(), errorResponse);
                    }

                    @Override
                    public void onSuccess(Object response) {
                           String url = (String) response);
                    }
                });
```           

* To delete a login you'll need a LoginSecret            
    
```java    
        SERequestManager.getInstance().deleteLogin(loginSecret, new SERequestManager.FetchListener() {
            @Override
            public void onFailure(String errorResponse) {...}

            @Override
            public void onSuccess(Object response) {
                String statusResponse = (String) response;
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
