package com.wirecard.accept.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.wirecard.accept.R;

import java.util.ArrayList;
import java.util.List;

import de.wirecard.accept.sdk.extensions.PaymentFlowController;

/**
 * Created by jakub.misko on 13. 4. 2016.
 */
public class PaymentFlowDialogs {

    public interface DeviceToStringConverter<T> {
        String displayNameForDevice(T device);
    }

    public interface TerminalChooserListener<T> {
        void onDeviceSelected(T device);
        void onSelectionCanceled();
    }

    public interface SignatureRequestCancelListener {
        void onSignatureRequestCancellationConfirmed();
        void onSignatureRequestCancellationSkipped();
    }

    public interface SignatureConfirmationListener {
        void onSignatureConfirmedIsOK();
        void onSignatureConfirmedIsNotOK();
    }

    public static void showTerminalDiscoveryError(final Context context, final PaymentFlowController.DiscoveryError discoveryError, final String technicalMessage, final View.OnClickListener confirmedClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.acceptsdk_dialog_discovery_error_title)
                .setMessage(context.getString(R.string.acceptsdk_dialog_discovery_error_message, discoveryError + " - " + technicalMessage))
                .setCancelable(false /* important */)
                .setPositiveButton(R.string.acceptsdk_dialog_discovery_error_confirm, (dialog, which) -> {
                    confirmedClickListener.onClick(null);
                }).create().show();
    }

    public static void showNothingDrawnWarning(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.acceptsdk_dialog_nothing_drawn_title)
                .setMessage(R.string.acceptsdk_dialog_nothing_drawn_message)
                .setCancelable(false /* important */)
                .setPositiveButton(R.string.acceptsdk_dialog_nothing_drawn_confirm, null)
                .create().show();
    }

    public static void showConfirmSignatureRequestCancellation(final Context context, final SignatureRequestCancelListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.acceptsdk_dialog_cancel_signature_request_title)
                .setMessage(R.string.acceptsdk_dialog_cancel_signature_request_message)
                .setCancelable(false /* important */)
                .setPositiveButton(R.string.acceptsdk_dialog_cancel_signature_request_confirm, (dialog, which) -> {
                    if (listener != null) listener.onSignatureRequestCancellationConfirmed();
                }).setNegativeButton(R.string.acceptsdk_dialog_cancel_signature_request_skip, (dialog, which) -> {
                    if (listener != null) listener.onSignatureRequestCancellationSkipped();
                }).create().show();
    }

    public static Dialog showSignatureConfirmation(final Context context, Bitmap signature, boolean showButtons, final SignatureConfirmationListener listener) {
        final View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_for_sign_confirm, null);
        ((ImageView)contentView.findViewById(R.id.image)).setImageBitmap(signature);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.acceptsdk_dialog_signature_confirm_title)
                .setView(contentView)
                .setCancelable(false /* important */);

        if(showButtons) {
            builder.setPositiveButton("OK", (dialog, which) -> {
                listener.onSignatureConfirmedIsOK();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                listener.onSignatureConfirmedIsNotOK();
            });
        }
        final Dialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static void showSignatureInstructions(final Context context, final View.OnClickListener confirmationClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.acceptsdk_dialog_signature_instruction_title)
                .setMessage(R.string.acceptsdk_dialog_signature_instruction_message)
                .setCancelable(false /* important */)
                .setPositiveButton(R.string.acceptsdk_dialog_signature_instruction_confirm, (dialog, which) -> {
                    if ( confirmationClickListener != null ) confirmationClickListener.onClick(null);
                })
                .create().show();
    }

    public static void showNoDevicesError(final Context context, final View.OnClickListener confirmedClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.acceptsdk_dialog_no_terminals_title)
                .setMessage(R.string.acceptsdk_dialog_no_terminals_message)
                .setCancelable(false /* important */)
                .setPositiveButton(R.string.acceptsdk_dialog_no_terminals_confirm, (dialog, which) -> {
                    if ( confirmedClickListener != null ) confirmedClickListener.onClick(null);
                }).create().show();
    }

    public static <T> void showTerminalChooser(Context context, final List<T> devices, DeviceToStringConverter<T> converter, final TerminalChooserListener<T> listener) {
        final List<CharSequence> convertedNames = new ArrayList<>();
        for ( T device : devices ) {
            convertedNames.add(converter.displayNameForDevice(device));
        }
        new AlertDialog.Builder(context)
                .setTitle(R.string.acceptsdk_dialog_terminal_chooser_title)
                .setCancelable(false /* important */)
                .setPositiveButton(R.string.acceptsdk_dialog_terminal_chooser_cancel, (dialog, which) -> {
                    if (listener != null) listener.onSelectionCanceled();
                })
                .setSingleChoiceItems(convertedNames.toArray(new CharSequence[convertedNames.size()]), -1, (dialog, which) -> {
                    dialog.dismiss();
                    if (listener != null) listener.onDeviceSelected(devices.get(which));
                }).create().show();
    }

    public static void showPaymentFlowError(Context context, PaymentFlowController.Error paymentFlowError, final String technicalMessage, final View.OnClickListener confirmClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.acceptsdk_dialog_payment_error_title)
                .setCancelable(false /* important */)
                .setMessage(context.getString(R.string.acceptsdk_dialog_payment_error_message, paymentFlowError + " - " + technicalMessage))
                .setPositiveButton(R.string.acceptsdk_dialog_payment_error_confirm, (dialog, which) -> {
                    confirmClickListener.onClick(null);
                }).create().show();
    }
}
