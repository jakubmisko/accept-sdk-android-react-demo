package com.wirecard.accept.activities.menu;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.wirecard.accept.exceptions.DeviceDiscoverException;
import com.wirecard.accept.help.DiscoverDevices;

import de.wirecard.accept.extension.refactor.AcceptThyronPaymentFlowController;
import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.FirmwareNumberAndUrl;
import de.wirecard.accept.sdk.backend.AcceptBackendService;
import de.wirecard.accept.sdk.backend.AcceptFirmwareVersion;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import de.wirecard.accept.sdk.model.TerminalInfo;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by jakub on 24.06.2016.
 */
//TODO rewrite with composition
public class MenuPresenter extends RxPresenter<MenuActivity> {
    private Context context;
    private PaymentFlowController.Device device;
    private AcceptThyronPaymentFlowController controller;
    private final int DISCOVER_DEVICES = 0;
    private final int VERSION_CHECK = 1;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        //todo consider using fromCallable to make cold observerable=created when observer subscribe
        restartableLatestCache(DISCOVER_DEVICES,
                //factory
                ()->{

                    return DiscoverDevices.devices(context, new AcceptThyronPaymentFlowController(false, true));
                },
                //success
                MenuActivity::presentDiscoveredDevices,
                //failure
                (menuActivity, throwable) -> menuActivity.presentDiscoveryError((DeviceDiscoverException) throwable)
        );

        restartableLatestCache(VERSION_CHECK, () -> Observable.create((Observable.OnSubscribe<AcceptBackendService.Response<AcceptFirmwareVersion, Void>>) subscriber -> {
                    //clear remembered data
                    AcceptSDK.saveCurrentVersionOfFirmwareInBackend(null);
                    //setup new data and remember
                    AcceptBackendService.Response<AcceptFirmwareVersion, Void> response = AcceptSDK.fetchFirmwareVersionInfo();

                    if (response == null || response.hasError()) {
                        //TODO wrap error to exception?
                        subscriber.onError(new Throwable());
                    } else {
                        subscriber.onNext(response);
                    }
                }
                ), (menuActivity, acceptFirmwareVersion) -> {
                    AcceptFirmwareVersion currentVersionDataFormBackend = acceptFirmwareVersion.getBody();
                    if (currentVersionDataFormBackend == null || TextUtils.isEmpty(currentVersionDataFormBackend.url)) {
                        menuActivity.presentWrongBeData();
                    } else {
                        try {
                            // throws exception if you will do something not allowed (mix versions/terminal compatibility)
                            if (device != null && TerminalInfo.needsFirmwareUpdate(currentVersionDataFormBackend.version)) {
                                AcceptSDK.saveCurrentVersionOfFirmwareInBackend(new FirmwareNumberAndUrl(currentVersionDataFormBackend.version, currentVersionDataFormBackend.url));
                                menuActivity.gotToFirmwareUpdate(device.id);
                            } else {
                                menuActivity.presentUpdateNotNeeded();
                            }
                        } catch (RuntimeException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                },
                (menuActivity, throwable) -> {
                    menuActivity.presentFwVersionError();
                }
        );
    }




    void discoverDevices(Context context) {
        this.context = context;
        AcceptSDK.saveCurrentVersionOfFirmwareInBackend(null);//clear remembered data
        //SDK is remembering versions per login
        start(DISCOVER_DEVICES);
    }

    void checkDeviceIdentity(PaymentFlowController.Device device) {
        this.device = device;
        controller.checkDeviceIdentity(device, new AcceptThyronPaymentFlowController.SimpleConnectListener() {
            @Override
            public void onSuccessfulConnect(boolean b) {
                view().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(menuActivity -> {
                            menuActivity.presentSuccessfulConnect(b);
                            menuActivity.presentVersionCheckStarted();
                        });
                start(VERSION_CHECK);
            }

            @Override
            public void onBluetoothConnectionError() {
                view().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(MenuActivity::presentBluetoothError);
            }

            @Override
            public void onError(String s) {
                view().observeOn(AndroidSchedulers.mainThread())
                        .subscribe(menuActivity -> menuActivity.presentError(s));
            }
        });


    }

}
