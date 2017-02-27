package com.wirecard.accept.uicomponents.signature;

/**
 * Created by jakub.misko on 27. 2. 2017.
 */

public interface SignatureContract {
    void onSignatureFinished(byte[] sinature);
    void onNotSigned();
    void onSignatureCancel();
}
