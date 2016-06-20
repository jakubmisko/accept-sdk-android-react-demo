package com.wirecard.accept.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import com.wirecard.accept.R;

import java.util.List;

import de.wirecard.accept.sdk.model.Payment;

/**
 * Created by jakub on 18.06.2016.
 */
public class PaymentAdapter extends ArrayAdapter<Payment> {
    public PaymentAdapter(Context context, int resource) {
        super(context, resource);
    }

    //    public PaymentAdapter(Context context, int resource, int textViewResourceId) {
//        super(context, resource, textViewResourceId);
//    }
//
//    public PaymentAdapter(Context context, int resource, Payment[] objects) {
//        super(context, resource, objects);
//    }
//
//    public PaymentAdapter(Context context, int resource, int textViewResourceId, Payment[] objects) {
//        super(context, resource, textViewResourceId, objects);
//    }
//
    public PaymentAdapter(Context context, int resource, List<Payment> objects) {
        super(context, resource, objects);
    }
//
//    public PaymentAdapter(Context context, int resource, int textViewResourceId, List<Payment> objects) {
//        super(context, resource, textViewResourceId, objects);
//    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PaymentView payment = (PaymentView) convertView;
        if (payment == null) {
            payment = (PaymentView) ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate( R.layout.payment_history, parent);
            //(PaymentView) PaymentView.inflate(parent.getContext(), R.layout.payment_history, parent);
        }
        payment.setPaymentRow(getItem(position));
        return payment;
    }
}
