package com.wirecard.accept.activities.history;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wirecard.accept.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.model.Payment;
import de.wirecard.accept.sdk.model.PaymentItem;
import de.wirecard.accept.sdk.util.CurrencyWrapper;
import de.wirecard.accept.sdk.util.TaxUtils;

/**
 * Created by super on 06.04.2017.
 */

public class ReceiptLayountBuilder {
    private View view;
    private Context context;

    @BindView(R.id.card_number)
    TextView cardNumber;
    @BindView(R.id.cardholder_name)
    TextView cardholder;
    @BindView(R.id.card_type)
    TextView cardType;
    @BindView(R.id.approval_code)
    TextView authorizationCode;
    @BindView(R.id.merchant)
    TextView merchant;

    public ReceiptLayountBuilder(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.receipt, null);
        ButterKnife.bind(this, view);
    }
    public  ReceiptLayountBuilder merchantInfo(String merchantInfo){
        merchant.setText(merchantInfo);
        return this;
    }

    public ReceiptLayountBuilder receiptNumber(String number){
        TextView receiptNo = (TextView) view.findViewById(R.id.receipt_no);
        receiptNo.setText(number);
        return this;
    }

    public ReceiptLayountBuilder time(String time){
        TextView receiptNo = (TextView) view.findViewById(R.id.receipt_time);
        receiptNo.setText(time);
        return this;
    }

    public ReceiptLayountBuilder item(PaymentItem paymentItem, String currency, boolean taxIclusive){
        TableLayout items = (TableLayout) view.findViewById(R.id.items);
        items.addView(new ItemRow(context, paymentItem, currency, taxIclusive));
        return this;
    }

    public ReceiptLayountBuilder items(List<PaymentItem> paymentItems, String currency, boolean taxIclusive){
        TableLayout items = (TableLayout) view.findViewById(R.id.items);
        for(PaymentItem paymentItem : paymentItems){
            items.addView(new ItemRow(context, paymentItem, currency, taxIclusive));
        }
        return this;
    }

    public ReceiptLayountBuilder total(String total, String currency){
        TextView totalAmount = (TextView) view.findViewById(R.id.total);
        totalAmount.setText(total);
        TextView curr = (TextView) view.findViewById(R.id.currency);
        curr.setText(currency);
        return this;
    }

    public ReceiptLayountBuilder status(String status){
        TextView statusView = (TextView) view.findViewById(R.id.status);
        statusView.setText(status);
        return this;
    }

    public ReceiptLayountBuilder transactionType(String type){
        TextView trx = (TextView) view.findViewById(R.id.type);
        trx.setText(type);
        return this;
    }

    public ReceiptLayountBuilder signature(String signatureData){
        ImageView signature = (ImageView) view.findViewById(R.id.signature);
        Picasso.with(context).load(signatureData).into(signature);
        signature.setVisibility(View.VISIBLE);
        return this;
    }

    public  ReceiptLayountBuilder cardNumber(String cardNum){
        cardNumber.setText(cardNum);
        return this;
    }

    public ReceiptLayountBuilder cardHolder(String firstName, String lastName){
        cardholder.setText(firstName+" "+lastName);
        return this;
    }

    public ReceiptLayountBuilder cardType(String type){
        cardType.setText(type);
        return this;
    }

    public ReceiptLayountBuilder authorizationCode(String code){
        authorizationCode.setText(code);
        return this;
    }

    public View build(){
        return view;
    }
    private class ItemRow extends TableRow {

        public ItemRow(Context context) {
            super(context);
        }

        public ItemRow(Context context, PaymentItem paymentItem, String currency, boolean taxIsInclusive /*String desc, String quant, String unitPrice, String tax, String total*/) {
            super(context);
            TextView description = new TextView(context);
            description.setText(TextUtils.isEmpty(paymentItem.getNote()) ? "" : paymentItem.getNote());
            addView(description);
            TextView mult = new TextView(context);
            mult.setText("x");
            addView(mult);
            TextView quantity = new TextView(context);
            quantity.setText(paymentItem.getQuantity());
            addView(quantity);
            TextView unit = new TextView(context);
            unit.setText(CurrencyWrapper.setAmountFormat(paymentItem.getPrice(), currency));
            addView(unit);
            TextView taxPercent = new TextView(context);
            taxPercent.setText(TaxUtils.taxRateToString(Payment.SCALE, paymentItem.getTaxRate())+ "%");
            addView(taxPercent);
            TextView totalAmount = new TextView(context);
            if (taxIsInclusive) {
                totalAmount.setText(CurrencyWrapper.setAmountFormat(paymentItem.getTotalPrice(), currency));
            } else {
                totalAmount.setText(CurrencyWrapper.setAmountFormat(TaxUtils.getTotalItemAmount(paymentItem), currency));
            }
            addView(totalAmount);
        }
    }
}
