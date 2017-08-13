package example.com.mix.widgets;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import static example.com.mix.widgets.MediaPlayerThread.ACTION_PAUSE;
import static example.com.mix.widgets.MediaPlayerThread.ACTION_PREPARE;
import static example.com.mix.widgets.MediaPlayerThread.ACTION_SET_PATH;
import static example.com.mix.widgets.MediaPlayerThread.ACTION_SET_SURFACE;
import static example.com.mix.widgets.MediaPlayerThread.ACTION_START;

/**
 * Created by Administrator on 2017/8/10.
 */

public class VideoTextureView extends TextureView implements MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener {
    private static final String TAG = "videoView";

    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_COMPLETED = 5;

    private MediaPlayerThread mMediaPlayerThread;
    private int mPlayState = STATE_IDLE;
    private boolean mIsSurfaceAvailable;
    private String mVideoPath;
    private boolean mWaitPlay;
    private final Object mPlayLock = new Object();
    private final Object mReleaseLock = new Object();

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;
    private MediaPlayer.OnErrorListener mErrorListener;
    private MediaPlayer.OnInfoListener mInfoListener;
    private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private MediaPlayer.OnPreparedListener mPreparedListener;

    public VideoTextureView(Context context) {
        this(context, null);
    }

    public VideoTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        log("init");
        setSurfaceTextureListener(mSurfaceListener);
    }

    private final SurfaceTextureListener mSurfaceListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            log("onSurfaceTextureAvailable in");
            synchronized (mPlayLock) {
                log("onSurfaceTextureAvailable mVideoPath=" + mVideoPath);
                mIsSurfaceAvailable = true;
                if (mVideoPath != null) {
                    initVideo();
                }
            }
            log("onSurfaceTextureAvailable end");
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            log("onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            log("onSurfaceTextureDestroyed in");
            synchronized (mPlayLock) {
                boolean isPlaying = isPlaying();
                boolean isWaitPlay = mWaitPlay;
                log("onSurfaceTextureDestroyed isPlaying=" + isPlaying + " isWaitPlay=" + isWaitPlay);
                mIsSurfaceAvailable = false;
                release();
                if (isWaitPlay || isPlaying) {
                    mWaitPlay = true;
                }
            }
            log("onSurfaceTextureDestroyed end");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            //log("onSurfaceTextureUpdated");
        }
    };

    public void setVideoPath(String path) {
        log("setVideoPath in");
        synchronized (mPlayLock) {
            log("setVideoPath path=" + path);
            mVideoPath = path;
            initVideo();
        }
        log("setVideoPath end");
    }

    private void initVideo() {
        log("initVideo in");
        log("initVideo mIsSurfaceAvailable=" + mIsSurfaceAvailable);
        if (!mIsSurfaceAvailable) return;

        release();
        mMediaPlayerThread = MediaPlayerThread.create();
        mMediaPlayerThread.setOnBufferingUpdateListener(this);
        mMediaPlayerThread.setOnErrorListener(this);
        mMediaPlayerThread.setOnInfoListener(this);
        mMediaPlayerThread.setOnPreparedListener(this);
        mMediaPlayerThread.setOnCompletionListener(this);
        mMediaPlayerThread.setOnSeekCompleteListener(this);
        mMediaPlayerThread.operateWithData(ACTION_SET_SURFACE,
                new Surface(getSurfaceTexture()));
        mMediaPlayerThread.operateWithData(ACTION_SET_PATH, mVideoPath);
        mMediaPlayerThread.operate(ACTION_PREPARE);
        mPlayState = STATE_PREPARING;
        log("initVideo end");
    }

    public void start() {
        log("start in");
        log("start isPrepared=" + isPrepared());
        if (isPrepared()) {
            mMediaPlayerThread.operate(ACTION_START);
            mWaitPlay = false;
            mPlayState = STATE_PLAYING;
        } else {
            mWaitPlay = true;
        }
        log("start end");
    }

    public void pause() {
        log("start isPrepared=" + isPrepared());
        if (isPrepared()) {
            mMediaPlayerThread.operate(ACTION_PAUSE);
        }
        mWaitPlay = false;
    }

    public void seekTo(int millis){
        if(isPrepared()){
            mMediaPlayerThread.operateWithData(MediaPlayerThread.ACTION_SEEK, millis);
        }
    }

    public boolean isPlaying() {
        log("isPlaying in");
        MediaPlayer mediaPlayer = mediaPlayer();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            log("isPlaying end true");
            return true;
        }
        log("isPlaying end false");

        return false;
    }

    private MediaPlayer mediaPlayer() {
        final MediaPlayerThread playerThread = mMediaPlayerThread;
        if (playerThread != null) {
            return playerThread.mediaPlayer();
        }
        return null;
    }

    public void release() {
        log("release in");
        synchronized (mReleaseLock) {
            log("release mPlayState=" + mPlayState);
            switch (mPlayState) {
                case STATE_IDLE:
                    break;
                case STATE_PLAYING:
                case STATE_PAUSED:
                case STATE_ERROR:
                case STATE_PREPARING:
                case STATE_PREPARED:
                case STATE_COMPLETED:
                    if (mMediaPlayerThread != null) {
                        mMediaPlayerThread.clearMessageAndReleaseQuit();
                        mMediaPlayerThread = null;
                    }
                    mPlayState = STATE_IDLE;
                    mWaitPlay = false;
                    break;
            }
        }
        log("release end");
    }

    public boolean isPrepared() {
        return mPlayState != STATE_IDLE &&
                mPlayState != STATE_ERROR &&
                mPlayState != STATE_PREPARING;
    }

    public int getPlayPosition() {
        if (isPrepared()) {
            MediaPlayer mediaPlayer = mediaPlayer();
            if (mediaPlayer != null) {
                return mediaPlayer.getCurrentPosition();
            }
        }
        return 0;
    }

    public int getPlayDuration() {
        if (isPrepared()) {
            MediaPlayer mediaPlayer = mediaPlayer();
            if (mediaPlayer != null) {
                return mediaPlayer.getDuration();
            }
        }
        return -1;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        log("onBufferingUpdate percent=" + percent);
        if (mBufferingUpdateListener != null) {
            mBufferingUpdateListener.onBufferingUpdate(mp, percent);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        log("onCompletion");
        mPlayState = STATE_COMPLETED;
        if (mCompletionListener != null) {
            mCompletionListener.onCompletion(mp);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        log("onError what=" + what + " extra=" + extra);
        mPlayState = STATE_ERROR;
        if (mErrorListener != null) {
            mErrorListener.onError(mp, what, extra);
        }
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        log("onInfo what=" + what + " extra=" + extra);
        if (mInfoListener != null) {
            mInfoListener.onInfo(mp, what, extra);
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        log("onPrepared in");
        mPlayState = STATE_PREPARED;
        if (mWaitPlay) {
            start();
        }
        if (mPreparedListener != null) {
            mPreparedListener.onPrepared(mp);
        }
        log("onPrepared end");
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        log("onSeekComplete");
        if (mSeekCompleteListener != null) {
            mSeekCompleteListener.onSeekComplete(mp);
        }
    }

    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) {
        mBufferingUpdateListener = listener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        mErrorListener = listener;
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener listener) {
        mInfoListener = listener;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mCompletionListener = listener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener) {
        mSeekCompleteListener = listener;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        mPreparedListener = listener;
    }

    public void log(String content) {
        Log.d(TAG, content);
    }
}
