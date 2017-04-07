package com.wirecard.accept.activities.login;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.wirecard.accept.R;
import com.wirecard.accept.help.Constants;

/**
 * inform about corrupted configuration
 */
public class WrongAcceptSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_config);
        ((TextView) findViewById(R.id.text)).setText(getIntent().getStringExtra(Constants.TEXT));
    }
}
