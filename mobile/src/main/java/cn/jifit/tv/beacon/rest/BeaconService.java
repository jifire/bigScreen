package cn.jifit.tv.beacon.rest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by addler on 2017/6/14.
 */

public interface BeaconService {
    @GET("braclets")
    Call <ResponseBody> braclets();

    @GET("weather")
    Call <ResponseBody> weather();

    @POST("beacondata")
    Call<ResponseBody> postJson(@Body RequestBody body);
//    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
//    //serviceCaller is the interface initialized with retrofit.create...
//    Call<ResponseBody> response = serviceCaller.login("loginpostfix", body);


}
