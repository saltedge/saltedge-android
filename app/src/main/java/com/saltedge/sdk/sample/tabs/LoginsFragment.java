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
package com.saltedge.sdk.sample.tabs;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.saltedge.sdk.models.SELogin;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.LoginsAdapter;
import com.saltedge.sdk.sample.utils.Constants;
import com.saltedge.sdk.sample.utils.Tools;
import com.saltedge.sdk.sample.utils.UITools;

import java.util.ArrayList;

public class LoginsFragment extends Fragment {

    private ProgressDialog progressDialog;
    private ArrayList<SELogin> logins;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        progressDialog = UITools.createProgressDialog(getActivity(), getString(R.string.loading));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_list_view, null);
        getLogins();
        return mainView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UITools.destroyProgressDialog(progressDialog);
    }

    private void getLogins() {
        String[] loginSecretArray = Tools.getArrayFromPreferences(getActivity(), Constants.LOGIN_SECRET_ARRAY);
        logins = new ArrayList<>();
        getAllLogins(loginSecretArray, 0);
    }

    public void getAllLogins(final String[] loginSecretArray, int i) {
        final int position = i;
        if (position >= loginSecretArray.length) {
            showLogin();
            return;
        }
        UITools.showProgress(progressDialog);
        String loginSecret = loginSecretArray[position];
        String customerSecret = Tools.getStringFromPreferences(getActivity(), Constants.KEY_CUSTOMER_SECRET);
        SERequestManager.getInstance().fetchLogin(loginSecret, customerSecret,
                new SERequestManager.FetchListener() {

                    @Override
                    public void onFailure(String errorResponse) {
                        UITools.destroyAlertDialog(progressDialog);
                        UITools.failedParsing(getActivity(), errorResponse);
                    }

                    @Override
                    public void onSuccess(Object response) {
                        UITools.destroyAlertDialog(progressDialog);
                        logins.add((SELogin) response);
                        getAllLogins(loginSecretArray, position + 1);
                    }
                });
    }

    public void showLogin() {
        if (getView() != null) {
            ListView listView = getView().findViewById(R.id.listView);
            listView.setAdapter(new LoginsAdapter(getActivity(), logins));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SELogin login = logins.get(position);
                    goToAccounts(login.getProviderCode(), login.getId());
                }
            });
        }
    }

    public void goToAccounts(String providerCode, String loginId) {
        AccountsFragment accountsFragment = AccountsFragment.newInstance(loginId, providerCode);
        try {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, accountsFragment).addToBackStack(null).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
