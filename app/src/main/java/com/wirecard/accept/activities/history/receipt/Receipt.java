package com.wirecard.accept.activities.history.receipt;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.wirecard.accept.help.EnhancedStringBuilder;

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
    public AlertDialog showReceipt() {
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


        return new AlertDialog.Builder(context)
                .setView(view)
                .show();
    }

    private static String buildMerchantInfo() {
        EnhancedStringBuilder sb = new EnhancedStringBuilder(new StringBuilder());
        sb.appendWithNewLine(ReceiptBuilder.getMerchantNameAndSurname())
                .appendWithNewLine(ReceiptBuilder.getMerchantAddressLine1())
                .appendWithNewLine(ReceiptBuilder.getMerchantAddressLine2())
                .append(ReceiptBuilder.getMerchantAddressCity())
                .append(" ")
                .appendWithNewLine(ReceiptBuilder.getMerchantAddressZipCode())
                .appendWithNewLine(ReceiptBuilder.getMerchantCountryCode());
        return sb.toString();
    }

}
