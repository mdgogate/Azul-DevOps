package com.sdp.appazul.activities.transactions;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.sdp.appazul.R;
import com.sdp.appazul.adapters.CalendarAdapter;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.utils.DateUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class BottomDateFilterFragment extends BottomSheetDialogFragment {
    TextView title;
    GridView gridview;
    ImageView next;
    ImageView previous;
    boolean editText1Selected = false;
    boolean editText2Selected = false;
    GregorianCalendar month;
    GregorianCalendar selectedInitialDate;
    GregorianCalendar selectedFinalDate;
    List<String> daysList = new ArrayList<>();
    List<String> monthList = new ArrayList<>();

    String selectedInitialDateString = "";
    String selectedFinalDateString = "";
    String fechaFinal = "";

    CalendarAdapter calendarAdapter;
    ArrayList<String> desc;
    TextView tvFromDate;
    TextView tvToDate;
    ArrayList<String> nameOfEvent = new ArrayList<>();
    ArrayList<String> startDates = new ArrayList<>();
    DateUtils utils = new DateUtils();
    LinearLayout linearLayout;
    LinearLayout fromDateLayout;
    LinearLayout toDateLayoutw;
    RelativeLayout calendarLayout;
    String currentDateTime = "";
    ImageView ivFromIcon;
    ImageView ivToIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_advanced_date_filter, container, false);
        initControls(view);


        getDialog().setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) bottomSheet.getParent();
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(10000);
            coordinatorLayout.getParent().requestLayout();

        });

        return view;

    }

    private void initControls(View view) {
        title = view.findViewById(R.id.title);
        gridview = view.findViewById(R.id.gridview);
        next = view.findViewById(R.id.right);
        previous = view.findViewById(R.id.left);

        calendarLayout = view.findViewById(R.id.calendar_layout);
        fromDateLayout = view.findViewById(R.id.fromDateLayout);
        toDateLayoutw = view.findViewById(R.id.toDateLayoutw);
        linearLayout = view.findViewById(R.id.rel_days);

        tvFromDate = view.findViewById(R.id.editText1);
        tvToDate = view.findViewById(R.id.editText2);
        ivFromIcon = view.findViewById(R.id.iv_from_icon);
        ivToIcon = view.findViewById(R.id.iv_to_icon);

        fromDateLayout.setOnClickListener(fromDateView -> {
            if (calendarLayout.getVisibility() == View.VISIBLE) {
                calendarLayout.setVisibility(View.GONE);
            } else {
                calendarLayout.setVisibility(View.VISIBLE);
            }
            fromDateLayout.setBackgroundResource(R.drawable.spinner_background);
            editText1Selected = true;
            editText2Selected = false;
            ivToIcon.setImageResource(R.drawable.arr_down);
            ivFromIcon.setImageResource(R.drawable.up_arrow);
            calanderDialog();
        });

        toDateLayoutw.setOnClickListener(toDateLayoutwView -> {

            if (calendarLayout.getVisibility() == View.VISIBLE) {
                calendarLayout.setVisibility(View.GONE);
            } else {
                calendarLayout.setVisibility(View.VISIBLE);
            }
            toDateLayoutw.setBackgroundResource(R.drawable.spinner_background);
            editText1Selected = false;
            editText2Selected = true;

            ivFromIcon.setImageResource(R.drawable.arr_down);
            ivToIcon.setImageResource(R.drawable.up_arrow);

            calanderDialog();

        });

        setInitialAndFinalDate();
        initParameters();
        setDays();
    }


    public void initParameters() {
        monthList = utils.getmonthList();
        daysList = utils.getDaysList();
    }

    public GregorianCalendar getDateOneMonthsBeforeDate(GregorianCalendar date) {
        return new GregorianCalendar(date.get(YEAR), (date.get(MONTH)),
                date.get(DAY_OF_MONTH));
    }

    public void setInitialAndFinalDate() {
        selectedFinalDate = (GregorianCalendar) getInstance();

        String selectedMonth = "" + (selectedFinalDate.get(MONTH) + 1);
        String date = "" + selectedFinalDate.get(DAY_OF_MONTH);

        currentDateTime = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedFinalDate.get(YEAR);

        selectedFinalDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedFinalDate.get(YEAR);

        selectedInitialDate = getDateOneMonthsBeforeDate(selectedFinalDate);
        selectedMonth = "" + (selectedInitialDate.get(MONTH) + 1);
        date = "" + selectedInitialDate.get(DAY_OF_MONTH);

        selectedInitialDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedInitialDate.get(YEAR);
        tvFromDate.setText(selectedInitialDateString);

        if (!TextUtils.isEmpty(fechaFinal)) {
            String finalDate = utils.getStringFormatted(Constants.DD_MM_YYYY, utils.getDateFormatted(Constants.YYYY_MM_DD, fechaFinal));
            tvToDate.setText(finalDate);
            selectedFinalDateString = finalDate;
            Date d = utils.getDateFormatted(Constants.YYYY_MM_DD, fechaFinal);
            selectedFinalDate = new GregorianCalendar();
            selectedFinalDate.setTimeInMillis(d.getTime());
        } else {
            tvToDate.setText(selectedFinalDateString);
        }
    }

    public void setDays() {
        final TextView [] txtView = new TextView[7];
        final Typeface typefaceBold = ResourcesCompat.getFont(getActivity(), R.font.vag_bold);
        for (int i = 0; i < 7; i++) {

            txtView[i] = new TextView(getActivity());
            txtView[i].setText(daysList.get(i));
            txtView[i].setTextSize(13);
            txtView[i].setTextColor(ContextCompat.getColor(getActivity(), R.color.time_frame));
            txtView[i].setId(i);
            txtView[i].setPadding(8, 8, 0, 0);
            txtView[i].setTypeface(typefaceBold);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, MATCH_PARENT,
                    1);
            linearLayout.addView(txtView[i], param);
        }
    }

    public void calanderDialog() {

        if (editText1Selected) {

            String year = (String) DateFormat.format(Constants.YYYY, selectedInitialDate);
            month = (GregorianCalendar) selectedInitialDate.clone();
            String spanishmonthname = monthList
                    .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
            title.setText(spanishmonthname + " " + year);

            String initialMonth = "" + (selectedInitialDate.get(MONTH) + 1);
            String initialDate = "" + selectedInitialDate.get(DAY_OF_MONTH);

            selectedInitialDateString = (initialDate.length() == 1 ? ("0" + initialDate) : initialDate) + "/"
                    + (initialMonth.length() == 1 ? ("0" + initialMonth) : initialMonth) + "/"
                    + selectedInitialDate.get(YEAR);

            String selectedMonth = "" + (selectedFinalDate.get(MONTH) + 1);
            String date = "" + selectedFinalDate.get(DAY_OF_MONTH);

            selectedFinalDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                    + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                    + selectedFinalDate.get(YEAR);
            Log.d("DATE To 222 ", "FROM ::: " + selectedInitialDateString + "TO ::: " + selectedFinalDateString);

            calendarAdapter = new CalendarAdapter(getActivity(), month, false, selectedInitialDateString,
                    selectedFinalDateString);

        } else if (editText2Selected) {

            String year = (String) DateFormat.format(Constants.YYYY, selectedFinalDate);
            month = (GregorianCalendar) selectedFinalDate.clone();
            String spanishmonthname = monthList
                    .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
            title.setText(spanishmonthname + " " + year);

            String initialMonth = "" + (selectedInitialDate.get(MONTH) + 1);
            String initialDate = "" + selectedInitialDate.get(DAY_OF_MONTH);

            selectedInitialDateString = (initialDate.length() == 1 ? ("0" + initialDate) : initialDate) + "/"
                    + (initialMonth.length() == 1 ? ("0" + initialMonth) : initialMonth) + "/"
                    + selectedInitialDate.get(YEAR);
            String selectedMonth = "" + (selectedFinalDate.get(MONTH) + 1);
            String date = "" + selectedFinalDate.get(DAY_OF_MONTH);
            selectedFinalDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                    + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                    + selectedFinalDate.get(YEAR);
            calendarAdapter = new CalendarAdapter(getActivity(), month, false, selectedInitialDateString,
                    selectedFinalDateString);
        }

        next.setEnabled(true);
        if (editText1Selected) {
            previous.setVisibility(View.VISIBLE);
        }

        calendarAdapter.fechaFinal = selectedFinalDate;
        calendarAdapter.fechaInitial = selectedInitialDate;
        gridview.setAdapter(calendarAdapter);
        gridview.setOnItemClickListener((parent, v, position, id) -> {
            desc = new ArrayList<>();
            String selectedGridDate = CalendarAdapter.dayString.get(position);
            String[] separatedTime = selectedGridDate.split("-");
            ((CalendarAdapter) parent.getAdapter()).setSelected(v);

            String gridvalueString = separatedTime[2].replaceFirst("^0*", "");
            int gridvalue = Integer.parseInt(gridvalueString);
            if ((gridvalue > 10) && (position < 8)) {
                setPreviousMonth();
                refreshCalendar();
            } else if ((gridvalue < 7) && (position > 28)) {
                setNextMonth();
                refreshCalendar();
            } else {

                String selectedMonth = "" + (month.get(MONTH) + 1);
                GregorianCalendar selectedDate = (GregorianCalendar) month.clone();
                selectedDate.set(DATE, gridvalue);

                String selectedDateString = (gridvalue < 10 ? ("0" + gridvalue) : gridvalue) + "/"
                        + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                        + month.get(YEAR);
                if (editText1Selected) {
                    if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {

                        if (!TextUtils.isEmpty(tvToDate.getText().toString())) {
                            String tempDate = tvToDate.getText().toString();
                            if (utils.isFromDateSmaller(selectedDateString, tvToDate.getText().toString())) {
                                tvFromDate.setText(selectedDateString);
                                tvToDate.setText(tempDate);
                                selectedInitialDate = selectedDate;
                            }else {
                                tvFromDate.setText(tempDate);
                                tvToDate.setText(selectedDateString);
                                selectedInitialDate = selectedFinalDate;
                                selectedFinalDate = selectedDate;
                            }

                            calendarLayout.setVisibility(View.GONE);
                            fromDateLayout.setBackgroundResource(R.drawable.tv_unselect_background);
                            editText1Selected = false;
                            ivToIcon.setImageResource(R.drawable.arr_down);
                            ivFromIcon.setImageResource(R.drawable.arr_down);
                        }
                    } else {
                        toastPopup(getActivity(), R.string.current_date_compare);
                    }

                } else if (editText2Selected) {
                    if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {
                        if (!TextUtils.isEmpty(tvFromDate.getText().toString())) {
                            String tempDate = tvFromDate.getText().toString();
                            if (utils.isToDateSmaller(tvFromDate.getText().toString(), selectedDateString)) {
                                tvFromDate.setText(selectedDateString);
                                tvToDate.setText(tempDate);
                                selectedFinalDate = selectedInitialDate;
                                selectedInitialDate = selectedDate;
                            }else {
                                tvFromDate.setText(tempDate);
                                tvToDate.setText(selectedDateString);
                                selectedFinalDate = selectedDate;
                            }
                            toDateLayoutw.setBackgroundResource(R.drawable.tv_unselect_background);
                            calendarLayout.setVisibility(View.GONE);
                            editText2Selected = false;

                            ivToIcon.setImageResource(R.drawable.arr_down);
                            ivFromIcon.setImageResource(R.drawable.arr_down);
                        }
                    } else {
                        toastPopup(getActivity(), R.string.current_date_compare);
                    }
                }
            }
            ((CalendarAdapter) parent.getAdapter()).setSelected(v);

            for (int i = 0; i < startDates.size(); i++) {
                if (startDates.get(i).equals(selectedGridDate)) {
                    desc.add(nameOfEvent.get(i));
                }
            }
            desc = null;
        });


        previous.setOnClickListener(previousView -> {
            setPreviousMonth();
            next.setVisibility(View.VISIBLE);
            refreshCalendar();
            next.setEnabled(true);
        });

        next.setOnClickListener(nextView -> {
            setNextMonth();
            previous.setVisibility(View.VISIBLE);
            refreshCalendar();
            next.setEnabled(true);

        });
    }

    public void toastPopup(Context context, int message) {
        Toast toast = Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT);
        View tView = toast.getView();
        tView.setBackgroundResource(R.drawable.toast_bg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void refreshCalendar() {
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        String spanishmonthname = monthList
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        String year = (String) DateFormat.format(Constants.YYYY, month);
        String fulldate = spanishmonthname + " " + year;
        title.setText(fulldate);
    }

    protected void setNextMonth() {
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        if (month.get(MONTH) == month.getActualMaximum(MONTH)) {
            month.set((month.get(YEAR) + 1), month.getActualMinimum(MONTH), 1);
        } else {
            month.set(MONTH, month.get(MONTH) + 1);
        }
    }

    protected void setPreviousMonth() {
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        if (month.get(MONTH) == month.getActualMinimum(MONTH)) {
            month.set((month.get(YEAR) - 1), month.getActualMaximum(MONTH), 1);
        } else {
            month.set(MONTH, month.get(MONTH) - 1);
        }
    }
}
