package com.sdp.appazul.activities.menuitems;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.sdp.appazul.R;


public class MyInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_info);

        ImageView infoBackButton;

        infoBackButton = findViewById(R.id.infoBackButton);
        infoBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyInfo.this, MyProfile.class);
            startActivity(intent);
        });
    }
}