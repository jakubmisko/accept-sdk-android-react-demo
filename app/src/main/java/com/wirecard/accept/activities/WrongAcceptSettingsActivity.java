package com.wirecard.accept.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.wirecard.accept.R;
import com.wirecard.accept.help.Constants;

/**
 * Created by jakub on 02.04.2016.
 */
public class WrongAcceptSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong);
        ((TextView) findViewById(R.id.text)).setText(getIntent().getStringExtra(Constants.text));
    }
}
