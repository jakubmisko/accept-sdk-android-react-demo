package com.wirecard.accept.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.wirecard.accept.R;
import com.wirecard.accept.presenters.MenuPresenter;
import com.wirecard.accept.help.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.wirecard.accept.extension.thyron.ThyronDevice;
import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.backend.AcceptFirmwareVersion;
import nucleus.factory.RequiresPresenter;
import rx.Subscription;

/**
 * Created by jakub on 10.04.2016.
 */
@RequiresPresenter(MenuPresenter.class)
public class MenuActivity extends AbstractMenuActivity<MenuPresenter> {
    private static final String TAG = "MenuActivity";
    @BindView(R.id.firmwareUpdate)
    Button firmwareUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.firmwareUpdate)
    public void firmwareUpdate() {
        getPresenter().firmwareUpdate();
    }

}
