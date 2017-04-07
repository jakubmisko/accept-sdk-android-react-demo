package com.wirecard.accept.activities.history;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.wirecard.accept.sdk.model.Payment;
import de.wirecard.accept.sdk.util.CurrencyWrapper;
import de.wirecard.accept.sdk.util.ReceiptBuilder;
import de.wirecard.accept.sdk.util.TaxUtils;

/**
 * Created by super on 18.12.2016.
 */

public class Receipt {
    /**
     * presentation of sdk receipt building and data getting
     *
     * @param p
     */
    public static void showReceipt(Context context, Payment p) {
        ReceiptLayountBuilder builder = new ReceiptLayountBuilder(context);
        builder.receiptNumber(ReceiptBuilder.getReceiptNumber(p))
                .time(new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault()).format(new Date(ReceiptBuilder.getTransactionDate(p))))
                .items(p.getPaymentItems(), p.getCurrency(), TaxUtils.transactionTaxesInclusive(p))
                .total(p.getTotalAmount().toString(), CurrencyWrapper.getCurrencySymbol());
        if (p.getStatus() != null) {
            builder.status(p.getStatus().toString());
        }
        if (!TextUtils.isEmpty(p.getCardNumber())) {
            builder.cardNumber(p.getCardNumber());
        }
        if (!TextUtils.isEmpty(p.getCardHolderLastName())) {
            builder.cardHolder(p.getCardHolderFirstName(), p.getCardHolderLastName());
        }
        if (!TextUtils.isEmpty(p.getCardType())) {
            if(p.getCardType().equals("cash")){
                builder.transactionType("CASH");
            } else {
                builder.transactionType("CARD");
                builder.cardType(p.getCardType());
            }
        }
        if (!TextUtils.isEmpty(p.getAuthorizationCode())) {
            builder.authorizationCode(p.getAuthorizationCode());
        }
        if (!TextUtils.isEmpty(p.getSignature())) {
            builder.signature(p.getSignature());
        }
        View view = builder.build();




        new AlertDialog.Builder(context)
        .setTitle("Customer Receipt")
        .setNegativeButton(android.R.string.cancel, null)
        .setView(view)
        .show();
    }

}
