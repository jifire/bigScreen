package cn.jifit.tv.beacon.rest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.Callable;

import cn.jifit.tv.beacon.Bracelet;
import cn.jifit.tv.beacon.SingleThreadQueue;
import cn.jifit.tv.beacon.Snapshot;
import cn.jifit.tv.beacon.Weather;
import cn.jifit.tv.beacon.WorkFactory;
import cn.jifit.tv.beacon.weather.CityDayWeather;
import cn.jifit.tv.beacon.weather.CityWeather;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by addler on 2017/6/14.
 */

public class BeaconRest {

    private BeaconService service = null;

    private BeaconRest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://beacon.jifit.cn/api/")
                .build();

        service = retrofit.create(BeaconService.class);
    }
    private static class SingletonHelper{
        private static final BeaconRest INSTANCE = new BeaconRest();
    }
    public static BeaconRest getInstance(){
        return SingletonHelper.INSTANCE;
    }

    public void braclets(){
        Call<ResponseBody> call = service.braclets();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //{protocol=http/1.1, code=200, message=OK, url=http://beacon.jifit.cn/api/braclets}
                String bodyString;
				try {
					bodyString = new String(response.body().bytes(), Charset.forName("UTF-8"));
                    JSONArray array = new JSONArray(bodyString);
                    if (array != null){
                        for (int i = 0; i < array.length(); i++){
                            String cname = array.getJSONObject(i).getString("cname");
                            String mac = array.getJSONObject(i).getString("braceletID");
                            Bracelet.getInstance().update(mac, cname);
                        }
                    }
//					System.out.println(bodyString);
				} catch (Exception e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
            }

        });
    }

    public void weather(){
        Call<ResponseBody> call = service.weather();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String bodyString;
                try{
                    bodyString = new String(response.body().bytes(), Charset.forName("UTF-8"));
                    JSONObject data = new JSONObject(bodyString);
                    if (data != null){
                        CityWeather weather = new CityWeather();
                        weather.time = data.getString("time");
                        weather.update_time = data.getString("create_time");
                        weather.areaCode = data.getJSONObject("cityInfo").getString("c1");
                        weather.areaName = data.getJSONObject("cityInfo").getString("c3");
                        weather.cityName = data.getJSONObject("cityInfo").getString("c5");
                        weather.provinceName = data.getJSONObject("cityInfo").getString("c7");
                        JSONObject now = data.getJSONObject("now");
                        weather.now_pm2_5 = now.getJSONObject("aqiDetail").getString("pm2_5");
                        weather.now_pm10 = now.getJSONObject("aqiDetail").getString("pm10");
                        weather.now_aqi = now.getString("aqi");
                        weather.now_temperature = now.getString("temperature");
                        weather.now_temperature_time = now.getString("temperature_time");
                        weather.now_weather = now.getString("weather");
                        weather.now_sd = now.getString("sd");
                        weather.now_wind_direction = now.getString("wind_direction");
                        weather.now_wind_power = now.getString("wind_power");
                        weather.today = CityDayWeather.build(data.getJSONObject("today"));
                        weather.tomorrow = CityDayWeather.build(data.getJSONObject("tomorrow"));
                        Weather.getInstance().addWeather(weather);
                    }

                } catch (Exception e){
                    // TODO
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    public void postData(Snapshot ss) {
        final Snapshot cache = ss;
        String json = "{";
        JSONObject obj = new JSONObject();
        try {
            obj.put("time", ss.minute);
            obj.put("oa", ss.beacon);
            JSONArray da = new JSONArray();
            Iterator<String> it = ss.members.iterator();
            while (it.hasNext()){
                da.put(it.next());
            }
            obj.put("da", da);
            System.out.println(obj.toString());
            RequestBody body = RequestBody.create(
                    okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    obj.toString());
            // serviceCaller is the interface initialized with retrofit.create...
            Call<ResponseBody> call = service.postJson(body);
            call.enqueue(new Callback<ResponseBody>(){

                @Override
                public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // TODO Auto-generated method stub
                    String bodyString;
                    try {
                        bodyString = new String(response.body().bytes(), Charset.forName("UTF-8"));
                        JSONObject data = new JSONObject(bodyString);
                        int code = data.getInt("code");
                        if (code == 200){
                            Callable task = WorkFactory.getInstance().getCallable(WorkFactory.TYPE.MI_BEACON_DATA_AFTER_POST_WORK, cache);
                            SingleThreadQueue.getInstance().submit(task);
                            Log.d("Code", "OK");
                        }

                    } catch (Exception e) {
                        // TODO
                    }
                }

            });
        } catch (JSONException e) {
        }

    }

}
