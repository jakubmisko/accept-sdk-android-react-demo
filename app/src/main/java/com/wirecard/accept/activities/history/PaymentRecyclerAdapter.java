package com.wirecard.accept.activities.history;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wirecard.accept.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.AcceptSDK;
import de.wirecard.accept.sdk.model.Payment;

/**
 * Created by super on 26.03.2017.
 */

public class PaymentRecyclerAdapter extends RecyclerView.Adapter<PaymentRecyclerAdapter.ViewHolder> {
    private List<Payment> payments;
    private Context context;
    private TransactionsHistoryFragment.HistoryPopupMenuCallback popupMenuCallback;

    /**
     * delete stored payments
     */
    public void clear(){
        payments.clear();
//        notifyDataSetChanged();
    }

    /**
     * set new list of payments
     * @param payments list of payments
     */
    public void setPayments(List<Payment> payments){
        this.payments.addAll(payments);
//        notifyDataSetChanged();
    }

    public PaymentRecyclerAdapter(List<Payment> payments, TransactionsHistoryFragment.HistoryPopupMenuCallback popupMenuCallback) {
        this.payments = payments;
        this.popupMenuCallback = popupMenuCallback;
    }

    /**
     * inflate transaction history layout
     * @param parent view
     * @param viewType view type
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_history_material, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * set data from payment to views in row
     * @param holder view holding views in row
     * @param position position of row
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Payment p = payments.get(position);
        holder.title.setText(p.getCardHolderFirstName() + " " + p.getCardHolderLastName());
        holder.amount.setText(p.getTotalAmount().toString());
        holder.status.setImageDrawable(getStatusIcon(p.getStatus()));
    }

    /**
     *
     * @return count of all stored items
     */
    @Override
    public int getItemCount() {
        return payments.size();
    }

    /**
     * get icon drawable based on payment status
     * @param status payment status
     * @return icon representing given status
     */

    private Drawable getStatusIcon(AcceptSDK.Status status) {
        switch (status) {
            case REFUNDED:
            case REVERSED:
                return ContextCompat.getDrawable(context, R.drawable.ic_replay_black_36dp);
            case APPROVED:
                return ContextCompat.getDrawable(context, R.drawable.ic_done_black_36dp);
            case REJECTED:
                return ContextCompat.getDrawable(context, R.drawable.ic_not_interested_black_36dp);
            case PENDING:
                return ContextCompat.getDrawable(context, R.drawable.ic_query_builder_black_36dp);
            case NEW:
            case BOOKBACKED:
            case CHARGEBACKED:
            case CANCELED:
            case PREAUTHORIZED:
            case AUTHORIZED:
                //Implement all statuses by yourself
            default:
                return ContextCompat.getDrawable(context, R.drawable.ic_remove_black_36dp);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_holder)
        TextView title;
        @BindView(R.id.payment_amount)
        TextView amount;
        @BindView(R.id.payment_status)
        ImageView status;
        @BindView(R.id.actions)
        ImageButton imageButton;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            imageButton.setOnClickListener(this::showPopupMenu);
        }

        private void showPopupMenu(View v) {
            PopupMenu popup = new PopupMenu(context, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_transaction_history, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                popupMenuCallback.onItemSelected(getAdapterPosition(), item.getTitle().toString());
                return true;
            });
            popup.show();
        }
    }
}
