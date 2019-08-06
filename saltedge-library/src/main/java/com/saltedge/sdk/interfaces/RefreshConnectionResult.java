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
package com.saltedge.sdk.interfaces;

import com.saltedge.sdk.model.SEConnection;
import com.saltedge.sdk.model.SEStage;

/**
 * Interface definition for a callback to be invoked when Refresh Connection operation state changes
 */
public interface RefreshConnectionResult {

    /**
     * Callback method is invoked when Refresh Connection operation finished with success
     *
     * @param connection SEConnection objects
     */
    void onRefreshSuccess(SEConnection connection);

    /**
     * Callback method is invoked when Refresh Connection operation finished with error
     *
     * @param errorMessage String which describes occurred error
     */
    void onRefreshFailure(String errorMessage);

    /**
     * Callback method is invoked when interactive step of Refresh Connection operation finished with error
     *
     * @param errorMessage String which describes occurred error
     */
    void onInteractiveStepFailure(String errorMessage);

    /**
     * Callback method is invoked when Fetch State step of Refresh Connection operation finished with error
     *
     * @param errorMessage String which describes occurred error
     */
    void onConnectionStateFetchError(String errorMessage);

    /**
     * Callback method is invoked when the currently fetching Connection requires any interactive credentials for fetching.
     * Call `refreshService.sendInteractiveData(credentials)` when credentials are ready
     *
     * @param lastStage SEStage object
     */
    void provideInteractiveData(SEStage lastStage);
}
