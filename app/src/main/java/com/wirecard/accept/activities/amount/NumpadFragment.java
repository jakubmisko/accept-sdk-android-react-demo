package com.wirecard.accept.activities.amount;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.base.BaseFragment;
import com.wirecard.accept.activities.paymentflow.PaymentFlowActivity;
import com.wirecard.accept.help.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

//TODO start from floating point part, refactor name
@RequiresPresenter(NumpadPresenter.class)
public class NumpadFragment extends BaseFragment<NumpadPresenter> implements View.OnClickListener {
    @BindView(R.id.amount)
    TextView amount;
    @BindView(R.id.currency)
    TextView currency;
    @BindView(R.id.methods)
    Spinner paymentMethods;
    @BindView(R.id.sepa)
    CheckBox sepa;
    @BindView(R.id.one)
    AppCompatButton one;
    @BindView(R.id.two)
    AppCompatButton two;
    @BindView(R.id.three)
    AppCompatButton three;
    @BindView(R.id.four)
    AppCompatButton four;
    @BindView(R.id.five)
    AppCompatButton five;
    @BindView(R.id.six)
    AppCompatButton six;
    @BindView(R.id.seven)
    AppCompatButton seven;
    @BindView(R.id.eight)
    AppCompatButton eight;
    @BindView(R.id.nine)
    AppCompatButton nine;
    @BindView(R.id.zero)
    AppCompatButton zero;

    @OnClick(R.id.backspace)
    public void backSpaceClick() {
        getPresenter().clearLastDigit();
    }

    @OnClick(R.id.clear)
    public void dividerClick() {
        getPresenter().clearAmount();
    }

    /**
     * start payment flow activity
     */
    @OnClick(R.id.pay)
    public void pay() {
        Intent intent = new Intent(getActivity(), PaymentFlowActivity.class);
        intent.putExtra(Constants.AMOUNT, amount.getText());
        intent.putExtra(Constants.SEPA, sepa.isChecked());
        intent.putExtra(Constants.PAYMENT_METHOD, paymentMethods.getSelectedItem().toString());
        startActivity(intent);
//        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_choose_amount, container, false);
        ButterKnife.bind(this, view);
        attachListeners();
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.payment_methods, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        paymentMethods.setAdapter(adapter);
        //set max acceptable digits for amount, for demonstration purposes we consider just one item in basket
        getPresenter().setMaxDigits(15);
        return view;
    }

    private void attachListeners(){
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zero.setOnClickListener(this);
    }

    public void setAmount(String amount) {
        this.amount.setText(amount);
    }

    public void setCurrency(String currency) {
        this.currency.setText(currency);
    }

    /**
     * handle number button click and pass value to presenter
     *
     * @param v button clicked
     */
    public void onClick(View v) {
        String value = ((AppCompatButton) v).getText().toString();
        getPresenter().onValueChange(value);
    }
}
