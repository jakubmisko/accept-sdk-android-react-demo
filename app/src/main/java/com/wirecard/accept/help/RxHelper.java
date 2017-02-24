package com.wirecard.accept.help;

import rx.Observable;
import rx.Subscription;

public class RxHelper {
    public static void unsubscribe(Subscription... s){
        Observable.from(s).subscribe(RxHelper::unsubscribe);
    }
    public static void unsubscribe(Subscription s){
        if (s != null && s.isUnsubscribed()) {
            s.unsubscribe();
        }
    }
}
