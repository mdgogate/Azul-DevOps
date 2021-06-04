package com.azul.app.android.activities.transactions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azul.app.android.R;
import com.azul.app.android.adapters.SettleTransactionAdapter;
import com.azul.app.android.classes.SettleTransaction;
import com.azul.app.android.globals.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SettledTransactionsQuery extends AppCompatActivity {
    ListView listTransactions;
    TextView tvNoRecordFound;

    SettleTransactionAdapter adapter;
    List<SettleTransaction> list;
    RelativeLayout topBarLayout;
    RelativeLayout layoutSearchByEdittext;
    EditText etSearchBy;
    ImageView clearEnteredText;

    TextView tvLotNo;
    TextView tvApprovalNo;
    TextView tvTerminalNo;
    TextView tvCardNo;
    boolean lotNoClick = true;
    boolean approvalNoClick = false;
    boolean cardNoClick = false;
    boolean terminalNoClick = false;
    List<SettleTransaction> tempList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settled_transactions_query);
        initControls();
    }

    private void initControls() {
        tvLotNo = findViewById(R.id.tvLotNo);
        tvApprovalNo = findViewById(R.id.tvApprovalNo);
        tvCardNo = findViewById(R.id.tvCardNo);
        tvTerminalNo = findViewById(R.id.tvTerminalNo);

        tvLotNo.setOnClickListener(tvLotNoView -> filterWithLotNo());
        tvApprovalNo.setOnClickListener(tvLotNoView -> filterWithApprovalNo());
        tvCardNo.setOnClickListener(tvLotNoView -> filterWithCardNo());
        tvTerminalNo.setOnClickListener(tvLotNoView -> filterWithTerminalNo());

        clearEnteredText = findViewById(R.id.clearEnteredText);
        etSearchBy = findViewById(R.id.etSearchBy);
        layoutSearchByEdittext = findViewById(R.id.layoutSearchByEdittext);
        topBarLayout = findViewById(R.id.topBarLayout);

        etSearchBy.setOnClickListener(etSearchByView ->
                showSearchBar()
        );

        etSearchBy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("TAG", "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Boolean.TRUE.equals(lotNoClick)) {
                    filterList(charSequence, 0);
                } else if (Boolean.TRUE.equals(approvalNoClick)) {
                    filterList(charSequence, 1);
                } else if (Boolean.TRUE.equals(cardNoClick)) {
                    filterList(charSequence, 2);
                } else if (Boolean.TRUE.equals(terminalNoClick)) {
                    filterList(charSequence, 3);
                } else {
                    Log.d("TAG", "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("TAG", "");

            }
        });
        layoutSearchByEdittext.setOnClickListener(layoutSearchView ->
                showSearchBar()
        );
        clearEnteredText.setOnClickListener(clearEnteredTextView ->
                hideSearchBar()
        );

        list = new ArrayList<>();
        listTransactions = findViewById(R.id.listTransactions);
        tvNoRecordFound = findViewById(R.id.tvNoRecordFound);

        parseLocalJson();
    }

    private void parseLocalJson() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());

            JSONObject jsonTransactionOb = obj.getJSONObject("settleTransactions");
            JSONArray jsonArray = jsonTransactionOb.getJSONArray("Transactions");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTrObject = jsonArray.getJSONObject(i);

                String cardNumber = jsonTrObject.getString("CardNumber");
                String cardName = jsonTrObject.getString("CardName");
                String approvalNo = jsonTrObject.getString("ApprovalNumber");
                String transactionDate = jsonTrObject.getString("TransactionDate");
                String amount = jsonTrObject.getString("Amount");
                String referenceNo = jsonTrObject.getString("ReferenceNo");
                String settleDaate = jsonTrObject.getString("SettleDaate");
                String terminalId = jsonTrObject.getString("TerminalId");
                String transactionType = jsonTrObject.getString("TransactionType");
                String lotNo = jsonTrObject.getString("lotNo");
                String cardType = jsonTrObject.getString("cardType");

                list.add(new SettleTransaction(cardNumber, cardType, transactionDate, cardName, approvalNo, amount, transactionType, terminalId,
                        lotNo, referenceNo, settleDaate));

            }

            adapter = new SettleTransactionAdapter(SettledTransactionsQuery.this, list);
            listTransactions.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        InputStream is = null;
        String json = null;
        try {
            is = getAssets().open("dummySettleTrnJson.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, Constants.UTF_FORMAT);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    private void filterList(CharSequence charSequence, int sType) {
        tempList = new ArrayList<>();
        for (SettleTransaction transaction : list) {
            if (sType == 0 && transaction.getLotNo().trim().contains(charSequence)) {
                tempList.add(transaction);
            } else if (sType == 1 && transaction.getApprovalNo().trim().contains(charSequence)) {
                tempList.add(transaction);
            } else if (sType == 2 && transaction.getCardNumber().trim().contains(charSequence)) {
                tempList.add(transaction);
            } else if (sType == 3 && transaction.getTerminalId().trim().contains(charSequence)) {
                tempList.add(transaction);
            } else {
                Log.d("TAG", "");
            }
        }
        if (!tempList.isEmpty()) {
            listTransactions.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
            adapter.updateList(tempList);
            adapter.notifyDataSetChanged();
        } else {
            listTransactions.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }
    }

    private void filterWithLotNo() {
        tvLotNo.setBackgroundResource(R.drawable.middle_navigation_bg);
        tvLotNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));

        tvApprovalNo.setBackgroundResource(0);
        tvCardNo.setBackgroundResource(0);
        tvTerminalNo.setBackgroundResource(0);
        tvApprovalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvCardNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvTerminalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));

        lotNoClick = true;
        approvalNoClick = false;
        cardNoClick = false;
        terminalNoClick = false;

        etSearchBy.setText("");
    }

    private void filterWithApprovalNo() {
        tvApprovalNo.setBackgroundResource(R.drawable.middle_navigation_bg);
        tvApprovalNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));

        tvLotNo.setBackgroundResource(0);
        tvCardNo.setBackgroundResource(0);
        tvTerminalNo.setBackgroundResource(0);

        tvLotNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvCardNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvTerminalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));

        lotNoClick = false;
        approvalNoClick = true;
        cardNoClick = false;
        terminalNoClick = false;

        etSearchBy.setText("");
    }

    private void filterWithCardNo() {
        tvCardNo.setBackgroundResource(R.drawable.middle_navigation_bg);
        tvCardNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));

        tvLotNo.setBackgroundResource(0);
        tvApprovalNo.setBackgroundResource(0);
        tvTerminalNo.setBackgroundResource(0);

        tvLotNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvApprovalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvTerminalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));

        lotNoClick = false;
        approvalNoClick = false;
        cardNoClick = true;
        terminalNoClick = false;

        etSearchBy.setText("");
    }

    private void filterWithTerminalNo() {
        tvTerminalNo.setBackgroundResource(R.drawable.middle_navigation_bg);
        tvTerminalNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));

        tvLotNo.setBackgroundResource(0);
        tvApprovalNo.setBackgroundResource(0);
        tvCardNo.setBackgroundResource(0);

        tvLotNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvApprovalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvCardNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));

        lotNoClick = false;
        approvalNoClick = false;
        cardNoClick = false;
        terminalNoClick = true;

        etSearchBy.setText("");
    }


    private void showSearchBar() {
        etSearchBy.setEnabled(true);
        if (topBarLayout.getVisibility() == View.GONE) {
            topBarLayout.setVisibility(View.VISIBLE);
        }
        if (clearEnteredText.getVisibility() == View.GONE) {
            clearEnteredText.setVisibility(View.VISIBLE);
        }
    }

    private void hideSearchBar() {
        etSearchBy.setEnabled(false);
        if (topBarLayout.getVisibility() == View.VISIBLE) {
            topBarLayout.setVisibility(View.GONE);
        }
        if (clearEnteredText.getVisibility() == View.VISIBLE) {
            clearEnteredText.setVisibility(View.GONE);
        }
        etSearchBy.setText("");
    }
}