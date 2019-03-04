package com.example.robby.basicfirebaseapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robby.basicfirebaseapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FeedAdapter extends ArrayAdapter<Map.Entry> {

    private ViewHolder viewHolder;

    private DatabaseReference mDatabase;
    private Map<String, User> users;

    private static class ViewHolder {
        private TextView titleTextView;
        private ImageView imageView;
        private TextView dateTextView;
        private TextView usernameTextView;
    }

    public FeedAdapter(Context context, int textViewResourceId, ArrayList<Map.Entry> items) {
        super(context, textViewResourceId, items);

        users = new HashMap<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    users.put(child.getKey(), child.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.newsfeed_view, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.feedTitleTextView);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.feedImageView);
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.feedDateTextView);
            viewHolder.usernameTextView = (TextView) convertView.findViewById(R.id.feedUsernameTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Map.Entry item = getItem(position);
        if (item!= null) {
            Map singlePost = (Map) item.getValue();

            // title
            viewHolder.titleTextView.setText(singlePost.get("title").toString());

            // image
            Bitmap bitmap = ImageUtils.decodeBase64(singlePost.get("image").toString());
            viewHolder.imageView.setImageBitmap(bitmap);

            // time
            long yourmilliseconds = (long) singlePost.get("time");
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
            Date resultdate = new Date(yourmilliseconds);
            viewHolder.dateTextView.setText(sdf.format(resultdate));

            // user id
            String uid = (String) singlePost.get("uid");
            String username = users.get(uid).getUsername();
            viewHolder.usernameTextView.setText(username);
        }

        return convertView;
    }
}
