package com.wirecard.accept.exceptions;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;

/**
 * exception thrown when there's problem with device discovery, wrapping discorery error
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
