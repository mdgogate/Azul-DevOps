package com.sdp.appazul.activities.menuitems;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sdp.appazul.R;
import com.sdp.appazul.classes.LoginData;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;


public class MyBusiness extends AppCompatActivity {

    TextView name;
    TextView rnc;
    TextView sector;
    LoginData loginData;
    String locationJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_business);

        Intent locationIntent = getIntent();
        locationJson = locationIntent.getStringExtra(Constants.LOCATION_RESPONSE);
        ImageView myBusinessButton;

        myBusinessButton = findViewById(R.id.myBusinessButton);
        myBusinessButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyBusiness.this, MyProfile.class);
            intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });

        name = findViewById(R.id.name);
        rnc = findViewById(R.id.rnc);
        sector = findViewById(R.id.sector);
        getLoginData();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyBusiness.this, MyProfile.class);
        intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
    }
    private void getLoginData() {
        loginData = ((AzulApplication) this.getApplication()).getLoginData();
        if (loginData.getGroupName().equals("")) name.setText("-");
        else name.setText(loginData.getGroupName());
        if(loginData.getIdentNum().equals("")) rnc.setText("-");
        else rnc.setText(loginData.getIdentNum());
        if(loginData.getGroupIndustrialSector().equals("")) sector.setText("-");
        else sector.setText(loginData.getGroupIndustrialSector());
    }

}