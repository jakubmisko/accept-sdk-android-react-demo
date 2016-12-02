package com.wirecard.accept.activities.paymentflow;

import android.text.TextUtils;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.PaymentFlowSignatureView;
import com.wirecard.accept.dialogs.PaymentFlowDialogs;
import com.wirecard.accept.help.CurrencyUtils;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import de.wirecard.accept.sdk.model.PaymentItem;
import nucleus.presenter.Presenter;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by jakub on 16.06.2016.
 */
public class PaymentFlowPresenter extends Presenter<AbstractPaymentFlowActivity> {


     void handlePaymentInterrupted() {
        if (signatureConfirmationDialog != null) {
            signatureConfirmationDialog.dismiss();
        }
        paymentFlowController.cancelPaymentFlow();
    }

     void runOnUiThreadIfNotDestroyed(Action0 action/*final Runnable runnable*/) {
        if (!isDestroyed) {
            //TODO refactor
            Single.create(singleSubscriber -> {
                action.call();
                singleSubscriber.onSuccess("Done");
            }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
            //runOnUiThread(runnable);
        }
    }

    /**
     * first step discovery devices
     */
     void proceedToDevicesDiscovery() {
        showProgress(R.string.acceptsdk_progress__searching, true);
        paymentFlowController.discoverDevices(this, new PaymentFlowController.DiscoverDelegate() {

            @Override
            public void onDiscoveryError(final PaymentFlowController.DiscoveryError error, final String technicalMessage) {
                runOnUiThreadIfNotDestroyed(() -> {
                    showProgress(-1, false);
                    PaymentFlowDialogs.showTerminalDiscoveryError(AbstractPaymentFlowActivity.this, error, technicalMessage, v -> finish());
                });
            }

            @Override
            public void onDiscoveredDevices(final List<PaymentFlowController.Device> devices) {
                runOnUiThreadIfNotDestroyed(() -> {
                    showProgress(-1, false);
                    if (devices.isEmpty()) {
                        PaymentFlowDialogs.showNoDevicesError(AbstractPaymentFlowActivity.this, v -> finish());
                        return;
                    }
                    if (devices.size() == 1) {
                        proceedToPayment(devices.get(0));
                        return;
                    }
                    PaymentFlowDialogs.showTerminalChooser(AbstractPaymentFlowActivity.this, devices, device -> {
                        if (TextUtils.isEmpty(device.displayName)) {
                            return device.id;
                        }
                        return device.displayName;
                    }, new PaymentFlowDialogs.TerminalChooserListener<PaymentFlowController.Device>() {
                        @Override
                        public void onDeviceSelected(PaymentFlowController.Device device) {
                            proceedToPayment(device);
                        }

                        @Override
                        public void onSelectionCanceled() {
                            finish();
                        }
                    });
                });
            }
        });
    }

    /**
     * second step: pay with discovered device
     *
     * @param device
     */
    private void proceedToPayment(final PaymentFlowController.Device device) {
        signatureConfirmationDialog = null;
        final PaymentFlowSignatureView paymentFlowSignatureView = (PaymentFlowSignatureView) findViewById(R.id.signature);
        paymentFlowSignatureView.clear();
        showProgress(getString(R.string.acceptsdk_progress__connecting, device.displayName), true);
        enableButtons(-1);
        AcceptSDK.startPayment();
        Float tax;
        if (AcceptSDK.getPrefTaxArray().isEmpty())
            tax = 0f;
        else tax = AcceptSDK.getPrefTaxArray().get(0);
        AcceptSDK.addPaymentItem(new PaymentItem(1, "", new BigDecimal("10.0"), tax));

        final Currency amountCurrency = Currency.getInstance(AcceptSDK.getCurrency());
        final long amountUnits = AcceptSDK.getPaymentTotalAmount().scaleByPowerOfTen(amountCurrency.getDefaultFractionDigits()).longValue();


        String amount = CurrencyUtils.format(amountUnits, amountCurrency, Locale.getDefault());
        getView().setAmount(amount);
        paymentFlowController.startPaymentFlow(device, amountUnits, amountCurrency, this);
    }
}
