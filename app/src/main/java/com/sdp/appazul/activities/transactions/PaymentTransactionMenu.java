package com.sdp.appazul.activities.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

import java.util.List;

public class PaymentTransactionMenu extends BottomSheetDialogFragment {

    RelativeLayout btnMoreInformation;
    RelativeLayout btnResendLink;
    String locationJson;
    String SelectedLinkId;
    String paymentCode;
    String currencyCode;
    String statusValue;
    String trnResponse;
    String amountToShow;
    String taxExemptFlag;
    List<String> permissionList;
    List<String> productPermissionList;

    public PaymentTransactionMenu(String trnResponse, String amountToShow, String statusValue, String linkId, String responseCode, String selectedCurrency, String taxExemptFlag) {
        this.trnResponse = trnResponse;
        this.amountToShow = amountToShow;
        this.statusValue = statusValue;
        SelectedLinkId = linkId;
        paymentCode = responseCode;
        this.currencyCode = selectedCurrency;
        this.taxExemptFlag = taxExemptFlag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_bottom_menu_layout, container, false);
        initControls(view);

        return view;

    }

    private void initControls(View view) {
        btnMoreInformation = view.findViewById(R.id.btnMoreInformation);
        permissionList = ((AzulApplication) getActivity().getApplication()).getFeaturePermissionsList();
        productPermissionList = ((AzulApplication) getActivity().getApplication()).getProductPermissionsList();
        btnResendLink = view.findViewById(R.id.btnResendLink);
        locationJson = ((AzulApplication) getActivity().getApplicationContext()).getLocationDataShare();

        if (permissionList.contains("APPPaymentLinksSale")
                || permissionList.contains("APPPaymentLinksHold")) {
            if (permissionList.contains("APPPaymentLinksQueryOwnTransactions")
                    || permissionList.contains("APPPaymentLinksQueryAllTransactions")) {
                btnResendLink.setVisibility(View.VISIBLE);

                btnResendLink.setBackgroundResource(R.drawable.payment_link_menu_inactive);
                if (statusValue.equalsIgnoreCase("Generated")
                        || statusValue.equalsIgnoreCase("Abierto") ||
                        statusValue.equalsIgnoreCase("Open")
                        || statusValue.equalsIgnoreCase("Processed")) {
                    validatePermission();
                } else {
                    btnResendLink.setVisibility(View.GONE);
                }
//
            } else {
                btnResendLink.setVisibility(View.GONE);
            }
        } else {
            btnResendLink.setVisibility(View.GONE);
        }


        btnMoreInformation.setBackgroundResource(R.drawable.payment_link_menu_inactive);
        btnMoreInformation.setOnClickListener(btnMoreInformationView -> {
            dismiss();

            btnMoreInformation.setBackgroundResource(R.drawable.payment_menu_border_bg);
            ((AzulApplication) (getActivity()).getApplication()).setLocationDataShare(locationJson);
            Intent intent = new Intent(getActivity(), PaymentLinkDetails.class);
            intent.putExtra("LINK_ID", SelectedLinkId);
            intent.putExtra("PAYMENT_CODE", paymentCode);
            intent.putExtra("PAYMENT_TAX", taxExemptFlag);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });
        btnResendLink.setBackgroundResource(R.drawable.payment_link_menu_inactive);
        btnResendLink.setOnClickListener(btnResendLinkView -> {
            dismiss();
            String linkToDisplay = "https://pagos.azul.com.do/" + SelectedLinkId;
            btnResendLink.setBackgroundResource(R.drawable.payment_menu_border_bg);
            Intent intent = new Intent(getActivity(), ResendPaymentLink.class);
            intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
            intent.putExtra(Constants.PAYMENT_LOCATION_RESPONSE, locationJson);
            intent.putExtra("RESPONSE_LINK", linkToDisplay);
            intent.putExtra("DISPLAY_AMOUNT", amountToShow);
            intent.putExtra("CURRENCY", currencyCode);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });
    }

    private void validatePermission() {
        if (statusValue.equalsIgnoreCase("Processed") || statusValue.equalsIgnoreCase("Procesado")) {
            if (trnResponse.equalsIgnoreCase("Declinado") || trnResponse.equalsIgnoreCase("Declined")) {
                btnResendLink.setVisibility(View.VISIBLE);
            } else {
                btnResendLink.setVisibility(View.GONE);
            }
        } else {
            btnResendLink.setVisibility(View.VISIBLE);
        }
    }
}
