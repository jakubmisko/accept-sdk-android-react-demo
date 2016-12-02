package com.wirecard.accept.menu;

import android.content.Context;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.rx.dialog.RxDialog;

import java.util.List;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import rx.Observable;

/**
 * Created by super on 24.11.2016.
 */

public class DiscoverDevices implements PaymentFlowController.DiscoverDelegate {
    private Context context;

    public DiscoverDevices(Context context) {
        this.context = context;
    }

    @Override
    public void onDiscoveryError(PaymentFlowController.DiscoveryError discoveryError, String s) {
        //Check DiscoveryError enum and handle all states
        Toast.makeText(context, context.getString(R.string.bt_enable), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDiscoveredDevices(List<PaymentFlowController.Device> list) {
        //received all paired devices from smartphone
        if (list == null || list.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.pair_terminal), Toast.LENGTH_LONG).show();
            return;
        }
//        String[] devices = new String[list.size()];
        //todo observerable inside observerable?
        getDeviceNames(list)
                .subscribe(onNext -> {
                    String[] devices = onNext.toArray(new String[list.size()]);
                    RxDialog.create(context, R.string.acceptsdk_dialog_terminal_chooser_title, android.R.string.cancel, devices)
                            .subscribe(choosen -> {
                                PaymentFlowController.Device choosenDevice = list.get(choosen);
                            });
                });
    }

    private Observable<List<String>> getDeviceNames(List<PaymentFlowController.Device> devices) {
        return Observable.from(devices)
                .map(f -> f.displayName == null ? f.id : f.displayName)
                .toList();
    }
}
