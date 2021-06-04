package com.azul.app.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.azul.app.android.R;
import com.azul.app.android.classes.SettleTransaction;

import java.util.List;

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
    public SettleTransactionAdapter(Context context, List<SettleTransaction> list) {
        this.context = context;
        this.list = list;
    }
    public void updateList(List<SettleTransaction> settleTransactions){
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
        tvCardNumber =  view.findViewById(R.id.tvCardNumber);
        tvDateTime =  view.findViewById(R.id.tvDateTime);
        tvCardName1 =  view.findViewById(R.id.tvCardName1);
        tvApprovalNo =  view.findViewById(R.id.tvApprovalNo);
        tvAmount =  view.findViewById(R.id.tvAmount);
        tvtransactionType =  view.findViewById(R.id.tvtransactionType);
        tvTerminalId =  view.findViewById(R.id.tvTerminalId);
        tvLotNo =  view.findViewById(R.id.tvLotNo);
        tvRefNo =  view.findViewById(R.id.tvRefNo);
        tvSettleDate =  view.findViewById(R.id.tvSettleDate);

        SettleTransaction st = list.get(i);

        if (st.getCardType().equalsIgnoreCase("0")){
            cardImgView.setImageResource(R.drawable.mastercard_logo);
        }else if (st.getCardType().equalsIgnoreCase("1")){
            cardImgView.setImageResource(R.drawable.visa_icon);
        }else if (st.getCardType().equalsIgnoreCase("2")){
            cardImgView.setImageResource(R.drawable.discover_logo);
        }else {
            cardImgView.setImageResource(R.drawable.mastercard_logo);
        }

        tvCardNumber.setText(st.getCardNumber());
        tvDateTime.setText(st.getTrnDateTime());
        tvCardName1.setText(st.getCardName());
        tvApprovalNo.setText(st.getApprovalNo());
        tvAmount.setText(st.getAmount());
        tvtransactionType.setText(st.getTrnType());
        tvTerminalId.setText(st.getTerminalId());
        tvLotNo.setText(st.getLotNo());
        tvRefNo.setText(st.getReferenceNo());
        tvSettleDate.setText(st.getSettlementDate());



        return view;
    }
}
