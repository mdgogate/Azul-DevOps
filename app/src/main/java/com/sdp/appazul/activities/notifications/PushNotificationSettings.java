package com.sdp.appazul.activities.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
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
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification_settings);
        Intent locationIntent = getIntent();
        locationJson = locationIntent.getStringExtra(Constants.LOCATION_RESPONSE);
        initControls();
        notificationUpdate();
    }

    private void getPushToken() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            pushToken = task.getResult();
            Log.d("TAG", "onResume:11 " + pushToken);
            callApiPushToken(pushToken);
        });
    }

    private void initControls() {
        notificationSwitch = findViewById(R.id.notificationSwitch);
        backButton = findViewById(R.id.backButton);

        notificationSwitch.setOnClickListener(notificationSwitchView -> {

            if (Boolean.FALSE.equals(buttonOn)) {
                buttonOn = true;
            } else {
                buttonOn = false;
            }
            openAppSettings();
            notificationSwitch.setImageResource(R.drawable.switch_on_state);
        });
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(PushNotificationSettings.this, DashBoardActivity.class);
            intent.putExtra("LOCATION_RESPONSE", locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
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


    public void callApiPushToken(String tokenData) {
        JSONObject json = new JSONObject();
        try {
            boolean notificationStatus = NotificationManagerCompat.from(this).areNotificationsEnabled();
            if (notificationStatus) {
                status = "true";
            } else {
                status = "false";
            }
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) this.getApplication()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(PushNotificationSettings.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(this));
            payload.put("token", tokenData);
            payload.put("notify", status);

            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));

            Log.d("PinSetActivity", "callApiPushToken: "+payload.toString());

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.PUSH_TOKEN, json);
    }

    public void pushTokenResponse(String responseString) {
        Log.d("Data", "" + responseString);
    }
}