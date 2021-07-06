package com.sdp.appazul.activities.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdp.appazul.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PaymentLinkBottomSheet extends BottomSheetDialogFragment {
    TextView tvGenerateLink;
    String locationJson;

    public PaymentLinkBottomSheet(String locationData) {
        this.locationJson = locationData;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.payment_link_bottom_sheet, container, false);

        tvGenerateLink = view.findViewById(R.id.tvGenerateLink);
        tvGenerateLink.setOnClickListener(tvGenerateLinkView -> openGenerateLinkScreen(locationJson));

        return view;
    }

    private void openGenerateLinkScreen(String locationData) {
        Intent intent = new Intent(getActivity(), SetPaymentInfoActivity.class);
        intent.putExtra("LOCATION_RESPONSE",locationData);
        startActivity(intent);
    }


}
