package com.sdp.appazul.activities.home;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdp.appazul.R;
import com.sdp.appazul.adapters.ListViewAdapter;
import com.sdp.appazul.classes.LocationFilterSecondGroup;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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
    private ListViewAdapter mAdapter;

    public MenuLocationFilter() {
    }

    public MenuLocationFilter(String locationResponse, String activityName) {
        this.locationResponse = locationResponse;
        this.activityName = activityName;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        bottomSheetDialog.setOnShowListener(dialogInterface ->
                Log.d("ONSHOW", "")
        );


        return bottomSheetDialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_location_filter_sheet, container, false);


        ExpandableListView elvInvestments = (ExpandableListView) view.findViewById(R.id.elvInvestments);

        elvInvestments.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            RelativeLayout layout = view.findViewById(R.id.layoutExpand);
            ViewGroup.LayoutParams params = layout.getLayoutParams();

            @Override
            public void onGroupExpand(int groupPosition) {

                params.height = 960;
                layout.setLayoutParams(params);

                if (groupPosition != previousGroup) {
                    elvInvestments.collapseGroup(previousGroup);
                    mAdapter.reset(groupPosition);

                    params.height = 960;
                    layout.setLayoutParams(params);
                }
                previousGroup = groupPosition;
            }
        });


        elvInvestments.setOnGroupCollapseListener(groupPosition -> {

            RelativeLayout layout = view.findViewById(R.id.layoutExpand);
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = 580;
            layout.setLayoutParams(params);

            mAdapter.reset(groupPosition);

        });


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

                JSONArray assignedLocationsQrObject = parentData.getJSONArray("AssignedLocationsQR");
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



