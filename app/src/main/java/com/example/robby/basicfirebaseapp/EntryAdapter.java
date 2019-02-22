package com.example.robby.basicfirebaseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class EntryAdapter extends ArrayAdapter<Map.Entry> {

    private ViewHolder viewHolder;
    private String value;

    private static class ViewHolder {
        private TextView nameTextView;
    }

    public EntryAdapter(Context context, int textViewResourceId, ArrayList<Map.Entry> items, String value) {
        super(context, textViewResourceId, items);
        this.value = value;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.map_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.label);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Map.Entry item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            Map singleUser = (Map) item.getValue();
            viewHolder.nameTextView.setText((String) singleUser.get(value));
        }

        return convertView;
    }
}
