package com.sdp.appazul.activities.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.sdp.appazul.R;
import com.sdp.appazul.activities.transactions.BottomDateFilterFragment;

public class GenerateStatementFragment extends Fragment {

    RelativeLayout btnAdvancedFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_last, container, false);

        RelativeLayout lastDay = (RelativeLayout) view.findViewById(R.id.lastDayBtn);
        RelativeLayout monthToDate = (RelativeLayout) view.findViewById(R.id.monthToDateBtn);
        RelativeLayout previousMonth = (RelativeLayout) view.findViewById(R.id.previousMonthBtn);

        lastDay.setOnClickListener(v1 -> {

        });

        monthToDate.setOnClickListener(v1 -> {

        });

        previousMonth.setOnClickListener(v1 -> {

        });

        initControls(view);
        return view;
    }

    private void initControls(View fragmentView) {
        btnAdvancedFilter = fragmentView.findViewById(R.id.btnAdvancedFilter);
        btnAdvancedFilter.setOnClickListener(btnAdvancedFilterView ->
                bottomSheetOpenFunc()

        );
    }

    private void bottomSheetOpenFunc() {
        BottomDateFilterFragment bottomDateFilterActivity = new BottomDateFilterFragment();

        bottomDateFilterActivity.show(getActivity().getSupportFragmentManager(), "bottomSheetDateFilter");

    }


}

