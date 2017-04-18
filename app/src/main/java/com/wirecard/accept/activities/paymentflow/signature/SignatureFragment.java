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
import com.wirecard.accept.rx.dialog.RxAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * hadling of signature capture and confirmation
 */
public class SignatureFragment extends BaseFragment {
    @BindView(R.id.signature)
    SignatureView signatureView;
    @BindView(R.id.confirm)
    Button confirm;
    @BindView(R.id.cancel)
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

    @OnClick(R.id.confirm)
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
            RxAlertDialog.createAlert(getActivity(), R.string.acceptsdk_dialog_nothing_drawn_message, android.R.string.ok).subscribe();
        }
    }

    @OnClick(R.id.cancel)
    public void cancelHandle() {
        //when you try to cancel signature show confirmation dialog
        RxAlertDialog.createAlert(getActivity(), R.string.acceptsdk_dialog_cancel_signature_request_message, R.string.yes, R.string.no)
                //filter only positive button and ignore negative
                .filter(btn -> btn)
                .subscribe(click -> confirmRequestWrapper.cancel());
    }

    public void setSignatureRequest(ConfirmRequestWrapper confirmRequestWrapper) {
        this.confirmRequestWrapper = confirmRequestWrapper;
    }

    /**
     * hide button when it's signature confirmation, use terminal to confirm
     */
    public void hideButtons(){
        confirm.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
    }

    public void showProgress(){
        if(barProgressDialog == null) {
            barProgressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
//            barProgressDialog.setTitle("Signature");
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
