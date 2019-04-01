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
package com.saltedge.sdk.model;

import com.google.gson.annotations.SerializedName;
import com.saltedge.sdk.utils.SEConstants;

public class SEApiError {

    @SerializedName(SEConstants.KEY_ERROR)
    private ApiErrorContent error;

    public String getErrorMessage() {
        return error.errorMessage;
    }

    public String getErrorClass() {
        return error.errorClass;
    }

    public String getDocumentationUrl() {
        return error.documentationUrl;
    }

    private class ApiErrorContent {
        @SerializedName(SEConstants.KEY_MESSAGE)
        private String errorMessage;

        @SerializedName(SEConstants.KEY_CLASS)
        private String errorClass;

        @SerializedName(SEConstants.KEY_DOCUMENTATION_URL)
        private String documentationUrl;
    }
}
