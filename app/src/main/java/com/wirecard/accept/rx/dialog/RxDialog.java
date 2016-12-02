package com.wirecard.accept.rx.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by super on 26.11.2016.
 */

public class RxDialog {

    @NonNull
    public static Observable<Boolean> create(Context context, int title, int message, View customContent, int positiveBtn, int negativeBtn){
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setView(customContent)
                    .setCancelable(false)
                    .setPositiveButton(positiveBtn, (dialog, which) -> {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    })
                    .setNegativeButton(negativeBtn, (dialog, which) -> {
                        subscriber.onNext(false);
                        subscriber.onCompleted();
                    })
                    .create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }

    @NonNull
    public static Observable<Boolean> create(Context context, int title, int message, int positiveBtn, int negativeBtn) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(positiveBtn, (dialog, which) -> {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    })
                    .setNegativeButton(negativeBtn, (dialog, which) -> {
                        subscriber.onNext(false);
                        subscriber.onCompleted();
                    })
                    .create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }
    //todo abstraction
    @NonNull
    public static Observable<Boolean> create(Context context, String title, String message, int positiveBtn, int negativeBtn) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(positiveBtn, (dialog, which) -> {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    })
                    .setNegativeButton(negativeBtn, (dialog, which) -> {
                        subscriber.onNext(false);
                        subscriber.onCompleted();
                    })
                    .create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }
    @NonNull
    public static Observable<Boolean> create(Context context, String title, String message) {
        return create(context, title, message, android.R.string.ok, android.R.string.cancel);
    }
    @NonNull
    public static Observable<Boolean> create(Context context, int title, int message){
        return create(context, title, message, android.R.string.ok, android.R.string.cancel);
    }

    @NonNull
    public static Observable<Integer> create(Context context, int title, int negativeBtn, CharSequence[] items){
        return Observable.create((Subscriber<? super Integer> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setCancelable(false)
                    .setSingleChoiceItems(items, -1, (dialogInterface, i) -> {
                        subscriber.onNext(i);
                        subscriber.onCompleted();
                    })
                    .setNegativeButton(negativeBtn, (dialogInterface, i) -> {
                        subscriber.onCompleted();
                    })
                    .create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }

}
