package com.sdp.appazul.activities.payment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

public class LinkSharingActivity extends BaseLoggedInActivity {

    TextView tvLinkTitleQuickSale;
    TextView tvLink;
    RelativeLayout linkInfo;
    TextView btnClose;
    Button btnNextSale;
    String locationJson;
    String link;
    RelativeLayout copyLayout;
    RelativeLayout shareIngLayout;
    TextView tvFinalAmount;
    String amountToDisplay;
    String currency;
    String selectedLocation;
    ImageView btnCopyLink;
    Context context;
    RelativeLayout activity_link_sharing;
    static int SNACK_LENGTH = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_sharing);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        context = this;
        copyLayout = findViewById(R.id.copyLayout);
        activity_link_sharing = findViewById(R.id.activity_link_sharing);
        btnClose = findViewById(R.id.btnClose);
        tvFinalAmount = findViewById(R.id.tvFinalAmount);
        btnCopyLink = findViewById(R.id.btnCopyLink);
        btnNextSale = findViewById(R.id.btnNextSale);
        tvLink = findViewById(R.id.tvLink);
        shareIngLayout = findViewById(R.id.shareingLayout);
        tvLinkTitleQuickSale = findViewById(R.id.tvLinkTitleQuickSale);
        linkInfo = findViewById(R.id.linkInfo);
        Intent dataIntent = getIntent();
        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();
        link = dataIntent.getStringExtra("RESPONSE_LINK");
        amountToDisplay = dataIntent.getStringExtra("DISPLAY_AMOUNT");
        currency = dataIntent.getStringExtra("CURRENCY");
        selectedLocation = dataIntent.getStringExtra("SELECTED_LOCATION");

        showAnimation();
        setData(link, amountToDisplay);
        copyLayout.setOnClickListener(imgCopyLinkView -> {

            if (!TextUtils.isEmpty(link)) {
                btnNextSale.setEnabled(true);
                btnNextSale.setBackgroundResource(R.drawable.link_button_background);
                btnCopyLink.setColorFilter(ContextCompat.getColor(this, R.color.gradient_button_right));
                copyLinkToClipboard(link);
            }

            Snackbar snackbar = Snackbar.make(activity_link_sharing,context.getResources().getString(R.string.link_copied),SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor("#0091DF"))
                    ;
            snackbar.show();
        });
        btnClose.setOnClickListener(btnCloseView ->
                cancelAlert(LinkSharingActivity.this)
        );
        shareIngLayout.setOnClickListener(shareIngLayoutView -> {
            if (!TextUtils.isEmpty(link)) {
                btnNextSale.setEnabled(true);
                btnNextSale.setBackgroundResource(R.drawable.link_button_background);
                shareLink(link, selectedLocation);
            }
        });
        btnNextSale.setOnClickListener(btnNextSaleView -> {
            Intent intent = new Intent(LinkSharingActivity.this, SetPaymentInfoActivity.class);
            ((AzulApplication) ((LinkSharingActivity) this).getApplication()).setLocationDataShare(locationJson);

            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });
    }

    private void copyLinkToClipboard(String copiedLink) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Azul Link", copiedLink);
        clipboard.setPrimaryClip(clip);
    }

    private void setData(String link, String givenAmount) {
        if (link != null && link.length() > 0) {
            tvLink.setText(link);

            tvLink.setOnClickListener(tvLinkView -> {
                if (link.startsWith("https://") || link.startsWith("http://")) {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(LinkSharingActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                }

            });

        } else {
            String dummyUrl = "https://pagos.azul.com.do/ba780bd0";
            tvLink.setText(dummyUrl);
        }


        if (!TextUtils.isEmpty(givenAmount)) {
            String amountToShow;
            if (currency.equalsIgnoreCase("USD")) {
                amountToShow = Constants.CURRENCY_FORMAT_USD + "" + amountToDisplay;
            } else {
                amountToShow = Constants.CURRENCY_FORMAT + "" + amountToDisplay;
            }
            tvFinalAmount.setText(amountToShow);
        }

    }


    private void showAnimation() {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        tvLinkTitleQuickSale.startAnimation(aniFade);
        linkInfo.startAnimation(aniFade);
        btnNextSale.startAnimation(aniFade);
        btnClose.startAnimation(aniFade);
    }

    Dialog cancelAlertDialog;

    public void cancelAlert(Context activity) {
        try {

            cancelAlertDialog = new Dialog(activity);
            cancelAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cancelAlertDialog.setCancelable(false);
            cancelAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            cancelAlertDialog.setContentView(R.layout.cancel_dialog_layout);
            TextView textTitle = cancelAlertDialog.findViewById(R.id.textTitle);
            TextView textMsg = cancelAlertDialog.findViewById(R.id.textMsg);
            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");
            textTitle.setTypeface(typeface);
            textTitle.setText(getResources().getString(R.string.close));
            textMsg.setTypeface(typeface);
            textMsg.setText(getResources().getString(R.string.close_button_message));
            TextView btnNo = cancelAlertDialog.findViewById(R.id.btnNo);
            TextView btnYes = cancelAlertDialog.findViewById(R.id.btnYes);
            btnYes.setTypeface(typeface);

            btnYes.setOnClickListener(view -> {

                Intent intent = new Intent(LinkSharingActivity.this, DashBoardActivity.class);
                ((AzulApplication) ((LinkSharingActivity) this).getApplication()).setLocationDataShare(locationJson);

                startActivity(intent);
                this.overridePendingTransition(R.anim.animation_leave,
                        R.anim.slide_nothing);
                if (cancelAlertDialog != null && cancelAlertDialog.isShowing()) {
                    cancelAlertDialog.dismiss();
                }
            });
            btnNo.setOnClickListener(view -> {
                if (cancelAlertDialog != null && cancelAlertDialog.isShowing()) {
                    cancelAlertDialog.dismiss();
                }
            });
            cancelAlertDialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    private void shareLink(String link, String selectedLocation) {
        try {
            String subjectText = "Link de Pagos" + " " + selectedLocation;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText);
            String shareMessage = getResources().getString(R.string.payment_link_label_message) + link + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.payment_link_label)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }
}