package com.example.robby.basicfirebaseapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private ViewHolder viewHolder;
    private DatabaseReference mDatabase;
    private Map<String, User> users;


    private static class ViewHolder {
        private TextView nameTextView;
        private TextView commentTextView;
    }

    public CommentAdapter(Context context, int textViewResourceId, ArrayList<Comment> items) {
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
                    .inflate(R.layout.comment_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.commentUserTextView);
            viewHolder.commentTextView = (TextView) convertView.findViewById(R.id.commentTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Comment item = getItem(position);
        if (item!= null) {
            viewHolder.commentTextView.setText(item.getComment());
            User user = users.get(item.getUid());
            viewHolder.nameTextView.setText(user.getUsername());
        }

        return convertView;
    }
}
