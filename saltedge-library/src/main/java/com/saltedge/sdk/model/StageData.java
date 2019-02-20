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
import java.util.Map;

public class StageData extends BaseModel implements Serializable {

    @SerializedName(SEConstants.KEY_NAME)
    private String name;

    @SerializedName(SEConstants.KEY_INTERACTIVE_HTML)
    private String interactiveHtml;

    @SerializedName(SEConstants.KEY_INTERACTIVE_FIELDS_NAMES)
    private String[] interactiveFieldsNames;

    @SerializedName(SEConstants.KEY_INTERACTIVE_FIELDS_OPTIONS)
    private Map<String, InteractiveFieldOptionData> interactiveFieldsOptions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInteractiveHtml() {
        return interactiveHtml;
    }

    public void setInteractiveHtml(String interactiveHtml) {
        this.interactiveHtml = interactiveHtml;
    }

    public String[] getInteractiveFieldsNames() {
        return interactiveFieldsNames;
    }

    public void setInteractiveFieldsNames(String[] interactiveFieldsNames) {
        this.interactiveFieldsNames = interactiveFieldsNames;
    }

    public Map<String, InteractiveFieldOptionData> getInteractiveFieldsOptions() {
        return interactiveFieldsOptions;
    }

    public void setInteractiveFieldsOptions(Map<String, InteractiveFieldOptionData> interactiveFieldsOptions) {
        this.interactiveFieldsOptions = interactiveFieldsOptions;
    }
}
