package com.wirecard.accept;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.wirecard.accept.activities.login.LoginActivity;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.rx.receivers.RxBroadcastReceiver;

import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.AcceptSDKIntents;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jakub.misko on 1. 4. 2016.
 */
public class Application extends android.app.Application {
    private String errorMessage = "";
    private Subscription receiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // Init loads the rest of configuration from config_for_accept.xml file.
        try {
            AcceptSDK.init(this,
                    BuildConfig.clientID,
                    BuildConfig.clientSecret,
                    BuildConfig.apiPath);
            AcceptSDK.loadExtensions(this, null);
        } catch (IllegalArgumentException e) {
            errorMessage = e.getMessage();
        }
        AcceptSDK.setPrefTimeout(15);//timeout for requests
        if (AcceptSDK.isLoggedIn()) {
            AcceptSDK.sessionRefresh((apiResult, stringStringHashMap) -> {
                        if (!apiResult.isSuccess()) {
                            sendLogoutIntentAndGoLogin();
                        }
                    }
            );
        }
        receiver = RxBroadcastReceiver.create(this, new IntentFilter(AcceptSDKIntents.SESSION_TERMINATED))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext -> {
                    //Broadcast receiver onReceive:
                    Log.e("Session Timeout", "sending Log Out");
                    sendLogoutIntentAndGoLogin();
                });
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void sendLogoutIntentAndGoLogin() {
        AcceptSDK.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constants.LOGOUT, true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        intent = new Intent(Constants.INTENT);
        intent.putExtra(Constants.INTENT_TYPE, Constants.INTENT_TYPE_LOGOUT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onTerminate() {
        AcceptSDK.finish();
        if (!receiver.isUnsubscribed()) {
            receiver.unsubscribe();
            receiver = null;
        }
        super.onTerminate();
    }
}
