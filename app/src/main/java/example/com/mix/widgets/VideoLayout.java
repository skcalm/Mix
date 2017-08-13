package example.com.mix.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

import example.com.mix.R;

/**
 * Created by gordon on 2017/8/13.
 */

public class VideoLayout extends FrameLayout implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnPreparedListener {
    private static final String TAG = "video_layout";

    private static final String DESC_PLAY = "play";
    private static final String DESC_PAUSE = "pause";

    private static final int PLAY_CONTROL_HIDE_TIMEOUT = 4000;

    private static final int STATE_NORMAL = 0;
    private static final int STATE_ERROR = 1;

    private VideoTextureView mVideoView;
    private ViewGroup mLayoutVideoMask;
    private ImageView mImgCover;
    private ViewGroup mLayoutPlayControl;
    private ImageButton mImgBtnPlayPause;
    private TextView mTxtCurrentTime;
    private SeekBar mSeekBarProgress;
    private TextView mTxtTotalTime;
    private ProgressBar mProgressBar;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private int mState = STATE_NORMAL;
    private String mVideoPath;
    private boolean mSeekBarDragging;

    public VideoLayout(@NonNull Context context) {
        this(context, null);
        log("VideoLayout");
    }

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        log("init in");
        mVideoView = new VideoTextureView(getContext());
        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnSeekCompleteListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setLayoutParams(lp);

        addView(mVideoView);

