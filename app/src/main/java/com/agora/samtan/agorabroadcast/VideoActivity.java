package com.agora.samtan.agorabroadcast;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;


public class VideoActivity extends AppCompatActivity {

    private RtcEngine mRtcEngine;
    private String channelName;
    private int channelProfile;
    public static final String LOGIN_MESSAGE = "com.agora.samtan.agorabroadcast.CHANNEL_LOGIN";
    public static final String TOKEN = "007eJxTYDi6QIZX5MhJsf3dfVrPLOa4v3XX3x2pyCD0eZrtXiV9nU0KDEaG5oYmxpbGFuaJJiYWyQYWqeZpKcaGxiZpBmZmpknGWfIpKQ2BjAy5jw+wMDIwMrAAMYjPBCaZwSQLmORiyMxLT83LTC3KTGRgAABQ3SFv";

    private IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }
    };

    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        channelName = intent.getStringExtra(MainActivity.channelMessage);
        channelProfile = intent.getIntExtra(MainActivity.profileMessage, -1);

        if (channelProfile == -1) {
            Log.e("TAG: ", "No profile");
        }

        initAgoraEngineAndJoinChannel();
    }

    private void initAgoraEngineAndJoinChannel() {
        initalizeAgoraEngine();
        mRtcEngine.setClientRole(channelProfile);
        mRtcEngine.enableVideo();
        mRtcEngine.enableAudio();
        setupLocalVideo();
        mRtcEngine.startPreview();
        joinChannel();
    }

    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
    }



    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        container.removeAllViews();
    }



    private void initalizeAgoraEngine() {
        try {
            RtcEngineConfig rtcEngineConfig = new RtcEngineConfig();
            rtcEngineConfig.mAppId = "2171439387a448c08e7fd3134f0665b3";
            rtcEngineConfig.mContext = this.getApplicationContext();
            rtcEngineConfig.mEventHandler = mRtcEventHandler;

            mRtcEngine = RtcEngine.create(rtcEngineConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupRemoteVideo(int uid) {
        SurfaceView remoteView = new SurfaceView(getApplicationContext());
        //SurfaceView surfaceView = RtcEngine.CreateRendererView(this);

        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        if (container.getChildCount()>1){
            return;
        }

        container.addView(remoteView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        remoteView.getTag();
    }

    private void setupLocalVideo() {
        SurfaceView localView = new SurfaceView(getApplicationContext());
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        if(channelProfile == 1) {
            container.addView(localView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            localView.setZOrderMediaOverlay(true);
            mRtcEngine.setupLocalVideo(new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        }else{
            container.setVisibility(View.GONE);
        }
    }

    private void joinChannel() {
        mRtcEngine.joinChannel(TOKEN, channelName, "Optional Data", 0);
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    public void onEndCallClicked(View view) {
        this.finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
