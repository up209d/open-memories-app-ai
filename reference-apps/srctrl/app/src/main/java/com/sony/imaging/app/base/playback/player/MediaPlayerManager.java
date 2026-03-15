package com.sony.imaging.app.base.playback.player;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.scalar.media.MediaPlayer;
import java.io.IOException;

/* loaded from: classes.dex */
public class MediaPlayerManager extends NotificationManager {
    protected static final String MSG_INIT = "initialize : ";
    protected static final String MSG_MOVE_TO_FIRST = "moveToFirstFrame";
    protected static final String MSG_ON_COMP = "onCompletion";
    protected static final String MSG_ON_LAP = "onLapTimeEvent : ";
    protected static final String MSG_PAUSE = "pause";
    protected static final String MSG_SEEK = "seekTo : ";
    protected static final String MSG_SET_CONFIG = "setConfig";
    protected static final String MSG_SET_CONTENT = "setContent : ";
    protected static final String MSG_SET_SPEED = "setPlaySpeed : ";
    protected static final String MSG_SET_SURFACE = "setSurface : ";
    protected static final String MSG_START = "start";
    protected static final String MSG_STATUS_CHANGED = "statusChanged : ";
    protected static final String MSG_TERMINATE = "terminate";
    protected static final int PF_VER_SUPPORTING_SEEK = 9;
    private static final String TAG = "MediaPlayerManager";
    public static final String TAG_LAPTIME_CHANGED = "on_laptime_changed";
    public static final String TAG_ON_ERROR = "on_error";
    public static final String TAG_STATUS_CHANGED = "on_status_changed";
    protected static MediaPlayerManager sInstance = new MediaPlayerManager();
    protected ErrorStatus mErrorStatus;
    protected MediaPlayer mMediaPlayer;
    protected Status mStatus = Status.Uninitialized;
    protected int mLapTime = -1;
    protected boolean mIsContentNeeded = true;
    protected ContentInfo mContentInfo = null;

    /* loaded from: classes.dex */
    public enum Status {
        Uninitialized,
        Idle,
        Prepared,
        Starting,
        Started,
        PausedAtTop,
        Paused,
        PlaybackCompleted,
        Stopped,
        Error
    }

    /* loaded from: classes.dex */
    public class ErrorStatus {
        public int extra;
        public int what;

        public ErrorStatus(int aWhat, int anExtra) {
            this.what = aWhat;
            this.extra = anExtra;
        }
    }

    protected MediaPlayerManager() {
    }

    public static MediaPlayerManager getInstance() {
        return sInstance;
    }

    public MediaPlayer getMediaPlayer() {
        return this.mMediaPlayer;
    }

    public static boolean isSupportedContent(ContentsIdentifier id) {
        if (Environment.isMoviePlaybackSupported() && id != null) {
            return id.contentType == 4 || id.contentType == 256;
        }
        return false;
    }

