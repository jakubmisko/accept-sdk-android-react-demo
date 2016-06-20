package com.wirecard.accept.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.wirecard.accept.Application;
import com.wirecard.accept.BuildConfig;
import com.wirecard.accept.R;
import com.wirecard.accept.help.Constants;

import de.wirecard.accept.sdk.AcceptSDK;

/**
 * Created by jakub.misko on 1. 4. 2016.
 */
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Application app = (Application) getApplication();
        String wrongSdkConfig = app.getErrorMessage();
        if (!TextUtils.isEmpty(wrongSdkConfig)) {
            Intent intent = new Intent(this, WrongAcceptSettingsActivity.class);
            intent.putExtra(Constants.TEXT, wrongSdkConfig);
            startActivity(intent);
            finish();
            return;
        }
        if (!TextUtils.isEmpty(AcceptSDK.getToken())) {
            startActivity(new Intent(this, MenuActivity.class));
            finish();
            return;
        }
        findViewById(R.id.action).setOnClickListener(l -> {
            handleOnLoginPressed();
        });

        ((TextView) findViewById(R.id.backend)).setText(BuildConfig.apiPath);
        String version = String.format("Version: %s(%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        ((TextView) findViewById(R.id.version)).setText(version);
    }

    private void handleOnLoginPressed() {
        final EditText usernameEditText = (EditText) findViewById(R.id.username);
        final EditText passwordEditText = (EditText) findViewById(R.id.password);
        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(username)) {
            presentFormError("Username field is empty.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            presentFormError("Password field is empty.");
            return;
        }

        enableForm(false);
        AcceptSDK.login(username, password, (apiResult, result) -> {
            enableForm(true);
            if (apiResult.isSuccess()) {
                startActivity(new Intent(this, MenuActivity.class));
                finish();
                return;
            }
            presentFormError(apiResult.getDescription());
        });
    }

    private void enableForm(final boolean flag) {
        findViewById(R.id.action).setEnabled(flag);
        findViewById(R.id.username).setEnabled(flag);
        findViewById(R.id.password).setEnabled(flag);
    }

    private void presentFormError(final String error) {
        new AlertDialog.Builder(this)
                .setTitle("Login Error")
                .setMessage(error)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
}
