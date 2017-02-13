/**
 * Copyright (c) 2015 Wirecard. All rights reserved.
 * <p>
 * Accept SDK for Android
 */
package com.wirecard.accept.activities.paymentflow;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseActivity;
import com.wirecard.accept.activities.paymentflow.payment.PaymentFragment;
import com.wirecard.accept.activities.paymentflow.signature.ConfirmRequestWrapper;
import com.wirecard.accept.activities.paymentflow.signature.SignatureFragment;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.help.RxHelper;
import com.wirecard.accept.rx.receivers.RxBroadcastReceiver;

import butterknife.BindView;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import de.wirecard.accept.sdk.model.Payment;
import rx.Subscription;

/**
 * Basic payment flow controlling activity
 */
public abstract class AbstractPaymentFlowActivity extends BaseActivity implements PaymentFlowController.PaymentFlowDelegate {
    //container for fragments
    @BindView(R.id.container)
    View content;
    private Subscription receiver;
    protected PaymentFlowController paymentFlowController;
    private PaymentFragment paymentFragment;
    private SignatureFragment signatureFragment;
    private String TAG = getClass().getSimpleName();


//    public PaymentFlowController getPaymentFlowController() {
//        return paymentFlowController;
//    }

    public void showPaymentFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (paymentFragment != null && paymentFragment.isAdded()) {
            transaction.show(paymentFragment);
        } else {
           //pass arguments from amount activity to payment fragment
            paymentFragment = PaymentFragment.newInstance(getIntent().getExtras());
            transaction.add(R.id.container, paymentFragment, Constants.PAYMENT_FRAGMENT_TAG);
        }
        if (signatureFragment != null && signatureFragment.isAdded()) {
            transaction.hide(signatureFragment);
        }
        transaction.commit();
    }

    public void showSignatureFragment(ConfirmRequestWrapper wrapper) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (signatureFragment != null && signatureFragment.isAdded()) {
            transaction.show(signatureFragment);
            signatureFragment.setSignatureRequest(wrapper);
        } else {
            signatureFragment = SignatureFragment.newInstance(wrapper);
            transaction.add(R.id.container, signatureFragment, Constants.SINGATURE_FRAGMENT_TAG);
        }
        if (paymentFragment != null && paymentFragment.isAdded()) {
            transaction.hide(paymentFragment);
        }
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        paymentFlowController = createNewController();
        if (paymentFlowController == null)
            throw new IllegalArgumentException("You have to implement createNewController()");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        showPaymentFragment();
        Log.d(TAG, "onCreate: show payment fragment");
        receiver = RxBroadcastReceiver.create(this, new IntentFilter(Intent.ACTION_SCREEN_OFF))
                .subscribe(intent -> paymentFragment.showPaymentResult(false));
//        isDestroyed = false;
    }


    abstract PaymentFlowController createNewController();

    abstract boolean isSignatureConfirmationInApplication();

    private boolean isDestroyed = false; // To support Android 4.2, 4.2.2 ( < API 17 ).

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
//        handlePaymentInterrupted();
        RxHelper.unsubscribe(receiver);
        receiver = null;
        paymentFlowController.cancelPaymentFlow();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        handlePaymentInterrupted();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //fragment can be aceesed in on resume method
        paymentFragment.startPayment(paymentFlowController);
    }
//    private void handlePaymentInterrupted() {
//        if (signatureConfirmationDialog != null) {
//            signatureConfirmationDialog.dismiss();
//        }
//        //paymentFlowController.cancelPaymentFlow();
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

