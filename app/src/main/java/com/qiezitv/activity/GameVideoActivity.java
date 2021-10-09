package com.qiezitv.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.contrarywind.interfaces.IPickerViewData;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiezitv.R;
import com.qiezitv.common.Constants;
import com.qiezitv.common.FinishActivityManager;
import com.qiezitv.common.ImageLoaderUtil;
import com.qiezitv.common.http.AutoRefreshTokenCallback;
import com.qiezitv.common.http.RetrofitManager;
import com.qiezitv.dto.http.ResponseEntity;
import com.qiezitv.http.provider.OneyuanServiceProvider;
import com.qiezitv.http.provider.LiveServiceProvider;
import com.qiezitv.model.activity.ActivityVO;
import com.qiezitv.model.oneyuan.MatchStatus;
import com.qiezitv.model.oneyuan.MatchVO;
import com.qiezitv.model.oneyuan.TimeLine;
import com.qiezitv.pojo.AgainstTeamPickerViewData;
import com.qiezitv.pojo.OneyuanEvent;
import com.qiezitv.pojo.OneyuanTimelineEventData;
import com.qiezitv.pojo.MatchAgainstVO;
import com.qiezitv.view.BaseScoreBoardView;
import com.qiezitv.view.ColorPickerDialog;
import com.qiezitv.view.ScoreBoardOneyuan;
import com.qiezitv.view.WaitingDialog;
import com.qiezitv.view.WaterMarkView;
import com.laifeng.sopcastsdk.camera.CameraListener;
import com.laifeng.sopcastsdk.configuration.AudioConfiguration;
import com.laifeng.sopcastsdk.configuration.CameraConfiguration;
import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.entity.Watermark;
import com.laifeng.sopcastsdk.entity.WatermarkPosition;
import com.laifeng.sopcastsdk.stream.packer.rtmp.RtmpPacker;
import com.laifeng.sopcastsdk.stream.sender.DebugInfo;
import com.laifeng.sopcastsdk.stream.sender.rtmp.RtmpSender;
import com.laifeng.sopcastsdk.ui.CameraLivingView;
import com.laifeng.sopcastsdk.utils.SopCastLog;
import com.laifeng.sopcastsdk.video.effect.Effect;
import com.laifeng.sopcastsdk.video.effect.HSLEffect;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

import static com.qiezitv.pojo.OneyuanEvent.FINISH;
import static com.qiezitv.pojo.OneyuanEvent.NEXT_SETION;
import static com.qiezitv.pojo.OneyuanEvent.PRE_SETION;
import static com.qiezitv.pojo.OneyuanEvent.START;
import static com.qiezitv.pojo.OneyuanEvent.SWITCH_AGAINST;

@SuppressLint("HandlerLeak")
public class GameVideoActivity extends BaseActivity {
    private static final String TAG = GameVideoActivity.class.getSimpleName();

    private static final String HINT_PUSH = "是否开始直播？";
    private static final String HINT_LEAVE = "是否离开？";
    private static final String HINT_STOP = "是否停止直播？";

    private CameraLivingView mLFLiveView;
    private RtmpSender mRtmpSender;
    private GestureDetector mGestureDetector;
    private VideoConfiguration mVideoConfiguration;
    private int mCurrentBps;
    private int mVideoQuality = Constants.VideoQuality.MID;

    private ImageView ivStart, ivFinish, ivVideoSetting, ivResult, ivAdd, ivReduce, ivStatus,
            ivMute, ivNetStatus;
    private TextView tvHint, tvHintBandwidth;
    private SeekBar seekbarZoom;
    private LinearLayout llBottom, llRight, llHintNetwork, llHintPhone;
    private WaterMarkView waterMarkContainer;

    // 确认对话框
    private DialogPlus confirmDialog;
    private TextView confirmDialogTvHint;

    // 设置分辨率对话框
    private DialogPlus videoSettingDialog;

    private boolean isMute = false;
    private boolean isPublish = false;
    private boolean isScoreBoardShow = true;
    private boolean isLogoShow = true;
    private float brightness = 1.0f;
    private boolean isAutoFocus = false;
    private boolean isPushRetry = true;

    // 设置比分牌对话框
    private DialogPlus scoreSettingDialog;
    private ImageView ivHostColor, ivGuestColor;
    private int hostColor = Color.rgb(255, 0, 0);
    private int guestColor = Color.rgb(0, 0, 255);
    private String hostTeamName, guestTeamName, hostTeamHeadImg, guestTeamHeadImg;
    private Long hostTeamId, guestTeamId;
    private int section, againstIndex, hostScore, guestScore;

    private MatchVO match;
    private MatchStatus matchStatus;
    private ActivityVO activity;

    private Integer liveQuality;
    private int pushRetryTimes = 0;
    private static final int MAX_RETRY_TIMES = 3;
    private long firstRetryTime = -1L;

    //定时刷新
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private int SECOND = 30;
    //定时获取推流波动
    private Timer mTimerLiveQuality = new Timer();
    private TimerTask mTimerTaskLiveQuality;
    private int SECOND_LIVE_QUALITY = 70;

