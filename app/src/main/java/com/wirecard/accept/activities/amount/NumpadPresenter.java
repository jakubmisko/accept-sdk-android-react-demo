package com.wirecard.accept.activities.amount;

import java.math.BigDecimal;

import de.wirecard.accept.sdk.util.CurrencyWrapper;
import nucleus.presenter.Presenter;
import rx.subjects.PublishSubject;

/**
 * Created by super on 09.02.2017.
 */
public class NumpadPresenter extends Presenter<NumpadFragment> {
    private final String TAG = getClass().getSimpleName();
    private final String ZERO = "0";
    private final String BACKSPACE = "<";
    private final String CLEAR = "x";
    //    private String actualAmount = ZERO;
    private char DELIMETER = '.';
    private int maxDigits = 0;
    private int decimals = 2;
    //    private Observable<String> amounts;
    private PublishSubject<String> events;

    @Override
    protected void onTakeView(NumpadFragment chooseAmountActivity) {
        super.onTakeView(chooseAmountActivity);
        //present initial value
        chooseAmountActivity.setAmount(formatAmount(ZERO));
        chooseAmountActivity.setCurrency(CurrencyWrapper.getCurrencySymbol());
        events = PublishSubject.create();
        events.asObservable()
                //react to new digit (or event)
                .scan(this::handleEvents)
                //format actual value to correct format
                .map(this::formatAmount)
                //set value to view
                .subscribe(s -> getView().setAmount(s));
    }

    /**
     * hadle events floating trough stream and modify acumulator
     *
     * @param acumulator actual amount(acumulated)
     * @param event      latest event from user
     * @return modified actual value
     */
    private String handleEvents(String acumulator, String event) {
        switch (event) {
            case BACKSPACE:
                if (acumulator.length() == 1) {
                    return ZERO;
                }
                return acumulator.substring(0, acumulator.length() - 1);
            case CLEAR:
                return ZERO;
            default:
                if (acumulator.length() == maxDigits) {
                    return acumulator;
                }
                return acumulator + event;
        }
    }

    /**
     * format entered digits into floating point format
     * @param amount to be formated
     * @return amount in correct format
     */
    private String formatAmount(String amount) {
        //put as much zeros to floating point part as currency allows
        amount = amount.replaceFirst("^0+(?!$)", "");
        if (amount.length() <= decimals) {
            String prefix = "";
            for (int i = amount.length(); i <= decimals; i++) {
                prefix += 0;
            }
            amount = prefix + amount;
        }
        return new StringBuilder(amount).insert(amount.length() - 2, DELIMETER).toString();
    }


    /**
     * produce backspace event
     */
    void clearLastDigit() {
        events.onNext(BACKSPACE);
    }

    /**
     * handle number key press from activity
     *
     * @param value 0-9 digits
     */
    void onValueChange(String value) {
//        events
        events.onNext(value);

    }

    /**
     * set delimeter character
     * @param delimeter displayed in number
     */
    void setDivider(char delimeter) {
        DELIMETER = delimeter;
    }

    /**
     * set allowed max decimals for amount formating
     * @param decimals max allowed decimals
     */
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
        String amount = getView().amount.getText().toString();
        return new BigDecimal(amount.substring(0, amount.length() - 2));
    }

    /**
     * produce clear amount field event
     */

    public void clearAmount() {
        events.onNext(CLEAR);
    }
}
