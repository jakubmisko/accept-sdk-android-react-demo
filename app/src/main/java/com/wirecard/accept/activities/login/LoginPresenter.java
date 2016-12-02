package com.wirecard.accept.activities.login;

import android.content.Intent;
import android.text.TextUtils;

import com.wirecard.accept.Application;
import com.wirecard.accept.activities.WrongAcceptSettingsActivity;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.menu.MenuActivity;

import de.wirecard.accept.sdk.AcceptSDK;
import nucleus.presenter.Presenter;

import static com.wirecard.accept.help.Preconditions.nullCheck;

/**
 * Created by super on 28.11.2016.
 */
class LoginPresenter extends Presenter<LoginActivity> {
    private void viewNullCheck(){
        nullCheck(getView(), "Login view is null");
    }
    void evaluateLogin() {
        viewNullCheck();
        if (getView().isNameEmpty()) {
            getView().presentFormError("Username field is empty.");
            return;
        }
        if (getView().isPassEmpty()) {
            getView().presentFormError("Password field is empty.");
            return;
        }

        getView().enableForm(false);
        AcceptSDK.login(getView().getUsername(), getView().getPass(), (apiResult, result) -> {
            getView().enableForm(true);
            if (apiResult.isSuccess()) {
                getView().startActivity(new Intent(getView(), MenuActivity.class));
                getView().finish();
                return;
            }
            getView().presentFormError(apiResult.getDescription());
        });
    }

    void checkBackendConfig(){
        viewNullCheck();
        Application app = (Application) getView().getApplication();
        String wrongSdkConfig = app.getErrorMessage();
        if (!TextUtils.isEmpty(wrongSdkConfig)) {
            Intent intent = new Intent(getView(), WrongAcceptSettingsActivity.class);
            intent.putExtra(Constants.TEXT, wrongSdkConfig);
            getView().startActivity(intent);
            getView().finish();
        }
    }

    void checkLoginToken() {
        viewNullCheck();
        if (!TextUtils.isEmpty(AcceptSDK.getToken())) {
            getView().startActivity(new Intent(getView(), MenuActivity.class));
            getView().finish();
        }
    }
}
