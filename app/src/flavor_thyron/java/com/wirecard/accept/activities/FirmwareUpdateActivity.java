package com.wirecard.accept.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.presenters.FirmwareUpdatePresenter;
import com.wirecard.accept.help.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusActivity;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jakub on 10.04.2016.
 */
@RequiresPresenter(FirmwareUpdatePresenter.class)
public class FirmwareUpdateActivity extends NucleusActivity<FirmwareUpdatePresenter> {
    private static String TAG = FirmwareUpdateActivity.class.getSimpleName();
    @BindView(R.id.textViewMessage)
    private TextView message_text;
    @BindView(R.id.textViewProgress)
    private TextView progress_text;
    @BindView(R.id.button)
    private Button cancelButton;
    private boolean isDestroyed = false;


//    public static Intent intent(final Context context) {
//        return new Intent(context, FirmwareUpdateActivity.class);
//    }


    public void setMessageText(int resourceId) {
        message_text.setText(resourceId);
    }
    public void setMessageText(String text) {
        message_text.setText(text);
    }
    public void setProgressText(int resourceId){
        progress_text.setText(resourceId);
    }

    public void setCancelButtonText(int resourdeId){
        cancelButton.setText(resourdeId);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);
        ButterKnife.bind(this);
        getPresenter().setCurrentDev(getIntent().getExtras().getParcelable(Constants.EXTRA_SELECTED_DEVICE));
        setResult(RESULT_CANCELED);

    }
    @OnClick(R.id.button)
    public void cancel(){
        isDestroyed = true;
        //result is set
        getPresenter().cancelActualTask();
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            progress_text.setText(R.string.progress_bluetooth_off);
            message_text.setText(R.string.enable_bt_and_try_again);
        } else {
            //actualTask = new LoadFirmwareTask().execute();
            //rx observerable replacement
            getPresenter().downloadFirmwareExecution();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().cancelActualTask();
    }



    public void showFirmwareScreen_LoadingVersionInfo() {
        progress_text.setText(R.string.progress_contacting);
        message_text.setText(R.string.downloading_fw);
    }



    public boolean wasDestroyed() {
        return isDestroyed;
    }

//    @Override
//    public void onAdapterEvent(AdapterEvent event) {
//        Log.d(TAG, "onAdapterEvent: " + event);
//
//        switch (event) {
//            case ADAPTER_DISABLED:
//                showBluetoothConnectionLostScreen();
//                break;
//            case ADAPTER_DISABLING:
//
//                break;
//            case ADAPTER_ENABLING:
//
//                break;
//            case ADAPTER_IDLE:
//                Log.d(TAG, "Idle with action: " + event.getActionCode());
//                switch (event.getActionCode()) {
//                    case AdapterEvent.ACTION_IDLE_CONNECTION_FAILED_CRITICAL:
//                    case AdapterEvent.ACTION_IDLE_CONNECTION_FAILED:
//                    case AdapterEvent.ACTION_IDLE:
//                    case AdapterEvent.ACTION_IDLE_CONNECTION_FINISHED:
//                        if (!finishedUpdate)
//                            showFailedConnectingScreen();
//                        break;
//                    case AdapterEvent.ACTION_IDLE_CONNECTION_LOST:
//                        if (!terminalResetByApp && !finishedUpdate)
//                            showBluetoothConnectionLostScreen();
//                        break;
//                    case AdapterEvent.ACTION_IDLE_CONF_UPDATE_FAILED:
//                        showBluetoothConnectionLostScreen();
//                        break;
//                }
//                break;
//            default:
//                break;
//        }
//    }

    public void showFailedConnectingScreen() {
        progress_text.setText(' ');
        message_text.setText(R.string.bt_pairing_failed);
    }

    public void showBluetoothConnectionLostScreen() {
        progress_text.setText(' ');
        message_text.setText(R.string.bt_pairing_connection_lost);
    }

//    @Override
//    public void onConnectionStarted() {
//        Log.d(TAG, "onConnectionStarted");
//        progress_text.setText(R.string.progress_contacting);
//        message_text.setText(R.string.bt_pairing_connecting);
//    }

//    @Override
//    public void onTerminalEvent(TerminalEvent action) {
//        Log.d(TAG, "onTerminalEvent: " + action);
//        switch (action) {
//            case CONFIG_UPDATE_FINISHED:
//
//                break;
//            case CONFIG_UPDATE_STARTED:
//                progress_text.setText(R.string.processing);
//                message_text.setText(R.string.installing_fw);
//                break;
//            case FIRMWARE_UPDATE_AVAILABLE:
//
//                break;
//            case FIRMWARE_UPDATE_FINISHED:
//                progress_text.setText(" ");
//                message_text.setText(R.string.fw_install_success);
//                finishedUpdate = true;
//                if (restarted)
//                    AcceptSDK.saveCurrentVersionOfFirmwareInBackend(null);
//
//                cancelButton.setText(R.string.wl_general_done);
//                setResult(RESULT_OK);
//                break;
//            case FIRMWARE_UPDATE_STARTED:
//                progress_text.setText(R.string.processing);
//                message_text.setText(getString(R.string.wl_general_firmware_update_pending,
//                        AcceptSDK.getTerminalInfo() != null ? AcceptSDK.getTerminalInfo().firmwareVersion : "-",
//                        AcceptSDK.getCurrentVersionOfSavedFirmwareInBackend() != null ? AcceptSDK.getCurrentVersionOfSavedFirmwareInBackend().getFwNumber() : "-"));
//                break;
//            default:
//                break;
//        }
//    }

//    @Override
//    public void onConnectionEstablished(boolean restartRequired) {
//        Log.d(TAG, "connection established");
//        if (restartRequired && controller != null && !restarted) {
//            terminalResetByApp = true;
//            controller.restartDevice();
//            restarted = true;
//        }
//    }

//    @Override
//    public void onProcessStarted() {
//        //do nothing
//    }
//
//    @Override
//    public void onProcessUpdate(ProcessState state) {
//        //do nothing
//    }

//    @Override
//    public void onProcessFinished(ProcessResult result, Exception errorException) {
//        //do nothing
//    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "disconnecting from cnp controller");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getPresenter().disconnect();
        finish();
        super.onDestroy();
    }



    @Override
    public void onPause() {
        super.onPause();
        //unregister
        getPresenter().unregisterController();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        getPresenter().cancelActualTask();
        super.onBackPressed();
    }
}
