package com.sdp.appazul.activities.dashboard;

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

public class DashboardMenuBottomsheet extends BottomSheetDialogFragment {
    TextView tvLogout;
    RelativeLayout miPerfilLayout;
    RelativeLayout preferenciasLayout;
    String locationJson;

    public DashboardMenuBottomsheet(String locationJson) {
        this.locationJson = locationJson;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_menu_bottomsheet, container, false);
        tvLogout = view.findViewById(R.id.tvLogout);
        preferenciasLayout = view.findViewById(R.id.preferenciasLayout);
        miPerfilLayout = view.findViewById(R.id.miPerfilLayout);
        miPerfilLayout.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), MyProfile.class);
            intent.putExtra("LOCATION_RESPONSE", locationJson);
            startActivity(intent);
        });

        preferenciasLayout.setOnClickListener(preferenciasLayoutView -> {
            Intent intent = new Intent(getActivity(), PushNotificationSettings.class);
            intent.putExtra("LOCATION_RESPONSE", locationJson);
            startActivity(intent);
        });

        tvLogout.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), MainMenuActivity.class);
            startActivity(intent);
        });
        return view;
    }
}

