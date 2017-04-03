package com.wirecard.accept.activities.paymentflow.signature;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseFragment;
import com.wirecard.accept.rx.dialog.RxDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * hadling of signature capture and confirmation
 */
public class SignatureFragment extends BaseFragment {
    @BindView(R.id.signature)
    SignatureView signatureView;
    @BindView(R.id.confirm_signature)
    Button confirm;
    @BindView(R.id.cancel_signature_confirmation)
    Button cancel;
    @BindView(R.id.label)
    TextView label;

    private ConfirmRequestWrapper confirmRequestWrapper;
    private ProgressDialog barProgressDialog;

    public static SignatureFragment newInstance(ConfirmRequestWrapper confirmRequestWrapper) {
        SignatureFragment fragment = new SignatureFragment();
        fragment.setSignatureRequest(confirmRequestWrapper);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_signature, container, false);
        //apply binding to ui fields
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.confirm_signature)
    public void confirmHandle() {
        //check if signature isn't blank
        if (signatureView.isSomethingDrawn()) {
            //when request requires data then compress bitmap to png byte array
            if(confirmRequestWrapper.requireSignature()) {
                confirmRequestWrapper.confirm(signatureView.compressSignatureBitmapToPNG());
            } else {
                //pass null when it's just signature confirmation
                confirmRequestWrapper.confirm(null);
            }
            showProgress();
        } else {
            //TODO check for leakage, maybe unsubscribe needed
            RxDialog.create(getActivity(), R.string.acceptsdk_dialog_nothing_drawn_title, R.string.acceptsdk_dialog_nothing_drawn_message, android.R.string.ok).subscribe();
        }
    }

    @OnClick(R.id.cancel_signature_confirmation)
    public void cancelHandle() {
        //when you try to cancel signature show confirmation dialog
        RxDialog.create(getActivity(), R.string.acceptsdk_dialog_cancel_signature_request_title, R.string.acceptsdk_dialog_cancel_signature_request_message,
                android.R.string.yes, android.R.string.no).filter(btn -> true).subscribe(click -> {
            confirmRequestWrapper.cancel();
        });
    }

    public void setSignatureRequest(ConfirmRequestWrapper confirmRequestWrapper) {
        this.confirmRequestWrapper = confirmRequestWrapper;
    }

    /**
     * hide button when it's signature confirmation, use terminal to confirm
     */
    public void hideButtons(){
        confirm.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
    }

    public void showProgress(){
        if(barProgressDialog == null) {
            barProgressDialog = new ProgressDialog(getActivity());
            barProgressDialog.setTitle("Signature");
            barProgressDialog.setMessage("Uploading signature ...");
            barProgressDialog.setIndeterminate(true);
        }
        barProgressDialog.show();
    }

    public void dissmissProgress(){
        if(barProgressDialog != null){
            barProgressDialog.dismiss();
        }
    }

    public void setLabel(String text){
        label.setText(text);
    }
}
