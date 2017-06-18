package cn.jifit.tv.bigscreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by addler on 2017/6/5.
 */

public class OnTheFly {

    private ArrayList<Fly> cache = new ArrayList<Fly>();

    // Singleton
    private OnTheFly() {}
    private static class SingletonHelper{
        private static final OnTheFly INSTANCE = new OnTheFly();
    }
    public static OnTheFly getInstance(){
        return SingletonHelper.INSTANCE;
    }

    public void addFly(int x, int y, int w, int h, long time, int speed, String content){
        Fly fly = new Fly(x, y, w, h, time, speed, content);
        cache.add(fly);
    }
    public List<Fly> getFlys(){
        return cache;
    }
}
