package cn.jifit.tv.beacon.weather;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by addler on 2017/6/15.
 */

public class City3hourWeather {
    public String day = null;
    public String hour = null;
    public String temperature = null;
    public String temperature_min = null;
    public String temperature_max = null;
    public String wind_direction = null;
    public String wind_power = null;
    public String weather = null;

    public static City3hourWeather build(JSONObject data, String day) {
        if (data == null) return null;
        City3hourWeather hours = new City3hourWeather();
        hours.day = day;
        try {
            hours.hour = data.getString("hour");
            hours.temperature = data.getString("temperature");
            hours.temperature_min = data.getString("temperature_min");
            hours.temperature_max = data.getString("temperature_max");
            hours.wind_direction = data.getString("wind_direction");
            hours.wind_power = data.getString("wind_power");
            hours.weather = data.getString("weather");
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        return hours;
    }

}
