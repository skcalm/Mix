package example.com.mix.widgets;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by Administrator on 2017/8/10.
 */

public class VideoTextureView extends TextureView implements MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_COMPLETED = 5;

    private static final int ACTION_RESET = 0;
    private static final int ACTION_RELEASE = 1;
    private static final int ACTION_STOP = 2;

    private MediaPlayer mMediaPlayer;
    private MediaPlayerHandler mMediaPlayerHandler;
    private HandlerThread mMediaPlayerThread;
    private int mPlayState = STATE_IDLE;
    private boolean mIsSurfaceAvailable;
    private Uri mVideoUri;

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
        setSurfaceTextureListener(mSurfaceListener);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayerThread = new HandlerThread("mediaPlayerThread");
        mMediaPlayerHandler = new MediaPlayerHandler(mMediaPlayerThread.getLooper());
    }

    private final SurfaceTextureListener mSurfaceListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mIsSurfaceAvailable = true;
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mIsSurfaceAvailable = false;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private class MediaPlayerHandler extends Handler {
        public MediaPlayerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACTION_RESET:
                    mMediaPlayer.reset();
                    break;
                case ACTION_RELEASE:
                    mMediaPlayer.release();
                    break;
            }
        }
    }

    private void mediaPlayerOperate(int... actions) {
        for (int action : actions) {
            mMediaPlayerHandler.sendEmptyMessage(action);
        }
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mVideoUri = uri;
        initVideo();
    }

    private void initVideo() {

        release();
    }

    public void release() {
        switch (mPlayState) {
            case STATE_IDLE:
                break;
            case STATE_PLAYING:
            case STATE_PAUSED:
                mediaPlayerOperate(ACTION_STOP);
            case STATE_ERROR:
            case STATE_PREPARING:
            case STATE_PREPARED:
            case STATE_COMPLETED:
                mediaPlayerOperate(ACTION_RESET, ACTION_RELEASE);
                break;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}
