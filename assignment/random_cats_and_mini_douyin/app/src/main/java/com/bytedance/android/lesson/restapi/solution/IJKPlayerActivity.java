package com.bytedance.android.lesson.restapi.solution;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bytedance.android.lesson.restapi.solution.player.VideoPlayerIJK;
import com.bytedance.android.lesson.restapi.solution.player.VideoPlayerListener;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;



public class IJKPlayerActivity extends AppCompatActivity {
    private static final String TAG ="IJK" ;
    private VideoPlayerIJK ijkPlayer;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private SeekBar seekBar;
    private String video_url;
    public static final int PROGRESS_CHANGED = 1;
    public Handler handler = new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ijkplayer);
        setTitle("ijkPlayer");
        Bundle bundle = this.getIntent().getExtras();
        video_url = bundle.getString("url");


        ijkPlayer = findViewById(R.id.ijkPlayer);
        seekBar = findViewById(R.id.seekbar);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PROGRESS_CHANGED:
                            refresh();
                            handler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
                        break;
                }

            }
        };
        handler.sendEmptyMessage(PROGRESS_CHANGED);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO ex1-2: 这里应该调用哪个函数呢
                if(fromUser == true){
                    float pr = (float)progress/100;
                    System.out.println("当前:"+pr);
                    Long pos = ijkPlayer.getDuration() * progress/100;
                    ijkPlayer.seekTo(pos);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        ijkPlayer.setListener(new VideoPlayerListener());

        int i = video_url.indexOf("s");
        video_url = video_url.substring(0,i) + video_url.substring(i+1);
        ijkPlayer.setVideoPath(video_url);


        ijkPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ijkPlayer.isPlaying()){
                    ijkPlayer.pause();
                    Toast.makeText(IJKPlayerActivity.this,"暂停",Toast.LENGTH_SHORT).show();
                }
                else{
                    ijkPlayer.start();
                    Toast.makeText(IJKPlayerActivity.this,"继续",Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.buttonPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ijkPlayer.start();
            }
        });

        findViewById(R.id.buttonPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ijkPlayer.pause();
            }
        });

        findViewById(R.id.buttonChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //变成横屏了
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                }
            }
        });

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(null != handler) {
            handler = null;
        }
    }
    private String getVideoPath() {
        return "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";
//        return "android.resource://" + this.getPackageName() + "/" + resId;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ijkPlayer.isPlaying()) {
            ijkPlayer.stop();
        }

        IjkMediaPlayer.native_profileEnd();
    }
    private void refresh() {
        long current = ijkPlayer.getCurrentPosition() / 1000;
        long duration = ijkPlayer.getDuration() / 1000;
        Log.v("zzw", current + " " + duration);
        if (duration != 0) {
            seekBar.setProgress((int) (current * 100 / duration));
        }

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
    }

}
