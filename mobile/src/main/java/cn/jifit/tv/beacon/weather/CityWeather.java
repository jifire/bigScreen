package cn.jifit.tv.beacon.weather;

/**
 * Created by addler on 2017/6/15.
 */

public class CityWeather {
    public String areaCode = null;
    public String areaName = "";    //朝阳
    public String cityName = "";
    public String provinceName = "";
    public String update_time = null;
    public String time = null;      //气象台预报时间
    public String now_pm2_5 = null;
    public String now_pm10 = null;
    public String now_aqi = null;
    public String now_temperature = null;
    public String now_temperature_time = null;
    public String now_weather = null;
    public String now_sd = null;
    public String now_wind_direction = null;
    public String now_wind_power = null;
    public CityDayWeather today = null;
    public CityDayWeather tomorrow = null;
}
