package com.wirecard.accept.activities.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import de.wirecard.accept.sdk.AcceptSDK;
import nucleus.presenter.RxPresenter;
import rx.Observable;

/**
 * Created by super on 28.11.2016.
 */
public class LoginPresenter extends RxPresenter<LoginActivity> {
    private String TAG = getClass().getSimpleName();
    private final int LOGIN = 0;
    private String username, pass;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        restartableLatestCache(LOGIN,
                () -> Observable.create(subscriber -> {

                    AcceptSDK.login(username, pass, (apiResult, result) -> {
                        System.out.println(apiResult.getCode());
                        if (apiResult.isSuccess()) {
                            subscriber.onNext(result);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(new Throwable(apiResult.getDescription()));
                        }
                    });
                    Log.d(TAG, "login request: executing on thread " + Thread.currentThread().getName());
                })
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread()),
                ,
                (loginActivity, o) -> loginActivity.goToMenu(),
                (loginActivity, throwable) -> loginActivity.presentFormError(throwable.getMessage())
        );

    }

    public void evaluateLogin(String username, String pass) {
        this.username = username;
        this.pass = pass;
        start(LOGIN);
    }

    public boolean isLoginTokenExpired() {
        return TextUtils.isEmpty(AcceptSDK.getToken());
    }

    public boolean isBEConfigOk(String config) {
        return TextUtils.isEmpty(config);
    }

}
