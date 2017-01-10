package com.wirecard.accept.activities.paymentflow;

import de.wirecard.accept.extension.refactor.AcceptThyronPaymentFlowController;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;

/**
 * Created by jakub on 16.06.2016.
 */
public class PaymentFlowActivity extends AbstractPaymentFlowActivity {
    @Override
    public PaymentFlowController createNewController() {
        // this is just feature because of supporting more terminals (flavours)
        return new AcceptThyronPaymentFlowController(false, false);
    }

    @Override
    public boolean isSignatureConfirmationInApplication() {
        return false;
    }
}
