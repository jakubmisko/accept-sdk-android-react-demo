package com.wirecard.accept.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wirecard.accept.Application;
import com.wirecard.accept.BuildConfig;
import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseActivity;
import com.wirecard.accept.activities.menu.MenuActivity;
import com.wirecard.accept.help.Constants;
import com.wirecard.accept.help.RxHelper;
import com.wirecard.accept.rx.dialog.RxAlertDialog;
import com.wirecard.accept.rx.dialog.RxProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;
import rx.Subscription;

/**
 * A login screen that offers login via username/password.
 */
@RequiresPresenter(LoginPresenter.class)
public class LoginActivity extends BaseActivity<LoginPresenter> {
    private final String TAG = getClass().getSimpleName();
    // UI references.
    @BindView(R.id.email)
    EditText username;
    @BindView(R.id.email_layout)
    TextInputLayout userLayout;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.backend)
    TextView backend;
    @BindView(R.id.version)
    TextView version;

    private Subscription alertDialog;
    private Subscription progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        backend.setText(BuildConfig.apiPath);
        String versionStr = String.format("Version: %s(%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        version.setText(versionStr);

    }


    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.btn_login)
    void attemptLogin() {
        // Reset errors.
        userLayout.setError(null);
        userLayout.setErrorEnabled(false);
        passwordLayout.setError(null);
        passwordLayout.setErrorEnabled(false);

        // Store values at the time of the login attempt.
        String usrStr = this.username.getText().toString();
        String passStr = this.password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(passStr)) {
            this.passwordLayout.setError(getString(R.string.error_empty_password));
            passwordLayout.setErrorEnabled(true);
            focusView = this.password;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(usrStr)) {
            this.userLayout.setError(getString(R.string.error_empty_username));
            userLayout.setErrorEnabled(true);
            focusView = this.username;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress();
            getPresenter().evaluateLogin(usrStr, passStr);
        }
    }

    private void showProgress() {
        progressDialog = RxProgressDialog.create(this, "Authenticating...")
                .subscribe();
    }

    private void dissmisProgress(){
        RxHelper.unsubscribe(progressDialog);
    }

    public void goToMenu() {
        dissmisProgress();
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    public void presentFormError(String message) {
        dissmisProgress();
        alertDialog = RxAlertDialog.create(this, getString(R.string.login_error), message)
                .subscribe(click -> Log.d(TAG, "presentFormError: dialog button clicked"),
                        err -> Log.e(TAG, "presentFormError: ", err));
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkBackendConfig();
        checkLoginToken();
        RxHelper.unsubscribe(alertDialog);
    }

    /**
     * inform that there's corrupted backend configuration
     */
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

