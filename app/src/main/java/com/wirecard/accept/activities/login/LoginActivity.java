package com.wirecard.accept.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wirecard.accept.Application;
import com.wirecard.accept.BuildConfig;
import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseActivity;
import com.wirecard.accept.activities.menu.MenuActivity;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.help.RxHelper;
import com.wirecard.accept.rx.dialog.RxDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;
import rx.Subscription;

/**
 * Created by jakub.misko on 1. 4. 2016.
 */
@RequiresPresenter(LoginPresenter.class)
public class LoginActivity extends BaseActivity<LoginPresenter> {
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login)
    Button loginBtn;
    @BindView(R.id.backend)
    TextView backend;
    @BindView(R.id.version)
    TextView version;

    private Subscription alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        backend.setText(BuildConfig.apiPath);
        //TODO resource
        String versionStr = String.format("Version: %s(%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        version.setText(versionStr);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBackendConfig();
        checkLoginToken();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxHelper.unsubscribe(alertDialog);
    }

    @OnClick(R.id.login)
    public void handleLogin() {
        if (TextUtils.isEmpty(username.getText()) && TextUtils.isEmpty(password.getText())) {
            presentFormError(getString(R.string.login_empty_pass_and_name));
        } else if (TextUtils.isEmpty(username.getText())) {
            presentFormError(getString(R.string.login_empty_name));
        } else if (TextUtils.isEmpty(password.getText())) {
            presentFormError(getString(R.string.login_empty_pass));
        } else {
            enableForm(false);
            getPresenter().evaluateLogin(username.getText().toString(), password.getText().toString());
        }
    }

    public void goToMenu() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }


    public void enableForm(final boolean flag) {
        loginBtn.setEnabled(flag);
        username.setEnabled(flag);
        password.setEnabled(flag);
    }

    public void presentFormError(final String error) {
        alertDialog = RxDialog.create(this, getString(R.string.login_error), error)
                .doOnEach(notification -> enableForm(true))
                .subscribe(click -> {
                    Log.d(TAG, "presentFormError: dialog button clicked");
                }, err -> {
                    Log.e(TAG, "presentFormError: ", err);
                });
    }

    private void checkBackendConfig() {
        String wrongSdkConfig = ((Application) getApplication()).getErrorMessage();
        if (!getPresenter().isBEConfigOk(wrongSdkConfig)) {
            Intent intent = new Intent(this, WrongAcceptSettingsActivity.class);
            intent.putExtra(Constants.TEXT, wrongSdkConfig);
            startActivity(intent);
            finish();
        }
    }

    /**
     * if user left application and return back in
     */
    void checkLoginToken() {
        if (!getPresenter().isLoginTokenExpired()) {
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        }
    }
}
