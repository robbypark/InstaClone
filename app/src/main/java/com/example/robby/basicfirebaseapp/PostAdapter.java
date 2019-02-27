package com.example.robby.basicfirebaseapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class PostAdapter extends ArrayAdapter<Map.Entry> {

    private ViewHolder viewHolder;

    private static class ViewHolder {
        private ImageView imageView;
    }

    public PostAdapter(Context context, int textViewResourceId, ArrayList<Map.Entry> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.post_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.postListImageView);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Map.Entry item = getItem(position);
        if (item!= null) {
            Map singlePost = (Map) item.getValue();

            Bitmap bitmap = ImageUtils.decodeBase64(singlePost.get("image").toString());
            viewHolder.imageView.setImageBitmap(bitmap);
        }

        return convertView;
    }
}
