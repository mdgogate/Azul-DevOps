package com.sdp.appazul.activities.menuitems;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdp.appazul.R;
import com.sdp.appazul.classes.LoginData;
import com.sdp.appazul.expandable.CustomizedExpandableListAdapter;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UnitLocation extends AppCompatActivity {
    TextView toolbarTextTitle;
    LoginData loginData;
    ExpandableListView expandableListViewExample;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableTitleList;
    HashMap<String, List<String>> expandableDetailList;
    String locationJson;
    ImageView unitsLocationBackButton;
    HashMap<String, ArrayList<String>> LOGIN_DATA = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_units_location);

        Intent locationIntent = getIntent();
        locationJson = locationIntent.getStringExtra(Constants.LOCATION_RESPONSE);
        LOGIN_DATA = new HashMap<>();
        LOGIN_DATA = ((AzulApplication) UnitLocation.this.getApplication()).getListOfData();
        initComponent();
        initComponentOnclick();
        expandableCode();
    }

    private void expandableCode() {
        expandableListViewExample = (ExpandableListView) findViewById(R.id.expandableListViewSample);
        expandableDetailList = getData(((AzulApplication) UnitLocation.this.getApplication()).getListOfData());
        expandableTitleList = new ArrayList<String>(expandableDetailList.keySet());
        expandableListAdapter = new CustomizedExpandableListAdapter(this, expandableTitleList, expandableDetailList);
        expandableListViewExample.setAdapter(expandableListAdapter);
//        expandableListViewExample.notifyDataSetChanged();

        expandableListViewExample.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            private int lastPosition = -1;

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastPosition != -1
                        && groupPosition != lastPosition) {
                    expandableListViewExample.collapseGroup(lastPosition);
                }
                lastPosition = groupPosition;

            }
        });

//         This method is called when the group is collapsed
        expandableListViewExample.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Log.d("TAG", "onGroupCollapse: ");
            }
        });

//         This method is called when the child in any group is clicked
        expandableListViewExample.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), expandableTitleList.get(groupPosition)
                        + " -> "
                        + expandableDetailList.get(
                        expandableTitleList.get(groupPosition)).get(
                        childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
    }

    public HashMap<String, List<String>> getData(HashMap<String, ArrayList<String>> listData) {

        HashMap<String, List<String>> expandableDetailList = new HashMap<String, List<String>>();

        Iterator it = listData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //it.remove(); // avoids a ConcurrentModificationException
            expandableDetailList.put(pair.getKey().toString(), (List<String>) pair.getValue());
        }

        return expandableDetailList;
    }

    private void initComponentOnclick() {
        unitsLocationBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(UnitLocation.this, MyProfile.class);
            intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });
    }

    private void initComponent() {
        unitsLocationBackButton = findViewById(R.id.unitsLocationBackButton);
        toolbarTextTitle = findViewById(R.id.toolbarTextTitle);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UnitLocation.this, MyProfile.class);
        intent.putExtra(Constants.LOCATION_RESPONSE,locationJson);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
    }
}