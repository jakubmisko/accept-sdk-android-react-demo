package com.wirecard.accept.activities.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wirecard.accept.BuildConfig;
import com.wirecard.accept.R;
import com.wirecard.accept.activities.BaseActivity;
import com.wirecard.accept.rx.dialog.RxDialog;
import com.wirecard.accept.rx.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nucleus.factory.RequiresPresenter;
import rx.Subscription;

/**
 * Created by jakub.misko on 1. 4. 2016.
 */
@RequiresPresenter(LoginPresenter.class)
public class LoginActivity extends BaseActivity<LoginPresenter> {
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

    private Subscription btnClick, alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        btnClick = RxView.clicks(loginBtn).subscribe(click -> getPresenter().evaluateLogin(),
                error -> Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());

        backend.setText(BuildConfig.apiPath);
        String versionStr = String.format("Version: %s(%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        version.setText(versionStr);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().checkBackendConfig();
        getPresenter().checkLoginToken();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (btnClick != null) {
            btnClick.unsubscribe();
            btnClick = null;
        }
        if (alertDialog != null) {
            alertDialog.unsubscribe();
            alertDialog = null;
        }
    }

    public boolean isNameEmpty() {
        return TextUtils.isEmpty(username.getText());
    }

    public boolean isPassEmpty() {
        return TextUtils.isEmpty(password.getText());
    }

    public String getUsername() {
        return username.getText().toString();
    }

    public String getPass() {
        return password.getText().toString();
    }
//    @OnClick(R.id.login)
//    void handleOnLoginPressed() {
//        final String usernameText = username.getText().toString();
//        final String passwordText = password.getText().toString();
//
//    }

    public void enableForm(final boolean flag) {
        loginBtn.setEnabled(flag);
        username.setEnabled(flag);
        password.setEnabled(flag);
    }

    public void presentFormError(final String error) {
//        new AlertDialog.Builder(this)
//                .setTitle("Login Error")
//                .setMessage(error)
//                .setPositiveButton("OK", null)
//                .create()
//                .show();
        alertDialog = RxDialog.create(this, "Login Error", error).subscribe();
    }
}
