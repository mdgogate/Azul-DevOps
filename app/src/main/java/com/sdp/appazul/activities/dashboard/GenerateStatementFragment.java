package com.sdp.appazul.activities.dashboard;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.sdp.appazul.R;
import com.sdp.appazul.activities.transactions.BottomDateFilterFragment;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GenerateStatementFragment extends Fragment {

    RelativeLayout btnAdvancedFilter;
    RelativeLayout lastDay;
    RelativeLayout monthToDate;
    RelativeLayout previousMonth;
    String selectedMerchant;

    public GenerateStatementFragment() {
    }

    public GenerateStatementFragment(String selectedLocation) {
        this.selectedMerchant = selectedLocation;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_last, container, false);

        lastDay = (RelativeLayout) view.findViewById(R.id.lastDayBtn);
        monthToDate = (RelativeLayout) view.findViewById(R.id.monthToDateBtn);
        previousMonth = (RelativeLayout) view.findViewById(R.id.previousMonthBtn);
        Log.d("GenerateStatement", "onCreateView: " + selectedMerchant);
        initControls(view);
        onClickListeners();
        return view;
    }

    private void initControls(View fragmentView) {
        btnAdvancedFilter = fragmentView.findViewById(R.id.btnAdvancedFilter);
        btnAdvancedFilter.setOnClickListener(btnAdvancedFilterView ->
                bottomSheetOpenFunc()

        );
    }

    private void onClickListeners() {
        lastDay.setOnClickListener(v1 -> {
            DateFormat dateFormat = new SimpleDateFormat(Constants.NORMAL_DATE);
            Calendar cal = Calendar.getInstance();
            String todaysDate = dateFormat.format(cal.getTime());
            cal.add(Calendar.DATE, -1);
            String lastDay = dateFormat.format(cal.getTime());
            callPdfValidationAPI(todaysDate, todaysDate);
        });

        monthToDate.setOnClickListener(v1 -> {
            DateFormat dateFormat = new SimpleDateFormat(Constants.NORMAL_DATE);
            Calendar cal = Calendar.getInstance();
            String todaysDate = dateFormat.format(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            String lastDay = dateFormat.format(cal.getTime());
            callPdfValidationAPI(todaysDate, lastDay);
        });

        previousMonth.setOnClickListener(v1 -> {
            DateFormat dateFormat = new SimpleDateFormat(Constants.NORMAL_DATE);
            Calendar cal = Calendar.getInstance();
            String todaysDate = dateFormat.format(cal.getTime());
            cal.add(Calendar.MONTH, -1);
            String lastDay = dateFormat.format(cal.getTime());
            callPdfValidationAPI(todaysDate, lastDay);
        });
    }

    private void bottomSheetOpenFunc() {
        BottomDateFilterFragment bottomDateFilterActivity = new BottomDateFilterFragment(selectedMerchant);
        bottomDateFilterActivity.show(getActivity().getSupportFragmentManager(), "bottomSheetDateFilter");

    }

    private void callPdfValidationAPI(String todaysDate, String fromDate) {
        ApiManager apiManager = new ApiManager(getActivity());
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) getActivity().getApplicationContext()).getTcpKey();
            String vcr = ((AzulApplication) getActivity().getApplicationContext()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(getContext(), tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(getContext()));
            payload.put("format", "pdf");
            payload.put("type", "lotes");
            payload.put("dateFrom", fromDate);
            payload.put("dateTo", todaysDate);
            payload.put("merchantId", selectedMerchant);
//            payload.put("merchantId", "39036630010");


            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));
            Log.d("GET_TRANSACTION_PDF", "callPdfValidationAPI: " + payload.toString());

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.GET_TRANSACTION_PDF, json);
      ((AzulApplication) getActivity().getApplicationContext()).setPdfJson(json.toString());



    }

}

