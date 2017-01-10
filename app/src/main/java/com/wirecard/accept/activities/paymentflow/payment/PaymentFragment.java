package com.wirecard.accept.activities.paymentflow.payment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.BaseFragment;
import com.wirecard.accept.exceptions.DeviceDiscoverException;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.help.DiscoverDevices;
import com.wirecard.accept.rx.dialog.RxDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by super on 07.01.2017.
 */

public class PaymentFragment extends BaseFragment<PaymentPresenter> {
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.amount)
    TextView amount;

    private PaymentContract paymentContract;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        paymentContract = (PaymentContract) context;
    }

    public static PaymentFragment newInstance(int initialStatusRes) {
        Bundle args = new Bundle();
        if (initialStatusRes != -1) {
            args.putInt(Constants.INITIAL_MESSAGE, initialStatusRes);
        }
        PaymentFragment fragment = new PaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static PaymentFragment newInstance() {
        return newInstance(-1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_fragment, container, false);
        ButterKnife.bind(this, view);
        if (getArguments().containsKey(Constants.INITIAL_MESSAGE)) {
            status.setText(savedInstanceState.getInt(Constants.INITIAL_MESSAGE));
        }
        return view;
    }

    public void showTerminalDiscoveryError(DeviceDiscoverException exception) {
//TODO check leakage and use unsubscribe after
        RxDialog.create(getActivity(), getString(R.string.acceptsdk_dialog_discovery_error_title), getString(R.string.acceptsdk_dialog_discovery_error_message,
                exception.getDiscoveryError() + " - " + exception.getMessage()), getString(android.R.string.ok))
                .subscribe(click -> getActivity().finish());
    }

    public void terminalChooser(List<PaymentFlowController.Device> devices) {
        if (devices.isEmpty()) {
            showNoDeviceError();
        } else {
            String[] devicesNames = DiscoverDevices.getDeviceNames(devices);
            RxDialog.create(getActivity(), R.string.acceptsdk_dialog_terminal_chooser_title, android.R.string.cancel, devicesNames)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(chosenDevice -> {
                                showProgress(getString(R.string.acceptsdk_progress__connecting, devices.get(chosenDevice).displayName), true);
                                paymentContract.startPayment(devices.get(chosenDevice), "29.5");
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

    private void showProgress(final String message, final boolean showProgress) {
//        runOnUiThreadIfNotDestroyed(() -> {
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
        status.setText(message);
//        });
    }

    public void successfulPayment() {
//        runOnUiThreadIfNotDestroyed(()->{
        showPaymentResult(true);
        Toast.makeText(getActivity(), "Payment successful !", Toast.LENGTH_LONG).show();
        getActivity().finish();
//        });
    }


    public void showNoDeviceError() {
        /*                .setTitle(R.string.acceptsdk_dialog_no_terminals_title)
                .setMessage(R.string.acceptsdk_dialog_no_terminals_message)*/
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
}
