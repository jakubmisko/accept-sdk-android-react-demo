package com.wirecard.accept.help;

import android.content.Context;

import com.wirecard.accept.exceptions.DeviceDiscoverException;

import java.util.List;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import rx.Observable;

/**
 * Created by super on 07.01.2017.
 */

public class DiscoverDevices {

    public static Observable<List<PaymentFlowController.Device>> devices(Context context, PaymentFlowController controller) {
        return Observable.create(subscriber -> {

            //first we have to call discover devices to get list of paired device from smartphone
            controller.discoverDevices(context, new PaymentFlowController.DiscoverDelegate() {

                @Override
                public void onDiscoveryError(PaymentFlowController.DiscoveryError discoveryError, String s) {
                    subscriber.onError(new DeviceDiscoverException(s, discoveryError));
                }

                @Override
                public void onDiscoveredDevices(List<PaymentFlowController.Device> list) {
                    subscriber.onNext(list);
                    subscriber.onCompleted();
                }
            });
        });
    }

    public static String[] getDeviceNames(List<PaymentFlowController.Device> devices) {
        String[] devicesArray = new String[devices.size()];
        Observable.from(devices)
                .map(f -> f.displayName == null ? f.id : f.displayName)
                .toList()
                .subscribe(s -> {
                    s.toArray(devicesArray);
                });
        return devicesArray;
    }
}
