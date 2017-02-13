package com.wirecard.accept.activities.paymentflow.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseFragment;
import com.wirecard.accept.exceptions.DeviceDiscoverException;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.help.DiscoverDevices;
import com.wirecard.accept.rx.dialog.RxDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import nucleus.factory.RequiresPresenter;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by super on 07.01.2017.
 */

@RequiresPresenter(PaymentPresenter.class)
public class PaymentFragment extends BaseFragment<PaymentPresenter> {
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.amount)
    TextView amount;
    private String amountValue;

    /**
     * create payment fragment instance with amount to be payed, payment method and sepa flag
     * @param arguments amount, payment method and sepa parameters
     * @return payment fragment
     */
    public static PaymentFragment newInstance(Bundle arguments) {
        PaymentFragment fragment = new PaymentFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.payment_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if (getArguments().containsKey(Constants.INITIAL_MESSAGE)) {
            status.setText(getArguments().getInt(Constants.INITIAL_MESSAGE));
        }
        //set amount to text view with currency
        if (getArguments().containsKey(Constants.AMOUNT)) {
            amountValue = getArguments().getString(Constants.AMOUNT);
            String amountWithCurrency = getPresenter().buildAmountWithCurrency(amountValue);
            amount.setText(amountWithCurrency);
        }
    }

    public void showTerminalDiscoveryError(DeviceDiscoverException exception) {
        //TODO check leakage and use unsubscribe after
        RxDialog.create(getActivity(), getString(R.string.acceptsdk_dialog_discovery_error_title), getString(R.string.acceptsdk_dialog_discovery_error_message,
                exception.getDiscoveryError() + " - " + exception.getMessage()), getString(android.R.string.ok))
                .subscribe(click -> getActivity().finish());
    }

    public void terminalChooser(List<PaymentFlowController.Device> devices) {
        if (devices.isEmpty()) {
            //present no bounded devices error
            showNoDeviceError();
        } else {
            String[] devicesNames = DiscoverDevices.getDeviceNames(devices);
            RxDialog.create(getActivity(), R.string.acceptsdk_dialog_terminal_chooser_title, android.R.string.cancel, devicesNames)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(chosenDevice -> {
                                showProgress(getString(R.string.acceptsdk_progress__connecting, devices.get(chosenDevice).displayName), true);
                                getPresenter().proceedToCardPayment(devices.get(chosenDevice), amount.getText().toString(), (PaymentFlowController.PaymentFlowDelegate) getActivity());
                            },
                            cancel -> {
                                getActivity().finish();
                            });
        }
    }


    public void showPaymentResult(final boolean success) {
//        runOnUiThreadIfNotDestroyed(() -> {
        status.setText(getString(success ? R.string.acceptsdk_progress__sucesful : R.string.acceptsdk_progress__declined));
        progressBar.setVisibility(View.GONE);
//        });
    }

    public void showPaymentFlowError(PaymentFlowController.Error error, String technicalDetails) {
        //TODO check leakage and use unsubscribe after
        RxDialog.create(getActivity(), getString(R.string.acceptsdk_dialog_payment_error_title), getString(R.string.acceptsdk_dialog_payment_error_message,
                error + " - " + technicalDetails), getString(android.R.string.ok))
                .subscribe(click -> getActivity().finish());
    }

    public void showProgress(final int messageRes, final boolean showProgress) {
        showProgress(messageRes == -1 ? "" : getString(messageRes), showProgress);
    }

    void showProgress(final String message, final boolean showProgress) {
//        runOnUiThreadIfNotDestroyed(() -> {
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
        status.setText(message);
//        });
    }

    public void setAmount(String amount) {
        this.amount.setText(amount);
    }

    public void successfulPayment() {
//        runOnUiThreadIfNotDestroyed(()->{
        showPaymentResult(true);
        Toast.makeText(getActivity(), "Payment successful !", Toast.LENGTH_LONG).show();
        getActivity().finish();
//        });
    }


    public void showNoDeviceError() {
        RxDialog.create(getActivity(), R.string.acceptsdk_dialog_no_terminals_title, R.string.acceptsdk_dialog_no_terminals_message, android.R.string.ok)
                .subscribe(click -> getActivity().finish());
    }

    /**
     * first step discovery devices
     */
    public void proceedToDevicesDiscovery(PaymentFlowController controller) {
        showProgress(R.string.acceptsdk_progress__searching, true);
        getPresenter().startDeviceDiscovery(getActivity(), controller);
    }

    public void startPayment(PaymentFlowController paymentFlowController) {
        if(getArguments().containsKey(Constants.PAYMENT_METHOD) && getArguments().getString(Constants.PAYMENT_METHOD).equals("Cash")){
//            showProgress("Cash payment", false);
            getPresenter().payByCash(amountValue);
        } else {
            proceedToDevicesDiscovery(paymentFlowController);
        }
    }
}
