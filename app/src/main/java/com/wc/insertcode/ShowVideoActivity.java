package com.wc.insertcode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.wc.utils.DisplayUtils;

public class ShowVideoActivity extends AppCompatActivity {
    private DisplayUtils.NavigationBarContentObserver mObserver;
    private View mNavigationBar;
    //private StandardGSYVideoPlayer mVideoPlayer;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private boolean mIsPrepared = false;
    private ImageView mThumbImageView;
    private View mBgView;
    private View mPlay, mPause;
    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // 当SurfaceView中的Surface被创建的时候被调用
            //在这里我们指定MediaPlayer在当前的Surface中进行播放
            mMediaPlayer.setDisplay(holder);
            //在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了
            mMediaPlayer.prepareAsync();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        DisplayUtils.init(this);
        super.onCreate(savedInstanceState);
        int layoutResID = getLayoutResID();
        View v = getContentView();
        if (layoutResID != 0 || v != null) {
            //将window扩展至全屏，并且不会覆盖状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //避免在状态栏的显示状态发生变化时重新布局
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            final int navigationBarHeight = DisplayUtils.getNavigationBarHeight();
            if (navigationBarHeight != 0) {
                LinearLayout contentView = new LinearLayout(this);
                contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                contentView.setOrientation(LinearLayout.VERTICAL);
                mNavigationBar = new View(this);
                mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                mNavigationBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                View view = layoutResID == 0 ? v : View.inflate(this, layoutResID, null);
                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
                contentView.addView(view);
                contentView.addView(mNavigationBar);
                mObserver = new DisplayUtils.NavigationBarContentObserver(new Handler(Looper.getMainLooper()), this);
                mObserver.setOnNavigationBarChangedListener(new DisplayUtils.NavigationBarContentObserver.OnNavigationBarChangedListener() {
                    @Override
                    public void onNavigationBarChanged(boolean isShow) {
                        if (isShow) {
                            mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
                        } else {
                            mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                        }
                    }
                });
                if (DisplayUtils.isNavigationBarShowing(this)) {
                    mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
                }
                setContentView(contentView);
            } else {
                if (layoutResID == 0) {
                    setContentView(v);
                } else {
                    setContentView(layoutResID);
                }
            }
        }
    }

    protected void initView() {
        mSurfaceView = findViewById(R.id.video_surface);
        mBgView = findViewById(R.id.fl_bg);
        mThumbImageView = findViewById(R.id.iv_bg);
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPlay = findViewById(R.id.iv_play);
        mPause = findViewById(R.id.iv_pause);
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPrepared) {
                    mPlay.setVisibility(View.INVISIBLE);
                    mBgView.setVisibility(View.GONE);
                    mPause.setVisibility(View.VISIBLE);
                    startPlayVideo();
                }
            }
        });
        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mPlay.setVisibility(View.VISIBLE);
                    mPause.setVisibility(View.INVISIBLE);
                    mMediaPlayer.pause();
                }
            }
        });
    }

    protected void initData() {
        //给SurfaceView添加CallBack监听
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mCallback);
        //为了可以播放视频或者使用Camera预览，我们需要指定其Buffer类型
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //下面开始实例化MediaPlayer对象
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("MainActivity", "视频播放完毕");
                stopPlayVideo();
            }
        });
        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                Log.e("MainActivity", "onVideoSizeChanged");
                int videoHeight = mp.getVideoHeight();
                int videoWidth = mp.getVideoWidth();
                int layoutWidth = mSurfaceView.getMeasuredWidth();
                int layoutHeight = mSurfaceView.getMeasuredHeight();
                //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
                //竖屏模式下按视频宽度计算放大倍数值
                float max = Math.max((float) videoWidth / (float) layoutWidth, (float) videoHeight / (float) layoutHeight);
                //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
                videoWidth = (int) Math.ceil((float) videoWidth / max);
                videoHeight = (int) Math.ceil((float) videoHeight / max);
                //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(videoWidth, videoHeight);
                params.gravity = Gravity.CENTER;
                mSurfaceView.setLayoutParams(params);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                stopPlayVideo();
                resetLayout();
                return false;
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e("MainActivity", "视频准备完毕");
                mIsPrepared = true;
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                resetLayout();
            }
        });
        try {
            String videoUrl = getIntent().getStringExtra("videoUrl");
            Glide.with(this).load(videoUrl).into(mThumbImageView);
            mMediaPlayer.setDataSource(videoUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void stopPlayVideo() {
        mMediaPlayer.seekTo(0);
        mMediaPlayer.stop();
    }

    private void resetLayout() {
        mPlay.setVisibility(View.VISIBLE);
        mPause.setVisibility(View.INVISIBLE);
    }

    private void startPlayVideo() {
        if (!mIsPrepared) {
            return;
        }
        mMediaPlayer.start();
    }

    protected int getLayoutResID() {
        return R.layout.activity_show_video;
    }

    protected View getContentView() {
        return null;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initData();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
        initData();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mObserver != null) {
            mObserver.destroy();
        }
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer = null;
        }
    }
}
