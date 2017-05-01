/**
 * Copyright (c) 2015 Wirecard. All rights reserved.
 * <p>
 * Accept SDK for Android
 */
package com.wirecard.accept.activities.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseFragment;
import com.wirecard.accept.activities.history.receipt.Receipt;
import com.wirecard.accept.help.RxHelper;
import com.wirecard.accept.rx.dialog.RxAlertDialog;
import com.wirecard.accept.rx.dialog.RxProgressDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.backend.AcceptBackendService;
import de.wirecard.accept.sdk.backend.AcceptTransaction;
import de.wirecard.accept.sdk.model.Payment;
import nucleus.factory.RequiresPresenter;
import rx.Subscription;

@RequiresPresenter(TransactionHistioryPresenter.class)
public class TransactionsHistoryFragment extends BaseFragment<TransactionHistioryPresenter> implements SwipeRefreshLayout.OnRefreshListener{
    private String TAG = getClass().getSimpleName();

    @BindView(R.id.rvList)
    RecyclerView recyclerView;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout container;
    private Subscription progressDialog, loadingError;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        showProgress("Loading payments...");
        getPresenter().loadPayments(1, 100, null, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    public void fillList(List<Payment> payments) {
        Log.d(TAG, "fillList");
        if(recyclerView.getAdapter() == null) {
            PaymentRecyclerAdapter recyclerAdapter = new PaymentRecyclerAdapter(payments, (position, action) -> {
                Payment p = payments.get(position);
                switch (action) {
                    case "Receipt":
                        new Receipt(getActivity(), p).showReceipt();
                        break;
                    case "Reverse":
                    case "Refund":
                        showProgress(action);
                        getPresenter().reverseOrRefund(p);
                        break;
                    default:
                        Snackbar.make(container, action + "is not implemented", Snackbar.LENGTH_SHORT).show();
                }

            });
            container.setOnRefreshListener(this);
            recyclerView.setAdapter(recyclerAdapter);
        } else {
            ((PaymentRecyclerAdapter)recyclerView.getAdapter()).setPayments(payments);
        }
        recyclerView.getAdapter().notifyDataSetChanged();
        dissmisProgress();
    }

    private void showProgress(String message) {
        progressDialog = RxProgressDialog.create(getActivity(), message)
                .subscribe();
    }

    private void dissmisProgress() {
        RxHelper.unsubscribe(progressDialog);
        container.setRefreshing(false);
    }

    public void paymentsLoadingError(final Throwable error) {
        loadingError = RxAlertDialog.create(getActivity(), this.getString(R.string.dialog_trx_load_err), error.getMessage())
                .subscribe(aVoid -> getActivity().finish(),
                        throwable -> Log.e(TAG, throwable.getMessage()));
    }

    public void notifyReverseRefund(AcceptBackendService.Response response) {
        if (response.hasError()) {
            Snackbar.make(container, response.getError().toString(), Snackbar.LENGTH_SHORT).show();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
            AcceptTransaction body = (AcceptTransaction) response.getBody();
            if (body.status == AcceptTransaction.Status.reversed) {
                Snackbar.make(container, R.string.reversed, Snackbar.LENGTH_SHORT).show();
            } else if (body.status == AcceptTransaction.Status.refunded) {
                Snackbar.make(container, R.string.refunded, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(container, R.string.reversed_or_refunded, Snackbar.LENGTH_SHORT).show();
            }
        }
        dissmisProgress();
    }

    public void unableToReverseRefund() {
        Snackbar.make(container, R.string.not_reversed_refunded, Snackbar.LENGTH_SHORT).show();
        dissmisProgress();
    }

    @Override
    public void onRefresh() {
        ((PaymentRecyclerAdapter)recyclerView.getAdapter()).clear();
        getPresenter().loadPayments(1, 100, null, null);
    }

    public interface HistoryPopupMenuCallback {
        void onItemSelected(int position, String action);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxHelper.unsubscribe(loadingError);
        dissmisProgress();
    }
}
