package com.wirecard.accept.activities.history;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wirecard.accept.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.model.Payment;

/**
 * Created by super on 18.12.2016.
 */

public class PaymentArrayAdapter extends ArrayAdapter<Payment> {
    private Context context;

    PaymentArrayAdapter(Context context) {
        super(context, R.layout.row_payment_history);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.row_payment_history, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Payment payment = getItem(position);
        //TODO resources
        viewHolder.title.setText(payment.getCardHolderFirstName() + " " + payment.getCardHolderLastName());
        viewHolder.amount.setText(payment.getTotalAmount().toString());
        viewHolder.status.setText(payment.getStatus().name());

        return convertView;
    }

    public class ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.status)
        TextView status;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}