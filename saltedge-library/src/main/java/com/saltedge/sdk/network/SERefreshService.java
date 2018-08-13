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
package com.saltedge.sdk.network;

import com.saltedge.sdk.connector.LoginInteractiveCredentialsConnector;
import com.saltedge.sdk.connector.LoginRefreshConnector;
import com.saltedge.sdk.connector.LoginsShowConnector;
import com.saltedge.sdk.interfaces.FetchLoginResult;
import com.saltedge.sdk.interfaces.FetchLoginsResult;
import com.saltedge.sdk.interfaces.RefreshLoginResult;
import com.saltedge.sdk.model.LoginData;
import com.saltedge.sdk.model.StageData;
import com.saltedge.sdk.utils.SEConstants;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SERefreshService {

    public SERefreshService(RefreshLoginResult callback) {
        this.callback = callback;
    }

    private RefreshLoginResult callback;
    private LoginRefreshConnector refreshConnector;
    private LoginInteractiveCredentialsConnector interactiveConnector;
    private LoginsShowConnector showLoginConnector;
    private final static long POLLING_TIMEOUT = 5000L;
    private String customerSecret;
    private LoginData login;
    private Timer timer;
    private FetchLoginResult refreshLoginResult = new FetchLoginResult() {

        @Override
        public void onSuccess(LoginData login) {
            checkLoginStage(login);
        }

        @Override
        public void onFailure(String errorMessage) {
            onRefreshError(errorMessage);
        }
    };

    private FetchLoginsResult showLoginResult = new FetchLoginsResult() {

        @Override
        public void onSuccess(List<LoginData> logins) {
            if (!logins.isEmpty()) {
                LoginData lastLogin = logins.get(0);
                checkLoginStage(lastLogin);
            }
        }

        @Override
        public void onFailure(String errorMessage) {
            if (callback != null) callback.onLoginStateFetchError(errorMessage);
        }
    };

    /**
     * Start refresh login process
     * @param customerSecret - current customer secret
     * @param login - login model which should be refreshed
     * @param refreshScopes - refresh scopes. e.g. `['accounts', 'transactions']`
     * @return SERefreshService itself
     */
    public SERefreshService startRefresh(String customerSecret, LoginData login, String[] refreshScopes) {
        this.customerSecret = customerSecret;
        this.login = login;
        if (secretsNotValid()) {
            onRefreshError(SEConstants.ERROR_INVALID_REFRESH_SECRETS);
        } else {
            if (refreshConnector != null) {
                refreshConnector.cancel();
            }
            refreshConnector = new LoginRefreshConnector(refreshLoginResult);
            refreshConnector.refreshLogin(customerSecret, login.getSecret(), refreshScopes);
        }
        return this;
    }

    /**
     * Cancel all communications
     */
    public void cancel() {
        if (refreshConnector != null) {
            refreshConnector.cancel();
        }
        if (interactiveConnector != null) {
            interactiveConnector.cancel();
        }
        stopLoginPolling();
    }

    /**
     * Sends requested interactive credentials or empty map
     * @param credentials - credentials map. e.g.: `{ "data": { "credentials": { "sms": "123456" } } }`
     */
    public void sendInteractiveData(Map<String, Object> credentials) {
        if (secretsNotValid()) {
            onRefreshError(SEConstants.ERROR_INVALID_REFRESH_SECRETS);
        } else {
            if (interactiveConnector != null) {
                interactiveConnector.cancel();
            }
            interactiveConnector = new LoginInteractiveCredentialsConnector(refreshLoginResult);
            interactiveConnector.sendLoginCredentials(customerSecret, login.getSecret(), credentials);
        }
    }

    private void startLoginPolling() {
        try {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pollingAction();
                }
            }, 0, POLLING_TIMEOUT);
            showLoginConnector = new LoginsShowConnector(showLoginResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopLoginPolling() {
        try {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            if (showLoginConnector != null) {
                showLoginConnector.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showLoginConnector = null;
        timer = null;
    }

    private boolean pollingIsRunning() {
        return timer != null;
    }

    private void pollingAction() {
        if (showLoginConnector != null) {
            showLoginConnector.fetchLogins(new String[]{ login.getSecret() }, customerSecret);
        }
    }

    private void checkLoginStage(LoginData login) {
        if (login.attemptIsFinished()) {
            onRefreshSuccess();
        } else if (login.attemptIsInteractive()) {
            askInteractiveData(login.getLastAttempt().getLastStage());
        } else {
            if (!pollingIsRunning()) {
                startLoginPolling();
            }
        }
    }

    private void askInteractiveData(StageData lastStage) {
        stopLoginPolling();
        if (callback != null) callback.provideInteractiveData(lastStage);
    }

    private void onRefreshSuccess() {
        stopLoginPolling();
        if (callback != null) callback.onRefreshSuccess();
    }

    private void onRefreshError(String errorMessage) {
        stopLoginPolling();
        if (callback != null) callback.onRefreshFailure(errorMessage);
    }

    private boolean secretsNotValid() {
        return customerSecret == null || customerSecret.isEmpty() || login == null || login.getSecret() == null || login.getSecret().isEmpty();
    }
}
