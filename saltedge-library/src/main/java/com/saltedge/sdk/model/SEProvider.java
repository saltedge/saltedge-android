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

/**
 * Model for Provider
 *
 * @see <a href="https://docs.saltedge.com/account_information/v5/#providers-attributes">Provider Attributes</a>
 */
public class SEProvider extends BaseModel implements Serializable {

    @SerializedName(SEConstants.KEY_CODE)
    private String code;

    @SerializedName(SEConstants.KEY_NAME)
    private String name;

    @SerializedName(SEConstants.KEY_MODE)
    private String mode;

    @SerializedName(SEConstants.KEY_STATUS)
    private String status;

    @SerializedName(SEConstants.KEY_AUTOMATIC_FETCH)
    private Boolean automaticFetch;

    @SerializedName(SEConstants.KEY_CUSTOMER_NOTIFIED_ON_SIGN_IN)
    private Boolean customerNotifiedOnSignIn;

    @SerializedName(SEConstants.KEY_INTERACTIVE)
    private Boolean interactive;

    @SerializedName(SEConstants.KEY_IDENTIFICATION_MODE)
    private String identificationMode;

    @SerializedName(SEConstants.KEY_INSTRUCTION)
    private String instruction;

    @SerializedName(SEConstants.KEY_HOME_URL)
    private String homeUrl;

    @SerializedName(SEConstants.KEY_LOGIN_URL)
    private String loginUrl;

    @SerializedName(SEConstants.KEY_LOGO_URL)
    private String logoUrl;

    @SerializedName(SEConstants.KEY_COUNTRY_CODE)
    private String countryCode;

    @SerializedName(SEConstants.KEY_REFRESH_TIMEOUT)
    private int refreshTimeout;

    @SerializedName(SEConstants.KEY_HOLDER_INFO)
    private String[] holderInfo;

    @SerializedName(SEConstants.KEY_MAX_CONSENT_DAYS)
    private Integer maxConsentDays;

    // GETTER AND SETTERS

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getAutomaticFetch() {
        return automaticFetch;
    }

    public void setAutomaticFetch(Boolean automaticFetch) {
        this.automaticFetch = automaticFetch;
    }

    public Boolean getCustomerNotifiedOnSignIn() {
        return customerNotifiedOnSignIn;
    }

    public void setCustomerNotifiedOnSignIn(Boolean customerNotifiedOnSignIn) {
        this.customerNotifiedOnSignIn = customerNotifiedOnSignIn;
    }

    public Boolean getInteractive() {
        return interactive;
    }

    public void setInteractive(Boolean interactive) {
        this.interactive = interactive;
    }

    public String getIdentificationMode() {
        return identificationMode;
    }

    public void setIdentificationMode(String identificationMode) {
        this.identificationMode = identificationMode;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getRefreshTimeout() {
        return refreshTimeout;
    }

    public void setRefreshTimeout(int refreshTimeout) {
        this.refreshTimeout = refreshTimeout;
    }

    public String[] getHolderInfo() {
        return holderInfo;
    }

    public void setHolderInfo(String[] holderInfo) {
        this.holderInfo = holderInfo;
    }

    public Integer getMaxConsentDays() {
        return maxConsentDays;
    }

    public void setMaxConsentDays(Integer maxConsentDays) {
        this.maxConsentDays = maxConsentDays;
    }

    // EXTRA METHODS

    public boolean isOAuth() {
        return mode.equals("oauth");
    }
}
