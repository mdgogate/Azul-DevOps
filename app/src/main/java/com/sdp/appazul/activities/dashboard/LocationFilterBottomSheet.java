package com.sdp.appazul.activities.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdp.appazul.R;
import com.sdp.appazul.adapters.ListViewAdapter;
import com.sdp.appazul.classes.LocationFilterSecondGroup;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.sdp.appazul.globals.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sdp.appazul.globals.KeyConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationFilterBottomSheet extends BottomSheetDialogFragment {
    String locationResponse;
    String activityName;

    public LocationFilterBottomSheet() {
    }

    public LocationFilterBottomSheet(String locationResponse, String qrCode) {
        this.locationResponse = locationResponse;
        this.activityName = qrCode;
    }


    private ListViewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_location_filter_bottomsheet, container, false);


        ExpandableListView elvInvestments = (ExpandableListView) view.findViewById(R.id.elvInvestments);
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


        elvInvestments.setOnGroupCollapseListener(groupPosition -> mAdapter.reset(groupPosition));

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
            if (activityName.equalsIgnoreCase(Constants.SET_PAYMENT)
                    || activityName.equalsIgnoreCase(Constants.PAYMENT_VALIDATE)
                    || activityName.equalsIgnoreCase(Constants.PAYMENT_TRANSACTION)
                    || activityName.equalsIgnoreCase(Constants.PAYMENT_CONFIRM)) {
                parentLevelLocations = jsonResponse.getJSONArray("CallCenters");
            } else {
                parentLevelLocations = jsonResponse.getJSONArray("AssignedUnits");
            }

            childLevelLocationsList = new ArrayList<>();
            String parentLocationCode = "";
            String parentLocationId = "";
            for (int i = 0; i < parentLevelLocations.length(); i++) {
                JSONObject parentData = parentLevelLocations.getJSONObject(i);
                if (activityName.equalsIgnoreCase(Constants.SET_PAYMENT)
                        || activityName.equalsIgnoreCase(Constants.PAYMENT_VALIDATE)
                        || activityName.equalsIgnoreCase(Constants.PAYMENT_TRANSACTION)
                        || activityName.equalsIgnoreCase(Constants.PAYMENT_CONFIRM)) {
                    parentLocationCode = parentData.getString("Code");
                }
                String parentLocationName = parentData.getString("Name");
                parentLocationId = parentData.getString("Id");

                parentLevelLocationsList.add(new LocationFilterSecondGroup(parentLocationId, parentLocationName, parentLocationCode));
                JSONArray assignedLocationsQrObject;
                JSONArray assignedLocationsObject;

                if (activityName.equalsIgnoreCase(Constants.SET_PAYMENT)
                        || activityName.equalsIgnoreCase(Constants.PAYMENT_VALIDATE)
                        || activityName.equalsIgnoreCase(Constants.PAYMENT_TRANSACTION)
                        || activityName.equalsIgnoreCase(Constants.PAYMENT_CONFIRM)) {
                    assignedLocationsObject = parentData.getJSONArray("Merchants");
                    paymentLocationFilter(parentLocationId, parentLocationName, childLevelLocationsList, assignedLocationsObject);
                } else {
                    assignedLocationsObject = parentData.getJSONArray("AssignedLocations");
                    assignedLocationsQrObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR);
                    assignedLocationFiltering(parentLocationId, parentLocationName, assignedLocationsObject, childLevelLocationsList);
                    assignedLocationsQrFiltering(parentLocationId, parentLocationCode, parentLocationName, assignedLocationsQrObject, childLevelLocationsList);
                }

                lstItemsGroup.put(parentLevelLocationsList.get(i), childLevelLocationsList);
            }

            mAdapter = new ListViewAdapter(getActivity(), parentLevelLocationsList, lstItemsGroup, activityName);
            elvInvestments.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void assignedLocationsQrFiltering(String parentLocationId, String parentLocationCode, String parentLocationName, JSONArray assignedLocationsQrObject, List<LocationFilterThirdGroup> childLevelLocationsList) {
        try {
            if (!activityName.equalsIgnoreCase("SettledTransactionsQuery")) {
                for (int j = 0; j < assignedLocationsQrObject.length(); j++) {
                    JSONObject childData = assignedLocationsQrObject.getJSONObject(j);

                    String childLocationName = childData.getString("Name");
                    String taxStatus = childData.getString("TaxExempt");
                    String childMerchantId = childData.getString(Constants.MERCHANT_ID);

                    paymentValidation(parentLocationId, parentLocationCode, childLevelLocationsList, childLocationName, childMerchantId, parentLocationName, taxStatus);

                }
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void paymentLocationFilter(String parentLocationId, String parentLocationName, List<LocationFilterThirdGroup> childLevelLocationsList, JSONArray assignedLocationsObject) {
        try {
            for (int j = 0; j < assignedLocationsObject.length(); j++) {
                JSONObject childData = assignedLocationsObject.getJSONObject(j);

                String childLocationName = childData.getString("Name");
                String taxStatus;
                taxStatus = childData.getString("ReportsTax");
                String childMerchantId = childData.getString(Constants.MERCHANT_ID);

                paymentValidation(parentLocationId, parentLocationName, childLevelLocationsList, childLocationName, childMerchantId, parentLocationName, taxStatus);
            }

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void assignedLocationFiltering(String parentLocationId, String parentLocationName, JSONArray assignedLocationsObject, List<LocationFilterThirdGroup> childLevelLocationsList) {
        try {
            if (!activityName.equalsIgnoreCase("QrCode")) {

                for (int j = 0; j < assignedLocationsObject.length(); j++) {
                    JSONObject childData = assignedLocationsObject.getJSONObject(j);
                    String childLocationName = childData.getString("Name");
                    String childMerchantId = childData.getString(Constants.MERCHANT_ID);
                    childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, childLocationName, childMerchantId, parentLocationName, "AssignedLocations"));
                }
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void paymentValidation(String parentLocationId, String parentLocationCode, List<LocationFilterThirdGroup> childLevelLocationsList, String childLocationName, String childMerchantId, String parentLocationName, String taxStatus) {
        if (activityName.equalsIgnoreCase(Constants.SET_PAYMENT)) {

            childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName,
                    childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION_QR, taxStatus)
            );
        } else if (activityName.equalsIgnoreCase(Constants.SET_QUICK_PAYMENT)) {
            childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName,
                    childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION_QR, taxStatus)
            );
        } else if (activityName.equalsIgnoreCase(Constants.PAYMENT_CONFIRM)) {
            childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName,
                    childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION_QR, taxStatus)
            );
        } else if (activityName.equalsIgnoreCase(Constants.PAYMENT_VALIDATE)) {
            childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName,
                    childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION_QR, taxStatus)
            );
        } else if (activityName.equalsIgnoreCase(Constants.PAYMENT_TRANSACTION)) {
            childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName,
                    childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION_QR, taxStatus)
            );
        } else {
            Log.d("TAG", "paymentValidation: " + parentLocationId + parentLocationCode);
            childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName,
                    childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION_QR, ""));
        }
    }

}




