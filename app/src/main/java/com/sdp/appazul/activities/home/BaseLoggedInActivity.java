package com.sdp.appazul.activities.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.sdp.appazul.activities.registration.PinLoginActivity;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.security.SecurePreferences;
import com.sdp.appazul.globals.AzulApplication;

public class BaseLoggedInActivity extends AppCompatActivity {

    SharedPreferences sscPrefMain;
    public static boolean isAppInFg = false;
    public static boolean isScrInFg = false;
    public static boolean isChangeScrFg = false;
    public GlobalFunctions globalFunctions = new GlobalFunctions(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sscPrefMain = ((AzulApplication) this.getApplication()).getPrefs();

        if (sscPrefMain == null) {
            sscPrefMain = new SecurePreferences(this,	globalFunctions.getPPRString(),
                    globalFunctions.getSSCString());
            ((AzulApplication) this.getApplication()).setPrefs(sscPrefMain);
            Intent intent = new Intent(this, PinLoginActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    /**
     * set flag for starting activity
     */
    public void startActivitywithIntent(Intent i) {
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    /**
     * check flag isStop for avoiding to start timer onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * check flag isStop for avoiding to start timer onStop
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (!isScrInFg || !isChangeScrFg) {
            isAppInFg = false;
            //Background code - you can write function here
        }
        isScrInFg = false;
    }

    @Override
    protected void onStart() {
        if (!isAppInFg) {
            isAppInFg = true;
            isChangeScrFg = false;
            //Foreground code - you can write function here
        } else {
            isChangeScrFg = true;
        }
        isScrInFg = true;
        super.onStart();
    }
}
