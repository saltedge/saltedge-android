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
package com.saltedge.sdk.sample.features;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.interfaces.DeleteEntryResult;
import com.saltedge.sdk.interfaces.FetchConnectionsResult;
import com.saltedge.sdk.interfaces.ProvidersResult;
import com.saltedge.sdk.model.SEConnection;
import com.saltedge.sdk.model.SEProvider;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.BuildConfig;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.ConnectionsAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferenceRepository;
import com.saltedge.sdk.sample.utils.UITools;

import java.util.ArrayList;
import java.util.List;

import static com.saltedge.sdk.sample.utils.UITools.refreshProgressDialog;

public class ConnectionsActivity extends AppCompatActivity implements
        ProvidersDialog.ProviderSelectListener,
        AdapterView.OnItemClickListener,
        View.OnClickListener
{
    private BottomSheetDialog mBottomSheetDialog;
    private ProgressDialog progressDialog;
    private ArrayList<SEProvider> providers;
    private ArrayList<SEConnection> connectionsList;
    private String customerSecret = "";
    private String applicationCountryCode = "XF";//"XF" for fake providers
    private ListView listView;
    private View emptyView;
    private TextView emptyLabelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        applicationCountryCode = BuildConfig.DEBUG ? "XF" : getResources().getConfiguration().locale.getCountry();
        customerSecret = PreferenceRepository.getStringFromPreferences(Constants.KEY_CUSTOMER_SECRET);
        setupActionBar();
        setupContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_connections, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_provider:
                fetchAndShowProviders();
                return true;
            case R.id.show_rates:
                showRates();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViewsData();
    }

    @Override
    protected void onStop() {
        UITools.destroyProgressDialog(progressDialog);
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
        }
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addEntityButton:
                String[] connectionsSecretArray = PreferenceRepository.getAllConnectionsSecrets();
                if (connectionsSecretArray.length == 0) {
                    fetchAndShowProviders();
                }
                break;
            case R.id.accountsOption:
                if (mBottomSheetDialog != null) {
                    mBottomSheetDialog.dismiss();
                }
                showAccountsOfConnection(connectionsList.get((int) view.getTag()));
                break;
            case R.id.consentsOption:
                if (mBottomSheetDialog != null) {
                    mBottomSheetDialog.dismiss();
                }
                showConsentsOfConnection(connectionsList.get((int) view.getTag()));
                break;
            case R.id.deleteOption:
                if (mBottomSheetDialog != null) {
                    mBottomSheetDialog.dismiss();
                }
                if (SaltEdgeSDK.isPartner()) {
                    onDeleteConnectionSuccess(connectionsList.get((int) view.getTag()).getSecret());
                } else {
                    sendDeleteConnectionRequest(connectionsList.get((int) view.getTag()));
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showConnectionOptions(position);
    }

    @Override
    public void onProviderSelected(SEProvider provider) {
        UITools.showShortToast(this, "Connecting " + provider.getName());
        showConnectActivity(provider);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.drawable.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void setupContent() {
        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);
        emptyLabelView = findViewById(R.id.emptyLabelView);
        Button addProviderButton = findViewById(R.id.addEntityButton);
        addProviderButton.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(this);
        addProviderButton.setOnClickListener(this);
    }

    private void updateViewsData() {
        String[] connectionsSecrets = PreferenceRepository.getAllConnectionsSecrets();

        connectionsList = new ArrayList<>();
        emptyView.setVisibility(View.VISIBLE);
        if (connectionsSecrets.length == 0) {
            emptyLabelView.setText(R.string.no_connections);
        } else {
            emptyLabelView.setText(R.string.fetching_connections);
            fetchConnections(connectionsSecrets);
        }
    }

    private void fetchAndShowProviders() {
        if (providers == null || providers.isEmpty()) {
            progressDialog = refreshProgressDialog(this, progressDialog, R.string.fetching_providers);

            SERequestManager.getInstance().fetchProviders(applicationCountryCode, new ProvidersResult() {
                @Override
                public void onSuccess(List<SEProvider> providersList) {
                    onFetchProvidersSuccess(new ArrayList<>(providersList));
                }

                @Override
                public void onFailure(String errorMessage) {
                    onFetchFailure(errorMessage);
                }});
        } else {
            showProvidersListDialog();
        }
    }

    private void onFetchProvidersSuccess(ArrayList<SEProvider> providersList) {
        try {
            UITools.destroyAlertDialog(progressDialog);
            providers = providersList;
            showProvidersListDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProvidersListDialog() {
        if (providers != null && !providers.isEmpty()) {
            UITools.showShortToast(this, "Fetched " + providers.size());
            FragmentManager fragmentManager = getSupportFragmentManager();
            ProvidersDialog.newInstance(providers, this).show(fragmentManager, ProvidersDialog.TAG);
        } else {
            UITools.showAlertDialog(this, getString(R.string.providers_empty));
        }
    }

    private void fetchConnections(String[] connectionsSecrets) {
        progressDialog = refreshProgressDialog(this, progressDialog, R.string.fetching_connections);
        SERequestManager.getInstance().fetchConnections(customerSecret, connectionsSecrets,
                new FetchConnectionsResult() {
                    @Override
                    public void onSuccess(List<SEConnection> connections) {
                        onFetchConnectionsSuccess(connections);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        onFetchFailure(errorMessage);
                    }
                });
    }

    private void onFetchConnectionsSuccess(List<SEConnection> connections) {
        try {
            UITools.destroyAlertDialog(progressDialog);
            emptyView.setVisibility(View.GONE);
            connectionsList = new ArrayList<>(connections);
            listView.setAdapter(new ConnectionsAdapter(this, connectionsList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onFetchFailure(String errorMessage) {
        UITools.destroyAlertDialog(progressDialog);
        UITools.showAlertDialog(this, errorMessage);
    }

    private void sendDeleteConnectionRequest(SEConnection connection) {
        progressDialog = refreshProgressDialog(this, progressDialog, R.string.removing_connection);
        SERequestManager.getInstance().deleteConnection(customerSecret, connection.getSecret(),
                new DeleteEntryResult() {
                    @Override
                    public void onSuccess(Boolean entryIsRemoved, String connectionId) {
                        SEConnection connection = findConnectionById(connectionId);
                        if (entryIsRemoved && connection != null) {
                            onDeleteConnectionSuccess(connection.getSecret());
                        }
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        onFetchFailure(errorResponse);
                    }
                });
    }

    private void onDeleteConnectionSuccess(String connectionSecret) {
        try {
            PreferenceRepository.removeConnectionSecret(connectionSecret);
            UITools.destroyAlertDialog(progressDialog);
            updateViewsData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showConnectionOptions(int connectionIndex) {
        mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_connection_options, null);
        View accountsOption = sheetView.findViewById(R.id.accountsOption);
        accountsOption.setTag(connectionIndex);
        accountsOption.setOnClickListener(this);
        View consentsOption = sheetView.findViewById(R.id.consentsOption);
        consentsOption.setTag(connectionIndex);
        consentsOption.setOnClickListener(this);
        View deleteOption = sheetView.findViewById(R.id.deleteOption);
        deleteOption.setTag(connectionIndex);
        deleteOption.setOnClickListener(this);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }

    private void showAccountsOfConnection(SEConnection connection) {
        startActivity(AccountsActivity.newIntent(this, connection));
    }

    private void showConsentsOfConnection(SEConnection connection) {
        startActivity(ConsentsActivity.newIntent(this, connection));
    }

    private void showRates() {
        startActivity(new Intent(this, CurrenciesRatesActivity.class));
    }

    private void showConnectActivity(SEProvider provider) {
        try {
            this.startActivityForResult(ConnectActivity.newIntent(this, provider), ConnectActivity.CONNECT_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SEConnection findConnectionById(String connectionId) {
        for (SEConnection connection : connectionsList) {
            if (connection.getId().equals(connectionId)) return connection;
        }
        return null;
    }
}
