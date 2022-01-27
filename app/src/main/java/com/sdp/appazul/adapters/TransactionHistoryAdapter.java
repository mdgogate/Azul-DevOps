package com.sdp.appazul.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sdp.appazul.R;
import com.sdp.appazul.classes.TransactionHistory;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.utils.DateUtils;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionHistoryAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    List<TransactionHistory> historyArrayList;
    SimpleDateFormat olderFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat newFormat = new SimpleDateFormat(Constants.DD_MM_YYYY);
    DateUtils dateUtils = new DateUtils();
    Map<String, String> monthList = new HashMap<>();

    public TransactionHistoryAdapter(Context context, List<TransactionHistory> historyArrayList) {
        this.context = context;
        this.historyArrayList = historyArrayList;
    }

    @Override
    public int getCount() {
        return historyArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = inflater.inflate(R.layout.transaction_list_item, null);
        }

        TextView tvLocationName = view.findViewById(R.id.tvLocationName);
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvReferenceNo = view.findViewById(R.id.tvReferenceNo);
        TextView tvPrice = view.findViewById(R.id.tvPrice);

        tvLocationName.setText(historyArrayList.get(pos).getLocation());
        String referenceNo = historyArrayList.get(pos).getReferenceNo().substring(1);
        DecimalFormat format = new DecimalFormat("#,##0.00");

        tvReferenceNo.setText(referenceNo);
        double amount = Double.parseDouble(historyArrayList.get(pos).getAmount());
        tvPrice.setText("RD$ " + format.format(amount));

        String givenTime = historyArrayList.get(pos).getTime();//PT17H22M"
        String hr = givenTime.substring(0, 5);

        String finalTimeString = timeFormatConvertor(hr);
        String transDate ;
        String startDay = null;
        String monthOfDate = null;
        try {
            Date newDate = olderFormat.parse(historyArrayList.get(pos).getTrDate());
            String trnDate = newFormat.format(newDate);
            transDate = trnDate.substring(0, 10);
            startDay = trnDate.substring(0, 2);
            monthOfDate = transDate.substring(3, 5);

            monthList = dateUtils.getSpanishMonth();

        } catch (ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }


        if (!TextUtils.isEmpty(finalTimeString)) {
            tvTime.setText(startDay+ " de "+monthList.get(monthOfDate) + " - " + finalTimeString);
        }
        return view;
    }

    public void updateList(List<TransactionHistory> list) {
        historyArrayList = list;
        notifyDataSetChanged();
    }

    private String timeFormatConvertor(String inputTime) {
        try {
            DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
            symbols.setAmPmStrings(new String[]{"a.m.", "p.m."});
            SimpleDateFormat sdfFormat24 = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdfFormat12 = new SimpleDateFormat("hh:mm a");
            sdfFormat12.setDateFormatSymbols(symbols);
            Date datefor24hr = sdfFormat24.parse(inputTime);
            return sdfFormat12.format(datefor24hr);
        } catch (final ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return null;
    }
}
