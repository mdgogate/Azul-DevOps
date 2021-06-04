package com.azul.app.android.activities.menuitems;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.azul.app.android.R;
import com.azul.app.android.activities.home.BasicRegistrationActivity;
import com.azul.app.android.activities.home.MainMenuActivity;
import com.azul.app.android.activities.registration.UserRegisterActivity;


public class WebActivity extends BasicRegistrationActivity {

    String newUA = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0 Android";
    WebView mWebView;
    TextView toolbarTextTitle;
    ImageView btnWebBack;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web);
        initializeLocalViewControls();
        backButton();

    }

    private void initializeLocalViewControls(){
        mWebView = findViewById(R.id.myWeb1);
        toolbarTextTitle = findViewById(R.id.toolbarTextTitle);
        btnWebBack = findViewById(R.id.web_back_button);

        mWebView.getSettings().setUserAgentString(newUA);
        mWebView.getSettings().setBuiltInZoomControls(true);

        String webSite = getIntent().getStringExtra("links");
        Log.i("Links", webSite);
        String bar = getIntent().getStringExtra("toolbarTitleText");

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setInitialScale(1);
        toolbarTextTitle.setText(bar);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.i("TAG", "onReceivedSslError " + error.toString());
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                Log.i("TAG", "Closed " + url);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.i("onReceivedHttpError", errorResponse.toString());
                super.onReceivedHttpError(view, request, errorResponse);
            }

        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(webSite);

    }

    private void backButton() {

        btnWebBack.setOnClickListener(v -> {
            if (getIntent().getStringExtra("backActivity") != null) {
                String backActivity = getIntent().getStringExtra("backActivity");
                Log.d("print",backActivity);
                if (backActivity.equalsIgnoreCase("LoginOne")) {
                    Intent intent = new Intent(WebActivity.this, UserRegisterActivity.class);
                    startActivity(intent);
                } else if (backActivity.equalsIgnoreCase("LoginTwo")) {
                    Intent intent = new Intent(WebActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) mWebView.goBack(); else {
            super.onBackPressed();
        }
    }

}