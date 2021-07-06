package com.sdp.appazul.activities.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONObject;

public class PushNotificationSettings extends AppCompatActivity {
    private String locationJson;
    private ImageView notificationSwitch;
    ImageView backButton;
    private boolean buttonOn = false;
    String pushToken;
    ApiManager apiManager = new ApiManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification_settings);
        Intent locationIntent = getIntent();
        locationJson = locationIntent.getStringExtra("LOCATION_RESPONSE");
        getPushToken();
        initControls();
        notificationUpdate();
    }

    private void getPushToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        pushToken = task.getResult();
                        Log.d("TAG", "FCM TOKEN :::  " + pushToken);
                    }
                });
    }

    private void initControls() {
        notificationSwitch = findViewById(R.id.notificationSwitch);
        backButton = findViewById(R.id.backButton);


        notificationSwitch.setOnClickListener(notificationSwitchView -> {

            if (!buttonOn) {
                buttonOn = true;
                openAppSettings();
            } else {
                buttonOn = true;
                openAppSettings();
            }
            notificationSwitch.setImageResource(R.drawable.switch_on_state);
        });
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(PushNotificationSettings.this, DashBoardActivity.class);
            intent.putExtra("LOCATION_RESPONSE", locationJson);
            startActivity(intent);
        });

    }

    public void openAppSettings() {

        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPushToken();
        callApiPushToken();
        notificationUpdate();
    }

    public void notificationUpdate() {
        boolean notificationStatus = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (notificationStatus) {
            notificationSwitch.setImageResource(R.drawable.switch_on_state);
        } else {
            notificationSwitch.setImageResource(R.drawable.switch_off_state);
        }
    }

    String status;

    public void callApiPushToken() {
        JSONObject json = new JSONObject();
        try {
            boolean notificationStatus = NotificationManagerCompat.from(this).areNotificationsEnabled();
            if (notificationStatus) {
                status = "true";
            } else {
                status = "false";
            }
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            json.put("tcp", RSAHelper.ecryptRSA(PushNotificationSettings.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(this));
            JSONObject pagosrequest = new JSONObject();
            pagosrequest.put("token", pushToken);
            pagosrequest.put("notify", status);

            payload.put("obtenerrequest", pagosrequest);
            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0)));

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.PUSH_TOKEN, json);
    }

    public void pushTokenResponse(String responseString) {
        Log.d("Data", "" + responseString);

    }
}