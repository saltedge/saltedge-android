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

import java.io.Serializable;

public class SEConsent extends BaseModel implements Serializable {

    public final static String SCOPE_HOLDER_INFORMATION = "holder_information";
    public final static String SCOPE_ACCOUNT_DETAILS = "account_details";
    public final static String SCOPE_TRANSACTIONS_DETAILS = "transactions_details";

    @SerializedName(SEConstants.KEY_CONNECTION_ID)
    private String connectionId;

    @SerializedName(SEConstants.KEY_CUSTOMER_ID)
    private String customerId;

    @SerializedName(SEConstants.KEY_SCOPES)
    private String[] scopes;

    @SerializedName(SEConstants.KEY_FROM_DATE)
    private String fromDate;

    @SerializedName(SEConstants.KEY_TO_DATE)
    private String toDate;

    @SerializedName(SEConstants.KEY_PERIOD_DAYS)
    private Integer periodDays;

    @SerializedName(SEConstants.KEY_EXPIRES_AT)
    private String expiresAt;

    @SerializedName(SEConstants.KEY_REVOKED_AT)
    private String revokedAt;

    @SerializedName(SEConstants.KEY_COLLECTED_BY)
    private String collectedBy;

// CONSTRUCTOR

    public SEConsent(String[] scopes) {
        this.scopes = scopes;
    }

// GETTER AND SETTERS

    public String[] getScopes() {
        return scopes;
    }

    public void setScopes(String[] scopes) {
        this.scopes = scopes;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Integer getPeriodDays() {
        return periodDays;
    }

    public void setPeriodDays(Integer periodDays) {
        this.periodDays = periodDays;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(String revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }
}
