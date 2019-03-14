package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;


public class FilterActivity extends CreatePostActivity {
    private Bitmap originalImage;
    private Bitmap filteredImage;

    private ImageView imageView;
    private Button blueMessButton;
    private Button starlitButton;
    private Button awestruckButton;
    private Button limeStutterButton;
    private Button nightWhisperButton;
    private Button originalButton;

    private Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        System.loadLibrary("NativeImageProcessor");

        imageView = findViewById(R.id.imageView1);
        blueMessButton = findViewById(R.id.blueMessButton);
        starlitButton = findViewById(R.id.starlitButton);
        awestruckButton = findViewById(R.id.awestruckButton);
        limeStutterButton = findViewById(R.id.limeStutterButton);
        nightWhisperButton = findViewById(R.id.nightWhisperButton);
        originalButton = findViewById(R.id.originalButton);
        submit = findViewById(R.id.filterSubmitButton);

        originalImage = ImageUtils.retreiveBitmap(FilterActivity.this);
        imageView.setImageBitmap(originalImage);

        blueMessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accessing single filter...
                Bitmap image = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                // apply filter
                Filter fooFilter = SampleFilters.getBlueMessFilter();
                filteredImage = fooFilter.processFilter(image);
                imageView.setImageBitmap(filteredImage);
            }
        });

        starlitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accessing single filter...
                Bitmap image = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                // apply filter
                Filter fooFilter = SampleFilters.getStarLitFilter();
                filteredImage = fooFilter.processFilter(image);
                imageView.setImageBitmap(filteredImage);
            }
        });

        awestruckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accessing single filter...
                Bitmap image = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                // apply filter
                Filter fooFilter = SampleFilters.getAweStruckVibeFilter();
                filteredImage = fooFilter.processFilter(image);
                imageView.setImageBitmap(filteredImage);
            }
        });

        limeStutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accessing single filter...
                Bitmap image = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                // apply filter
                Filter fooFilter = SampleFilters.getLimeStutterFilter();
                filteredImage = fooFilter.processFilter(image);
                imageView.setImageBitmap(filteredImage);
            }
        });

        nightWhisperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accessing single filter...
                Bitmap image = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                // apply filter
                Filter fooFilter = SampleFilters.getNightWhisperFilter();
                filteredImage = fooFilter.processFilter(image);
                imageView.setImageBitmap(filteredImage);
            }
        });

        originalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(originalImage);
                filteredImage = originalImage;
            }
        });

        // submit
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // assume imageview contains latest image
                if(filteredImage != null){
                    Intent returnIntent = new Intent();
                    ImageUtils.storeBitmap(FilterActivity.this, filteredImage);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }
}