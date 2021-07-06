package com.sdp.appazul.activities.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdp.appazul.R;
import com.sdp.appazul.adapters.ListViewAdapter;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.LocationFilterSecondGroup;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.sdp.appazul.globals.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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
    LocationFilter locationFilter;

    private void parseJsonData(String locationResponse, ExpandableListView elvInvestments) {

        try {
            final Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroup = new HashMap<>();

            List<LocationFilterSecondGroup> parentLevelLocationsList = new ArrayList<>();
            List<LocationFilterThirdGroup> childLevelLocationsList;
            JSONObject jsonResponse = new JSONObject(locationResponse);
            JSONArray parentLevelLocations = jsonResponse.getJSONArray("AssignedUnits");
            for (int i = 0; i < parentLevelLocations.length(); i++) {
                childLevelLocationsList = new ArrayList<>();
                JSONObject parentData = parentLevelLocations.getJSONObject(i);
                String parentLocatioName = parentData.getString("Name");
                parentLevelLocationsList.add(new LocationFilterSecondGroup(parentLocatioName));

                JSONArray assignedLocationsObject = parentData.getJSONArray("AssignedLocations");
                JSONArray assignedLocationsQrObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR);

                if (!activityName.equalsIgnoreCase("QrCode")) {

                    for (int j = 0; j < assignedLocationsObject.length(); j++) {
                        JSONObject childData = assignedLocationsObject.getJSONObject(j);
                        String childLocationName = childData.getString("Name");
                        String childMerchantId = childData.getString("MerchantId");
                        childLevelLocationsList.add(new LocationFilterThirdGroup(childLocationName, childMerchantId, parentLocatioName, "AssignedLocations"));
                    }
                }
                for (int j = 0; j < assignedLocationsQrObject.length(); j++) {
                    JSONObject childData = assignedLocationsQrObject.getJSONObject(j);

                    String childLocationName = childData.getString("Name");
                    String taxStatus = childData.getString("TaxExempt");
                    String childMerchantId = childData.getString("MerchantId");


                    paymentVAlidation(childLevelLocationsList,childLocationName,childMerchantId,parentLocatioName,taxStatus);

                }
                lstItemsGroup.put(parentLevelLocationsList.get(i), childLevelLocationsList);

            }


            mAdapter = new ListViewAdapter(getActivity(), parentLevelLocationsList, lstItemsGroup, activityName);
            elvInvestments.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void paymentVAlidation(List<LocationFilterThirdGroup> childLevelLocationsList, String childLocationName, String childMerchantId, String parentLocatioName, String taxStatus){
        if (activityName.equalsIgnoreCase("SET_PAYMENT")) {
            childLevelLocationsList.add(new LocationFilterThirdGroup(childLocationName,
                    childMerchantId, parentLocatioName, Constants.ASSIGNED_LOCATION_QR, taxStatus)
            );
        } else if (activityName.equalsIgnoreCase("SET_QUICK_PAYMENT")) {
            childLevelLocationsList.add(new LocationFilterThirdGroup(childLocationName,
                    childMerchantId, parentLocatioName, Constants.ASSIGNED_LOCATION_QR, taxStatus)
            );
        } else if (activityName.equalsIgnoreCase("PAYMENT_CONFIRM")) {
            childLevelLocationsList.add(new LocationFilterThirdGroup(childLocationName,
                    childMerchantId, parentLocatioName, Constants.ASSIGNED_LOCATION_QR, taxStatus)
            );
        }else if (activityName.equalsIgnoreCase("QUICK_PAYMENT_CONFIRM")) {
            childLevelLocationsList.add(new LocationFilterThirdGroup(childLocationName,
                    childMerchantId, parentLocatioName, Constants.ASSIGNED_LOCATION_QR, taxStatus)
            );
        } else if (activityName.equalsIgnoreCase(Constants.PAYMENT_VALIDATE)) {
            childLevelLocationsList.add(new LocationFilterThirdGroup(childLocationName,
                    childMerchantId, parentLocatioName, Constants.ASSIGNED_LOCATION_QR, taxStatus)
            );
        } else {
            childLevelLocationsList.add(new LocationFilterThirdGroup(childLocationName, childMerchantId, parentLocatioName, Constants.ASSIGNED_LOCATION_QR));
        }
    }

}




