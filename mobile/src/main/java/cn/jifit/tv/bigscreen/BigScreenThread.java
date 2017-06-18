package cn.jifit.tv.bigscreen;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.concurrent.Callable;

import cn.jifit.tv.beacon.KeyValueCache;
import cn.jifit.tv.beacon.SingleThreadQueue;
import cn.jifit.tv.beacon.Snapshot;
import cn.jifit.tv.beacon.WorkFactory;
import cn.jifit.tv.beacon.rest.BeaconRest;

/**
 * Created by addler on 2017/6/5.
 */

public class BigScreenThread extends Thread{
    private boolean running;
    private BigScreenSurface bsSurface;
    private SurfaceHolder bsHolder;
    public BigScreenThread(BigScreenSurface surface, SurfaceHolder holder){
        this.bsSurface = surface;
        this.bsHolder = holder;
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        //last work time by second
        long work_async_braclet = 0;
        long work_async_weather = 0;
        long work_async_postdata = 0;
        long work_async_remove_data = 0;
        //interval to work
        long interval_async_braclet = 60;
        long interval_async_weather = 900;
        long interval_async_postdata = 10;
        long interval_async_remove_data = 10;

        long now = startTime / 1000;
        boolean willDraw = true;
        while (running){
            Canvas canvas = null;
            now = System.currentTimeMillis() / 1000;
            willDraw = true;
            if (willDraw){
                try {
                    canvas = this.bsHolder.lockCanvas();
                    if (canvas != null){
                        synchronized (canvas){
                            this.bsSurface.draw(canvas);
                        }
                    }
                }catch (Exception e){
                    //Do nothing
                }finally {
                    if (canvas != null){
                        this.bsHolder.unlockCanvasAndPost(canvas);
                        willDraw = false;
                    }
                }

            }else{
            }

            if (now - work_async_braclet > interval_async_braclet){
                BeaconRest.getInstance().braclets();
                work_async_braclet = now;
            }
            if (now - work_async_weather > interval_async_weather){
                BeaconRest.getInstance().weather();
                work_async_weather = now;
            }
            if (now - work_async_postdata > interval_async_postdata){
                Snapshot ss = KeyValueCache.getInstance().getData(5);
                if (ss != null){
                    BeaconRest.getInstance().postData(ss);
                }
                work_async_postdata = now;
            }
            if (now - work_async_remove_data > interval_async_remove_data){
                Snapshot ss = KeyValueCache.getInstance().getData(40, true);
                if (ss != null){
                    Callable task = WorkFactory.getInstance().getCallable(WorkFactory.TYPE.MI_BEACON_DATA_REMOVE_WORK, ss);
                    SingleThreadQueue.getInstance().submit(task);
                }
                interval_async_remove_data = now;
            }
        }
    }
    public void setRunning(boolean running){
        this.running = running;
    }

}
