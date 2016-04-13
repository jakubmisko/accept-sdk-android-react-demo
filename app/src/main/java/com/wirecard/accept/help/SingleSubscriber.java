package com.wirecard.accept.help;

import rx.Observer;

/**
 * Created by jakub on 12.04.2016.
 */
public abstract class SingleSubscriber<T> implements Observer<T>{

    @Override
    public void onNext(T t) {
        //do nothing
    }
}
