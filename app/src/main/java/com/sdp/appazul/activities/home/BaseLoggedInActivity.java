package com.sdp.appazul.activities.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.registration.PinLoginActivity;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.security.SecurePreferences;
import com.sdp.appazul.globals.AzulApplication;

public class BaseLoggedInActivity extends AppCompatActivity {

    SharedPreferences sscPrefMain;
    boolean isAppInFg = false;
    boolean isScrInFg = false;
    boolean isChangeScrFg = false;
    GlobalFunctions globalFunctions = new GlobalFunctions(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sscPrefMain = ((AzulApplication) this.getApplication()).getPrefs();

        if (sscPrefMain == null) {
            sscPrefMain = new SecurePreferences(this, globalFunctions.getPPRString(),
                    globalFunctions.getSSCString());
            ((AzulApplication) this.getApplication()).setPrefs(sscPrefMain);
            Intent intent = new Intent(this, PinLoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        }
    }

    public void startActivityWithIntent(Intent i) {
        startActivity(i);
        overridePendingTransition(1, 1);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!isScrInFg || !isChangeScrFg) {
            isAppInFg = false;
        }
        isScrInFg = false;
    }

    @Override
    protected void onStart() {
        if (!isAppInFg) {
            isAppInFg = true;
            isChangeScrFg = false;
        } else {
            isChangeScrFg = true;
        }
        isScrInFg = true;
        super.onStart();
    }
}
