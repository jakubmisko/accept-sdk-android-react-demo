package com.wirecard.accept.activities;

import de.wirecard.accept.extension.AcceptBbposPaymentFlowController;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;

/**
 * Created by jakub on 15.06.2016.
 */
public class PaymentFlowActivity extends AbstractPaymentFlowActivity {
    @Override
    PaymentFlowController createNewController() {
        return new AcceptBbposPaymentFlowController();
    }

    @Override
    boolean isSignatureConfirmationInApplication() {
        return true;
    }
}
