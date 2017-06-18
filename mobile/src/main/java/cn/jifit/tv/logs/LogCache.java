package cn.jifit.tv.logs;

import java.util.ArrayList;

/**
 * Created by addler on 2017/6/6.
 */

public class LogCache {
    static LogCache cache;
    private ArrayList<String> stick = new ArrayList<String>();
    private ArrayList<String> flash = new ArrayList<String>();
    public static LogCache getInstance(){
        if (cache == null){
            cache = new LogCache();
        }
        return cache;
    }

    private LogCache(){

    }

    public void update(){
        flash.clear();
    }
    public void addStick(String s){
        stick.add(s);
    }
    public void addflash(String s){
        flash.add(s);
    }
    public ArrayList<String> getStick() { return stick; }
    public ArrayList<String> getFlash(){
        return flash;
    }
}
