package com.wirecard.accept.activities.update;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.BaseActivity;
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

/**
 * Created by jakub on 10.04.2016.
 */

@RequiresPresenter(FirmwareUpdatePresenter.class)
public class FirmwareUpdateActivity extends BaseActivity<FirmwareUpdatePresenter> implements CNPListener {
    private static String TAG = FirmwareUpdateActivity.class.getSimpleName();
    @BindView(R.id.textViewMessage)
    TextView message_text;
    @BindView(R.id.textViewProgress)
    TextView progress_text;
    @BindView(R.id.button)
    Button cancelButton;
    private boolean isDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);
        ButterKnife.bind(this);
        getPresenter().setCurrentDev(getIntent().getExtras().getParcelable(Constants.EXTRA_SELECTED_DEVICE));
        setResult(RESULT_CANCELED);
    }

    @OnClick(R.id.button)
    public void cancel() {
        isDestroyed = true;
        //result is set
        getPresenter().cancelActualTask();
        finish();
    }

    public void showFirmwareScreen_LoadingVersionInfo() {
        progress_text.setText(R.string.progress_contacting);
        message_text.setText(R.string.downloading_fw);
    }
    public void showEnableBluetooth(){
        progress_text.setText(R.string.progress_bluetooth_off);
        message_text.setText(R.string.enable_bt_and_try_again);
    }


    public void showFailedConnectingScreen() {
        progress_text.setText("");
        message_text.setText(R.string.bt_pairing_failed);
    }

    public void showFailedDownloadAndExtract(){
        showFailedConnectingScreen();
        message_text.setText(R.string.fw_update_failed_download_and_extract);
    }

    public void showBluetoothConnectionLostScreen() {
        progress_text.setText("");
        message_text.setText(R.string.bt_pairing_connection_lost);
    }


    @Override
    protected void onDestroy() {
        // to the bottom?
        super.onDestroy();
        Log.d(TAG, "disconnecting from cnp controller");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        getPresenter().cancelActualTask();
    }

    public boolean wasDestroyed() {
        return isDestroyed;
    }

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
        progress_text.setText(R.string.fw_update_progress_contacting);
        message_text.setText(R.string.fw_update_message_bt_connecting);
    }

    @Override
    public void onTerminalEvent(TerminalEvent terminalEvent) {
        Log.d(TAG, "onTerminalEvent: " + terminalEvent);
        switch (terminalEvent) {
            case CONFIG_UPDATE_FINISHED:

                break;
            case CONFIG_UPDATE_STARTED:
                progress_text.setText(R.string.processing);
                message_text.setText(R.string.installing_fw);
                break;
            case FIRMWARE_UPDATE_AVAILABLE:

                break;
            case FIRMWARE_UPDATE_FINISHED:
                progress_text.setText(R.string.blank);
                message_text.setText(R.string.fw_install_success);
                getPresenter().saveCurrentVersionOnBe();
                cancelButton.setText(R.string.wl_general_done);
                setResult(RESULT_OK);
                break;
            case FIRMWARE_UPDATE_STARTED:
                progress_text.setText(R.string.processing);
                message_text.setText(getString(R.string.wl_general_firmware_update_pending,  getPresenter().getFromVersion(), getPresenter().getToVersion()));
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
