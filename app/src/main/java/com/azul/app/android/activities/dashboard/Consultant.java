package com.azul.app.android.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.azul.app.android.R;
import com.azul.app.android.activities.transactions.QrTransactions;
import com.azul.app.android.activities.transactions.SettledTransactionsQuery;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class Consultant extends BottomSheetDialogFragment {

    TextView tvLiquidatedTrans;
    TextView tvQrTransactions;
    String loc;

    public Consultant() {
    }

    public Consultant(String locationJson) {
        this.loc = locationJson;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.activity_consultant_bottomsheet, container, false);

        initControls(view);


        return view;
    }

    private void initControls(View view) {

        tvLiquidatedTrans = view.findViewById(R.id.tvLiquidatedTrans);
        tvQrTransactions = view.findViewById(R.id.tvQrTransactions);

        tvQrTransactions.setOnClickListener(tvQrTransactionsView ->
                openQrTransactionScreen());
        tvLiquidatedTrans.setOnClickListener(tvLiquidatedTransView -> openSettledTransactionScreen());

    }

    private void openQrTransactionScreen() {
        Intent intent = new Intent(getActivity(), QrTransactions.class);
        intent.putExtra("LOCATION_RESPONSE",loc);
        startActivity(intent);
    }

    private void openSettledTransactionScreen() {
        Intent intent = new Intent(getActivity(), SettledTransactionsQuery.class);
        intent.putExtra("LOCATION_RESPONSE", loc);
        startActivity(intent);
    }
}