package com.example.robby.basicfirebaseapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class EntryAdapter extends ArrayAdapter<Map.Entry> {

    private ViewHolder viewHolder;

    private static class ViewHolder {
        private TextView nameTextView;
        private ImageView imageView;
    }

    public EntryAdapter(Context context, int textViewResourceId, ArrayList<Map.Entry> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.map_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.mapItemTextView);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.mapItemImageView);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Map.Entry item = getItem(position);
        if (item!= null) {
            Map singleUser = (Map) item.getValue();
            viewHolder.nameTextView.setText((String) singleUser.get("username"));
            String imageString = (String) singleUser.get("image");
            if(imageString != null){
                Bitmap bitmap = ImageUtils.decodeBase64(imageString);
                viewHolder.imageView.setImageBitmap(bitmap);
            }
        }

        return convertView;
    }
}
