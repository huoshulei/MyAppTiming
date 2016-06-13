package com.aspsine.myapptiming;

import android.os.Handler;

/**
 * Created by Administrator on 2016/6/13.
 * 貌似重点店在这儿
 */
public abstract class MyRunnable implements Runnable {
    Handler mHandler;
    int time = 0;

    public MyRunnable(Handler handler, int time) {
        mHandler = handler;
        this.time = time;
    }

    @Override
    public void run() {
        time = time - 1;
        turnoverTime(time);
        if (time == 30) {
            onPlayNotification();
        }
        if (time > 0) {
            mHandler.postDelayed(this, 1000);
        } else {
            overTime();
        }

    }

    protected abstract void onPlayNotification();

    protected abstract void overTime();

    public abstract void turnoverTime(int time);

}
