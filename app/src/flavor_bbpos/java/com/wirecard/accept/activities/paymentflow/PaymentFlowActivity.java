package com.wirecard.accept.activities.paymentflow;

import com.wirecard.accept.activities.paymentflow.AbstractPaymentFlowActivity;

import de.wirecard.accept.extension.AcceptBbposPaymentFlowController;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;

/**
 * Created by jakub on 15.06.2016.
 */
public class PaymentFlowActivity extends AbstractPaymentFlowActivity {
    @Override
    public PaymentFlowController createNewController() {
        return new AcceptBbposPaymentFlowController();
    }

    @Override
    public boolean isSignatureConfirmationInApplication() {
        return true;
    }
}
