package com.sdp.appazul.activities.menuitems;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.home.BasicRegistrationActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class UnitsLocation extends BasicRegistrationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_units_location);

        ImageView unitsLocationBackButton;

        unitsLocationBackButton = findViewById(R.id.unitsLocationBackButton);
        unitsLocationBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(UnitsLocation.this, MyProfile.class);
            startActivity(intent);

        });
    }

    boolean submenuVisibility;
    ScrollView subMenu;
    ImageView arrowUp;
    public void firstBusinessClicked(View view) {
        if (view!=null){
            Log.d("Data","");
        }
        subMenu = findViewById(R.id.subMenu);
        arrowUp = findViewById(R.id.downArrow1);
        submenuVisibility = !submenuVisibility;

        subMenu.setVisibility(submenuVisibility ? VISIBLE: GONE);
        arrowUp.setRotation(submenuVisibility ? 180:0);

    }

    boolean visible2;
    public void secondBusinessClicked(View view) {
        if (view!=null){
            Log.d("Data","");
        }
        subMenu = findViewById(R.id.subMenu1);
        arrowUp = findViewById(R.id.downArrow2);
        visible2 = !visible2;

        subMenu.setVisibility(visible2 ? VISIBLE: GONE);
        arrowUp.setRotation(visible2 ? 180:0);
    }

    boolean visible3;
    public void thirdBusinessClicked(View view) {
        if (view!=null){
            Log.d("Data","");
        }
        subMenu = findViewById(R.id.subMenu2);
        arrowUp = findViewById(R.id.downArrow3);
        visible3 = !visible3;

        subMenu.setVisibility(visible3 ? VISIBLE: GONE);
        arrowUp.setRotation(visible3 ? 180:0);
    }
}