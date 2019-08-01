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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.saltedge.sdk.model.SEProvider;
import com.saltedge.sdk.sample.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProvidersAdapter extends ArrayAdapter<SEProvider> implements Filterable {

    private ArrayList<SEProvider> allAproviders = new ArrayList<>();

    public ProvidersAdapter(Context context, ArrayList<SEProvider> providers) {
        super(context, 0, providers);
        allAproviders = new ArrayList<>(providers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SEProvider item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_provider, parent, false);
        }
        // Lookup view for data population
        ImageView logo = convertView.findViewById(R.id.logo);
        TextView title = convertView.findViewById(R.id.title);
        // Populate the data into the template view using the data object
        String logoUrl = item != null ? item.getLogoUrl() : "";
        Picasso.get().load(logoUrl).fit().centerCrop().error(R.drawable.ic_bank_24dp).placeholder(R.drawable.ic_bank_24dp).into(logo);
        title.setText(item != null ? item.getName() : "Invalid entry");
        // Return the completed view to render on screen
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<SEProvider> filteredProviders = new ArrayList<>();
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filteredProviders = allAproviders;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (SEProvider provider : allAproviders) {
                        if (provider.getName().toLowerCase().contains(constraint.toString())) {
                            filteredProviders.add(provider);
                        }
                    }
                }
                results.count = filteredProviders.size();
                results.values = filteredProviders;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<SEProvider> collection = (List<SEProvider>) results.values;
                clear();
                addAll(collection);
            }
        };
    }
}
