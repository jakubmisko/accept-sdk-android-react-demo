package com.wirecard.accept.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.wirecard.accept.help.Constants;

/**
 * Created by jakub.misko on 14. 4. 2016.
 */
public abstract class BaseActivity extends Activity{
    final String TAG = BaseActivity.class.getSimpleName();


    public final static int TYPE_LOGOUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        LocalBroadcastManager.getInstance(this).registerReceiver(mLogoutReceiver, new IntentFilter(Constants.INTENT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLogoutReceiver);
    }

    /**
     * Receiver for "timeout" of server login session. After timeout You have to login again
     */
    private final BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = null;
            if (intent != null)
                extras = intent.getExtras();
            if (extras != null) {
                switch (extras.getInt(Constants.INTENT_TYPE)){
                    case TYPE_LOGOUT:
                        // Kill receiving activity
                        Log.e(TAG, ">>>>>>>>>>>>> LOGOUT <<<<<<<<<<<<<<<<");
                        finish();
                        break;
                    default:
                        break;
                }
            }
        }
    };
}
