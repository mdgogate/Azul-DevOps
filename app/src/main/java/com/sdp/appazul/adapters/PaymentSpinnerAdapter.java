package com.sdp.appazul.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdp.appazul.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaymentSpinnerAdapter extends BaseAdapter {

    List<String> filterList;
    Context context;

    public PaymentSpinnerAdapter(List<String> filterList, Context context) {
        this.filterList = filterList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return filterList != null ? filterList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.raw, null);
        }
        TextView tvItemName = convertView.findViewById(R.id.tvSpinnerItem);
        if(position == 0){
            // Set the hint text color gray
            tvItemName.setTextColor(Color.parseColor("#C4D0DC"));
            Typeface typeface;
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/montserrat_semi_bold_font.ttf");
            tvItemName.setTypeface(typeface);
            tvItemName.setTextSize(14);
        }else {
            tvItemName.setTextSize(16);
            tvItemName.setTextColor(Color.parseColor("#0091DF"));
        }


        tvItemName.setText(filterList.get(position));
        return convertView;
    }


}
