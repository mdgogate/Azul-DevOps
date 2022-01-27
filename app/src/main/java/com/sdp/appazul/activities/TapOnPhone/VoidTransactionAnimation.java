package com.sdp.appazul.activities.TapOnPhone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sdp.appazul.R;
import com.sdp.appazul.classes.TapOnPhone;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

import java.util.Map;

import digital.paynetics.phos.PhosSdk;
import digital.paynetics.phos.exceptions.PhosException;
import digital.paynetics.phos.sdk.callback.TransactionCallback;
import digital.paynetics.phos.sdk.entities.Transaction;

public class VoidTransactionAnimation extends AppCompatActivity {
    ImageView gifImageView;
    TextView tvAnimationText;
    String totalAmountEntered;
    String selectedLocation;
    String locationJson;
    Transaction tap;
    int option = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void_transaction_animation);

        Intent intent = getIntent();
        option = intent.getIntExtra("OPTION", 0);

        locationJson = ((AzulApplication) this.getApplicationContext()).getLocationDataShare();
        tap = ((AzulApplication) this.getApplicationContext()).getTransaction();

        tvAnimationText = findViewById(R.id.tvAnimationText);
        if (option == 2) {
            tvAnimationText.setText("Procesando…");
        } else {
            tvAnimationText.setText("Autorizando…");
        }
        gifImageView = findViewById(R.id.GifImageView);
        Glide.with(this).asGif().load(R.raw.loading_animation).into(gifImageView);
    }


    String totalAmountNew;

    private void callNextScreen() {
        if (option == 1) {

            Intent intent = new Intent(this, VoidReceiptActivity.class);
            startActivity(intent);
            ((AzulApplication) ((VoidTransactionAnimation) this).getApplication()).setLocationDataShare(locationJson);
            ((AzulApplication) ((VoidTransactionAnimation) this).getApplication()).setTransaction(tap);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        } else if (option == 2) {
//            Intent intent = new Intent(this, PhosSdkPerformActivity.class);
//            String totalAmount = tap.getAmount().replace(",", "");
//            totalAmountNew = totalAmount.replace("RD$ ", "");
//            String totalLastAmount = totalAmountNew.replace(".", "");
//            intent.putExtra(Constants.TOTAL_AMOUNT, totalLastAmount);
//            intent.putExtra("SCREEN_TYPE", "REFUND");
//            intent.putExtra(Constants.LOCATION_NAME_SELECTED, tap.getLocName() );
//            startActivity(intent);
//            ((AzulApplication) ((VoidTransactionAnimation) this).getApplication()).setLocationDataShare(locationJson);
//            ((AzulApplication) ((VoidTransactionAnimation) this).getApplication()).setTap(tap);
//            this.overridePendingTransition(R.anim.animation_enter,
//                    R.anim.slide_nothing);
        }
    }
}