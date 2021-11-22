package com.sdp.appazul.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sdp.appazul.R;
import com.sdp.appazul.classes.Model;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.utils.DateUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MyAdapter extends PagerAdapter {

    private final List<Model> models;
    LayoutInflater layoutInflater;
    private final Context context;
    SimpleDateFormat olderFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat format = new SimpleDateFormat(Constants.DD_MM_YYYY);
    DecimalFormat amountFormat = new DecimalFormat("#,##0.00");
    DateUtils dateUtils = new DateUtils();

    public MyAdapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cardview_widget, container, false);

        TextView tvDateFilter;
        TextView amount;
        TextView month;
        TextView timeFrame;
        TextView netSalesAmountNumber;
        TextView totalDiscountNumber;
        TextView quantityNumber;

        amount = view.findViewById(R.id.amount);
        month = view.findViewById(R.id.month);
        tvDateFilter = view.findViewById(R.id.tvDateFilter);
        timeFrame = view.findViewById(R.id.timeFrame);
        netSalesAmountNumber = view.findViewById(R.id.netSalesAmountNumber);
        totalDiscountNumber = view.findViewById(R.id.totalDiscountNumber);
        quantityNumber = view.findViewById(R.id.quantityNumber);


        periodValidation(timeFrame, month, tvDateFilter, position);

        if (!TextUtils.isEmpty(models.get(position).getAmountToReceive()) && !models.get(position).getAmountToReceive().equalsIgnoreCase("-")) {
            setAmount(position, amount);
        } else {
            setEmptyAmount(position, amount);
        }

        if (!TextUtils.isEmpty(models.get(position).getTransactionSum()) && !models.get(position).getTransactionSum().equalsIgnoreCase("-")) {
            setSalesAmount(position, netSalesAmountNumber);
        } else {
            setEmptySalesAmount(netSalesAmountNumber, position);
        }

        if (!TextUtils.isEmpty(models.get(position).getDiscount()) && !models.get(position).getDiscount().equalsIgnoreCase("-")) {
            setDiscount(position, totalDiscountNumber);
        } else {
            setEmptyDiscount(position, totalDiscountNumber);
        }
        if (!TextUtils.isEmpty(models.get(position).getTransactionCount()) && !models.get(position).getTransactionCount().equalsIgnoreCase("-")) {
            quantityNumber.setText(models.get(position).getTransactionCount());
        } else {
            quantityNumber.setText("-");
        }

        container.addView(view, 0);

        return view;
    }

    private void setDiscount(int position, TextView totalDiscountNumber) {
        double discount = Double.parseDouble(models.get(position).getDiscount());
        if (models.get(position).getCurrency().equalsIgnoreCase("USD")) {
            totalDiscountNumber.setText(Constants.CURRENCY_FORMAT_USD + amountFormat.format(discount));
        } else {
            totalDiscountNumber.setText(Constants.CURRENCY_FORMAT + amountFormat.format(discount));
        }
    }

    private void setEmptyDiscount(int position, TextView totalDiscountNumber) {
        if (models.get(position).getCurrency().equalsIgnoreCase("USD")) {
            totalDiscountNumber.setText(Constants.CURRENCY_FORMAT_USD + "-");
        } else {
            totalDiscountNumber.setText(Constants.CURRENCY_FORMAT + "-");
        }
    }

    private void setSalesAmount(int position, TextView netSalesAmountNumber) {
        double totalAmount = Double.parseDouble(models.get(position).getTransactionSum());
        if (models.get(position).getCurrency().equalsIgnoreCase("USD")) {
            netSalesAmountNumber.setText(Constants.CURRENCY_FORMAT_USD + amountFormat.format(totalAmount));
        } else {
            netSalesAmountNumber.setText(Constants.CURRENCY_FORMAT + amountFormat.format(totalAmount));
        }
    }

    private void setEmptySalesAmount(TextView netSalesAmountNumber, int position) {
        if (models.get(position).getCurrency().equalsIgnoreCase("USD")) {
            netSalesAmountNumber.setText(Constants.CURRENCY_FORMAT_USD + "-");
        } else {
            netSalesAmountNumber.setText(Constants.CURRENCY_FORMAT + "-");
        }
    }

    private void setAmount(int position, TextView amount) {
        double amountToReceive = Double.parseDouble(models.get(position).getAmountToReceive());
        if (models.get(position).getCurrency().equalsIgnoreCase("USD")) {
            amount.setText(Constants.CURRENCY_FORMAT_USD + amountFormat.format(amountToReceive));
        } else {
            amount.setText(Constants.CURRENCY_FORMAT + amountFormat.format(amountToReceive));
        }
    }

    private void setEmptyAmount(int position, TextView amount) {
        if (models.get(position).getCurrency().equalsIgnoreCase("USD")) {
            amount.setText(Constants.CURRENCY_FORMAT_USD + "-");
        } else {
            amount.setText(Constants.CURRENCY_FORMAT + "-");
        }
    }

    private void periodValidation(TextView timeFrame, TextView month, TextView tvDateFilter, int position) {
        if (models.get(position).getPeriod().equalsIgnoreCase("ThisMonthToDate")) {
            currrentMonthToDate(timeFrame, month, tvDateFilter, position);
        } else if (models.get(position).getPeriod().equalsIgnoreCase("PastMonth")) {
            lastMonth(timeFrame, tvDateFilter, position, month);
        } else if (models.get(position).getPeriod().equalsIgnoreCase("PastDay")) {
            lastDay(timeFrame, tvDateFilter, position, month);
        } else {
            month.setText("-");
        }
    }

    private void lastDay(TextView timeFrame, TextView tvDateFilter, int position, TextView month) {
        try {
            Date toDateOld;

            toDateOld = olderFormat.parse(models.get(position).getDateTo());
            String toDate = format.format(toDateOld);

            tvDateFilter.setVisibility(View.GONE);
            month.setVisibility(View.VISIBLE);

            month.setText(toDate);
            timeFrame.setText(" - Último día");
        } catch (ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void lastMonth(TextView timeFrame, TextView tvDateFilter, int position, TextView month) {
        try {
            Date toDateOld;


            toDateOld = olderFormat.parse(models.get(position).getDateTo());
            String toDate = format.format(toDateOld);  //31/05/2021
            month.setVisibility(View.VISIBLE);
            String monthOfDate = toDate.substring(3, 5);
            String yearOfDate = toDate.substring(6, 10);

            Map<String, String> monthList = dateUtils.getSpanishMonth();

            tvDateFilter.setVisibility(View.GONE);
            timeFrame.setText(" - Mes anterior");
            month.setText(monthList.get(monthOfDate) + " " + yearOfDate);

        } catch (ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void currrentMonthToDate(TextView timeFrame, TextView month, TextView tvDateFilter, int position) {
        try {
            timeFrame.setText("Mes a la fecha");
            Date fromDateOld;
            Date toDateOld;
            fromDateOld = olderFormat.parse(models.get(position).getDateFrom());
            toDateOld = olderFormat.parse(models.get(position).getDateTo());

            String fromDate = format.format(fromDateOld);
            String toDate = format.format(toDateOld);

            tvDateFilter.setText(fromDate + " - " + toDate);
            tvDateFilter.setVisibility(View.VISIBLE);
            month.setVisibility(View.GONE);
        } catch (ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
