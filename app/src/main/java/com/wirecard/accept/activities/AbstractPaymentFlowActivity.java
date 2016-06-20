package com.wirecard.accept.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.dialogs.PaymentFlowDialogs;
import com.wirecard.accept.help.CurrencyUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import de.wirecard.accept.sdk.model.Payment;
import de.wirecard.accept.sdk.model.PaymentItem;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jakub on 15.06.2016.
 */
public abstract class AbstractPaymentFlowActivity extends BaseActivity implements PaymentFlowController.PaymentFlowDelegate {
    private PaymentFlowController paymentFlowController;

    abstract PaymentFlowController createNewController();

    abstract boolean isSignatureConfirmationInApplication();

    private boolean isDestroyed = false; // To support Android 4.2, 4.2.2 ( < API 17 ).
    private Dialog signatureConfirmationDialog = null;

//    public static Intent intent(final Context context) {
//        return new Intent(context, PaymentFlowActivity.class);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentFlowController = createNewController();
        if (paymentFlowController == null)
            throw new IllegalArgumentException("You have to implement createNewController()");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        proceedToDevicesDiscovery();
        enableButtons(-1);
        registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        isDestroyed = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        handlePaymentInterrupted();
        unregisterReceiver(screenOffReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handlePaymentInterrupted();
    }

    private final BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handlePaymentInterrupted();
            showResultSection(false);
        }
    };

    private void handlePaymentInterrupted() {
        if (signatureConfirmationDialog != null) {
            signatureConfirmationDialog.dismiss();
        }
        paymentFlowController.cancelPaymentFlow();
    }

    private void runOnUiThreadIfNotDestroyed(final Runnable runnable) {
        if (!isDestroyed) {
            Single.create(singleSubscriber -> {
                runnable.run();
                singleSubscriber.onSuccess("Done");
            }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
            //runOnUiThread(runnable);
        }
    }

    /**
     * first step discovery devices
     */
    private void proceedToDevicesDiscovery() {
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

        final TextView amountTextView = (TextView) findViewById(R.id.amount);
        amountTextView.setText(CurrencyUtils.format(amountUnits, amountCurrency, Locale.getDefault()));
        paymentFlowController.startPaymentFlow(device, amountUnits, amountCurrency, this);
    }

    @Override
    public void onPaymentFlowUpdate(PaymentFlowController.Update update) {
        switch (update) {
            case CONFIGURATION_UPDATE:
                showProgress(R.string.acceptsdk_progress__ca_keys, true);
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


    /**
     * if used signature as verification method , seller have to check and compare signature on display and signature od back side of card
     * <p>
     * we have to just display signature on screen
     *
     * @param signatureConfirmationRequest
     */
    @Override
    public void onSignatureConfirmationRequested(final PaymentFlowController.SignatureConfirmationRequest signatureConfirmationRequest) {
        if (signatureConfirmationDialog != null) {
            return;
        }
        runOnUiThreadIfNotDestroyed(() -> {
            final PaymentFlowSignatureView signatureView = (PaymentFlowSignatureView) findViewById(R.id.signature);
            signatureConfirmationDialog = PaymentFlowDialogs.showSignatureConfirmation(AbstractPaymentFlowActivity.this, signatureView.getSignatureBitmap(), isSignatureConfirmationInApplication(), new PaymentFlowDialogs
                    .SignatureConfirmationListener() {
                @Override
                public void onSignatureConfirmedIsOK() {
                    showProgress(R.string.acceptsdk_progress__follow, false);
                    signatureConfirmationRequest.signatureConfirmed();
                }

                @Override
                public void onSignatureConfirmedIsNotOK() {
                    signatureConfirmationRequest.signatureRejected();
                }
            });
        });
    }

    private void showResultSection(final boolean success) {
        runOnUiThreadIfNotDestroyed(() -> {
            ((TextView) findViewById(R.id.status)).setText(getString(success ?
                    R.string.acceptsdk_progress__sucesful : R.string.acceptsdk_progress__declined));
            findViewById(R.id.progress_section).setVisibility(View.GONE);
            findViewById(R.id.signature_section).setVisibility(View.GONE);
        });
    }

    private void showProgress(final int messageRes, final boolean showProgress) {
        showProgress(messageRes == -1 ? "" : getString(messageRes), showProgress);
    }

    private void showProgress(final String message, final boolean showProgress) {
        runOnUiThreadIfNotDestroyed(() -> {
            findViewById(R.id.progress_section).setVisibility(View.VISIBLE);
            findViewById(R.id.progress).setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
            ((TextView) findViewById(R.id.status)).setText(message);
            findViewById(R.id.signature_section).setVisibility(View.GONE);
        });
    }

    private void showSignatureSection() {
        runOnUiThreadIfNotDestroyed(() -> {
            findViewById(R.id.progress_section).setVisibility(View.GONE);
            findViewById(R.id.signature_section).setVisibility(View.VISIBLE);
        });
    }

    private void enableButtons(final Integer... ids) {
        final List<Integer> idsList = Arrays.asList(ids);
        runOnUiThreadIfNotDestroyed(() -> {
            final ViewGroup buttonsSection = (ViewGroup) findViewById(R.id.buttons_section);
            for (int i = 0; i < buttonsSection.getChildCount(); ++i) {
                final View view = buttonsSection.getChildAt(i);
                if (view instanceof Button) {
                    view.setVisibility(idsList.contains(view.getId()) ? View.VISIBLE : View.GONE);
                }
            }
        });
    }
}
