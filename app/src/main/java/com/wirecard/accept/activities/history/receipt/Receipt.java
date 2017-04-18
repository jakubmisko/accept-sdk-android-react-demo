package com.wirecard.accept.activities.history.receipt;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.wirecard.accept.help.MyStringBuilder;

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
    private Context context;
    private Payment payment;

    public Receipt(Context context, Payment payment) {
        this.context = context;
        this.payment = payment;
    }

    /**
     * presentation of sdk receipt building and data getting
     *
     */
    public void showReceipt() {
        ReceiptLayountBuilder builder = new ReceiptLayountBuilder(context);
        builder.receiptNumber(ReceiptBuilder.getReceiptNumber(payment))
                .merchantInfo(buildMerchantInfo())
                .time(new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault()).format(new Date(ReceiptBuilder.getTransactionDate(payment))))
                .items(payment.getPaymentItems(), payment.getCurrency(), TaxUtils.transactionTaxesInclusive(payment))
                .total(payment.getTotalAmount().toString(), CurrencyWrapper.getCurrencySymbol());
        if (payment.getStatus() != null) {
            builder.status(payment.getStatus().toString());
        }
        if (!TextUtils.isEmpty(payment.getCardNumber())) {
            builder.cardNumber(payment.getCardNumber());
        }
        if (!TextUtils.isEmpty(payment.getCardHolderFirstName()) && !TextUtils.isEmpty(payment.getCardHolderLastName())) {
            builder.cardHolder(payment.getCardHolderFirstName(), payment.getCardHolderLastName());
        }
        if (!TextUtils.isEmpty(payment.getCardType())) {
            if (payment.getCardType().equals("cash")) {
                builder.transactionType("CASH");
            } else {
                builder.transactionType("CARD");
                builder.cardType(payment.getCardType());
            }
        }
        if (!TextUtils.isEmpty(payment.getAuthorizationCode())) {
            builder.authorizationCode(payment.getAuthorizationCode());
        }
        if (!TextUtils.isEmpty(payment.getSignature())) {
            builder.signature(payment.getSignature());
        }
        View view = builder.build();


        new AlertDialog.Builder(context)
//        .setTitle("Customer Receipt")
//        .setNegativeButton(android.R.string.cancel, null)
                .setView(view)
                .show();
    }

    private static String buildMerchantInfo() {
        MyStringBuilder sb = new MyStringBuilder(new StringBuilder());
        sb.appendWithNextLine(ReceiptBuilder.getMerchantNameAndSurname())
                .appendWithNextLine(ReceiptBuilder.getMerchantAddressLine1())
                .appendWithNextLine(ReceiptBuilder.getMerchantAddressLine2())
                .append(ReceiptBuilder.getMerchantAddressCity())
                .append(" ")
                .appendWithNextLine(ReceiptBuilder.getMerchantAddressZipCode())
                .appendWithNextLine(ReceiptBuilder.getMerchantCountryCode());
        return sb.toString();
    }

}
