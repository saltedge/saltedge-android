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

import com.saltedge.sdk.model.ProviderData;
import com.saltedge.sdk.sample.R;

import java.util.List;

public class ProvidersAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<ProviderData> providersList;

    public ProvidersAdapter(Context context) {
         layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListItems(List<ProviderData> providersList) {
        this.providersList = providersList;
    }

    @Override
    public int getCount() {
        return providersList.size();
    }

    @Override
    public ProviderData getItem(int position) {
        return (position < 0 || position >= providersList.size()) ? null : providersList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = layoutInflater.inflate(R.layout.list_item_provider, parent, false);
        TextView textView = rowView.findViewById(R.id.providerTitle);
        ProviderData item = getItem(position);
        if (item != null) {
            textView.setText(getItem(position).getName());
        }
        return rowView;
    }
}
