package com.wirecard.accept.activities.paymentflow.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.PaymentFlowSignatureView;
import com.wirecard.accept.rx.dialog.RxDialog;

import butterknife.BindView;
import rx.Subscription;

/**
 * Created by super on 30.11.2016.
 */

public class SignatureFragment extends Fragment {
    @BindView(R.id.signature)
    PaymentFlowSignatureView signatureView;

    Subscription confirmationDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.signature_fragment, container, false);
    }

    public void showSignatureConfirmRequest(){
//        if (signatureConfirmationDialog != null) {
//            return;
//        }
//        runOnUiThreadIfNotDestroyed(() -> {
//
//            signatureConfirmationDialog = PaymentFlowDialogs.showSignatureConfirmation(AbstractPaymentFlowActivity.this, signatureView.getSignatureBitmap(), isSignatureConfirmationInApplication(), new PaymentFlowDialogs
//                    .SignatureConfirmationListener() {
//                @Override
//                public void onSignatureConfirmedIsOK() {
//                    showProgress(R.string.acceptsdk_progress__follow, false);
//                    signatureConfirmationRequest.signatureConfirmed();
//                }
//
//                @Override
//                public void onSignatureConfirmedIsNotOK() {
//                    signatureConfirmationRequest.signatureRejected();
//                }
//            });
//        });
        RxDialog.
    }
}
