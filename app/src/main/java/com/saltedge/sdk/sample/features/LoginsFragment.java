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
package com.saltedge.sdk.sample.features;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.saltedge.sdk.interfaces.FetchLoginsResult;
import com.saltedge.sdk.model.LoginData;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.LoginsAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.PreferencesTools;
import com.saltedge.sdk.sample.utils.UITools;

import java.util.ArrayList;
import java.util.List;

public class LoginsFragment extends Fragment {

    private ProgressDialog progressDialog;
    private ArrayList<LoginData> loginsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLogins();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UITools.destroyProgressDialog(progressDialog);
    }

    private void getLogins() {
        String[] loginSecretArray = PreferencesTools.getArrayFromPreferences(getActivity(), Constants.LOGIN_SECRET_ARRAY);
        loginsList = new ArrayList<>();
        if (loginSecretArray.length == 0) {
            UITools.showAlertDialog(getActivity(), getString(R.string.no_logins));
        } else {
            getAllLogins(loginSecretArray);
        }
    }

    public void getAllLogins(String[] loginSecretsArray) {
        UITools.destroyProgressDialog(progressDialog);
        progressDialog = UITools.showProgressDialog(getActivity(), getString(R.string.loading_logins));
        String customerSecret = PreferencesTools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        SERequestManager.getInstance().fetchLogins(loginSecretsArray, customerSecret, new FetchLoginsResult() {
                    @Override
                    public void onSuccess(List<LoginData> logins) {
                        UITools.destroyAlertDialog(progressDialog);
                        loginsList = new ArrayList<>(logins);
                        showLogins();
                    }

                    @Override
                    public void onFailure(String errorResponse) {
                        UITools.destroyAlertDialog(progressDialog);
                        UITools.failedParsing(getActivity(), errorResponse);
                    }
                });
    }

    public void showLogins() {
        if (getView() != null) {
            ListView listView = getView().findViewById(R.id.listView);
            listView.setAdapter(new LoginsAdapter(getActivity(), loginsList));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LoginData login = loginsList.get(position);
                    goToAccounts(login.getProviderCode(), login.getId());
                }
            });
        }
    }

    public void goToAccounts(String providerCode, String loginId) {
        Intent accountsIntent = AccountsActivity.newIntent(getActivity(), loginId, providerCode);
        startActivity(accountsIntent);
    }
}
