package com.wirecard.accept.activities.paymentflow.payment;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;

/**
 * Created by super on 07.01.2017.
 */

public interface PaymentContract {
    void startPayment(final PaymentFlowController.Device device, String amount);
}
