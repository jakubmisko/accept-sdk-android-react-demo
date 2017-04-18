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
import rx.Single;
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

    String buildAmountWithCurrency(String amount) {
        final Currency currency = Currency.getInstance(AcceptSDK.getCurrency());
        return CurrencyUtils.format(amount, currency, Locale.getDefault());
    }

    /**
     * second step: pay with discovered device
     *
     * @param device
     * @param amount
     * @param delegate
     */
    public void proceedToCardPayment(final PaymentFlowController.Device device, String amount, PaymentFlowController.PaymentFlowDelegate delegate) {
        AcceptSDK.startPayment();
        Float tax;
        if (AcceptSDK.getPrefTaxArray().isEmpty())
            tax = 0f;
        else tax = AcceptSDK.getPrefTaxArray().get(0);
        AcceptSDK.addPaymentItem(new PaymentItem(1, "description", new BigDecimal(amount), tax));
        final Currency currency = Currency.getInstance(AcceptSDK.getCurrency());
        final long amountUnits = AcceptSDK.getPaymentTotalAmount().scaleByPowerOfTen(currency.getDefaultFractionDigits()).longValue();
        controller.startPaymentFlow(device, amountUnits, Currency.getInstance(AcceptSDK.getCurrency()), delegate);
    }

    void payByCash(String amount) {
        AcceptSDK.startPayment();
        Float tax;
        if (AcceptSDK.getPrefTaxArray().isEmpty())
            tax = 0f;
        else tax = AcceptSDK.getPrefTaxArray().get(0);
        AcceptSDK.addPaymentItem(new PaymentItem(1, "description", new BigDecimal(amount), tax));
        AcceptSDK.setPaymentTransactionType(AcceptSDK.TransactionType.CASH_PAYMENT);
        Single.create(subscriber -> AcceptSDK.postCashPayment((apiResult, payment) -> {
            if (apiResult.isSuccess()) {
                subscriber.onSuccess(apiResult);
            } else {
                subscriber.onError(new Throwable(apiResult.getMessage()));
            }
        }))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    result -> view().subscribe(PaymentFragment::successfulPayment),
                    error -> view().subscribe(paymentFragment -> paymentFragment.showProgress(error.getMessage(), false))
            );
    }
}
