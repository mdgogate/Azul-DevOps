package com.sdp.appazul.activities.TapOnPhone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sdp.appazul.R;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

public class CardlessAnimationScreen extends AppCompatActivity {

    ImageView gifImageView;
    String totalAmountEntered;
    String selectedLocation;
    String locationJson;
    String option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardless_animation_screen);
        Intent intent = getIntent();
        totalAmountEntered = intent.getStringExtra(Constants.TOTAL_AMOUNT);
        selectedLocation = intent.getStringExtra(Constants.LOCATION_NAME_SELECTED);
        option = intent.getStringExtra("SCREEN_TYPE");
        locationJson = ((AzulApplication) this.getApplicationContext()).getLocationDataShare();

        gifImageView = findViewById(R.id.GifImageView);
        Glide.with(this).asGif().load(R.raw.loading_animation).into(gifImageView);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callNextScreen();
            }
        }, 3000);
    }

    private void callNextScreen() {
//        Intent intent = new Intent(this, SaleReceiptActivity.class);
//        intent.putExtra(Constants.TOTAL_AMOUNT, totalAmountEntered);
//        intent.putExtra(Constants.LOCATION_NAME_SELECTED, selectedLocation);
//        intent.putExtra("SCREEN_TYPE", option);
//        startActivity(intent);
//        ((AzulApplication) ((CardlessAnimationScreen) this).getApplication()).setLocationDataShare(locationJson);
//
//        this.overridePendingTransition(R.anim.animation_enter,
//                R.anim.slide_nothing);
    }
}