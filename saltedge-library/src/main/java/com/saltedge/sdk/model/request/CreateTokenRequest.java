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
package com.saltedge.sdk.model.request;

import com.google.gson.annotations.SerializedName;
import com.saltedge.sdk.utils.SEConstants;

public class CreateTokenRequest {

    @SerializedName(SEConstants.KEY_DATA)
    private SECreateTokenData data;

    public CreateTokenRequest(String[] allowedCountries, String providerCode, String[] scopes, String returnTo) {
        data = new SECreateTokenData(allowedCountries, providerCode, scopes, returnTo, SEConstants.IFRAME);
    }

    private class SECreateTokenData {

        @SerializedName(SEConstants.KEY_ALLOWED_COUNTRIES)
        private String[] allowedCountries;

        @SerializedName(SEConstants.KEY_PROVIDER_CODE)
        private String providerCode;

        @SerializedName(SEConstants.KEY_FETCH_SCOPES)
        private String[] fetchScopes;

        @SerializedName(SEConstants.KEY_RETURN_TO)
        private String returnTo;

        @SerializedName(SEConstants.JAVASCRIPT_CALLBACK)
        private String javascriptCallback;

        private SECreateTokenData(String[] allowedCountries, String providerCode, String[] scopes,
                                  String returnTo, String javascriptCallback) {
            this.allowedCountries = allowedCountries;
            this.providerCode = providerCode;
            this.fetchScopes = scopes;
            this.returnTo = returnTo;
            this.javascriptCallback = javascriptCallback;
        }
    }
}
