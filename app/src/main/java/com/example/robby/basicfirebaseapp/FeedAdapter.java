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

public class FeedAdapter extends ArrayAdapter<Map.Entry> {

    private ViewHolder viewHolder;

    private static class ViewHolder {
        private TextView titleTextView;
        private ImageView imageView;
        private TextView dateTextView;
    }

    public FeedAdapter(Context context, int textViewResourceId, ArrayList<Map.Entry> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.newsfeed_view, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.feedTitleTextView);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.feedImageView);
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.feedDateTextView);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Map.Entry item = getItem(position);
        if (item!= null) {
            Map singlePost = (Map) item.getValue();

            viewHolder.titleTextView.setText(singlePost.get("title").toString());

            Bitmap bitmap = ImageUtils.decodeBase64(singlePost.get("image").toString());
            viewHolder.imageView.setImageBitmap(bitmap);

            long yourmilliseconds = (long) singlePost.get("time");
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
            Date resultdate = new Date(yourmilliseconds);
            viewHolder.dateTextView.setText(sdf.format(resultdate));
        }

        return convertView;
    }
}
