package com.example.robby.basicfirebaseapp;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private ArrayList<Post> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        public Button mLikeButton;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.feedTitleTextView);
            mImageView = v.findViewById(R.id.feedImageView);
            mLikeButton = v.findViewById(R.id.likeButton);
        }
    }

    public PostAdapter(ArrayList<Post> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsfeed_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Post post = mDataset.get(position);
        if (post != null) {
            holder.mTextView.setText(post.getTitle());
            Bitmap bitmap = ImageUtils.decodeBase64(post.getImage());
            holder.mImageView.setImageBitmap(bitmap);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addPost(Post post) {
        mDataset.add(0, post);
        notifyDataSetChanged();
    }
}