package com.mkpazon.kisireader.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.mkpazon.kisireader.Constants;
import com.mkpazon.kisireader.R;
import com.mkpazon.kisireader.service.web.Webservice;
import com.mkpazon.kisireader.service.web.WebserviceProvider;
import com.mkpazon.kisireader.ui.view.SmartAnimationDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class StartActivity extends AppCompatActivity {

    private CompositeDisposable mDisposables = new CompositeDisposable();
    private SmartAnimationDrawable mSmartAnimationDrawable;

    @BindView(R.id.imageView_lock)
    ImageView mIvLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        AnimationDrawable animationDrawable = (AnimationDrawable) ContextCompat.getDrawable(this, R.drawable.lock);
        mSmartAnimationDrawable = new SmartAnimationDrawable(animationDrawable);
        mSmartAnimationDrawable.setOneShot(true);
        mIvLock.setBackground(mSmartAnimationDrawable);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Timber.d(".onNewIntent");
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            processIntent(intent);
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
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        // TODO move this in onNext()
                        mSmartAnimationDrawable.setOnAnimationFinishListener(new SmartAnimationDrawable.OnAnimationFinishListener() {
                            @Override
                            public void onAnimationFinish() {
                                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        mSmartAnimationDrawable.start();
                    }
                })
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
}
