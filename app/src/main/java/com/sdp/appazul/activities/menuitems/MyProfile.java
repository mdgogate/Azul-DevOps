package com.sdp.appazul.activities.menuitems;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

public class MyProfile extends AppCompatActivity {
    String locationJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_profile);
        Intent locationIntent = getIntent();
        locationJson = locationIntent.getStringExtra("LOCATION_RESPONSE");

        ImageView profileBackButton;
        TextView tvMyInfo;
        TextView tvAccessData;
        TextView tvMyBuisness;
        TextView tvUnitsLocation;

        profileBackButton = findViewById(R.id.profileBackButton);
        profileBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, DashBoardActivity.class);
            intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });

        tvMyInfo = findViewById(R.id.my_info);
        tvMyInfo.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, MyInfo.class);
            intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });

        tvAccessData = findViewById(R.id.access_data);
        tvAccessData.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, AccessData.class);
            intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });

        tvMyBuisness = findViewById(R.id.my_business);
        tvMyBuisness.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, MyBusiness.class);
            intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });

        tvUnitsLocation = findViewById(R.id.units_location);
        tvUnitsLocation.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, UnitLocation.class);
            intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyProfile.this, DashBoardActivity.class);
        intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
    }
}