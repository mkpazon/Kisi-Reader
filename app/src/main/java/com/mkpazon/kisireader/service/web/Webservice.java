package com.mkpazon.kisireader.service.web;

import io.reactivex.Observable;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by mkpazon on 05/08/2017.
 */

public interface Webservice {

    @POST("/locks/5124/access")
    Observable<String> unlock(@Header("Authorization") String authorization);

}
