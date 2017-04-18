package com.wirecard.accept.rx.dialog;

import android.app.ProgressDialog;
import android.content.Context;

import com.wirecard.accept.R;

import rx.Observable;
import rx.subscriptions.Subscriptions;

/**
 * Created by super on 09.04.2017.
 */

public class RxProgressDialog {
    /**
     * create not cancelable progress dialog as observable that doesn't emit anything and dismiss at unsubscribe
     * @param context
     * @param message string that may be shown in progress dialog
     * @return in observerable wrapped progress dialog
     */
    public static Observable<Void> create(Context context, String message) {
        return Observable.create(subscriber -> {
            final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            if (message != null) {
                progressDialog.setMessage(message);
            }
            subscriber.onNext(null);
//            subscriber.onCompleted();
            subscriber.add(Subscriptions.create(progressDialog::dismiss));
            progressDialog.show();
        });
    }

    public static Observable<Void> create(Context context, int messageRes) {
        return create(context, context.getString(messageRes));
    }
}