    private int eventType = -1;
    private OneyuanTimelineEventData statusEventData;
    private TextView statusDetailTvHostTeamName;
    private TextView statusDetailTvGuestTeamName;
    private ImageView statusDetailIvHostTeamHeadImg;
    private ImageView statusDetailIvGuestTeamHeadImg;
    private TextView statusDetailTvScore;
    private int currentSwitchAgainst = -1;

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = () -> {
        if (mLFLiveView != null && waterMarkContainer.getScoreBoard() != null) {
            reloadWatermark();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_match_video);

        match = (MatchVO) getIntent().getSerializableExtra("Match");

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLFLiveView.stop();
        setPublish(false);
        tvHint.setText("未直播");
        tvHintBandwidth.setText("未直播");
        ivStart.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLFLiveView.stop();
        mLFLiveView.release();
        mTimer.cancel();
        mTimerLiveQuality.cancel();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        // 注释后防止屏幕翻转
//        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mLFLiveView.zoom(false, 1);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                mLFLiveView.zoom(true, 1);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (isPublish()) {
            showConfirmDialog(HINT_LEAVE);
        } else {
            finish();
        }
    }

    //init
    private void init() {
        hideBottomUIMenu();
        //初始化view
        initView();
        //初始化直播
        initLiveView();
        //初始化logo水印
        initWaterMark();
        //初始化计时器
        initTimer();
        //初始化比分牌
        initScoreBoard();
        //获取直播地址
        queryActivityInfo();
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams lp = this.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        decorView.setOnSystemUiVisibilityChangeListener(i -> {
            int uiOptions1 = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions1);
        });
    }

    private void initView() {
        mLFLiveView = findViewById(R.id.gl_surface_view_camera);
        ivStart = findViewById(R.id.iv_start);
        ivFinish = findViewById(R.id.iv_finish);
        ivVideoSetting = findViewById(R.id.iv_video_setting);
        ivMute = findViewById(R.id.iv_mute);
        ivResult = findViewById(R.id.iv_result);
        ivStatus = findViewById(R.id.iv_status);
        llBottom = findViewById(R.id.ll_bottom);
        tvHint = findViewById(R.id.tv_hint);
        tvHintBandwidth = findViewById(R.id.tv_hint_bandwidth);
        llRight = findViewById(R.id.ll_right);
        ivAdd = findViewById(R.id.iv_add);
        ivReduce = findViewById(R.id.iv_reduce);
        seekbarZoom = findViewById(R.id.seekbar_zoom);
        ivNetStatus = findViewById(R.id.iv_net_status);
        llHintNetwork = findViewById(R.id.ll_hint_network);
        llHintPhone = findViewById(R.id.ll_hint_phone);
        seekbarZoom.setClickable(false);
        llHintPhone.setOnClickListener(v -> {
            callPhone();
        });

        ivStart.setOnClickListener(v -> {
            if (!isPublish()) {
                if (activity != null) {
                    showConfirmDialog(HINT_PUSH);
                } else {
                    showToast("获取直播地址失败,请尝试重新进入该页面");
                }
            } else {
                showConfirmDialog(HINT_STOP);
            }
        });
        ivFinish.setOnClickListener(v -> {
            if (isPublish()) {
                showConfirmDialog(HINT_LEAVE);
            } else {
                finish();
            }
        });
        ivMute.setOnClickListener(v -> {
            if (isMute()) {
                setMute(false);
            } else {
                setMute(true);
            }
        });
        ivVideoSetting.setOnClickListener(v -> showSettingMenu(ivVideoSetting));
        ivStatus.setOnClickListener(v -> {
            showStatusDialog();
        });
        ivResult.setOnClickListener(v -> {
            showBasketballEventDialog();
        });
        ivAdd.setOnClickListener(v -> {
            mLFLiveView.zoom(true, 1);
        });
        ivReduce.setOnClickListener(v -> {
            mLFLiveView.zoom(false, 1);
        });
    }

    private void initWaterMark() {
        waterMarkContainer = findViewById(R.id.top_container);
        waterMarkContainer.setPadding(20, 20, 20, 20);

        //init logo
        ImageView logoWaterMarkView = new ImageView(this);
        logoWaterMarkView.setImageResource(R.drawable.ic_logo_horizontal);
        logoWaterMarkView.setAdjustViewBounds(true);
        logoWaterMarkView.setMaxWidth(mVideoConfiguration.width / 6);
        logoWaterMarkView.setMaxHeight((int) (mVideoConfiguration.width / 6 / 2));
        FrameLayout.LayoutParams logoLp = new FrameLayout.LayoutParams(mVideoConfiguration.width / 6, (int) (mVideoConfiguration.width / 6 / 2));
        logoLp.leftMargin = (int) (mVideoConfiguration.width * 0.0214);
        logoLp.topMargin = (int) (mVideoConfiguration.height * 0.023);
        logoWaterMarkView.setLayoutParams(logoLp);

        //水印设置成跟视频大小一样
        FrameLayout.LayoutParams waterMarkContainerLp = new FrameLayout.LayoutParams(mVideoConfiguration.width, mVideoConfiguration.height);
        waterMarkContainer.setLayoutParams(waterMarkContainerLp);

        waterMarkContainer.setLogo(logoWaterMarkView);
    }

    private void initTimer() {
        //定时刷新
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                queryMatchStatusDetail();
            }
        };
        mTimer.schedule(mTimerTask, 0, SECOND * 1000);
    }

    private void initLiveView() {
        SopCastLog.isOpen(true);
        mLFLiveView.init();
        CameraConfiguration.Builder cameraBuilder = new CameraConfiguration.Builder();
        cameraBuilder.setOrientation(CameraConfiguration.Orientation.LANDSCAPE)
                .setFacing(CameraConfiguration.Facing.BACK)
                .setFocusMode(CameraConfiguration.FocusMode.TOUCH);
        CameraConfiguration cameraConfiguration = cameraBuilder.build();
        mLFLiveView.setCameraConfiguration(cameraConfiguration);

        mVideoConfiguration = new VideoConfiguration.Builder().build(VideoConfiguration.VideoQuality.VideoQuality_Mid);
        mVideoQuality = Constants.VideoQuality.MID;
        mLFLiveView.setVideoConfiguration(mVideoConfiguration);

        //设置预览监听
        mLFLiveView.setCameraOpenListener(new CameraListener() {
            @Override
            public void onOpenSuccess() {
                seekbarZoom.setMax(mLFLiveView.getMaxZoom());
            }

            @Override
            public void onOpenFail(int error) {
                showToast("相机开启失败");
            }

            @Override
            public void onCameraChange() {
                showToast("相机切换");
            }
        });

        //设置手势识别
        mGestureDetector = new GestureDetector(this, new GestureListener());
        mLFLiveView.setOnTouchListener((v, event) -> {
            mGestureDetector.onTouchEvent(event);
            return false;
        });

        //初始化flv打包器
        RtmpPacker packer = new RtmpPacker();
        packer.initAudioParams(AudioConfiguration.DEFAULT_FREQUENCY, 16, false);
        mLFLiveView.setPacker(packer);
        //设置发送器
        mRtmpSender = new RtmpSender();
        mRtmpSender.setVideoParams(mVideoConfiguration.width, mVideoConfiguration.height, mVideoConfiguration.maxBps, mVideoConfiguration.fps);
        mRtmpSender.setAudioParams(AudioConfiguration.DEFAULT_FREQUENCY, 16, false);
        mRtmpSender.setSenderListener(mSenderListener);
        mLFLiveView.setSender(mRtmpSender);
        mLFLiveView.setLivingStartListener(new CameraLivingView.LivingStartListener() {
            @Override
            public void startError(int error) {
                //直播失败
                showToast("开始直播失败");
                mLFLiveView.stop();
            }

            @Override
            public void startSuccess() {
                //直播成功
                setVideoBitRate(mVideoConfiguration.maxBps);
            }
        });
        mLFLiveView.setOnZoomProgressListener(progress -> seekbarZoom.setProgress((int) Math.floor(progress * 100)));
    }

    private RtmpSender.OnSenderListener mSenderListener = new RtmpSender.OnSenderListener() {
        @Override
        public void onConnecting() {

        }

        @Override
        public void onConnected() {
            tvHint.setText("已连接");
            tvHintBandwidth.setText("已连接");
            mLFLiveView.start();
            llHintPhone.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onDisConnected() {
            showToast("直播断开");
            mLFLiveView.stop();
            setPublish(false);
            tvHint.setText("已断开");
            tvHintBandwidth.setText("已断开");
        }

        @Override
        public void onPublishFail() {
            showToast("推流失败");
            setPublish(false);
            llHintPhone.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNetGood() {
            if (mCurrentBps + 50 <= mVideoConfiguration.maxBps) {
                SopCastLog.d(TAG, "BPS_CHANGE good up 50");
                int bps = mCurrentBps + 50;
                setVideoBitRate(bps);
            } else {
                SopCastLog.d(TAG, "BPS_CHANGE good good good");
            }
            SopCastLog.d(TAG, "Current Bps: " + mCurrentBps);
            tvHint.setText("网络正常，码率：" + mCurrentBps);
            ivNetStatus.setImageResource(R.drawable.shape_network_status_good);
        }

        @Override
        public void onNetBad() {
            if (mCurrentBps - 100 >= mVideoConfiguration.minBps) {
                SopCastLog.d(TAG, "BPS_CHANGE bad down 100");
                int bps = mCurrentBps - 100;
                setVideoBitRate(bps);
            } else {
                SopCastLog.d(TAG, "BPS_CHANGE bad down 100");
            }
            SopCastLog.d(TAG, "Current Bps: " + mCurrentBps);
            tvHint.setText("网络差，码率：" + mCurrentBps);
            ivNetStatus.setImageResource(R.drawable.shape_network_status_bad);
        }

        @Override
        public void onDebug(DebugInfo debugInfo) {
            tvHintBandwidth.setText(debugInfo.getCurrentBandwidth() / 1000 + "kb/s");
        }
    };

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > 100
                    && Math.abs(velocityX) > 200) {
                // Fling left
            } else if (e2.getX() - e1.getX() > 100
                    && Math.abs(velocityX) > 200) {
                // Fling right
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void queryActivityInfo() {
        LiveServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(LiveServiceProvider.class);
        Call<ResponseEntity<ActivityVO>> response = request.getActivityVo(match.getActivityId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<ActivityVO>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<ActivityVO> result) {
                activity = result.getData();
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<ActivityVO>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("获取直播地址失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("获取直播地址失败:" + t.getMessage());
                }
            }
        });
    }


    private void queryLiveQuality() {
        if (isPushRetry) {
            return;
        }
        LiveServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(LiveServiceProvider.class);
        Call<ResponseEntity<Integer>> response = request.quality(match.getActivityId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Integer>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<Integer> result) {
                liveQuality = result.getData();
                switch (liveQuality) {
                    case Constants.ActivityQuality.BAD:
                        long now = System.currentTimeMillis();
                        if (pushRetryTimes < MAX_RETRY_TIMES) {
                            mLFLiveView.restart();
                            if (now - firstRetryTime <= 5 * 60 * 1000) {
                                pushRetryTimes = pushRetryTimes + 1;
                            } else {
                                firstRetryTime = now;
                                pushRetryTimes = 1;
                            }
                            showToast("网络不佳，推流重试:" + pushRetryTimes + "次");
                        } else {
                            llHintNetwork.setVisibility(View.VISIBLE);
                        }
                        break;
                    case Constants.ActivityQuality.UNKNOW:
                        break;
                    case Constants.ActivityQuality.NORMAL:
                    case Constants.ActivityQuality.NOTBAD:
                        if (pushRetryTimes >= MAX_RETRY_TIMES) {
                            llHintNetwork.setVisibility(View.INVISIBLE);
                            pushRetryTimes = 0;
                        }
                        break;
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Integer>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("请求直播质量失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("请求直播质量失败:" + t.getMessage());
                }
            }
        });
    }

    private void startQueryLiveQuality() {
        mTimerLiveQuality.cancel();
        mTimerLiveQuality = new Timer();
        pushRetryTimes = 0;
        mTimerTaskLiveQuality = new TimerTask() {
            @Override
            public void run() {
                queryLiveQuality();
            }
        };
        mTimerLiveQuality.schedule(mTimerTaskLiveQuality, SECOND_LIVE_QUALITY * 1000, SECOND_LIVE_QUALITY * 1000);
    }

    private void showConfirmDialog(String hint) {
        if (confirmDialog == null) {
            confirmDialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_confirm))
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setOnClickListener((dialog, view) -> {
                        switch (view.getId()) {
                            case R.id.btn_confirm:
                                String btnText = confirmDialogTvHint.getText().toString();
                                if (btnText.equals(HINT_PUSH)) {
                                    Log.d(TAG, "PushStreamUrl->" + activity.getPushStreamUrl());
                                    mRtmpSender.setAddress(activity.getPushStreamUrl());
                                    mRtmpSender.connect();
                                    setPublish(true);
                                    tvHint.setText("直播中");
                                    tvHintBandwidth.setText("直播中");
                                    llHintNetwork.setVisibility(View.INVISIBLE);
                                    refreshAgainstTeamInfo(match.getAgainstTeams(), matchStatus);
                                    reloadScoreBoard();
                                    startQueryLiveQuality();
                                } else if (btnText.equals(HINT_LEAVE)) {
                                    finish();
                                } else if (btnText.equals(HINT_STOP)) {
                                    setPublish(false);
                                    mLFLiveView.stop();
                                }
                                dialog.dismiss();
                                break;
                            case R.id.btn_cancel:
                                dialog.dismiss();
                                break;
                        }
                    })
                    .setExpanded(false)
                    .create();
            confirmDialogTvHint = (TextView) confirmDialog.findViewById(R.id.tv_hint);
        }
        confirmDialogTvHint.setText(hint);
        confirmDialog.show();
    }

    private void showVideoResolutionSettingDialog() {
        if (videoSettingDialog == null) {
            videoSettingDialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_video_setting))
                    .setContentBackgroundResource(R.drawable.shape_circle_popup)
                    .setGravity(Gravity.CENTER)
                    .setCancelable(true)
                    .setExpanded(false)
                    .create();
            SeekBar seekbarQuality = (SeekBar) videoSettingDialog.findViewById(R.id.seekbar_quality);
            seekbarQuality.setProgress(mVideoQuality);
            seekbarQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    switch (i) {
                        case Constants.VideoQuality.LOW:
                            changeVideoQuality(Constants.VideoQuality.LOW);
                            break;
                        case Constants.VideoQuality.MID:
                            changeVideoQuality(Constants.VideoQuality.MID);
                            break;
                        case Constants.VideoQuality.HIGH:
                            changeVideoQuality(Constants.VideoQuality.HIGH);
                            break;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            SeekBar seekbarBrightness = (SeekBar) videoSettingDialog.findViewById(R.id.seekbar_brightness);
            seekbarBrightness.setProgress((int) (brightness - 1.0f));
            seekbarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    brightness = 1.0f + i / 100f;
                    setBrightness(brightness);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            Switch switchAutoFocus = (Switch) videoSettingDialog.findViewById(R.id.switch_autoFocus);
            switchAutoFocus.setChecked(isAutoFocus);
            switchAutoFocus.setOnCheckedChangeListener((compoundButton, b) -> setAutoFocus(b));
            Switch switchPushRetry = (Switch) videoSettingDialog.findViewById(R.id.switch_pushRetry);
            switchPushRetry.setChecked(isPushRetry);
            switchPushRetry.setOnCheckedChangeListener((compoundButton, b) -> setPushRetry(b));
        }

        videoSettingDialog.show();
    }

    private void showSettingMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_setting, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.scoreboard_setting:
                    showScoreBoardSettingDialog();
                    break;
                case R.id.live_setting:
                    showVideoResolutionSettingDialog();
                    break;
            }
            return true;
        });

    }

    private void queryMatchStatusDetail() {
        OneyuanServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(OneyuanServiceProvider.class);
        Call<ResponseEntity<MatchStatus>> response = request.getMatchStatusDetailById(match.getId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<MatchStatus>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<MatchStatus> result) {
                matchStatus = result.getData();
                if (waterMarkContainer.getScoreBoard() != null) {
                    refreshAgainstTeamInfo(match.getAgainstTeams(), matchStatus);
                    reloadScoreBoard();
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<MatchStatus>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
            }
        });
    }

    private void showScoreBoardSettingDialog() {
        if (scoreSettingDialog == null) {
            scoreSettingDialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_score_board_setting))
                    .setGravity(Gravity.CENTER)
                    .setCancelable(true)
                    .setContentBackgroundResource(R.drawable.shape_circle_popup)
                    .setOnClickListener((dialog, view) -> {
                        ColorPickerDialog colorPickerDialog;
                        switch (view.getId()) {
                            case R.id.tv_host_name:
                            case R.id.iv_host_color:
                                colorPickerDialog = new ColorPickerDialog(GameVideoActivity.this, hostColor, "主队颜色", color -> {
                                    hostColor = color;
                                    ivHostColor.setBackgroundColor(hostColor);
                                    if (waterMarkContainer.getScoreBoard() != null) {
                                        waterMarkContainer.getScoreBoard().setHostColor(hostColor);
                                        reloadWatermark();
                                    }
                                });
                                colorPickerDialog.show();
                                break;
                            case R.id.tv_guest_name:
                            case R.id.iv_guest_color:
                                colorPickerDialog = new ColorPickerDialog(GameVideoActivity.this, guestColor, "客队颜色", color -> {
                                    guestColor = color;
                                    ivGuestColor.setBackgroundColor(guestColor);
                                    if (waterMarkContainer.getScoreBoard() != null) {
                                        waterMarkContainer.getScoreBoard().setGuestColor(guestColor);
                                        reloadWatermark();
                                    }
                                });
                                colorPickerDialog.show();
                                break;
                        }
                    })
                    .setExpanded(false)
                    .create();
            Switch switchLogo = (Switch) scoreSettingDialog.findViewById(R.id.switch_logo);
            Switch switchScoreboard = (Switch) scoreSettingDialog.findViewById(R.id.switch_scoreboard);
            switchLogo.setChecked(isLogoShow);
            switchScoreboard.setChecked(isScoreBoardShow);
            switchLogo.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    showLogo();
                } else {
                    hideLogo();
                }
            });
            switchScoreboard.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    showScoreboard();
                } else {
                    hideScoreboard();
                }
            });
            ivHostColor = (ImageView) scoreSettingDialog.findViewById(R.id.iv_host_color);
            ivGuestColor = (ImageView) scoreSettingDialog.findViewById(R.id.iv_guest_color);
            TextView tvHostTeamName = (TextView) scoreSettingDialog.findViewById(R.id.tv_host_name);
            tvHostTeamName.setText(hostTeamName);
            TextView tvGuestTeamName = (TextView) scoreSettingDialog.findViewById(R.id.tv_guest_name);
            tvGuestTeamName.setText(guestTeamName);
        }
        scoreSettingDialog.show();
    }

    private void refreshAgainstTeamInfo(Map<Integer, MatchAgainstVO> againstTeams, MatchStatus matchStatus) {
        hostTeamName = "无";
        guestTeamName = "无";
        section = 1;
        hostScore = 0;
        guestScore = 0;
        if (againstTeams != null && againstTeams.size() > 0) {
            for (Integer key : againstTeams.keySet()) {
                //如果对阵方等于当前对阵方
                if (matchStatus != null && matchStatus.getAgainstIndex().equals(key)) {
                    MatchAgainstVO againstTeam = againstTeams.get(key);
                    if (againstTeam != null && againstTeam.getHostTeam() != null) {
                        hostTeamId = againstTeam.getHostTeam().getId();
                        hostTeamHeadImg = againstTeam.getHostTeam().getHeadImg();
                        if (againstTeam.getHostTeam().getShortName() != null) {
                            hostTeamName = againstTeam.getHostTeam().getShortName();
                        } else {
                            hostTeamName = againstTeam.getHostTeam().getName();
                        }
                    }
                    if (againstTeam != null && againstTeam.getGuestTeam() != null) {
                        guestTeamId = againstTeam.getGuestTeam().getId();
                        guestTeamHeadImg = againstTeam.getGuestTeam().getHeadImg();
                        if (againstTeam.getGuestTeam().getShortName() != null) {
                            guestTeamName = againstTeam.getGuestTeam().getShortName();
                        } else {
                            guestTeamName = againstTeam.getGuestTeam().getName();
                        }
                    }
                    String score = "0-0";
                    if (matchStatus.getScore() != null && matchStatus.getScore().containsKey(key)) {
                        score = matchStatus.getScore().get(key);
                    }
                    String[] againstScore = score.split("-");
                    hostScore = Integer.parseInt(againstScore[0]);
                    guestScore = Integer.parseInt(againstScore[1]);
                }
            }
        }
        if (matchStatus != null && matchStatus.getSection() != null) {
            section = matchStatus.getSection();
        }
        if (matchStatus != null && matchStatus.getAgainstIndex() != null) {
            againstIndex = matchStatus.getAgainstIndex();
        }
    }

    private <T extends BaseScoreBoardView> void initScoreBoard() {
        //初始化比分牌文字
        refreshAgainstTeamInfo(match.getAgainstTeams(), match.getStatus());
        BaseScoreBoardView scoreBoardWaterMarkView = new ScoreBoardOneyuan(this);
        //删除比分牌
        if (waterMarkContainer.getScoreBoard() != null) {
            waterMarkContainer.removeView(waterMarkContainer.getScoreBoard());
        }
        //添加比分牌
        scoreBoardWaterMarkView.setTeamNameHost(hostTeamName);
        scoreBoardWaterMarkView.setTeamNameGuest(guestTeamName);
        scoreBoardWaterMarkView.setScoreHost(hostScore);
        scoreBoardWaterMarkView.setScoreGuest(guestScore);
        scoreBoardWaterMarkView.setSection(section);
        scoreBoardWaterMarkView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        if (match.getLeague() != null && (match.getLeague().getRuleType() == Constants.LeagueRuleType.TYPE_1_x_1 || match.getLeague().getRuleType() == Constants.LeagueRuleType.TYPE_3_x_3)) {
            scoreBoardWaterMarkView.showLogoMask();
        } else {
            scoreBoardWaterMarkView.hideLogoMask();
        }
        waterMarkContainer.setScoreBoard(scoreBoardWaterMarkView);
        if (this.match.getAgainstTeams() == null || this.match.getAgainstTeams().size() == 0) {
            this.hideScoreboard();
        }
    }

    //隐藏比分牌
    private void hideScoreboard() {
        isScoreBoardShow = false;
        waterMarkContainer.hideScoreBoard();
        reloadWatermark();
    }

    //显示比分牌
    private void showScoreboard() {
        isScoreBoardShow = true;
        waterMarkContainer.showScoreBoard();
        reloadWatermark();
    }

    private void reloadScoreBoard() {
        waterMarkContainer.getScoreBoard().setTeamNameHost(hostTeamName);
        waterMarkContainer.getScoreBoard().setTeamNameGuest(guestTeamName);
        waterMarkContainer.getScoreBoard().setScoreHost(hostScore);
        waterMarkContainer.getScoreBoard().setScoreGuest(guestScore);
        waterMarkContainer.getScoreBoard().setSection(section);
        reloadWatermark();
    }

    //隐藏logo
    private void hideLogo() {
        isLogoShow = false;
        waterMarkContainer.hideLogo();
        reloadWatermark();
    }

    //显示logo
    private void showLogo() {
        isLogoShow = true;
        waterMarkContainer.showLogo();
        reloadWatermark();
    }

    private void reloadWatermark() {
        setWatermark(waterMarkContainer.getBitmap());
    }

    private void setWatermark(Bitmap watermarkImg) {
        if (watermarkImg == null) {
            mLFLiveView.setWatermark(null);
            return;
        }
        //全屏水印
        Watermark watermark = new Watermark(watermarkImg, WatermarkPosition.WATERMARK_ORIENTATION_TOP_RIGHT, true);
        mLFLiveView.setWatermark(watermark);
    }

    private boolean isPublish() {
        return isPublish;
    }

    private void setPublish(boolean publish) {
        isPublish = publish;
        if (isPublish) {
            ivStart.setImageResource(R.drawable.ic_stop);
            tvHint.setText("直播中");
            tvHintBandwidth.setText("直播中");
        } else {
            ivStart.setImageResource(R.drawable.ic_play);
            tvHint.setText("未直播");
            tvHintBandwidth.setText("未直播");
        }
    }

    private boolean isMute() {
        return isMute;
    }

    private void setMute(boolean mute) {
        isMute = mute;
        mLFLiveView.mute(isMute);
        if (isMute) {
            ivMute.setImageResource(R.drawable.ic_mute_on);
        } else {
            ivMute.setImageResource(R.drawable.ic_mute_off);
        }
    }

    private void changeVideoQuality(int quality) {
        switch (quality) {
            case Constants.VideoQuality.LOW:
                mVideoConfiguration = new VideoConfiguration.Builder().build(VideoConfiguration.VideoQuality.VideoQuality_Low);
                mVideoQuality = Constants.VideoQuality.LOW;
                mLFLiveView.setVideoConfiguration(mVideoConfiguration);
                setVideoBitRate(mVideoConfiguration.maxBps);
                break;
            case Constants.VideoQuality.MID:
                mVideoConfiguration = new VideoConfiguration.Builder().build(VideoConfiguration.VideoQuality.VideoQuality_Mid);
                mVideoQuality = Constants.VideoQuality.MID;
                mLFLiveView.setVideoConfiguration(mVideoConfiguration);
                setVideoBitRate(mVideoConfiguration.maxBps);
                break;
            case Constants.VideoQuality.HIGH:
                mVideoConfiguration = new VideoConfiguration.Builder().build(VideoConfiguration.VideoQuality.VideoQuality_High);
                mVideoQuality = Constants.VideoQuality.HIGH;
                mLFLiveView.setVideoConfiguration(mVideoConfiguration);
                setVideoBitRate(mVideoConfiguration.maxBps);
                break;
        }
    }

    private void setVideoBitRate(int bps) {
        if (mLFLiveView != null) {
            boolean result = mLFLiveView.setVideoBps(bps);
            if (result) {
                mCurrentBps = bps;
            }
        }
    }

    private void setBrightness(float brightness) {
        Effect effect = new HSLEffect(this);
        effect.setL(brightness);
        setEffet(effect);
    }

    private void setEffet(Effect effet) {
        mLFLiveView.setEffect(effet);
    }

    private void setAutoFocus(boolean autoFocus) {
        isAutoFocus = !autoFocus;
        mLFLiveView.switchFocusMode();
    }

    private void setPushRetry(boolean pushRetry) {
        isPushRetry = !pushRetry;
        pushRetryTimes = 0;
    }

    private void gotoLoginActivity() {
        runOnUiThread(() -> {
            showToast("授权过期，请重新登录");
            readyGo(LoginActivity.class);
            finish();
            FinishActivityManager.getManager().finishActivity(MatchActivity.class);
            FinishActivityManager.getManager().finishActivity(MainActivity.class);
        });
    }

    private void showStatusDialog() {
        currentSwitchAgainst = -1;
        eventType = -1;
        DialogPlus statusDialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_status_event_selector))
                .setContentBackgroundResource(R.drawable.shape_circle_bottom_popup)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setOnDismissListener(dialog -> {
                    if (eventType != -1) {
                        showStatusDetailDialog();
                    }
                })
                .setOnClickListener((dialog, view) -> {
                    if (view instanceof LinearLayout) {
                        switch (view.getId()) {
                            case R.id.ll_start:
                                eventType = START;
                                break;
                            case R.id.ll_change_against:
                                eventType = SWITCH_AGAINST;
                                break;
                            case R.id.ll_finish:
                                eventType = FINISH;
                                break;
                            case R.id.ll_next_section:
                                eventType = NEXT_SETION;
                                break;
                            case R.id.ll_pre_section:
                                eventType = PRE_SETION;
                                break;
                        }
                    }
                    dialog.dismiss();
                })
                .setExpanded(false)
                .create();
        statusDialog.show();
    }

    private void showStatusDetailDialog() {
        //获取当前对阵
        Map<Integer, OneyuanTimelineEventData> eventDataMap = OneyuanTimelineEventData.statusEventDataMap;
        if (!eventDataMap.containsKey(eventType)) {
            return;
        }
        statusEventData = eventDataMap.get(eventType);

        DialogPlus statusDetailDialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_status_event))
                .setContentBackgroundResource(R.drawable.shape_circle_popup)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setOnClickListener((dialog, view) -> {
                    switch (view.getId()) {
                        case R.id.ll_against:
                        case R.id.ll_against_index:
                        case R.id.tv_host_team_name:
                        case R.id.tv_guest_team_name:
                        case R.id.iv_host_team_img:
                        case R.id.iv_guest_team_img:
                        case R.id.tv_score:
                            if (eventType == SWITCH_AGAINST) {
                                showChooseAgainstDialog();
                            }
                            break;
                        case R.id.btn_confirm:
                            TimeLine timeLine = new TimeLine();
                            if (eventType == SWITCH_AGAINST) {
                                if (currentSwitchAgainst == -1) {
                                    showToast("请选择要切换的对阵");
                                    return;
                                } else {
                                    timeLine.setAgainstIndex(currentSwitchAgainst);
                                }
                            } else if (eventType == NEXT_SETION || eventType == PRE_SETION) {
                                timeLine.setAgainstIndex(againstIndex);
                            }
                            timeLine.setEventType(eventType);
                            timeLine.setMatchId(match.getId());
                            addEvent(timeLine);
                            dialog.dismiss();
                            break;
                    }
                })
                .setExpanded(false)
                .create();
        String score = hostScore + "-" + guestScore;
        ImageView statusDetailIvEvent = (ImageView) statusDetailDialog.findViewById(R.id.iv_event_ic);
        TextView statusDetailTvEventName = (TextView) statusDetailDialog.findViewById(R.id.tv_event_name);
        LinearLayout statusDetailLlAgainstIndex = (LinearLayout) statusDetailDialog.findViewById(R.id.ll_against_index);
        statusDetailTvHostTeamName = (TextView) statusDetailDialog.findViewById(R.id.tv_host_team_name);
        statusDetailTvGuestTeamName = (TextView) statusDetailDialog.findViewById(R.id.tv_guest_team_name);
        statusDetailIvHostTeamHeadImg = (ImageView) statusDetailDialog.findViewById(R.id.iv_host_team_img);
        statusDetailIvGuestTeamHeadImg = (ImageView) statusDetailDialog.findViewById(R.id.iv_guest_team_img);
        statusDetailTvScore = (TextView) statusDetailDialog.findViewById(R.id.tv_score);
        if (eventType != SWITCH_AGAINST) {
            statusDetailLlAgainstIndex.setVisibility(View.GONE);
        } else {
            statusDetailLlAgainstIndex.setVisibility(View.VISIBLE);
        }
        statusDetailIvEvent.setBackgroundResource(statusEventData.getBackgroundResourceId());
        statusDetailTvEventName.setText(statusEventData.getText());
        statusDetailTvHostTeamName.setText(hostTeamName);
        statusDetailTvGuestTeamName.setText(guestTeamName);
        statusDetailTvScore.setText(score);
        ImageLoader.getInstance().displayImage(hostTeamHeadImg, statusDetailIvHostTeamHeadImg, ImageLoaderUtil.getOptions());
        ImageLoader.getInstance().displayImage(guestTeamHeadImg, statusDetailIvGuestTeamHeadImg, ImageLoaderUtil.getOptions());
        statusDetailDialog.show();
    }

    private void showChooseAgainstDialog() {
        List<IPickerViewData> options1Items = new ArrayList<>();
        if (match.getAgainstTeams() != null) {
            for (Integer key : match.getAgainstTeams().keySet()) {
                MatchAgainstVO againstTeam = match.getAgainstTeams().get(key);
                AgainstTeamPickerViewData pickerViewData = new AgainstTeamPickerViewData();
                String hostName = "";
                String guestName = "";
                String score = "0-0";
                if (againstTeam != null && againstTeam.getHostTeam() != null) {
                    if (againstTeam.getHostTeam().getShortName() != null) {
                        hostName = againstTeam.getHostTeam().getShortName();
                    } else {
                        hostName = againstTeam.getHostTeam().getName();
                    }
                    pickerViewData.setHostTeamHeadImg(againstTeam.getHostTeam().getHeadImg());
                }
                if (againstTeam != null && againstTeam.getGuestTeam() != null) {
                    if (againstTeam.getGuestTeam().getShortName() != null) {
                        guestName = againstTeam.getGuestTeam().getShortName();
                    } else {
                        guestName = againstTeam.getGuestTeam().getName();
                    }
                    pickerViewData.setGuestTeamHeadImg(againstTeam.getGuestTeam().getHeadImg());
                }
                if (matchStatus != null && matchStatus.getScore() != null && matchStatus.getScore().containsKey(key)) {
                    score = matchStatus.getScore().get(key);
                }
                pickerViewData.setHostTeamName(hostName);
                pickerViewData.setGuestTeamName(guestName);
                pickerViewData.setAgainstTeam(hostName + "VS" + guestName);
                pickerViewData.setAgainstIndex(key);
                pickerViewData.setScore(score);
                options1Items.add(pickerViewData);
            }
        }
        OptionsPickerView<IPickerViewData> pvOptions = new OptionsPickerBuilder(this,
                (options1, option2, options3, v) -> {
                    AgainstTeamPickerViewData data = (AgainstTeamPickerViewData) options1Items.get(options1);
                    currentSwitchAgainst = data.getAgainstIndex();
                    statusDetailTvHostTeamName.setText(data.getHostTeamName());
                    statusDetailTvGuestTeamName.setText(data.getGuestTeamName());
                    statusDetailTvScore.setText(data.getScore());
                    ImageLoader.getInstance().displayImage(data.getHostTeamHeadImg(), statusDetailIvHostTeamHeadImg, ImageLoaderUtil.getOptions());
                    ImageLoader.getInstance().displayImage(data.getGuestTeamHeadImg(), statusDetailIvGuestTeamHeadImg, ImageLoaderUtil.getOptions());
                }).build();
        pvOptions.setPicker(options1Items);
        pvOptions.show();
    }

    private void addEvent(TimeLine timeLine) {
        if (timeLine == null) {
            showToast("参数错误,请重新选择");
            return;
        }
        showWaitingDialog();
        OneyuanServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(OneyuanServiceProvider.class);
        Call<ResponseEntity<Boolean>> response = request.addTimeLine(timeLine);
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Boolean>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<Boolean> result) {
                dismissWaitingDialog();
                Gson gson = new Gson();
                if (result.getData()) {
                    queryMatchStatusDetail();
                    showToast("添加成功");
                } else {
                    showToast("新增请求失败:" + gson.toJson(result));
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Boolean>> response, @Nullable Throwable t) {
                dismissWaitingDialog();
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
            }
        });
    }

    private DialogPlus oneyuanEventDialog;

    private TextView tvHostName;
    private TextView tvGuestName;

    private Button btnHostReduce1;
    private Button btnHostReduce2;
    private Button btnHostReduce3;
    private Button btnGuestReduce1;
    private Button btnGuestReduce2;
    private Button btnGuestReduce3;

    private Button btnHostPlus1;
    private Button btnHostPlus2;
    private Button btnHostPlus3;
    private Button btnGuestPlus1;
    private Button btnGuestPlus2;
    private Button btnGuestPlus3;

    private Button btnPreSection;
    private Button btnNextSection;

    private boolean isUpdating = false;

    private void showBasketballEventDialog() {
        if (oneyuanEventDialog == null) {
            oneyuanEventDialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_event_oneyuan))
                    .setContentBackgroundResource(R.drawable.shape_circle_popup)
                    .setGravity(Gravity.CENTER)
                    .setCancelable(true)
                    .setContentWidth(1200)
                    .setOnClickListener((dialog, view) -> {
                        switch (view.getId()) {
                            case R.id.btn_host_plus1:
                                updateMatchScore(hostTeamId, OneyuanEvent.GOAL_ONE);
                                break;
                            case R.id.btn_host_plus2:
                                updateMatchScore(hostTeamId, OneyuanEvent.GOAL_TWO);
                                break;
                            case R.id.btn_host_plus3:
                                updateMatchScore(hostTeamId, OneyuanEvent.GOAL_THREE);
                                break;
                            case R.id.btn_host_reduce1:
                                updateMatchScore(hostTeamId, OneyuanEvent.GOAL_ONE_REVERSE);
                                break;
                            case R.id.btn_host_reduce2:
                                updateMatchScore(hostTeamId, OneyuanEvent.GOAL_TWO_REVERSE);
                                break;
                            case R.id.btn_host_reduce3:
                                updateMatchScore(hostTeamId, OneyuanEvent.GOAL_THREE_REVERSE);
                                break;
                            case R.id.btn_guest_plus1:
                                updateMatchScore(guestTeamId, OneyuanEvent.GOAL_ONE);
                                break;
                            case R.id.btn_guest_plus2:
                                updateMatchScore(guestTeamId, OneyuanEvent.GOAL_TWO);
                                break;
                            case R.id.btn_guest_plus3:
                                updateMatchScore(guestTeamId, OneyuanEvent.GOAL_THREE);
                                break;
                            case R.id.btn_guest_reduce1:
                                updateMatchScore(guestTeamId, OneyuanEvent.GOAL_ONE_REVERSE);
                                break;
                            case R.id.btn_guest_reduce2:
                                updateMatchScore(guestTeamId, OneyuanEvent.GOAL_TWO_REVERSE);
                                break;
                            case R.id.btn_guest_reduce3:
                                updateMatchScore(guestTeamId, OneyuanEvent.GOAL_THREE_REVERSE);
                                break;
                            case R.id.btn_pre_section:
                                updateMatchSection(OneyuanEvent.PRE_SETION);
                                break;
                            case R.id.btn_next_section:
                                updateMatchSection(OneyuanEvent.NEXT_SETION);
                                break;
                        }
                    })
                    .setExpanded(false)
                    .create();
            tvHostName = (TextView) oneyuanEventDialog.findViewById(R.id.tv_host_name);
            tvGuestName = (TextView) oneyuanEventDialog.findViewById(R.id.tv_guest_name);

            btnHostReduce1 = (Button) oneyuanEventDialog.findViewById(R.id.btn_host_reduce1);
            btnHostReduce2 = (Button) oneyuanEventDialog.findViewById(R.id.btn_host_reduce2);
            btnHostReduce3 = (Button) oneyuanEventDialog.findViewById(R.id.btn_host_reduce3);

            btnGuestReduce1 = (Button) oneyuanEventDialog.findViewById(R.id.btn_guest_reduce1);
            btnGuestReduce2 = (Button) oneyuanEventDialog.findViewById(R.id.btn_guest_reduce2);
            btnGuestReduce2 = (Button) oneyuanEventDialog.findViewById(R.id.btn_guest_reduce3);

            btnHostPlus1 = (Button) oneyuanEventDialog.findViewById(R.id.btn_host_plus1);
            btnHostPlus2 = (Button) oneyuanEventDialog.findViewById(R.id.btn_host_plus2);
            btnHostPlus3 = (Button) oneyuanEventDialog.findViewById(R.id.btn_host_plus3);

            btnGuestPlus1 = (Button) oneyuanEventDialog.findViewById(R.id.btn_guest_plus1);
            btnGuestPlus2 = (Button) oneyuanEventDialog.findViewById(R.id.btn_guest_plus2);
            btnGuestPlus2 = (Button) oneyuanEventDialog.findViewById(R.id.btn_guest_plus3);

            btnPreSection = (Button) oneyuanEventDialog.findViewById(R.id.btn_pre_section);
            btnNextSection = (Button) oneyuanEventDialog.findViewById(R.id.btn_next_section);
        }
        if (match != null) {
            tvHostName.setText(hostTeamName);
            tvGuestName.setText(guestTeamName);
        }
        oneyuanEventDialog.show();
    }

    private synchronized void updateMatchScore(Long teamId, Integer eventType) {
        if (isUpdating) {
            showToast("正在操作中...");
            return;
        }
        TimeLine timeLine = new TimeLine();
        timeLine.setMatchId(match.getId());
        timeLine.setTeamId(teamId);
        timeLine.setEventType(eventType);
        timeLine.setAgainstIndex(againstIndex);
        timeLine.setSection(section);
        timeLine.setRemark("1");
        isUpdating = true;
        showWaitingDialog();
        OneyuanServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(OneyuanServiceProvider.class);
        Call<ResponseEntity<Boolean>> response = request.addTimeLine(timeLine);
        try {
            response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Boolean>>() {
                @Override
                public void onRefreshTokenFail() {
                    isUpdating = false;
                    gotoLoginActivity();
                }

                @Override
                public void onSuccess(ResponseEntity<Boolean> result) {
                    isUpdating = false;
                    dismissWaitingDialog();
                    Gson gson = new Gson();
                    if (result.getData()) {
                        queryMatchStatusDetail();
                        showToast("添加成功");
                    } else {
                        showToast("新增失败:" + gson.toJson(result));
                    }
                }

                @Override
                public void onFail(@Nullable Response<ResponseEntity<Boolean>> response, @Nullable Throwable t) {
                    dismissWaitingDialog();
                    isUpdating = false;
                    if (response != null) {
                        showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                    }
                    if (t != null) {
                        showToast("网络请求失败:" + t.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            isUpdating = false;
            showToast("添加失败");
        }
    }

    private void updateMatchSection(Integer eventType) {
        TimeLine timeLine = new TimeLine();
        timeLine.setMatchId(match.getId());
        timeLine.setEventType(eventType);
        timeLine.setAgainstIndex(againstIndex);
        addEvent(timeLine);
    }

    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:17750235615");
        intent.setData(data);
        startActivity(intent);
    }

    private WaitingDialog waitingDialog;

    private void showWaitingDialog() {
        if (waitingDialog == null) {
            waitingDialog = new WaitingDialog(this, "");
            waitingDialog.setCanceledOnTouchOutside(false);
        }
        this.runOnUiThread(() -> waitingDialog.show());
    }

    private void dismissWaitingDialog() {
        this.runOnUiThread(() -> {
            if (waitingDialog != null && !isDestroyed()) {
                waitingDialog.dismiss();
            }
        });
    }
}
