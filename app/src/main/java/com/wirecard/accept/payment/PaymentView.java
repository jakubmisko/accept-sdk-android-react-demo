package com.wirecard.accept.payment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wirecard.accept.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.model.Payment;

/**
 * Created by jakub on 18.06.2016.
 */
//TODO ListView rather than relative???
public class PaymentView extends RelativeLayout {
    @BindView(R.id.title)
    private TextView title;
    @BindView(R.id.amount)
    private TextView amount;
    @BindView(R.id.status)
    private TextView status;

    public PaymentView(Context context) {
        super(context);
    }

    public PaymentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaymentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_payment_history, this, true);
        ButterKnife.bind(this);
    }

    public void setPaymentRow(Payment payment) {
        //TODO resource with parameters
        //String titleStr = String.format(getContext().getString(R.string.payment_title), payment.getCardHolderFirstName(), payment.getCardHolderLastName());
        title.setText(payment.getCardHolderFirstName()+" "+ payment.getCardHolderLastName());
        amount.setText(payment.getTotalAmount().toString());
        status.setText(payment.getStatus().name());
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getAmount() {
        return amount;
    }

    public TextView getStatus() {
        return status;
    }
}
