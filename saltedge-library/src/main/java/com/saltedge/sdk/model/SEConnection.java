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
import com.saltedge.sdk.utils.SEDateTools;

import java.io.Serializable;
import java.util.Date;

public class SEConnection extends BaseModel implements Serializable {

    @SerializedName(SEConstants.KEY_SECRET)
    private String secret;

    @SerializedName(SEConstants.KEY_PROVIDER_ID)
    private String providerId;

    @SerializedName(SEConstants.KEY_PROVIDER_CODE)
    private String providerCode;

    @SerializedName(SEConstants.KEY_PROVIDER_NAME)
    private String providerName;

    @SerializedName(SEConstants.KEY_DAILY_REFRESH)
    private Boolean dailyRefresh;

    @SerializedName(SEConstants.KEY_LAST_SUCCESS_AT)
    private String lastSuccessAt;

    @SerializedName(SEConstants.KEY_STATUS)
    private String status;

    @SerializedName(SEConstants.KEY_COUNTRY_CODE)
    private String countryCode;

    @SerializedName(SEConstants.KEY_NEXT_REFRESH_POSSIBLE_AT)
    private String nextRefreshPossibleAt;

    @SerializedName(SEConstants.KEY_STORE_CREDENTIALS)
    private Boolean storeCredentials;

    @SerializedName(SEConstants.KEY_LAST_ATTEMPT)
    private SEAttempt lastAttempt;

    @SerializedName(SEConstants.KEY_HOLDER_INFO)
    private SEHolder holderInfo;

    @SerializedName(SEConstants.KEY_SHOW_CONSENT_CONFIRMATION)
    private Boolean showConsentConfirmation;

    @SerializedName(SEConstants.KEY_CONSENT_TYPES)
    private String[] consentTypes;

    @SerializedName(SEConstants.KEY_CONSENT_PERIOD_DAYS)
    private Integer consentPeriodDays;

    @SerializedName(SEConstants.KEY_CONSENT_GIVEN_AT)
    private String consentGivenAt;

    @SerializedName(SEConstants.KEY_CONSENT_EXPIRES_AT)
    private String consentExpiresAt;

    public boolean attemptIsFinished() {
        return lastAttempt != null && lastAttempt.getLastStage() != null
                && "finish".equals(lastAttempt.getLastStage().getName());
    }

    public boolean attemptIsInteractive() {
        return lastAttempt != null && lastAttempt.getLastStage() != null
                && "interactive".equals(lastAttempt.getLastStage().getName());
    }

// GETTER AND SETTERS

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Boolean getDailyRefresh() {
        return dailyRefresh;
    }

    public void setDailyRefresh(Boolean dailyRefresh) {
        this.dailyRefresh = dailyRefresh;
    }

    public String getLastSuccessAt() {
        return lastSuccessAt;
    }

    public void setLastSuccessAt(String lastSuccessAt) {
        this.lastSuccessAt = lastSuccessAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNextRefreshPossibleAt() {
        return nextRefreshPossibleAt;
    }

    public void setNextRefreshPossibleAt(String nextRefreshPossibleAt) {
        this.nextRefreshPossibleAt = nextRefreshPossibleAt;
    }

    public Boolean getStoreCredentials() {
        return storeCredentials;
    }

    public void setStoreCredentials(Boolean storeCredentials) {
        this.storeCredentials = storeCredentials;
    }

    public SEAttempt getLastAttempt() {
        return lastAttempt;
    }

    public void setLastAttempt(SEAttempt lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    public SEHolder getHolderInfo() {
        return holderInfo;
    }

    public void setHolderInfo(SEHolder holderInfo) {
        this.holderInfo = holderInfo;
    }

    public Boolean getShowConsentConfirmation() {
        return showConsentConfirmation;
    }

    public void setShowConsentConfirmation(Boolean showConsentConfirmation) {
        this.showConsentConfirmation = showConsentConfirmation;
    }

    public String[] getConsentTypes() {
        return consentTypes;
    }

    public void setConsentTypes(String[] consentTypes) {
        this.consentTypes = consentTypes;
    }

    public Integer getConsentPeriodDays() {
        return consentPeriodDays;
    }

    public void setConsentPeriodDays(Integer consentPeriodDays) {
        this.consentPeriodDays = consentPeriodDays;
    }

    public String getConsentGivenAt() {
        return consentGivenAt;
    }

    public void setConsentGivenAt(String consentGivenAt) {
        this.consentGivenAt = consentGivenAt;
    }

    public String getConsentExpiresAt() {
        return consentExpiresAt;
    }

    public void setConsentExpiresAt(String consentExpiresAt) {
        this.consentExpiresAt = consentExpiresAt;
    }
}
