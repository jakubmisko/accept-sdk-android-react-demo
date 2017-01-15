package com.wirecard.accept.activities.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseActivity;
import com.wirecard.accept.activities.history.TransactionsHistoryActivity;
import com.wirecard.accept.activities.login.LoginActivity;
import com.wirecard.accept.activities.paymentflow.PaymentFlowActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.wirecard.accept.sdk.AcceptSDK;
import nucleus.presenter.RxPresenter;

/**
 * Created by jakub on 02.04.2016.
 */
public abstract class AbstractMenuActivity<P extends RxPresenter> extends BaseActivity<P> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.payment)
    public void newPayment() {
        startActivity(new Intent(getApplicationContext(), PaymentFlowActivity.class));
    }
    @OnClick(R.id.history)
    public void paymentsHistory() {
        startActivity(new Intent(getApplicationContext(), TransactionsHistoryActivity.class));
    }
    @OnClick(R.id.logout)
    public void logOut(){
        AcceptSDK.logout();
        Toast.makeText(this, "Bye", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
