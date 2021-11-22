package com.sdp.appazul.activities.payment;

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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sdp.appazul.activities.menuitems.WebActivity;
import com.sdp.appazul.activities.transactions.PaymentLinkTransactions;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.utils.NetworkAddress;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PaymentLinkBottomSheet extends BottomSheetDialogFragment {
    TextView tvGenerateLink;
    TextView tvPaymentTransactions;
    String locationJson;
    PaymentLinkSheetCloseEvent paymentLinkSheetCloseEvent;
    List<String> permissionList;
    List<String> productPermissionList;
    Dialog dialog;
    Context mcontext;

    public PaymentLinkBottomSheet() {
    }

    public PaymentLinkBottomSheet(Context context, String locationData) {
        this.locationJson = locationData;
        mcontext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.payment_link_bottom_sheet, container, false);

        tvGenerateLink = view.findViewById(R.id.tvGenerateLink);
        tvPaymentTransactions = view.findViewById(R.id.tvPaymentTransactions);
        tvGenerateLink.setOnClickListener(tvGenerateLinkView -> openGenerateLinkScreen(locationJson));
        tvPaymentTransactions.setOnClickListener(tvPaymentTransactionsView -> openPaymentTransactionsScreen(locationJson));
        permissionList = new ArrayList<>();
        productPermissionList = new ArrayList<>();

        permissionList = ((AzulApplication) getActivity().getApplication()).getFeaturePermissionsList();
        productPermissionList = ((AzulApplication) getActivity().getApplication()).getProductPermissionsList();

        return view;
    }

    private void openGenerateLinkScreen(String locationData) {
        if (productPermissionList.contains(Constants.HAS_PAYMENT_LINK)) {

            if (permissionList.contains("APPPaymentLinksSale")) {
                Intent intent = new Intent(getActivity(), SetPaymentInfoActivity.class);
                intent.putExtra(Constants.LOCATION_RESPONSE, locationData);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animation_enter,
                        R.anim.slide_nothing);
            } else {
                errorAlert(getActivity(), 1);
            }
        } else {
            errorAlert(getActivity(), 2);
        }
    }

    private void openPaymentTransactionsScreen(String locationData) {

        if (productPermissionList.contains(Constants.HAS_PAYMENT_LINK)) {

            if (permissionList.contains("APPPaymentLinksQueryOwnTransactions")
                    || permissionList.contains("APPPaymentLinksQueryAllTransactions")) {

                Intent intent = new Intent(getActivity(), PaymentLinkTransactions.class);
                intent.putExtra(Constants.LOCATION_RESPONSE, locationData);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animation_enter,
                        R.anim.slide_nothing);
            } else {

                errorAlert(getActivity(), 1);
            }

        } else {

            errorAlert(getActivity(), 2);
        }
    }

    public interface PaymentLinkSheetCloseEvent {
        void onPaymentSheetClosed(int text);
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        paymentLinkSheetCloseEvent.onPaymentSheetClosed(1);
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        paymentLinkSheetCloseEvent.onPaymentSheetClosed(1);
        super.onDestroy();
    }

    public void setPaymentLinkSheetCloseEvent(PaymentLinkSheetCloseEvent paymentLinkSheetCloseEvent) {
        this.paymentLinkSheetCloseEvent = paymentLinkSheetCloseEvent;
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


            if (errorType == 1) {
                btnCancel.setVisibility(View.VISIBLE);
                textTitle.setText(getResources().getString(R.string.no_access_for_functionality));
                textMsg.setText(getResources().getString(R.string.no_access_for_functionality_msg));
            } else if (errorType == 2) {
                btnCancel.setVisibility(View.VISIBLE);
                textTitle.setText(getResources().getString(R.string.not_permitted_title));
                textMsg.setText(getResources().getString(R.string.get_blue_payment_link));
            }


            TextView btnContinue = dialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);

            btnContinue.setOnClickListener(view -> {

                if (errorType == 2) {
                    String qrLink = new NetworkAddress().getSpecificUrl(3);
                    String toolBarTitleText = "Solicitud Link de Pagos";
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
        intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
        startActivity(intent);
    }
}
