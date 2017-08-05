package com.mkpazon.kisireader;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by mkpazon on 05/08/2017.
 */
public final class Configuration {

    private Configuration() {

    }

    static void init(Context context) {
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }

    public static void configApiHandler(OkHttpClient.Builder httpClientBuilder) {
        httpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(httpLoggingInterceptor);
    }
}

