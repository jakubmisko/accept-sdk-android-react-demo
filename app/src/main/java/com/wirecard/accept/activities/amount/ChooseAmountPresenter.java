package com.wirecard.accept.activities.amount;

import java.math.BigDecimal;

import nucleus.presenter.Presenter;

/**
 * Created by super on 09.02.2017.
 */
class ChooseAmountPresenter extends Presenter<ChooseAmountActivity>{
    private  final String TAG = getClass().getSimpleName();
    private String actualAmount = "0";
    private boolean decimalPart = false;
    private int decimals = 0;
    private int maxDigits;

    @Override
    protected void onTakeView(ChooseAmountActivity chooseAmountActivity) {
        super.onTakeView(chooseAmountActivity);
        //present initial value
        chooseAmountActivity.setAmount(actualAmount);
    }

    /**
     * append digit on amount displayed
     * @param digit 0-9 digit
     */
    private void appendAmount(String digit){
        if(actualAmount.length() < maxDigits || (decimalPart && actualAmount.length() <= maxDigits)) {
            if (actualAmount.equals("0")) {
                actualAmount = digit;
            } else if (decimalPart && decimals < 2) {
                decimals++;
                actualAmount += digit;
            } else if (!decimalPart) {
                actualAmount += digit;
            }
        }
    }

    /**
     * delete last digit from display
     */
    void clearLastDigit(){
        if(actualAmount.length() == 1){
            actualAmount = "0";
        } else {
            actualAmount = actualAmount.substring(0, actualAmount.length() - 1);
        }
        if(decimalPart){
            decimals--;
            decimalPart = decimals != 0;
        }
        getView().setAmount(actualAmount);
    }

    /**
     * handle number key press from activity
     * @param value 0-9 digits
     */
    void onValueChange(String value){
//        events
        appendAmount(value);
        getView().setAmount(actualAmount);

    }

    /**
     * put decimal point, if there's point all next digits are considered decimal
     */
    void addDivider(){
        if(!decimalPart) {
            decimalPart = true;
            actualAmount += ".";
            getView().setAmount(actualAmount);
        }
    }

    /**
     * set max allowed digits to be set in amount
     * @param maxDigits max length
     */
    public void setMaxDigits(int maxDigits) {
        this.maxDigits = maxDigits;
    }

    public BigDecimal getAmount(){
        return new BigDecimal(actualAmount);
    }
}
