package com.sdp.appazul.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.dashboard.QrCode;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.payment.PaymentConfirmActivity;
import com.sdp.appazul.activities.payment.PaymentDataValidateActivity;
import com.sdp.appazul.activities.payment.QuickPayConfirmActivity;
import com.sdp.appazul.activities.payment.QuickSetPaymentActivity;
import com.sdp.appazul.activities.payment.SetPaymentInfoActivity;
import com.sdp.appazul.activities.transactions.PaymentLinkTransactions;
import com.sdp.appazul.activities.transactions.QrTransactions;
import com.sdp.appazul.activities.transactions.SettledTransactionsQuery;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.LocationFilterSecondGroup;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

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
    LocationFilter gettingLocationsData;

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

        preferencesInstanceCheck(groupPosition, childPosition);


        checkActivities(actName, groupPosition, childPosition);
        childView.setTag(childPosition);

        childView.setOnClickListener(view -> {
            mChildCheckStates.put(groupPosition, (Integer) view.getTag());
            LocationFilterSecondGroup parentObjectData = (LocationFilterSecondGroup) getGroup(groupPosition);

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
                sharedPreferences.edit().putInt(Constants.SELECTED_PARENT_QRCODE, groupPosition).apply();
                sharedPreferences.edit().putInt(Constants.SELECTED_CHILD_QRCODE, (Integer) view.getTag()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_QRCODE, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_NAME_QRCODE, locationFilterThirdGroup.getName()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_MID_QRCODE, locationFilterThirdGroup.getMerchantId()).apply();
                act.setContent(tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(), locationFilterThirdGroup.getMerchantId(), 1);

            } else if (actName.equalsIgnoreCase(Constants.SETTLE_TRANSACTION)) {
                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());
                SettledTransactionsQuery query = (SettledTransactionsQuery) context;
                query.setContent(tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(), 1);

                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_MID_DASHBOARD, locationFilterThirdGroup.getMerchantId()).apply();
                sharedPreferences.edit().putInt(Constants.SELECTED_PARENT_DASHBOARD, groupPosition).apply();
                sharedPreferences.edit().putInt(Constants.SELECTED_CHILD_DASHBOARD, (Integer) view.getTag()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_DASHBOARD, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString()).apply();

            } else if (actName.equalsIgnoreCase(Constants.SET_PAYMENT)) {
                LocationFilterSecondGroup locationFilterSecondGroup = (LocationFilterSecondGroup) getGroup(groupPosition);
                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());
                SetPaymentInfoActivity query = (SetPaymentInfoActivity) context;
                query.setContent(tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(), 1, locationFilterSecondGroup.getCode(), locationFilterThirdGroup);

                setLocationData(locationFilterThirdGroup, Constants.SET_PAYMENT, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(),
                        groupPosition, (Integer) view.getTag(), locationFilterThirdGroup.getTaxExempt());

            } else if (actName.equalsIgnoreCase(Constants.SET_QUICK_PAYMENT)) {
                LocationFilterSecondGroup locationFilterSecondGroup = (LocationFilterSecondGroup) getGroup(groupPosition);
                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());
                QuickSetPaymentActivity query = (QuickSetPaymentActivity) context;
                query.setContent(tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(), 1, locationFilterSecondGroup.getCode());

                setLocationData(locationFilterThirdGroup, Constants.SET_QUICK_PAYMENT, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(),
                        groupPosition, (Integer) view.getTag(), locationFilterThirdGroup.getTaxExempt());

            } else if (actName.equalsIgnoreCase(Constants.PAYMENT_CONFIRM)) {
                LocationFilterSecondGroup locationFilterSecondGroup = (LocationFilterSecondGroup) getGroup(groupPosition);
                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());


                PaymentConfirmActivity query = (PaymentConfirmActivity) context;
                query.setContent(parentObjectData.getName(), locationFilterThirdGroup.getName(),
                        tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(),
                        1, locationFilterThirdGroup,
                        locationFilterSecondGroup.getCode());

                setLocationData(locationFilterThirdGroup, Constants.PAYMENT_CONFIRM, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(),
                        groupPosition, (Integer) view.getTag(), locationFilterThirdGroup.getTaxExempt());

            } else if (actName.equalsIgnoreCase(Constants.QUICK_PAYMENT_CONFIRM)) {
                LocationFilterSecondGroup locationFilterSecondGroup = (LocationFilterSecondGroup) getGroup(groupPosition);
                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());


                QuickPayConfirmActivity query = (QuickPayConfirmActivity) context;
                query.setContent(parentObjectData.getName(), locationFilterThirdGroup.getName(), tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(), 1, locationFilterThirdGroup, locationFilterSecondGroup.getCode());

                setLocationData(locationFilterThirdGroup, Constants.QUICK_PAYMENT_CONFIRM, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(),
                        groupPosition, (Integer) view.getTag(), locationFilterThirdGroup.getTaxExempt());

            } else if (actName.equalsIgnoreCase("Menu")) {
                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());
                MainMenuActivity act = (MainMenuActivity) context;
                act.setContent(parentObjectData.getCode(), parentObjectData.getName(), locationFilterThirdGroup.getName(),
                        locationFilterThirdGroup.getMerchantId(),
                        1, locationFilterThirdGroup);
                setLocationData(locationFilterThirdGroup, "Menu", tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(),
                        groupPosition, (Integer) view.getTag(), locationFilterThirdGroup.getTaxExempt());

            } else if (actName.equalsIgnoreCase(Constants.PAYMENT_VALIDATE)) {
                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());
                PaymentDataValidateActivity query = (PaymentDataValidateActivity) context;
                query.setContent(locationFilterThirdGroup.getName(), tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(), 1, locationFilterThirdGroup.getTaxExempt());

                setLocationData(locationFilterThirdGroup, Constants.PAYMENT_VALIDATE, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(),
                        groupPosition, (Integer) view.getTag(), locationFilterThirdGroup.getTaxExempt());

            } else if (actName.equalsIgnoreCase(Constants.PAYMENT_TRANSACTION)) {
                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());
                PaymentLinkTransactions query = (PaymentLinkTransactions) context;
                query.setContent(locationFilterThirdGroup.getName(), tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(), 1, locationFilterThirdGroup.getTaxExempt());

                setLocationData(locationFilterThirdGroup, Constants.PAYMENT_TRANSACTION, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        locationFilterThirdGroup.getMerchantId(),
                        groupPosition, (Integer) view.getTag(), locationFilterThirdGroup.getTaxExempt());

            } else {

                LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, (Integer) view.getTag());
                DashBoardActivity act = (DashBoardActivity) context;
                act.setContent(locationFilterThirdGroup.getLocationType(),
                        locationFilterThirdGroup.getMerchantId(),
                        tvItem.getText().toString() + " - " + tvRealAmount.getText().toString(),
                        1, locationFilterThirdGroup.getCurrency());
                sharedPreferences.edit().putInt(Constants.SELECTED_PARENT_DASHBOARD, groupPosition).apply();
                sharedPreferences.edit().putInt(Constants.SELECTED_CHILD_DASHBOARD, (Integer) view.getTag()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_DASHBOARD, tvItem.getText().toString() + " - " + tvRealAmount.getText().toString()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_MID_DASHBOARD, locationFilterThirdGroup.getMerchantId()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_TYPE_DASHBOARD, locationFilterThirdGroup.getLocationType()).apply();
                sharedPreferences.edit().putString(Constants.SELECTED_CUURENCY_DASHBOARD, locationFilterThirdGroup.getCurrency()).apply();

            }
            notifyDataSetChanged();
        });

        LocationFilterThirdGroup locationFilterThirdGroup = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);


        tvItem.setText(locationFilterThirdGroup.getName());
        tvRealAmount.setText(locationFilterThirdGroup.getMerchantId());


        return convertView;
    }

    private void checkActivities(String actName, int groupPosition, int childPosition) {
        if (!actName.equalsIgnoreCase(Constants.SET_PAYMENT)
                && !actName.equalsIgnoreCase(Constants.PAYMENT_CONFIRM)
                && !actName.equalsIgnoreCase(Constants.QUICK_PAYMENT_CONFIRM)) {

            if (!actName.equalsIgnoreCase("Menu")) {
                if (!actName.equalsIgnoreCase(Constants.SET_QUICK_PAYMENT)
                        && !actName.equalsIgnoreCase(Constants.PAYMENT_TRANSACTION)
                        && !actName.equalsIgnoreCase(Constants.PAYMENT_VALIDATE)) {
                    otherChildValidations(groupPosition, childPosition);
                }
            }
        }
    }


    private void otherChildValidations(int groupPosition, int childPosition) {
        LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
        String lastChildTypeDash = sharedPreferences.getString(Constants.SELECTED_LOCATION_TYPE_DASHBOARD, "");


        int lastParentPosDash = sharedPreferences.getInt(Constants.SELECTED_PARENT_DASHBOARD, 0);
        int lastParentPosQr = sharedPreferences.getInt(Constants.SELECTED_PARENT_QRCODE, 0);
        int lastChildPosQr = sharedPreferences.getInt(Constants.SELECTED_CHILD_QRCODE, 0);
        String lastLocationQr = sharedPreferences.getString(Constants.SELECTED_LOCATION_QRCODE, "");
        int lastChildPosDash = sharedPreferences.getInt(Constants.SELECTED_CHILD_DASHBOARD, 0);
        long lastChildPosDashLong = sharedPreferences.getLong(Constants.SELECTED_CHILD_DASHBOARD_STRING, 0);

        String lastChildMidQr = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_QRCODE, "");
        String lastLocationLoc = sharedPreferences.getString(Constants.SELECTED_LOCATION_DASHBOARD, "");
        String previousLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();

        try {
            if (actName.equalsIgnoreCase(Constants.QR_CODE)) {
                if (lastParentPosQr == groupPosition &&
                        lastChildPosQr == childPosition &&
                        lastLocationQr.equalsIgnoreCase(previousLocationName)) {
                    toggleButton.setChecked(lastChildPosQr == childPosition);
                    QrCode act = (QrCode) context;
                    act.setContent(lastLocationQr, childLocName.getName(), lastChildMidQr, 0);
                } else {
                    toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
                }
            } else if (actName.equalsIgnoreCase(Constants.DASH_BOARD)) {
                if (lastChildPosDashLong != 0) {
                    previousSelectedDashboard(lastLocationLoc,lastChildPosDash,previousLocationName,lastParentPosDash,
                            lastChildPosDashLong,groupPosition,childPosition);


                } else {
                    dashboardPositionCheck(groupPosition, lastChildPosDash, childPosition, lastLocationLoc, previousLocationName, lastChildTypeDash);
                }
            }

            transactionChildValidations(groupPosition, childPosition);

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void previousSelectedDashboard(String lastLocationLoc, int lastChildPosDash, String previousLocationName, int lastParentPosDash, long lastChildPosDashLong, int groupPosition, int childPosition){
        LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
        String lastChildTypeDash = sharedPreferences.getString(Constants.SELECTED_LOCATION_TYPE_DASHBOARD, "");
        String lastChildMidDash = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_DASHBOARD, "");
        if (lastParentPosDash == groupPosition &&
                lastChildPosDashLong == childPosition &&
                lastLocationLoc.equalsIgnoreCase(previousLocationName)) {
            toggleButton.setChecked(lastChildPosDash == childPosition);
            DashBoardActivity act = (DashBoardActivity) context;
            act.setContent(lastChildTypeDash, lastChildMidDash, lastLocationLoc, 0, childLocName.getCurrency());
        } else {
            toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
        }
    }
    private void dashboardPositionCheck(int groupPosition, int lastChildPosDash, int childPosition, String lastLocationLoc, String previousLocationName, String lastChildTypeDash) {
        int lastParentPosDash = sharedPreferences.getInt(Constants.SELECTED_PARENT_DASHBOARD, 0);
        String lastChildMidDash = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_DASHBOARD, "");
        LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
        if (lastParentPosDash == groupPosition &&
                lastChildPosDash == childPosition &&
                lastLocationLoc.equalsIgnoreCase(previousLocationName)) {
            toggleButton.setChecked(lastChildPosDash == childPosition);
            DashBoardActivity act = (DashBoardActivity) context;
            act.setContent(lastChildTypeDash, lastChildMidDash, lastLocationLoc, 0, childLocName.getCurrency());
        } else {
            toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
        }
    }


    private void transactionChildValidations(int groupPosition, int childPosition) {
        LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
        int lastParentPosDash = sharedPreferences.getInt(Constants.SELECTED_PARENT_DASHBOARD, 0);
        int lastChildPosDash = sharedPreferences.getInt(Constants.SELECTED_CHILD_DASHBOARD, 0);
        String lastLocationLoc = sharedPreferences.getString(Constants.SELECTED_LOCATION_DASHBOARD, "");
        String lastChildMidDash = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_DASHBOARD, "");
        String previousLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();
        int lastParentPosQr = sharedPreferences.getInt(Constants.SELECTED_PARENT_QRCODE, 0);
        int lastChildPosQr = sharedPreferences.getInt(Constants.SELECTED_CHILD_QRCODE, 0);
        String lastLocationQr = sharedPreferences.getString(Constants.SELECTED_LOCATION_QRCODE, "");
        String lastChildMidQr = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_QRCODE, "");
        String previousQrLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();

        if (actName.equalsIgnoreCase(Constants.QR_TRANSACTION)) {
            if (lastParentPosQr == groupPosition &&
                    lastChildPosQr == childPosition &&
                    lastLocationQr.equalsIgnoreCase(previousQrLocationName)) {
                toggleButton.setChecked(lastChildPosQr == childPosition);
                QrTransactions act = (QrTransactions) context;
                act.setContent(lastLocationQr, lastChildMidQr, 0);
            } else {
                toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
            }

        } else if (actName.equalsIgnoreCase(Constants.SETTLE_TRANSACTION)) {
            if (lastParentPosDash == groupPosition &&
                    lastChildPosDash == childPosition &&
                    lastLocationLoc.equalsIgnoreCase(previousLocationName)) {
                toggleButton.setChecked(lastChildPosDash == childPosition);
                SettledTransactionsQuery act = (SettledTransactionsQuery) context;
                act.setContent(lastLocationLoc, lastChildMidDash, 0);
            } else {
                toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
            }
        }
    }

    private void preferencesInstanceCheck(int groupPosition, int childPosition) {
        if (actName.equalsIgnoreCase(Constants.QR_CODE)) {
            sharedPreferences = ((AzulApplication) ((QrCode) context).getApplication()).getPrefs();
        } else if (actName.equalsIgnoreCase(Constants.QR_TRANSACTION)) {
            sharedPreferences = ((AzulApplication) ((QrTransactions) context).getApplication()).getPrefs();
        } else if (actName.equalsIgnoreCase(Constants.SETTLE_TRANSACTION)) {
            sharedPreferences = ((AzulApplication) ((SettledTransactionsQuery) context).getApplication()).getPrefs();
        } else if (actName.equalsIgnoreCase(Constants.SET_PAYMENT)) {
            setPaymentOperations(groupPosition, childPosition);

        } else if (actName.equalsIgnoreCase(Constants.SET_QUICK_PAYMENT)) {
            setPaymentQuick(groupPosition, childPosition);
        } else if (actName.equalsIgnoreCase(Constants.PAYMENT_VALIDATE)) {

            paymentValidationOperation(groupPosition, childPosition);

        } else if (actName.equalsIgnoreCase(Constants.PAYMENT_TRANSACTION)) {

            paymentTransactionOperation(groupPosition, childPosition);

        } else if (actName.equalsIgnoreCase(Constants.PAYMENT_CONFIRM)) {
            paymentConfirmationOperation(groupPosition, childPosition);

        } else if (actName.equalsIgnoreCase("Menu")) {

            menuSetContent(groupPosition, childPosition);

        } else if (actName.equalsIgnoreCase(Constants.QUICK_PAYMENT_CONFIRM)) {

            quickPaymentConfirmation(groupPosition, childPosition);

        } else {
            sharedPreferences = ((AzulApplication) ((DashBoardActivity) context).getApplication()).getPrefs();
        }
    }

    private void paymentTransactionOperation(int groupPosition, int childPosition) {
        gettingLocationsData = ((AzulApplication) ((PaymentLinkTransactions) context).getApplication()).getLocationFilter();

        if (gettingLocationsData != null) {
            LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
            String pravLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();
            String nameMid = gettingLocationsData.getLocationNameAndId();
            if (gettingLocationsData.getParentPosition() == groupPosition
                    && gettingLocationsData.getChildPosition() == childPosition
                    && nameMid.equalsIgnoreCase(pravLocationName)) {
                toggleButton.setChecked(gettingLocationsData.getChildPosition() == childPosition);
                PaymentLinkTransactions act = (PaymentLinkTransactions) context;
                act.setContent(childLocName.getName(), nameMid, gettingLocationsData.getmId(), 0, childLocName.getTaxExempt());
            } else {
                toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
            }
        }
    }

    private void setPaymentOperations(int groupPosition, int childPosition) {
        LocationFilterSecondGroup locationFilterSecondGroup = (LocationFilterSecondGroup) getGroup(groupPosition);
        gettingLocationsData = ((AzulApplication) ((SetPaymentInfoActivity) context).getApplication()).getLocationFilter();
        if (gettingLocationsData != null) {
            LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
            String pravLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();
            String nameMid = gettingLocationsData.getLocationNameAndId();
            if (gettingLocationsData.getParentPosition() == groupPosition
                    && gettingLocationsData.getChildPosition() == childPosition
                    && nameMid.equalsIgnoreCase(pravLocationName)) {
                toggleButton.setChecked(gettingLocationsData.getChildPosition() == childPosition);
                SetPaymentInfoActivity act = (SetPaymentInfoActivity) context;
                act.setContent(nameMid, gettingLocationsData.getmId(), 0, locationFilterSecondGroup.getCode(), childLocName);
            } else {
                toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
            }
        }
    }

    private void quickPaymentConfirmation(int groupPosition, int childPosition) {
        LocationFilterSecondGroup locationFilterSecondGroup = (LocationFilterSecondGroup) getGroup(groupPosition);
        gettingLocationsData = ((AzulApplication) ((QuickPayConfirmActivity) context).getApplication()).getLocationFilter();

        if (gettingLocationsData != null) {
            LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
            String pravLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();
            String nameMid = gettingLocationsData.getLocationNameAndId();
            if (gettingLocationsData.getParentPosition() == groupPosition
                    && gettingLocationsData.getChildPosition() == childPosition
                    && nameMid.equalsIgnoreCase(pravLocationName)) {
                toggleButton.setChecked(gettingLocationsData.getChildPosition() == childPosition);
                QuickPayConfirmActivity act = (QuickPayConfirmActivity) context;
                LocationFilterSecondGroup parentObject = (LocationFilterSecondGroup) getGroup(groupPosition);

                act.setContent(parentObject.getName(), childLocName.getName(), nameMid, gettingLocationsData.getmId(), 0, childLocName, locationFilterSecondGroup.getCode());
            } else {
                toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
            }
        }
    }

    private void paymentConfirmationOperation(int groupPosition, int childPosition) {
        LocationFilterSecondGroup locationFilterSecondGroup = (LocationFilterSecondGroup) getGroup(groupPosition);
        gettingLocationsData = ((AzulApplication) ((PaymentConfirmActivity) context).getApplication()).getLocationFilter();

        if (gettingLocationsData != null) {
            LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
            String pravLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();
            String nameMid = gettingLocationsData.getLocationNameAndId();
            if (gettingLocationsData.getParentPosition() == groupPosition
                    && gettingLocationsData.getChildPosition() == childPosition
                    && nameMid.equalsIgnoreCase(pravLocationName)) {
                toggleButton.setChecked(gettingLocationsData.getChildPosition() == childPosition);
                PaymentConfirmActivity act = (PaymentConfirmActivity) context;
                LocationFilterSecondGroup parentObject = (LocationFilterSecondGroup) getGroup(groupPosition);

                act.setContent(parentObject.getName(), childLocName.getName(), nameMid, gettingLocationsData.getmId(),
                        0, childLocName, locationFilterSecondGroup.getCode());
            } else {
                toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
            }
        }
    }

    private void paymentValidationOperation(int groupPosition, int childPosition) {
        gettingLocationsData = ((AzulApplication) ((PaymentDataValidateActivity) context).getApplication()).getLocationFilter();

        if (gettingLocationsData != null) {
            LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
            String pravLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();
            String nameMid = gettingLocationsData.getLocationNameAndId();
            if (gettingLocationsData.getParentPosition() == groupPosition
                    && gettingLocationsData.getChildPosition() == childPosition
                    && nameMid.equalsIgnoreCase(pravLocationName)) {
                toggleButton.setChecked(gettingLocationsData.getChildPosition() == childPosition);
                PaymentDataValidateActivity act = (PaymentDataValidateActivity) context;
                act.setContent(childLocName.getName(), nameMid, gettingLocationsData.getmId(), 0, childLocName.getTaxExempt());
            } else {
                toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
            }
        }
    }

    private void setPaymentQuick(int groupPosition, int childPosition) {
        LocationFilterSecondGroup parentObjectData = (LocationFilterSecondGroup) getGroup(groupPosition);

        gettingLocationsData = ((AzulApplication) ((SetPaymentInfoActivity) context).getApplication()).getLocationFilter();
        if (gettingLocationsData != null) {
            LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
            String pravLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();
            String nameMid = gettingLocationsData.getLocationNameAndId();
            if (gettingLocationsData.getParentPosition() == groupPosition
                    && gettingLocationsData.getChildPosition() == childPosition
                    && nameMid.equalsIgnoreCase(pravLocationName)) {
                toggleButton.setChecked(gettingLocationsData.getChildPosition() == childPosition);
                QuickSetPaymentActivity act = (QuickSetPaymentActivity) context;
                act.setContent(nameMid, gettingLocationsData.getmId(), 0, parentObjectData.getCode());
            } else {
                toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
            }
        }

    }

    private void menuSetContent(int groupPosition, int childPosition) {
        gettingLocationsData = ((AzulApplication) ((MainMenuActivity) context).getApplication()).getLocationFilter();

        if (gettingLocationsData != null) {
            LocationFilterThirdGroup childLocName = (LocationFilterThirdGroup) getChild(groupPosition, childPosition);
            String pravLocationName = childLocName.getName() + " - " + childLocName.getMerchantId();
            String nameMid = gettingLocationsData.getLocationNameAndId();
            if (gettingLocationsData.getParentPosition() == groupPosition
                    && gettingLocationsData.getChildPosition() == childPosition
                    && nameMid.equalsIgnoreCase(pravLocationName)) {
                toggleButton.setChecked(gettingLocationsData.getChildPosition() == childPosition);
                MainMenuActivity act = (MainMenuActivity) context;
                LocationFilterSecondGroup parentObject = (LocationFilterSecondGroup) getGroup(groupPosition);
                act.setContent(parentObject.getCode(), parentObject.getName(), childLocName.getName(), nameMid, 0, childLocName);
            } else {
                toggleButton.setChecked(childPosition == mChildCheckStates.get(groupPosition));
            }
        }
    }

    public void setLocationData(LocationFilterThirdGroup locationFilterThirdGroup, String paymentConfirm, String locationName, String mId, int groupPosition, int childPosition, String tax) {
        LocationFilterSecondGroup parentObjectData = (LocationFilterSecondGroup) getGroup(groupPosition);
        LocationFilter locationFilterObj = new LocationFilter();
        locationFilterObj.setChildPosition((Integer) childPosition);
        locationFilterObj.setParentPosition(groupPosition);
        locationFilterObj.setLocationNameAndId(locationName);
        locationFilterObj.setLocationName(locationFilterThirdGroup.getName());
        locationFilterObj.setmId(mId);
        locationFilterObj.setTaxExempt(tax);
        locationFilterObj.setParentName(parentObjectData.getName());
        locationFilterObj.setPaymentCode(parentObjectData.getCode());
        locationFilterObj.setCurrency(locationFilterThirdGroup.getCurrency());
        if (paymentConfirm.equalsIgnoreCase(Constants.SET_PAYMENT)) {
            ((AzulApplication) ((SetPaymentInfoActivity) context).getApplication()).setLocationFilter(locationFilterObj);
        } else if (paymentConfirm.equalsIgnoreCase(Constants.SET_QUICK_PAYMENT)) {
            ((AzulApplication) ((QuickSetPaymentActivity) context).getApplication()).setLocationFilter(locationFilterObj);
        } else if (paymentConfirm.equalsIgnoreCase(Constants.QUICK_PAYMENT_CONFIRM)) {
            ((AzulApplication) ((QuickPayConfirmActivity) context).getApplication()).setLocationFilter(locationFilterObj);
        } else if (paymentConfirm.equalsIgnoreCase(Constants.PAYMENT_VALIDATE)) {
            ((AzulApplication) ((PaymentDataValidateActivity) context).getApplication()).setLocationFilter(locationFilterObj);
        } else if (paymentConfirm.equalsIgnoreCase(Constants.PAYMENT_TRANSACTION)) {
            ((AzulApplication) ((PaymentLinkTransactions) context).getApplication()).setLocationFilter(locationFilterObj);
        } else if (paymentConfirm.equalsIgnoreCase("Menu")) {
            ((AzulApplication) ((MainMenuActivity) context).getApplication()).setLocationFilter(locationFilterObj);
        } else {
            ((AzulApplication) ((PaymentConfirmActivity) context).getApplication()).setLocationFilter(locationFilterObj);
        }
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
