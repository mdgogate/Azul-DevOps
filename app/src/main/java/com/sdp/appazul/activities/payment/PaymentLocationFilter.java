package com.sdp.appazul.activities.payment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sdp.appazul.R;
import com.sdp.appazul.adapters.ListViewAdapter;
import com.sdp.appazul.classes.LocationFilterSecondGroup;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentLocationFilter extends BottomSheetDialogFragment {

    String locationResponse;
    String activityName;
    int functionalityType;
    private ListViewAdapter mAdapter;
    TextView tvLocationTitle;
    View viewBeloTitle;

    public PaymentLocationFilter() {
    }

    public PaymentLocationFilter(String locationResponse, String activityName, int functionalityType) {
        this.locationResponse = locationResponse;
        this.activityName = activityName;
        this.functionalityType = functionalityType;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        bottomSheetDialog.setOnShowListener(dialogInterface ->
                Log.d("", "")
        );


        return bottomSheetDialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_location_filter_sheet, container, false);


        ExpandableListView elvInvestments = (ExpandableListView) view.findViewById(R.id.elvInvestments);
        tvLocationTitle = (TextView) view.findViewById(R.id.tvLocationTitle);
        viewBeloTitle = view.findViewById(R.id.viewBeloTitle);

        if (functionalityType == 2) {
            tvLocationTitle.setVisibility(View.VISIBLE);
            viewBeloTitle.setVisibility(View.VISIBLE);
        } else if (functionalityType == 1) {
            tvLocationTitle.setVisibility(View.GONE);
            viewBeloTitle.setVisibility(View.GONE);
        } else {
            tvLocationTitle.setVisibility(View.GONE);
            viewBeloTitle.setVisibility(View.GONE);
        }

        elvInvestments.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {

                if (groupPosition != previousGroup) {
                    elvInvestments.collapseGroup(previousGroup);
                    mAdapter.reset(groupPosition);

                }
                previousGroup = groupPosition;
            }
        });


        elvInvestments.setOnGroupCollapseListener(groupPosition ->

                mAdapter.reset(groupPosition));


        if (!TextUtils.isEmpty(locationResponse)) {
            parseJsonData(locationResponse, elvInvestments);
        }
        return view;
    }

    private void parseJsonData(String locationResponse, ExpandableListView elvInvestments) {

        try {
            final Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroup = new HashMap<>();

            List<LocationFilterSecondGroup> parentLevelLocationsList = new ArrayList<>();
            List<LocationFilterThirdGroup> childLevelLocationsList;
            JSONObject jsonResponse = new JSONObject(locationResponse);
            JSONArray parentLevelLocations;

            if (functionalityType == 1) {
                parentLevelLocations = jsonResponse.getJSONArray("CallCenters");
            } else {
                parentLevelLocations = jsonResponse.getJSONArray("AssignedUnits");
            }

            if (functionalityType != 1) {
                for (int i = 0; i < parentLevelLocations.length(); i++) {
                    childLevelLocationsList = new ArrayList<>();
                    JSONObject parentData = parentLevelLocations.getJSONObject(i);
                    normalLocationsFilter(i, lstItemsGroup, childLevelLocationsList, parentData, parentLevelLocationsList, parentLevelLocations);
                }
            }


            mAdapter = new ListViewAdapter(getActivity(), parentLevelLocationsList, lstItemsGroup, activityName);
            elvInvestments.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void normalLocationsFilter(int i, Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroup, List<LocationFilterThirdGroup> childLevelLocationsList, JSONObject parentData, List<LocationFilterSecondGroup> parentLevelLocationsList, JSONArray parentLevelLocations) {
        try {
            if (!parentData.getJSONArray(Constants.ASSIGNED_LOCATION).toString().equalsIgnoreCase("[]")
                    || !parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR).toString().equalsIgnoreCase(" []")
                    && !parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR).toString().equalsIgnoreCase("[]")) {

                String parentLocationName = parentData.getString("Name");
                String parentLocationCode = "";

                if (!activityName.equalsIgnoreCase("SettledTransactionsQuery") &&
                        !activityName.equalsIgnoreCase("QrTransactions") &&
                        !activityName.equalsIgnoreCase("Menu") &&
                        !activityName.equalsIgnoreCase("QrCode")) {
                    parentLocationCode = parentData.getString("Code");
                }
                String parentLocationId = parentData.getString("Id");
                parentLevelLocationsList.add(new LocationFilterSecondGroup(parentLocationId, parentLocationName, parentLocationCode));

                locationArrayParsing(parentLevelLocationsList, parentLocationId, parentLocationCode, parentData, childLevelLocationsList, parentLocationName);

                lstItemsGroup.put(parentLevelLocationsList.get(i), childLevelLocationsList);
            } else {
                Log.d("PaymentLocationFilter", "normalLocationsFilter: 2222   " + parentData.getString("Name"));
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void locationArrayParsing(List<LocationFilterSecondGroup> parentLevelLocationsList, String parentLocationId, String parentLocationCode, //173
                                      JSONObject parentData,
                                      List<LocationFilterThirdGroup> childLevelLocationsList,
                                      String parentLocationName) {
        try {
            if (functionalityType == 1) {
                JSONArray locationsForPayment = parentData.getJSONArray("Merchants");

                for (int j = 0; j < locationsForPayment.length(); j++) {
                    JSONObject childData = locationsForPayment.getJSONObject(j);

                    String childLocationName = childData.getString("Name");
                    String taxStatus;
                    taxStatus = childData.getString("ReportsTax");
                    String childMerchantId = childData.getString(Constants.MERCHANT_ID);

                    childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName, childMerchantId, parentLocationName, "PaymentAffiliatedLocations", taxStatus));
                }

            } else {
                normalTransactions(parentData, childLevelLocationsList, parentLocationId, parentLocationCode, parentLocationName);

            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void normalTransactions(JSONObject parentData, List<LocationFilterThirdGroup> childLevelLocationsList,
                                    String parentLocationId, String parentLocationCode, String parentLocationName) {
        try {
            if (activityName.equalsIgnoreCase("SettledTransactionsQuery")) {
                JSONArray assignedLocationsQrObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION);

                for (int j = 0; j < assignedLocationsQrObject.length(); j++) { //196
                    JSONObject childData = assignedLocationsQrObject.getJSONObject(j);

                    String childLocationName = childData.getString("Name");
                    String childMerchantId = childData.getString(Constants.MERCHANT_ID);

                    childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName, childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION, ""));
                }

            } else if (activityName.equalsIgnoreCase("QrTransactions")
                    || activityName.equalsIgnoreCase("QrCode") || activityName.equalsIgnoreCase("Menu")) {
                JSONArray assignedLocationsQrObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR);

                for (int j = 0; j < assignedLocationsQrObject.length(); j++) {  //210
                    JSONObject childData = assignedLocationsQrObject.getJSONObject(j);

                    String childLocationName = childData.getString("Name");
                    String taxStatus = childData.getString("TaxExempt");
                    String childMerchantId = childData.getString(Constants.MERCHANT_ID);

                    childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName, childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION_QR, taxStatus));
                }

            } else {
                JSONArray assignedLocationsQrObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR);

                for (int j = 0; j < assignedLocationsQrObject.length(); j++) {  //222
                    JSONObject childData = assignedLocationsQrObject.getJSONObject(j);

                    String childLocationName = childData.getString("Name");
                    String taxStatus = childData.getString("TaxExempt");
                    String childMerchantId = childData.getString(Constants.MERCHANT_ID);

                    childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName, childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION_QR, taxStatus));
                }
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }


}



