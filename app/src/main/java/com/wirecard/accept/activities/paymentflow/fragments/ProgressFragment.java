package com.wirecard.accept.activities.paymentflow.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wirecard.accept.R;

import butterknife.BindView;

/**
 * Created by super on 30.11.2016.
 */

public class ProgressFragment extends Fragment {
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.status)
    TextView status;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.progress_fragment, container, false);
    }

    private void showProgress(final int messageRes, final boolean showProgress) {
        showProgress(messageRes == -1 ? "" : getString(messageRes), showProgress);
    }

    private void showProgress(final String message, final boolean showProgress) {
            progressBar.setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
            status.setText(message);
    }
}
