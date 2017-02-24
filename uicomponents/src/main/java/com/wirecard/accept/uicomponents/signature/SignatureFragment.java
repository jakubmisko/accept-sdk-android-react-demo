package com.wirecard.accept.uicomponents.signature;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wirecard.accept.uicomponents.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

/**
 * hadling of signature capture and confirmation
 */
public class SignatureFragment extends Fragment implements SignatureComponent {

    private SignatureView signatureView;
    private Button confirm;
    private Button cancel;

//    private ConfirmRequestWrapper confirmRequestWrapper;
//
//    public static SignatureFragment newInstance(ConfirmRequestWrapper confirmRequestWrapper) {
//        SignatureFragment fragment = new SignatureFragment();
//        fragment.setSignatureRequest(confirmRequestWrapper);
//        return fragment;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //default layout
        LinearLayout root = new LinearLayout(getActivity());
        root.setOrientation(VERTICAL);
        //label on the top
        TextView label = new TextView(getActivity());
        label.setText(R.string.label_sign_in);
        label.setGravity(Gravity.CENTER);
        label.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        //confirm and cancel buttons
        LinearLayout buttons = new LinearLayout(getActivity());
        buttons.setOrientation(HORIZONTAL);
        confirm = new Button(getActivity());
        confirm.setText(R.string.button_confirm);
        confirm.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        cancel.setText(R.string.button_cancel);
        cancel.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
//       weight=1??
        //signature view
        signatureView = new SignatureView(getActivity());
        int signatureHeight = root.getWidth() - label.getWidth() - buttons.getWidth();
        signatureView.setLayoutParams(new LayoutParams(MATCH_PARENT, signatureHeight));

        root.addView(label);
        root.addView(signatureView);
        root.addView(buttons);
        return root;
    }

//    @OnClick(R.id.confirm_signature)
//    public void confirmHandle() {
//        //check if signature isn't blank
//        if (signatureView.isSomethingDrawn()) {
//            //when request requires data then compress bitmap to png byte array
//            if(confirmRequestWrapper.requireSignature()) {
//                confirmRequestWrapper.confirm(signatureView.compressSignatureBitmapToPNG());
//            } else {
//                //pass null when it's just signature confirmation
//                confirmRequestWrapper.confirm(null);
//            }
//        } else {
//            //TODO check for leakage, maybe unsubscribe needed
//            RxDialog.create(getActivity(), R.string.acceptsdk_dialog_nothing_drawn_title, R.string.acceptsdk_dialog_nothing_drawn_message, android.R.string.ok).subscribe();
//        }
//    }
//
//    @OnClick(R.id.cancel_signature_confirmation)
//    public void cancelHandle() {
//        //when you try to cancel signature show confirmation dialog
//        RxDialog.create(getActivity(), R.string.acceptsdk_dialog_cancel_signature_request_title, R.string.acceptsdk_dialog_cancel_signature_request_message,
//                android.R.string.yes, android.R.string.no).filter(btn -> true).subscribe(click -> {
//            confirmRequestWrapper.cancel();
//        });
//    }
//
//    public void setSignatureRequest(ConfirmRequestWrapper confirmRequestWrapper) {
//        this.confirmRequestWrapper = confirmRequestWrapper;
//    }

    /**
     * hide button when it's signature confirmation, use terminal to confirm
     */
    public void hideButtons() {
        confirm.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
    }

    @Override
    public boolean isSomethingDrawn() {
        return signatureView.isSomethingDrawn();
    }

    @Override
    public byte[] getPNG() {
        return signatureView.compressSignatureBitmapToPNG();
    }
}
