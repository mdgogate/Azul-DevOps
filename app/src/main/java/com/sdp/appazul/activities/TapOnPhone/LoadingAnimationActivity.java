package com.sdp.appazul.activities.TapOnPhone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.classes.TapOnPhone;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

import java.util.Map;

import digital.paynetics.phos.PhosSdk;
import digital.paynetics.phos.exceptions.PhosException;
import digital.paynetics.phos.sdk.callback.AuthCallback;
import digital.paynetics.phos.sdk.callback.InitCallback;

public class LoadingAnimationActivity extends AppCompatActivity {
    ImageView gifImageView;
    TextView tvAnimationText;
    String locationJson;
    RelativeLayout mainlayoutId;
    int option = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_animation);

        Intent intent = getIntent();
        option = intent.getIntExtra("OPTION", 0);

        locationJson = ((AzulApplication) this.getApplicationContext()).getLocationDataShare();

        mainlayoutId = findViewById(R.id.mainlayoutId);
        tvAnimationText = findViewById(R.id.tvAnimationText);

        gifImageView = findViewById(R.id.GifImageView);
        Glide.with(this).asGif().load(R.raw.loading_animation).into(gifImageView);
        initPhosSdk();
    }

    String TAG = "PHOS_SDK";


    private void initPhosSdk() {
        Log.d(TAG, "P H O S Initializing..");

        PhosSdk.getInstance().init(getApplicationContext(), new InitCallback() {
            @Override
            public void onSuccess(Void unused, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                PhosAuthentication();
            }

            @Override
            public void onFailure(PhosException e, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                OpenCancelConfirmationDialog(option, "Validación de seguridad no completada", "Inténtalo de nuevo");
            }
        });
    }

    private void PhosAuthentication() {

        String issuer = "azul";
        String license = "Test123";
        String token = "Test123";

        PhosSdk.getInstance().authenticate(issuer, license, token, new AuthCallback() {
            @Override
            public void onSuccess(Void unused, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                callNextScreen();
            }

            @Override
            public void onFailure(PhosException e, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                OpenCancelConfirmationDialog(option, "Validación de seguridad no completada", "Inténtalo de nuevo");
            }
        });
    }

    private void callNextScreen() {
        Intent intent = null;
        if (option == 1) {
            intent = new Intent(this, TapTransactions.class);
        } else {
            intent = new Intent(this, PhosCalculatorScreen.class);
        }

        startActivity(intent);
        ((AzulApplication) ((LoadingAnimationActivity) this).getApplication()).setLocationDataShare(locationJson);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }

    Dialog confirmationDialog;

    private void OpenCancelConfirmationDialog(int option, String messageName, String buttonText) {
        confirmationDialog = new Dialog(this);
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDialog.setCancelable(true);
        confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        confirmationDialog.setContentView(R.layout.retry_validation_for_top);
        RelativeLayout btnCancelPayment = confirmationDialog.findViewById(R.id.btnCancelPayment);
        ImageView btnCloaseCancelDialog = confirmationDialog.findViewById(R.id.btnCloaseCancelDialog);
        TextView tvButtonText = confirmationDialog.findViewById(R.id.tvButtonText);
        TextView tvCancelAlertText = confirmationDialog.findViewById(R.id.tvCancelAlertText);
        tvCancelAlertText.setText(messageName);
        tvButtonText.setText(buttonText);

        btnCancelPayment.setOnClickListener(btnCancelPaymentView -> {
            if (confirmationDialog != null && confirmationDialog.isShowing()) {
                confirmationDialog.dismiss();
            }
            initPhosSdk();
        });
        btnCloaseCancelDialog.setOnClickListener(btnCloaseCancelDialogView -> {
            if (confirmationDialog != null && confirmationDialog.isShowing()) {
                confirmationDialog.dismiss();
            }
            Intent intent = null;
            if (option == 1) {
                intent = new Intent(this, DashBoardActivity.class);
            } else {
                intent = new Intent(this, MainMenuActivity.class);
            }
            ((AzulApplication) ((LoadingAnimationActivity) this).getApplication()).setLocationDataShare(locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });
        confirmationDialog.show();
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            doSomethingOnNetworkChange(ni, intent);
        }
    };

    private void doSomethingOnNetworkChange(NetworkInfo ni, Intent intent) {
        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            Log.d("TAG", "NETWORK CONNECTED... ");
            if (cancelAlertDialog != null && cancelAlertDialog.isShowing()) {
                cancelAlertDialog.dismiss();
            }
        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            cancelAlert(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    Dialog cancelAlertDialog;

    public void cancelAlert(Context activity) {
        try {
            cancelAlertDialog = new Dialog(activity);
            cancelAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cancelAlertDialog.setCancelable(false);
            cancelAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            cancelAlertDialog.setContentView(R.layout.cancel_dialog_layout);
            TextView textTitle = cancelAlertDialog.findViewById(R.id.textTitle);
            TextView textMsg = cancelAlertDialog.findViewById(R.id.textMsg);
            LinearLayout buttonsLayout = cancelAlertDialog.findViewById(R.id.buttonsLayout);
            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");
            textTitle.setTypeface(typeface);
            textTitle.setText(getResources().getString(R.string.Network_lbl));
            textMsg.setTypeface(typeface);
            textMsg.setText(getResources().getString(R.string.internet_connection_status));
            TextView btnNo = cancelAlertDialog.findViewById(R.id.btnNo);
            TextView btnYesHide = cancelAlertDialog.findViewById(R.id.btnYesHide);
            btnYesHide.setTypeface(typeface);
            btnNo.setVisibility(View.GONE);
            buttonsLayout.setVisibility(View.GONE);
            btnYesHide.setVisibility(View.VISIBLE);
            btnYesHide.setOnClickListener(btnYesHideView -> {
                callBackScreen();
            });

            cancelAlertDialog.show();

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void callBackScreen() {
        Intent setIntent;
        setIntent = new Intent(this, MainMenuActivity.class);
        startActivity(setIntent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
    }
}