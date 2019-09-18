# How To...  
  
* [Connect OAuth provider with Client Keys](#connect-oauth-provider-with-client-keys)

---
  
## Connect OAuth provider with Client Keys  

Salt Edge now supports Account Information Services channels, which can be used with PSD2 and Open Banking compliant APIs.
Read about it in [Salt Edge Documentation](https://docs.saltedge.com/general/#client_provider_keys).

1. Register application deep-link in Manifest.
    ```xml
      <activity
          android:name=".StartActivity">

          <intent-filter>
              <action android:name="android.intent.action.MAIN" />
              <category android:name="android.intent.category.LAUNCHER" />
          </intent-filter>

          <intent-filter android:label="@string/app_name">
              <action android:name="android.intent.action.VIEW" />
              <category android:name="android.intent.category.DEFAULT" />
              <category android:name="android.intent.category.BROWSABLE" />
              <!-- Accepts URIs that begin with "custom_scheme://custom_host/custom_path" -->
              <data
                  android:scheme="custom_scheme"
                  android:host="custom_host"
                  android:pathPattern="/custom_path"/>
          </intent-filter>
      </activity>
    ```

2. [Init SDK](https://github.com/saltedge/saltedge-android/blob/master/SDK_GUIDE.MD#init-sdk) with returnToUrl as application deep-link
    ```java
      String returnToUrl = "custom_scheme://custom_host/custom_path";

      SaltEdgeSDK.getInstance().init(
          applicationContext, 
          clientAppId, 
          clientAppSecret, 
          returnToUrl
      );
    ```

3. [Fetch providers](https://github.com/saltedge/saltedge-android/blob/master/SDK_GUIDE.MD#fetch-list-of-available-providers) and select one

4. [Create Connect Session](https://github.com/saltedge/saltedge-android/blob/master/SDK_GUIDE.MD#create-connect-session)

5. Open received `connectUrl` in external app 
    ```java
        private void loadConnectUrl(String connectUrl) {
            if (provider.isOAuth()) {
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(connectUrl)));//Open in external app
            } else {
                SEWebViewTools.getInstance().initializeWithUrl(this, webView, connectUrl, this);//Open in WebView
            }
        }
    ```

6. All connect operations (Authorizations, Fetching, etc.) will be performed in external app.

7. If deep-link is received, then extract customer_secret from params.
    ```java
        String dataString = getIntent().getDataString();
        if (dataString != null) {
            Uri uri = Uri.parse(dataString);
            String connectionSecret = uri.getQueryParameter(Constants.KEY_CONNECTION_SECRET);
            if (connectionSecret != null && !connectionSecret.isEmpty()) {
                //Save received `connection secret` for future operations
            }
        }
    ```

---
Copyright © 2014 - 2019 Salt Edge Inc. https://www.saltedge.com
