## SaltEdge Android

A handful of classes to help you interact with the Salt Edge API from your Android app.

## Requirements

Android 4.0+ (minimum 15 sdk version)

### Source

Clone this repository

`$ git clone git@github.com:saltedge/saltedge-android.git`

## Connecting logins using the sample app

1. Replace the `clientId`, `appSecret` constants in [AndroidManifest.xml:16-17](https://github.com/saltedge/saltedge-android/blob/master/app/src/main/AndroidManifest.xml) with your Client ID and corresponding App secret.
2. Add the `customerIdentifier` (that should be random set of chars, for each app install) to [StartActivity.java:35](https://github.com/saltedge/saltedge-android/blob/master/app/src/main/java/com/saltedge/sdk/sample/StartActivity.java)
3. Run the app

*Note*: You can find your Client ID and App secret at your [profile](https://www.saltedge.com/clients/profile/settings) page.

### Usage

* In Android Studio create an app
* Android Studio -> File -> Import Module -> find module named "saltedge-library" -> import
* In your Android Manijfest.xml file between tags <application> </application> add this:
        <meta-data android:name="App-secret" android:value="YOUR APP SECRET" />
        <meta-data android:name="Client-Id" android:value="YOUR CLIENT ID" />
Warning! Without this settings SaltEdgeSDK won't work!

* For using our sdk you should provide customerIdentifier, that will be used to get customerId, 
It will identify each new installed app. Also this customerId will be used to create token, 
for connect to bank provider.

You need:
- courage

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
* To get bank providers list you need:
    - courage

```java
    public static void fetchProviders(final Context context,
                                        final DialogInterface.OnClickListener listener) {
            SERequestManager.getInstance().listingProviders(true, 
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
* To connect to the bank you need provider code and country code  (each of the items from list you got in example above, 
you can obtain by getCode() and getCountryCode() method for each object). 
Also you need customerId and callbackUrl (url that you will be redirected after bank login fetch complete).
Also we have `SECreateTokenParams`, for more comfortable and clear posting data container. 
One more time, you need:
    - courage
    - provider code
    - country code
    - customerId
    - callback url
    
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
                        String bankUrl = (String) response;
                    }
                });

    }
```
* Now you can go to your favorite bank. You will need webview and url, that you get in example above.

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
* Congratulations, now you have your bank in our sistem and we happy to give you loginSecret, with this 
few symbols you can make magic. 
To get your login you need:
    - login secret

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
* Now you can get all accounts of this login. 
What you need:
    -login secret
    
```java    
    SERequestManager.getInstance().listingAccounts(loginSecret, true, new SERequestManager.FetchListener() {
            @Override
            public void onFailure(String errorResponse) {...}

            @Override
            public void onSuccess(Object response) {
                ArrayList<SEAccount> accounts = (ArrayList<SEAccount>) response;
            }
        });
```
* For each account you have possibility to get transactions. 
What you need:
    - login secret
    - account id (Each `SEAccount` contains id, you can obtain in with method getId() )

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
* Each login could be refreshed and reconnected.  Also we have `SETokenParams`, for more 
comfortable and clear posting data container. You will recieve url, to refresh your login. 
(Same logic as for connect flow). 
You need:
    - login id (`SELogin` object contains it, you can obtain in with method getId())
    - callback url
    - loginSecret

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

* For login delete you need:                
    - login secret
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

#### To see how it works, you can run app included to this lib.

## Models

There are some provided models for serializing the objects received in the API responses. These represent the providers, logins, accounts, transactions, provider fields and their options. Whenever you request a resource that returns one of these types, they will always get serialized into Java classes. (For instance, the `listingTransactionsOfAccount(...)` method has a `ArrayList<>` containing `SETransaction` instances in it's success callback.)

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
