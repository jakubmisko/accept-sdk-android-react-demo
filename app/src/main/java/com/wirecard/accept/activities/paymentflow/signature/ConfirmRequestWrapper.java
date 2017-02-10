package com.wirecard.accept.activities.paymentflow.signature;

import android.support.annotation.Nullable;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;

/**
 * wrap signature request and signature confirmation to one request for easier work with signature fragment
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

    /**
     * negative answer of request, user reject to sign or signature doesn't match with signature on card
     */
    public void cancel(){
        if (signatureRequest != null){
            signatureRequest.signatureCanceled();
        } else {
            signatureConfirmationRequest.signatureRejected();
        }
    }

    /**
     * positive answer of request, user signed to device screen or merchant accepted signature
     * @param signature if it's signature request than signature in byte array format is sent to back end
     */
    void confirm(@Nullable  byte[] signature){
        if (signatureRequest != null){
            signatureRequest.signatureEntered(signature);
        } else {
            signatureConfirmationRequest.signatureConfirmed();
        }
    }

    boolean requireSignature(){
        return signatureRequest != null;
    }
}
