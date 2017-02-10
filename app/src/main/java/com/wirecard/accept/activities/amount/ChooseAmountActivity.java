package com.wirecard.accept.activities.amount;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseActivity;
import com.wirecard.accept.activities.paymentflow.PaymentFlowActivity;
import com.wirecard.accept.help.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

@RequiresPresenter(ChooseAmountPresenter.class)
public class ChooseAmountActivity extends BaseActivity<ChooseAmountPresenter> {
    @BindView(R.id.amount)
    TextView amount;
    @BindView(R.id.methods)
    Spinner paymentMethods;
    @BindView(R.id.sepa)
    CheckBox sepa;

    @OnClick(R.id.one)
    public void oneClick() {
        getPresenter().onValueChange("1");
    }

    @OnClick(R.id.two)
    public void twoClick() {
        getPresenter().onValueChange("2");
    }

    @OnClick(R.id.three)
    public void threeClick() {
        getPresenter().onValueChange("3");
    }

    @OnClick(R.id.four)
    public void fourClick() {
        getPresenter().onValueChange("4");
    }

    @OnClick(R.id.five)
    public void fiveClick() {
        getPresenter().onValueChange("5");
    }

    @OnClick(R.id.six)
    public void sixClick() {
        getPresenter().onValueChange("6");
    }

    @OnClick(R.id.seven)
    public void sevenClick() {
        getPresenter().onValueChange("7");
    }

    @OnClick(R.id.eight)
    public void eightClick() {
        getPresenter().onValueChange("8");
    }

    @OnClick(R.id.nine)
    public void nineClick() {
        getPresenter().onValueChange("9");
    }

    @OnClick(R.id.zero)
    public void zeroClick() {
        getPresenter().onValueChange("0");
    }

    @OnClick(R.id.backspace)
    public void backSpaceClick() {
        getPresenter().clearLastDigit();
    }

    @OnClick(R.id.divider)
    public void dividerClick() {
        getPresenter().addDivider();
    }

    /**
     * start payment flow activity
     */
    @OnClick(R.id.pay)
    public void pay() {
        Intent intent = new Intent(this, PaymentFlowActivity.class);
        intent.putExtra(Constants.AMOUNT, amount.getText());
        intent.putExtra(Constants.SEPA, sepa.isChecked());
        intent.putExtra(Constants.PAYMENT_METHOD, paymentMethods.getPrompt());
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_amount);
        ButterKnife.bind(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.payment_methods, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        paymentMethods.setAdapter(adapter);
        //set max acceptable digits for amount, for demonstration purposes we consider just one item in basket
        getPresenter().setMaxDigits(15);
    }


    public void setAmount(String amount) {
        this.amount.setText(amount);
    }
}
