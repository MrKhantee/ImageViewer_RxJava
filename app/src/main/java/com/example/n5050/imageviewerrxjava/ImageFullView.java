package com.example.n5050.imageviewerrxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageFullView extends AppCompatActivity {

    private String imageUrl;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_view);
        Bundle bundle=getIntent().getExtras();
        imageUrl=bundle.getString("path");

        imageView=(ImageView) findViewById(R.id.image_fullview);
        Picasso.with(this).load(imageUrl).into(imageView);
    }
}
