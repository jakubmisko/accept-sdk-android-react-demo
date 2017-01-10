package com.wirecard.accept.help;

import rx.Observable;
import rx.Subscription;

/**
 * Created by super on 03.12.2016.
 */

public class RxHelper {
    public static void unsubscribe(Subscription... s){
        Observable.from(s).subscribe(RxHelper::unsubscribe);
    }
    public static void unsubscribe(Subscription s){
        if (s != null && s.isUnsubscribed()) {
            s.unsubscribe();
        }
    }

    public static void uiThreadScheduler(){
        //todo use ui thread for composition
    }
}
