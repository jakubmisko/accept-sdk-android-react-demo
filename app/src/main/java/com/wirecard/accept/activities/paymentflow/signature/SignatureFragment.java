package com.wirecard.accept.activities.paymentflow.signature;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseFragment;
import com.wirecard.accept.rx.dialog.RxDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Created by super on 06.01.2017.
 */
@RequiresPresenter(SignaturePresenter.class)
public class SignatureFragment extends BaseFragment<SignaturePresenter> {
    @BindView(R.id.signature)
    SignatureView signatureView;
    @BindView(R.id.confirm_signature)
    Button confirm;
    @BindView(R.id.cancel_signature_confirmation)
    Button cancel;

    private ConfirmRequestWrapper confirmRequestWrapper;

    public static SignatureFragment newInstance(ConfirmRequestWrapper confirmRequestWrapper) {
        SignatureFragment fragment = new SignatureFragment();
        fragment.setSignatureRequest(confirmRequestWrapper);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_signature, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.confirm_signature)
    public void confirmHandle() {
        if (signatureView.isSomethingDrawn()) {
//            Bundle sign = new Bundle();
//            signatureView.serialize(sign);
            if(confirmRequestWrapper.requireSignature()) {
                confirmRequestWrapper.confirm(signatureView.compressSignatureBitmapToPNG());
            } else {
                confirmRequestWrapper.confirm(null);
            }
        } else {
            //TODO check for leakage, maybe unsubscribe needed
            RxDialog.create(getActivity(), R.string.acceptsdk_dialog_nothing_drawn_title, R.string.acceptsdk_dialog_nothing_drawn_message, android.R.string.ok).subscribe();
        }
    }

    @OnClick(R.id.cancel_signature_confirmation)
    public void cancelHandle() {
        RxDialog.create(getActivity(), R.string.acceptsdk_dialog_cancel_signature_request_title, R.string.acceptsdk_dialog_cancel_signature_request_message,
                android.R.string.yes, android.R.string.no).filter(btn -> true).subscribe(click -> {
            confirmRequestWrapper.cancel();
        });
    }

    public void setSignatureRequest(ConfirmRequestWrapper confirmRequestWrapper) {
        this.confirmRequestWrapper = confirmRequestWrapper;
    }

    public void hideButtons(){
        confirm.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
    }
}
