package com.wirecard.accept.activities.login;

import android.os.Bundle;
import android.text.TextUtils;

import de.wirecard.accept.sdk.AcceptSDK;
import nucleus.presenter.RxPresenter;
import rx.Observable;

/**
 * Created by super on 28.11.2016.
 */
public class LoginPresenter extends RxPresenter<LoginActivity/*LoginActivity*/> {
    private String TAG = getClass().getSimpleName();
    private final int LOGIN = 0;
    private String username, pass;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        restartableLatestCache(LOGIN,
                () -> Observable.create(subscriber -> {
                    //send credentials to backend for evaluation, request is executed on new thread
                    AcceptSDK.login(username, pass, (apiResult, result) -> {
                        if (apiResult.isSuccess()) {
                            subscriber.onNext(result);
                            subscriber.onCompleted();
                        } else {
                            //wrap error message as throwable
                            subscriber.onError(new Throwable(apiResult.getMessage()));
                        }
                    });
                })
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread()),
                ,
                (loginActivity, o) -> loginActivity.goToMenu(),
                (loginActivity, throwable) -> loginActivity.presentFormError(throwable.getMessage())
        );

    }

    void evaluateLogin(String username, String pass) {
        this.username = username;
        this.pass = pass;
        //start restartable for login
        start(LOGIN);
    }

    boolean isLoginTokenExpired() {
        return TextUtils.isEmpty(AcceptSDK.getToken());
    }

    boolean isBEConfigOk(String config) {
        return TextUtils.isEmpty(config);
    }

}
