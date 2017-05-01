package com.wirecard.accept.rx.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

import rx.Observable;
import rx.android.MainThreadSubscription;

/**
 * Reactive wrapper for onclick listener on view
 */

public class RxView {
    @CheckResult
    @NonNull
    public static Observable<Void> clicks(@NonNull View view) {
        return Observable.create(subscriber -> {
            MainThreadSubscription.verifyMainThread();
            View.OnClickListener listener = v -> {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                }
            };
            //remove listener to avoid memory leaks
            subscriber.add(new MainThreadSubscription() {
                @Override protected void onUnsubscribe() {
                    view.setOnClickListener(null);
                }
            });

            view.setOnClickListener(listener);
        });
    }
}
