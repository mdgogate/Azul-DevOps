package com.sdp.appazul.adapters;

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.sdp.appazul.R;
import com.sdp.appazul.classes.TapOnPhone;
import com.sdp.appazul.classes.TransactionHistory;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.utils.DateUtils;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import digital.paynetics.phos.sdk.entities.Transaction;

public class TapOnPhoneAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    List<Transaction> historyArrayList;
    ImageView imageViewCards;
    LinearLayout grid_adapter;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat currFormat = new DecimalFormat("#,##0.00", symbols);
    SimpleDateFormat olderFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - hh:mm aaa");
    DateFormatSymbols dateSymbols = new DateFormatSymbols(Locale.getDefault());

    public TapOnPhoneAdapter(Context context, List<Transaction> historyArrayList) {
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
            view = inflater.inflate(R.layout.tap_on_phone_adapter_layout, null);
        }

        TextView tvCardName = view.findViewById(R.id.tvCardName);
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvApprovedNo = view.findViewById(R.id.tvApprovedNo);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        TextView tvTrnType = view.findViewById(R.id.tvTrnType);
        imageViewCards = view.findViewById(R.id.imageViewCards);

        Transaction tap = historyArrayList.get(pos);
        ImageView igMoreInfo = view.findViewById(R.id.igMoreInfo);

        if (tap.getCard().equalsIgnoreCase("")) {
            tvCardName.setText("-");
        } else {
            tvCardName.setText(tap.getCard());
        }
        Date trnDate;

//        try {
            trnDate = tap.getDate();
//            trnDate = olderFormat.parse(tap.getDate().toString());
            dateSymbols.setAmPmStrings(new String[]{"a.m.", "p.m."});
            format.setDateFormatSymbols(dateSymbols);
            String toDate = format.format(trnDate);
            tvTime.setText(toDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        tvPrice.setText(Constants.CURRENCY_FORMAT + currFormat.format(tap.getAmount()));
        if (tap.getCardType().equalsIgnoreCase("MASTERCARD")) {
            imageViewCards.setImageResource(R.drawable.ic_marca_mastercard);
        } else if (tap.getCardType().equalsIgnoreCase("VISA")) {
            imageViewCards.setImageResource(R.drawable.ic_marca_visa);
        } else if (tap.getCardType().equalsIgnoreCase("")) {
            imageViewCards.setImageResource(R.drawable.no_card_presenet);
        }

        if (tap.getType().getType().equalsIgnoreCase("sale")) {
            tvTrnType.setText("Venta");
        } else if (tap.getType().getType().equalsIgnoreCase("refund")) {
            tvTrnType.setText("Devolución");
        } else if (tap.getType().getType().equalsIgnoreCase("void")) {
            tvTrnType.setText("Anulación");
        }

        if (tap.getAuthCode().equalsIgnoreCase("")) {
            tvApprovedNo.setText("Declinada");
            tvApprovedNo.setTextColor(ContextCompat.getColor(context, R.color.clear_bg));
        } else {
            tvApprovedNo.setText("Aprobada " + tap.getAuthCode());
            tvApprovedNo.setTextColor(ContextCompat.getColor(context, R.color.date_border_color));
        }

        grid_adapter = view.findViewById(R.id.grid_adapter);

        setBackgroundByType(tap);

        igMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customListener != null) {
                    customListener.onItemClickListener(tap);
                }
            }
        });
        return view;
    }

    private void setBackgroundByType(Transaction tap) {
        if (tap.getType().getType().equalsIgnoreCase("sale")) {
            grid_adapter.setBackgroundResource(R.drawable.top_back_shadow);
        } else if (tap.getType().getType().equalsIgnoreCase("void")) {
            grid_adapter.setBackgroundResource(R.drawable.top_back_shadow_red);
        } else if (tap.getType().getType().equalsIgnoreCase("refund")) {
            grid_adapter.setBackgroundResource(R.drawable.top_back_shadow_gray);
        } else {
            grid_adapter.setBackgroundResource(R.drawable.top_back_shadow);
        }
    }

    public void setCustomButtonListener(PaymentItemSelectListener listener) {
        this.customListener = listener;
    }

    PaymentItemSelectListener customListener;

    public interface PaymentItemSelectListener {
        public void onItemClickListener(Transaction transaction);
    }

}
