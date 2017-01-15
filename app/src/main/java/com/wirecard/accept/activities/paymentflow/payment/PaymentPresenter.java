package com.wirecard.accept.activities.paymentflow.payment;

import android.content.Context;
import android.os.Bundle;

import com.wirecard.accept.exceptions.DeviceDiscoverException;
import com.wirecard.accept.help.CurrencyUtils;
import com.wirecard.accept.help.DiscoverDevices;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import de.wirecard.accept.sdk.model.PaymentItem;
import nucleus.presenter.RxPresenter;
import rx.android.schedulers.AndroidSchedulers;

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


    /**
     * second step: pay with discovered device
     *
     * @param device
     */
    public void proceedToPayment(final PaymentFlowController.Device device, String amount, PaymentFlowController.PaymentFlowDelegate delegate) {

        AcceptSDK.startPayment();
        Float tax;
        if (AcceptSDK.getPrefTaxArray().isEmpty())
            tax = 0f;
        else tax = AcceptSDK.getPrefTaxArray().get(0);
        AcceptSDK.addPaymentItem(new PaymentItem(1, "", new BigDecimal(amount), tax));
        final Currency amountCurrency = Currency.getInstance(AcceptSDK.getCurrency());
        final long amountUnits = AcceptSDK.getPaymentTotalAmount().scaleByPowerOfTen(amountCurrency.getDefaultFractionDigits()).longValue();
        view().observeOn(AndroidSchedulers.mainThread())
                .subscribe(view -> {
                    view.setAmount(CurrencyUtils.format(amountUnits, amountCurrency, Locale.getDefault()));
                });
        controller.startPaymentFlow(device, amountUnits, amountCurrency, delegate);
    }

}
