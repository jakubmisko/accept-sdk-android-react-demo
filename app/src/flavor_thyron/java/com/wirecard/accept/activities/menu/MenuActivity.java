package com.wirecard.accept.activities.menu;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.update.FirmwareUpdateFragment;
import com.wirecard.accept.exceptions.DeviceDiscoverException;
import com.wirecard.accept.help.DiscoverDevices;
import com.wirecard.accept.rx.dialog.RxAlertDialog;

import java.util.List;

import de.wirecard.accept.extension.thyron.ThyronBluetoothDevice;
import de.wirecard.accept.extension.thyron.ThyronUsbDevice;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import nucleus.factory.RequiresPresenter;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Menu for spire terminal flavour including firmware update
 */

@RequiresPresenter(MenuPresenter.class)
public class MenuActivity extends AbstractMenuActivity<MenuPresenter> {
    private final int REQUEST_ENABLE_BT = 1;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected boolean handleMenuItemClick(MenuItem menuItem) {
        if (!super.handleMenuItemClick(menuItem) && menuItem.getItemId() == R.id.fw_update) {
            toolbar.setTitle("Firmware update");

            getPresenter().discoverDevices(this);
        }
        return false;
    }

    /* discover devices */
    public void presentDiscoveryError(DeviceDiscoverException excpetion) {
        //stop previous task to prevent restarting when process restarts
        getPresenter().stop(MenuPresenter.DISCOVER_DEVICES);
        //Check DiscoveryError enum (exception.getDiscoveryError) and handle all states
        Snackbar.make(drawerLayout, R.string.bt_enable, Snackbar.LENGTH_LONG)
                .setAction("ENABLE", v -> enableBluetooth())
                .show();
    }

    public void presentDiscoveredDevices(List<PaymentFlowController.Device> devices) {
        //stop previous task to prevent restarting when process restarts
        getPresenter().stop(MenuPresenter.DISCOVER_DEVICES);
        if (devices == null || devices.isEmpty()) {
            Snackbar.make(drawerLayout, R.string.pair_terminal, Snackbar.LENGTH_LONG)
                    .setAction("PAIR", v -> startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 0))
                    .show();

        } else {
            //>>> prepare data phase <<<
            //this shows selector...but this is usually done in your app... just demo app have to handle it before, because we need current used Device
            terminalChooser(devices);
        }
    }

    public void presentSuccessfulConnect(boolean withReboot) {
        if (withReboot) {
            //here you can display some message like terminal will be restarted after configuration update
            Snackbar.make(drawerLayout, R.string.fw_config_success_now_reboot, Snackbar.LENGTH_LONG)
                    .show();
        }
        Snackbar.make(drawerLayout, R.string.fw_config_success_continue, Snackbar.LENGTH_LONG).show();
    }

    public void presentBluetoothError() {
        Toast.makeText(this, R.string.menu_bt_error, Toast.LENGTH_LONG).show();
    }

    public void presentError(String technicalMessage) {
        Log.e(TAG, "checkDeviceIdentity:\n" + technicalMessage);
        Snackbar.make(drawerLayout, getString(R.string.menu_device_identity_error, technicalMessage), Snackbar.LENGTH_LONG).show();
    }

    private void terminalChooser(List<PaymentFlowController.Device> devices) {
        String[] devicesNames = DiscoverDevices.getDeviceNames(devices);
        RxAlertDialog.create(this, R.string.acceptsdk_dialog_terminal_chooser_title, android.R.string.cancel, devicesNames)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        chosenDevice -> getPresenter().checkDeviceIdentity(devices.get(chosenDevice)),
                        cancel -> finish()
                );
    }

    /* firmware version check */
    public void gotToFirmwareUpdate(String deviceId) {
        Parcelable device = isUsbDevice() ? new ThyronUsbDevice(null, deviceId) : new ThyronBluetoothDevice(null, deviceId);
        FirmwareUpdateFragment firmwareUpdateFragment = FirmwareUpdateFragment.newInstance(device);
        recplaceFragment(firmwareUpdateFragment);
    }

    public void presentVersionCheckStarted() {
        Snackbar.make(drawerLayout, R.string.menu_fw_version_check, Snackbar.LENGTH_LONG).show();
    }

    public void presentWrongBeData() {
        Snackbar.make(drawerLayout, R.string.menu_fw_check_wrong_be_data, Snackbar.LENGTH_LONG).show();
    }

    public void presentUpdateNotNeeded() {
        Snackbar.make(drawerLayout, R.string.menu_fw_check_update_not_needed, Snackbar.LENGTH_LONG).show();
    }

    public void presentFwVersionError() {
        Snackbar.make(drawerLayout, R.string.menu_fw_check_net_con, Snackbar.LENGTH_LONG).show();
    }

    private void enableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
