package com.wirecard.accept.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.paymentflow.PaymentFlowActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.wirecard.accept.sdk.AcceptSDK;
import nucleus.presenter.Presenter;

/**
 * Created by jakub on 02.04.2016.
 */
public abstract class AbstractMenuActivity<P extends Presenter> extends BaseActivity<P> {
    @BindView(R.id.payment)
    Button payment;
    @BindView(R.id.history)
    Button history;
    @BindView(R.id.logout)
    Button logout;

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
        Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
        finish();
    }
}
