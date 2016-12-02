package com.wirecard.accept.dialogs;

/**
 * Created by super on 24.11.2016.
 */

public interface TerminalChooserListener<T> {
    void onDeviceSelected(T device);
    void onSelectionCanceled();
}
