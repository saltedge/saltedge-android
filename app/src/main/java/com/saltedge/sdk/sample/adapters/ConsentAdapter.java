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

import com.saltedge.sdk.model.SEConsent;
import com.saltedge.sdk.sample.R;
import com.saltedge.sdk.sample.utils.DateTools;

import java.util.ArrayList;
import java.util.Arrays;

public class ConsentAdapter extends ArrayAdapter<SEConsent> {

    public ConsentAdapter(Context context, ArrayList<SEConsent> consents) {
        super(context, 0, consents);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SEConsent consent = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_consent, parent, false);
        }
        // Lookup view for data population
        TextView title = convertView.findViewById(R.id.title);
        TextView subtitle = convertView.findViewById(R.id.subtitle);
        // Populate the data into the template view using the data object
        String titleText = "Invalid entry";
        String subtitleText = "";
        if (consent != null) {
            titleText = consent.getId() + (consent.getScopes() != null ? " / " + Arrays.toString(consent.getScopes()) : "");
            String fromDate = (consent.getFromDate() != null ? consent.getFromDate() : DateTools.formatDateToString(consent.getCreatedAt()));
            subtitleText = fromDate + " - " + (consent.getToDate() != null ? consent.getToDate() : "");
        }
        title.setText(titleText);
        subtitle.setText(subtitleText);
        // Return the completed view to render on screen
        return convertView;
    }
}
