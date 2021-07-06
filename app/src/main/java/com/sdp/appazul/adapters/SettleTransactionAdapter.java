package com.sdp.appazul.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdp.appazul.R;
import com.sdp.appazul.classes.SettleTransaction;
import com.sdp.appazul.globals.Constants;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettleTransactionAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    List<SettleTransaction> list;
    TextView tvCardNumber;
    TextView tvDateTime;
    TextView tvCardName1;
    TextView tvApprovalNo;
    TextView tvAmount;
    TextView tvtransactionType;
    TextView tvTerminalId;
    TextView tvLotNo;
    TextView tvRefNo;
    TextView tvSettleDate;
    ImageView cardImgView;
    LinearLayout mainShadowItem;

    public SettleTransactionAdapter(Context context, List<SettleTransaction> list) {
        this.context = context;
        this.list = list;
    }

    public void updateList(List<SettleTransaction> settleTransactions) {
        this.list = settleTransactions;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = inflater.inflate(R.layout.settle_trans_list_item, null);
        }
        cardImgView = view.findViewById(R.id.cardImgView);
        tvCardNumber = view.findViewById(R.id.tvCardNumber);
        tvDateTime = view.findViewById(R.id.tvDateTime);
        tvCardName1 = view.findViewById(R.id.tvCardName1);
        tvApprovalNo = view.findViewById(R.id.tvApprovalNo);
        tvAmount = view.findViewById(R.id.tvAmount);
        tvtransactionType = view.findViewById(R.id.tvtransactionType);
        tvTerminalId = view.findViewById(R.id.tvTerminalId);
        tvLotNo = view.findViewById(R.id.tvLotNo);
        tvRefNo = view.findViewById(R.id.tvRefNo);
        tvSettleDate = view.findViewById(R.id.tvSettleDate);

        SettleTransaction st = list.get(i);
        String currency = st.getCurrency();
        if (st.getCardType().equalsIgnoreCase("MasterCard")) {
            cardImgView.setImageResource(R.drawable.mastercard_logo);
        } else if (st.getCardType().equalsIgnoreCase("Visa")) {
            cardImgView.setImageResource(R.drawable.visa_icon);
        } else if (st.getCardType().equalsIgnoreCase("American Express")) {
            cardImgView.setImageResource(R.drawable.ic_marca_tarjeta___amex);
        }

        mainShadowItem = view.findViewById(R.id.mainShadowItem);
        if (st.getTrnType().equalsIgnoreCase("Venta")) {
            mainShadowItem.setBackgroundResource(R.drawable.card_back_shadow_blue);
        } else {
            mainShadowItem.setBackgroundResource(R.drawable.card_back_with_shadow);
        }

        SimpleDateFormat olderFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat(Constants.DD_MM_YYYY);
        try {
            Date newDate = olderFormat.parse(st.getTrnDateTime());
            Date oldSettleDate = olderFormat.parse(st.getSettlementDate());
            String trnDate = format.format(newDate);
            String newSettleDate = format.format(oldSettleDate);
            String transDate = trnDate.substring(0, 10);
            String finalSettleDate = newSettleDate.substring(0, 10);
            String trnTime = st.getTime().substring(11, 17);
            String finalTimeString = timeFormatConvertor(trnTime);
            tvDateTime.setText(transDate + " - " + finalTimeString);
            tvSettleDate.setText(finalSettleDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DecimalFormat currFormat = new DecimalFormat("#,##0.00");

        tvCardNumber.setText(st.getCardNumber());
        tvCardName1.setText(st.getCardName() + " |");
        tvApprovalNo.setText(st.getApprovalNo());
        double amount = Double.parseDouble(st.getAmount());
        tvAmount.setText(currency + " " + currFormat.format(amount));
        tvtransactionType.setText(st.getTrnType());
        tvTerminalId.setText(st.getTerminalId());
        tvLotNo.setText(st.getLotNo());
        tvRefNo.setText(st.getReferenceNo());


        return view;
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
            e.printStackTrace();
        }
        return null;
    }
}
