/*
Copyright © 2015 Salt Edge. https://saltedge.com

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
package com.saltedge.sdk.models;

import com.google.gson.annotations.SerializedName;
import com.saltedge.sdk.utils.SEConstants;
import com.saltedge.sdk.utils.SEDateTools;

import java.util.ArrayList;
import java.util.Date;

public class SELogin {

    @SerializedName(SEConstants.KEY_ID)
    private int id;

    @SerializedName(SEConstants.KEY_SECRET)
    private String secret;

    @SerializedName(SEConstants.KEY_FINISHED)
    private boolean finished;

    @SerializedName(SEConstants.KEY_FINISHED_RECENT)
    private boolean finishedRecent;

    @SerializedName(SEConstants.KEY_PARTIAL)
    private boolean partial;

    @SerializedName(SEConstants.KEY_PROVIDER_CODE)
    private String providerCode;

    @SerializedName(SEConstants.KEY_PROVIDER_NAME)
    private String providerName;

    @SerializedName(SEConstants.KEY_AUTOMATIC_FETCH)
    private boolean automaticFetch;

    @SerializedName(SEConstants.KEY_INTERACTIVE)
    private boolean interactive;

    @SerializedName(SEConstants.KEY_INTERACTIVE_HTML)
    private String interactiveHtml;

    @SerializedName(SEConstants.KEY_INTERACTIVE_FIELDS_NAMES)
    private ArrayList<String> interactiveFieldsNames;

    @SerializedName(SEConstants.KEY_CUSTOMER_EMAIL)
    private String customerEmail;

    @SerializedName(SEConstants.KEY_CREATED_AT)
    private String createdAt;

    @SerializedName(SEConstants.KEY_UPDATED_AT)
    private String updatedAt;

    @SerializedName(SEConstants.KEY_LAST_FAIL_AT)
    private String lastFailAt;

    @SerializedName(SEConstants.KEY_LAST_REQUEST_AT)
    private String lastRequestAt;

    @SerializedName(SEConstants.KEY_LAST_FAIL_MESSAGE)
    private String lastFailMessage;

    @SerializedName(SEConstants.KEY_LAST_SUCCESS_AT)
    private String lastSuccessAt;

    @SerializedName(SEConstants.KEY_STATUS)
    private String status;

    @SerializedName(SEConstants.KEY_STAGE)
    private String stage;

    @SerializedName(SEConstants.KEY_COUNTRY_CODE)
    private String countryCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinishedRecent() {
        return finishedRecent;
    }

    public void setFinishedRecent(boolean finishedRecent) {
        this.finishedRecent = finishedRecent;
    }

    public boolean getPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
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

    public boolean isAutomaticFetch() {
        return automaticFetch;
    }

    public void setAutomaticFetch(boolean automaticFetch) {
        this.automaticFetch = automaticFetch;
    }

    public boolean isInteractive() {
        return interactive;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public String getInteractiveHtml() {
        return interactiveHtml;
    }

    public void setInteractiveHtml(String interactiveHtml) {
        this.interactiveHtml = interactiveHtml;
    }

    public ArrayList<String> getInteractiveFieldsNames() {
        return interactiveFieldsNames;
    }

    public void setInteractiveFieldsNames(ArrayList<String> interactiveFieldsNames) {
        this.interactiveFieldsNames = interactiveFieldsNames;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Date getCreatedAt() {
        return SEDateTools.parseStringToDate(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return SEDateTools.parseStringToDate(updatedAt);
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getLastFailAt() {
        return SEDateTools.parseStringToDate(lastFailAt);
    }

    public void setLastFailAt(String lastFailAt) {
        this.lastFailAt = lastFailAt;
    }

    public Date getLastRequestAt() {
        return SEDateTools.parseStringToDate(lastRequestAt);
    }

    public void setLastRequestAt(String lastRequestAt) {
        this.lastRequestAt = lastRequestAt;
    }

    public String getLastFailMessage() {
        return lastFailMessage;
    }

    public void setLastFailMessage(String lastFailMessage) {
        this.lastFailMessage = lastFailMessage;
    }

    public Date getLastSuccessAt() {
        return SEDateTools.parseStringToDate(lastSuccessAt);
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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean equals(SELogin object) {
        return object instanceof SELogin && id == ((object).getId());
    }

}
