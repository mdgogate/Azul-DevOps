package com.azul.app.android.activities.menuitems;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.azul.app.android.R;
import com.azul.app.android.activities.home.MainMenuActivity;


public class MyProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_profile);

        ImageView profileBackButton;
        TextView tvMyInfo;
        TextView tvAccessData;
        TextView tvMyBuisness;
        TextView tvUnitsLocation;

        profileBackButton = findViewById(R.id.profileBackButton);
        profileBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, MainMenuActivity.class);
            startActivity(intent);
        });

        tvMyInfo = findViewById(R.id.my_info);
        tvMyInfo.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, MyInfo.class);
            startActivity(intent);
        });

        tvAccessData = findViewById(R.id.access_data);
        tvAccessData.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, AccessData.class);
            startActivity(intent);
        });

        tvMyBuisness = findViewById(R.id.my_business);
        tvMyBuisness.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, MyBusiness.class);
            startActivity(intent);
        });

        tvUnitsLocation = findViewById(R.id.units_location);
        tvUnitsLocation.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, UnitsLocation.class);
            startActivity(intent);
        });


    }
}