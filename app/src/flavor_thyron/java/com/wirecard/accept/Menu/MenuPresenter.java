package com.wirecard.accept.menu;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.dialogs.PaymentFlowDialogs;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.update.FirmwareUpdateActivity;

import java.util.List;

import butterknife.BindView;
import de.wirecard.accept.extension.refactor.AcceptThyronPaymentFlowController;
import de.wirecard.accept.extension.thyron.ThyronDevice;
import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.extensions.PaymentFlowController;
import nucleus.presenter.Presenter;

/**
 * Created by jakub on 24.06.2016.
 */
public class MenuPresenter extends Presenter<MenuActivity> implements FirmwareActivityStart {
    @BindView(R.id.firmwareUpdate)
    Button firmwareUpdateButton;
    private Context context;
    private PaymentFlowController.Device device;
    private FirmwareCheck fwCheck;

    public void showFirmwareActivity() {
        getView().startActivityForResult(new Intent(getView(), FirmwareUpdateActivity.class)
                        .putExtra(Constants.EXTRA_SELECTED_DEVICE, new ThyronDevice(device.id))
                , Constants.REQUEST_FIRMWARE_UPDATE);
    }

    public void firmwareUpdate() {
        AcceptSDK.saveCurrentVersionOfFirmwareInBackend(null);//clear remembered data
        //SDK is remembering versions per login
        showSpireBoundedDevicesChooserDialog();
    }

    private void showSpireBoundedDevicesChooserDialog() {
        if (getView() != null) {
            context = getView().getApplicationContext();
        }
        final AcceptThyronPaymentFlowController controller = new AcceptThyronPaymentFlowController(false, true);

        //like first we have to call discover devices to get list of paired device from smartphone
        controller.discoverDevices(context, new PaymentFlowController.DiscoverDelegate() {
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
                //>>> prepare data phase <<<
                //this shows selector...bud this is usually done in your app... just demo app have to handle it before, because we need current used Device
                PaymentFlowDialogs.showTerminalChooser(getView(), list,
                        device1 -> {
                            if (TextUtils.isEmpty(device1.displayName)) {
                                return device1.id;
                            }
                            return device1.displayName;
                        },
                        new PaymentFlowDialogs.TerminalChooserListener<PaymentFlowController.Device>() {
                            @Override
                            public void onDeviceSelected(PaymentFlowController.Device selectedDevice) {
                                device = selectedDevice;

                                //this method is added only for support compatibility beween this (reviewed)SDK and new SDK 2.0
                                //method start communication to get some basic terminal information (which we can compare with data from server)
                                //in the new SDK it will be something like communication initialisation method

                                //we have to simulate first connect to terminal and on succesfull connection event lets start wit real firmware update
                                // sometimes is needed try again because restart-hardware related feature related to upload configuration during first connect,
                                // in real implementation should be checkDeviceIdentity used only one time and best after the login into app.
                                // therefore firmware update should be implemented in app like "behind" checkDeviceIdentity =first connect with terminal(in the settings screen for example)
                                controller.checkDeviceIdentity(device, new AcceptThyronPaymentFlowController.SimpleConnectListener() {
                                    @Override
                                    public void onSuccessfulConnect(boolean withRestart) {
                                        if (withRestart) {
                                            //here you can display some message like terminal will be restarted after configuration update
                                            Toast.makeText(context, context.getString(R.string.fw_config_success_now_reboot), Toast.LENGTH_LONG).show();
                                        }
                                        Toast.makeText(context, context.getString(R.string.fw_config_success_continue), Toast.LENGTH_LONG).show();

                                        //>>> start of firmware update<<<
                                        //start async task for checking firmware version on server
                                        //new FirmwareCheckTask().execute();
                                        runFirmwareCheckTask(device);
                                    }

                                    @Override
                                    public void onBluetoothConnectionError() {
                                        Toast.makeText(context, context.getString(R.string.bt_connection_error), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onError(String technicalMessage) {
                                        Log.e("checkDeviceIdentity", technicalMessage);
                                        //TODO resource with placeholder
                                        Toast.makeText(context, "Check device identity error code : " + technicalMessage, Toast.LENGTH_LONG).show();
                                    }
                                });


                            }

                            @Override
                            public void onSelectionCanceled() {
                                getView().finish();
                            }
                        });
            }
        });
    }

    private void runFirmwareCheckTask(PaymentFlowController.Device device) {
        fwCheck = new FirmwareCheck(this, context);
        fwCheck.execute(device);
    }

    @Override
    protected void onDropView() {
        super.onDropView();
        if (fwCheck != null)
            fwCheck.cancel();
    }
}
