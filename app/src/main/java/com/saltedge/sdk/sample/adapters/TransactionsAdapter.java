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
package com.saltedge.sdk.sample.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.saltedge.sdk.model.SETransaction;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.utils.DateTools;

import java.util.ArrayList;

public class TransactionsAdapter extends ArrayAdapter<SETransaction> {

    public TransactionsAdapter(Context context, ArrayList<SETransaction> transactions) {
        super(context, 0, transactions);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        SETransaction transaction = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_transaction, parent, false);
        }
        // Lookup view for data population
        TextView dateLabel = convertView.findViewById(R.id.dateLabel);
        TextView amountLabel = convertView.findViewById(R.id.amountLabel);
        TextView descriptionLabel = convertView.findViewById(R.id.description);
        // Populate the data into the template view using the data object
        String dateText = "Invalid entry";
        String amountText = "";
        String descriptionText = "";
        if (transaction != null) {
            dateText = DateTools.formatDateToString(transaction.getMadeOnDate());
            amountText = transaction.getCurrencyCode() + " " + transaction.getAmount();
            descriptionText = transaction.getDescription();
        }
        dateLabel.setText(dateText);
        descriptionLabel.setText(descriptionText);
        amountLabel.setText(amountText);
        // Return the completed view to render on screen
        return convertView;
    }
}
