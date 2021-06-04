package com.azul.app.android.activities.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.azul.app.android.R;
import com.azul.app.android.adapters.ListViewAdapter;
import com.azul.app.android.classes.LocationFilterSecondGroup;
import com.azul.app.android.classes.LocationFilterThirdGroup;
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
                JSONArray assignedLocationsQrObject = parentData.getJSONArray("AssignedLocationsQR");
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
                    String childMerchantId = childData.getString("MerchantId");
                    childLevelLocationsList.add(new LocationFilterThirdGroup(childLocationName, childMerchantId, parentLocatioName, "AssignedLocationsQR"));
                }
                lstItemsGroup.put(parentLevelLocationsList.get(i), childLevelLocationsList);

            }


            mAdapter = new ListViewAdapter(getActivity(), parentLevelLocationsList, lstItemsGroup, activityName);
            elvInvestments.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}




