package com.wirecard.accept.presenters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wirecard.accept.R;
import com.wirecard.accept.activities.TransactionsHistoryActivity;
import com.wirecard.accept.async.ReverseOrRefund;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.help.MyStringBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.model.Payment;
import de.wirecard.accept.sdk.model.PaymentItem;
import de.wirecard.accept.sdk.util.CurrencyWrapper;
import de.wirecard.accept.sdk.util.ReceiptBuilder;
import de.wirecard.accept.sdk.util.TaxUtils;
import nucleus.presenter.Presenter;

/**
 * Created by jakub on 18.06.2016.
 */
public class TransactionHistioryPresenter extends Presenter<TransactionsHistoryActivity> {
    private ReverseOrRefund task;

    /**
     * initializes list view with transactions
     *
     * @param context
     */
    public void initView(Context context) {
        TransactionsHistoryActivity trxHistoryView = getView();
        if (trxHistoryView == null) {
            throw new RuntimeException("View not set for TransactionHistoryPresenter");
        }
        final String[] menu = new String[]{Constants.RECEIPT, Constants.REVERSE_REFUND};
        ListView listView = trxHistoryView.getListView();
        View loading = trxHistoryView.getLoading();
        PaymentAdapter pa = new PaymentAdapter(context);
        listView.setAdapter(pa);
        getPayments(context, trxHistoryView, listView, loading);
        //TODO butterknife?
        listView.setOnItemClickListener((parent, view, position, id) -> {
            final Payment payment = (Payment) listView.getAdapter().getItem(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            //just simple way how to display data into the list
            builder.setItems(menu, (dialog, which) -> {
                if (menu[which].equals(Constants.RECEIPT)) {
                    showReceipt(context, payment);
                } else if (menu[which].equals(Constants.REVERSE_REFUND)) {
                    task = new ReverseOrRefund(context, listView, loading);
                    task.execute(payment);
                }
            });
            builder.show();
        });
    }

    private void getPayments(Context context, TransactionsHistoryActivity trxHistoryView, ListView listView, View loading) {
        AcceptSDK.getPaymentsList(1, 100, null, null, (apiResult, result) -> {
            if (apiResult.isSuccess()) {
                if (result.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.toast_label_no_transactions), Toast.LENGTH_LONG).show();
                    trxHistoryView.finish();
                    return;
                }
                if (listView.getAdapter() != null) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                        ((ArrayAdapter<Payment>) listView.getAdapter()).addAll(result);
                    } else {
                        for (Payment p : result)
                            ((ArrayAdapter<Payment>) listView.getAdapter()).add(p);
                    }
                    loading.setVisibility(View.GONE);
                    return;
                }
            }
            presentFormError(context, apiResult.getDescription());
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
    }

    private void presentFormError(Context context, final String error) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_trx_load_err))
                .setMessage(error)
                .setPositiveButton("OK", (dialog, which) -> {
                    getView().finish();
                })
                .create()
                .show();
    }

    @Override
    protected void onDropView() {
        super.onDropView();
        if (task != null)
            task.cancel();
    }

    /**
     * presentation of sdk receipt building and data getting
     *
     * @param p
     */
    private void showReceipt(Context context, Payment p) {
        MyStringBuilder sb = new MyStringBuilder(new StringBuilder());
        sb.append("Receipt number ");
        sb.appendWithNextLine(ReceiptBuilder.getReceiptNumber(p));
        sb.appendWithNextLine(new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(new Date(ReceiptBuilder.getTransactionDate(p))));
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
        builder.setNegativeButton("Close", null);
        builder.setView(receiptView);
        builder.show();
    }

    private void appendMerchantInfo(MyStringBuilder sb) {
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

    private void appendPaymentItems(MyStringBuilder sb, Payment p) {
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

    private void appendPaymentDetails(MyStringBuilder sb, Payment p) {
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

    public class PaymentAdapter extends ArrayAdapter<Payment> {
        private Context context;

        PaymentAdapter(Context context) {
            super(context, R.layout.row_payment_history);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.row_payment_history, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Payment payment = getItem(position);
            //TODO resources
            viewHolder.title.setText(payment.getCardHolderFirstName() + " " + payment.getCardHolderLastName());
            viewHolder.amount.setText(payment.getTotalAmount().toString());
            viewHolder.status.setText(payment.getStatus().name());

            return convertView;
        }

        public class ViewHolder {
            @BindView(R.id.title)
            TextView title;
            @BindView(R.id.amount)
            TextView amount;
            @BindView(R.id.status)
            TextView status;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
