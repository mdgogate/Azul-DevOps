package com.sdp.appazul.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DateUtils {


    @SuppressLint("SimpleDateFormat")
    public static String changeDateFormat(String date, String oldFormat, String newFormat) {
        String formattedDate = "";
        if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(oldFormat) && !TextUtils.isEmpty(newFormat)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
            SimpleDateFormat newformat = new SimpleDateFormat(newFormat);
            try {
                Date date3 = dateFormat.parse(date);
                formattedDate = newformat.format(Objects.requireNonNull(date3));
            } catch (ParseException e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }
        }
        return formattedDate;
    }

    @SuppressLint("SimpleDateFormat")
    public boolean isValidDate(String toDate, String fromDate) { // to and from
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DD_MM_YYYY);
        Date toDateSelected;
        Date fromDateSelected;
        if (!TextUtils.isEmpty(toDate) && !TextUtils.isEmpty(fromDate)) {
            try {
                toDateSelected = sdf.parse(toDate);
                fromDateSelected = sdf.parse(fromDate);
                if (toDateSelected.compareTo(fromDateSelected) > 0) {
                    return false;
                } else if (toDateSelected.compareTo(fromDateSelected) < 0) {
                    return true;
                } else if (toDateSelected.compareTo(fromDateSelected) == 0) {
                    return false;
                }
            } catch (ParseException e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }
        }
        return false;
    }

    @SuppressLint("SimpleDateFormat")
    public boolean isCurrentDayCheck(String currentDate, String selectedDate) { // to and from
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DD_MM_YYYY);
        Date curDate;
        Date selDate;
        if (!TextUtils.isEmpty(currentDate) && !TextUtils.isEmpty(selectedDate)) {
            try {
                curDate = sdf.parse(currentDate);
                selDate = sdf.parse(selectedDate);
                if (selDate.compareTo(curDate) > 0) {
                    return false;
                } else if (selDate.compareTo(curDate) < 0) {
                    return true;
                } else if (selDate.compareTo(curDate) == 0) {
                    return true;
                }
            } catch (ParseException e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }
        }
        return false;
    }

    @SuppressLint("SimpleDateFormat")
    public boolean isToDateSmaller(String from, String to) {

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DD_MM_YYYY);
        Date fromd;
        Date tod;
        if (!TextUtils.isEmpty(from) && !TextUtils.isEmpty(to)) {
            try {
                fromd = sdf.parse(from);
                tod = sdf.parse(to);
                if (tod.compareTo(fromd) > 0) {
                    return false;
                } else if (tod.compareTo(fromd) < 0) {
                    return true;
                } else if (tod.compareTo(fromd) == 0) {
                    return false;
                }
            } catch (ParseException e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }
        }
        return false;
    }

    @SuppressLint("SimpleDateFormat")
    public boolean isFromDateSmaller(String from, String to) {

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DD_MM_YYYY);
        Date fromd;
        Date tod;
        if (!TextUtils.isEmpty(from) && !TextUtils.isEmpty(to)) {
            try {
                fromd = sdf.parse(from);
                tod = sdf.parse(to);
                if (fromd.compareTo(tod) > 0) {
                    return false;
                } else if (fromd.compareTo(tod) < 0) {
                    return true;
                } else if (fromd.compareTo(tod) == 0) {
                    return false;
                }
            } catch (ParseException e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }
        }
        return false;
    }


    @SuppressLint("SimpleDateFormat")
    public String getStringFormatted(String dateFormat, Date inputDate) {
        SimpleDateFormat df = null;
        if (inputDate != null && !TextUtils.isEmpty(dateFormat)) {
            df = new SimpleDateFormat(dateFormat);
            return df.format(inputDate);
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public Date getDateFormatted(String dateFormat, String inputStringDate) {
        if (!TextUtils.isEmpty(inputStringDate) && !TextUtils.isEmpty(dateFormat)) {
            SimpleDateFormat df = new SimpleDateFormat(dateFormat);
            try {
                return df.parse(inputStringDate);
            } catch (ParseException e) {

                Log.e("Exception : ", Log.getStackTraceString(e));
            }
        }
        return null;
    }


    public List<String> getDaysList() {
        ArrayList<String> daysList = new ArrayList<>();
        daysList.add("Dom");
        daysList.add("Lun");
        daysList.add("Mar");
        daysList.add("Mie");
        daysList.add("Jue");
        daysList.add("Vie");
        daysList.add("Sab");
        return daysList;
    }


    public List<String> getmonthList() {

        ArrayList<String> monthList = new ArrayList<>();
        monthList.add("enero");
        monthList.add("febrero");
        monthList.add("marzo");
        monthList.add("abril");
        monthList.add("mayo");
        monthList.add("junio");
        monthList.add("julio");
        monthList.add("agosto");
        monthList.add("septiembre");
        monthList.add("octubre");
        monthList.add("noviembre");
        monthList.add("diciembre");

        return monthList;
    }

    public HashMap<String, String> getSpanishMonth() {
        HashMap<String, String> monthList = new HashMap<>();//Creating HashMap

        monthList.put("01", "Enero");
        monthList.put("02", "Febrero");
        monthList.put("03", "Marzo");
        monthList.put("04", "Abril");
        monthList.put("05", "Mayo");
        monthList.put("06", "Junio");
        monthList.put("07", "Julio");
        monthList.put("08", "Agosto");
        monthList.put("09", "Septiembre");
        monthList.put("10", "Octubre");
        monthList.put("11", "Noviembre");
        monthList.put("12", "Diciembre");

        return monthList;
    }

    public List<String> getmonthListWithCaps() {

        ArrayList<String> monthList = new ArrayList<>();
        monthList.add("Enero");
        monthList.add("Febrero");
        monthList.add("Marzo");
        monthList.add("Abril");
        monthList.add("Mayo");
        monthList.add("Junio");
        monthList.add("Julio");
        monthList.add("Agosto");
        monthList.add("Septiembre");
        monthList.add("Octubre");
        monthList.add("Noviembre");
        monthList.add("Diciembre");

        return monthList;
    }

    public boolean isBetweenDates(String min, String max, String inputDate) {
        try {
            boolean returnValue = false;
            if (!TextUtils.isEmpty(min) && !TextUtils.isEmpty(max) && !TextUtils.isEmpty(inputDate)) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date minDate = null;
                Date maxDate = null;
                Date input = null;

                minDate = df1.parse(min);
                maxDate = df1.parse(max);
                input = df.parse(inputDate);


                returnValue = input.getTime() >= minDate.getTime() && input.getTime() <= maxDate.getTime()
                        || input.getTime() >= maxDate.getTime() && input.getTime() <= minDate.getTime();
            }
            return returnValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