//    private void runOnUiThreadIfNotDestroyed(final Runnable runnable) {
//        if (!isDestroyed) runOnUiThread(runnable);
//    }

    @Override
    public void onPaymentFlowUpdate(PaymentFlowController.Update update) {
        switch (update) {
            case CONFIGURATION_UPDATE:
                paymentFragment.showProgress(R.string.acceptsdk_progress__ca_keys, true);
                break;
            case FIRMWARE_UPDATE:
                paymentFragment.showProgress(R.string.acceptsdk_progress__firmware, true);
                break;
            case LOADING:
            case COMMUNICATION_LAYER_ENABLING:
                paymentFragment.showProgress(R.string.acceptsdk_progress__loading_pls_wait, true);
                break;
            case RESTARTING:
                paymentFragment.showProgress(R.string.acceptsdk_progress__restart, true);
                break;
            case ONLINE_DATA_PROCESSING:
                paymentFragment.showProgress(R.string.acceptsdk_progress__online, true);
                break;
            case EMV_CONFIGURATION_LOAD:
                paymentFragment.showProgress(R.string.acceptsdk_progress__terminal_configuration, true);
                break;
            case DATA_PROCESSING:
                paymentFragment.showProgress(R.string.acceptsdk_progress__processing, true);
                break;
            case WAITING_FOR_CARD_REMOVE:
                paymentFragment.showProgress(R.string.acceptsdk_progress__remove, false);
                break;
            case WAITING_FOR_INSERT:
                paymentFragment.showProgress(R.string.acceptsdk_progress__insert, false);
                break;
            case WAITING_FOR_INSERT_OR_SWIPE:
                paymentFragment.showProgress(R.string.acceptsdk_progress__insert_or_swipe, false);
                break;
            case WAITING_FOR_INSERT_SWIPE_OR_TAP:
                paymentFragment.showProgress(R.string.acceptsdk_progress__insert_swipe_or_tap, false);
                break;
            case WAITING_FOR_SWIPE:
                paymentFragment.showProgress(R.string.acceptsdk_progress__swipe, false);
                break;
            case WAITING_FOR_PINT_ENTRY:
                paymentFragment.showProgress(R.string.acceptsdk_progress__enter_pin, false);
                break;
            case WAITING_FOR_AMOUNT_CONFIRMATION:
                paymentFragment.showProgress(R.string.acceptsdk_progress__confirm_amount, false);
                break;
            case WAITING_FOR_SIGNATURE_CONFIRMATION:
//            paymentFragment.showProgress(R.string.acceptsdk_progress__confirm_signature, false);
                showSignatureFragment(null);
                signatureFragment.hideButtons();
                //just need to show captured signature on display for confirm
                //TODO put toast snacbar to inform about signature confirm
                break;
            case TERMINATING:
                paymentFragment.showProgress(R.string.acceptsdk_progress__terminating, false);
                break;
            case TRANSACTION_UPDATE:
                //toto dismiss confirm dialog if present
                paymentFragment.showProgress(R.string.acceptsdk_progress__tc_update, true);
                break;
            case WRONG_SWIPE:
                paymentFragment.showProgress(R.string.acceptsdk_progress__bad_readout, true);
                break;
            case UNKNOWN:
                paymentFragment.showProgress(R.string.acceotsdk_progress__unknown, true);
                break;
        }
    }

    @Override
    public void onPaymentFlowError(PaymentFlowController.Error error, String s) {
        paymentFragment.showPaymentFlowError(error, s);
    }

    @Override
    public void onPaymentSuccessful(Payment payment, String s) {
        runOnUiThread(() ->paymentFragment.successfulPayment());

    }


    /**
     * In some cases  is needed signature as primary or additional cardholder verification method
     * <p>
     * simple display view with drawing possibilities and "OK"-signature done / "Cancel"-cancel payment buttons
     *
     * @param signatureRequest
     */
    @Override
    public void onSignatureRequested(PaymentFlowController.SignatureRequest signatureRequest) {
        showSignatureFragment(new ConfirmRequestWrapper(signatureRequest));
    }

    /**
     * if used signature as verification method , seller have to check and compare signature on display and signature od back side of card
     * <p>
     * we have to just display signature on screen
     *
     * @param signatureConfirmationRequest
     */
    @Override
    public void onSignatureConfirmationRequested(PaymentFlowController.SignatureConfirmationRequest signatureConfirmationRequest) {
        showSignatureFragment(new ConfirmRequestWrapper(signatureConfirmationRequest));
        if(!isSignatureConfirmationInApplication()){
            signatureFragment.hideButtons();
        }
    }

}