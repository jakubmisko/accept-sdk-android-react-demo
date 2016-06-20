package com.wirecard.accept.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.wirecard.accept.R;

import de.wirecard.accept.sdk.AcceptSDK;

/**
 * Created by jakub on 02.04.2016.
 */
public class AbstractMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
//TODO activities
        findViewById(R.id.payment).setOnClickListener(l -> {
            startActivity(new Intent(getApplicationContext(), PaymentFlowActivity.class));
            //Toast.makeText(this, "payment flow click", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.history).setOnClickListener(l -> {
            startActivity(new Intent(getApplicationContext(), TransactionsHistoryActivity.class));
//            Toast.makeText(this, "payment history click", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.logout).setOnClickListener(l -> {
            AcceptSDK.logout();
            Toast.makeText(this, "payment history click", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
