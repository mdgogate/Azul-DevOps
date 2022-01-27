package com.sdp.appazul.activities.dashboard;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import com.sdp.appazul.BuildConfig;
import com.sdp.appazul.R;


import com.sdp.appazul.activities.home.BasicRegistrationActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.registration.UserRegisterActivity;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.SecurePreferences;


import org.json.JSONObject;

public class SplashScreenActivity extends BasicRegistrationActivity {

    ImageView gifImageView;
    SharedPreferences sscPref;
    GlobalFunctions globals = new GlobalFunctions(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        gifImageView = findViewById(R.id.GifImageView);
        Glide.with(this).asGif().load(R.raw.azul_splash_112).into(gifImageView);

//        RootBeer rootBeer = new RootBeer(getApplicationContext());
        furtherAction();
//        if (!BuildConfig.APP_TEST_MODE) {
//            if (rootBeer.isRooted()) {
//                restrictUser();
//            } else redirectLoginActivity();
//        } else
        redirectLoginActivity();


    }

    private void restrictUser() {
        gifImageView.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.warning));
        builder.setMessage(getResources().getString(R.string.device_not_compatible));
        builder.setCancelable(false);
        builder.setNegativeButton(getResources().getString(R.string.exit_lablel), (dialogInterface, i) -> System.exit(0));
        builder.show();
    }

    private void redirectLoginActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            String firstTime = sscPref.getString(Constants.FIRST_TIME, "");
            if (firstTime != null && firstTime.equals("Yes")) {
                startActivity(new Intent(this, MainMenuActivity.class));
                this.overridePendingTransition(R.anim.animation_enter,
                        R.anim.slide_nothing);
            } else {
                startActivity(new Intent(this, UserRegisterActivity.class));
                this.overridePendingTransition(R.anim.animation_enter,
                        R.anim.slide_nothing);
            }
            finish();

        }, 3000);
    }

    public void getDeregisterAPIResponse(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            String status = jsonObject.has("status") ? jsonObject.getString("status") : "";
            if (status.equalsIgnoreCase("success")) {
                ((AzulApplication) this.getApplication()).getPrefs().edit().clear().apply();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    startActivity(new Intent(this, UserRegisterActivity.class));
                    finish();
                }, 3000);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void furtherAction() {
        sscPref = new SecurePreferences(this, globals.getPPRString(), globals.getSSCString());
        ((AzulApplication) this.getApplication()).setPrefs(sscPref);
    }
}