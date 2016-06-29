package com.wirecard.accept.presenters;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;
import android.widget.Button;

import com.wirecard.accept.FirmwareUpdate;
import com.wirecard.accept.R;
import com.wirecard.accept.activities.FirmwareUpdateActivity;
import com.wirecard.accept.async.DownloadFirmware;

import butterknife.BindView;
import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.FirmwareNumberAndUrl;
import de.wirecard.accept.sdk.cnp.CNPController;
import de.wirecard.accept.sdk.cnp.CNPDevice;
import de.wirecard.accept.sdk.cnp.observer.AdapterEvent;
import de.wirecard.accept.sdk.cnp.observer.CNPListener;
import de.wirecard.accept.sdk.cnp.observer.ProcessResult;
import de.wirecard.accept.sdk.cnp.observer.ProcessState;
import de.wirecard.accept.sdk.cnp.observer.TerminalEvent;
import de.wirecard.accept.sdk.model.TerminalInfo;
import nucleus.presenter.Presenter;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jakub on 16.06.2016.
 */
public class FirmwareUpdatePresenter extends Presenter<FirmwareUpdateActivity> implements CNPListener, FirmwareUpdate {
    private static final String TAG = FirmwareUpdatePresenter.class.getSimpleName();
    private CNPDevice currentDev;
    private CNPController<?> controller = null; //old version of implementation

    private boolean terminalResetByApp = false;
    private boolean restarted = false;
    private boolean finishedUpdate = false;
    private FirmwareNumberAndUrl firmwareNumberAndUrl;
    private DownloadFirmware downloadTask;
    private Subscription handleFile;

    public FirmwareUpdatePresenter() {
        firmwareNumberAndUrl = AcceptSDK.getCurrentVersionOfSavedFirmwareInBackend();
        controller = AcceptSDK.getCNPController();
    }

    public void setCurrentDev(CNPDevice currentDev) {
        this.currentDev = currentDev;
    }

    @Override
    protected void onTakeView(FirmwareUpdateActivity firmwareUpdateActivity) {
        super.onTakeView(firmwareUpdateActivity);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            getView().showEnableBluetooth();
        } else {
            downloadTask = new DownloadFirmware(firmwareNumberAndUrl, getView().getApplicationContext());
            downloadTask.execute();
        }
    }


    /*
     if files downloaded call controller.connectToDevice
     this method will be depricated in new SDK
     but for now you can use it like best way to upload firmware
     */
    public void handleFirmwareFileReady() {
        handleFile = Observable.create(subscriber -> {
            if (!getView().wasDestroyed()) {
                controller.setCNPListener(this);
                controller.connectToDevice(currentDev, true, true, -1);
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public void showFirmwareScreen_LoadingVersionInfo() {
        getView().showFirmwareScreen_LoadingVersionInfo();
    }

    @Override
    protected void onDropView() {
        super.onDropView();
        if (downloadTask != null) {
            downloadTask.cancel();
        }
        if (handleFile != null && handleFile.isUnsubscribed()) {
            handleFile.unsubscribe();
            handleFile = null;
        }
    }

    @Override
    public void onAdapterEvent(AdapterEvent adapterEvent) {
        Log.d(TAG, "onAdapterEvent: " + adapterEvent);

        switch (adapterEvent) {
            case ADAPTER_DISABLED:
                getView().showBluetoothConnectionLostScreen();
                break;
            case ADAPTER_DISABLING:

                break;
            case ADAPTER_ENABLING:

                break;
            case ADAPTER_IDLE:
                Log.d(TAG, "Idle with action: " + adapterEvent.getActionCode());
                switch (adapterEvent.getActionCode()) {
                    case AdapterEvent.ACTION_IDLE_CONNECTION_FAILED_CRITICAL:
                    case AdapterEvent.ACTION_IDLE_CONNECTION_FAILED:
                    case AdapterEvent.ACTION_IDLE:
                    case AdapterEvent.ACTION_IDLE_CONNECTION_FINISHED:
                        if (!finishedUpdate)
                            getView().showFailedConnectingScreen();
                        break;
                    case AdapterEvent.ACTION_IDLE_CONNECTION_LOST:
                        if (!terminalResetByApp && !finishedUpdate)
                            getView().showBluetoothConnectionLostScreen();
                        break;
                    case AdapterEvent.ACTION_IDLE_CONF_UPDATE_FAILED:
                        getView().showBluetoothConnectionLostScreen();
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
        getView().setProgressText(R.string.progress_contacting);
        getView().setMessageText(R.string.bt_pairing_connecting);
    }

    @Override
    public void onTerminalEvent(TerminalEvent terminalEvent) {
        Log.d(TAG, "onTerminalEvent: " + terminalEvent);
        switch (terminalEvent) {
            case CONFIG_UPDATE_FINISHED:

                break;
            case CONFIG_UPDATE_STARTED:
                getView().setProgressText(R.string.processing);
                getView().setMessageText(R.string.installing_fw);
                break;
            case FIRMWARE_UPDATE_AVAILABLE:

                break;
            case FIRMWARE_UPDATE_FINISHED:
                getView().setProgressText(R.string.blank);
                getView().setMessageText(R.string.fw_install_success);
                finishedUpdate = true;
                if (restarted)
                    AcceptSDK.saveCurrentVersionOfFirmwareInBackend(null);

                getView().setCancelButtonText(R.string.wl_general_done);
                //result_ok
                getView().setResult(-1);
                break;
            case FIRMWARE_UPDATE_STARTED:
                getView().setProgressText(R.string.processing);
                getView().setMessageText(getView().getString(R.string.wl_general_firmware_update_pending,
                        getFromToVersion()));
                break;
            default:
                break;


        }
    }

    private String[] getFromToVersion() {
        String[] fromTo = new String[2];
        if (AcceptSDK.getTerminalInfo() != null) {
            fromTo[0] = AcceptSDK.getTerminalInfo().firmwareVersion;
        } else {
            fromTo[0] = "-";
        }
        if (AcceptSDK.getCurrentVersionOfSavedFirmwareInBackend() != null) {
            fromTo[1] = AcceptSDK.getCurrentVersionOfSavedFirmwareInBackend().getFwNumber();
        } else {
            fromTo[1] = "-";
        }
        return fromTo;
    }

    @Override
    public void onConnectionEstablished(boolean restartRequired) {
        Log.d(TAG, "connection established");
        if (restartRequired && controller != null && !restarted) {
            terminalResetByApp = true;
            controller.restartDevice();
            restarted = true;
        }
    }


    public void disconnect() {
        if (controller != null) {
            controller.disconnect();
            controller.setCNPListener(null);
            controller = null;
        }
    }

    public void unregisterController() {
        Log.d(TAG, "unregister from cnp controller");
        if (controller == null) return;
        controller.setCNPListener(null);
        if(downloadTask != null)
        downloadTask.cancel();
    }

    //not used
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