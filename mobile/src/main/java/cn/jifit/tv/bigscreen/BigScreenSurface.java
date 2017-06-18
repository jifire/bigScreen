package cn.jifit.tv.bigscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import cn.jifit.tv.beacon.KeyValueCache;
import cn.jifit.tv.logs.LogCache;

/**
 * Created by addler on 2017/6/5.
 */

public class BigScreenSurface extends SurfaceView implements SurfaceHolder.Callback{
    private BigScreenThread bsThread;
    private BigScreenDrawer bsDrawer;
    public int status = 0;
    private long second = 0;

    public BigScreenSurface(Context context)  {
        super(context);

        // Make Game Surface focusable so it can handle events. .
        this.setFocusable(true);

        // Sét callback.
        this.getHolder().addCallback(this);
        Rect surface = this.getHolder().getSurfaceFrame();
        LogCache.getInstance().addStick("Surface Frame: " + surface.width() + " x " + surface.height());

    }
    public void surfaceCreated(SurfaceHolder holder) {
        //TODO: Load Resource
        this.bsDrawer = new BigScreenDrawer();
        this.bsDrawer.loadBackground(this);
        this.bsDrawer.loadOnTheFly(this);

//        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.chibi1);
//        this.chibi1 = new ChibiCharacter(this,chibiBitmap1,100,50);

        this.bsThread = new BigScreenThread(this, holder);
        this.bsThread.setRunning(true);
        this.bsThread.start();
        Log.d("H", "surface Created");
    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        long now = System.currentTimeMillis();
        if (now - second > 1000){
            Log.d("Surface", "draw: " + canvas.getWidth() + " x " + canvas.getHeight());
            second = now;
        }
        LogCache.getInstance().update();
//        String[] ss = KeyValueCache.getInstance().toStrings();
//        for (int i = 0; i < ss.length; i++){
//            LogCache.getInstance().addflash(ss[i]);
//        }
        Canvas c = this.bsDrawer.getCanvas();
        if (c != null){
            if (status % 2 == 0) {
                //TODO: draw background
                this.bsDrawer.drawBackground(c);
                //TODO: draw welcame
                this.bsDrawer.drawOnTheFly(c);
                //TODO: draw weather
                this.bsDrawer.drawWeather(c);

            } else {
                this.bsDrawer.drawHeart(c);
            }

//            this.bsDrawer.drawLogs(c, 100, 100);

            Bitmap b = this.bsDrawer.getBoard();

            canvas.drawBitmap(b, new Rect(0, 0, b.getWidth(), b.getHeight()),
                    new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);

        }
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry= true;
        while(retry) {
            try {
                this.bsThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.bsThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry= true;
        }
    }

    public BigScreenThread getThread(){
        return this.bsThread;
    }

}
