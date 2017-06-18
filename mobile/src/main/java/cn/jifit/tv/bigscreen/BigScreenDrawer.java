package cn.jifit.tv.bigscreen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;

import com.example.addler.apptest2.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import cn.jifit.tv.beacon.Bracelet;
import cn.jifit.tv.beacon.KeyValueCache;
import cn.jifit.tv.beacon.Weather;
import cn.jifit.tv.beacon.weather.CityWeather;
import cn.jifit.tv.logs.LogCache;

/**
 * Created by addler on 2017/6/5.
 */

public class BigScreenDrawer {
    private Bitmap background = null;
    private Bitmap header = null;
    private Bitmap tailer = null;
    private Bitmap board = null;
    private Bitmap weather = null;
    private Bitmap dot = null;
    private Bitmap percent = null;

    private Bitmap heart01 = null;
    private Bitmap heart02 = null;
    private Bitmap heart03 = null;
    private Bitmap heart04 = null;
    private Bitmap heart05 = null;
    private ArrayList<OnTheFly> otfs = new ArrayList<OnTheFly>(16);

    public void loadBackground(BigScreenSurface surface){
        this.background = BitmapFactory.decodeResource(surface.getResources(), R.drawable.white_bg1);
        this.weather = BitmapFactory.decodeResource(surface.getResources(), R.drawable.weather);
        this.dot = BitmapFactory.decodeResource(surface.getResources(), R.drawable.dot);
        this.percent = BitmapFactory.decodeResource(surface.getResources(), R.drawable.percent);
        this.board = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
        this.heart01 = BitmapFactory.decodeResource(surface.getResources(), R.drawable.heart1);
        this.heart02 = BitmapFactory.decodeResource(surface.getResources(), R.drawable.heart2);
        this.heart03 = BitmapFactory.decodeResource(surface.getResources(), R.drawable.heart3);
        this.heart04 = BitmapFactory.decodeResource(surface.getResources(), R.drawable.heart4);
        this.heart05 = BitmapFactory.decodeResource(surface.getResources(), R.drawable.heart5);
    }

    public void loadOnTheFly(BigScreenSurface surface){
        this.header = BitmapFactory.decodeResource(surface.getResources(), R.drawable.white_head);
        this.tailer = BitmapFactory.decodeResource(surface.getResources(), R.drawable.white_tail);
    }
    //load from drawable 1920x1080 = 5760x3240          3
    //load from drawable-xhdpi 1920x1080 = 2880x1620    1.5
    //load from drawable-hdpi 1920x1080 = 3840x2160     2

