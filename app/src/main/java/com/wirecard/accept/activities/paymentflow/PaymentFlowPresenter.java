package com.wirecard.accept.activities.paymentflow;

import android.os.Bundle;

import java.math.BigDecimal;
import java.util.Currency;

import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import de.wirecard.accept.sdk.model.PaymentItem;
import nucleus.presenter.Presenter;

/**
 * Created by super on 07.01.2017.
 */
public class PaymentFlowPresenter extends Presenter<AbstractPaymentFlowActivity> {
    protected Boolean sepa = false;// used for sepa payment support
    Bundle sign = null;
    private PaymentFlowController controller;

    /**
     * second step: pay with discovered device
     *
     * @param device
     */
    public void proceedToPayment(final PaymentFlowController.Device device, String amount) {

        AcceptSDK.startPayment();
        Float tax;
        if (AcceptSDK.getPrefTaxArray().isEmpty())
            tax = 0f;
        else tax = AcceptSDK.getPrefTaxArray().get(0);
        AcceptSDK.addPaymentItem(new PaymentItem(1, "", new BigDecimal(amount), tax));
        final Currency amountCurrency = Currency.getInstance(AcceptSDK.getCurrency());
        final long amountUnits = AcceptSDK.getPaymentTotalAmount().scaleByPowerOfTen(amountCurrency.getDefaultFractionDigits()).longValue();
//        final TextView amountTextView = (TextView) findViewById(R.id.amount);
//        amountTextView.setText(CurrencyUtils.format(amountUnits, amountCurrency, Locale.getDefault()));
        controller.startPaymentFlow(device, amountUnits, amountCurrency, getView());
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

    }

    @Override
    protected void onTakeView(AbstractPaymentFlowActivity abstractPaymentFlowActivity) {
        super.onTakeView(abstractPaymentFlowActivity);
        controller = abstractPaymentFlowActivity.getPaymentFlowController();
    }


}
