package com.mkpazon.kisireader.service.web;

import android.content.Context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.mkpazon.kisireader.App;
import com.mkpazon.kisireader.Configuration;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by mkpazon on 05/08/2017.
 */

public class WebserviceProvider {
    private static OkHttpClient mOkHttpClient;
    private static ObjectMapper mObjectMapper;

    private static final String BASE_URL = "https://api.getkisi.com/";
    private static Webservice webservice;
    private static WebserviceProvider webserviceProvider = new WebserviceProvider();

    protected void init(Context context) {
        if (!(context instanceof App)) {
            context = context.getApplicationContext();
        }
        initOkhttpClient(context);
        Retrofit retrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(getDefaultObjectMapper()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        createWebService(retrofit);
    }

    public static Webservice getWebService() {
        if (webservice == null) {
            webserviceProvider.init(App.getInstance().getApplicationContext());
        }
        return webservice;
    }

    private void createWebService(Retrofit retrofit) {
        webservice = retrofit.create(Webservice.class);
    }

    public static ObjectMapper getDefaultObjectMapper() {
        // Configure global Jackson configuration
        if (mObjectMapper == null) {
            mObjectMapper = new ObjectMapper();
            mObjectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        }
        return mObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    private static void initOkhttpClient(Context context) {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            Configuration.configApiHandler(httpClientBuilder);
            mOkHttpClient = httpClientBuilder.build();
        }
    }

}
