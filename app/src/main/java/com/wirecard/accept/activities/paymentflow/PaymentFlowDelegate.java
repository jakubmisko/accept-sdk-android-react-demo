package com.wirecard.accept.activities.paymentflow;

import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.PaymentFlowSignatureView;
import com.wirecard.accept.dialogs.PaymentFlowDialogs;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import de.wirecard.accept.sdk.model.Payment;

/**
 * Created by super on 29.11.2016.
 */

public class PaymentFlowDelegate implements PaymentFlowController.PaymentFlowDelegate {

    private AbstractPaymentFlowActivity view;

    public void attachView(AbstractPaymentFlowActivity view){
        this.view = view;
    }

    /**
     * if used signature as verification method , seller have to check and compare signature on display and signature od back side of card
     * <p>
     * we have to just display signature on screen
     *
     * @param signatureConfirmationRequest
     */
    @Override
    public void onSignatureConfirmationRequested(final PaymentFlowController.SignatureConfirmationRequest signatureConfirmationRequest) {

    }

    @Override
    public void onPaymentFlowUpdate(PaymentFlowController.Update update) {
        switch (update) {
            case CONFIGURATION_UPDATE:
                view.showProgress(R.string.acceptsdk_progress__ca_keys, true);
                break;
            case FIRMWARE_UPDATE:
                showProgress(R.string.acceptsdk_progress__firmware, true);
                break;
            case LOADING:
                showProgress(R.string.acceptsdk_progress__wait, true);
                break;
            case RESTARTING:
                showProgress(R.string.acceptsdk_progress__restart, true);
                break;
            case ONLINE_DATA_PROCESSING:
                showProgress(R.string.acceptsdk_progress__online, true);
                break;
            case EMV_CONFIGURATION_LOAD:
                showProgress(R.string.acceptsdk_progress__terminal_configuration, true);
                break;
            case DATA_PROCESSING:
                showProgress(R.string.acceptsdk_progress__processing, true);
                break;
            case WAITING_FOR_CARD_REMOVE:
                showProgress(R.string.acceptsdk_progress__remove, false);
                break;
            case WAITING_FOR_INSERT:
                showProgress(R.string.acceptsdk_progress__insert, false);
                break;
            case WAITING_FOR_INSERT_OR_SWIPE:
                showProgress(R.string.acceptsdk_progress__insert_or_swipe, false);
                break;
            case WAITING_FOR_SWIPE:
                showProgress(R.string.acceptsdk_progress__swipe, false);
                break;
            case WAITING_FOR_PINT_ENTRY:
                showProgress(R.string.acceptsdk_progress__enter_pin, false);
                break;
            case WAITING_FOR_AMOUNT_CONFIRMATION:
                showProgress(R.string.acceptsdk_progress__confirm_amount, false);
                break;
            case TRANSACTION_UPDATE:
                enableButtons(-1);
                if (signatureConfirmationDialog != null) {
                    signatureConfirmationDialog.dismiss();
                    signatureConfirmationDialog = null;
                }
                showProgress(R.string.acceptsdk_progress__tc_update, true);
                break;
            case WRONG_SWIPE:
                showProgress(R.string.acceptsdk_progress__bad_readout, true);
                break;
        }
    }

    @Override
    public void onPaymentFlowError(final PaymentFlowController.Error error, final String technicalDetails) {
        runOnUiThreadIfNotDestroyed(() -> {
            showResultSection(false);
            PaymentFlowDialogs.showPaymentFlowError(AbstractPaymentFlowActivity.this, error, technicalDetails, v -> finish());
        });
    }

    @Override
    public void onPaymentSuccessful(final Payment payment, String TC) {
        runOnUiThreadIfNotDestroyed(() -> {
            showResultSection(true);
            Toast.makeText(getApplicationContext(), getString(R.string.toast_label_successful_payment), Toast.LENGTH_LONG).show();
            finish();
        });
    }

    /**
     * In some cases  is needed signature as primary or additional cardholder verification method
     * <p>
     * simple display view with drawing possibilities and "OK"-signature done / "Cancel"-cancel payment buttons
     *
     * @param signatureRequest
     */

    @Override
    public void onSignatureRequested(final PaymentFlowController.SignatureRequest signatureRequest) {
        runOnUiThreadIfNotDestroyed(() -> PaymentFlowDialogs.showSignatureInstructions(AbstractPaymentFlowActivity.this, v -> {
            showProgress(R.string.acceptsdk_progress__customer_sign_request, false);
            showSignatureSection();
            final PaymentFlowSignatureView signatureView = (PaymentFlowSignatureView) findViewById(R.id.signature);
            signatureView.clear();
            enableButtons(R.id.confirm_signature, R.id.cancel_signature_confirmation);
            findViewById(R.id.confirm_signature).setOnClickListener(v1 -> {
                if (signatureView.isSomethingDrawn()) {
                    enableButtons(-1);
                    showProgress(-1, false);
                    signatureRequest.signatureEntered(signatureView.compressSignatureBitmapToPNG());
                } else {
                    PaymentFlowDialogs.showNothingDrawnWarning(AbstractPaymentFlowActivity.this);
                }
            });
            findViewById(R.id.cancel_signature_confirmation).setOnClickListener(v1 -> PaymentFlowDialogs.showConfirmSignatureRequestCancellation(AbstractPaymentFlowActivity.this, new PaymentFlowDialogs.SignatureRequestCancelListener() {
                @Override
                public void onSignatureRequestCancellationConfirmed() {
                    signatureRequest.signatureCanceled();
                    finish();
                }

                @Override
                public void onSignatureRequestCancellationSkipped() {
                    // Do nothing. Dialog will be dismissed.
                }
            }));
        }));
    }
}
