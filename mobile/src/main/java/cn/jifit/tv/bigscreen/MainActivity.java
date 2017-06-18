package cn.jifit.tv.bigscreen;

import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.util.Log;
import android.view.WindowManager;

import cn.jifit.tv.beacon.MIBeaconServer;
import cn.jifit.tv.beacon.NonBlockHandler;
import cn.jifit.tv.beacon.SingleThreadQueue;
import cn.jifit.tv.logs.LogCache;

public class MainActivity extends AppCompatActivity {

    private BigScreenSurface mainSurface;
    private PowerManager.WakeLock wakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Activity", "onCreate: ");
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        this.mainSurface = new BigScreenSurface(this);
        this.setContentView(mainSurface);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        // Start Beacon Server
        MIBeaconServer server = new MIBeaconServer();
        server.start();

        // Start Selector Handler
        final NonBlockHandler handler = NonBlockHandler.getInstance();
        Thread t = new Thread(handler);
        t.start();

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d("Activity", "onStart: ");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("Activity", "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Activity", "onPause: ");
        this.mainSurface.getThread().setRunning(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putSerializable(STATE_LOCAL_DATE_TIME, mLocalDateTime);
        Log.d("Activity", "onSaveInstanceState: ");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d("Activity", "onStop: ");
        this.mainSurface.getThread().setRunning(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wakeLock.release();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        mLocalDateTime = (LocalDateTime)savedInstanceState.getSerializable(STATE_LOCAL_DATE_TIME);
        Log.d("Activity", "onRestoreInstanceState: ");
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) safeFindViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
        Log.d("Activity", "onBackPressed: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        Log.d("Activity", "onCreateOptionsMenu: ");
        return true;
    }

    public void onDestroy(){
        super.onDestroy();
        mainSurface.getThread().setRunning(false);
        Log.d("Activity", "onDestroy: ");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
            {
                LogCache.getInstance().addStick("Home");
                mainSurface.status = 0;
                return true;
            }
            case KeyEvent.KEYCODE_MENU:
            {
                LogCache.getInstance().addStick("MENU");
                if ((mainSurface.status & 0x00001000) > 0){
                    mainSurface.status = mainSurface.status - 8;
                }else{
                    mainSurface.status = mainSurface.status + 8;
                }
                return true;
            }
            case KeyEvent.KEYCODE_DPAD_LEFT:
            {
                LogCache.getInstance().addStick("LEFT");
                if ((mainSurface.status & 0x00000111) == 7){
                    mainSurface.status = mainSurface.status - 7;
                }else{
                    mainSurface.status = mainSurface.status + 1;
                }
                return true;
            }
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            {
                LogCache.getInstance().addStick("RIGHT");
                if ((mainSurface.status & 0x00000111) == 0){
                    mainSurface.status = mainSurface.status + 7;
                }else{
                    mainSurface.status = mainSurface.status - 1;
                }
                return true;
            }
            case KeyEvent.KEYCODE_BACK:
            {
                LogCache.getInstance().addStick("BACK");
                NonBlockHandler.getInstance().shutdown();
                mainSurface.getThread().setRunning(false);
                SingleThreadQueue.getInstance().shutdown();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
