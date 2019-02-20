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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.saltedge.sdk.model.ProviderData;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.adapters.ProvidersAdapter;

import java.util.ArrayList;

public class ProvidersDialog extends DialogFragment implements DialogInterface.OnClickListener, TextWatcher, AdapterView.OnItemClickListener {

    private static final String KEY_DATA = "KEY_DATA";
    private ProvidersAdapter adapter;
    private ArrayList<ProviderData> providers;
    public ProviderSelectListener selectListener;

    public static ProvidersDialog newInstance(ArrayList<ProviderData> providers, ProviderSelectListener selectListener) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_DATA, providers);
        ProvidersDialog fragment = new ProvidersDialog();
        fragment.selectListener = selectListener;
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        providers = (ArrayList<ProviderData>) getArguments().getSerializable(KEY_DATA);

        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_providers, null);
        EditText inputView = dialogView.findViewById(R.id.inputView);
        inputView.addTextChangedListener(this);

        ListView listView = dialogView.findViewById(R.id.listView);

        adapter = new ProvidersAdapter(getActivity());
        adapter.setListItems(providers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        adb.setTitle(R.string.choose_provider_title)
                .setNegativeButton(android.R.string.cancel, this)
                .setView(dialogView);
        return adb.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(true);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_NEGATIVE) {
            dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ProviderData selectedProvider = adapter.getItem(i);
        if (selectedProvider != null) {
            dismiss();
            selectListener.onProviderSelected(selectedProvider);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        adapter.setListItems(getFilteredProviders(providers, editable.toString()));
        adapter.notifyDataSetChanged();
    }

    private static ArrayList<ProviderData> getFilteredProviders(ArrayList<ProviderData> providers, String filter) {
        ArrayList<ProviderData> resultProviders = new ArrayList<>();
        for (ProviderData provider : providers) {
            if (provider.getName().toLowerCase().contains(filter.toLowerCase())) {
                resultProviders.add(provider);
            }
        }
        return filter.isEmpty() ? providers : resultProviders;
    }

    public interface ProviderSelectListener {
        void onProviderSelected(ProviderData provider);
    }
}
