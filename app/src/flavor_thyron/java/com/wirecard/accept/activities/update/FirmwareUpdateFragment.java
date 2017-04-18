package com.wirecard.accept.activities.update;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseFragment;
import com.wirecard.accept.help.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.wirecard.accept.sdk.cnp.observer.AdapterEvent;
import de.wirecard.accept.sdk.cnp.observer.CNPListener;
import de.wirecard.accept.sdk.cnp.observer.ProcessResult;
import de.wirecard.accept.sdk.cnp.observer.ProcessState;
import de.wirecard.accept.sdk.cnp.observer.TerminalEvent;
import nucleus.factory.RequiresPresenter;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Firmware update activity
 */
@RequiresPresenter(FirmwareUpdatePresenter.class)
public class FirmwareUpdateFragment extends BaseFragment<FirmwareUpdatePresenter> implements CNPListener {
    private static String TAG = FirmwareUpdateFragment.class.getSimpleName();
    @BindView(R.id.message)
    TextView message_text;
    @BindView(R.id.progress)
    TextView progress_text;
    @BindView(R.id.button)
    Button cancelButton;

    public static FirmwareUpdateFragment newInstance(Parcelable device) {

        Bundle args = new Bundle();
        args.putParcelable(Constants.EXTRA_SELECTED_DEVICE, device);

        FirmwareUpdateFragment fragment = new FirmwareUpdateFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firmware_update, container, false);
        ButterKnife.bind(view);
        //store choosen device
        getPresenter().setCurrentDev(getActivity().getIntent().getExtras().getParcelable(Constants.EXTRA_SELECTED_DEVICE));
        getActivity().setResult(RESULT_CANCELED);
        return view;
    }

    @OnClick(R.id.button)
    public void cancel() {
        //result is set
        getPresenter().cancelActualTask();
        getActivity().finish();
    }

    public void showFirmwareScreen_LoadingVersionInfo() {
        showProgress(R.string.progress_contacting, R.string.downloading_fw);
    }

    public void showProgress(int progressRes, int messageRes) {
        progress_text.setText(progressRes);
        message_text.setText(messageRes);
    }

    public void showProgress(int progressRes, String messageRes) {
        progress_text.setText(progressRes);
        message_text.setText(messageRes);
    }

    public void showProgress(int messageRes) {
        progress_text.setText("");
        message_text.setText(messageRes);
    }

    public void showEnableBluetooth(){
        showProgress(R.string.progress_bluetooth_off, R.string.enable_bt_and_try_again);
    }


    public void showFailedConnectingScreen() {
        showProgress(R.string.bt_pairing_failed);
    }

    public void showFailedDownloadAndExtract(){
        showFailedConnectingScreen();
        //todo check this
        message_text.setText(R.string.fw_update_failed_download_and_extract);
    }

    public void showBluetoothConnectionLostScreen() {
        showProgress(R.string.bt_pairing_connection_lost);
    }

    @Override
    public void onDestroy() {
        // to the bottom?
        super.onDestroy();
        Log.d(TAG, "disconnecting from cnp controller");
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().finish();
    }


//TODO back press will cancel fw update
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        getActivity().setResult(RESULT_CANCELED);
//        getPresenter().cancelActualTask();
//    }


    @Override
    public void onAdapterEvent(AdapterEvent adapterEvent) {
        Log.d(TAG, "onAdapterEvent: " + adapterEvent);

        switch (adapterEvent) {
            case ADAPTER_DISABLED:
                showBluetoothConnectionLostScreen();
                break;
            case ADAPTER_DISABLING:
            case ADAPTER_ENABLING:
                break;
            case ADAPTER_IDLE:
                Log.d(TAG, "Idle with action: " + adapterEvent.getActionCode());
                switch (adapterEvent.getActionCode()) {
                    case AdapterEvent.ACTION_IDLE_CONNECTION_FAILED_CRITICAL:
                    case AdapterEvent.ACTION_IDLE_CONNECTION_FAILED:
                    case AdapterEvent.ACTION_IDLE:
                    case AdapterEvent.ACTION_IDLE_CONNECTION_FINISHED:
                        if (!getPresenter().isFinishedUpdate())
                            showFailedConnectingScreen();
                        break;
                    case AdapterEvent.ACTION_IDLE_CONNECTION_LOST:
                        if (!getPresenter().isTerminalResetByApp() && !getPresenter().isFinishedUpdate())
                            showBluetoothConnectionLostScreen();
                        break;
                    case AdapterEvent.ACTION_IDLE_CONF_UPDATE_FAILED:
                        showBluetoothConnectionLostScreen();
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onConnectionStarted() {
        Log.d(TAG, "onConnectionStarted");
        showProgress(R.string.fw_update_progress_contacting);
        progress_text.setText(R.string.fw_update_message_bt_connecting);
    }

    @Override
    public void onTerminalEvent(TerminalEvent terminalEvent) {
        Log.d(TAG, "onTerminalEvent: " + terminalEvent);
        switch (terminalEvent) {
            case CONFIG_UPDATE_FINISHED:
            case FIRMWARE_UPDATE_AVAILABLE:
                break;
            case CONFIG_UPDATE_STARTED:
                showProgress(R.string.processing, R.string.installing_fw);
                break;
            case FIRMWARE_UPDATE_FINISHED:
                showProgress(R.string.fw_install_success);
                getPresenter().saveCurrentVersionOnBe();
                cancelButton.setText(R.string.wl_general_done);
                getActivity().setResult(RESULT_OK);
                break;
            case FIRMWARE_UPDATE_STARTED:
                showProgress(R.string.processing, getString(R.string.wl_general_firmware_update_pending,  getPresenter().getFromVersion(), getPresenter().getToVersion()));
                break;
            default:
                break;


        }
    }



    @Override
    public void onConnectionEstablished(boolean restartRequired) {
        getPresenter().onConnectionEstablished(restartRequired);
    }

    @Override
    public void onProcessStarted() {

    }

    @Override
    public void onProcessUpdate(ProcessState processState) {

    }

    @Override
    public void onProcessFinished(ProcessResult processResult, Exception e) {

    }
}
