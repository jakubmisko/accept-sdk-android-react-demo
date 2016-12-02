package com.wirecard.accept.rx.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

/**
 * Created by super on 24.11.2016.
 */

public class RxView {
    @CheckResult
    @NonNull
    public static Observable<Void> clicks(@NonNull View view) {
        //todo not null check
        return Observable.create(new Observable.OnSubscribe<Void>(){
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                MainThreadSubscription.verifyMainThread();
                View.OnClickListener listener = v -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(null);
                    }
                };

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.setOnClickListener(null);
                    }
                });

                view.setOnClickListener(listener);
            }
        });
    }
}
