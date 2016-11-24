package com.wirecard.accept.async;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.wirecard.accept.FirmwareActivityStart;
import com.wirecard.accept.R;

import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.FirmwareNumberAndUrl;
import de.wirecard.accept.sdk.backend.AcceptBackendService;
import de.wirecard.accept.sdk.backend.AcceptFirmwareVersion;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import de.wirecard.accept.sdk.model.TerminalInfo;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.util.async.Async;

/**
 * Created by jakub on 24.06.2016.
 */
public class FirmwareCheck {
    private final String TAG = "FirmwareCheck";
    private Context context;
    private FirmwareActivityStart starter;
    private Subscription task;

    public FirmwareCheck(FirmwareActivityStart starter, Context context) {
        this.starter = starter;
        this.context = context;
    }

    public void beforeExecute() {
        Toast.makeText(context, "Started firmware version check assync task", Toast.LENGTH_LONG).show();
    }

    public void execute(PaymentFlowController.Device device) {
        beforeExecute();
        task = Async.start(() -> {
            AcceptSDK.saveCurrentVersionOfFirmwareInBackend(null);//clear remembered data
            AcceptBackendService.Response<AcceptFirmwareVersion, Void> response = AcceptSDK.fetchFirmwareVersionInfo();
            if (response == null) {
                onError(new Throwable(context.getString(R.string.fw_install_no_response)));
            } else if (response.hasError()) {
                onError(new Throwable(response.getError().toString()));
            }
            return response;
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(onSucces(device));
    }

    //pre execute
    private Action1<AcceptBackendService.Response<AcceptFirmwareVersion, Void>> onSucces(PaymentFlowController.Device device) {
        return response -> {
            AcceptFirmwareVersion currentVersionDataFormBackend = response.getBody();
            if (currentVersionDataFormBackend == null || TextUtils.isEmpty(currentVersionDataFormBackend.url)) {
                Toast.makeText(context, context.getString(R.string.fw_install_wrong_data), Toast.LENGTH_LONG).show();
            } else {
                try {
                    if (device != null && TerminalInfo.needsFirmwareUpdate(currentVersionDataFormBackend.version)) { // throws exception if you will do something not allowed (mix versions/terminal compatibility)
                        AcceptSDK.saveCurrentVersionOfFirmwareInBackend(new FirmwareNumberAndUrl(currentVersionDataFormBackend.version, currentVersionDataFormBackend.url));
                        starter.showFirmwareActivity();
                    } else {
                        Toast.makeText(context, context.getString(R.string.fw_update_not_needed), Toast.LENGTH_LONG).show();
                    }
                } catch (RuntimeException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        };
    }


    private void onError(Throwable e) {
        Log.e(TAG, e.getMessage());
        Toast.makeText(context, context.getString(R.string.something_wrong_try_again), Toast.LENGTH_LONG).show();
    }

    public void cancel() {
        if (task != null && !task.isUnsubscribed()) {
            task.unsubscribe();
            task = null;
        }
    }

}
