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
package com.saltedge.sdk.sample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.saltedge.sdk.model.TransactionData;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.utils.PreferencesTools;

import java.util.ArrayList;

public class TransactionAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<TransactionData> transactionsList;

    public TransactionAdapter(Context context, ArrayList<TransactionData> transactionsList) {
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.transactionsList = transactionsList;
    }

    @Override
    public int getCount() {
        return transactionsList.size();
    }

    @Override
    public TransactionData getItem(int position) {
        return transactionsList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = layoutInflater.inflate(R.layout.list_item_transaction, parent, false);
        TextView title = rowView.findViewById(R.id.title);
        TextView subtitleLeft = rowView.findViewById(R.id.subtitleLeft);
        TextView subtitleRight = rowView.findViewById(R.id.subtitleRight);
        TransactionData transaction = getItem(position);
        title.setText(transaction.getDescription());
        subtitleLeft.setText(PreferencesTools.parseDateToString(transaction.getMadeOnData()));
        String subTitle = transaction.getCurrencyCode() + " " + transaction.getAmount();
        subtitleRight.setText(subTitle);
        return rowView;
    }
}
