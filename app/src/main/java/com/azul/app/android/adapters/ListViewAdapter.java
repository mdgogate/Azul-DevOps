package com.azul.app.android.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.azul.app.android.R;
import com.azul.app.android.activities.dashboard.DashBoardActivity;
import com.azul.app.android.activities.dashboard.QrCode;
import com.azul.app.android.activities.transactions.QrTransactions;
import com.azul.app.android.classes.LocationFilterSecondGroup;
import com.azul.app.android.classes.LocationFilterThirdGroup;
import com.azul.app.android.globals.AzulApplication;
import com.azul.app.android.globals.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewAdapter extends BaseExpandableListAdapter {

    private List<LocationFilterSecondGroup> lstGroups;
    private Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> lstItemsGroups;
    private Context context;
    Map<Integer, Integer> mChildCheckStates = new HashMap<>();
    ToggleButton toggleButton;
    String actName = "";
    SharedPreferences sharedPreferences;

    public ListViewAdapter(Context context, List<LocationFilterSecondGroup> groups, Map<LocationFilterSecondGroup, List<LocationFilterThirdGroup>> itemsGroups, String activityName) {
        this.context = context;
        this.lstGroups = groups;
        this.lstItemsGroups = itemsGroups;
        this.actName = activityName;
    }

    @Override
    public int getGroupCount() {

        return lstGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //error
        return lstItemsGroups.get(getGroup(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return lstGroups.get(groupPosition);

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return lstItemsGroups.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;
    }

    @Override
    public boolean hasStableIds() {

        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

        TextView tvGroup = (TextView) convertView.findViewById(R.id.tvGroup);
        ImageView tvAmount = (ImageView) convertView.findViewById(R.id.tvAmount);

        LocationFilterSecondGroup locationFilterSecondGroup = (LocationFilterSecondGroup) getGroup(groupPosition);
        tvGroup.setText(locationFilterSecondGroup.getName());

        isExpanded = !isExpanded;
        tvAmount.setRotation(isExpanded ? 0 : 180);


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_group, null);
        }

        toggleButton = (ToggleButton) convertView.findViewById(R.id.toggleButton);
        TextView tvItem = (TextView) convertView.findViewById(R.id.tvItem);
        TextView tvRealAmount = (TextView) convertView.findViewById(R.id.tvRealAmount);
        RelativeLayout childView = (RelativeLayout) convertView.findViewById(R.id.childView);
        if (actName.equalsIgnoreCase(Constants.QR_CODE)) {
            sharedPreferences = ((AzulApplication) ((QrCode) context).getApplication()).getPrefs();
        } else if (actName.equalsIgnoreCase(Constants.QR_TRANSACTION)) {
            sharedPreferences = ((AzulApplication) ((QrTransactions) context).getApplication()).getPrefs();
        } else {
            sharedPreferences = ((AzulApplication) ((DashBoardActivity) context).getApplication()).getPrefs();
        }

        int lastParentPosTransaction = sharedPreferences.getInt(Constants.SELECTED_PARENT_QR_TRANSACTION, 0);
        int lastChildPosTransaction = sharedPreferences.getInt(Constants.SELECTED_CHILD_QR_TRANSACTION, 0);
        String lastLocationLocTransaction = sharedPreferences.getString(Constants.SELECTED_LOCATION_QR_TRANSACTION, "");
        String lastChildMidTransaction = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_QR_TRANSACTION, "");


        int lastParentPosDash = sharedPreferences.getInt(Constants.SELECTED_PARENT_DASHBOARD, 0);
        int lastParentPosQr = sharedPreferences.getInt(Constants.SELECTED_PARENT_QRCODE, 0);
        int lastChildPosDash = sharedPreferences.getInt(Constants.SELECTED_CHILD_DASHBOARD, 0);
        int lastChildPosQr = sharedPreferences.getInt(Constants.SELECTED_CHILD_QRCODE, 0);

        String lastChildMidQr = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_QRCODE, "");
        String lastLocationQr = sharedPreferences.getString(Constants.SELECTED_LOCATION_QRCODE, "");
        String lastLocationLoc = sharedPreferences.getString(Constants.SELECTED_LOCATION_DASHBOARD, "");

        LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);

        String pravLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();

        try {
            if (actName.equalsIgnoreCase(Constants.QR_CODE)) {
                if (lastParentPosQr == groupPosition && lastChildPosQr == childPosition && lastLocationQr.equalsIgnoreCase(pravLocationName)) {
                    toggleButton.setChecked(lastChildPosQr == childPosition);
                    QrCode act = (QrCode) context;
                    act.setContent(lastLocationQr, childLocName.getName(), lastChildMidQr, 0);

                } else {
                    toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
                }
            } else if (actName.equalsIgnoreCase(Constants.QR_TRANSACTION)) {
                if (lastParentPosTransaction == groupPosition && lastChildPosTransaction == childPosition && lastLocationLocTransaction.equalsIgnoreCase(pravLocationName)) {
                    toggleButton.setChecked(lastChildPosTransaction == childPosition);
                    QrTransactions act = (QrTransactions) context;
                    act.setContent(lastLocationLocTransaction,lastChildMidTransaction,0);
                } else {
                    toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
                }
            } else {
                if (lastParentPosDash == groupPosition && lastChildPosDash == childPosition && lastLocationLoc.equalsIgnoreCase(pravLocationName)) {
                    toggleButton.setChecked(lastChildPosDash == childPosition);
                    DashBoardActivity act = (DashBoardActivity) context;
                    act.setContent(lastLocationLoc,0);
                } else {
                    toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        childView.setTag(childPosition);

        childView.setOnClickListener(view -> {
            mChildCheckStates.put(groupPosition, (Integer) view.getTag());
            if (actName.equalsIgnoreCase(Constants.QR_CODE)) {
                QrCode act = (QrCode) context;
                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());
                sharedPreferences.edit().putInt(Constants.SELECTED_PARENT_QRCODE, groupPosition).apply();
                sharedPreferences.edit().putInt(Constants.SELECTED_CHILD_QRCODE, (Integer) view.getTag()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_QRCODE, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_NAME_QRCODE, locationFilterThirdGroup.getName()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_MID_QRCODE, locationFilterThirdGroup.getMerchantId()).apply();
                act.setContent(tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(), locationFilterThirdGroup.getName(), locationFilterThirdGroup.getMerchantId(), 1);
            } else if (actName.equalsIgnoreCase(Constants.QR_TRANSACTION)) {
                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());

                QrTransactions act = (QrTransactions) context;
                sharedPreferences.edit().putInt(Constants.SELECTED_PARENT_QR_TRANSACTION, groupPosition).apply();
                sharedPreferences.edit().putInt(Constants.SELECTED_CHILD_QR_TRANSACTION, (Integer) view.getTag()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_QR_TRANSACTION, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_MID_QR_TRANSACTION, locationFilterThirdGroup.getMerchantId()).apply();
                act.setContent(tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),locationFilterThirdGroup.getMerchantId(),1);


            } else {
                DashBoardActivity act = (DashBoardActivity) context;
                act.setContent(tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),1);
                sharedPreferences.edit().putInt(Constants.SELECTED_PARENT_DASHBOARD, groupPosition).apply();
                sharedPreferences.edit().putInt(Constants.SELECTED_CHILD_DASHBOARD, (Integer) view.getTag()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_DASHBOARD, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString()).apply();

            }
            notifyDataSetChanged();
        });

        LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
        tvItem.setText(locationFilterThirdGroup.getName());
        tvRealAmount.setText(locationFilterThirdGroup.getMerchantId());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }

    public void reset(int groupPos) {
        mChildCheckStates.put(groupPos, -1);
        notifyDataSetChanged();
    }
}
