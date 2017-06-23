package com.example.misakisuede.danmutest;

import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.VideoView;

import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class BarrageActivity extends AppCompatActivity {

    private  VideoView videoView;
    private boolean showDanmaku;
    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    LinearLayout linearLayout;
    Button button;
    EditText editText;

    private BaseDanmakuParser parser =new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrage);
        videoView= (VideoView) findViewById(R.id.Demo_Video_view);
        videoView.setVideoPath(Environment.getExternalStorageDirectory()+"/Logan.mp4");
        videoView.start();
        //获取控件，实例化
        danmakuView  = (DanmakuView) findViewById(R.id.Demo_danmakuView);
        //调用enableDanmakuDrawingCache()方法提升绘制效率
        danmakuView.enableDanmakuDrawingCache(true);
        //调用了setCallback()方法来设置回调函数。
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku=true;
                danmakuView.start();
                generateSomeDanmaku();

            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });

        danmakuContext=DanmakuContext.create();
        danmakuView.prepare(parser,danmakuContext);

        linearLayout = (LinearLayout) findViewById(R.id.Dome_ll);
        button= (Button) findViewById(R.id.send);
        editText= (EditText) findViewById(R.id.edit_text);

        danmakuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayout.getVisibility()==View.GONE){
                    linearLayout.setVisibility(View.VISIBLE);
                }else {
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    addDanmaku(content, true);
                    editText.setText("");
                }
            }
        });

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if (i == View.SYSTEM_UI_FLAG_VISIBLE) {
                    onWindowFocusChanged(true);
                }
            }
        });


    }

    /**
     * 向弹幕view中添加一条弹幕
     * @param content 弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    private void addDanmaku(String content,boolean withBorder){
         BaseDanmaku danmaku =danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
         danmaku.text=content;
         danmaku.padding=5;
         danmaku.textSize=SpToPx(20);
         danmaku.textColor= Color.WHITE;
        danmaku.setTime(danmakuView.getCurrentTime());
        if(withBorder){
            danmaku.borderColor=Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (showDanmaku){
                    int time = new Random().nextInt(300);
                    String content=" "+time+time;
                    addDanmaku(content,true);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

       public  int SpToPx(float spValue){
           final  float fontScale =getResources().getDisplayMetrics().scaledDensity;
           return (int) (spValue * fontScale + 0.5f);
       }


    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
                    View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
