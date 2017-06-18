package cn.jifit.tv.bigscreen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by addler on 2017/6/8.
 */

public class Fly {
    public int speed = 500;    // move speed * viewport/10000 per second
    public String content = "";   // content on the fly
    public int x = 0, y = 0;   // current position
    public int w = 0, h = 0;
    //    int color = 0;
    public int start_x = 0, start_y = 0;
    public long start_time = 0;

    //notice x, y left_bottom, (0,0) viewpoint left_top
    public Fly(int x, int y, int w, int h, long time, int speed, String content){
        this.speed = speed;
        this.start_x = x;
        this.start_y = y;
        this.w = w;
        this.h = h;
        this.start_time = time;
        this.content = content;
    }
    public Fly update(long time, int vp_width){
        this.x = this.start_x - Math.round((time - this.start_time) * speed * vp_width / 10000000);
        this.y = this.start_y;
        return this;
    }

    // outbound of screen right
    public boolean rightOutRect(int viewpoint_width){
        if (this.x + this.w + 300 < viewpoint_width) return true;
        return false;
    }

    // all part left out of screen
    public boolean leftOutRect(){
        if (this.x + this.w + 300 < 0) return true;
        return false;
    }

    public void draw(Canvas canvas, Paint paint, Bitmap header, Bitmap tailer){
        // header:103px 138px, tailer:115px 138px
        if (this.y < 10) return;
        canvas.drawBitmap(header, this.x, this.y, paint);
        canvas.drawText(this.content, this.x + 103, this.y + 124, paint);
        canvas.drawBitmap(tailer, this.x + 103 + this.w, this.y, paint);
        float width = paint.getStrokeWidth();
        paint.setStrokeWidth(4);
        canvas.drawLine(this.x+103, this.y + 50, this.x + 103 + this.w, this.y + 50, paint);
        canvas.drawLine(this.x+103, this.y + 136, this.x + 103 + this.w, this.y + 136, paint);
        paint.setStrokeWidth(width);
//        Log.d("DRAW FLY", "draw: width:" + width);
    }

}