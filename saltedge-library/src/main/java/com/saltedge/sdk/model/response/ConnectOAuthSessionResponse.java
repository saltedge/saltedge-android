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
package com.saltedge.sdk.model.response;

import com.google.gson.annotations.SerializedName;
import com.saltedge.sdk.utils.SEConstants;

public class ConnectOAuthSessionResponse {

    @SerializedName(SEConstants.KEY_DATA)
    private SECreateConnectOAuthSessionData data;

    public SECreateConnectOAuthSessionData getData() {
        return data;
    }

    public String getRedirectUrl() {
        return data.redirectUrl;
    }

    public String getExpiresAt() {
        return data.expiresAt;
    }

    public String getConnectionId() {
        return data.connectionId;
    }

    public String getAttemptId() {
        return data.attemptId;
    }

    public String getToken() {
        return data.token;
    }

    class SECreateConnectOAuthSessionData {

        @SerializedName(SEConstants.KEY_REDIRECT_URL)
        public String redirectUrl;

        @SerializedName(SEConstants.KEY_EXPIRES_AT)
        public String expiresAt;

        @SerializedName(SEConstants.KEY_CONNECTION_ID)
        public String connectionId;

        @SerializedName(SEConstants.KEY_ATTEMPT_ID)
        public String attemptId;

        @SerializedName(SEConstants.KEY_TOKEN)
        public String token;
    }
}
