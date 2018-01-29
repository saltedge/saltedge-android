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
package com.saltedge.sdk.sample.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.saltedge.sdk.models.SEProvider;
import com.saltedge.sdk.models.comparators.SEProviderComparator;
import com.saltedge.sdk.network.SERequestManager;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.ProviderAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class UITools {

    public static ArrayList<SEProvider> providers;
    public static ArrayList<SEProvider> searchedProviders;

    public static AlertDialog showAlertDialog(Context context, String message) {
        return showAlertDialog(context, message, null);
    }

    public static AlertDialog showAlertDialog(Context context, String message, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.warning)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener);
        return adb.show();
    }

    public static void destroyAlertDialog(AlertDialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static ProgressDialog createProgressDialog(Context context, String title) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.show();
        dialog.setIndeterminate(true);
        dialog.setContentView(R.layout.progress_layout);
        dialog.setMessage(title);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.hide();
        return dialog;
    }

    public static void destroyProgressDialog(ProgressDialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static void showProgress(ProgressDialog dialog) {
        dialog.show();
    }

    public static void hideProgress(ProgressDialog dialog) {
        dialog.hide();
    }

    public static void failedParsing(Context context, String message) {
        UITools.showAlertDialog(context, message);
    }

    public static void fetchProviders(final Context context, final DialogInterface.OnClickListener listener) {
        if (providers == null || providers.isEmpty()) {
            final ProgressDialog progressDialog = UITools.createProgressDialog(context, context.getString(R.string.fetching_providers));
            progressDialog.show();
            SERequestManager.getInstance().listingProviders(true, new SERequestManager.FetchListener() {
                @Override
                public void onFailure(String errorResponse) {
                    UITools.destroyAlertDialog(progressDialog);
                    UITools.failedParsing(context, errorResponse);
                }

                @Override
                public void onSuccess(Object response) {
                    UITools.destroyAlertDialog(progressDialog);
                    providers = (ArrayList<SEProvider>) response;
                    Collections.sort(providers, new SEProviderComparator());
                    UITools.showProviders(context, listener);
                }
            });
        } else {
            UITools.showProviders(context, listener);
        }
    }

    public static void showProviders(Context context, DialogInterface.OnClickListener listener) {
        final ProviderAdapter adapter = new ProviderAdapter(context);
        searchedProviders = providers;
        adapter.setListItems(searchedProviders);
        if (providers != null && !providers.isEmpty()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final EditText input = new EditText(context);
            input.setHint("Find a bank");
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchedProviders = findProviders(s);
                    adapter.setListItems(searchedProviders);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            builder.setTitle(R.string.choose_provider_title)
                    .setAdapter(adapter, listener)
                    .setView(input)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.getListView().setFastScrollEnabled(true);
            dialog.show();

        } else {
            UITools.showAlertDialog(context, context.getString(R.string.providers_empty));
        }

    }

    private static ArrayList<SEProvider> findProviders(CharSequence chars) {
        ArrayList<SEProvider> resultProviders = new ArrayList<>();
        for(SEProvider provider : providers) {
            if (provider.getName().toLowerCase().contains(chars.toString().toLowerCase())) {
                resultProviders.add(provider);
            }
        }
        return chars.length() != 0 ? resultProviders : providers;
    }

}
