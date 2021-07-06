package com.sdp.appazul.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sdp.appazul.R;
import com.sdp.appazul.classes.Model;
import com.sdp.appazul.globals.Constants;
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


        if (models.get(position).getPeriod().equalsIgnoreCase("ThisMonthToDate")) {
            try {
                timeFrame.setText("Mes a la fecha");
                Date fromDateOld = null;
                Date toDateOld = null;
                fromDateOld = olderFormat.parse(models.get(position).getDateFrom());
                toDateOld = olderFormat.parse(models.get(position).getDateTo());

                String fromDate = format.format(fromDateOld);
                String toDate = format.format(toDateOld);

                tvDateFilter.setText(fromDate + " - " + toDate);
                tvDateFilter.setVisibility(View.VISIBLE);
                month.setVisibility(View.GONE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (models.get(position).getPeriod().equalsIgnoreCase("PastMonth")) {
            try {
                Date toDateOld = null;


                toDateOld = olderFormat.parse(models.get(position).getDateTo());
                String toDate = format.format(toDateOld);  //31/05/2021
                month.setVisibility(View.VISIBLE);
                String monthOfDate = toDate.substring(3, 5);
                String yearOfDate = toDate.substring(6, 10);
                System.out.println("toDate " + toDate);
                System.out.println("yearOfDate " + yearOfDate);

                Map<String, String> monthList = dateUtils.getSpanishMonth();
                if (monthList != null && monthList.containsKey(monthOfDate)) {
                    monthList.get(monthOfDate);

                    System.out.println("Month Value " + monthList.get(monthOfDate));
                }
                tvDateFilter.setVisibility(View.GONE);
                timeFrame.setText(" - Mes Anterior");
                month.setText(monthList.get(monthOfDate) + " " + yearOfDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (models.get(position).getPeriod().equalsIgnoreCase("PastDay")) {
            try {
                Date toDateOld = null;

                toDateOld = olderFormat.parse(models.get(position).getDateTo());
                String toDate = format.format(toDateOld);

                tvDateFilter.setVisibility(View.GONE);
                month.setVisibility(View.VISIBLE);

                month.setText(toDate);
                timeFrame.setText(" - Último día");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        double amountToReceive = Double.parseDouble(models.get(position).getAmountToReceive());

        amount.setText(Constants.CURRANCY_FORMAT + amountFormat.format(amountToReceive));


        double totalAmount = Double.parseDouble(models.get(position).getTransactionSum());

        netSalesAmountNumber.setText(Constants.CURRANCY_FORMAT + amountFormat.format(totalAmount));

        double discount = Double.parseDouble(models.get(position).getDiscount());

        totalDiscountNumber.setText(Constants.CURRANCY_FORMAT + amountFormat.format(discount));

        quantityNumber.setText(models.get(position).getTransactionCount());

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
