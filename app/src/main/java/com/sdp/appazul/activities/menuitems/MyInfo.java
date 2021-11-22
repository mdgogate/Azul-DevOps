package com.sdp.appazul.activities.menuitems;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.sdp.appazul.R;
import com.sdp.appazul.classes.LoginData;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class MyInfo extends AppCompatActivity {

    ImageView infoBackButton;
    LoginData loginData;
    TextView un;
    TextView nameLastName;
    TextView cedulaNumber;
    TextView dob;
    TextView occupation;
    TextView telephone;
    TextView cellPhone;
    TextView email;
    TextView role;
    String locationJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_info);
        Intent locationIntent = getIntent();
        locationJson = locationIntent.getStringExtra(Constants.LOCATION_RESPONSE);
        initComponent();
        initComponentOnclick();
        getLoginData();
    }


    private void initComponent() {

        un = findViewById(R.id.un);
        nameLastName = findViewById(R.id.name_lastName);
        cedulaNumber = findViewById(R.id.cedulaNumber);
        dob = findViewById(R.id.dob);
        occupation = findViewById(R.id.occupation);
        telephone = findViewById(R.id.telephone);
        cellPhone = findViewById(R.id.cellPhone);
        email = findViewById(R.id.email);
        role = findViewById(R.id.role);
        infoBackButton = findViewById(R.id.infoBackButton);
    }

    private void initComponentOnclick() {
        infoBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyInfo.this, MyProfile.class);
            intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });
    }

    private void getLoginData() {
        loginData = ((AzulApplication) this.getApplication()).getLoginData();

        checkValidationPartOne();

        if (loginData.getBirthDate().equals("")) {
            dob.setText("-");
        } else {
            String strDate1 = loginData.getBirthDate().substring(0, loginData.getBirthDate().indexOf(" "));
            dob.setText(getDateFormatted(strDate1));
        }

        if (loginData.getOccupation().equals("")) occupation.setText("-");
        else occupation.setText(loginData.getOccupation());
        if (loginData.getPhone().equals("")) telephone.setText("-");
        else telephone.setText(loginData.getPhone());
        if (loginData.getCellPhone().length() == 0) cellPhone.setText("-");
        else cellPhone.setText(loginData.getCellPhone());
        if (loginData.getEmail().equals("")) email.setText("-");
        else email.setText(loginData.getEmail());
        if (loginData.getRole().equals("")) role.setText("");
        else role.setText(loginData.getRole());
    }

    private void checkValidationPartOne() {
        if (loginData.getUn().equals("")) {
            un.setText("-");
        } else {
            un.setText(loginData.getUn());
        }
        if (loginData.getName().equals("") && loginData.getLastName().equals("")) {
            nameLastName.setText("-");
        } else {
            nameLastName.setText(loginData.getName() + " " + loginData.getLastName());
        }
        if (loginData.getIdentNum().equals("")) {
            cedulaNumber.setText("-");
        } else {
            cedulaNumber.setText(loginData.getIdentNum());
        }
    }

    public String getDateFormatted(String inputStringDate) {
        try {
            String[] splitString = inputStringDate.split("-");
            String finalString = splitString[2] + "/" + splitString[1] + "/" + splitString[0];
            return finalString;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyInfo.this, MyProfile.class);
        intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
    }
}