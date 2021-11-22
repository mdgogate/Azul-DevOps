package com.sdp.appazul.activities.home;

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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeLocationFilter extends BottomSheetDialogFragment {
    String locationResponse;
    TextView tvLocationTitle;
    View viewBeloTitle;
    private ListViewAdapter mAdapter;
    ExpandableListView elvInvestments;
    String activityName;
    int nameFlag;

    public HomeLocationFilter() {
    }

    public HomeLocationFilter(String locationResponse, String actName, int nameFlag) {
        this.locationResponse = locationResponse;
        this.activityName = actName;
        this.nameFlag = nameFlag;
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


        elvInvestments = (ExpandableListView) view.findViewById(R.id.elvInvestments);
        tvLocationTitle = (TextView) view.findViewById(R.id.tvLocationTitle);
        viewBeloTitle = view.findViewById(R.id.viewBeloTitle);

        if (nameFlag == 1) {
            tvLocationTitle.setVisibility(View.VISIBLE);
            viewBeloTitle.setVisibility(View.VISIBLE);
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
            parseJsonData(locationResponse);
        }
        return view;
    }

    private void parseJsonData(String locationResponse) {
        List<LocationFilterSecondGroup> parentLevelLocationsList = new ArrayList<>();
        List<LocationFilterThirdGroup> childLevelLocationsList;
        JSONArray parentLevelLocations;

        try {
            final Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroup = new HashMap<>();

            JSONObject jsonResponse = new JSONObject(locationResponse);

            parentLevelLocations = jsonResponse.getJSONArray("AssignedUnits");
            for (int i = 0; i < parentLevelLocations.length(); i++) {
                childLevelLocationsList = new ArrayList<>();
                JSONObject parentData = parentLevelLocations.getJSONObject(i);

                String parentLocationName = parentData.getString("Name");
                String parentLocationCode = "";
                String parentLocationId = parentData.getString("Id");

                JSONArray assignedLocationsQrObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR);

                if (!assignedLocationsQrObject.toString().equalsIgnoreCase("[]")) {
                    checkParentData(lstItemsGroup, parentLevelLocationsList, parentLocationId, parentLocationName, parentLocationCode, assignedLocationsQrObject, childLevelLocationsList);
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkParentData(Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroup, List<LocationFilterSecondGroup> parentLevelLocationsList, String parentLocationId, String parentLocationName, String parentLocationCode, JSONArray assignedLocationsQrObject, List<LocationFilterThirdGroup> childLevelLocationsList) {
        try {
            parentLevelLocationsList.add(new LocationFilterSecondGroup(parentLocationId, parentLocationName, parentLocationCode));


            for (int j = 0; j < assignedLocationsQrObject.length(); j++) {  //210
                JSONObject childData = assignedLocationsQrObject.getJSONObject(j);

                String childLocationName = childData.getString("Name");
                String taxStatus = childData.getString("TaxExempt");
                String childMerchantId = childData.getString(Constants.MERCHANT_ID);

                childLevelLocationsList.add(new LocationFilterThirdGroup(parentLocationId, parentLocationCode, childLocationName, childMerchantId, parentLocationName, Constants.ASSIGNED_LOCATION_QR, taxStatus));
            }

            for (int j = 0; j < parentLevelLocationsList.size(); j++) {
                lstItemsGroup.put(parentLevelLocationsList.get(j), childLevelLocationsList);
            }

            mAdapter = new ListViewAdapter(getActivity(), parentLevelLocationsList, lstItemsGroup, activityName);
            elvInvestments.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
