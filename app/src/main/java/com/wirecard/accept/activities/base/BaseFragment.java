package com.wirecard.accept.activities.base;

import android.os.Bundle;

import icepick.Icepick;
import nucleus.presenter.Presenter;
import nucleus.view.NucleusFragment;

/**
 * Created by super on 18.12.2016.
 */

public class BaseFragment<P extends Presenter> extends NucleusFragment<P> {
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Icepick.saveInstanceState(this, bundle);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Icepick.restoreInstanceState(this, bundle);
    }
}