    public void initialize(boolean forceInit) {
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_INIT).append(forceInit).toString());
        if (Environment.isMoviePlaybackSupported()) {
            if (forceInit || Status.Uninitialized.equals(this.mStatus) || Status.Error.equals(this.mStatus)) {
                if (this.mMediaPlayer == null) {
                    this.mMediaPlayer = new MediaPlayer();
                    initListener();
                }
                this.mMediaPlayer.reset();
                this.mErrorStatus = null;
                this.mContentInfo = null;
                this.mLapTime = -1;
                setStatus(Status.Idle);
            }
        }
    }

    public void terminate() {
        Log.i(TAG, MSG_TERMINATE);
        if (this.mMediaPlayer != null) {
            terminateListener();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        this.mContentInfo = null;
        this.mErrorStatus = null;
        this.mLapTime = -1;
        setStatus(Status.Uninitialized);
    }

    @Deprecated
    public boolean setSurfaceData(SurfaceHolder holder) {
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_SET_SURFACE).append(holder).toString());
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.setDisplay(holder);
            return true;
        }
        return true;
    }

    @Deprecated
    public boolean setSurfacData(Surface surface) {
        return setSurface(surface);
    }

    @Deprecated
    public boolean setSurface(Surface surface) {
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_SET_SURFACE).append(surface).toString());
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.setSurface(surface);
            return true;
        }
        return true;
    }

    public void setStatus(Status status) {
        if (status != null) {
            Status current = this.mStatus;
            this.mStatus = status;
            if (status != null && !status.equals(current)) {
                Log.i(TAG, LogHelper.getScratchBuilder(MSG_STATUS_CHANGED).append(status).toString());
                notify(TAG_STATUS_CHANGED, true);
            }
        }
    }

    public Status getStatus() {
        return this.mStatus;
    }

    protected void initListener() {
        if (Environment.isMoviePlaybackSupported()) {
            MediaPlayerListener listener = getMediaPlayerListener();
            this.mMediaPlayer.setOnCompletionListener(listener);
            this.mMediaPlayer.setOnErrorListener(listener);
            this.mMediaPlayer.setOnLapTimeEventListener(listener);
        }
    }

    protected void terminateListener() {
        if (Environment.isMoviePlaybackSupported()) {
            this.mMediaPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener) null);
            this.mMediaPlayer.setOnErrorListener((MediaPlayer.OnErrorListener) null);
            this.mMediaPlayer.setOnLapTimeEventListener((MediaPlayer.OnLapTimeEventListener) null);
        }
    }

    public MediaPlayer.PlayerConfig getConfig() {
        if (this.mMediaPlayer != null) {
            return this.mMediaPlayer.getPlayerConfig();
        }
        return null;
    }

    public boolean setConfig(MediaPlayer.PlayerConfig config) {
        Log.i(TAG, MSG_SET_CONFIG);
        if (this.mMediaPlayer == null || !(Status.Idle.equals(this.mStatus) || Status.Prepared.equals(this.mStatus))) {
            return false;
        }
        this.mMediaPlayer.setPlayerConfig(config);
        return true;
    }

    public void setIfContentInfoUsed(boolean useIt) {
        this.mIsContentNeeded = useIt;
    }

    public ContentInfo getLastSetContentInfo() {
        return this.mContentInfo;
    }

    public boolean setContent(ContentsIdentifier id) {
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_SET_CONTENT).append(id.data).toString());
        boolean result = false;
        Status status = this.mStatus;
        if (this.mMediaPlayer != null) {
            if (!Status.Idle.equals(this.mStatus)) {
                this.mMediaPlayer.reset();
                status = Status.Idle;
            }
            try {
                if (this.mIsContentNeeded) {
                    ContentsManager contentsManager = ContentsManager.getInstance();
                    ContentsIdentifier current = contentsManager.getContentsId();
                    if (current != null && current.data != null && current.data.equals(id.data)) {
                        contentsManager.requestUpdateData(true);
                    }
                    ContentInfo info = contentsManager.getContentInfo(id);
                    if (info != null && info.makeData()) {
                        this.mContentInfo = info;
                    } else {
                        this.mContentInfo = null;
                    }
                }
                this.mLapTime = -1;
                this.mMediaPlayer.setDataSource(id.data);
                this.mMediaPlayer.prepare();
                status = Status.Prepared;
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                handleError(300, 0);
                return false;
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                e3.printStackTrace();
            } catch (SecurityException e4) {
                e4.printStackTrace();
            }
        }
        setStatus(status);
        return result;
    }

    public boolean start() {
        Log.i(TAG, "start");
        boolean result = false;
        Status status = this.mStatus;
        if (this.mMediaPlayer != null) {
            if (Status.PlaybackCompleted.equals(this.mStatus)) {
                this.mMediaPlayer.stop();
                status = Status.Stopped;
                try {
                    this.mMediaPlayer.prepare();
                    status = Status.Prepared;
                } catch (IOException e) {
                    e.printStackTrace();
                    handleError(300, 0);
                    return false;
                } catch (IllegalStateException e2) {
                    e2.printStackTrace();
                }
            }
            if (Status.Prepared.equals(this.mStatus) || Status.Started.equals(this.mStatus) || Status.Paused.equals(this.mStatus) || Status.PausedAtTop.equals(this.mStatus)) {
                try {
                    this.mMediaPlayer.start();
                    status = Status.Started;
                    result = true;
                } catch (IOException e3) {
                    e3.printStackTrace();
                    handleError(300, 0);
                    return false;
                } catch (IllegalStateException e4) {
                    e4.printStackTrace();
                }
            }
        }
        setStatus(status);
        return result;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0015. Please report as an issue. */
    public boolean moveToFirstFrame() {
        Log.i(TAG, MSG_MOVE_TO_FIRST);
        boolean result = false;
        Status status = this.mStatus;
        switch (this.mStatus) {
            case PausedAtTop:
                result = true;
                setStatus(status);
                return result;
            case Paused:
            case Started:
            case PlaybackCompleted:
                this.mMediaPlayer.stop();
                status = Status.Stopped;
                try {
                    this.mMediaPlayer.prepare();
                    status = Status.Prepared;
                    result = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    handleError(300, 0);
                    return false;
                } catch (IllegalStateException e2) {
                    e2.printStackTrace();
                }
                setStatus(status);
                return result;
            default:
                setStatus(status);
                return result;
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:33:0x0075 -> B:24:0x0045). Please report as a decompilation issue!!! */
    public boolean pause() {
        Log.i(TAG, MSG_PAUSE);
        boolean result = false;
        Status status = this.mStatus;
        if (this.mMediaPlayer != null) {
            if (Status.PlaybackCompleted.equals(this.mStatus)) {
                this.mMediaPlayer.stop();
                status = Status.Stopped;
                try {
                    this.mMediaPlayer.prepare();
                    status = Status.Prepared;
                } catch (IOException e) {
                    e.printStackTrace();
                    handleError(300, 0);
                    return false;
                } catch (IllegalStateException e2) {
                    e2.printStackTrace();
                }
            }
            try {
                if (Status.PausedAtTop.equals(this.mStatus) || Status.Prepared.equals(this.mStatus)) {
                    this.mMediaPlayer.pause();
                    status = Status.PausedAtTop;
                    result = true;
                } else if (Status.Started.equals(this.mStatus) || Status.Paused.equals(this.mStatus)) {
                    this.mMediaPlayer.pause();
                    status = Status.Paused;
                    result = true;
                }
            } catch (IOException e3) {
                e3.printStackTrace();
                handleError(300, 0);
                return false;
            } catch (IllegalStateException e4) {
                e4.printStackTrace();
            }
        }
        setStatus(status);
        return result;
    }

    public int getPlaySpeed() {
        if (this.mMediaPlayer != null) {
            return this.mMediaPlayer.getPlaybackSpeed();
        }
        return 0;
    }

    public boolean setPlaySpeed(int speed) {
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_PAUSE).append(speed).toString());
        if (this.mMediaPlayer == null) {
            return false;
        }
        if (!Status.Prepared.equals(this.mStatus) && !Status.Started.equals(this.mStatus) && !Status.Paused.equals(this.mStatus) && !Status.PausedAtTop.equals(this.mStatus)) {
            return true;
        }
        try {
            this.mMediaPlayer.setPlaybackSpeed(speed);
            if (!Status.Started.equals(this.mStatus)) {
                return true;
            }
            notify(TAG_STATUS_CHANGED, true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            handleError(300, 0);
            return false;
        }
    }

    public boolean canSeek() {
        return 9 <= Environment.getVersionPfAPI();
    }

    public boolean seekTo(int msec) {
        if (!canSeek()) {
            return false;
        }
        boolean result = false;
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_SEEK).append(msec).toString());
        if (this.mMediaPlayer == null) {
            return false;
        }
        if (!Status.Started.equals(this.mStatus) && !Status.Paused.equals(this.mStatus) && !Status.PausedAtTop.equals(this.mStatus) && !Status.PlaybackCompleted.equals(this.mStatus)) {
            return false;
        }
        try {
            this.mMediaPlayer.seekTo(msec);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            handleError(300, 0);
            return false;
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        }
        if (Status.PlaybackCompleted.equals(this.mStatus)) {
            setStatus(Status.Paused);
            return result;
        }
        return result;
    }

    protected void handleError(int what, int extra) {
        Log.w(TAG, "onError what = " + what + ", extra = " + extra);
        setStatus(Status.Error);
        this.mErrorStatus = new ErrorStatus(what, extra);
        notify(TAG_ON_ERROR);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class MediaPlayerListener implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnLapTimeEventListener {
        protected MediaPlayerListener() {
        }

        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (1 == what) {
                throw new RuntimeException("notified MEDIA_ERROR_UNKNOWN");
            }
            MediaPlayerManager.this.handleError(what, extra);
            return true;
        }

        public void onCompletion(MediaPlayer arg0) {
            Log.i(MediaPlayerManager.TAG, MediaPlayerManager.MSG_ON_COMP);
            if (!Status.Error.equals(MediaPlayerManager.this.mStatus)) {
                MediaPlayerManager.this.setStatus(Status.PlaybackCompleted);
            }
        }

        public void onLapTimeEvent(MediaPlayer arg0, int arg1, int arg2) {
            Log.i(MediaPlayerManager.TAG, LogHelper.getScratchBuilder(MediaPlayerManager.MSG_ON_LAP).append(arg1).toString());
            MediaPlayerManager.this.mLapTime = arg1;
            MediaPlayerManager.this.notify(MediaPlayerManager.TAG_LAPTIME_CHANGED, true);
        }
    }

    protected MediaPlayerListener getMediaPlayerListener() {
        return new MediaPlayerListener();
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        if (TAG_ON_ERROR.equals(tag)) {
            return this.mErrorStatus;
        }
        return null;
    }

    public int getLapTime() {
        return this.mLapTime;
    }

    public int getDuration() {
        if (this.mMediaPlayer != null) {
            return this.mMediaPlayer.getDuration();
        }
        return -1;
    }
}
