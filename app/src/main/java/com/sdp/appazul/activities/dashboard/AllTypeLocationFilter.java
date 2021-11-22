package com.sdp.appazul.activities.dashboard;

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

public class AllTypeLocationFilter extends BottomSheetDialogFragment {
    String locationResponse;
    String activityName;
    int functionalityType;
    private ListViewAdapter mAdapter;
    TextView tvLocationTitle;
    View viewBeloTitle;

    public AllTypeLocationFilter() {
    }

    public AllTypeLocationFilter(String locationResponse, String activityName, int functionalityType) {
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

        tvLocationTitle.setVisibility(View.GONE);
        viewBeloTitle.setVisibility(View.GONE);


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

            String parentLocationCode = "";
            for (int i = 0; i < parentLevelLocations.length(); i++) {
                childLevelLocationsList = new ArrayList<>();
                JSONObject parentData = parentLevelLocations.getJSONObject(i);
                if (functionalityType == 0) {
                    normalLocations(i, parentData, parentLocationCode, parentLevelLocationsList, childLevelLocationsList, lstItemsGroup);
                } else {
                    paymentLocations(i, parentData, parentLocationCode, parentLevelLocationsList, childLevelLocationsList, lstItemsGroup);
                }
            }


            mAdapter = new ListViewAdapter(getActivity(), parentLevelLocationsList, lstItemsGroup, activityName);
            elvInvestments.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void paymentLocations(int i, JSONObject parentData, String parentLocationCode, List<LocationFilterSecondGroup> parentLevelLocationsList, List<LocationFilterThirdGroup> childLevelLocationsList, Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroup) {
        try {
            String parentLocationName = parentData.getString("Name");
            String parentLocationId = parentData.getString("Id");
            if (functionalityType == 1) {
                parentLocationCode = parentData.getString("Code");
            }
            parentLevelLocationsList.add(new LocationFilterSecondGroup(parentLocationId, parentLocationName, parentLocationCode));

            locationArrayParsing( parentLocationId, parentLocationCode, parentData, childLevelLocationsList, parentLocationName);

            lstItemsGroup.put(parentLevelLocationsList.get(i), childLevelLocationsList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void normalLocations(int i, JSONObject parentData, String parentLocationCode, List<LocationFilterSecondGroup> parentLevelLocationsList, List<LocationFilterThirdGroup> childLevelLocationsList, Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroup) {
        try {
            if (!parentData.getJSONArray(Constants.ASSIGNED_LOCATION).toString().equalsIgnoreCase(" []")
                    && !parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR).toString().equalsIgnoreCase(" []")
                    || !parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR).toString().equalsIgnoreCase("[]")) {

                String parentLocationName = parentData.getString("Name");
                String parentLocationId = parentData.getString("Id");

                if (functionalityType == 1) {
                    parentLocationCode = parentData.getString("Code");
                }
                parentLevelLocationsList.add(new LocationFilterSecondGroup( parentLocationId, parentLocationName, parentLocationCode));

                locationArrayParsing( parentLocationId, parentLocationCode, parentData, childLevelLocationsList, parentLocationName);

                lstItemsGroup.put(parentLevelLocationsList.get(i), childLevelLocationsList);

            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void locationArrayParsing( String parentLocationId, String parentLocationCode, JSONObject parentData, List<LocationFilterThirdGroup> childLevelLocationsList, String parentLocationName) {
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
                JSONArray assignedLocationsObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION);

                for (int j = 0; j < assignedLocationsObject.length(); j++) {
                    JSONObject childData = assignedLocationsObject.getJSONObject(j);

                    String childLocationName = childData.getString("Name");
                    String childMerchantId = childData.getString("MerchantId");
                    String childCurrencyCode = childData.getString("CurrencyCode");

                    LocationFilterThirdGroup thirdGroup = new LocationFilterThirdGroup();
                    thirdGroup.setParentLocationId(parentLocationId);
                    thirdGroup.setParentCode(parentLocationCode);
                    thirdGroup.setName(childLocationName);
                    thirdGroup.setMerchantId(childMerchantId);
                    thirdGroup.setParent(parentLocationName);
                    thirdGroup.setLocationType(Constants.ASSIGNED_LOCATION);
                    thirdGroup.setTaxExempt("");
                    thirdGroup.setCurrency(childCurrencyCode);
                    Log.d("AllTypeLocationFilter", "locationArrayParsing: 111 )  CurrencyType :::  " + childCurrencyCode);
                    childLevelLocationsList.add(new LocationFilterThirdGroup(thirdGroup));

                }

                JSONArray assignedLocationsQrObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR);

                for (int j = 0; j < assignedLocationsQrObject.length(); j++) {
                    JSONObject childData = assignedLocationsQrObject.getJSONObject(j);

                    String childLocationName = childData.getString("Name");
                    String taxStatus = childData.getString("TaxExempt");
                    String childMerchantId = childData.getString("MerchantId");
                    LocationFilterThirdGroup thirdGroup = new LocationFilterThirdGroup();
                    thirdGroup.setParentLocationId(parentLocationId);
                    thirdGroup.setParentCode(parentLocationCode);
                    thirdGroup.setName(childLocationName);
                    thirdGroup.setMerchantId(childMerchantId);
                    thirdGroup.setParent(parentLocationName);
                    thirdGroup.setLocationType(Constants.ASSIGNED_LOCATION_QR);
                    thirdGroup.setTaxExempt(taxStatus);
                    thirdGroup.setCurrency("DOP");
                    childLevelLocationsList.add(new LocationFilterThirdGroup(thirdGroup));
                }

            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

}
