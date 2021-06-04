package com.azul.app.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.azul.app.android.R;
import com.azul.app.android.globals.GlobalFunctions;
import com.azul.app.android.utils.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.WEEK_OF_MONTH;
import static java.util.Calendar.YEAR;

public class CalendarAdapter extends BaseAdapter {

    private Context mContext;
    private Calendar month;
    GregorianCalendar pmonth;
    GregorianCalendar nmonth;
    GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDay;
    int maxWeeknumber;
    int maxP;
    public GregorianCalendar fechaInitial;
    public GregorianCalendar fechaFinal;
    int maxPNew;
    int calMaxP;
    int mnthlength;
    String itemvalue;
    String curentDateString;
    DateFormat df;
    List<String> monthNames = new ArrayList<>();
    boolean isIntialDate;
    private ArrayList<String> items;
    public static List<String> dayString;
    String selectedInitialDateString;
    String selectedFinalDateString;
    GlobalFunctions global;
    DateUtils utils = new DateUtils();
    String timeInMiliSec = " 00:00:00";
    public CalendarAdapter(Context c, GregorianCalendar monthCalendar, boolean isIntialDate1,
                           String selectedInitialDateString, String selectedFinalDateString) {
        Locale.setDefault(Locale.US);
        CalendarAdapter.dayString = new ArrayList<>();
        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
        mContext = c;
        month.set(DAY_OF_MONTH, 1);
        this.items = new ArrayList<>();
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        isIntialDate = isIntialDate1;
        this.selectedInitialDateString = selectedInitialDateString;
        this.selectedFinalDateString = selectedFinalDateString;
        refreshDays();
        global = new GlobalFunctions(c);


    }

    public int getCount() {
        return dayString.size();
    }

    public Object getItem(int position) {
        return dayString.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView;
        LinearLayout layoutdatebg;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);
        }

        layoutdatebg = (LinearLayout) v.findViewById(R.id.layoutdatebg);
        dayView = (TextView) v.findViewById(R.id.date);
        String[] separatedTime = dayString.get(position).split("-");
        String gridvalue = separatedTime[2].replaceFirst("^0*", "");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (selectedDate.get(MONTH) <= calendar.get(MONTH) && selectedDate.get(DAY_OF_MONTH) <= calendar.get(DAY_OF_MONTH)) {
            dayView.setTextColor(Color.parseColor("#FF0000"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        }

        dayCheckFunc(dayView, gridvalue, position);


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat stringFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String initString = "";
        String finalString = "";
        try {
            Date newDate = formatter.parse(selectedInitialDateString);
            Date finalDate = formatter.parse(selectedFinalDateString);
            initString = stringFormatter.format(newDate);
            finalString = stringFormatter.format(finalDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dayView.setText(gridvalue);


        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Gotham-Medium.ttf");
        dayView.setTypeface(typeface);
        String date = dayString.get(position);


        if (utils.isBetweenDates(initString + timeInMiliSec, finalString + timeInMiliSec,
                date + timeInMiliSec)) {
            layoutdatebg.setBackgroundResource(R.drawable.date_selection_highlight_bg);
        } else {
            layoutdatebg.setBackgroundResource(0);
            dayView.setBackgroundResource(R.drawable.list_item_selector);
        }

        final Typeface typefaceLight = ResourcesCompat.getFont(mContext, R.font.vag_light);
        final Typeface typefaceBold = ResourcesCompat.getFont(mContext, R.font.vag_bold);

        if (date.equalsIgnoreCase(initString)) {
            editTextView(dayView,typefaceBold);
        } else if (date.equalsIgnoreCase(finalString)) {
            editTextView(dayView,typefaceBold);
        } else {
            dayView.setBackgroundResource(0);
            dayView.setTypeface(typefaceLight);
            dayView.setTextColor(ContextCompat.getColor(mContext, R.color.font_hint));
        }
        return v;
    }

    private void editTextView(TextView dayView, Typeface typefaceBold){
        dayView.setTextColor(ContextCompat.getColor(mContext, R.color.bottom_navigation_bar_selector));
        dayView.setBackgroundResource(R.drawable.date_selection_bg);
        dayView.setTypeface(typefaceBold);
    }
    private View previousView;

    public View setSelected(View view) {
        if (previousView != null) {
            previousView.setBackgroundResource(R.drawable.list_item_selector);
        }
        previousView = view;
        view.setBackgroundResource(R.drawable.date_range_bg);
        return view;
    }

    private void dayCheckFunc(TextView dayView, String gridvalue, int position) {
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            dayView.setTextColor(Color.parseColor("#a9a9a9"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        }
        if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            dayView.setTextColor(Color.parseColor("#a9a9a9"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            dayView.setTextColor(Color.parseColor("#6f7173"));
        }
    }

    public void refreshDays() {
        items.clear();
        dayString.clear();
        monthNames.clear();
        Locale.setDefault(Locale.getDefault());
        int currentMonth = month.get(MONTH);
        pmonth = (GregorianCalendar) month.clone();
        nmonth = (GregorianCalendar) month.clone();
        firstDay = month.get(DAY_OF_WEEK);
        maxWeeknumber = month.getActualMaximum(WEEK_OF_MONTH);
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...

        pmonthmaxset = (GregorianCalendar) pmonth.clone();

        pmonthmaxset.set(DAY_OF_MONTH, calMaxP + 1);


        for (int n = 0; n < mnthlength; n++) {
            itemvalue = df.format(pmonthmaxset.getTime());
            if (pmonthmaxset.get(MONTH) != currentMonth) {
                if (n == 0) {
                    monthNames.add(android.text.format.DateFormat.format("MMM", pmonthmaxset).toString());
                } else if (pmonthmaxset.get(DATE) == 1) {
                    monthNames.add(android.text.format.DateFormat.format("MMM", pmonthmaxset).toString());
                } else {
                    monthNames.add(" ");
                }
            } else {
                monthNames.add(" ");
            }
            pmonthmaxset.add(DATE, 1);
            dayString.add(itemvalue);
        }
    }

    private int getMaxP() {

        if (month.get(MONTH) == month.getActualMinimum(MONTH)) {
            pmonth.set((month.get(YEAR) - 1), month.getActualMaximum(MONTH), 1);
        } else {
            pmonth.set(MONTH, month.get(MONTH) - 1);
        }
        maxPNew = pmonth.getActualMaximum(DAY_OF_MONTH);
        return maxPNew;
    }
}