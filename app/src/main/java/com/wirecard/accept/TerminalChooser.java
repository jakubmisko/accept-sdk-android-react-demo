package com.wirecard.accept;

import com.wirecard.accept.dialogs.TerminalChooserListener;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;

/**
 * Created by super on 24.11.2016.
 */

public class TerminalChooser implements TerminalChooserListener<PaymentFlowController.Device> {
    @Override
    public void onDeviceSelected(PaymentFlowController.Device device) {

    }

    @Override
    public void onSelectionCanceled() {

    }
}
