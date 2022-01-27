package com.sdp.appazul.activities.menuitems;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sdp.appazul.R;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DateUtils;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.getInstance;


public class BurgerMenuBottomSheet extends BottomSheetDialogFragment {
    RelativeLayout deregisterUser;
    String loginResponse;
    TextView clientUserName;
    TextView clientUserProfile;
    TextView tvVersion;
    TextView lastLoginDate;
    GregorianCalendar month;
    List<String> monthList = new ArrayList<>();
    DateUtils utils = new DateUtils();

    public BurgerMenuBottomSheet() {
    }

    public BurgerMenuBottomSheet(String locationFilterJson) {
        this.loginResponse = locationFilterJson;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_burger_menu_bottomsheet, container, false);
        deregisterUser = view.findViewById(R.id.deregisterUser);
        clientUserName = view.findViewById(R.id.clientUserName);
        tvVersion = view.findViewById(R.id.tvVersion);
        clientUserProfile = view.findViewById(R.id.clientUserProfile);
        lastLoginDate = view.findViewById(R.id.lastLoginDate);
        setUserData(loginResponse);
        onClickListener();
        GlobalFunctions globalFunctions = new GlobalFunctions(getActivity());
        tvVersion.setText(getActivity().getString(R.string.version_label).concat(" ").concat(globalFunctions.getAppVersion()));

        return view;
    }

    private void setUserData(String loginResponse) {
        try {
            if (loginResponse != null && !loginResponse.equalsIgnoreCase("")) {
                JSONObject jsonObject = new JSONObject(loginResponse);
                String userName = jsonObject.getString("Name");
                String groupName = jsonObject.getString(Constants.GROUP_NAME);

                if (userName.equalsIgnoreCase("")) {
                    clientUserName.setText("-");
                } else {
                    clientUserName.setText(userName);
                }
                if (groupName.equalsIgnoreCase("")) {
                    clientUserProfile.setText("-");
                } else {
                    clientUserProfile.setText(groupName);
                }
                monthList = utils.getmonthListWithCaps();
                month = (GregorianCalendar) getInstance();

                String spanishMonthName = monthList
                        .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
                String year = (String) DateFormat.format(Constants.YYYY, month);

                String date = "" + month.get(DAY_OF_MONTH);

                String currentDateToDisplay = date + " de " + spanishMonthName + ", " + year;
                lastLoginDate.setText(currentDateToDisplay);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void onClickListener() {
        deregisterUser.setOnClickListener(deregisterUserView -> callDeregisterApi());
    }


    private void callDeregisterApi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.unlink_lbl));
        builder.setMessage(getResources().getString(R.string.unlink_account_msg));
        builder.setCancelable(false);
        builder.setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i) -> dismiss());
        builder.setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
            ApiManager apiManager = new ApiManager(getActivity());
            JSONObject json = new JSONObject();
            try {
                String tcpKey = ((AzulApplication) getActivity().getApplicationContext()).getTcpKey();
                json.put("tcp", RSAHelper.ecryptRSA(getContext(), tcpKey));
                json.put(Constants.DEVICE, RSAHelper.ecryptRSA(getContext(), DeviceUtils.getDeviceId(getContext())));

            } catch (Exception e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }
            apiManager.callAPI(ServiceUrls.DEREGISTER_USER, json);
        });
        builder.show();

    }

}




