package com.sdp.appazul.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.sdp.appazul.R;
import com.sdp.appazul.classes.PaymentTransactions;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.utils.DateUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentTransactionAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    DateUtils dateUtils = new DateUtils();
    List<PaymentTransactions> transactionsList;
    SimpleDateFormat olderDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat newDateFormat = new SimpleDateFormat(Constants.DD_MM_YYYY);
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);

    DecimalFormat amountDecimalFormat = new DecimalFormat("#,##0.00",symbols);

    TextView tvPaymentDate;
    TextView tvPaymentAmount;
    TextView tvTransactionType;
    TextView tvLinkId;
    TextView tvApprovalNo;
    TextView tvStatus;
    TextView tvClientName;
    ImageView imgMoreButton;

    customItemSelectListener customListener;

    public PaymentTransactionAdapter(Context context, List<PaymentTransactions> transactionsList) {
        this.context = context;
        this.transactionsList = transactionsList;
    }

    @Override
    public int getCount() {
        return transactionsList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void updateUi(Context context, List<PaymentTransactions> transactionsList) {
        this.context = context;
        this.transactionsList = transactionsList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = inflater.inflate(R.layout.payment_transaction_item_layout, parent, false);
        }
        PaymentTransactions transactions = transactionsList.get(i);
        tvPaymentDate = view.findViewById(R.id.tvPaymentDate);
        tvPaymentAmount = view.findViewById(R.id.tvPaymentAmount);
        tvTransactionType = view.findViewById(R.id.tvTransactionType);
        tvLinkId = view.findViewById(R.id.tvLinkId);
        tvApprovalNo = view.findViewById(R.id.tvApprovalNo);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvClientName = view.findViewById(R.id.tvClientName);
        imgMoreButton = view.findViewById(R.id.imgMoreButton);

        tvLinkId.setText("Link ID: " + transactions.getLinkId());
        tvClientName.setText(transactions.getClientName());
        checkTransactionType(transactions);
        double amount = Double.parseDouble(transactions.getAmount());
        String finalAmount = null;
        if (transactions.getCurrency().equalsIgnoreCase("USD")) {
            finalAmount = Constants.CURRENCY_FORMAT_USD + "" + amountDecimalFormat.format(amount);
        } else {
            finalAmount = Constants.CURRENCY_FORMAT + "" + amountDecimalFormat.format(amount);
        }
        tvPaymentAmount.setText(finalAmount);

        checkItemsStatus(transactions);
        if (transactions.getStatus().equalsIgnoreCase("Cancelled")
                && transactions.getTransactionResponse().equalsIgnoreCase("None")) {
            tvApprovalNo.setVisibility(View.GONE);
            tvStatus.setTextColor(Color.parseColor("#FF3A3A"));
            tvStatus.setText(Constants.STATUS_CANCELLED_SPANISH);
            Log.d("LOGS", "getView: 66" + transactions.getStatus() + " "+ transactions.getLinkId() + " "+ transactions.getPayedAuthorizationNumber());
        } else if (transactions.getStatus().equalsIgnoreCase("Expired")
                || transactions.getStatus().equalsIgnoreCase("Expirado")) {
            tvApprovalNo.setVisibility(View.GONE);
            tvStatus.setText("Expirado");
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.no_record_found));
            Log.d("LOGS", "getView: 77" + transactions.getStatus() + " "+ transactions.getLinkId() + " "+ transactions.getPayedAuthorizationNumber());
        } else if (transactions.getStatus().equalsIgnoreCase("Generated") ||
                transactions.getStatus().equalsIgnoreCase("Generado")) {
            tvApprovalNo.setVisibility(View.GONE);
            tvStatus.setText("Generado");
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.cursor_circle));
            Log.d("LOGS", "getView: 88" + transactions.getStatus() + " "+ transactions.getLinkId() + " "+ transactions.getPayedAuthorizationNumber());
        } else if (transactions.getStatus().equalsIgnoreCase("Open")
                && transactions.getTransactionResponse().equalsIgnoreCase("None")) {
            tvApprovalNo.setVisibility(View.GONE);
            tvStatus.setText("Abierto");
            Log.d("LOGS", "getView: 99" + transactions.getStatus() + " "+ transactions.getLinkId() + " "+ transactions.getPayedAuthorizationNumber());
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.cursor_circle));
        }

        try {
            Date newDate = olderDateFormat.parse(transactions.getPaymentDate());
            String paymentDate = newDateFormat.format(newDate);
            tvPaymentDate.setText(paymentDate);

        } catch (ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }

        imgMoreButton.setOnClickListener(imgMoreButtonView -> {
            if (customListener != null) {
                customListener.onItemClickListener(transactions.getTransactionResponse(), i, transactions.getLinkId(), transactions.getStatus(), transactions.getAmount(),transactions.getCurrency());
            }
        });

        return view;
    }

    private void checkItemsStatus(PaymentTransactions transactions) {

        if (transactions.getStatus().equalsIgnoreCase("Processed") && //Processed == Procesado
                transactions.getTransactionResponse().equalsIgnoreCase("Approved")) {
            tvApprovalNo.setVisibility(View.VISIBLE);
            tvApprovalNo.setText(" Aprobada" + " " + transactions.getPayedAuthorizationNumber());
            tvApprovalNo.setTextColor(Color.parseColor("#3EAEC9"));
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.cursor_circle));
            tvStatus.setText("Procesado" + " - ");
            Log.d("LOGS", "getView: 11" + transactions.getStatus() + " "+ transactions.getLinkId() + " "+ transactions.getPayedAuthorizationNumber());

        } else if (transactions.getStatus().equalsIgnoreCase("Processed")
                && transactions.getTransactionResponse().equalsIgnoreCase("Declined")
                || transactions.getTransactionResponse().equalsIgnoreCase(Constants.STATUS_DECLINED_SPANISH)) {
            Log.d("LOGS", "getView: 22" + transactions.getStatus() + " "+ transactions.getLinkId() + " "+ transactions.getPayedAuthorizationNumber());

            tvApprovalNo.setVisibility(View.VISIBLE);
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.cursor_circle));
            tvStatus.setText("Procesado" + " - ");
            tvApprovalNo.setText(Constants.STATUS_DECLINED_SPANISH);
            tvApprovalNo.setTextColor(Color.parseColor(Constants.ORANGE));
        } else if (transactions.getStatus().equalsIgnoreCase("Voided") &&
                transactions.getTransactionResponse().equalsIgnoreCase(Constants.STATUS_DECLINED_SPANISH)) {
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.no_record_found));
            tvStatus.setText("Anulada" + " - ");
            tvApprovalNo.setVisibility(View.VISIBLE);
            tvApprovalNo.setText(" " + Constants.STATUS_DECLINED_SPANISH);
            tvApprovalNo.setTextColor(Color.parseColor(Constants.ORANGE));
            Log.d("LOGS", "getView: 33" + transactions.getStatus() + " "+ transactions.getLinkId() + " "+ transactions.getPayedAuthorizationNumber());

        } else if (transactions.getStatus().equalsIgnoreCase("Voided") &&
                transactions.getTransactionResponse().equalsIgnoreCase("Approved")
                || transactions.getTransactionResponse().equalsIgnoreCase("Aprobada")) {  //Approved  == Aprobada
            tvApprovalNo.setVisibility(View.VISIBLE);
            tvApprovalNo.setText(" Aprobada" + " " + transactions.getPayedAuthorizationNumber());
            tvApprovalNo.setTextColor(Color.parseColor("#3EAEC9"));
            tvStatus.setText("Anulada" + " - ");
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.no_record_found));
            Log.d("LOGS", "getView: 44" + transactions.getStatus() + " "+ transactions.getLinkId() + " "+ transactions.getPayedAuthorizationNumber());
        } else if (transactions.getStatus().equalsIgnoreCase(Constants.STATUS_CANCELLED_SPANISH)
                || transactions.getStatus().equalsIgnoreCase("Cancelled")
                && transactions.getTransactionResponse().equalsIgnoreCase("Declined")
                || transactions.getTransactionResponse().equalsIgnoreCase(Constants.STATUS_DECLINED_SPANISH)) {
            tvStatus.setTextColor(Color.parseColor("#FF3A3A"));
            tvStatus.setText(Constants.STATUS_CANCELLED_SPANISH + " - ");
            tvApprovalNo.setVisibility(View.VISIBLE);
            tvApprovalNo.setText(Constants.STATUS_DECLINED_SPANISH);
            tvApprovalNo.setTextColor(Color.parseColor(Constants.ORANGE));
            Log.d("LOGS", "getView: 55" + transactions.getStatus() + " "+ transactions.getLinkId() + " "+ transactions.getPayedAuthorizationNumber());
        }
    }

    private void checkTransactionType(PaymentTransactions transactions) {
        if (transactions.getTransactionStatus().equalsIgnoreCase("Sale")) {
            tvTransactionType.setText("Venta");
        } else if (transactions.getTransactionStatus().equalsIgnoreCase("HoldOnly")) {
            tvTransactionType.setText("Sólo autorización");
        } else if (transactions.getTransactionStatus().equalsIgnoreCase("HoldPosted")) {
            tvTransactionType.setText("Autorización completada");
        } else {
            tvTransactionType.setText(transactions.getTransactionStatus());
        }
    }

    public void setCustomButtonListener(customItemSelectListener listener) {
        this.customListener = listener;
    }

    public interface customItemSelectListener {
        public void onItemClickListener(String trnRsponse, int position, String value, String status, String amountToShow,String curr);
    }
}
