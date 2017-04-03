/**
 * Copyright (c) 2015 Wirecard. All rights reserved.
 * <p>
 * Accept SDK for Android
 */
package com.wirecard.accept.activities.history;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.backend.AcceptBackendService;
import de.wirecard.accept.sdk.backend.AcceptTransaction;
import de.wirecard.accept.sdk.model.Payment;
import nucleus.factory.RequiresPresenter;

//TODO filter?
@RequiresPresenter(TransactionHistioryPresenter.class)
public class TransactionsHistoryFragment extends BaseFragment<TransactionHistioryPresenter> {
    private String TAG = getClass().getSimpleName();

//    @BindView(R.id.loading)
//    View loading;

    //    @BindView(R.id.list)
//    ListView listView;
    @BindView(R.id.rvList)
    RecyclerView recyclerView;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout container;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history_material, container, false);
        ButterKnife.bind(this, view);
        showProgress("Loading payments...");
        getPresenter().loadPayments(1, 100, null, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_history);
//        setContentView(R.layout.activity_history_material);
//        ButterKnife.bind(this);
//        showProgress("Loading payments...");
//        getPresenter().loadPayments(1, 100, null, null);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //listview + array adapter
//        final String[] menu = new String[]{Constants.RECEIPT, Constants.REVERSE_REFUND};
//        PaymentArrayAdapter pa = new PaymentArrayAdapter(this);
//        listView.setAdapter(pa);
////        getPayments(context, trxHistoryView, listView, loading);
//        getPresenter().loadPayments(1, 100, null, null);
//        //TODO rxAlert?
//        listView.setOnItemClickListener((parent, view, position, id) -> {
//            final Payment payment = (Payment) listView.getAdapter().getItem(position);
//            //TODO simple menu instead alert dialog
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            //just simple way how to display data into the list
//            builder.setItems(menu, (dialog, which) -> {
//                if (menu[which].equals(Constants.RECEIPT)) {
//                    Receipt.showReceipt(this, payment);
//                } else if (menu[which].equals(Constants.REVERSE_REFUND)) {
//                    loading.setVisibility(View.VISIBLE);
//                    getPresenter().reverseOrRefund(payment);
//                }
//            });
//            builder.show();
//        });
//    }

//    private void setOnTouchListeners(){
//        recyclerView.setOnTouchListener(new RecyclerItemC);
//    }

    public void fillListView(List<Payment> payments) {
        Log.d(TAG, "fillListView");
//        if (listView.getAdapter() != null) {
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
//                ((ArrayAdapter<Payment>) listView.getAdapter()).addAll(payments);
//            } else {
//                for (Payment p : payments)
//                    ((ArrayAdapter<Payment>) listView.getAdapter()).add(p);
//            }
////            loading.setVisibility(View.GONE);
//        }
        PaymentRecyclerAdapter recyclerAdapter = new PaymentRecyclerAdapter(payments, (position, action) -> {
            Payment p = payments.get(position);
            switch (action) {
                case "Receipt":
                    Receipt.showReceipt(getActivity(), p);
                    break;
                case "Reverse":
                case "Refund":
                    showProgress(action);
                    getPresenter().reverseOrRefund(p);
                    break;
                default:
                    //Toast.makeText(TransactionsHistoryFragment.this, action + "is not implemented", Toast.LENGTH_SHORT).show();
                    Snackbar.make(container, action + "is not implemented", Snackbar.LENGTH_SHORT).show();
            }

        });
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
        dissmisProgress();
    }

    private void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    private void dissmisProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    //TODO rxAlert?
    public void paymentsLoadingError(final Throwable error) {
        new AlertDialog.Builder(getActivity())
                .setTitle(this.getString(R.string.dialog_trx_load_err))
                .setMessage(error.getMessage())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> getActivity().finish())
                .create()
                .show();
    }

    public void notifyReverseRefund(AcceptBackendService.Response response) {
        if (response.hasError()) {
//            Toast.makeText(this, response.getError().toString(), Toast.LENGTH_LONG).show();
            Snackbar.make(container, response.getError().toString(), Snackbar.LENGTH_SHORT).show();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
            //                ((PaymentArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
            AcceptTransaction body = (AcceptTransaction) response.getBody();
            if (body.status == AcceptTransaction.Status.reversed) {
//                Toast.makeText(this, R.string.reversed, Toast.LENGTH_LONG).show();
                Snackbar.make(container, R.string.reversed, Snackbar.LENGTH_SHORT).show();
            } else if (body.status == AcceptTransaction.Status.refunded) {
//                Toast.makeText(this, R.string.refunded, Toast.LENGTH_LONG).show();
                Snackbar.make(container, R.string.refunded, Snackbar.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, R.string.reversed_or_refunded, Toast.LENGTH_LONG).show();
                Snackbar.make(container, R.string.reversed_or_refunded, Snackbar.LENGTH_SHORT).show();
            }
        }
//        loading.setVisibility(View.GONE);
        dissmisProgress();
    }

    public void unableToReverseRefund() {
//        Toast.makeText(this, R.string.not_reversed_refunded, Toast.LENGTH_LONG).show();
//        loading.setVisibility(View.GONE);
        Snackbar.make(container, R.string.not_reversed_refunded, Snackbar.LENGTH_SHORT).show();
        dissmisProgress();
    }

    public interface HistoryPopupMenuCallback {
        void onItemSelected(int position, String action);
    }
}