    public Canvas getCanvas(){
        if (this.board == null) return null;
        Canvas canvas = new Canvas(this.board);
        return canvas;
    }
    public Bitmap getBoard(){
        return board;
    }
    public void drawBackground(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawBitmap(this.background, new Rect(0, 0, this.background.getWidth(), this.background.getHeight()),
                new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
    }
    public void drawHeart(Canvas canvas){
        Bitmap now = this.heart01;
        long time = System.currentTimeMillis() / 8000;
        time = time % 5 + 1;
        if (time == 1) now = heart01;
        if (time == 2) now = heart02;
        if (time == 3) now = heart03;
        if (time == 4) now = heart04;
        if (time == 5) now = heart05;

        canvas.drawBitmap(now, new Rect(0, 0, now.getWidth(), now.getHeight()),
                new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
    }

    //notice draw all in RECT(0, 0, 1920, 1080)
    public void drawOnTheFly(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(80);
        //TODO: set Themes
        //TODO: set fonts
        //TODO: draw all flys on background;

        //TODO: calculating, update flys
        List<Fly> flyes = OnTheFly.getInstance().getFlys();
        long now = System.currentTimeMillis();
        for (int i = 0; i < flyes.size(); i++){
            Fly fly = flyes.get(i);
            fly.update(now, 1920);
            if (fly.leftOutRect()) flyes.remove(i);
        }
        //TODO: if all flys in ViewPort,then create 2 new flys
        boolean toAdd = true;
        for (int i = 0; i < flyes.size(); i++){
            Fly fly = flyes.get(i);
            if (!fly.rightOutRect(1920)) toAdd = false;
        }
        if (toAdd){
            String[] members = KeyValueCache.getInstance().getActiveMembers(30, null);
            if (members.length > 0){
                Set<String> selected = new HashSet<String>();
                for (int i = 0; i < 2; i++){
                    int x = 1920 + ThreadLocalRandom.current().nextInt(0, 400) + i * 200;
                    int ry = ThreadLocalRandom.current().nextInt(0, 22);
                    if (selected.contains("" + ry)) continue;
                    selected.add("" + ry);
                    selected.add("" + (ry + 1));
                    selected.add("" + (ry + 2));
                    selected.add("" + (ry - 2));
                    selected.add("" + (ry - 1));

                    int y = 40 + ry * 40;
                    String name = Bracelet.getInstance().getName(members[ThreadLocalRandom.current().nextInt(0, members.length) ]);
                    if (!name.equals("")){
                        String content = "欢迎"
                                + name;
                        Rect rect = new Rect();
                        paint.getTextBounds(content, 0, content.length(), rect);
                        int w = rect.width();
                        int h = rect.height();
                        OnTheFly.getInstance().addFly(x, y, w, h, now, 600, content);
                    }
//                    LogCache.getInstance().addStick("AddFly: " + content + " x:" + x + " y:" + y + " w:" + w + " h:" + h);
                    //
                }

            }

        }

        //TODO: for each draw flys
        //
        for (int i = 0; i < flyes.size(); i++){
            Fly fly = flyes.get(i);
            LogCache.getInstance().addflash("Fly: " + fly.content + " x:" + fly.x + " y:" + fly.y);
//            canvas.drawBitmap(header, fly.x, fly.y, paint);

            fly.draw(canvas, paint, header, tailer);
        }

//        canvas.drawText(text, 800, 800, paint);
//        Rect rect = new Rect();
//        paint.getTextBounds(text, 0, text.length(), rect);
//        LogCache.getInstance().addflash("TextSize: " + rect.width() + " x " + rect.height());
//        canvas.drawRect(new Rect(100, 100, 100+640, 180), paint);
    }

    // x:59, y:34
    // temperature (370,57) (370,189)
    // percent (372,353)
    // (301,327) (?323, 452) (301, 195) (301, 63)
    public void drawWeather(Canvas canvas){
        //TODO: get weather data
        //TODO: get inner data
        String outter_at = "--";//未获取到  at=air temperature
        CityWeather now_weather = Weather.getInstance().getWeather("101010300");
        String outter_aq = "--";
        String outter_humidity = "--";
        if (now_weather != null){
            outter_at = now_weather.now_temperature;
            outter_aq = now_weather.now_aqi;
            outter_humidity = now_weather.now_sd;
        }
        String inner_at = "--"; //
        String inner_humidity = "--"; //
        String inner_aq = "--";//
        //Drawing
//        Paint paint = new Paint();
        TextPaint paint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        int fontSize = 50;
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(fontSize);

        Rect testpaint = new Rect();
        canvas.drawBitmap(this.weather, new Rect(0, 0, weather.getWidth(), weather.getHeight()), new Rect(59, 34, 59+weather.getWidth(), 34+weather.getHeight()), paint);
        paint.getTextBounds(outter_at, 0, outter_at.length(), testpaint);
        canvas.drawText(outter_at, 301, 55 + fontSize, paint);
        if (!outter_at.equals("--")){
            canvas.drawBitmap(this.dot, new Rect(0, 0, dot.getWidth(), dot.getHeight()), new Rect(370, 57, 370 +dot.getWidth(), 57 +dot.getHeight()), paint);
        }

        canvas.drawText(inner_at, 301, 187 + fontSize, paint);
        if (!inner_at.equals("--")) {
            canvas.drawBitmap(this.dot, new Rect(0, 0, dot.getWidth(), dot.getHeight()), new Rect(370, 189, 370+dot.getWidth(), 189+dot.getHeight()), paint);
        }
        canvas.drawText(inner_humidity, 301, 319 + fontSize, paint);
//        canvas.drawBitmap(this.percent, new Rect(0, 0, percent.getWidth(), percent.getHeight()), new Rect(0, 0, percent.getWidth(), percent.getHeight()), paint);
        canvas.drawText(inner_aq, 301, 444 + fontSize, paint);
    }

    public void drawLogs(Canvas canvas, int x, int y){
        int fontSize = 40;
        int lineHight = 42;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(fontSize);
        ArrayList<String> stick = LogCache.getInstance().getStick();
        ArrayList<String> flash = LogCache.getInstance().getFlash();
        for (int i = 0; i < stick.size(); i++){
            y += lineHight;
            canvas.drawText(stick.get(i), x, y, paint);
        }
        y += lineHight;
        for (int i = 0; i < flash.size(); i++){
            y += lineHight;
            canvas.drawText(flash.get(i), x, y, paint);
        }
        y += lineHight;
        String[] cache = KeyValueCache.getInstance().toStrings();

        y += lineHight;
        canvas.drawText("" + cache.length, x, y, paint);

        for (int i = 0; i < cache.length; i++){
            y += lineHight;
            canvas.drawText(cache[i], x, y, paint);
        }

    }
}
