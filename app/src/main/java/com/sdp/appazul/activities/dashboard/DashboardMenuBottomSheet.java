package com.sdp.appazul.activities.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.notifications.PushNotificationSettings;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.menuitems.MyProfile;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DashboardMenuBottomSheet extends BottomSheetDialogFragment {
    TextView tvLogout;
    RelativeLayout myProfileLayout;
    RelativeLayout preferenceLayout;
    String locationJson;
    Activity context;
    RelativeLayout btnLogout;

    public DashboardMenuBottomSheet(String locationJson, Activity context) {
        this.locationJson = locationJson;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_menu_bottomsheet, container, false);
        tvLogout = view.findViewById(R.id.tvLogout);
        preferenceLayout = view.findViewById(R.id.preferenciasLayout);
        myProfileLayout = view.findViewById(R.id.miPerfilLayout);
        myProfileLayout.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), MyProfile.class);
            intent.putExtra("LOCATION_RESPONSE", locationJson);
            startActivity(intent);
            context.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });
        btnLogout = view.findViewById(R.id.btnLogout);

        preferenceLayout.setOnClickListener(preferenceLayoutView -> {
            Intent intent = new Intent(getActivity(), PushNotificationSettings.class);
            intent.putExtra("LOCATION_RESPONSE", locationJson);
            startActivity(intent);
            context.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });

        tvLogout.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), MainMenuActivity.class);
            startActivity(intent);
            context.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });
        btnLogout.setOnClickListener(btnLogoutView -> {
            Intent intent = new Intent(getActivity(), MainMenuActivity.class);
            startActivity(intent);
            context.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });
        return view;
    }
}

