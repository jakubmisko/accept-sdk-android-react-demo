package com.wirecard.accept.activities.menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.update.FirmwareUpdateActivity;
import com.wirecard.accept.exceptions.DeviceDiscoverException;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.help.DiscoverDevices;
import com.wirecard.accept.rx.dialog.RxDialog;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.wirecard.accept.extension.thyron.ThyronBluetoothDevice;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import nucleus.factory.RequiresPresenter;
import rx.android.schedulers.AndroidSchedulers;

//import de.wirecard.accept.extension.thyron.ThyronDevice;

/**
 * Created by jakub on 10.04.2016.
 */

//todo consider using snackbars
@RequiresPresenter(MenuPresenter.class)
public class MenuActivity extends AbstractMenuActivity<MenuPresenter> {
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.firmwareUpdate)
    public void firmwareUpdate() {
        getPresenter().discoverDevices(this);
    }

    /* discover devices */
    public void presentDiscoveryError(DeviceDiscoverException excpetion) {
        //Check DiscoveryError enum (exception.getDiscoveryError) and handle all states
        Toast.makeText(this, getString(R.string.bt_enable), Toast.LENGTH_LONG).show();
    }

    public void presentDiscoveredDevices(List<PaymentFlowController.Device> devices) {
        if (devices == null || devices.isEmpty()) {
            Toast.makeText(this, getString(R.string.pair_terminal), Toast.LENGTH_LONG).show();
        } else {
            //>>> prepare data phase <<<
            //this shows selector...bud this is usually done in your app... just demo app have to handle it before, because we need current used Device
            terminalChooser(devices);
        }
    }

    public void presentSuccessfulConnect(boolean withReboot){
        if (withReboot) {
            //here you can display some message like terminal will be restarted after configuration update
            Toast.makeText(this, getString(R.string.fw_config_success_now_reboot), Toast.LENGTH_LONG).show();
        }
        Toast.makeText(this, getString(R.string.fw_config_success_continue), Toast.LENGTH_LONG).show();
    }

    public void presentBluetoothError(){
        Toast.makeText(this, R.string.menu_bt_error, Toast.LENGTH_LONG).show();
    }

    public void presentError(String technicalMessage){
        Log.e(TAG, "checkDeviceIdentity:\n"+technicalMessage);
        Toast.makeText(this, getString(R.string.menu_device_identity_error, technicalMessage), Toast.LENGTH_LONG).show();
    }

    private void terminalChooser(List<PaymentFlowController.Device> devices) {
        String[] devicesNames = DiscoverDevices.getDeviceNames(devices);
        RxDialog.create(this, R.string.acceptsdk_dialog_terminal_chooser_title, android.R.string.cancel, devicesNames)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chosenDevice -> {
                            getPresenter().checkDeviceIdentity(devices.get(chosenDevice));
                        },
                        cancel -> {
                            finish();
                        });
    }
    /* firmware version check */
    public void gotToFirmwareUpdate(String deviceId) {
        //TODO put usb alternative
        startActivityForResult(new Intent(this, FirmwareUpdateActivity.class).putExtra(Constants.EXTRA_SELECTED_DEVICE, new ThyronBluetoothDevice(null, deviceId) /*new ThyronDevice(deviceId)*/), Constants.REQUEST_FIRMWARE_UPDATE);
    }

    public void presentVersionCheckStarted(){
        Toast.makeText(this, R.string.menu_fw_version_check, Toast.LENGTH_LONG).show();
    }

    public void presentWrongBeData(){
        Toast.makeText(this, R.string.menu_fw_check_wrong_be_data, Toast.LENGTH_LONG).show();
    }

    public void presentUpdateNotNeeded(){
        Toast.makeText(this, R.string.menu_fw_check_update_not_needed, Toast.LENGTH_LONG).show();
    }

    public void presentFwVersionError(){
        Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_LONG).show();
    }
}