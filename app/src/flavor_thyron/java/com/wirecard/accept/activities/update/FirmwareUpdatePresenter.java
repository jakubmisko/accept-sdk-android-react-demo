package com.wirecard.accept.activities.update;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.FirmwareNumberAndUrl;
import de.wirecard.accept.sdk.cnp.CNPController;
import de.wirecard.accept.sdk.cnp.CNPDevice;
import de.wirecard.accept.sdk.cnp.observer.CNPListener;
import de.wirecard.accept.sdk.model.TerminalInfo;
import nucleus.presenter.RxPresenter;
import rx.Observable;

/**
 * Created by jakub on 16.06.2016.
 */
public class FirmwareUpdatePresenter extends RxPresenter<FirmwareUpdateFragment> {
    private final String TAG = getClass().getSimpleName();

    private CNPDevice currentDev;
    private CNPController<?> controller = null; //old version of implementation

    private boolean terminalResetByApp = false;
    private boolean restarted = false;
    private boolean finishedUpdate = false;
    private FirmwareNumberAndUrl firmwareNumberAndUrl;

    private final int DOWNLOAD_FW = 0;
    private Context context;

    void setCurrentDev(CNPDevice currentDev) {
        this.currentDev = currentDev;
    }

    boolean isFinishedUpdate() {
        return finishedUpdate;
    }

    boolean isTerminalResetByApp() {
        return terminalResetByApp;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        firmwareNumberAndUrl = AcceptSDK.getCurrentVersionOfSavedFirmwareInBackend();
        controller = AcceptSDK.getCNPController();

        restartableLatestCache(
                DOWNLOAD_FW,
                () -> Observable.create(subscriber -> {
                    try {
                        TerminalInfo.downloadSaveAndExtractZipFile(context, firmwareNumberAndUrl.getFwUrl());
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                    //TODO onNext(null) may not work
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }),
                (firmwareUpdateFragment, t) -> handleFirmwareFileReady(firmwareUpdateFragment),
                (firmwareUpdateFragment, throwable) -> {
                    firmwareUpdateFragment.showFailedDownloadAndExtract();
                }

        );
    }

    @Override
    protected void onTakeView(FirmwareUpdateFragment firmwareUpdateFragment) {
        super.onTakeView(firmwareUpdateFragment);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            firmwareUpdateFragment.showEnableBluetooth();
        } else {
            firmwareUpdateFragment.showFirmwareScreen_LoadingVersionInfo();
            context = firmwareUpdateFragment.getActivity();
            start(DOWNLOAD_FW);
        }
    }


    /*
     if files downloaded call controller.connectToDevice
     this method will be deprecated in new SDK
     but for now you can use it like best way to upload firmware
     */
    public void handleFirmwareFileReady(CNPListener listener) {
        //TODO there may be main thread scheduler
//        handleFile = Observable.create(s -> {
        controller.setCNPListener(listener);
        controller.connectToDevice(currentDev, true, true, -1);
//        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    @Override
    protected void onDropView() {
        super.onDropView();
        stop(DOWNLOAD_FW);
        Log.d(TAG, "unregister from cnp controller");
        if (controller != null) {
            controller.setCNPListener(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    String getFromVersion() {
        return AcceptSDK.getTerminalInfo() != null ? AcceptSDK.getTerminalInfo().firmwareVersion : "-";
    }

    String getToVersion() {
        return AcceptSDK.getCurrentVersionOfSavedFirmwareInBackend() != null ? AcceptSDK.getCurrentVersionOfSavedFirmwareInBackend().getFwNumber() : "-";
    }

    void saveCurrentVersionOnBe() {
        finishedUpdate = true;
        if (restarted)
            AcceptSDK.saveCurrentVersionOfFirmwareInBackend(null);
    }

    void onConnectionEstablished(boolean restartRequired) {
        Log.d(TAG, "connection established");
        if (restartRequired && controller != null && !restarted) {
            terminalResetByApp = true;
            controller.restartDevice();
            restarted = true;
        }
    }


    private void disconnect() {
        if (controller != null) {
            controller.disconnect();
            controller.setCNPListener(null);
            controller = null;
        }
    }

    void cancelActualTask() {
        stop(DOWNLOAD_FW);
    }
}