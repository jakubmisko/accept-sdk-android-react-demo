package com.wirecard.accept.update;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wirecard.accept.R;
import com.wirecard.accept.help.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusActivity;

/**
 * Created by jakub on 10.04.2016.
 */
//TODO extend base activity?
@RequiresPresenter(FirmwareUpdatePresenter.class)
public class FirmwareUpdateActivity extends NucleusActivity<FirmwareUpdatePresenter> {
    private static String TAG = FirmwareUpdateActivity.class.getSimpleName();
    @BindView(R.id.textViewMessage)
    TextView message_text;
    @BindView(R.id.textViewProgress)
    TextView progress_text;
    @BindView(R.id.button)
    Button cancelButton;
    private boolean isDestroyed = false;
    public void setMessageText(int resourceId) {
        message_text.setText(resourceId);
    }

    public void setMessageText(String text) {
        message_text.setText(text);
    }

    public void setProgressText(int resourceId) {
        progress_text.setText(resourceId);
    }

    public void setCancelButtonText(int resourdeId) {
        cancelButton.setText(resourdeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
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

    @Override
    protected void onStart() {
        super.onStart();

    }
    //TODO not very good solution
    public void showFirmwareScreen_LoadingVersionInfo() {
        progress_text.setText(R.string.progress_contacting);
        message_text.setText(R.string.downloading_fw);
    }
    public void showEnableBluetooth(){
        progress_text.setText(R.string.progress_bluetooth_off);
        message_text.setText(R.string.enable_bt_and_try_again);
    }


    public void showFailedConnectingScreen() {
        progress_text.setText(' ');
        message_text.setText(R.string.bt_pairing_failed);
    }

    public void showBluetoothConnectionLostScreen() {
        progress_text.setText(' ');
        message_text.setText(R.string.bt_pairing_connection_lost);
    }


    @Override
    protected void onDestroy() {
        // to the bottom?
        super.onDestroy();
        Log.d(TAG, "disconnecting from cnp controller");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getPresenter().disconnect();
        finish();
    }


    @Override
    public void onPause() {
        super.onPause();
        //unregister
        getPresenter().unregisterController();
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
}
