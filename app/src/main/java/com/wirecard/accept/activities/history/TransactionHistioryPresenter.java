package com.wirecard.accept.activities.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.backend.AcceptBackendService;
import de.wirecard.accept.sdk.model.Payment;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jakub on 18.06.2016.
 */
public class TransactionHistioryPresenter extends RxPresenter<TransactionsHistoryFragment> {
    private String TAG = getClass().getSimpleName();

    private final int LOAD_PAYMENTS = 0;
    private final int REVERSE_REFUND = 1;
    //load payments parameters
    private int pageNum;
    private int pageSize;
    private Long since;
    private Long until;
    //reverse or refund parameter
    private Payment payment;

    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        restartableLatestCache(LOAD_PAYMENTS,
                //do request for payments on background
                () -> Observable.create((Observable.OnSubscribe<List<Payment>>) subscriber ->
                                AcceptSDK.getPaymentsList(pageNum, pageSize, null, null, (apiResult, result) -> {
                                    if (apiResult.isSuccess()) {
                                        subscriber.onNext(result);
                                        subscriber.onCompleted();
                                    } else {
                                        //wrap problem to throwable
                                        subscriber.onError(new Throwable(apiResult.getDescription()));
                                    }
                                })
                        //do network request on non ui thread
                )
                        .subscribeOn(Schedulers.io())
                        //handle data presentation on main ui thread
                        .observeOn(AndroidSchedulers.mainThread()),
                //present data
                TransactionsHistoryFragment::fillListView,
                //present error
                TransactionsHistoryFragment::paymentsLoadingError

        );
        //do reverse/refund request for transaction
        restartableFirst(REVERSE_REFUND,
                () -> Observable.create((Observable.OnSubscribe<AcceptBackendService.Response>) subscriber -> {
                    if (payment.isReversible()) {
                        payment.setStatusToReversed();
                        subscriber.onNext(AcceptSDK.reverseTransaction(payment.getTransactionId()));
                        subscriber.onCompleted();
                    } else if (payment.isRefundable()) {
                        payment.setStatusToRefunded();
                        subscriber.onNext(AcceptSDK.refundTransaction(payment.getTransactionId()));
                        subscriber.onCompleted();
                    } else {
                        //not much to put here just indicate that payment can not be reversed/refunded
                        subscriber.onError(new Throwable());
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()),
                //present successful action
                TransactionsHistoryFragment::notifyReverseRefund,
                //present that action could not be performed
                (transactionsHistoryActivity, throwable) -> transactionsHistoryActivity.unableToReverseRefund()
        );
    }


    public void reverseOrRefund(Payment payment) {
        Log.d(TAG, "reverseOrRefund: amount " + payment.getTotalAmount());
        this.payment = payment;
        //start restartable for reverse/refund
        start(REVERSE_REFUND);
    }

    public void loadPayments(int pageNum, int pageSize, Long since, Long until) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.since = since;
        this.until = until;
        Log.d(TAG, "loadPayments");
        //start restartable for loading payments
        start(LOAD_PAYMENTS);
    }
}
