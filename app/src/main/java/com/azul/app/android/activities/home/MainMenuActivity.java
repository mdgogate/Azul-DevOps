package com.azul.app.android.activities.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.azul.app.android.R;
import com.azul.app.android.activities.menuitems.BurgerMenuBottomSheet;
import com.azul.app.android.activities.menuitems.WebActivity;
import com.azul.app.android.activities.registration.PinLoginActivity;
import com.azul.app.android.activities.registration.UserRegisterActivity;
import com.azul.app.android.globals.AzulApplication;
import com.azul.app.android.globals.Constants;
import com.azul.app.android.globals.GlobalFunctions;
import com.azul.app.android.security.SecurePreferences;
import com.azul.app.android.utils.NetworkAddress;

public class MainMenuActivity extends BasicRegistrationActivity {

    private Button secondLoginButton;
    LinearLayout qrFormButton;
    ImageView btnBurgerMenu;
    SharedPreferences sscPref;
    GlobalFunctions globals = new GlobalFunctions(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_two);
        initControls();
        burgerMenu();
        toConfirmPin();
        webView();
        furtherAction();
    }


    private void burgerMenu() {
        btnBurgerMenu = findViewById(R.id.burgerMenuLoginBtn);
        btnBurgerMenu.setOnClickListener(btnBurgerMenuView -> {
            BurgerMenuBottomSheet bottomsheet = new BurgerMenuBottomSheet();
            bottomsheet.show(getSupportFragmentManager(), "bottomSheet");
        });
    }

    private void toConfirmPin() {
        secondLoginButton = findViewById(R.id.secondLoginButton);
        secondLoginButton.setOnClickListener(secondLoginButtonView -> {

            String firstTime = sscPref.getString(Constants.FIRST_TIME, "");
            if (firstTime.equals("Yes")) {
                Intent intent = new Intent(MainMenuActivity.this, PinLoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainMenuActivity.this, UserRegisterActivity.class);
                startActivity(intent);
            }


        });
    }

    private void webView() {
        qrFormButton = findViewById(R.id.qrFormButton);
        qrFormButton.setOnClickListener(qrFormButtonView ->
                callWebActivity());
    }

    private void callWebActivity() {
        Intent intent = new Intent(MainMenuActivity.this, WebActivity.class);
        intent.putExtra("backActivity", "LoginTwo");
        intent.putExtra("links", new NetworkAddress().getSpecificUrl(0));
        intent.putExtra("toolbarTitleText", getResources().getString(R.string.qr_bar));
        startActivity(intent);
    }


    private void initControls() {
        secondLoginButton = findViewById(R.id.secondLoginButton);
        secondLoginButton.setTransformationMethod(null);
    }

    private void furtherAction() {
        sscPref = new SecurePreferences(this, globals.getPPRString(), globals.getSSCString());
        ((AzulApplication) this.getApplication()).setPrefs(sscPref);
    }
}
