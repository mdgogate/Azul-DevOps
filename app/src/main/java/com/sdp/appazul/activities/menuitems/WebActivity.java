package com.sdp.appazul.activities.menuitems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.BasicRegistrationActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.registration.UserRegisterActivity;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;


public class WebActivity extends BasicRegistrationActivity {

    private WebView mWebView;
    private TextView toolbarTextTitle;
    private ImageView btnWebBack;
    private String locationJson;
    private Context context;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        context = this;
        initializeLocalViewControls();
        backButton();

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeLocalViewControls() {
        mWebView = findViewById(R.id.myWeb1);
        toolbarTextTitle = findViewById(R.id.toolbarTextTitle);
        btnWebBack = findViewById(R.id.backButton);
        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();

        String webSite = getIntent().getStringExtra("links");
        Log.i("Links", webSite);
        String bar = getIntent().getStringExtra("toolbarTitleText");

        toolbarTextTitle.setText(bar);

        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setUserAgentString(System.getProperty("http.agent"));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("TAG", "onPageFinished: "+url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("TAG", "shouldOverrideUrlLoading: "+url);
                view.loadUrl(url);
                return true;
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }
        });

        WebChromeClient webChromeClient = new WebChromeClient();


        mWebView.setWebChromeClient(webChromeClient);

        mWebView.loadUrl(webSite);

    }

    private void backButton() {

        btnWebBack.setOnClickListener(v -> {
            if (getIntent().getStringExtra("backActivity") != null) {
                String backActivity = getIntent().getStringExtra("backActivity");
                if (backActivity.equalsIgnoreCase("LoginOne")) {
                    Intent intent = new Intent(WebActivity.this, UserRegisterActivity.class);
                    startActivity(intent);
                } else if (backActivity.equalsIgnoreCase("LoginTwo")) {
                    Intent intent = new Intent(WebActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                } else if (backActivity.equalsIgnoreCase("Dashboard")) {
                    Intent intent = new Intent(WebActivity.this, DashBoardActivity.class);
                    ((AzulApplication) ((WebActivity) this).getApplication()).setLocationDataShare(locationJson);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) mWebView.goBack();
        else {
            super.onBackPressed();
        }
    }

}