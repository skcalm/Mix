package example.com.mix.widgets;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by gordon on 2017/8/12.
 */

public class MediaPlayerThread extends HandlerThread implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnCompletionListener {
    private static final String TAG = "videoThread";

    public static final int ACTION_CREATE_INSTANCE = 0;
    public static final int ACTION_SET_SURFACE = 1;
    public static final int ACTION_SET_PATH = 2;
    public static final int ACTION_PREPARE = 3;
    public static final int ACTION_START = 4;
    public static final int ACTION_PAUSE = 5;
    public static final int ACTION_STOP = 6;
    public static final int ACTION_RESET = 7;
    public static final int ACTION_RELEASE = 8;
    public static final int ACTION_SEEK = 9;
    public static final int ACTION_QUIT = 10;

    @IntDef({ACTION_CREATE_INSTANCE, ACTION_PREPARE,
            ACTION_START, ACTION_PAUSE, ACTION_STOP,
            ACTION_RESET, ACTION_RELEASE, ACTION_QUIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionNoData {
    }

    public static final String[] ACTIONS = new String[]{"ACTION_CREATE_INSTANCE",
            "ACTION_SET_SURFACE", "ACTION_SET_PATH", "ACTION_PREPARE",
            "ACTION_START", "ACTION_PAUSE", "ACTION_STOP", "ACTION_RESET",
            "ACTION_RELEASE", "ACTION_SEEK", "ACTION_QUIT"};

    @IntDef({ACTION_SET_SURFACE, ACTION_SET_PATH, ACTION_SEEK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionWithData {
    }

    public static final int ERROR_SET_DATA_SOURCE = -1;

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private Handler mHandler;
    private final Object mWaitThreadStarted = new Object();

    private MediaPlayer mMediaPlayer;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;
    private MediaPlayer.OnPreparedListener mPreparedListener;
    private MediaPlayer.OnErrorListener mErrorListener;
    private MediaPlayer.OnInfoListener mInfoListener;
    private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener;
    private MediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener;
    private MediaPlayer.OnCompletionListener mCompletionListener;

    public MediaPlayerThread(String name) {
        super(name);
        log("MediaPlayerThread");
    }

    public MediaPlayerThread(String name, int priority) {
        super(name, priority);
    }

    public MediaPlayer mediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    protected void onLooperPrepared() {
        log("onLooperPrepared in");
        mHandler = new MediaPlayerHandler();
        synchronized (mWaitThreadStarted) {
            mWaitThreadStarted.notifyAll();
        }
        log("onLooperPrepared end");
    }

    public void operate(@ActionNoData int... actions) {
        for (int action : actions) {
            log("operate action=" + ACTIONS[action]);
            mHandler.sendEmptyMessage(action);
        }
    }

    public void clearMessageAndReleaseQuit() {
        log("clearMessageAndReleaseQuit");
        removeAllMessage();
        operate(ACTION_RESET, ACTION_RELEASE, ACTION_QUIT);
    }

    public void removeAllMessage() {
        log("removeAllMessage");
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void operateWithData(@ActionWithData int action, Object data) {
        log("operateWithData action=" + ACTIONS[action]);
        Message msg = Message.obtain();
        msg.what = action;
        msg.obj = data;
        mHandler.sendMessage(msg);
    }

    private class MediaPlayerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            log("handleMessage msg.what=" + ACTIONS[msg.what]);
            switch (msg.what) {
                case ACTION_CREATE_INSTANCE:
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setScreenOnWhilePlaying(true);
                    mMediaPlayer.setOnBufferingUpdateListener(MediaPlayerThread.this);
                    mMediaPlayer.setOnPreparedListener(MediaPlayerThread.this);
                    mMediaPlayer.setOnErrorListener(MediaPlayerThread.this);
                    mMediaPlayer.setOnInfoListener(MediaPlayerThread.this);
                    mMediaPlayer.setOnCompletionListener(MediaPlayerThread.this);
                    mMediaPlayer.setOnVideoSizeChangedListener(MediaPlayerThread.this);
                    mMediaPlayer.setOnSeekCompleteListener(MediaPlayerThread.this);
                    break;
                case ACTION_SET_SURFACE:
                    mMediaPlayer.setSurface((Surface) msg.obj);
                    break;
                case ACTION_SET_PATH:
                    try {
                        mMediaPlayer.setDataSource((String) msg.obj);
                    } catch (IOException e) {
                        e.printStackTrace();
                        onError(mMediaPlayer, ERROR_SET_DATA_SOURCE, 0);
                    }
                    break;
                case ACTION_PREPARE:
                    mMediaPlayer.prepareAsync();
                    break;
                case ACTION_START:
                    mMediaPlayer.start();
                    break;
                case ACTION_PAUSE:
                    mMediaPlayer.pause();
                    break;
                case ACTION_STOP:
                    mMediaPlayer.stop();
                    break;
                case ACTION_RESET:
                    mMediaPlayer.reset();
                    break;
                case ACTION_RELEASE:
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    break;
                case ACTION_SEEK:
                    mMediaPlayer.seekTo((int) msg.obj);
                    break;
                case ACTION_QUIT:
                    quit();
                    break;
            }
        }
    }

    @Override
    public void onBufferingUpdate(final MediaPlayer mp, final int percent) {
        if (mBufferingUpdateListener != null) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBufferingUpdateListener.onBufferingUpdate(mp, percent);
                }
            });
        }
    }

    @Override
    public void onCompletion(final MediaPlayer mp) {
        if (mCompletionListener != null) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCompletionListener.onCompletion(mp);
                }
            });
        }
    }

    @Override
    public boolean onError(final MediaPlayer mp, final int what, final int extra) {
        if (mErrorListener != null) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mErrorListener.onError(mp, what, extra);
                }
            });
        }
        return true;
    }

    @Override
    public boolean onInfo(final MediaPlayer mp, final int what, final int extra) {
        if (mInfoListener != null) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mInfoListener.onInfo(mp, what, extra);
                }
            });
        }
        return false;
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        if (mPreparedListener != null) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPreparedListener.onPrepared(mp);
                }
            });
        }
    }

    @Override
    public void onSeekComplete(final MediaPlayer mp) {
        if (mSeekCompleteListener != null) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSeekCompleteListener.onSeekComplete(mp);
                }
            });
        }
    }

    @Override
    public void onVideoSizeChanged(final MediaPlayer mp, final int width, final int height) {
        if (mVideoSizeChangedListener != null) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mVideoSizeChangedListener.onVideoSizeChanged(mp, width, height);
                }
            });
        }
    }

    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) {
        mBufferingUpdateListener = listener;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        mPreparedListener = listener;
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

    public void setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener listener) {
        mVideoSizeChangedListener = listener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener) {
        mSeekCompleteListener = listener;
    }

    @Override
    public void start() {
        log("thread start in");
        synchronized (mWaitThreadStarted) {
            super.start();
            try {
                mWaitThreadStarted.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log("thread start end");
    }

    public static MediaPlayerThread create() {
        log("create in");
        MediaPlayerThread mediaPlayerThread = new MediaPlayerThread("MediaPlayerThread");
        mediaPlayerThread.start();
        mediaPlayerThread.operate(ACTION_CREATE_INSTANCE);
        log("create end");
        return mediaPlayerThread;
    }

    public static void log(String content) {
        Log.d(TAG, content);
    }
}
