package com.wirecard.accept.rx.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by super on 19.11.2016.
 */

public class RxBroadcastReceiver {
    private RxBroadcastReceiver() {
        throw new AssertionError("no instances");
    }

    @CheckResult
    @NonNull
    public static Observable<Intent> create(@NonNull final Context context,
                                            @NonNull final IntentFilter intentFilter) {
        return Observable.create(new Observable.OnSubscribe<Intent>() {
            @Override
            public void call(final Subscriber<? super Intent> subscriber) {
                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        subscriber.onNext(intent);
                    }
                };

                LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);
                //ensure that receiver is unregistered after subscription is unregistered
                subscriber.add(Subscriptions.create(() -> {
                    LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
                    Log.d("RxReceiver", "call: unsubscire");
                }));
            }
        });
    }
}
