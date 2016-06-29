/**
 * Copyright (c) 2015 Wirecard. All rights reserved.
 * <p>
 * Accept SDK for Android
 */
package com.wirecard.accept.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.wirecard.accept.R;
import com.wirecard.accept.presenters.TransactionHistioryPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import nucleus.factory.RequiresPresenter;

@RequiresPresenter(TransactionHistioryPresenter.class)
public class TransactionsHistoryActivity extends BaseActivity<TransactionHistioryPresenter> {
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.loading)
    View loading;

    public View getLoading() {
        return loading;
    }

    public ListView getListView() {
        return listView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        getPresenter().takeView(this);
        getPresenter().initView(this);
    }
}
