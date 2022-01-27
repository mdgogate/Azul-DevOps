package com.sdp.appazul.activities.TapOnPhone;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.Consultant;
import com.sdp.appazul.classes.TapOnPhone;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import digital.paynetics.phos.sdk.entities.Transaction;

public class ToPRefundCancelMenu extends BottomSheetDialogFragment {

    TextView tvMenuAmount;
    TextView tvCardNumber;
    TextView tvTime;
    TextView tvReferenceNo;
    TextView tvTrnType;
    RelativeLayout btnCancelPayment;
    RelativeLayout btnRefundAmount;
    ToPBottomSheetCloseEvent bottomSheetCloseEvent;
    ImageView imgCardLogo;
    Transaction transaction;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat currFormat = new DecimalFormat("#,##0.00", symbols);
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - hh:mm aaa");
    DateFormatSymbols dateSymbols = new DateFormatSymbols(Locale.getDefault());

    public ToPRefundCancelMenu() {
    }

    public ToPRefundCancelMenu(String locId, String locName, Transaction transaction) {
        this.transaction = transaction;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_refund_cancel_menu_layout, container, false);
        initControls(view);
        return view;

    }

    private void initControls(View view) {
        tvMenuAmount = view.findViewById(R.id.tvMenuAmount);
        tvCardNumber = view.findViewById(R.id.tvCardNumber);
        imgCardLogo = view.findViewById(R.id.imgCardLogo);
        tvTime = view.findViewById(R.id.tvTime);
        tvReferenceNo = view.findViewById(R.id.tvReferenceNo);
        tvTrnType = view.findViewById(R.id.tvTrnType);
        btnCancelPayment = view.findViewById(R.id.btnCancelPayment);
        btnRefundAmount = view.findViewById(R.id.btnRefundAmount);
        btnCancelPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
                bottomSheetCloseEvent.onClosed(transaction, 1);


            }
        });
        btnRefundAmount.setOnClickListener(btnRefundAmountVIew -> {
            dismiss();
            bottomSheetCloseEvent.onClosed(transaction, 2);

        });

        if (transaction.getType().getType().equalsIgnoreCase("void")) {
            btnCancelPayment.setVisibility(View.GONE);
            btnRefundAmount.setVisibility(View.GONE);
        } else if (transaction.getType().getType().equalsIgnoreCase("refund")) {
            btnCancelPayment.setVisibility(View.GONE);
            btnRefundAmount.setVisibility(View.GONE);
        } else {
            btnCancelPayment.setVisibility(View.VISIBLE);
            btnRefundAmount.setVisibility(View.VISIBLE);
        }

        bindDAtaToUi();
    }

    private void bindDAtaToUi() {
        tvMenuAmount.setText(Constants.CURRENCY_FORMAT + currFormat.format(transaction.getAmount()));
        tvCardNumber.setText(transaction.getCard());
        if (transaction.getAuthCode().equalsIgnoreCase("")) {
            tvReferenceNo.setText("Declinada");
            tvReferenceNo.setTextColor(ContextCompat.getColor(getActivity(), R.color.clear_bg));
        } else {
            tvReferenceNo.setText("Aprobada " + transaction.getAuthCode());
            tvReferenceNo.setTextColor(ContextCompat.getColor(getActivity(), R.color.date_border_color));
        }

        if (transaction.getType().getType().equalsIgnoreCase("sale")) {
            tvTrnType.setText("Venta");
        } else if (transaction.getType().getType().equalsIgnoreCase("refund")) {
            tvTrnType.setText("Devolución");
        } else if (transaction.getType().getType().equalsIgnoreCase("void")) {
            tvTrnType.setText("Anulación");
        }
        if (transaction.getCardType().equalsIgnoreCase("MASTERCARD")) {
            imgCardLogo.setImageResource(R.drawable.ic_marca_mastercard);
        } else if (transaction.getCardType().equalsIgnoreCase("VISA")) {
            imgCardLogo.setImageResource(R.drawable.ic_marca_visa);
        } else if (transaction.getCardType().equalsIgnoreCase("")) {
            imgCardLogo.setImageResource(R.drawable.no_card_presenet);
        }

        Date trnDate;
        trnDate = transaction.getDate();
        dateSymbols.setAmPmStrings(new String[]{"a.m.", "p.m."});
        format.setDateFormatSymbols(dateSymbols);
        String toDate = format.format(trnDate);
        tvTime.setText(toDate.toLowerCase());

        Date d2 = new Date();
        long difference_In_Time
                = d2.getTime() - trnDate.getTime();

        long difference_In_Minutes
                = (difference_In_Time
                / (1000 * 60))
                % 60;

        long difference_In_Hours
                = (difference_In_Time
                / (1000 * 60 * 60))
                % 24;

        long difference_In_Years
                = (difference_In_Time
                / (1000l * 60 * 60 * 24 * 365));

        long difference_In_Days
                = (difference_In_Time
                / (1000 * 60 * 60 * 24))
                % 365;

        if (difference_In_Years == 0) {
            //Days
            checkDays(difference_In_Days, difference_In_Hours, difference_In_Minutes);
            Log.d("TAG", "1111  difference_In_Years: " + difference_In_Years);
        } else {
            btnCancelPayment.setVisibility(View.GONE);
        }
    }


    public void setBottomSheetCloseEvent(ToPBottomSheetCloseEvent bottomSheetCloseEvent) {
        this.bottomSheetCloseEvent = bottomSheetCloseEvent;
    }

    public interface ToPBottomSheetCloseEvent {
        void onClosed(Transaction tap, int option);
    }


    private void checkDays(long difference_In_Days, long difference_In_Hours, long difference_In_Minutes) {
        if (difference_In_Days == 0) {
            //Hours
            Log.d("TAG", "1111  difference_In_Days: " + difference_In_Days);
            checkHours(difference_In_Hours, difference_In_Minutes);
        } else if (difference_In_Days == 30) {

        } else {
            btnCancelPayment.setVisibility(View.GONE);
        }
    }

    private void checkHours(long difference_In_Hours, long difference_In_Minutes) {
        if (difference_In_Hours == 0) {
            //Minutes
            Log.d("TAG", "1111  difference_In_Hours: " + difference_In_Hours);
            checkMinutes(difference_In_Minutes);
        } else {
            btnCancelPayment.setVisibility(View.GONE);
        }
    }

    private void checkMinutes(long difference_In_Minutes) {
        if (difference_In_Minutes > 20) {
            Log.d("TAG", "1111  difference_In_Minutes: " + difference_In_Minutes);
            btnCancelPayment.setVisibility(View.GONE);
        } else {
            btnCancelPayment.setVisibility(View.VISIBLE);
            Log.d("TAG", "2222  difference_In_Minutes: " + difference_In_Minutes);
        }
    }

}