        mLayoutVideoMask = (ViewGroup) LayoutInflater.from(getContext())
                .inflate(R.layout.layout_video_mask, this, false);
        addView(mLayoutVideoMask);
        mImgCover = (ImageView) mLayoutVideoMask.findViewById(R.id.imgCover);
        mLayoutPlayControl = (ViewGroup) mLayoutVideoMask.findViewById(R.id.layoutPlayControl);
        mImgBtnPlayPause = (ImageButton) mLayoutVideoMask.findViewById(R.id.imgBtnPlayPause);
        mTxtCurrentTime = (TextView) mLayoutVideoMask.findViewById(R.id.txtCurrentTime);
        mSeekBarProgress = (SeekBar) mLayoutVideoMask.findViewById(R.id.seekBarProgress);
        mTxtTotalTime = (TextView) mLayoutVideoMask.findViewById(R.id.txtTotalTime);
        mProgressBar = (ProgressBar) mLayoutVideoMask.findViewById(R.id.progressBar);
        mImgBtnPlayPause.setOnClickListener(mPlayPauseClickListener);
        mSeekBarProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mLayoutVideoMask.setOnClickListener(mVideoMaskClickListener);
        mLayoutPlayControl.setOnTouchListener(mPlayControlTouchListener);
        setPlayPauseView(true);

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        log("init end");
    }

    public void setVideoPath(String path) {
        log("setVideoPath" + path);
        mVideoPath = path;
        mVideoView.setVideoPath(path);
    }

    public void startPlay() {
        mVideoView.start();
    }

    public void setCoverImage(Bitmap bitmap) {
        mImgCover.setImageBitmap(bitmap);
    }

    public void setCoverImage(Drawable drawable) {
        mImgCover.setImageDrawable(drawable);
    }

    private void setPlayPauseView(boolean isShowPlay) {
        mImgBtnPlayPause.setImageResource(isShowPlay ? android.R.drawable.ic_media_play :
                android.R.drawable.ic_media_pause);
        mImgBtnPlayPause.setContentDescription(isShowPlay ? DESC_PLAY : DESC_PAUSE);
    }

    private boolean isShowPlayIcon() {
        String desc = mImgBtnPlayPause.getContentDescription().toString();
        return DESC_PLAY.equals(desc);
    }

    private final OnClickListener mPlayPauseClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            log("mPlayPauseClickListener mState=" + mState);
            if (mState == STATE_ERROR && mVideoPath != null) {
                mState = STATE_NORMAL;
                setVideoPath(mVideoPath);
                return;
            }

            log("mPlayPauseClickListener isPrepared=" + mVideoView.isPrepared());
            if (!mVideoView.isPrepared()) {
                return;
            }

            log("mPlayPauseClickListener isShowPlayIcon=" + isShowPlayIcon());
            removeCallbacks(mPlayControlHideRunnable);
            removeCallbacks(mPlayUpdateProgressRunnable);
            if (isShowPlayIcon()) {
                mVideoView.start();
                setPlayPauseView(false);
                post(mPlayUpdateProgressRunnable);
            } else {
                mVideoView.pause();
                setPlayPauseView(true);
                setTimeView();
            }
            postDelayed(mPlayControlHideRunnable, PLAY_CONTROL_HIDE_TIMEOUT);
        }
    };

    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                log("onProgressChanged fromUser=" + fromUser + " isPrepared=" + mVideoView.isPrepared());
                if (mVideoView.isPrepared()) {
                    int duration = mVideoView.getPlayDuration();
                    log("onProgressChanged duration=" + duration);
                    if (duration > 0) {
                        int seekPosition = (int) ((float) progress / seekBar.getMax() * duration);
                        log("onProgressChanged seekPosition=" + seekPosition);
                        mVideoView.seekTo(seekPosition);
                    }
                }else{
                    seekBar.setProgress(0);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeCallbacks(mPlayControlHideRunnable);
            mSeekBarDragging = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mSeekBarDragging = false;
            postDelayed(mPlayControlHideRunnable, PLAY_CONTROL_HIDE_TIMEOUT);
        }
    };

    private OnClickListener mVideoMaskClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            log("mVideoMaskClickListener");
            removeCallbacks(mPlayControlHideRunnable);
            if (mLayoutPlayControl.getVisibility() == VISIBLE) {
                mLayoutPlayControl.setVisibility(GONE);
            } else {
                mLayoutPlayControl.setVisibility(VISIBLE);
                postDelayed(mPlayControlHideRunnable, PLAY_CONTROL_HIDE_TIMEOUT);
            }
        }
    };

    private OnTouchListener mPlayControlTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            log("mPlayControlTouchListener action=" + action);
            if (action == MotionEvent.ACTION_DOWN) {
                removeCallbacks(mPlayControlHideRunnable);
            } else if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_CANCEL) {
                postDelayed(mPlayControlHideRunnable, PLAY_CONTROL_HIDE_TIMEOUT);
            }
            return true;
        }
    };

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        log("onBufferingUpdate percent=" + percent);
        mSeekBarProgress.setSecondaryProgress(10 * percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        log("onCompletion");
        mVideoView.seekTo(0);
        mSeekBarProgress.setProgress(0);
        mTxtCurrentTime.setText(stringForTime(0));
        setPlayPauseView(true);
        removeCallbacks(mPlayUpdateProgressRunnable);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        log("onError what=" + what + " extra=" + extra);
        mState = STATE_ERROR;
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        log("onInfo what=" + what + " extra=" + extra);
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            mImgCover.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        log("onSeekComplete");
        if(mImgCover.getVisibility() == View.VISIBLE){
            mImgCover.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        log("onPrepared");
        setTimeView();
        mVideoView.seekTo(0);
    }

    private Runnable mPlayControlHideRunnable = new Runnable() {
        @Override
        public void run() {
            log("mPlayControlHideRunnable");
            mLayoutPlayControl.setVisibility(View.GONE);
        }
    };

    private Runnable mPlayUpdateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            log("mPlayUpdateProgressRunnable");
            setTimeView();
            setProgressView();
            postDelayed(this, 1000);
        }
    };

    private void setProgressView() {
        log("setProgressView mSeekBarDragging=" + mSeekBarDragging);
        if(mSeekBarDragging) return;

        final int duration = mVideoView.getPlayDuration();
        log("setProgressView duration=" + duration);
        if (duration <= 0) {
            mSeekBarProgress.setProgress(0);
        } else {
            final int currentPosition = mVideoView.getPlayPosition();
            int progress = (int) (((float) currentPosition) / duration * mSeekBarProgress.getMax());
            log("setProgressView currentPosition=" + currentPosition + " progress=" + progress);
            mSeekBarProgress.setProgress(progress);
        }
    }

    private void setTimeView() {
        log("setTimeView");
        final int duration = mVideoView.getPlayDuration();
        log("setTimeView duration=" + duration);
        if (duration <= 0) {
            mTxtTotalTime.setText("");
            mTxtCurrentTime.setText("");
        } else {
            final int currentPosition = mVideoView.getPlayPosition();
            log("setTimeView currentPosition=" + currentPosition);
            mTxtCurrentTime.setText(stringForTime(currentPosition));
            mTxtTotalTime.setText(stringForTime(duration));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        log("onDetachedFromWindow=");
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void log(String content) {
        Log.d(TAG, content);
    }
}
