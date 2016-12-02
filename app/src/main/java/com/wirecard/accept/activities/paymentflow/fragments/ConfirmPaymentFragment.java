package com.wirecard.accept.activities.paymentflow.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wirecard.accept.R;

/**
 * Created by jakub.misko on 2. 12. 2016.
 */

public class ConfirmPaymentFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.confirm_payment_fragment, container, false);
    }
}
