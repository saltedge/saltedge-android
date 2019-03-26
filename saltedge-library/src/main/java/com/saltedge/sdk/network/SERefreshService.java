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
package com.saltedge.sdk.network;

import com.saltedge.sdk.connector.ConnectionInteractiveCredentialsConnector;
import com.saltedge.sdk.connector.ConnectionRefreshConnector;
import com.saltedge.sdk.connector.ConnectionsShowConnector;
import com.saltedge.sdk.interfaces.FetchConnectionResult;
import com.saltedge.sdk.interfaces.FetchConnectionsResult;
import com.saltedge.sdk.interfaces.RefreshConnectionResult;
import com.saltedge.sdk.model.SEConnection;
import com.saltedge.sdk.model.SEStage;
import com.saltedge.sdk.utils.SEConstants;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SERefreshService {

    public SERefreshService(RefreshConnectionResult callback) {
        this.callback = callback;
    }

    private RefreshConnectionResult callback;
    private ConnectionRefreshConnector refreshConnector;
    private ConnectionInteractiveCredentialsConnector interactiveConnector;
    private ConnectionsShowConnector showConnectionConnector;
    private final static long POLLING_TIMEOUT = 5000L;
    private String customerSecret;
    private SEConnection connectionData;
    private Timer timer;
    private FetchConnectionResult refreshConnectionCallback = new FetchConnectionResult() {

        @Override
        public void onSuccess(SEConnection connection) {
            checkConnectionStage(connection);
        }

        @Override
        public void onFailure(String errorMessage) {
            onRefreshError(errorMessage);
        }
    };

    private FetchConnectionResult interactiveConnectionCallback = new FetchConnectionResult() {

        @Override
        public void onSuccess(SEConnection connection) {
            checkConnectionStage(connection);
        }

        @Override
        public void onFailure(String errorMessage) {
            onInteractiveStepError(errorMessage);
        }
    };

    private FetchConnectionsResult showConnectionsCallback = new FetchConnectionsResult() {

        @Override
        public void onSuccess(List<SEConnection> connections) {
            if (!connections.isEmpty()) {
                SEConnection lastConnectionData = connections.get(0);
                checkConnectionStage(lastConnectionData);
            }
        }

        @Override
        public void onFailure(String errorMessage) {
            if (callback != null) callback.onConnectionStateFetchError(errorMessage);
        }
    };

    /**
     * Start refresh connectionData process
     * @param customerSecret - current customer secret
     * @param connectionData - ConnectionData model which should be refreshed
     * @param refreshScopes - refresh scopes. e.g. `['accounts', 'transactions']`
     * @return SERefreshService itself
     */
    public SERefreshService startRefresh(String customerSecret,
                                         SEConnection connectionData,
                                         String[] refreshScopes) {
        this.customerSecret = customerSecret;
        this.connectionData = connectionData;
        if (secretsNotValid()) {
            onRefreshError(SEConstants.ERROR_INVALID_REFRESH_SECRETS);
        } else {
            if (refreshConnector != null) {
                refreshConnector.cancel();
            }
            refreshConnector = new ConnectionRefreshConnector(refreshConnectionCallback);
            refreshConnector.refreshConnection(customerSecret, connectionData.getSecret(), refreshScopes);
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
        stopConnectionPolling();
    }

    /**
     * Sends requested interactive credentials or empty map
     * @param credentials - credentials map. e.g.: `{ "sms": "123456" }`
     */
    public void sendInteractiveData(Map<String, Object> credentials) {
        if (secretsNotValid()) {
            onRefreshError(SEConstants.ERROR_INVALID_REFRESH_SECRETS);
        } else {
            if (interactiveConnector != null) {
                interactiveConnector.cancel();
            }
            interactiveConnector = new ConnectionInteractiveCredentialsConnector(interactiveConnectionCallback);
            interactiveConnector.sendConnectionCredentials(
                    customerSecret,
                    connectionData.getSecret(),
                    credentials);
        }
    }

    private void startConnectionPolling() {
        try {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pollingAction();
                }
            }, 0, POLLING_TIMEOUT);
            showConnectionConnector = new ConnectionsShowConnector(showConnectionsCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopConnectionPolling() {
        try {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            if (showConnectionConnector != null) {
                showConnectionConnector.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showConnectionConnector = null;
        timer = null;
    }

    private boolean pollingIsStopped() {
        return timer == null;
    }

    private void pollingAction() {
        if (showConnectionConnector != null) {
            showConnectionConnector.fetchConnections(
                    customerSecret,
                    new String[]{ connectionData.getSecret() });
        }
    }

    private void checkConnectionStage(SEConnection connectionData) {
        if (connectionData.attemptIsFinished()) {
            onRefreshSuccess(connectionData);
        } else if (connectionData.attemptIsInteractive()) {
            askInteractiveData(connectionData.getLastAttempt().getLastStage());
        } else {
            if (pollingIsStopped()) {
                startConnectionPolling();
            }
        }
    }

    private void askInteractiveData(SEStage lastStage) {
        stopConnectionPolling();
        if (callback != null) callback.provideInteractiveData(lastStage);
    }

    private void onRefreshSuccess(SEConnection connectionData) {
        stopConnectionPolling();
        if (callback != null) callback.onRefreshSuccess(connectionData);
    }

    private void onRefreshError(String errorMessage) {
        stopConnectionPolling();
        if (callback != null) callback.onRefreshFailure(errorMessage);
    }

    private void onInteractiveStepError(String errorMessage) {
        if (callback != null) callback.onInteractiveStepFailure(errorMessage);
        if (pollingIsStopped()) {
            startConnectionPolling();
        }
    }

    private boolean secretsNotValid() {
        return customerSecret == null
                || customerSecret.isEmpty()
                || connectionData == null
                || connectionData.getSecret() == null
                || connectionData.getSecret().isEmpty();
    }
}
