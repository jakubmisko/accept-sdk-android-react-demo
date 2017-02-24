package com.wirecard.accept.uicomponents.signature;

/**
 * Created by super on 23.02.2017.
 */

public interface SignatureComponent {
    boolean isSomethingDrawn();
    byte[] getPNG();
}
