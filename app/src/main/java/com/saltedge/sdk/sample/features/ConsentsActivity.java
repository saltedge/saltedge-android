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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.saltedge.sdk.SaltEdgeSDK;
import com.saltedge.sdk.interfaces.FetchConsentsResult;
import com.saltedge.sdk.model.SEConnection;
import com.saltedge.sdk.model.SEConsent;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.ConsentAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferenceRepository;
import com.saltedge.sdk.sample.utils.UITools;

import java.util.ArrayList;
import java.util.List;

import static com.saltedge.sdk.sample.utils.UITools.refreshProgressDialog;

public class ConsentsActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, FetchConsentsResult {

    private enum ViewMode {
        CONSENTS, PARTNER_CONSENTS
    }

    private ProgressDialog progressDialog;
    private List<SEConsent> consents;
    private SEConnection currentConnection;
    private BottomSheetDialog mBottomSheetDialog;
    private ListView listView;
    private View emptyView;
    private TextView emptyLabelView;
    private String customerSecret = "";
    private ViewMode viewMode = ViewMode.CONSENTS;

    public static Intent newIntent(Activity activity, SEConnection connection) {
        Intent intent = new Intent(activity, ConsentsActivity.class);
        intent.putExtra(Constants.KEY_CONNECTION, connection);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        updateActivityTitle();
        setupViews();
        setInitialData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (SaltEdgeSDK.isPartner()) {
            getMenuInflater().inflate(R.menu.menu_consents, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// Respond to the action bar's Up/Home button
                finish();
                return true;
            case R.id.show_consents:
                tryToUpdateViewMode(ViewMode.CONSENTS);
                return true;
            case R.id.show_partner_consents:
                tryToUpdateViewMode(ViewMode.PARTNER_CONSENTS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchConsents();
    }

    @Override
    public void onDestroy() {
        UITools.destroyProgressDialog(progressDialog);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        //TODO
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SEConsent consent = consents.get(position);
        //TODO
    }

    @Override
    public void onSuccess(List<SEConsent> list) {
        UITools.destroyAlertDialog(progressDialog);
        consents = list;
        updateViewsContent();
    }

    @Override
    public void onFailure(String errorResponse) {
        UITools.destroyAlertDialog(progressDialog);
        updateViewsContent();
        UITools.showAlertDialog(this, errorResponse);
    }

    private void fetchConsents() {
        if (TextUtils.isEmpty(currentConnection.getSecret())) {
            return;
        }
        consents = new ArrayList<>();
        progressDialog = refreshProgressDialog(this, progressDialog, R.string.fetching_consents);
        if (viewMode == ViewMode.CONSENTS) {
            SERequestManager.getInstance().fetchConsents(customerSecret, currentConnection.getSecret(), this);
        } else {
            SERequestManager.getInstance().fetchPartnerConsents(currentConnection.getSecret(), this);
        }
    }

    private void updateViewsContent() {
        emptyView.setVisibility(consents.isEmpty() ? View.VISIBLE : View.GONE);
        if (consents.isEmpty()) {
            emptyLabelView.setText(R.string.no_accounts);
        } else {
            listView.setAdapter(new ConsentAdapter(this, new ArrayList<>(consents)));
        }
    }

    private void setupViews() {
        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);
        emptyLabelView = findViewById(R.id.emptyLabelView);
        emptyLabelView.setText(getEmptyViewTitleResId());
        listView.setOnItemClickListener(this);
    }

    private void setInitialData() {
        currentConnection = (SEConnection) this.getIntent().getSerializableExtra(Constants.KEY_CONNECTION);
        customerSecret = PreferenceRepository.getStringFromPreferences(Constants.KEY_CUSTOMER_SECRET);
    }

    private void tryToUpdateViewMode(ViewMode newViewMode) {
        if (viewMode != newViewMode) {
            viewMode = newViewMode;
            updateActivityTitle();
            fetchConsents();
        }
    }

    private void updateActivityTitle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            switch (viewMode) {
                case CONSENTS:
                    actionBar.setTitle(R.string.consents);
                    break;
                case PARTNER_CONSENTS:
                    actionBar.setTitle(R.string.partner_consents);
                    break;
            }
        }
    }

    private int getEmptyViewTitleResId() {
        switch (viewMode) {
            case CONSENTS:
                return R.string.no_consents;
            case PARTNER_CONSENTS:
                return R.string.no_partner_consents;
        }
        return R.string.no_consents;
    }
}
