package com.sdp.appazul.activities.menuitems;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.sdp.appazul.R;


public class AccessData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_access_data);
        ImageView accessDataBackButton;

        accessDataBackButton = findViewById(R.id.accessDataBackButton);
        accessDataBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccessData.this, MyProfile.class);
            startActivity(intent);
        });

    }
}