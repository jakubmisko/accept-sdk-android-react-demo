package com.wirecard.accept.activities.paymentflow.payment;

import android.content.Context;
import android.os.Bundle;

import com.wirecard.accept.exceptions.DeviceDiscoverException;
import com.wirecard.accept.help.DiscoverDevices;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import nucleus.presenter.RxPresenter;

/**
 * Created by super on 07.01.2017.
 */
public class PaymentPresenter extends RxPresenter<PaymentFragment> {
    private final int DISCOVER_DEVICES = 0;

    private Context context;
    private PaymentFlowController controller;

    @Override
    protected void onTakeView(PaymentFragment paymentFragment) {
        super.onTakeView(paymentFragment);

    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        restartableLatestCache(DISCOVER_DEVICES,
                () -> DiscoverDevices.devices(context, controller),
                PaymentFragment::terminalChooser,
                (paymentFragment, throwable) -> paymentFragment.showTerminalDiscoveryError((DeviceDiscoverException) throwable)
        );
    }

    public void startDeviceDiscovery(Context context, PaymentFlowController controller) {
        this.context = context;
        this.controller = controller;
        start(DISCOVER_DEVICES);
    }


}
