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
import com.saltedge.sdk.utils.SEDateTools;

import org.json.JSONObject;

import java.util.Date;

public class TransactionData extends BaseModel {

    @SerializedName(SEConstants.KEY_DUPLICATED)
    private boolean duplicated;

    @SerializedName(SEConstants.KEY_MODE)
    private String mode;

    @SerializedName(SEConstants.KEY_MADE_ON)
    private String madeOn;

    @SerializedName(SEConstants.KEY_AMOUNT)
    private double amount;

    @SerializedName(SEConstants.KEY_CURRENCY_CODE)
    private String currencyCode;

    @SerializedName(SEConstants.KEY_DESCRIPTION)
    private String description;

    @SerializedName(SEConstants.KEY_CATEGORY)
    private String category;

    @SerializedName(SEConstants.KEY_EXTRA)
    private JSONObject extra;

    public boolean isDuplicated() {
        return duplicated;
    }

    public void setDuplicated(boolean duplicated) {
        this.duplicated = duplicated;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Date getMadeOn() {
        return SEDateTools.parseShortStringToDate(madeOn);
    }

    public void setMadeOn(String madeOn) {
        this.madeOn = madeOn;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public JSONObject getExtra() {
        return extra;
    }

    public void setExtra(JSONObject extra) {
        this.extra = extra;
    }

}
