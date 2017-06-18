package cn.jifit.tv.beacon;

import java.util.ArrayList;

import cn.jifit.tv.beacon.weather.CityWeather;

/**
 * Created by addler on 2017/6/15.
 */

public class Weather {

    private ArrayList<CityWeather> cache = new ArrayList<CityWeather>();
    private Weather(){
    }
    private static class SingletonHelper{
        private static final Weather INSTANCE = new Weather();
    }
    public static Weather getInstance(){
        return SingletonHelper.INSTANCE;
    }

    public void addWeather(CityWeather weather){
        if (weather != null){
            String areaCode = weather.areaCode;
            if (areaCode != null){
                for (int i = 0; i < cache.size(); i++){
                    if (cache.get(i).areaCode.equals(areaCode)){
                        cache.remove(i);
                    }
                }
            }
            cache.add(weather);
        }
    }

    public CityWeather getWeather(String areaCode){
        for (int i = 0; i < cache.size(); i++){
            if (cache.get(i).areaCode.equals(areaCode)){
                return cache.get(i);
            }
        }
        return null;
    }

}
