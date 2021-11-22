package com.sdp.appazul.activities.transactions;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DownloadFile;
import com.sdp.appazul.adapters.CalendarAdapter;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DateUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONObject;

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
    List<String> monthListCaps = new ArrayList<>();

    String selectedInitialDateString = "";
    String selectedFinalDateString = "";
    String fetchFinal = "";

    CalendarAdapter calendarAdapter;
    ArrayList<String> desc;
    TextView tvFromDate;
    TextView tvToDate;
    ArrayList<String> nameOfEvent = new ArrayList<>();
    ArrayList<String> startDates = new ArrayList<>();
    DateUtils utils = new DateUtils();
    LinearLayout linearLayout;
    LinearLayout fromDateLayout;
    LinearLayout toDateLayout;
    RelativeLayout calendarLayout;
    String currentDateTime = "";
    ImageView ivFromIcon;
    ImageView ivToIcon;
    Button buttonGenerateStatus;
    LinearLayout dateFilterLayout;
    View titleViewBottom;
    TextView dateFilterTitle;
    GlobalFunctions globalFunctions = new GlobalFunctions(getActivity());
    String miD = "";

    public BottomDateFilterFragment() {
    }

    public BottomDateFilterFragment(String selectedMerchant) {
        miD = selectedMerchant;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_advanced_date_filter, container, false);
        initControls(view);

        return view;

    }

    private void initControls(View view) {
        dateFilterTitle = view.findViewById(R.id.dateFilterTitle);
        titleViewBottom = view.findViewById(R.id.titleViewBottom);
        dateFilterLayout = view.findViewById(R.id.dateFilterLayout);
        title = view.findViewById(R.id.title);
        gridview = view.findViewById(R.id.gridview);
        next = view.findViewById(R.id.right);
        previous = view.findViewById(R.id.left);

        calendarLayout = view.findViewById(R.id.calendar_layout);
        fromDateLayout = view.findViewById(R.id.fromDateLayout);
        toDateLayout = view.findViewById(R.id.toDateLayoutw);
        linearLayout = view.findViewById(R.id.rel_days);

        tvFromDate = view.findViewById(R.id.editText1);
        tvToDate = view.findViewById(R.id.editText2);
        ivFromIcon = view.findViewById(R.id.iv_from_icon);
        ivToIcon = view.findViewById(R.id.iv_to_icon);
        buttonGenerateStatus = view.findViewById(R.id.btnGenerateStatement);


        onClickListeners();
        setInitialAndFinalDate();
        initParameters();
        setDays();
        genrateStatusApi();
    }

    private void onClickListeners() {
        fromDateLayout.setOnClickListener(fromDateView -> {
            if (calendarLayout.getVisibility() == View.VISIBLE) {
                calendarLayout.setVisibility(View.GONE);
                buttonGenerateStatus.setVisibility(View.VISIBLE);
                dateFilterLayout.setVisibility(View.VISIBLE);
                titleViewBottom.setVisibility(View.VISIBLE);
                dateFilterTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue_2));
                dateFilterTitle.setText(getResources().getString(R.string.select_date_range));
                dateFilterTitle.setTextSize(20);
            } else {
                calendarLayout.setVisibility(View.VISIBLE);
                buttonGenerateStatus.setVisibility(View.GONE);
                dateFilterLayout.setVisibility(View.GONE);
                titleViewBottom.setVisibility(View.GONE);
                dateFilterTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue_3));
                dateFilterTitle.setText(getResources().getString(R.string.claendar_start_date_title));
                dateFilterTitle.setTextSize(24);
            }
            fromDateLayout.setBackgroundResource(R.drawable.spinner_background);
            editText1Selected = true;
            editText2Selected = false;
            calendarDialog();
        });

        toDateLayout.setOnClickListener(toDateLayoutView -> {

            if (calendarLayout.getVisibility() == View.VISIBLE) {
                calendarLayout.setVisibility(View.GONE);
                buttonGenerateStatus.setVisibility(View.VISIBLE);
                dateFilterLayout.setVisibility(View.VISIBLE);
                titleViewBottom.setVisibility(View.VISIBLE);
                dateFilterTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue_2));
                dateFilterTitle.setText(getResources().getString(R.string.select_date_range));
                dateFilterTitle.setTextSize(20);
            } else {
                calendarLayout.setVisibility(View.VISIBLE);
                buttonGenerateStatus.setVisibility(View.GONE);
                dateFilterLayout.setVisibility(View.GONE);
                titleViewBottom.setVisibility(View.GONE);
                dateFilterTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue_3));
                dateFilterTitle.setText(getResources().getString(R.string.claendar_end_date_title));
                dateFilterTitle.setTextSize(24);
            }
            toDateLayout.setBackgroundResource(R.drawable.spinner_background);
            editText1Selected = false;
            editText2Selected = true;


            calendarDialog();

        });
    }

    public void genrateStatusApi() {
        buttonGenerateStatus.setOnClickListener(buttonGenerateStatus -> {
            String toDate = globalFunctions.changeDateFormat(tvToDate.getText().toString(), KeyConstants.DD_MM_YYYY_KEY, KeyConstants.YYYY_MM_DD_KEY);
            String fromDate = globalFunctions.changeDateFormat(tvFromDate.getText().toString(), KeyConstants.DD_MM_YYYY_KEY, KeyConstants.YYYY_MM_DD_KEY);

            callPdfValidationAPI(toDate, fromDate);

        });
    }

    private void callPdfValidationAPI(String toDate, String fromDate) {
        ApiManager apiManager = new ApiManager(getActivity());
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) getActivity().getApplicationContext()).getTcpKey();
            String vcr = ((AzulApplication) getActivity().getApplicationContext()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(getContext(), tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(getContext()));
            payload.put("format", "pdf");
            payload.put("type", "lotes");
            payload.put("dateFrom", fromDate);
            payload.put("dateTo", toDate);
            payload.put("MerchantId", miD);
            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.GET_TRANSACTION_PDF, json);
        ((AzulApplication) getActivity().getApplicationContext()).setPdfJson(json.toString());


    }


    public void initParameters() {
        monthList = utils.getmonthListWithCaps();
        monthListCaps = utils.getmonthListWithCaps();
        daysList = utils.getDaysList();
    }

    public GregorianCalendar getDateOneMonthsBeforeDate(GregorianCalendar date) {
        return new GregorianCalendar(date.get(YEAR), (date.get(MONTH)),
                date.get(DAY_OF_MONTH));
    }

    public void setInitialAndFinalDate() {
        GregorianCalendar prevDate = (GregorianCalendar) getInstance();
        selectedFinalDate = (GregorianCalendar) getInstance();
        selectedFinalDate.add(prevDate.DATE, -1);
        String selectedMonth = "" + (selectedFinalDate.get(MONTH) + 1);
        String date = "" + selectedFinalDate.get(DAY_OF_MONTH);
        Log.d("TAG", "setInitialAndFinalDate: "+selectedFinalDate.get(DAY_OF_MONTH ));
        Log.d("TAG", "setInitialAndFinalDate: "+selectedMonth);

        currentDateTime = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedFinalDate.get(YEAR);

        selectedFinalDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedFinalDate.get(YEAR);


        selectedInitialDate = selectedFinalDate;

        selectedInitialDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedInitialDate.get(YEAR);
        tvFromDate.setText(selectedInitialDateString);

        if (!TextUtils.isEmpty(fetchFinal)) {
            String finalDate = utils.getStringFormatted(Constants.DD_MM_YYYY, utils.getDateFormatted(Constants.YYYY_MM_DD, fetchFinal));
            tvToDate.setText(finalDate);
            selectedFinalDateString = finalDate;
            Date d = utils.getDateFormatted(Constants.YYYY_MM_DD, fetchFinal);
            selectedFinalDate = new GregorianCalendar();
            selectedFinalDate.setTimeInMillis(d.getTime());
        } else {
            tvToDate.setText(selectedFinalDateString);
        }
    }

    public void setDays() {
        final TextView[] txtView = new TextView[7];
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

    public void calendarDialog() {

        if (editText1Selected) {
            fromDateSelectedFunc();
        } else if (editText2Selected) {
            toDateSelectedFunc();
        }
        next.setEnabled(true);
        if (editText1Selected) {
            previous.setVisibility(View.VISIBLE);
        }
        calendarAdapter.fechaFinal = selectedFinalDate;
        calendarAdapter.fechaInitial = selectedInitialDate;
        gridview.setAdapter(calendarAdapter);

        gridViewItemListener();

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

    private void gridViewItemListener() {
        gridview.setOnItemClickListener((parent, v, position, id) -> {

            desc = new ArrayList<>();
            String selectedGridDate = CalendarAdapter.dayString.get(position);
            String[] separatedTime = selectedGridDate.split("-");
            ((CalendarAdapter) parent.getAdapter()).setSelected(v);

            String gridValueString = separatedTime[2].replaceFirst("^0*", "");
            int gridValue = Integer.parseInt(gridValueString);

            gridViewOperations(gridValue, position);
            ((CalendarAdapter) parent.getAdapter()).setSelected(v);

            for (int i = 0; i < startDates.size(); i++) {
                if (startDates.get(i).equals(selectedGridDate)) {
                    desc.add(nameOfEvent.get(i));
                }
            }
            desc = null;
        });
    }

    private void gridViewOperations(int gridValue, int position) {
        if ((gridValue > 10) && (position < 8)) {
            setPreviousMonth();
            refreshCalendar();
        } else if ((gridValue < 7) && (position > 28)) {
            setNextMonth();
            refreshCalendar();
        } else {

            String selectedMonth = "" + (month.get(MONTH) + 1);
            GregorianCalendar selectedDate = (GregorianCalendar) month.clone();
            selectedDate.set(DATE, gridValue);

            String selectedDateString = (gridValue < 10 ? ("0" + gridValue) : gridValue) + "/"
                    + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                    + month.get(YEAR);
            if (editText1Selected) {
                fromDateItemSelectedFunction(selectedDateString, selectedDate);
            } else if (editText2Selected) {
                toDateItemSelectedFunction(selectedDateString, selectedDate);
            }
        }
    }

    private void toDateItemSelectedFunction(String selectedDateString, GregorianCalendar selectedDate) {
        if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {
            if (!TextUtils.isEmpty(tvFromDate.getText().toString())) {
                toDateSelectionInnerFilter(selectedDate, selectedDateString);
            }
        } else {
            toastPopup(getActivity());
        }
    }

    private void toDateSelectionInnerFilter(GregorianCalendar selectedDate, String selectedDateString) {
        String tempDate = tvFromDate.getText().toString();
        if (utils.isToDateSmaller(tvFromDate.getText().toString(), selectedDateString)) {
            tvFromDate.setText(selectedDateString);
            tvToDate.setText(tempDate);
            selectedFinalDate = selectedInitialDate;
            selectedInitialDate = selectedDate;
        } else {
            tvFromDate.setText(tempDate);
            tvToDate.setText(selectedDateString);
            selectedFinalDate = selectedDate;
        }
        toDateLayout.setBackgroundResource(R.drawable.tv_unselect_background);
        calendarLayout.setVisibility(View.GONE);
        buttonGenerateStatus.setVisibility(View.VISIBLE);
        dateFilterLayout.setVisibility(View.VISIBLE);
        titleViewBottom.setVisibility(View.VISIBLE);
        editText2Selected = false;
        dateFilterTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue_2));
    }

    private void fromDateItemSelectedFunction(String selectedDateString, GregorianCalendar selectedDate) {
        if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {

            if (!TextUtils.isEmpty(tvToDate.getText().toString())) {
                fromDateSelectionInnerFilter(selectedDateString, selectedDate);
            }
        } else {
            toastPopup(getActivity());
        }
    }

    private void fromDateSelectionInnerFilter(String selectedDateString, GregorianCalendar selectedDate) {
        String tempDate = tvToDate.getText().toString();
        if (utils.isFromDateSmaller(selectedDateString, tvToDate.getText().toString())) {
            tvFromDate.setText(selectedDateString);
            tvToDate.setText(tempDate);
            selectedInitialDate = selectedDate;
        } else {
            tvFromDate.setText(tempDate);
            tvToDate.setText(selectedDateString);
            selectedInitialDate = selectedFinalDate;
            selectedFinalDate = selectedDate;
        }

        calendarLayout.setVisibility(View.GONE);
        buttonGenerateStatus.setVisibility(View.VISIBLE);
        dateFilterLayout.setVisibility(View.VISIBLE);
        titleViewBottom.setVisibility(View.VISIBLE);
        dateFilterTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue_2));
        fromDateLayout.setBackgroundResource(R.drawable.tv_unselect_background);
        editText1Selected = false;
    }

    private void toDateSelectedFunc() {
        String year = (String) DateFormat.format(Constants.YYYY, selectedFinalDate);
        month = (GregorianCalendar) selectedFinalDate.clone();
        String spanishMonthName = monthListCaps
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        String toDateTitle = spanishMonthName + " " + year;
        title.setText(toDateTitle);

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

    private void fromDateSelectedFunc() {
        String year = (String) DateFormat.format(Constants.YYYY, selectedInitialDate);
        month = (GregorianCalendar) selectedInitialDate.clone();
        String spanishMonthName = monthListCaps
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        String fromDateTitle = spanishMonthName + " " + year;
        title.setText(fromDateTitle);

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

    public void toastPopup(Context context) {
        AppAlters.showPopupDialog(context,context.getResources().getString(R.string.current_date_compare));
    }

    public void refreshCalendar() {
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        String spanishMonthName = monthList
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        String year = (String) DateFormat.format(Constants.YYYY, month);
        String fullDate = spanishMonthName + " " + year;
        title.setText(fullDate);
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
