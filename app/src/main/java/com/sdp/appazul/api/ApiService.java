package com.sdp.appazul.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.JSONOperations;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.R;
import com.sdp.appazul.security.RSAHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiService extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    Context context;
    String url;
    boolean isForbiddenRes = false;
    boolean isSecureCall = false;
    OkHttpClient client;
    CallbackListener listener;
    String responseString;
    JSONObject jsonObject;
    ProgressDialog progressDialog;
    MediaType mediaTypeJSON;
    String strcodigoError = "codigoError";

    public ApiService(Context context, CallbackListener listener, String url, JSONObject jsonObject) {
        this.context = context;
        this.listener = listener;
        this.url = url;
        this.jsonObject = jsonObject;
        Log.d("Url : ", "" + url);
        Log.d("Request Object : ", "" + jsonObject.toString());
    }

    @Override
    public String doInBackground(String... params) {
        try {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //noinspection deprecation
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    //.addInterceptor(new ReceivedCookiesInterceptor())
                    .addInterceptor(logging)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS);

            String xAuthTokenHeaderValue = "ssc-mobile";
            client = clientBuilder.build();
            isSecureCall = getSecureServiceCalls().contains(url);

            String authTokenHeader = "";

            RequestBody requestBody = RequestBody.create(mediaTypeJSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(this.url)
                    .addHeader("x-ssc-key", "ssc-mobile")
                    .addHeader("Content-Type", "text/json")
                    .addHeader("X-AUTH-TOKEN", xAuthTokenHeaderValue)
                    .addHeader("Authorization", authTokenHeader)
                    //.addHeader("Authorization",authorizationHeader)
                    .method("POST", requestBody)
                    .build();
            getResponseObject(request);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return responseString;
    }

    private void getResponseObject(Request request) {
        try {
            Response response = client.newCall(request).execute();
            isForbiddenRes = response.code() == Constants.FORBIDDEN_CODE && isSecureCall;
            if (response.isSuccessful()) {
                if (isSecureCall) {
                    responseString = getDecryptedResponse(Objects.requireNonNull(response.body()).string());
                } else {
                    responseString = Objects.requireNonNull(response.body()).string();
                    Log.d("response", responseString);
                }
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if ((progressDialog != null) && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (final Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        } finally {
            this.progressDialog = null;
        }
        checkResponseURL();
    }

    private void checkResponseURL() {

        if (isForbiddenRes) {
            showSessionAlert();
        } else if (responseString != null && JSONOperations.isJSONValid(responseString)) {
            switch (url) {
                case ServiceUrls.REGISTER_USER:
                    listener.userRegisterResponse(responseString);
                    break;

                case ServiceUrls.REGISTER_PIN:
                    listener.pinRegisterResponse(responseString);
                    break;

                case ServiceUrls.LOGIN:
                    listener.loginResponse(responseString);
                    break;

                case ServiceUrls.QR_TRANSACTION:
                    listener.qrTransactionResponse(responseString);
                    break;

                case ServiceUrls.SETTLEMENT_TRANSACTION:
                    listener.settleTransactionResponse(responseString);
                    break;

                case ServiceUrls.QR_CODE_BEFORE_LOGIN:
                    listener.qrResponseLogin(responseString);
                    break;

                case ServiceUrls.GET_MERCHANTS_FOR_PAYMENT_LINK:
                    listener.paymentMerchantResponse(responseString);
                    break;

                case ServiceUrls.CREATE_PAYMENT_LINK:
                    listener.createPaymentLinkResponse(responseString);
                    break;

                case ServiceUrls.PUSH_TOKEN:
                    listener.pushTokenRepsonse(responseString);
                    break;

                case ServiceUrls.TRANSACTION_WIDGET:
                    listener.transactionWidgetResponse(responseString);
                    break;

                case ServiceUrls.TRANSACTION_WIDGET_QR:
                    listener.transactionWidgetQrResponse(responseString);
                    break;

                default:
                    break;
            }
        } else {

            if (responseString != null && responseString.contains("<svg version") && url.equalsIgnoreCase(ServiceUrls.GET_QR)) {
                listener.getQrResponse(responseString);
            }
        }
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, null,
                context.getString(R.string.loading_lbl), false, false);
        progressDialog.setCancelable(false);

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        Log.d("", "");
    }

    private List<String> getSecureServiceCalls() {
        List<String> serviceCallList = new ArrayList<>();
        serviceCallList.add(ServiceUrls.LOGIN);
        serviceCallList.add(ServiceUrls.QR_TRANSACTION);
        serviceCallList.add(ServiceUrls.GET_QR);
        serviceCallList.add(ServiceUrls.SETTLEMENT_TRANSACTION);
        serviceCallList.add(ServiceUrls.QR_CODE_BEFORE_LOGIN);
        serviceCallList.add(ServiceUrls.GET_MERCHANTS_FOR_PAYMENT_LINK);
        serviceCallList.add(ServiceUrls.CREATE_PAYMENT_LINK);
        serviceCallList.add(ServiceUrls.PUSH_TOKEN);
        serviceCallList.add(ServiceUrls.TRANSACTION_WIDGET);
        serviceCallList.add(ServiceUrls.TRANSACTION_WIDGET_QR);
        return serviceCallList;
    }

    private String getDecryptedResponse(String serviceResponse) {
        try {

            if (serviceResponse.contains("<svg version")) {
                return serviceResponse;
            } else {
                JSONObject jsonObject1 = new JSONObject(serviceResponse);
                Log.i("mamresponse--->", "mamresponse-" + jsonObject1.toString());
                if (jsonObject1.has("tcp")) {
                    String key = jsonObject1.has("tcp") ? jsonObject1.getString("tcp") : "";
                    byte[] decryptedkey = RSAHelper.decryptAESserverKey(key);
                    ((AzulApplication) ((Activity) context).getApplication()).setTcpKey(Base64.encodeToString(decryptedkey, 0));
                    String decryptedresponse = RSAHelper.decryptDataUsingDynamicKey(jsonObject1.getString("payload"), decryptedkey);
                    assert decryptedresponse != null;
                    JSONObject decryptedjsonobject = new JSONObject(decryptedresponse);

                    if (decryptedjsonobject.has("access_token")) {
                        ((AzulApplication) ((Activity) context).getApplication()).setAccToken(decryptedjsonobject.getString("access_token"));
                    }

                    int maxLogSize = 1300;
                    for (int i = 0; i <= decryptedresponse.length() / maxLogSize; i++) {
                        int start = i * maxLogSize;
                        int end = (i + 1) * maxLogSize;
                        end = end > decryptedresponse.length() ? decryptedresponse.length() : end;
                        Log.i("Decrypted response : ", decryptedresponse.substring(start, end));
                    }

                    return decryptedresponse;
                }else {
                    return serviceResponse;
                }

            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return "";
    }

    @SuppressWarnings("deprecation")
    public void showSessionAlert() {
        try {
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(context.getString(R.string.error_label));
            alertDialog.setMessage(context.getString(R.string.session_msg));
            alertDialog.setButton(context.getString(R.string.continue_lable), (dialog, which) ->
                    alertDialog.cancel()
            );
            alertDialog.show();
        } catch (Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }
}