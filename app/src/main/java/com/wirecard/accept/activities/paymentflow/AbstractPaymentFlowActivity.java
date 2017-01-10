/**
 * Copyright (c) 2015 Wirecard. All rights reserved.
 * <p>
 * Accept SDK for Android
 */
package com.wirecard.accept.activities.paymentflow;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.BaseActivity;
import com.wirecard.accept.activities.paymentflow.payment.PaymentContract;
import com.wirecard.accept.activities.paymentflow.payment.PaymentFragment;
import com.wirecard.accept.activities.paymentflow.signature.ConfirmRequestWrapper;
import com.wirecard.accept.activities.paymentflow.signature.SignatureContract;
import com.wirecard.accept.activities.paymentflow.signature.SignatureFragment;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.help.RxHelper;
import com.wirecard.accept.rx.receivers.RxBroadcastReceiver;

import butterknife.BindView;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import de.wirecard.accept.sdk.model.Payment;
import rx.Subscription;

/**
 * Basin payment flow controlling activity
 */
public abstract class AbstractPaymentFlowActivity extends BaseActivity<PaymentFlowPresenter> implements PaymentContract, SignatureContract, PaymentFlowController.PaymentFlowDelegate {

    private Subscription receiver;

    protected PaymentFlowController paymentFlowController;

    protected Boolean sepa = false;// used for sepa payment support
    Bundle sign = null;

    @BindView(R.id.container)
    View content;


    public PaymentFlowController getPaymentFlowController() {
        return paymentFlowController;
    }

    private void toFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        Fragment current = fm.findFragmentById(R.id.container);
        if (current == null || !current.equals(fragment)) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
        }
    }

    public PaymentFragment toPaymentFragment() {
        FragmentManager fm = getFragmentManager();
        Fragment current = fm.findFragmentById(R.id.container);
        if (current instanceof PaymentFragment) {
            return (PaymentFragment) current;
        } else {
            PaymentFragment paymentFragment = PaymentFragment.newInstance();
            toFragment(paymentFragment);
            //TODO need synchronization here because commit is too slow
            return paymentFragment;
        }
    }

    public SignatureFragment toSignatureFragment(ConfirmRequestWrapper confirmRequestWrapper) {
        FragmentManager fm = getFragmentManager();
        Fragment current = fm.findFragmentById(R.id.container);
        if (current instanceof SignatureFragment) {
            return (SignatureFragment) current;
        } else {
            SignatureFragment signatureFragment = SignatureFragment.newInstance(confirmRequestWrapper);
            toFragment(signatureFragment);
            return signatureFragment;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);

        final Bundle b = getIntent().getExtras();
        if (b != null) {
            sepa = b.getBoolean(Constants.SEPA, false);
        }

        paymentFlowController = createNewController();

        if (paymentFlowController == null)
            throw new IllegalArgumentException("You have to implement createNewController()");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        if (paymentFlowController instanceof AcceptThyronPaymentFlowController && ((Application) getApplicationContext()).usb) {
//            ((AcceptThyronPaymentFlowController) paymentFlowController).registerDiscoveryReadyListener(new AcceptThyronPaymentFlowController.DiscoveryReadyListener() {
//                @Override
//                public void onDiscoveryReady() {
//                    proceedToDevicesDiscovery();
//                }
//            });
//        }
//        else {
        toPaymentFragment();//.proceedToDevicesDiscovery(paymentFlowController);
//        }
        receiver = RxBroadcastReceiver.create(this, new IntentFilter(Intent.ACTION_SCREEN_OFF))
                .subscribe(intent -> toPaymentFragment().showPaymentResult(false));
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

    private void runOnUiThreadIfNotDestroyed(final Runnable runnable) {
        if (!isDestroyed) runOnUiThread(runnable);
    }

    //    public void requestCustomerSignature(){
//        RxDialog.create(this, R.string.acceptsdk_dialog_signature_instruction_title, R.string.acceptsdk_dialog_signature_instruction_message, android.R.string.ok)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(click->{
//                    Intent intent = new Intent(AbstractPaymentFlowActivity.this, SignatureFragment.class);
//                    startActivityForResult(intent, Constants.REQUEST_SIGNATURE);
//                });
//    }
    @Override
    public void onPaymentFlowUpdate(PaymentFlowController.Update update) {
        PaymentFragment paymentFragment = toPaymentFragment();
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
                toSignatureFragment(null).hideButtons();
                //just need to show captured signature on display for confirm
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
        toPaymentFragment().showPaymentFlowError(error, s);
    }

    @Override
    public void onPaymentSuccessful(Payment payment, String s) {
        toPaymentFragment().successfulPayment();
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
        toSignatureFragment(new ConfirmRequestWrapper(signatureRequest));
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
        toSignatureFragment(new ConfirmRequestWrapper(signatureConfirmationRequest));
    }

    @Override
    public void startPayment(PaymentFlowController.Device device, String amount) {
        getPresenter().proceedToPayment(device, amount);
    }
}