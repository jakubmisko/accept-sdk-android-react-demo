package com.wirecard.accept.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.wirecard.accept.R;

import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.model.Payment;

/**
 * Created by jakub on 18.06.2016.
 */
public class PaymentListFragment extends ListFragment {
    private ShowProgress update;

    public interface ShowProgress {
        public void setLoadingVisible(boolean visible);

        public void setPaymentSuccess(boolean paymentSuccess);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        AcceptSDK.getPaymentsList(1, 100, null, null, (apiResult, result) -> {
            if (apiResult.isSuccess()) {
                if (result.isEmpty()) {

                    Toast.makeText(getActivity(), getString(R.string.toast_label_no_transactions), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                    return;
                }
                //TODO check if working on 3.0
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
//                    // adapter add all
//                }else {
//                    for (Payment p : result)
//                        // adapter addadd
//                }
                //TODO why 0 as resource id?
                setListAdapter(new PaymentAdapter(getActivity(), 0, result));
                //update.setLoadingVisible(false);
                //update.setPaymentSuccess(true);
            }
            presentFormError(apiResult.getDescription());
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        update = (ShowProgress) context;
        super.onAttach(context);
    }

    private void presentFormError(final String error) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_trx_load_err))
                .setMessage(error)
                .setPositiveButton("OK", (dialog, which) -> {
                    getActivity().finish();
                })
                .create()
                .show();
    }
}
