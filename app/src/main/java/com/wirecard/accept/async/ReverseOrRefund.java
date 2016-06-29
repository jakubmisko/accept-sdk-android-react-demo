package com.wirecard.accept.async;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.presenters.TransactionHistioryPresenter;

import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.backend.AcceptBackendService;
import de.wirecard.accept.sdk.backend.AcceptTransaction;
import de.wirecard.accept.sdk.model.Payment;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.util.async.Async;

/**
 * Created by jakub on 24.06.2016.
 */
public class ReverseOrRefund {
    private Context context;
    private ListView listView;
    private Payment payment;
    private View loading;
    private Subscription task;

    public ReverseOrRefund(Context context, ListView listView, View loading) {
        this.context = context;
        this.listView = listView;
        this.loading = loading;
    }

    public void execute(Payment payment) {
        beforeExecute();
        this.payment = payment;
        task = Async.start((Func0<AcceptBackendService.Response>) () -> {
            if (payment.isReversible())
                return AcceptSDK.reverseTransaction(payment.getTransactionId());
            else if (payment.isRefundable())
                return AcceptSDK.refundTransaction(payment.getTransactionId());
            return null;
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(onSuccess());
    }

    private void beforeExecute() {
        loading.setVisibility(View.VISIBLE);
    }

    private void afterExecute() {
        loading.setVisibility(View.GONE);
    }

    private Action1<AcceptBackendService.Response> onSuccess() {
        return response -> {
            if (response != null) {
                if (response.hasError()) {
                    Toast.makeText(context, response.getError().toString(), Toast.LENGTH_LONG).show();
                } else {
                    AcceptTransaction body = (AcceptTransaction) response.getBody();
                    if (body.status == AcceptTransaction.Status.reversed) {
                        Toast.makeText(context, context.getString(R.string.reversed), Toast.LENGTH_LONG).show();
                        payment.setStatusToReversed();
                        ((TransactionHistioryPresenter.PaymentAdapter) listView.getAdapter()).notifyDataSetChanged();
                    } else if (body.status == AcceptTransaction.Status.refunded) {
                        Toast.makeText(context, context.getString(R.string.refunded), Toast.LENGTH_LONG).show();
                        payment.setStatusToRefunded();
                        ((TransactionHistioryPresenter.PaymentAdapter) listView.getAdapter()).notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, context.getString(R.string.reversed_or_refunded), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(context, context.getString(R.string.not_reversed_refunded), Toast.LENGTH_LONG).show();
            }
            afterExecute();
        };
    }

    public void cancel() {
        if (task != null && task.isUnsubscribed()) {
            task.unsubscribe();
            task = null;
        }
    }
}
