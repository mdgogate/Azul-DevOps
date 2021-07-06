package com.sdp.appazul.activities.menuitems;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sdp.appazul.R;

public class BurgerMenuBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_burger_menu_bottomsheet, container, false);
        RelativeLayout miPerfilLayout = view.findViewById(R.id.miPerfilLayout);
        miPerfilLayout.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), MyProfile.class);
            startActivity(intent);
        });
        return view;
    }
}




