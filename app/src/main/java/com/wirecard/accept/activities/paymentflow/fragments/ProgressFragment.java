package com.wirecard.accept.activities.paymentflow.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.rx.dialog.RxDialog;

import butterknife.BindView;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import rx.Subscription;

/**
 * Created by super on 30.11.2016.
 */

public class ProgressFragment extends Fragment {
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.status)
    TextView status;

    private Subscription noDevicesError;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.progress_fragment, container, false);
    }

    public void showProgress(final int messageRes, final boolean showProgress) {
        showProgress(messageRes == -1 ? "" : getString(messageRes), showProgress);
    }

    public void showProgress(final String message, final boolean showProgress) {
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
        status.setText(message);
    }

    public void showNoDeviceError() {
        noDevicesError = RxDialog.create(getActivity(), R.string.acceptsdk_dialog_no_terminals_title, R.string.acceptsdk_dialog_no_terminals_message)
                .subscribe(click -> {
                    getActivity().finish();
                });

    }

    public void showTerminalChooser(CharSequence[] terminals){
        RxDialog.create(getActivity(), R.string.acceptsdk_dialog_terminal_chooser_title, android.R.string.cancel, terminals)
                .subscribe(terminal -> {
                    //TODO presenter proceed to paymeny
                }, error -> {
                    //TODO exception that wont be thrown?
                }, () ->{
                    //cancel button pressed
                    getActivity().finish();
                });
    }

    public void showPaymentFlowError(final String message){
        //Todo one button dialog
//        RxDialog.create(getActivity(), R.string.acceptsdk_dialog_payment_error_title, message)
        status.setText(R.string.acceptsdk_progress__declined);
    }

    public void showPaymentFlowSuccess(){
        status.setText(R.string.acceptsdk_progress__sucesful);
        Toast.makeText(getActivity(), "Payment successful !", Toast.LENGTH_LONG).show();
        //TODO finish here on in activity?
        //finish();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(noDevicesError != null && noDevicesError.isUnsubscribed()){
            noDevicesError.unsubscribe();
            noDevicesError = null;
        }

    }
}
