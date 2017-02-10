package com.wirecard.accept.activities.base;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wirecard.accept.help.Constants;
import com.wirecard.accept.help.RxHelper;
import com.wirecard.accept.rx.receivers.RxBroadcastReceiver;

import icepick.Icepick;
import nucleus.presenter.Presenter;
import nucleus.view.NucleusActivity;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**

 */
public abstract class BaseActivity<P extends Presenter> extends NucleusActivity<P> {
    final String TAG = BaseActivity.class.getSimpleName();
    public final static int TYPE_LOGOUT = 1;
    private Subscription receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        receiver = RxBroadcastReceiver.create(this, new IntentFilter(Constants.INTENT))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(intent -> {
                    //Broadcast receiver onReceive:
                    Bundle extras = null;
                    if (intent != null)
                        extras = intent.getExtras();
                    if (extras != null) {
                        switch (extras.getInt(Constants.INTENT_TYPE)) {
                            case TYPE_LOGOUT:
                                // Kill receiving activity
                                Log.e(TAG, ">>>>>>>>>>>>> LOGOUT <<<<<<<<<<<<<<<<");
                                finish();
                                break;
                            default:
                                break;
                        }
                    }
                });
        Icepick.restoreInstanceState(this, savedInstanceState);
//        ButterKnife.bind(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.restoreInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxHelper.unsubscribe(receiver);
    }
}
