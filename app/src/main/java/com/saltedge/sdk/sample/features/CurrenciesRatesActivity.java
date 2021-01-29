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

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.saltedge.sdk.interfaces.FetchCurrencyRatesResult;
import com.saltedge.sdk.model.SECurrencyRate;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.CurrenciesRatesAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferenceRepository;
import com.saltedge.sdk.sample.utils.UITools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CurrenciesRatesActivity extends AppCompatActivity implements FetchCurrencyRatesResult {

    private String customerSecret = "";
    private ListView listView;
    private View emptyView;
    private TextView emptyLabelView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            String titleText = getString(R.string.currencies) + " (" + getCurrentDateStamp() + ")";
            actionBar.setTitle(titleText);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        customerSecret = PreferenceRepository.getStringFromPreferences(Constants.KEY_CUSTOMER_SECRET);
        setupViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// Respond to the action bar's Up/Home button
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchRates();
    }

    private void fetchRates() {
        String dateStamp = getCurrentDateStamp();
        SERequestManager.getInstance().fetchCurrenciesRates(customerSecret, dateStamp, this);

    }

    @Override
    public void onFetchCurrenciesSuccess(List<SECurrencyRate> rates) {
        emptyView.setVisibility(rates.isEmpty() ? View.VISIBLE : View.GONE);
        if (rates.isEmpty()) {
            emptyLabelView.setText(R.string.no_accounts);
        } else {
            CurrenciesRatesAdapter adapter = new CurrenciesRatesAdapter(this, new ArrayList<>(rates));
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onFetchCurrenciesFailure(String errorResponse) {
        UITools.showAlertDialog(this, errorResponse);
    }

    private String getCurrentDateStamp() {
        DateFormat formatter = SimpleDateFormat.getDateInstance();
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private void setupViews() {
        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);
        emptyLabelView = findViewById(R.id.emptyLabelView);
        emptyLabelView.setText(R.string.fetching_currencies);
    }
}
