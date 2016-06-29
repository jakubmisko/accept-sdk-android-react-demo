package com.wirecard.accept.async;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.wirecard.accept.FirmwareUpdate;
import com.wirecard.accept.R;

import java.io.IOException;

import de.wirecard.accept.sdk.FirmwareNumberAndUrl;
import de.wirecard.accept.sdk.model.TerminalInfo;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

/**
 * Created by jakub on 29.06.2016.
 */
public class DownloadFirmware {
    private FirmwareUpdate update;
    private Subscription task;
    private Context context;
    private FirmwareNumberAndUrl firmwareNumberAndUrl;

    public DownloadFirmware(FirmwareNumberAndUrl firmwareNumberAndUrl, Context context) {
        this.firmwareNumberAndUrl = firmwareNumberAndUrl;
        this.context = context;
    }

    public void beforeExecute() {
        update.showFirmwareScreen_LoadingVersionInfo();
    }

    public void execute(){
        task = Async.start(() -> {
            try {
                TerminalInfo.downloadSaveAndExtractZipFile(context, firmwareNumberAndUrl.getFwUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public Action1<Boolean> onSuccess(){
        return response ->{
            update.handleFirmwareFileReady();
        };
    }
    public void cancel() {
        if (task != null && task.isUnsubscribed()) {
            task.unsubscribe();
            task = null;
        }
    }
}
