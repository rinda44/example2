package example.myapplication19;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    final String TAG = "测试 PlayActivity";
    final int REQ_OPEN_FILE = 101;

    VideoView videoView;  //播放主界面
    SeekBar seekBar;      //下方进度条

    Handler msgHandler;
    Timer timer;
    TimerTask timerTask;
    ImageButton imageButton;
    View controlBar;//控制面板


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permissions.check(this);   //权限检查

        videoView = (VideoView)findViewById(R.id.id_videoview);

        seekBar = (SeekBar)findViewById(R.id.id_seekbar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                // 控制视频跳转到目标位置
                if(videoView.isPlaying())
                {
                    int progress = seekBar.getProgress();
                    int position = progress * videoView.getDuration()/100;
                    videoView.seekTo(position);
                }
            }
        });

        msgHandler = new MyHandler();

        imageButton = (ImageButton)findViewById(R.id.id_play_pause);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(videoView.isPlaying())
                {
                    Log.d(TAG, "正在播放，现在暂停...");
                    videoView.pause();
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_play));
                }
                else
                {
                    Log.d(TAG, "不在播放，现在继续...");
                    videoView.start();
                    imageButton.setImageDrawable(getDrawable(R.drawable.ic_pause));
                }
            }
        });
        controlBar = findViewById(R.id.id_controlbar);
        videoView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                Log.d(TAG, "点中画面。。");
                if(controlBar.getVisibility() == View.GONE)
                {
                    controlBar.setVisibility(View.VISIBLE);
                }
                else
                {
                    controlBar.setVisibility(View.GONE);
                    Log.d(TAG, "隐藏播放面板。。");
                }
                return false;
            }
        });

        // 接受外部调用
        Intent intent = getIntent();
        Uri mediaUri = intent.getData();
        if(mediaUri != null)
        {
            videoView.setVideoURI(mediaUri);
            videoView.start();
        }


    }
    public void openfile(View view)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, REQ_OPEN_FILE);
    }
    protected  void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQ_OPEN_FILE)
        {
            if(resultCode == RESULT_OK)
            {
                Uri mediaUri = data.getData();
                videoView.setVideoURI(mediaUri);
                videoView.start();
            }
        }
    }
     // 添加定时器任务类
    private class MyTimerTask extends TimerTask
    {

        @Override
        public void run()
        {
            if(!videoView.isPlaying()) return;

            //取得当前播放进度
            int duration = videoView.getDuration();
            int position = videoView.getCurrentPosition();

            //发消息给UI线程
            Message msg = new Message();
            msg.what = 1;
            msg.arg1 = duration;
            msg.arg2 = position;
            msgHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onStart()
    {
        //设置为横屏模式
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onStart();

        if(timer == null)
        {
            //启动定时器
            timerTask = new MyTimerTask();
            timer = new Timer();
            timer.schedule(timerTask, 500, 500);

        }
    }

    @Override
    protected void onStop()
    {
        if(timer != null)
        {
            timer.cancel();
            timer = null;
        }
        super.onStop();
    }

    private class MyHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1)
            {
                //// 从消息里取出进度数据，然后更新UI
                int duration = msg.arg1;
                int position = msg.arg2;
                showProgress(duration, position);
            }
        }
    }
    public void showProgress(int duration, int position)
    {
        //转化成百分比
        int percent = position * 100 / duration;
        seekBar.setProgress(percent);
    }
}
