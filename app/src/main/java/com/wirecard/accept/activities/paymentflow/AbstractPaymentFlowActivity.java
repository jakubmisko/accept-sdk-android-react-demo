package com.wirecard.accept.activities.paymentflow;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.BaseActivity;
import com.wirecard.accept.dialogs.PaymentFlowDialogs;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import nucleus.factory.RequiresPresenter;

/**
 * Created by jakub on 15.06.2016.
 */
@RequiresPresenter(PaymentFlowPresenter.class)
public abstract class AbstractPaymentFlowActivity extends BaseActivity<PaymentFlowPresenter>  {
    private PaymentFlowController paymentFlowController;

    public abstract PaymentFlowController createNewController();
    public abstract boolean isSignatureConfirmationInApplication();

    private boolean isDestroyed = false; // To support Android 4.2, 4.2.2 ( < API 17 ).
    private Dialog signatureConfirmationDialog = null;

    @BindView(R.id.amount)
    TextView amountTextView;
//    public static Intent intent(final Context context) {
//        return new Intent(context, PaymentFlowActivity.class);
//    }

    public PaymentFlowController getPaymentFlowController() {
        return paymentFlowController;
    }

    public void setAmount(String amount){
        amountTextView.setText(amount);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_old);

        paymentFlowController = createNewController();
        if (paymentFlowController == null)
            throw new IllegalArgumentException("You have to implement createNewController()");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getPresenter().proceedToDevicesDiscovery();
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


    public void showSignatureRequest(){
        if (signatureConfirmationDialog != null) {
            return;
        }
        runOnUiThreadIfNotDestroyed(() -> {

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
            status.setText(getString(success ? R.string.acceptsdk_progress__sucesful : R.string.acceptsdk_progress__declined));
            findViewById(R.id.progress_section).setVisibility(View.GONE);
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
