package com.sdp.appazul.activities.home;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdp.appazul.R;
import com.sdp.appazul.adapters.ListViewAdapter;
import com.sdp.appazul.classes.LocationFilterSecondGroup;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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

public class MenuLocationFilter extends BottomSheetDialogFragment {
    String locationResponse;
    String activityName;
    int functionalityType;
    private ListViewAdapter mAdapter;
    TextView tvLocationTitle;

    public MenuLocationFilter() {
    }

    public MenuLocationFilter(String locationResponse, String activityName, int functionalityType) {
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

        if (functionalityType == 1) {
            tvLocationTitle.setVisibility(View.VISIBLE);
            tvLocationTitle.setText(getResources().getString(R.string.generate_link_location_title));
        } else if (functionalityType == 3) {
            tvLocationTitle.setVisibility(View.VISIBLE);
            tvLocationTitle.setText(getResources().getString(R.string.card_location_message));
        } else if (functionalityType == 4) {
            tvLocationTitle.setVisibility(View.GONE);
        } else {
            tvLocationTitle.setVisibility(View.VISIBLE);
            tvLocationTitle.setText(getResources().getString(R.string.generate_qr_location_title));
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

    private void parseJsonData(String locationResponse, ExpandableListView elvInvestments) { //116

        try {
            final Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroup = new HashMap<>();

            List<LocationFilterSecondGroup> parentLevelLocationsList = new ArrayList<>();
            List<LocationFilterThirdGroup> childLevelLocationsList;
            JSONObject jsonResponse = new JSONObject(locationResponse);
            JSONArray parentLevelLocations;

            if (functionalityType == 1 || functionalityType == 3 || functionalityType == 4) {
                parentLevelLocations = jsonResponse.getJSONArray("CallCenters");
            } else {
                parentLevelLocations = jsonResponse.getJSONArray("AssignedUnits");
            }

            String parentLocationCode = "";
            for (int i = 0; i < parentLevelLocations.length(); i++) {
                childLevelLocationsList = new ArrayList<>();
                JSONObject parentData = parentLevelLocations.getJSONObject(i);
                if (functionalityType == 0) {
                    normalLocationsFilter(i, parentData, parentLocationCode, parentLevelLocationsList, childLevelLocationsList, lstItemsGroup);
                } else {
                    paymentLocationsFilter(i, parentData, parentLocationCode, parentLevelLocationsList, childLevelLocationsList, lstItemsGroup);
                }

            }


            mAdapter = new ListViewAdapter(getActivity(), parentLevelLocationsList, lstItemsGroup, activityName);
            elvInvestments.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void paymentLocationsFilter(int i, JSONObject parentData, String parentLocationCode, List<LocationFilterSecondGroup> parentLevelLocationsList, List<LocationFilterThirdGroup> childLevelLocationsList, Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroup) {
        try {
            String parentLocationName = parentData.getString("Name");
            String parentLocationId = parentData.getString("Id");
            if (functionalityType == 1 || functionalityType == 3 || functionalityType == 4) { //156
                parentLocationCode = parentData.getString("Code");
            }
            parentLevelLocationsList.add(new LocationFilterSecondGroup(parentLocationId, parentLocationName, parentLocationCode));

            locationArrayParsing(parentLocationId, parentLocationCode, parentData, childLevelLocationsList, parentLocationName);

            lstItemsGroup.put(parentLevelLocationsList.get(i), childLevelLocationsList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void normalLocationsFilter(int i, JSONObject parentData, String parentLocationCode, List<LocationFilterSecondGroup> parentLevelLocationsList, List<LocationFilterThirdGroup> childLevelLocationsList, Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroup) {
        try {
            if (!parentData.getJSONArray(Constants.ASSIGNED_LOCATION).toString().equalsIgnoreCase("[]")
                    || !parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR).toString().equalsIgnoreCase(" []")
                    && !parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR).toString().equalsIgnoreCase("[]")) { //137

            } else {
                String parentLocationName = parentData.getString("Name");
                String parentLocationId = parentData.getString("Id");
                if (functionalityType == 1 || functionalityType == 3 || functionalityType == 4) {
                    parentLocationCode = parentData.getString("Code");
                }
                parentLevelLocationsList.add(new LocationFilterSecondGroup(parentLocationId, parentLocationName, parentLocationCode));

                locationArrayParsing(parentLocationId, parentLocationCode, parentData, childLevelLocationsList, parentLocationName);

                lstItemsGroup.put(parentLevelLocationsList.get(i), childLevelLocationsList);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void locationArrayParsing(String parentLocationId, String parentLocationCode, JSONObject parentData, List<LocationFilterThirdGroup> childLevelLocationsList, String parentLocationName) {
        try {
            if (functionalityType == 1 || functionalityType == 3|| functionalityType == 4) {
                JSONArray locationsForPayment = parentData.getJSONArray("Merchants");

                for (int j = 0; j < locationsForPayment.length(); j++) {
                    JSONObject childData = locationsForPayment.getJSONObject(j);

                    String childLocationName = childData.getString("Name");
                    String childLocationCurrency = childData.getString("Currency");
                    String taxStatus;
                    taxStatus = childData.getString("ReportsTax");
                    String childMerchantId = childData.getString(Constants.MERCHANT_ID);

                    LocationFilterThirdGroup thirdGroup = new LocationFilterThirdGroup();
                    thirdGroup.setParentLocationId(parentLocationId);
                    thirdGroup.setParentCode(parentLocationCode);
                    thirdGroup.setName(childLocationName);
                    thirdGroup.setMerchantId(childMerchantId);
                    thirdGroup.setParent(parentLocationName);
                    thirdGroup.setLocationType("PaymentAffiliatedLocations");
                    thirdGroup.setTaxExempt(taxStatus);
                    thirdGroup.setCurrency(childLocationCurrency);


                    childLevelLocationsList.add(new LocationFilterThirdGroup(thirdGroup));
                }

            } else {
                JSONArray assignedLocationsQrObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR);

                for (int j = 0; j < assignedLocationsQrObject.length(); j++) {
                    JSONObject childData = assignedLocationsQrObject.getJSONObject(j);

                    String childLocationName = childData.getString("Name");
                    String taxStatus = childData.getString("TaxExempt");
                    String childMerchantId = childData.getString("MerchantId");

                    childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName, childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION_QR, taxStatus));
                }
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }


}



