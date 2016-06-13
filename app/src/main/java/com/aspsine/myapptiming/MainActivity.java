package com.aspsine.myapptiming;

import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import cn.qqtheme.framework.picker.TimePicker;

public class MainActivity extends AppCompatActivity {
    TextView   tv_time;
    TextView   tv_start;
    TextView   tv_finish;
    MyRunnable mRunnable;
    Handler    mHandler;
    int     time    = 0;
    boolean isPause = false;//是否暂停
    private MediaPlayer mPlayer;
    boolean isRuning = false;//是否运行中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        initView();
        initEvent();

    }

    private void initView() {
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_finish = (TextView) findViewById(R.id.tv_finish);
        Typeface fromAsset = Typeface.createFromAsset(getAssets(), "ttf/typeface.ttf");
        tv_time.setTypeface(fromAsset);
    }

    private void initEvent() {
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRuning) {
                    /**这货就是一个第三方的滚轮选择器*/
                    TimePicker picker = new TimePicker(MainActivity.this, TimePicker.HOUR_OF_DAY);
                    picker.setLineVisible(false);
                    picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
                        @Override
                        public void onTimePicked(String hour, String minute) {
                            tv_time.setText(hour + ":" + minute + ":" + "00");
//                        time = Integer.valueOf(hour) * 60 * 60 + Integer.valueOf(minute) * 60;
                        }
                    });
                    picker.show();
                }
            }
        });
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPause) {
                    time = TimeUtil.getIntTime(tv_time.getText()
                            .toString());
                    if (time > 0) {
                        isRuning = true;
                        mRunnable = new MyRunnable(mHandler,
                                MainActivity.this.time) {
                            /**
                             * 启动音乐
                             */
                            @Override
                            protected void onPlayNotification() {
                                onPlay();
                            }

                            /**
                             * 结束时调用
                             */
                            @Override
                            protected void overTime() {
                                isPause = false;
                                isRuning = false;
                                mPlayer.stop();
                                tv_start.setText("开始");
                            }

                            /**
                             * 更新显示时间
                             */
                            @Override
                            public void turnoverTime(int time) {
                                tv_time.setText(TimeUtil.getTime(time));
                            }
                        };
                        mHandler.postDelayed(mRunnable, 1000);
                        tv_start.setText("暂停");
                        isPause = true;
                    }

                } else {
                    isPause = false;
                    mHandler.removeCallbacks(mRunnable);
                    tv_start.setText("继续");
                }
            }
        });
        /**结束计时 并归零*/
        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = 0;
                mHandler.removeCallbacks(mRunnable);
                tv_time.setText(TimeUtil.getTime(0));
                if (mPlayer != null)
                    mPlayer.stop();
                isPause = false;
                isRuning = false;
                tv_start.setText("开始");
            }
        });
    }

    /**
     * 音乐播放
     */
    private void onPlay() {
        try {
            AssetFileDescriptor fd = getAssets().openFd("music/om.mp3");
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(fd.getFileDescriptor());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
