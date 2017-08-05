package com.mkpazon.kisireader.activity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mkpazon.kisireader.Constants;
import com.mkpazon.kisireader.R;
import com.mkpazon.kisireader.service.web.Webservice;
import com.mkpazon.kisireader.service.web.WebserviceProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CompositeDisposable mDisposables = new CompositeDisposable();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setupDrawer();

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.setNdefPushMessage(null, this, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Timber.d(".onNewIntent");

        setIntent(intent);
    }

    @Override
    protected void onResume() {
        Timber.d(".onResume");
        super.onResume();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    private void processIntent(Intent intent) {
        Timber.d(".processIntent");
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage msg = (NdefMessage) rawMessages[0];
            // record 0 contains the MIME type, record 1 is the AAR, if present
            String payload = new String(msg.getRecords()[0].getPayload());

            Timber.i("payload:" + payload);

            if (Constants.PAYLOAD_UNLOCK.equals(payload)) {
                processUnlock();
            } else if (Constants.PAYLOAD_NOTHING.equals(payload)) {
                // DO NOTHING
            }
        }
    }

    private void processUnlock() {
        Timber.d(".processUnlock");
        Webservice webservice = WebserviceProvider.getWebService();
        mDisposables.add(webservice.unlock(Constants.CONSTANT_AUTHORIZATION_HEADER)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        Timber.d("[unlock] -> .onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "[unlock] -> .onError");
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("[unlock] -> .onComplete");
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposables.dispose();
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}