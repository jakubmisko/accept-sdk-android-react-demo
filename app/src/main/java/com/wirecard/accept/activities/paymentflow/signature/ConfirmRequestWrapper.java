package com.wirecard.accept.activities.paymentflow.signature;

import android.support.annotation.Nullable;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;

/**
 * Created by super on 08.01.2017.
 */

public class ConfirmRequestWrapper {
    private PaymentFlowController.SignatureRequest signatureRequest;
    private PaymentFlowController.SignatureConfirmationRequest signatureConfirmationRequest;

    public ConfirmRequestWrapper(PaymentFlowController.SignatureRequest signatureRequest) {
        this.signatureRequest = signatureRequest;
    }

    public ConfirmRequestWrapper(PaymentFlowController.SignatureConfirmationRequest signatureConfirmationRequest) {
        this.signatureConfirmationRequest = signatureConfirmationRequest;
    }

    public void cancel(){
        if (signatureRequest != null){
            signatureRequest.signatureCanceled();
        } else {
            signatureConfirmationRequest.signatureRejected();
        }
    }

    public void confirm(@Nullable  byte[] signature){
        if (signatureRequest != null){
            signatureRequest.signatureEntered(signature);
        } else {
            signatureConfirmationRequest.signatureConfirmed();
        }
    }
}
