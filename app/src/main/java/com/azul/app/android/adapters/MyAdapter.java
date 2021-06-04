package com.azul.app.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.azul.app.android.R;
import com.azul.app.android.classes.Model;

import java.util.List;


public class MyAdapter extends PagerAdapter {

    private final List<Model> models;
    LayoutInflater layoutInflater;
    private final Context context;

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

        TextView amount;
        TextView month;
        TextView timeFrame;
        TextView netSalesAmountNumber;
        TextView totalDiscountNumber;
        TextView quantityNumber;

        amount = view.findViewById(R.id.amount);
        month = view.findViewById(R.id.month);
        timeFrame = view.findViewById(R.id.timeFrame);
        netSalesAmountNumber = view.findViewById(R.id.netSalesAmountNumber);
        totalDiscountNumber = view.findViewById(R.id.totalDiscountNumber);
        quantityNumber = view.findViewById(R.id.quantityNumber);

        amount.setText(models.get(position).getAmount());
        month.setText(models.get(position).getMonth());
        timeFrame.setText(models.get(position).getTimeFrame());
        netSalesAmountNumber.setText(models.get(position).getNetSalesAmountNumber());
        totalDiscountNumber.setText(models.get(position).getTotalDiscountNumber());
        quantityNumber.setText(models.get(position).getQuantityNumber());

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
