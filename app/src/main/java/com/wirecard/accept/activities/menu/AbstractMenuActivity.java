package com.wirecard.accept.activities.menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.wirecard.accept.R;
import com.wirecard.accept.activities.amount.NumpadFragment;
import com.wirecard.accept.activities.base.BaseActivity;
import com.wirecard.accept.activities.history.TransactionsHistoryFragment;
import com.wirecard.accept.activities.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.wirecard.accept.sdk.AcceptSDK;
import nucleus.presenter.RxPresenter;

/**
 * Created by jakub on 02.04.2016.
 */
public abstract class AbstractMenuActivity<P extends RxPresenter> extends BaseActivity<P> {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private SwitchCompat usb, contactless;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        //apply onclick listeners for buttons
        ButterKnife.bind(this);
        configureToolbar();
        //set menu items listeners
        navigationView.setNavigationItemSelectedListener(this::handleMenuItemClick);
        NumpadFragment numpadFragment = new NumpadFragment();
        recplaceFragment(numpadFragment);

        usb = (SwitchCompat) navigationView.findViewById(R.id.switch_usb);
        contactless = (SwitchCompat) navigationView.findViewById(R.id.switch_contactless);
//        firstName.setText("Jakub");
//        lastName.setText("Misko");


    }

    private void configureToolbar() {
        //TODO on button press show drawer
        toolbar.setTitle("New Payment");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setElevation(4f);
    }

    protected boolean handleMenuItemClick(MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.new_trx:
                toolbar.setTitle("New payment");
                fragment = new NumpadFragment();
                break;
            case R.id.trx_history:
                toolbar.setTitle("Transactions history");
                fragment = new TransactionsHistoryFragment();
                break;
            case R.id.logout:
                AcceptSDK.logout();
                Toast.makeText(this, "Bye", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
        }
        return recplaceFragment(fragment);
    }

    protected boolean recplaceFragment(Fragment fragment) {
        drawerLayout.closeDrawers();
        FragmentManager fragmentManager = getFragmentManager();
        //todo not replace if fragment is already there
        if (fragment != null && fragment != fragmentManager.findFragmentById(R.id.frame)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame, fragment);
            transaction.commit();
            return true;
        }
        return false;
    }

    public boolean isUsbDevice(){
        return usb.isChecked();
    }

    public boolean isContactless(){
        return contactless.isChecked();
    }
}
