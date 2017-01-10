package com.wirecard.accept.exceptions;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;

/**
 * Created by super on 19.12.2016.
 */

public class DeviceDiscoverException extends RuntimeException {
    private PaymentFlowController.DiscoveryError discoveryError;

    public DeviceDiscoverException(String message, PaymentFlowController.DiscoveryError discoveryError) {
        super(message);
        this.discoveryError = discoveryError;
    }

    public PaymentFlowController.DiscoveryError getDiscoveryError() {
        return discoveryError;
    }
}
