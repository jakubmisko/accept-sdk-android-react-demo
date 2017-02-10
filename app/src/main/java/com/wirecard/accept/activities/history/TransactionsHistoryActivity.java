/**
 * Copyright (c) 2015 Wirecard. All rights reserved.
 * <p>
 * Accept SDK for Android
 */
package com.wirecard.accept.activities.history;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseActivity;
import com.wirecard.accept.help.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.backend.AcceptBackendService;
import de.wirecard.accept.sdk.backend.AcceptTransaction;
import de.wirecard.accept.sdk.model.Payment;
import nucleus.factory.RequiresPresenter;

@RequiresPresenter(TransactionHistioryPresenter.class)
public class TransactionsHistoryActivity extends BaseActivity<TransactionHistioryPresenter> {
    private String TAG = getClass().getSimpleName();
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.loading)
    View loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        final String[] menu = new String[]{Constants.RECEIPT, Constants.REVERSE_REFUND};
        PaymentAdapter pa = new PaymentAdapter(this);
        listView.setAdapter(pa);
//        getPayments(context, trxHistoryView, listView, loading);
        getPresenter().loadPayments(1, 100, null, null);
        //TODO rxAlert?
        listView.setOnItemClickListener((parent, view, position, id) -> {
            final Payment payment = (Payment) listView.getAdapter().getItem(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //just simple way how to display data into the list
            builder.setItems(menu, (dialog, which) -> {
                if (menu[which].equals(Constants.RECEIPT)) {
                    Receipt.showReceipt(this, payment);
                } else if (menu[which].equals(Constants.REVERSE_REFUND)) {
                    loading.setVisibility(View.VISIBLE);
                    getPresenter().reverseOrRefund(payment);
                }
            });
            builder.show();
        });
    }

    public void fillListView(List<Payment> payments) {
        Log.d(TAG, "fillListView");
        if (listView.getAdapter() != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                ((ArrayAdapter<Payment>) listView.getAdapter()).addAll(payments);
            } else {
                for (Payment p : payments)
                    ((ArrayAdapter<Payment>) listView.getAdapter()).add(p);
            }
            loading.setVisibility(View.GONE);
        }
    }

    //TODO rxAlert?
    public void paymentsLoadingError(final Throwable error) {
        new AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.dialog_trx_load_err))
                .setMessage(error.getMessage())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> finish())
                .create()
                .show();
    }

    public void notifyReverseRefund(AcceptBackendService.Response response) {
        if (response.hasError()) {
            Toast.makeText(this, response.getError().toString(), Toast.LENGTH_LONG).show();
        } else {
            AcceptTransaction body = (AcceptTransaction) response.getBody();
            if (body.status == AcceptTransaction.Status.reversed) {
                Toast.makeText(this, R.string.reversed, Toast.LENGTH_LONG).show();
                ((PaymentAdapter) listView.getAdapter()).notifyDataSetChanged();
            } else if (body.status == AcceptTransaction.Status.refunded) {
                Toast.makeText(this, R.string.refunded, Toast.LENGTH_LONG).show();;
                ((PaymentAdapter) listView.getAdapter()).notifyDataSetChanged();
            } else {
                Toast.makeText(this, R.string.reversed_or_refunded, Toast.LENGTH_LONG).show();
            }
        }
        loading.setVisibility(View.GONE);
    }

    public void unableToReverseRefund() {
        Toast.makeText(this, R.string.not_reversed_refunded, Toast.LENGTH_LONG).show();
        loading.setVisibility(View.GONE);
    }
}
