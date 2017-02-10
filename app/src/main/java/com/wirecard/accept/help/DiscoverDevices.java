package com.wirecard.accept.help;

import android.content.Context;

import com.wirecard.accept.exceptions.DeviceDiscoverException;

import java.util.List;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import rx.Observable;
import rx.Single;

/**
 * Extracted device discovery functionality for usage on multiple places
 */

public class DiscoverDevices {
    /**
     * get list of paired devices
     * @param context to check permisions
     * @param controller to get devices
     * @return list of devices as observerable
     */
    public static Observable<List<PaymentFlowController.Device>> devices(Context context, PaymentFlowController controller) {
        return Observable.create(subscriber -> {

            //first we have to call discover devices to get list of paired device from smartphone
            controller.discoverDevices(context, new PaymentFlowController.DiscoverDelegate() {
                //wrap error to device discovery exception
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

    /**
     * convert device object to string representation
     * @param devices list of devices
     * @return array of string names
     */
    public static String[] getDeviceNames(List<PaymentFlowController.Device> devices) {
        String[] devicesArray = new String[devices.size()];
        //make stream from list (iterable object)
        Observable.from(devices)
                //every object map to display name or id if name is not present
                .map(f -> f.displayName == null ? f.id : f.displayName)
                //collect stream back to list
                .toList()
                .subscribe(s -> {
                    //subscribe to stream and convert list to array
                    s.toArray(devicesArray);
                });
        return devicesArray;
    }

    public Single<PaymentFlowController.Device> getSavedDevice(String id, Context context, PaymentFlowController controller) {
        return devices(context, controller)
                .flatMap(devices -> Observable.from(devices)
                        .filter(device -> device.id.equals(id))
                ).toSingle();
    }
}
