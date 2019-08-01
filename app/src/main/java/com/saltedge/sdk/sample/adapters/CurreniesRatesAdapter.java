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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.saltedge.sdk.model.SECurrency;
import com.saltedge.sdk.sample.R;

import java.util.ArrayList;

public class CurreniesRatesAdapter extends ArrayAdapter<SECurrency> {

    public CurreniesRatesAdapter(Context context, ArrayList<SECurrency> accounts) {
        super(context, 0, accounts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SECurrency currency = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_currency, parent, false);
        }
        // Lookup view for data population
        TextView title = convertView.findViewById(R.id.title);
        TextView rate = convertView.findViewById(R.id.rate);
        // Populate the data into the template view using the data object
        String titleText = "Invalid entry";
        String rateText = "";
        if (currency != null) {
            titleText = currency.getCurrencyCode();
            rateText = currency.getRate();
        }
        title.setText(titleText);
        rate.setText(rateText);
        // Return the completed view to render on screen
        return convertView;
    }
}
