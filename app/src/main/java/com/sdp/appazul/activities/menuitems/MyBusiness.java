package com.sdp.appazul.activities.menuitems;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.sdp.appazul.R;


public class MyBusiness extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_business);


        ImageView myBusinessButton;

        myBusinessButton = findViewById(R.id.myBusinessButton);
        myBusinessButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyBusiness.this, MyProfile.class);
            startActivity(intent);
        });

    }
}