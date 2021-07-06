package com.sdp.appazul.activities.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.globals.Constants;

public class LinkSharingActivity extends AppCompatActivity {

    TextView tvLinkTitle;
    TextView tvLink;
    RelativeLayout linkInfo;
    LinearLayout buttonsLayout;
    Button btnSalir;
    Button btnNextSale;
    String locationJson;
    String link;
    ImageView imgCopyLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_sharing);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        imgCopyLink = findViewById(R.id.imgCopyLink);
        btnSalir = findViewById(R.id.btnSalir);
        btnNextSale = findViewById(R.id.btnNextSale);
        tvLink = findViewById(R.id.tvLink);
        tvLinkTitle = findViewById(R.id.tvLinkTitle);
        linkInfo = findViewById(R.id.linkInfo);
        buttonsLayout = findViewById(R.id.buttonsLayout);
        Intent dataIntent = getIntent();
        locationJson = dataIntent.getStringExtra(Constants.LOCATION_RESPONSE);
        link = dataIntent.getStringExtra("RESPONSE_LINK");
        showAnimation();
        setData(link);
        imgCopyLink.setOnClickListener(imgCopyLinkView -> {
            Toast toast = Toast.makeText(getApplicationContext(), "Link de pago copiado", Toast.LENGTH_LONG);
            View view = toast.getView();
            TextView view1 = (TextView) view.findViewById(android.R.id.message);
            view1.setTextColor(Color.WHITE);
            view.setBackgroundResource(R.drawable.toast_link_background);
            toast.show();
        });
        btnSalir.setOnClickListener(btnSalirView -> {
            Intent intent = new Intent(LinkSharingActivity.this, DashBoardActivity.class);
            intent.putExtra("GENERATE_LINK", "LINK");
            intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
            startActivity(intent);
        });

        btnNextSale.setOnClickListener(btnNextSaleView -> {
            Intent intent = new Intent(LinkSharingActivity.this, SetPaymentInfoActivity.class);
            intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
            startActivity(intent);
        });
    }

    private void setData(String link) {
        tvLink.setText(link);
    }

    private void showAnimation() {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        tvLinkTitle.startAnimation(aniFade);
        linkInfo.startAnimation(aniFade);
        buttonsLayout.startAnimation(aniFade);
    }
}