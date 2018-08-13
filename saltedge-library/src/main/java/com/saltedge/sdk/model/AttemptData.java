/*
Copyright © 2018 Salt Edge. https://saltedge.com

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

public class AttemptData extends BaseModel implements Serializable {

    @SerializedName(SEConstants.KEY_API_MODE)
    private String apiMode;

    @SerializedName(SEConstants.KEY_API_VERSION)
    private String apiVersion;

    @SerializedName(SEConstants.KEY_AUTOMATIC_FETCH)
    private Boolean automaticFetch;

    @SerializedName(SEConstants.KEY_DAILY_REFRESH)
    private Boolean dailyRefresh;

    @SerializedName(SEConstants.KEY_CATEGORIZATION)
    private String categorization;

    @SerializedName(SEConstants.KEY_DEVICE_TYPE)
    private String deviceType;

    @SerializedName(SEConstants.KEY_REMOTE_IP)
    private String remoteIp;

    @SerializedName(SEConstants.KEY_EXCLUDE_ACCOUNTS)
    private String[] excludeAccounts;

    @SerializedName(SEConstants.KEY_IDENTIFY_MERCHANT)
    private Boolean identifyMerchant;

    @SerializedName(SEConstants.KEY_USER_PRESENT)
    private Boolean userPresent;

    @SerializedName(SEConstants.KEY_FAIL_AT)
    private String failAt;

    @SerializedName(SEConstants.KEY_FAIL_ERROR_CLASS)
    private String failErrorClass;

    @SerializedName(SEConstants.KEY_FAIL_MESSAGE)
    private String failMessage;

    @SerializedName(SEConstants.KEY_FETCH_SCOPES)
    private String[] fetchScopes;

    @SerializedName(SEConstants.KEY_FINISHED)
    private Boolean finished;

    @SerializedName(SEConstants.KEY_FINISHED_RECENT)
    private Boolean finishedRecent;

    @SerializedName(SEConstants.KEY_FROM_DATE)
    private String fromDate;

    @SerializedName(SEConstants.KEY_INTERACTIVE)
    private Boolean interactive;

    @SerializedName(SEConstants.KEY_LOCALE)
    private String locale;

    @SerializedName(SEConstants.KEY_PARTIAL)
    private Boolean partial;

    @SerializedName(SEConstants.KEY_STORE_CREDENTIALS)
    private Boolean storeCredentials;

    @SerializedName(SEConstants.KEY_SUCCESS_AT)
    private String successAt;

    @SerializedName(SEConstants.KEY_TO_DATE)
    private String toDate;

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

    @SerializedName(SEConstants.KEY_STAGES)
    private StageData[] stages;

    @SerializedName(SEConstants.KEY_LAST_STAGE)
    private StageData lastStage;

    public String getApiMode() {
        return apiMode;
    }

    public void setApiMode(String apiMode) {
        this.apiMode = apiMode;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Boolean getAutomaticFetch() {
        return automaticFetch;
    }

    public void setAutomaticFetch(Boolean automaticFetch) {
        this.automaticFetch = automaticFetch;
    }

    public Boolean getDailyRefresh() {
        return dailyRefresh;
    }

    public void setDailyRefresh(Boolean dailyRefresh) {
        this.dailyRefresh = dailyRefresh;
    }

    public String getCategorization() {
        return categorization;
    }

    public void setCategorization(String categorization) {
        this.categorization = categorization;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String[] getExcludeAccounts() {
        return excludeAccounts;
    }

    public void setExcludeAccounts(String[] excludeAccounts) {
        this.excludeAccounts = excludeAccounts;
    }

    public Boolean getIdentifyMerchant() {
        return identifyMerchant;
    }

    public void setIdentifyMerchant(Boolean identifyMerchant) {
        this.identifyMerchant = identifyMerchant;
    }

    public Boolean getUserPresent() {
        return userPresent;
    }

    public void setUserPresent(Boolean userPresent) {
        this.userPresent = userPresent;
    }

    public String getFailAt() {
        return failAt;
    }

    public void setFailAt(String failAt) {
        this.failAt = failAt;
    }

    public String getFailErrorClass() {
        return failErrorClass;
    }

    public void setFailErrorClass(String failErrorClass) {
        this.failErrorClass = failErrorClass;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    public String[] getFetchScopes() {
        return fetchScopes;
    }

    public void setFetchScopes(String[] fetchScopes) {
        this.fetchScopes = fetchScopes;
    }

    public Boolean isFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Boolean getFinishedRecent() {
        return finishedRecent;
    }

    public void setFinishedRecent(Boolean finishedRecent) {
        this.finishedRecent = finishedRecent;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public Boolean getInteractive() {
        return interactive;
    }

    public void setInteractive(Boolean interactive) {
        this.interactive = interactive;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean getPartial() {
        return partial;
    }

    public void setPartial(Boolean partial) {
        this.partial = partial;
    }

    public Boolean getStoreCredentials() {
        return storeCredentials;
    }

    public void setStoreCredentials(Boolean storeCredentials) {
        this.storeCredentials = storeCredentials;
    }

    public String getSuccessAt() {
        return successAt;
    }

    public void setSuccessAt(String successAt) {
        this.successAt = successAt;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
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

    public StageData[] getStages() {
        return stages;
    }

    public void setStages(StageData[] stages) {
        this.stages = stages;
    }

    public StageData getLastStage() {
        return lastStage;
    }

    public void setLastStage(StageData lastStage) {
        this.lastStage = lastStage;
    }
}
