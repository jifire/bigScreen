package cn.jifit.tv.beacon.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by addler on 2017/6/15.
 */

public class CityDayWeather {
    public String day = null;
    public String day_weather = null;
    public String night_weather = null;
    public String air_press = null;
    public String jiangshui = null;
    public String day_wind_power = null;
    public String night_wind_power = null;
    public String sun_begin_end = null;
    public String ziwaixian = null;
    public int weekday = 0;
    public String night_air_temperature = null;
    public String day_air_temperature = null;
    public String day_wind_direction = null;
    public String night_wind_direction = null;
    public City3hourWeather[] hours = null;

    public static CityDayWeather build(JSONObject data){
        if (data == null) return null;
        CityDayWeather day = new CityDayWeather();
        try {
            day.day = data.getString("day");
            day.day_weather = data.getString("day_weather");
            day.night_weather = data.getString("night_weather");
            day.air_press = data.getString("air_press");
            day.jiangshui = data.getString("jiangshui");
            day.day_wind_power = data.getString("day_wind_power");
            day.night_wind_power = data.getString("night_wind_power");
            day.sun_begin_end = data.getString("sun_begin_end");
            day.ziwaixian = data.getString("ziwaixian");
            day.night_air_temperature = data.getString("night_air_temperature");
            day.day_air_temperature = data.getString("day_air_temperature");
            day.day_wind_direction = data.getString("day_wind_direction");
            day.night_wind_direction = data.getString("night_wind_direction");
            day.weekday = data.getInt("weekday");
            JSONArray array = data.getJSONArray("3hourForcast");
            if (array != null){
                day.hours = new City3hourWeather[array.length()];
                for (int i = 0; i < array.length(); i++){
                    JSONObject hours = array.getJSONObject(i);
                    if (hours != null){
                        day.hours[i] = City3hourWeather.build(hours, day.day);
                    }else{
                        day.hours[i] = null;
                    }
                }
            }else{
                day.hours = new City3hourWeather[0];
            }

        } catch (JSONException e) {
//            e.printStackTrace();
        }
        return day;
    }
}
