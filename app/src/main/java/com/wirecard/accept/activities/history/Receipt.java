package com.wirecard.accept.activities.history;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wirecard.accept.R;
import com.wirecard.accept.help.MyStringBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.wirecard.accept.sdk.model.Payment;
import de.wirecard.accept.sdk.model.PaymentItem;
import de.wirecard.accept.sdk.util.CurrencyWrapper;
import de.wirecard.accept.sdk.util.ReceiptBuilder;
import de.wirecard.accept.sdk.util.TaxUtils;

/**
 * Created by super on 18.12.2016.
 */
//TODO  strings to resources
public class Receipt {
    /**
     * presentation of sdk receipt building and data getting
     *
     * @param p
     */
    public static void showReceipt(Context context, Payment p) {
        MyStringBuilder sb = new MyStringBuilder(new StringBuilder());
        sb.append("Receipt number ");
        sb.appendWithNextLine(ReceiptBuilder.getReceiptNumber(p));
        sb.appendWithNextLine(new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault()).format(new Date(ReceiptBuilder.getTransactionDate(p))));
        sb.append('\n');
        appendMerchantInfo(sb);
        sb.append('\n');
        sb.appendWithNextLine("Payment items:");
        appendPaymentItems(sb, p);
        sb.append('\n');
        sb.append("Total: \t\t");
        sb.appendWithNextLine(CurrencyWrapper.setAmountFormat(p.getTotalAmount(), p.getCurrency()));
        sb.append('\n');
        sb.appendWithNextLine("Payment details:");
        appendPaymentDetails(sb, p);
        sb.append('\n');
        sb.appendWithNextLine("Payment issued by accept by Wirecard");


        FrameLayout receiptView = (FrameLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_receipt, null);
        TextView receiptTextView = (TextView) receiptView.findViewById(R.id.receipt);
        receiptTextView.setText(sb.toString());

        if (!TextUtils.isEmpty(p.getSignature())) {
            ImageView signature = (ImageView) receiptView.findViewById(R.id.signature);
            Picasso.with(context).load(p.getSignature()).into(signature);
        } else {
            receiptView.findViewById(R.id.signatureText).setVisibility(View.GONE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Customer Receipt");
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setView(receiptView);
        builder.show();
    }

    private static void appendMerchantInfo(MyStringBuilder sb) {
        final String name = ReceiptBuilder.getMerchantNameAndSurname();
        final String address1 = ReceiptBuilder.getMerchantAddressLine1();
        final String address2 = ReceiptBuilder.getMerchantAddressLine2();
        final String city = ReceiptBuilder.getMerchantAddressCity();
        final String zip = ReceiptBuilder.getMerchantAddressZipCode();
        final String countryCode = ReceiptBuilder.getMerchantCountryCode();
        sb.appendWithNextLine(name);
        sb.appendWithNextLine(address1);
        sb.appendWithNextLine(address2);

        sb.appendTwoStringsWithNextLine(city, zip);

        sb.appendWithNextLine(countryCode);
    }

    private static void appendPaymentItems(MyStringBuilder sb, Payment p) {
        boolean taxIsInclusive = TaxUtils.transactionTaxesInclusive(p);
        final List<PaymentItem> items = ReceiptBuilder.getTransactionItems(p);
        for (PaymentItem pi : items) {
            final String desc = (TextUtils.isEmpty(pi.getNote()) ? "No description" : pi.getNote());
            final String tax = TaxUtils.taxRateToString(Payment.SCALE, pi.getTaxRate()) + "%";
            final String price = CurrencyWrapper.setAmountFormat(pi.getPrice(), p.getCurrency());
            final String totalAmount;
            if (taxIsInclusive) {
                totalAmount = CurrencyWrapper.setAmountFormat(pi.getTotalPrice(), p.getCurrency());
            } else {
                totalAmount = CurrencyWrapper.setAmountFormat(TaxUtils.getTotalItemAmount(pi), p.getCurrency());
            }
            sb.appendWithNextLine(desc);
            sb.append(pi.getQuantity() + " * ");
            sb.append(price);
            sb.append("\t\t");
            sb.append(tax);
            sb.append("\t\t");
            sb.appendWithNextLine(totalAmount);
        }
    }

    private static void appendPaymentDetails(MyStringBuilder sb, Payment p) {
        if (p.getStatus() != null) {
            sb.append("Transaction status: \t\t");
            sb.appendWithNextLine(p.getStatus().toString());
        }
        if (!TextUtils.isEmpty(p.getCardNumber())) {
            sb.append("Card number: \t\t");
            sb.appendWithNextLine(p.getCardNumber());
        }
        if (!TextUtils.isEmpty(p.getCardHolderLastName())) {
            sb.append("Cardholder name: \t\t");
            sb.append(p.getCardHolderFirstName());
            sb.append(" ");
            sb.appendWithNextLine(p.getCardHolderLastName());
        }
        if (!TextUtils.isEmpty(p.getCardType())) {
            sb.append("Card Type: \t\t");
            sb.appendWithNextLine(p.getCardType());
        }
        if (!TextUtils.isEmpty(p.getAuthorizationCode())) {
            sb.append("Approval Code: \t\t");
            sb.appendWithNextLine(p.getAuthorizationCode());
        }
    }
}
