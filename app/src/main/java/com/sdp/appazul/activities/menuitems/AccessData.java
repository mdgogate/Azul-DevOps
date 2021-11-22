package com.sdp.appazul.activities.menuitems;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sdp.appazul.R;
import com.sdp.appazul.classes.LoginData;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.utils.DateUtils;

import java.io.ObjectInputStream;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AccessData extends AppCompatActivity {
    TextView lastLoginDate;
    TextView pass;
    LoginData loginData;
    String locationJson;
    List<String> monthList = new ArrayList<>();
    DateUtils dateUtils = new DateUtils();
    List<String> daysList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_access_data);
        ImageView accessDataBackButton;
        Intent locationIntent = getIntent();
        locationJson = locationIntent.getStringExtra(Constants.LOCATION_RESPONSE);

        accessDataBackButton = findViewById(R.id.accessDataBackButton);
        accessDataBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccessData.this, MyProfile.class);
            intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });

        lastLoginDate = findViewById(R.id.lastLoginDate);
        pass = findViewById(R.id.pass);

        getLoginData();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AccessData.this, MyProfile.class);
        intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
    }

    private void getLoginData() {
        loginData = ((AzulApplication) this.getApplication()).getLoginData();
        pass.setText("********");
        if(loginData.getLastLoginDate().equals(""))
        {
            lastLoginDate.setText("-");
        }else {
            String[] splitString = loginData.getLastLoginDate().split(" ");
            String date = splitString[0];
            String time = splitString[1];
            lastLoginDate.setText(dateFormatConvertor(date) + " " + timeFormatConvertor(time));
        }
    }

    private String timeFormatConvertor(String inputTime) {
        try {
            DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
            symbols.setAmPmStrings(new String[]{"a.m.", "p.m."});
            SimpleDateFormat sdfFormat24 = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdfFormat12 = new SimpleDateFormat("hh:mm a");
            sdfFormat12.setDateFormatSymbols(symbols);
            Date datefor24hr = sdfFormat24.parse(inputTime);
            return sdfFormat12.format(datefor24hr);
        } catch (final ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return null;
    }

    private String dateFormatConvertor(String date)
    {
        String[] splitString = date.split("-");
        String day = splitString[2];
        String month = splitString[1];
        String year = splitString[0];
        return getDayFromDate(date)+" "+day+" "+getMonth(month)+", "+year;
    }

    private String getDayFromDate(String date) {
        try {
            daysList = dateUtils.getDaysList();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
            Date myDate = sdf.parse(date);
            sdf.applyPattern("EEEE, d MMM yyyy");
            return daysList.get(myDate.getDay());
        }
        catch (Exception e)
        {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return "";
    }

    private String getMonth(String date)
    {
        switch (date)
        {
            case "01":
            case "1":return "enero";
            case "02":
            case "2":return "febrero";
            case "03":
            case "3":return "marzo";
            case "04":
            case "4":return "abril";
            case "05":
            case "5":return "mayo";
            case "06":
            case "6":return "junio";
            case "07":
            case "7":return "julio";
            case "08":
            case "8":return "agosto";
            case "09":
            case "9":return "septiembre";
            case "10":return "octubre";
            case "11":return "noviembre";
            case "12":return "diciembre";
            default:return "";

        }
    }

}