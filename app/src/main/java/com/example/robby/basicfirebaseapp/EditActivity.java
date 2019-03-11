package com.example.robby.basicfirebaseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.robby.basicfirebaseapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    private EditText name;
    private CircleImageView profileImage;
    private String url;
    private DatabaseReference mDatabase;
    private FirebaseUser authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        name = (EditText) findViewById(R.id.name);
        profileImage = (CircleImageView) findViewById(R.id.mainProfileImageView);
        authUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(authUser != null){
            name.setText(authUser.getDisplayName());
            if(authUser.getPhotoUrl() != null) {
                url = authUser.getPhotoUrl().toString();
            }
            Glide.with(this).load(url).apply(new RequestOptions().placeholder(R.drawable.blue).error(R.drawable.blue)).into(profileImage);
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText et = new EditText(EditActivity.this);
                    new AlertDialog.Builder(EditActivity.this).setTitle("Input Url:")
                            .setView(et)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    url = et.getText().toString();
                                    Glide.with(EditActivity.this).load(url).apply(new RequestOptions().placeholder(R.drawable.blue).error(R.drawable.blue)).into(profileImage);
                                }
                            }).setNegativeButton("CANCEL",null).show();
                }
            });
        }
    }

    public void update(View view) {
        String names = name.getText().toString();
        if (TextUtils.isEmpty(names)) {
            Toast.makeText(this, "Please input display name.", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = new User(names, authUser.getEmail());
        mDatabase.child("users").child(authUser.getUid()).setValue(user);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(names)
                .setPhotoUri(Uri.parse(url))
                .build();
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditActivity.this, "update success.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditActivity.this, "update fail.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
