package com.wirecard.accept.menu;

import android.os.Bundle;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.AbstractMenuActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Created by jakub on 10.04.2016.
 */
@RequiresPresenter(MenuPresenter.class)
public class MenuActivity extends AbstractMenuActivity<MenuPresenter> {
    private static final String TAG = "MenuActivity";

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
