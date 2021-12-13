package com.sdp.appazul.activities.dashboard;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdp.appazul.R;

import com.sdp.appazul.activities.menuitems.WebActivity;
import com.sdp.appazul.activities.transactions.QrTransactions;
import com.sdp.appazul.activities.transactions.SettledTransactionsQuery;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.utils.NetworkAddress;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class Consultant extends BottomSheetDialogFragment {

    TextView tvLiquidatedTrans;
    TextView tvQrTransactions;
    String loc;
    BottomSheetCloseEvent bottomSheetCloseEvent;
    List<String> permissionList;
    List<String> productPermissionList;
    Dialog dialog;

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

        tvQrTransactions.setOnClickListener(tvQrTransactionsView -> {
            if (productPermissionList != null && !productPermissionList.isEmpty()) {
                openQrTransactionScreen();
            } else {
                AppAlters.errorAlert(getActivity(), 4);
            }
        });

        tvLiquidatedTrans.setOnClickListener(tvLiquidatedTransView -> {
            if (permissionList != null && !permissionList.isEmpty()) {
                openSettledTransactionScreen();
            } else {
                AppAlters.errorAlert(getActivity(), 4);
            }
        });

        permissionList = new ArrayList<>();
        productPermissionList = new ArrayList<>();

        permissionList = ((AzulApplication) getActivity().getApplication()).getFeaturePermissionsList();
        productPermissionList = ((AzulApplication) getActivity().getApplication()).getProductPermissionsList();

    }

    private void openQrTransactionScreen() {
        if (productPermissionList.contains("HasQR")) {
            if (permissionList != null && !permissionList.isEmpty()) {
                if (permissionList.contains("QRQueryTransactions")) {
                    Intent intent = new Intent(getActivity(), QrTransactions.class);
                    intent.putExtra("LOCATION_RESPONSE", loc);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.animation_enter,
                            R.anim.slide_nothing);
                } else {
                    errorAlert(getActivity(), 3);
                }
            } else {
                AppAlters.errorAlert(getActivity(), 4);
            }
        } else {
            errorAlert(getActivity(), 0);
        }
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        bottomSheetCloseEvent.onClosed(1);
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        bottomSheetCloseEvent.onClosed(1);
        super.onDestroy();
    }

    private void openSettledTransactionScreen() {
        if (permissionList.contains("QueryTransactions")) {
            Intent intent = new Intent(getActivity(), SettledTransactionsQuery.class);
            intent.putExtra(Constants.LOCATION_RESPONSE, loc);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        } else {
            errorAlert(getActivity(), 3);
        }
    }

    public void setBottomSheetCloseEvent(BottomSheetCloseEvent bottomSheetCloseEvent) {
        this.bottomSheetCloseEvent = bottomSheetCloseEvent;
    }

    public interface BottomSheetCloseEvent {
        void onClosed(int text);
    }


    public void errorAlert(Context activity, int errorType) {
        try {

            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.feature_permission_dialog);
            TextView textTitle = dialog.findViewById(R.id.textTitle);
            TextView textMsg = dialog.findViewById(R.id.textMsg);
            TextView btnCancel = dialog.findViewById(R.id.btnCancel);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");

            btnCancel.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            textTitle.setTypeface(typeface);

            if (errorType == 3) {
                textTitle.setText(getResources().getString(R.string.no_access_for_functionality));
                textMsg.setText(getResources().getString(R.string.no_access_for_functionality_msg));
                btnCancel.setVisibility(View.INVISIBLE);
            } else {
                btnCancel.setVisibility(View.VISIBLE);
                textTitle.setText(getResources().getString(R.string.not_permitted_title));
                textMsg.setText(getResources().getString(R.string.get_blue_qr_here));

            }


            TextView btnContinue = dialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);

            btnContinue.setOnClickListener(view -> {
                if (errorType == 0) {
                    String qrLink = new NetworkAddress().getSpecificUrl(0);
                    String toolBarTitleText = getResources().getString(R.string.qr_bar);
                    callWebActivity(qrLink, toolBarTitleText);

                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            });

            btnCancel.setOnClickListener(view -> {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    private void callWebActivity(String webLink, String toolBarTitleText) {
        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra("backActivity", Constants.DASH_BOARD);
        intent.putExtra("links", webLink);
        intent.putExtra("toolbarTitleText", toolBarTitleText);
        intent.putExtra(Constants.LOCATION_RESPONSE, loc);
        startActivity(intent);
    }
}