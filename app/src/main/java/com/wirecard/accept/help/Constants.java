package com.wirecard.accept.help;

/**
 * Constants used in application
 */
public interface Constants {
    String TEXT = "TEXT";
    String LOGOUT = "logout";
    String INTENT = "acceptsdk_intent";
    String INTENT_TYPE = "acceptsdk_intent_type";
    String EXTRA_SELECTED_DEVICE = "selected_device";
    String SEPA = "sepa";


    int INTENT_TYPE_LOGOUT = 1;
    int REQUEST_FIRMWARE_UPDATE = 11;

    String RECEIPT = "receipt";
    String REVERSE_REFUND = "reverse/refund";

    //signature view
    String SIGNATURE = "sign";
    //payment flow
    int REQUEST_SIGNATURE = 99;
    String INITIAL_MESSAGE = "initial";
    String SHOW_PROGRESSBAR = "progress";
    String PAYMENT_FRAGMENT_TAG = "pay_frag";
    String SINGATURE_FRAGMENT_TAG = "sign_frag";

}
