package com.wirecard.accept.uicomponents.signature;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wirecard.accept.uicomponents.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static com.wirecard.accept.uicomponents.Preconditions.nullCheck;

/**
 * Created by jakub.misko on 8. 3. 2017.
 */

public class SignatureFragmentDialog extends DialogFragment implements SignatureComponent {
    private SignatureView signatureView;
    private Button confirm;
    private Button cancel;
    private SignatureConfirmContract signatureConfirmContract;
    private SignatureContract signatureContract;
    public boolean signed = false;

    /**
     * method to create fragment manually
     *
     * @param signatureConfirmContract callback for signature cofnirmation
     * @param signatureContract        callback for signature capture
     * @return signature fragment instance
     */
    public static SignatureFragmentDialog newInstance(SignatureContract signatureContract, SignatureConfirmContract signatureConfirmContract) {
        SignatureFragmentDialog fragment = new SignatureFragmentDialog();
        fragment.setSignatureContract(signatureContract);
        fragment.setSignatureConfirmContract(signatureConfirmContract);
        return fragment;
    }

    public void setSignatureConfirmContract(SignatureConfirmContract signatureConfirmContract) {
        this.signatureConfirmContract = signatureConfirmContract;
    }


    public void setSignatureContract(SignatureContract signatureContract) {
        this.signatureContract = signatureContract;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = buildLayout();
        View view = inflater.inflate(R.layout.fragment_signature, container, false);
        confirm = (Button) view.findViewById(R.id.confirm);
        cancel = (Button) view.findViewById(R.id.cancel);
        signatureView = (SignatureView) view.findViewById(R.id.signature);
        setListeners();
        return view;
    }

    @Deprecated
    @NonNull
    private View buildLayout() {
        //default layout
        LinearLayout root = new LinearLayout(getActivity());
        root.setOrientation(VERTICAL);
        //label on the top
        TextView label = new TextView(getActivity());
        label.setText(R.string.label_sign_in);
        label.setGravity(Gravity.CENTER);
        label.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        //confirm and cancel buttons
        LinearLayout buttons = new LinearLayout(getActivity());
        buttons.setOrientation(HORIZONTAL);
        confirm = new Button(getActivity());
        confirm.setText(R.string.button_confirm);
        confirm.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        cancel = new Button(getActivity());
        cancel.setText(R.string.button_cancel);
        cancel.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        buttons.addView(cancel);
        buttons.addView(confirm);
//       weight=1??
        //signature view
        signatureView = new SignatureView(getActivity());
        int signatureHeight = root.getWidth() - label.getWidth() - buttons.getWidth();
        signatureView.setLayoutParams(new ViewGroup.LayoutParams(1920, signatureHeight));

        root.addView(label);
        root.addView(signatureView);
        root.addView(buttons);

        //just for testing
        confirm.setId(R.id.button_confirm);
        cancel.setId(R.id.button_cancel);
        signatureView.setId(R.id.signature_view);
        return root;
    }

    public void setListeners() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signed) {
                    nullCheck(signatureConfirmContract);
                    signatureConfirmContract.onSignatureConfirm();
                }
                else {
                    nullCheck(signatureContract);
                    if (isSomethingDrawn()) {
                        signatureContract.onSignatureFinished(getPNG());
                        signed = true;
                    }
                    else {
                        signatureContract.onNotSigned();
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signed) {
                    signatureConfirmContract.onSignatureDecline();
                }
                else {
                    signatureContract.onSignatureCancel();
                }

            }
        });
    }

    /**
     * hide button when it's signature confirmation, use terminal to confirm
     */
    public void hideButtons() {
        confirm.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
    }

    @Override
    public boolean isSomethingDrawn() {
        nullCheck(signatureView);
        return signatureView.isSomethingDrawn();
    }

    @Override
    public byte[] getPNG() {
        nullCheck(signatureView);
        return signatureView.compressSignatureBitmapToPNG();
    }

    public interface SignatureConfirmContract {
        void onSignatureConfirm();
        void onSignatureDecline();
    }

    public interface SignatureContract {
        void onSignatureFinished(byte[] sinature);
        void onNotSigned();
        void onSignatureCancel();
    }
}
