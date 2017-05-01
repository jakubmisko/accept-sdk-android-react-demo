package com.wirecard.accept.rx.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.wirecard.accept.R;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Work with dialogs as event streams
 */

public class RxAlertDialog {

    @NonNull
    public static Observable<Boolean> create(Context context, int title, int message, View customContent, int positiveBtn, int negativeBtn){
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context, R.style.AppTheme_Dialog)
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
            final AlertDialog ad = new AlertDialog.Builder(context, R.style.AppTheme_Dialog)
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
    public static Observable<Boolean> create(Context context, String title, String message, int positiveBtn, int negativeBtn) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context, R.style.AppTheme_Dialog)
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
    public static Observable<Void> create(Context context, String title, String message) {
        return create(context, title, message, context.getString(android.R.string.ok));
    }
    @NonNull
    public static Observable<Boolean> create(Context context, int title, int message){
        return create(context, title, message, android.R.string.ok, android.R.string.cancel);
    }

    @NonNull
    public static Observable<Integer> create(Context context, int title, int negativeBtn, CharSequence[] items){
        return Observable.create((Subscriber<? super Integer> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context, R.style.AppTheme_Dialog)
                    .setTitle(title)
                    .setCancelable(false)
                    .setSingleChoiceItems(items, -1, (dialogInterface, i) -> {
                        subscriber.onNext(i);
                        subscriber.onCompleted();
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(negativeBtn, (dialogInterface, i) -> {
//                        subscriber.onNext(-1);
                        subscriber.onError(new Throwable());
                        subscriber.onCompleted();
                    })
                    .create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }

    @NonNull
    public static Observable<Void> create(Context context, String title, String message, String possitiveBtn){
        return Observable.create((Subscriber<? super Void> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context, R.style.AppTheme_Dialog)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(possitiveBtn, (dialogInterface, i) -> {
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    })
                    .create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }

    @NonNull
    public static Observable<Void> create(Context context, int title, int message, int possitiveBtn){
        String titleString = context.getString(title);
        String messageString = context.getString(message);
        String possitiveBtnString = context.getString(possitiveBtn);

        return create(context, titleString, messageString, possitiveBtnString);
    }

    public static Observable<Void> createAlert(Context context, int message, int positiveBtn) {
        return Observable.create((Subscriber<? super Void> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context, R.style.AppTheme_Dialog)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(positiveBtn, (dialog, which) -> {
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    })
                    .create();
            // cleaning up
            subscriber.add(Subscriptions.create(ad::dismiss));
            ad.show();
        });
    }

    @NonNull
    public static Observable<Boolean> createAlert(Context context, int message, int positiveBtn, int negativeBtn) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            final AlertDialog ad = new AlertDialog.Builder(context, R.style.AppTheme_Dialog)
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
}
