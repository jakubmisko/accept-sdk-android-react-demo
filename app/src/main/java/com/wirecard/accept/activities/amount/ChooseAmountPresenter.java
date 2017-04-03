package com.wirecard.accept.activities.amount;

import java.math.BigDecimal;

import de.wirecard.accept.sdk.util.CurrencyWrapper;
import nucleus.presenter.Presenter;

/**
 * Created by super on 09.02.2017.
 */
public class ChooseAmountPresenter extends Presenter<ChooseAmountFragment> {
    private final String TAG = getClass().getSimpleName();
    private final String ZERO = "0";
    private String actualAmount = ZERO;
    private char DELIMETER = '.';
    private int maxDigits = 0;
    private int decimals = 2;

    @Override
    protected void onTakeView(ChooseAmountFragment chooseAmountActivity) {
        super.onTakeView(chooseAmountActivity);
        //present initial value
        chooseAmountActivity.setAmount(formatAmount());
        chooseAmountActivity.setCurrency(CurrencyWrapper.getCurrencySymbol());
    }

    /**
     * append digit on amount displayed
     *
     * @param digit 0-9 digit
     */
    private void appendAmount(String digit) {
        if (actualAmount.equals("0")) {
            actualAmount = digit;
        } else if (actualAmount.length() < maxDigits) {
            actualAmount += digit;
        }
    }

    private String formatAmount() {
        //put as much zeros to floating point part as currency allows
        actualAmount = actualAmount.replaceFirst("^0+(?!$)", "");
        if (actualAmount.length() <= decimals) {
            String prefix = "";
            for (int i = actualAmount.length(); i <= decimals; i++) {
                prefix += 0;
            }
            actualAmount = prefix + actualAmount;
        }
        return new StringBuilder(actualAmount).insert(actualAmount.length() - 2, DELIMETER).toString();
    }

    /**
     * delete last digit from display
     */
    void clearLastDigit() {
        if (actualAmount.length() > 1) {
            actualAmount = actualAmount.substring(0, actualAmount.length() - 1);
        } else {
            actualAmount = "0";
        }
        getView().setAmount(formatAmount());
    }

    /**
     * handle number key press from activity
     *
     * @param value 0-9 digits
     */
    void onValueChange(String value) {
//        events
        appendAmount(value);
        getView().setAmount(formatAmount());

    }

    /**
     * put decimal point, if there's point all next digits are considered decimal
     */
    void setDivider(char delimeter) {
        DELIMETER = delimeter;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    /**
     * set max allowed digits to be set in amount
     *
     * @param maxDigits max length
     */
    public void setMaxDigits(int maxDigits) {
        this.maxDigits = maxDigits;
    }

    /**
     * get amount wrapped in BigDecimal
     *
     * @return amount to pay
     */
    public BigDecimal getAmount() {
        return new BigDecimal(actualAmount);
    }

    public void clearAmount() {
        actualAmount = ZERO;
        getView().setAmount(formatAmount());
    }
}
