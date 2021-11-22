package com.sdp.appazul.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.dashboard.QrCode;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.payment.PaymentDataValidateActivity;
import com.sdp.appazul.activities.payment.QuickPayConfirmActivity;
import com.sdp.appazul.activities.payment.QuickPayValidationActivity;
import com.sdp.appazul.activities.registration.PinLoginActivity;
import com.sdp.appazul.activities.registration.PinSetActivity;
import com.sdp.appazul.activities.registration.UserRegisterActivity;
import com.sdp.appazul.activities.transactions.QrTransactions;
import com.sdp.appazul.activities.transactions.SettledTransactionsQuery;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.EncodingHelper;
import com.sdp.appazul.globals.JSONOperations;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.R;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
    int responseCode = -1;
    OkHttpClient client;
    CallbackListener listener;
    String responseString;
    JSONObject jsonObject;
    ProgressDialog progressDialog;
    MediaType mediaTypeJSON;
    Dialog errorAlertDialog;

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


            String xAuthTokenHeaderValue = Constants.SSC_MOBILE_VALUE;
            client = clientBuilder.build();
            isSecureCall = getSecureServiceCalls().contains(url);

            String authTokenHeader = (isSecureCall && !url.equals(ServiceUrls.LOGIN) && !url.equals(ServiceUrls.R_TOKEN))
                    ? "Bearer " + ((AzulApplication) ((Activity) context).getApplication()).getAccToken() : "";

            String deviceId = RSAHelper.ecryptRSA(context, DeviceUtils.getDeviceId(context));

            Request request;
            RequestBody requestBody = RequestBody.create(mediaTypeJSON, jsonObject.toString());
            if (Boolean.TRUE.equals(checkApi(url))) {
                Log.d("TAG", "without authTokenHeader : ");
                request
                        = new Request.Builder()
                        .url(this.url)
                        .addHeader(KeyConstants.SSC_KEY, Constants.SSC_MOBILE_VALUE)
                        .addHeader(KeyConstants.CONTENT_TYPE, KeyConstants.TEXT_JSON)
                        .addHeader(KeyConstants.AUTH_TOKEN, xAuthTokenHeaderValue)
//                        .addHeader("Authorization", authTokenHeader)
                        .method("POST", requestBody)
                        .build();
            } else if (url.equalsIgnoreCase(ServiceUrls.REGISTER_PIN)) {
                Log.d("TAG", "raccess-token : " + ((AzulApplication) ((Activity) context).getApplication()).getrAccToken());
                Log.d("TAG", "deviceId: " + deviceId);
                request
                        = new Request.Builder()
                        .url(this.url)
                        .addHeader(KeyConstants.SSC_KEY, Constants.SSC_MOBILE_VALUE)
                        .addHeader(KeyConstants.CONTENT_TYPE, KeyConstants.TEXT_JSON)
                        .addHeader(KeyConstants.AUTH_TOKEN, xAuthTokenHeaderValue)
                        .addHeader("raccess-token", ((AzulApplication) ((Activity) context).getApplication()).getrAccToken())
                        .method("POST", requestBody)
                        .build();
            } else {
                Log.d("TAG", "authTokenHeader: " + authTokenHeader);
                Log.d("TAG", "deviceId: " + deviceId);
                request
                        = new Request.Builder()
                        .url(this.url)
                        .addHeader(KeyConstants.SSC_KEY, Constants.SSC_MOBILE_VALUE)
                        .addHeader(KeyConstants.CONTENT_TYPE, KeyConstants.TEXT_JSON)
                        .addHeader(KeyConstants.AUTH_TOKEN, xAuthTokenHeaderValue)
                        .addHeader("Authorization", authTokenHeader)
                        .addHeader("device", deviceId)
                        .method("POST", requestBody)
                        .build();
            }
            ((AzulApplication) ((Activity) context).getApplication()).setAuthData(authTokenHeader);

            getResponseObject(request);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }

        return responseString;
    }

    private boolean checkApi(String CalledUrl) {
        if (CalledUrl.equalsIgnoreCase(ServiceUrls.REGISTER_USER) || CalledUrl.equalsIgnoreCase(ServiceUrls.LOGIN)) {
            return true;
        } else if (CalledUrl.equalsIgnoreCase(ServiceUrls.LOGIN_TOKEN)
                || CalledUrl.equalsIgnoreCase(ServiceUrls.R_TOKEN) || CalledUrl.equalsIgnoreCase(ServiceUrls.REQUEST_TOKEN_BEFORE_LOGIN)) {
            return true;
        } else {
            return false;
        }
    }

    private void getResponseObject(Request request) {
        try {
            Response response = client.newCall(request).execute();
            isForbiddenRes = response.code() == Constants.FORBIDDEN_CODE && isSecureCall;
            if (response.code() == Constants.INTERNAL_SERVER_ERROR) {
                isForbiddenRes = true;
                responseCode = response.code();
            }
            if (response.isSuccessful()) {
                if (isSecureCall) {
                    responseString = getDecryptedResponse(Objects.requireNonNull(response.body()).string(), response.code());
                } else {
                    responseString = Objects.requireNonNull(response.body()).string();
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
        if (responseCode == Constants.INTERNAL_SERVER_ERROR) {
            showSessionAlert(context.getResources().getString(R.string.internal_server_error));
        } else if (Boolean.FALSE.equals(isForbiddenRes) && responseString == null) {
            showSessionAlert("503 Service Unavailable");
        } else if (isForbiddenRes) {
            showSessionAlert(context.getString(R.string.session_msg));
        } else if (responseCode == Constants.OK_RESPONSE && url.equalsIgnoreCase(ServiceUrls.DEREGISTER_USER)) {
            listener.callingDeregisterUsersApi(responseString);
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

                case ServiceUrls.REQUEST_TOKEN_BEFORE_LOGIN:
                    listener.qrResponseLogin(responseString);
                    break;

                case ServiceUrls.GET_PAYMENT_MERCHANTS:
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
                case ServiceUrls.DEREGISTER_USER:
                    listener.callingDeregisterUsersApi(responseString);
                    break;

                case ServiceUrls.GET_APP_RELATED_PERMISSIONS:
                    listener.appPermissionsResponse(responseString);
                    break;

                case ServiceUrls.PAYMENT_LINK_SEARCH:
                    listener.getPaymentSearchResponse(responseString);
                    break;
                case ServiceUrls.PAYMENT_LINK_INFO:
                    listener.getPaymentLinkInfoResponse(responseString);
                    break;
                case ServiceUrls.LOGIN_TOKEN:
                    listener.getNewLoginResponse(responseString);
                    break;
                case ServiceUrls.GET_TRANSACTION_PDF:
                    listener.getTransactionPdfResponse(responseString);
                    break;

                case ServiceUrls.DASHBOARD_CALL:
                    listener.getDataForDashboard(responseString);
                    break;
                case ServiceUrls.R_TOKEN:
                    listener.getNewTokenAtPreLogin(responseString);
                    break;
                case ServiceUrls.GET_USER:
                    listener.getUserResponse(responseString);
                    break;

                default:
                    break;
            }
        } else {
            bigResponseApis();
        }
    }

    private void bigResponseApis() {
        if (responseString != null && responseString.contains("<svg version") && url.equalsIgnoreCase(ServiceUrls.GET_QR)) {
            listener.getQrResponse(responseString);
        } else if (responseString != null && url.equalsIgnoreCase(ServiceUrls.GET_TRANSACTION_PDF)) {
            listener.getTransactionPdfResponse(responseString);
        } else if (responseString != null && responseString.equalsIgnoreCase("") && url.equalsIgnoreCase(ServiceUrls.GET_QR)) {
            listener.getQrResponse(responseString);
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
        serviceCallList.add(ServiceUrls.REQUEST_TOKEN_BEFORE_LOGIN);
        serviceCallList.add(ServiceUrls.R_TOKEN);
        serviceCallList.add(ServiceUrls.GET_USER);
        serviceCallList.add(ServiceUrls.GET_PAYMENT_MERCHANTS);
        serviceCallList.add(ServiceUrls.CREATE_PAYMENT_LINK);
        serviceCallList.add(ServiceUrls.PUSH_TOKEN);
        serviceCallList.add(ServiceUrls.TRANSACTION_WIDGET);
        serviceCallList.add(ServiceUrls.TRANSACTION_WIDGET_QR);
        serviceCallList.add(ServiceUrls.DEREGISTER_USER);
        serviceCallList.add(ServiceUrls.GET_APP_RELATED_PERMISSIONS);
        serviceCallList.add(ServiceUrls.GET_TRANSACTION_PDF);
        serviceCallList.add(ServiceUrls.PAYMENT_LINK_SEARCH);
        serviceCallList.add(ServiceUrls.PAYMENT_LINK_INFO);
        serviceCallList.add(ServiceUrls.LOGIN_TOKEN);
        serviceCallList.add(ServiceUrls.DASHBOARD_CALL);
        return serviceCallList;
    }

    private String getDecryptedResponse(String serviceResponse, int code) {
        try {

            if (serviceResponse != null && !TextUtils.isEmpty(serviceResponse)
                    && !serviceResponse.equalsIgnoreCase("null")) {
                Log.i("serviceResponse ---> ", "serviceResponse =" + serviceResponse);
                if (ServiceUrls.GET_QR.equalsIgnoreCase(url) && serviceResponse.contains("<svg version")) {
                    return serviceResponse;
                } else if (ServiceUrls.GET_TRANSACTION_PDF.equalsIgnoreCase(url) && serviceResponse.contains("PDF")) {
                    ((AzulApplication) ((Activity) context).getApplication()).setPdfAvailable(true);
                    return serviceResponse;
                } else if (ServiceUrls.DEREGISTER_USER.equalsIgnoreCase(url) && code == Constants.OK_RESPONSE) {
                    return "deregister";
                } else {
                    JSONObject jsonObject1 = new JSONObject(serviceResponse);
                    return decryptGivenResponse(jsonObject1, serviceResponse);
                }
            } else {
                Log.d("TAG", "getDecryptedResponse: " + serviceResponse);
                if (ServiceUrls.DEREGISTER_USER.equalsIgnoreCase(url) && code == Constants.OK_RESPONSE) {
                    responseCode = code;
                    responseString = "deregister";
                }
            }

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return "";
    }

    private String decryptGivenResponse(JSONObject jsonObject1, String serviceResponse) {
        try {
            Log.i("mamresponse--->", "mamresponse-" + jsonObject1.toString());
            if (jsonObject1.has("tcp") && !jsonObject1.getString("tcp").equalsIgnoreCase("") &&
                    jsonObject1.has("vcr") && !jsonObject1.getString("vcr").equalsIgnoreCase("")) {
                String key = jsonObject1.has("tcp") ? jsonObject1.getString("tcp") : "";
                String vcr = jsonObject1.has("vcr") ? jsonObject1.getString("vcr") : "";
                byte[] decryptedkey = RSAHelper.decryptAESserverKey(key);
                byte[] decryptedVcr = RSAHelper.decryptAESserverKey(vcr);
                ((AzulApplication) ((Activity) context).getApplication()).setTcpKey(Base64.encodeToString(decryptedkey, 0));
                ((AzulApplication) ((Activity) context).getApplication()).setVcr(Base64.encodeToString(decryptedVcr, 0));

                String decryptedresponse = RSAHelper.decryptDataUsingDynamicKey(jsonObject1.getString("payload"), decryptedkey, decryptedVcr);
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
            } else {
                return serviceResponse;
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return "";
    }

    @SuppressWarnings("deprecation")
    public void showSessionAlert(String message) {
        try {
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            if (message.equalsIgnoreCase(context.getResources().getString(R.string.internal_server_error))) {
                errorAlert(context, 1);
            } else if (message.equalsIgnoreCase("503 Service Unavailable")) {
                errorAlert(context, 1);
            } else {
                alertDialog.setTitle("Sesión expirada");
                alertDialog.setMessage(message);
                alertDialog.setButton("Aceptar", (dialog, which) -> {
                            alertDialog.cancel();
                            if (message.equalsIgnoreCase(context.getString(R.string.session_msg))) {
                                Intent intent = new Intent(context, MainMenuActivity.class);
                                context.startActivity(intent);
                            } else if (message.equalsIgnoreCase(context.getResources().getString(R.string.internal_server_error))) {
                                setActivity(context);
                            }
                        }
                );
                alertDialog.show();
            }

        } catch (Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    private void setActivity(Context context) {
        if (context.getClass().equals(DashBoardActivity.class)) {
            Log.d("TAG", "setActivity: ");
        } else if (context.getClass().equals(SettledTransactionsQuery.class)) {
            redirectToDashBoard();
        } else if (context.getClass().equals(QrTransactions.class)) {
            Intent intent = new Intent(context, DashBoardActivity.class);
            context.startActivity(intent);
        } else if (context.getClass().equals(PaymentDataValidateActivity.class)) {
            redirectToDashBoard();
        } else if (context.getClass().equals(QrCode.class)) {
            redirectToDashBoard();
        } else if (context.getClass().equals(MainMenuActivity.class)) {
            Log.d("TAG", "setActivity: ");
        } else if (context.getClass().equals(QuickPayValidationActivity.class)) {
            Intent intent = new Intent(context, MainMenuActivity.class);
            context.startActivity(intent);
        } else if (context.getClass().equals(QuickPayConfirmActivity.class)) {
            redirectToPreLogin();
        } else if (context.getClass().equals(PinLoginActivity.class)) {
            redirectToPreLogin();
        } else if (context.getClass().equals(PinSetActivity.class)) {
            Intent intent = new Intent(context, UserRegisterActivity.class);
            context.startActivity(intent);
        } else if (context.getClass().equals(UserRegisterActivity.class)) {
            Intent intent1 = new Intent(context, UserRegisterActivity.class);
            context.startActivity(intent1);
        }
    }

    private void redirectToDashBoard() {
        Intent intent = new Intent(context, DashBoardActivity.class);
        context.startActivity(intent);
    }

    private void redirectToPreLogin() {
        Intent intent = new Intent(context, MainMenuActivity.class);
        context.startActivity(intent);
    }

    public void errorAlert(Context activity, int isPinBlock) {
        try {

            errorAlertDialog = new Dialog(activity);
            errorAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            errorAlertDialog.setCancelable(false);
            errorAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            errorAlertDialog.setContentView(R.layout.custom_login_alert_dialog);
            TextView textTitle = errorAlertDialog.findViewById(R.id.textTitle);
            TextView textMsg = errorAlertDialog.findViewById(R.id.textMsg);
            TextView tvTryCount = errorAlertDialog.findViewById(R.id.tvTryCount);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");

            tvTryCount.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            textTitle.setTypeface(typeface);

            if (isPinBlock == 1) {
                tvTryCount.setVisibility(View.INVISIBLE);
                textTitle.setText("Lo sentimos");
                textMsg.setText("Tu requerimiento no pudo ser completado. Por favor inténtalo más tarde");
            }


            TextView btnContinue = errorAlertDialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);
            btnContinue.setText("Aceptar");

            btnContinue.setOnClickListener(view -> {
                if (isPinBlock == 1) {
                    setActivity(context);
                }
                if (errorAlertDialog != null && errorAlertDialog.isShowing()) {
                    errorAlertDialog.dismiss();
                }
            });

            errorAlertDialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }
}