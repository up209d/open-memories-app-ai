package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.common.AudioSetting;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.base.shooting.movie.MovieStableExecutor;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public abstract class ExecutorCreator extends HandlerThread {
    public static final int FINALIZED = 102;
    public static final int INITIALIZED = 101;
    private static final String LOG_ADAPTER = "adapter";
    protected static final String LOG_CAMERA = "CAMERA :";
    private static final String LOG_COLON = ": ";
    private static final String LOG_EXECUTOR = "executor";
    private static final String LOG_GET_NEXT_EXECUTOR = "getNextExecutor";
    private static final String LOG_INITIAL_RUNNABLE = "Initial Runnable: ";
    private static final String LOG_INTERVAL = "REC_MODE_INTERVAL";
    private static final String LOG_LOOPREC = "REC_MODE_LOOPREC";
    private static final String LOG_MOVIE = "REC_MODE_MOVIE";
    private static final String LOG_MOVIE_LOOP_REC_SEQ_UPDATE_RUNNABLE = "MovieLoopRecUpdateRunnable";
    private static final String LOG_MOVIE_REC_PREPARED_RUNNABLE = "MovieRecPreparedRunnable";
    private static final String LOG_MY_CHANGE_REC_MODE_RUNNABLE = "MyChangeRecModeRunnable";
    private static final String LOG_MY_UPDATE_SEQUENCE = "MyUpdateSequence";
    private static final String LOG_ON_PREPARED = "onPrepared";
    private static final String LOG_ON_RELEASED = "onReleased";
    private static final String LOG_PROCESS = "process";
    protected static final String LOG_RUNNABLE = "Runnable";
    private static final String LOG_SETRECORDINGMODE = "setRecordingMode: ";
    private static final String LOG_SET_UP = "setup";
    private static final String LOG_SPACE = " ";
    private static final String LOG_STABLE_SEQUENCE = "stableSequence : ";
    private static final String LOG_STABLE_SEQUENCE_NEEDLESS = "stableSequence needless : ";
    private static final String LOG_STILL = "REC_MODE_STILL";
    private static final String LOG_STILL_INTERVAL_REC_SEQ_UPDATE_RUNNABLE = "StillIntervalReqUpdateRunnable";
    private static final String LOG_STILL_REC_PREPARED_RUNNABLE = "StillRecPreparedRunnable";
    private static final String LOG_STOP = "stop";
    private static final String LOG_TERMINATE = "terminate";
    protected static final String LOG_UI = "UI :";
    private static final String LOG_UNSTABLE = "REC_MODE_UNSTABLE";
    private static final String LOG_UPDATE_SEQUENCE = "updateSequence : ";
    public static final int MEDIA_STABLE = 2;
    private static final String PTAG_OPENED_CAMERA = "displayed EE screen";
    private static final String PTAG_STARTED_DIRECT_SHUTTER = "DLApp Boot. called startDirectShutter";
    private static final int REC_MODE_CAMERA_MOVIES = 10;
    private static final int REC_MODE_CAMERA_STILLS = 5;
    public static final int REC_MODE_INTERVAL = 4;
    public static final int REC_MODE_LOOP = 8;
    public static final int REC_MODE_MOVIE = 2;
    public static final int REC_MODE_NONE = 0;
    public static final int REC_MODE_STILL = 1;
    public static final int REC_MODE_UNSTABLE = 32768;
    public static final int REOPENING = 103;
    public static final int STANDARD_STABLE = 1;
    private static final String TAG = "ExecutorCreator";
    public static final int UNINITIALIZED = 100;
    private static final int WAITING_TIME_OUT = 100000;
    private Object lockObject;
    protected volatile IAdapter mAdapter;
    protected AudioManager mAudioManager;
    private BaseExclusiveSetting mBaseExclusiveSetting;
    private CameraSetting mCamSetting;
    protected CameraEx mCameraEx;
    protected volatile BaseShootingExecutor mExecutor;
    private HaltRunnable mHaltRunnable;
    private Handler mHandlerFromMain;
    private Handler mHandlerToMain;
    private InitRunnable mInitRunnable;
    private IntervalRecExecutor mIntervalRecExecutor;
    protected volatile boolean mIsRecModeChanging;
    protected MediaRecorder mMediaRecorder;
    private MovieShootingExecutor mMovieShootingExecutor;
    private MovieStableExecutor mMovieStableExecutor;
    private volatile Runnable mMyOpenDoneRunnable;
    private NormalExecutor mNormalExecutor;
    private CameraNotificationManager mNotify;
    private CameraEx.OpenCallback mOpenCallbackEC;
    protected volatile IProcess mProcess;
    private int mRecMode;
    private SpinalExecutor mSpinalExecutor;
    private StableExecutor mStableExecutor;
    private StableReleasedListener mStableReleasedListener;
    private State mState;
    private StoppedListener mStoppedListener;
    private int mSupportingRecMode;
    private Object mUpdateLock;
    private UpdateReleasedListener mUpdateReleasedListener;
    private boolean mUpdated;
    private volatile int prepareStatus;
    private static ExecutorCreator sDummy = new DummyExecutorCreator();
    private static ExecutorCreator sCreator = null;
    private static final StringBuilderThreadLocal STRBUILDS = new StringBuilderThreadLocal();
    private static int mStableFlg = 0;

    protected abstract IProcess getProcess(Camera camera, CameraEx cameraEx);

    protected abstract boolean isImmediatelyEEStart();

    protected abstract boolean isInheritDigitalZoomSetting();

    protected abstract boolean isInheritSetting();

    protected abstract boolean isSpinal();

    protected abstract boolean isSpinalZoomSetting();

    static /* synthetic */ int access$372(int x0) {
        int i = mStableFlg & x0;
        mStableFlg = i;
        return i;
    }

    static /* synthetic */ int access$376(int x0) {
        int i = mStableFlg | x0;
        mStableFlg = i;
        return i;
    }

    public int defaultBootModeToRecMode(String bootMode) {
        if ("movie".equals(bootMode)) {
            return 2;
        }
        if ("photo".equals(bootMode)) {
            return 1;
        }
        if ("interval".equals(bootMode)) {
            return 4;
        }
        if (!"loop".equals(bootMode)) {
            return 0;
        }
        return 8;
    }

    public String recModeTodefaultBootMode(int recMode) {
        switch (recMode) {
            case 1:
                return "photo";
            case 2:
                return "movie";
            case 3:
            case 5:
            case 6:
            case 7:
            default:
                return null;
            case 4:
                return "interval";
            case 8:
                return "loop";
        }
    }

    private static void setSequenceCreator(ExecutorCreator creator) {
        if (sCreator == null && !(creator instanceof DummyExecutorCreator)) {
            sCreator = creator;
            creator.start();
        }
    }

    public MediaRecorder getMediaRecorder() {
        return this.mMediaRecorder;
    }

    public AudioManager getAudioManager() {
        return this.mAudioManager;
    }

    public static ExecutorCreator getInstance() {
        return sCreator == null ? sDummy : sCreator;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ExecutorCreator() {
        super(TAG);
        this.mBaseExclusiveSetting = new BaseExclusiveSetting();
        this.mSupportingRecMode = 32768;
        this.mRecMode = 32768;
        this.mIsRecModeChanging = false;
        this.mCameraEx = null;
        this.mExecutor = null;
        this.mAdapter = null;
        this.mProcess = null;
        this.mNormalExecutor = null;
        this.mSpinalExecutor = null;
        this.mStableExecutor = null;
        this.mMovieShootingExecutor = null;
        this.mMovieStableExecutor = null;
        this.mIntervalRecExecutor = null;
        this.mMediaRecorder = null;
        this.mAudioManager = null;
        this.lockObject = new Object();
        this.prepareStatus = 100;
        this.mInitRunnable = new InitRunnable();
        this.mUpdated = false;
        this.mHaltRunnable = new HaltRunnable();
        this.mStoppedListener = new StoppedListener();
        this.mOpenCallbackEC = null;
        this.mMyOpenDoneRunnable = null;
        this.mUpdateLock = new Object();
        this.mUpdateReleasedListener = new UpdateReleasedListener();
        this.mStableReleasedListener = new StableReleasedListener();
        initializeData();
    }

    protected void initializeData() {
        this.mHandlerToMain = new Handler(Looper.getMainLooper());
        setSequenceCreator(this);
        this.mCamSetting = CameraSetting.getInstance();
        this.mNotify = CameraNotificationManager.getInstance();
    }

    public int getPrepareStatus() {
        int i;
        synchronized (this.lockObject) {
            i = this.prepareStatus;
        }
        return i;
    }

    public int getPrepareStatusWithoutSync() {
        return this.prepareStatus;
    }

    public void init() {
        this.mRecMode = getRecordingModeFromBackup();
        if (this.mHandlerFromMain == null) {
            this.mHandlerFromMain = new Handler(getLooper());
        }
        this.mHandlerFromMain.post(this.mInitRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class InitRunnable implements Runnable {
        private InitRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (ExecutorCreator.this.lockObject) {
                if (100 == ExecutorCreator.this.prepareStatus) {
                    int unused = ExecutorCreator.mStableFlg = 0;
                    CameraEx.OpenOptions opts = new CameraEx.OpenOptions();
                    opts.setPreview(ExecutorCreator.this.isImmediatelyEEStart());
                    opts.setInheritSetting(ExecutorCreator.this.isInheritSetting());
                    String[] media = ExecutorCreator.getMediaId();
                    BaseShootingExecutor.sCurrentMedia = media[0];
                    if (Environment.isMovieAPISupported()) {
                        int recmode = ExecutorCreator.this.getRecordingMode();
                        StringBuilder builder = ExecutorCreator.STRBUILDS.get();
                        if (recmode == 1) {
                            opts.setRecordingMode(0);
                            opts.setTargetMedia(media[0]);
                            builder.replace(0, builder.length(), ExecutorCreator.LOG_INITIAL_RUNNABLE).append(ExecutorCreator.LOG_STILL);
                        } else if (recmode == 2) {
                            opts.setRecordingMode(1);
                            builder.replace(0, builder.length(), ExecutorCreator.LOG_INITIAL_RUNNABLE).append(ExecutorCreator.LOG_MOVIE);
                        } else if (recmode == 4) {
                            opts.setRecordingMode(0);
                            opts.setTargetMedia(media[0]);
                            builder.replace(0, builder.length(), ExecutorCreator.LOG_INITIAL_RUNNABLE).append(ExecutorCreator.LOG_INTERVAL);
                        } else if (recmode == 8) {
                            opts.setRecordingMode(1);
                            builder.replace(0, builder.length(), ExecutorCreator.LOG_INITIAL_RUNNABLE).append(ExecutorCreator.LOG_LOOPREC);
                        } else {
                            builder.replace(0, builder.length(), ExecutorCreator.LOG_INITIAL_RUNNABLE).append(ExecutorCreator.LOG_UNSTABLE);
                        }
                        Log.i(ExecutorCreator.TAG, builder.toString());
                    } else {
                        opts.setTargetMedia(media[0]);
                    }
                    PTag.startTimeTag("CameraEx.open");
                    CameraEx cameraEx = CameraEx.open(0, opts);
                    PTag.endTimeTag("CameraEx.open");
                    PTag.end(ExecutorCreator.PTAG_OPENED_CAMERA);
                    ExecutorCreator.this.mCameraEx = cameraEx;
                    if (Environment.isMovieAPISupported()) {
                        ExecutorCreator.this.mMediaRecorder = new MediaRecorder();
                        ExecutorCreator.this.mAudioManager = AudioSetting.getInstance().getAudioManager();
                        if (ExecutorCreator.this.isCameraModeMovie()) {
                            ExecutorCreator.this.mMediaRecorder.setVideoSource(1);
                            ExecutorCreator.this.mMediaRecorder.setVideoSource(1);
                            ExecutorCreator.this.mMediaRecorder.setOutputMedia(AvindexStore.getExternalMediaIds()[0]);
                            ExecutorCreator.this.mMediaRecorder.setCamera(ExecutorCreator.this.mCameraEx);
                            MovieShootingExecutor.prepareForMovieRec(ExecutorCreator.this.mMediaRecorder, ExecutorCreator.this.mAudioManager);
                            ExecutorCreator.this.mMediaRecorder.prepare();
                            if (ExecutorCreator.isLoopRecEnable()) {
                                if (8 == ExecutorCreator.this.getRecordingMode()) {
                                    ExecutorCreator.this.mMediaRecorder.setLoopRecMode(true);
                                } else {
                                    ExecutorCreator.this.mMediaRecorder.setLoopRecMode(false);
                                }
                            }
                        }
                    }
                    if (ExecutorCreator.isIntervalRecEnable()) {
                        if (4 == ExecutorCreator.this.getRecordingMode()) {
                            ExecutorCreator.this.mCameraEx.setIntervalRecMode(true);
                        } else {
                            ExecutorCreator.this.mCameraEx.setIntervalRecMode(false);
                        }
                    }
                    ExecutorCreator.this.mIsRecModeChanging = false;
                    ExecutorCreator.this.mCamSetting.setCamera(ExecutorCreator.this.mCameraEx);
                    if (Environment.isMovieAPISupported()) {
                        ExecutorCreator.this.mCamSetting.setMediaRecorder(ExecutorCreator.this.mMediaRecorder);
                        AudioSetting.getInstance().onCameraOpened();
                    }
                    ExecutorCreator.this.canEnableExecutorWithBooting();
                    ExecutorCreator.this.settingBeforeInitialized();
                    ExecutorCreator.this.mStableReleasedListener.onReleased();
                    ExecutorCreator.this.prepareStatus = 101;
                    ExecutorCreator.this.mNotify.requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
                    ExecutorCreator.this.lockObject.notifyAll();
                }
            }
        }
    }

    @Deprecated
    protected boolean canEnableExecutorWithBooting() {
        return false;
    }

    protected void settingBeforeInitialized() {
    }

    public boolean isUpdated() {
        return this.mUpdated;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void terminate() {
        Log.i(TAG, LOG_TERMINATE);
        if (Environment.isMovieAPISupported()) {
            if (this.mMediaRecorder != null) {
                CameraSetting.getInstance().removeMediaRecorder();
            }
            AudioSetting.getInstance().onCameraRemoving();
        }
        CameraSetting.getInstance().removeCamera();
        Camera camera = this.mCameraEx.getNormalCamera();
        camera.release();
        this.mCameraEx.release();
        this.mExecutor = null;
        this.mCameraEx = null;
        this.mHandlerFromMain.removeCallbacksAndMessages(null);
        this.mHandlerToMain.removeCallbacksAndMessages(null);
        this.mMyOpenDoneRunnable = null;
        this.mUpdated = false;
        if (Environment.isMovieAPISupported()) {
            if (this.mMediaRecorder != null) {
                this.mMediaRecorder.release();
                this.mMediaRecorder = null;
            }
            AudioSetting.getInstance().onCameraRemoved();
            this.mAudioManager = null;
        }
    }

    public void halt() {
        synchronized (this.lockObject) {
            while (this.prepareStatus != 101) {
                try {
                    this.lockObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.mHandlerFromMain.post(this.mHaltRunnable);
            while (this.prepareStatus != 102) {
                try {
                    this.lockObject.wait();
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
            this.prepareStatus = 100;
        }
        Log.i(TAG, LOG_STOP);
    }

    /* loaded from: classes.dex */
    private class HaltRunnable implements Runnable {
        private HaltRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ExecutorCreator.this.mExecutor.release(ExecutorCreator.this.mStoppedListener);
        }
    }

    /* loaded from: classes.dex */
    private class StoppedListener implements BaseShootingExecutor.ReleasedListener {
        private StoppedListener() {
        }

        @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor.ReleasedListener
        public void onReleased() {
            ExecutorCreator.this.terminate();
            synchronized (ExecutorCreator.this.lockObject) {
                ExecutorCreator.this.prepareStatus = 102;
                ExecutorCreator.this.lockObject.notifyAll();
            }
        }
    }

    public void setMessageReceiver(State state) {
        this.mState = state;
        if (this.mExecutor != null) {
            this.mExecutor.state = state;
        }
    }

    public BaseShootingExecutor getSequence() {
        synchronized (this.lockObject) {
            if (!equals(Thread.currentThread())) {
                while (101 != this.prepareStatus) {
                    if (100 == this.prepareStatus) {
                        init();
                    }
                    try {
                        this.lockObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (100 == this.prepareStatus) {
                this.mInitRunnable.run();
            } else if (103 == this.prepareStatus) {
            }
        }
        return this.mExecutor;
    }

    public int getSupportingRecMode() {
        return this.mSupportingRecMode;
    }

    public void setSupportingRecMode(int mode) {
        this.mSupportingRecMode = mode;
    }

    public boolean isMovieModeSupported() {
        return (getSupportingRecMode() & 2) != 0;
    }

    public boolean isStillModeSupported() {
        return (getSupportingRecMode() & 1) != 0;
    }

    public boolean isIntervalRecModeSupported() {
        return (getSupportingRecMode() & 4) != 0;
    }

    public boolean isLoopRecModeSupported() {
        return (getSupportingRecMode() & 8) != 0;
    }

    public boolean isRecordingModeChanging() {
        return this.mIsRecModeChanging;
    }

    public int getRecordingMode() {
        return this.mRecMode;
    }

    public boolean isCameraModeStill() {
        return (getRecordingMode() & 5) != 0;
    }

    public boolean isCameraModeMovie() {
        return (getRecordingMode() & 10) != 0;
    }

    public int getRecordingModeFromBackup() {
        String recMode = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_RECORDING_MODE, "");
        if ("".equals(recMode)) {
            boolean isMovieDefault = ExposureModeController.isUiMovieMainFeature() || 3 == ScalarProperties.getInt("model.category");
            recMode = isMovieDefault ? BaseBackUpKey.MODE_MOVIE : BaseBackUpKey.MODE_STILL;
        }
        if (BaseBackUpKey.MODE_MOVIE.equals(recMode)) {
            return 2;
        }
        if (BaseBackUpKey.MODE_INTERVAL.equals(recMode)) {
            return 4;
        }
        if (!BaseBackUpKey.MODE_LOOPREC.equals(recMode)) {
            return 1;
        }
        return 8;
    }

    public void setRecordingModeToBackup(int mode) {
        String recMode = BaseBackUpKey.MODE_STILL;
        if (2 == mode) {
            recMode = BaseBackUpKey.MODE_MOVIE;
        } else if (4 == mode) {
            recMode = BaseBackUpKey.MODE_INTERVAL;
        } else if (8 == mode) {
            recMode = BaseBackUpKey.MODE_LOOPREC;
        }
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_RECORDING_MODE, recMode);
    }

    public void setRecordingMode(int recmode, CameraEx.OpenCallback callback) {
        String msg = LOG_SETRECORDINGMODE + recmode;
        Log.i(TAG, msg);
        PTag.startTimeTag(msg);
        synchronized (this.lockObject) {
            while (this.prepareStatus == 103) {
                PTag.startTimeTag("wait recmode changing");
                try {
                    this.lockObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PTag.endTimeTag("wait recmode changing");
            }
            if (this.mMyOpenDoneRunnable != null) {
                this.mHandlerToMain.removeCallbacks(this.mMyOpenDoneRunnable);
                this.mMyOpenDoneRunnable.run();
                this.mMyOpenDoneRunnable = null;
            }
            int currentRecMode = getRecordingMode();
            this.mRecMode = recmode;
            setRecordingModeToBackup(recmode);
            if (this.prepareStatus == 101) {
                if (recmode == currentRecMode) {
                    if (callback != null) {
                        callback.onReopened(this.mCameraEx);
                    }
                    return;
                }
                this.prepareStatus = 103;
                this.mCamSetting.reopeningCamera(CameraSetting.convertExecutorDef2CamSetDef(recmode));
                AudioSetting.getInstance().onCameraReopening();
                this.mIsRecModeChanging = true;
                this.mNotify.requestNotify(CameraNotificationManager.REC_MODE_CHANGING);
                if (5 == (currentRecMode | recmode)) {
                    Runnable runnable = new MyStillIntervalRecSequenceUpdateRunnable(callback);
                    this.mHandlerFromMain.post(runnable);
                } else if (10 == (currentRecMode | recmode)) {
                    Runnable runnable2 = new MyMovieLoopRecSequenceUpdateRunnable(callback);
                    this.mHandlerFromMain.post(runnable2);
                } else {
                    reopen(callback);
                }
            }
        }
    }

    protected void reopen(CameraEx.OpenCallback openCallback) {
        this.mOpenCallbackEC = openCallback;
        Runnable runnable = new MyChangeRecModeRunnable();
        this.mHandlerFromMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MyChangeRecModeRunnable implements Runnable {
        private ReopenUpdateReleasedListener mReopenUpdateReleasedListener = new ReopenUpdateReleasedListener();

        public MyChangeRecModeRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.i(ExecutorCreator.TAG, ExecutorCreator.LOG_MY_CHANGE_REC_MODE_RUNNABLE);
            synchronized (ExecutorCreator.this.lockObject) {
                reopenUpdateSequence();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void reopenUpdateSequence() {
            if (ExecutorCreator.this._myUpdateSequence(this.mReopenUpdateReleasedListener)) {
                CameraEx.OpenOptions options = new CameraEx.OpenOptions();
                options.setPreview(ExecutorCreator.this.isImmediatelyEEStart());
                options.setInheritSetting(ExecutorCreator.this.isInheritSetting());
                String[] media = ExecutorCreator.getMediaId();
                BaseShootingExecutor.sCurrentMedia = media[0];
                if (ExecutorCreator.this.isCameraModeStill()) {
                    ExecutorCreator.this.mMediaRecorder.reset();
                    options.setRecordingMode(0);
                    options.setTargetMedia(media[0]);
                } else if (ExecutorCreator.this.isCameraModeMovie()) {
                    options.setRecordingMode(1);
                }
                int recMode = ExecutorCreator.this.getRecordingMode();
                StringBuilder builder = ExecutorCreator.STRBUILDS.get();
                builder.replace(0, builder.length(), "CameraEx.reopen ").append(recMode);
                PTag.startTimeTag(builder.toString());
                ExecutorCreator.this.mCameraEx.reopen(options, new MyOpenCallback());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class ReopenUpdateReleasedListener implements BaseShootingExecutor.ReleasedListener {
            private ReopenUpdateReleasedListener() {
            }

            @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor.ReleasedListener
            public void onReleased() {
                ExecutorCreator.this.mExecutor = null;
                MyChangeRecModeRunnable.this.reopenUpdateSequence();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MyOpenCallback implements CameraEx.OpenCallback {
        private MyOpenCallback() {
        }

        public void onReopened(CameraEx cameraEx) {
            PTag.endTimeTag("CameraEx.reopen");
            synchronized (ExecutorCreator.this.lockObject) {
                ExecutorCreator.this.mCameraEx = cameraEx;
                int recmode = ExecutorCreator.this.getRecordingMode();
                if (ExecutorCreator.this.isCameraModeStill()) {
                    Log.i(ExecutorCreator.TAG, ExecutorCreator.LOG_STILL_REC_PREPARED_RUNNABLE);
                    if (ExecutorCreator.isIntervalRecEnable()) {
                        if (4 == recmode) {
                            ExecutorCreator.this.mCameraEx.setIntervalRecMode(true);
                        } else {
                            ExecutorCreator.this.mCameraEx.setIntervalRecMode(false);
                        }
                    }
                    ExecutorCreator.this.mIsRecModeChanging = false;
                    ExecutorCreator.this.mCamSetting.onCameraReopened(CameraSetting.convertExecutorDef2CamSetDef(recmode));
                    AudioSetting.getInstance().onCameraReopened();
                    if (ExecutorCreator.mStableFlg == 0) {
                        ExecutorCreator.this.myUpdateSequence();
                    }
                    ExecutorCreator.this.prepareStatus = 101;
                    if (ExecutorCreator.this.mOpenCallbackEC != null) {
                        ExecutorCreator.this.mMyOpenDoneRunnable = new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator.MyOpenCallback.1
                            @Override // java.lang.Runnable
                            public void run() {
                                ExecutorCreator.this.mMyOpenDoneRunnable = null;
                                ExecutorCreator.this.mOpenCallbackEC.onReopened(ExecutorCreator.this.mCameraEx);
                            }
                        };
                        if (Environment.hasGraphicsConstraint()) {
                            ExecutorCreator.this.mHandlerToMain.post(ExecutorCreator.this.mMyOpenDoneRunnable);
                        } else {
                            ExecutorCreator.this.mHandlerToMain.postAtFrontOfQueue(ExecutorCreator.this.mMyOpenDoneRunnable);
                        }
                    }
                    ExecutorCreator.this.mNotify.requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
                    PTag.endTimeTag(ExecutorCreator.LOG_SETRECORDINGMODE);
                    ExecutorCreator.this.lockObject.notifyAll();
                } else if (ExecutorCreator.this.isCameraModeMovie()) {
                    Log.i(ExecutorCreator.TAG, ExecutorCreator.LOG_MOVIE_REC_PREPARED_RUNNABLE);
                    ExecutorCreator.this.mMediaRecorder.setVideoSource(1);
                    ExecutorCreator.this.mMediaRecorder.setVideoSource(1);
                    ExecutorCreator.this.mMediaRecorder.setOutputMedia(AvindexStore.getExternalMediaIds()[0]);
                    ExecutorCreator.this.mMediaRecorder.setCamera(ExecutorCreator.this.mCameraEx);
                    ExecutorCreator.this.mMediaRecorder.setOnPreparedListener(new MyMovieRecPreparedListener());
                    MovieShootingExecutor.prepareForMovieRec(ExecutorCreator.this.mMediaRecorder, ExecutorCreator.this.mAudioManager);
                    ExecutorCreator.this.mMediaRecorder.prepareAsync();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private class MyMovieRecPreparedListener implements MediaRecorder.OnPreparedListener {
        private MyMovieRecPreparedListener() {
        }

        public void onPrepared(MediaRecorder mr) {
            synchronized (ExecutorCreator.this.lockObject) {
                Log.i(ExecutorCreator.TAG, ExecutorCreator.LOG_ON_PREPARED);
                if (ExecutorCreator.isLoopRecEnable()) {
                    if (8 == ExecutorCreator.this.getRecordingMode()) {
                        ExecutorCreator.this.mMediaRecorder.setLoopRecMode(true);
                    } else {
                        ExecutorCreator.this.mMediaRecorder.setLoopRecMode(false);
                    }
                }
                ExecutorCreator.this.mMediaRecorder = mr;
                ExecutorCreator.this.mIsRecModeChanging = false;
                int recmode = ExecutorCreator.this.getRecordingMode();
                ExecutorCreator.this.mCamSetting.onCameraReopened(CameraSetting.convertExecutorDef2CamSetDef(recmode));
                AudioSetting.getInstance().onCameraReopened();
                ExecutorCreator.this.myUpdateSequence();
                ExecutorCreator.this.prepareStatus = 101;
                if (ExecutorCreator.this.mOpenCallbackEC != null) {
                    ExecutorCreator.this.mMyOpenDoneRunnable = new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator.MyMovieRecPreparedListener.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ExecutorCreator.this.mMyOpenDoneRunnable = null;
                            ExecutorCreator.this.mOpenCallbackEC.onReopened(ExecutorCreator.this.mCameraEx);
                        }
                    };
                    if (Environment.hasGraphicsConstraint()) {
                        ExecutorCreator.this.mHandlerToMain.post(ExecutorCreator.this.mMyOpenDoneRunnable);
                    } else {
                        ExecutorCreator.this.mHandlerToMain.postAtFrontOfQueue(ExecutorCreator.this.mMyOpenDoneRunnable);
                    }
                }
                ExecutorCreator.this.mNotify.requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
                PTag.end(ExecutorCreator.LOG_SETRECORDINGMODE);
                PTag.endTimeTag(ExecutorCreator.LOG_SETRECORDINGMODE);
                ExecutorCreator.this.lockObject.notifyAll();
            }
        }
    }

    /* loaded from: classes.dex */
    private class MyStillIntervalRecSequenceUpdateRunnable implements Runnable {
        private CameraEx.OpenCallback mCallback;

        public MyStillIntervalRecSequenceUpdateRunnable(CameraEx.OpenCallback callback) {
            this.mCallback = callback;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (ExecutorCreator.this.lockObject) {
                Log.i(ExecutorCreator.TAG, ExecutorCreator.LOG_STILL_INTERVAL_REC_SEQ_UPDATE_RUNNABLE);
                if (ExecutorCreator.isIntervalRecEnable()) {
                    if (4 == ExecutorCreator.this.getRecordingMode()) {
                        ExecutorCreator.this.mCameraEx.setIntervalRecMode(true);
                    } else {
                        ExecutorCreator.this.mCameraEx.setIntervalRecMode(false);
                    }
                }
                ExecutorCreator.this.mIsRecModeChanging = false;
                ExecutorCreator.this.mCamSetting.onCameraReopened(CameraSetting.convertExecutorDef2CamSetDef(ExecutorCreator.this.getRecordingMode()));
                AudioSetting.getInstance().onCameraReopened();
                if (ExecutorCreator.mStableFlg == 0) {
                    ExecutorCreator.this.myUpdateSequence();
                }
                ExecutorCreator.this.prepareStatus = 101;
                if (this.mCallback != null && ExecutorCreator.this.mCameraEx != null) {
                    ExecutorCreator.this.mHandlerToMain.post(new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator.MyStillIntervalRecSequenceUpdateRunnable.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MyStillIntervalRecSequenceUpdateRunnable.this.mCallback.onReopened(ExecutorCreator.this.mCameraEx);
                        }
                    });
                }
                ExecutorCreator.this.mNotify.requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
                PTag.end(ExecutorCreator.LOG_SETRECORDINGMODE);
                PTag.endTimeTag(ExecutorCreator.LOG_SETRECORDINGMODE);
                ExecutorCreator.this.lockObject.notifyAll();
            }
        }
    }

    /* loaded from: classes.dex */
    private class MyMovieLoopRecSequenceUpdateRunnable implements Runnable {
        private CameraEx.OpenCallback mCallback;

        public MyMovieLoopRecSequenceUpdateRunnable(CameraEx.OpenCallback callback) {
            this.mCallback = callback;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (ExecutorCreator.this.lockObject) {
                Log.i(ExecutorCreator.TAG, ExecutorCreator.LOG_MOVIE_LOOP_REC_SEQ_UPDATE_RUNNABLE);
                if (ExecutorCreator.isLoopRecEnable()) {
                    if (8 == ExecutorCreator.this.getRecordingMode()) {
                        ExecutorCreator.this.mMediaRecorder.setLoopRecMode(true);
                    } else {
                        ExecutorCreator.this.mMediaRecorder.setLoopRecMode(false);
                    }
                }
                ExecutorCreator.this.mIsRecModeChanging = false;
                ExecutorCreator.this.mCamSetting.onCameraReopened(CameraSetting.convertExecutorDef2CamSetDef(ExecutorCreator.this.getRecordingMode()));
                AudioSetting.getInstance().onCameraReopened();
                if (ExecutorCreator.mStableFlg == 0) {
                    ExecutorCreator.this.myUpdateSequence();
                }
                ExecutorCreator.this.prepareStatus = 101;
                if (this.mCallback != null && ExecutorCreator.this.mCameraEx != null) {
                    ExecutorCreator.this.mHandlerToMain.post(new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator.MyMovieLoopRecSequenceUpdateRunnable.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MyMovieLoopRecSequenceUpdateRunnable.this.mCallback.onReopened(ExecutorCreator.this.mCameraEx);
                        }
                    });
                }
                ExecutorCreator.this.mNotify.requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
                PTag.endTimeTag(ExecutorCreator.LOG_SETRECORDINGMODE);
                ExecutorCreator.this.lockObject.notifyAll();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseShootingExecutor getExecutor(Class<?> clazz) {
        if (3 == Environment.DEVICE_TYPE) {
            if (SpinalExecutor.class.equals(clazz)) {
                if (this.mSpinalExecutor == null) {
                    this.mSpinalExecutor = new SpinalExecutor();
                }
                BaseShootingExecutor executor = this.mSpinalExecutor;
                return executor;
            }
            if (NormalExecutor.class.equals(clazz)) {
                if (this.mNormalExecutor == null) {
                    this.mNormalExecutor = new NormalExecutor();
                }
                BaseShootingExecutor executor2 = this.mNormalExecutor;
                return executor2;
            }
            if (StableExecutor.class.equals(clazz)) {
                if (this.mStableExecutor == null) {
                    this.mStableExecutor = new StableExecutor();
                }
                BaseShootingExecutor executor3 = this.mStableExecutor;
                return executor3;
            }
            if (MovieShootingExecutor.class.equals(clazz)) {
                if (this.mMovieShootingExecutor == null) {
                    this.mMovieShootingExecutor = new MovieShootingExecutor();
                }
                BaseShootingExecutor executor4 = this.mMovieShootingExecutor;
                return executor4;
            }
            if (MovieStableExecutor.class.equals(clazz)) {
                if (this.mMovieStableExecutor == null) {
                    this.mMovieStableExecutor = new MovieStableExecutor();
                }
                BaseShootingExecutor executor5 = this.mMovieStableExecutor;
                return executor5;
            }
            if (IntervalRecExecutor.class.equals(clazz)) {
                if (isIntervalRecEnable()) {
                    if (this.mIntervalRecExecutor == null) {
                        this.mIntervalRecExecutor = new IntervalRecExecutor();
                    }
                    BaseShootingExecutor executor6 = this.mIntervalRecExecutor;
                    return executor6;
                }
                BaseShootingExecutor executor7 = new StableExecutor();
                return executor7;
            }
            if (!BaseShootingExecutor.class.isAssignableFrom(clazz)) {
                return null;
            }
            Object obj = null;
            try {
                obj = clazz.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e2) {
                e2.printStackTrace();
            }
            BaseShootingExecutor executor8 = (BaseShootingExecutor) obj;
            return executor8;
        }
        BaseShootingExecutor executor9 = new StubExecutor();
        return executor9;
    }

    protected IAdapter getAdapter(String name) {
        Camera camera = this.mCameraEx.getNormalCamera();
        if (isCameraModeMovie()) {
            return null;
        }
        if (4 == getRecordingMode()) {
            if (!isIntervalRecEnable() || isSpinal() || isSpecial()) {
                return null;
            }
            IAdapter adapter = new IntervalRecAdapterImpl(camera, this.mCameraEx);
            return adapter;
        }
        if (!isSpinal()) {
            if (isSpecial()) {
                IAdapter adapter2 = new CaptureImagingAdapterImpl(camera, this.mCameraEx);
                return adapter2;
            }
            IAdapter adapter3 = new CaptureAdapterImpl(camera, this.mCameraEx);
            return adapter3;
        }
        if (!isSpecial()) {
            return null;
        }
        IAdapter adapter4 = new ImagingAdapterImpl(camera, this.mCameraEx);
        return adapter4;
    }

    protected BaseShootingExecutor setup(BaseShootingExecutor executor, IAdapter adapter, IProcess process) {
        StringBuilder builder = STRBUILDS.get();
        builder.replace(0, builder.length(), LOG_CAMERA).append("setup").append(" ").append(LOG_EXECUTOR).append(executor).append(": ").append(LOG_ADAPTER).append(adapter).append(": ").append(LOG_PROCESS).append(process).append(": ");
        Log.i(TAG, builder.toString());
        this.mExecutor = executor;
        this.mAdapter = adapter;
        this.mProcess = process;
        if (adapter != null && process != null) {
            adapter.setProcess(process);
        }
        executor.state = this.mState;
        executor.setAdapter(adapter);
        executor.prepare(this.mCameraEx.getNormalCamera(), this.mCameraEx);
        BaseShootingExecutor.setHandler(this.mHandlerFromMain, this.mHandlerToMain);
        PTag.end(PTAG_STARTED_DIRECT_SHUTTER);
        return executor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseUpdateLock(boolean updated) {
        synchronized (this.mUpdateLock) {
            this.mUpdated = updated;
            this.mUpdateLock.notifyAll();
        }
    }

    public void updateSequence() {
        updateSequence(1);
    }

    public void updateSequence(int flg) {
        synchronized (this.lockObject) {
            while (101 != this.prepareStatus) {
                if (100 == this.prepareStatus) {
                    init();
                }
                try {
                    this.lockObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synchronized (this.mUpdateLock) {
            StringBuilder builder = STRBUILDS.get();
            builder.replace(0, builder.length(), LOG_UPDATE_SEQUENCE).append(" ").append(flg);
            Log.i(TAG, builder.toString());
            this.mHandlerFromMain.post(new UpdateSequenceRunnable(flg));
            try {
                this.mUpdateLock.wait(100000L);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class UpdateSequenceRunnable implements Runnable {
        private int mFlg;

        public UpdateSequenceRunnable(int flg) {
            this.mFlg = flg;
        }

        @Override // java.lang.Runnable
        public void run() {
            ExecutorCreator.access$372(this.mFlg ^ (-1));
            StringBuilder builder = ExecutorCreator.STRBUILDS.get();
            builder.replace(0, builder.length(), ExecutorCreator.LOG_UPDATE_SEQUENCE).append(" ").append(this.mFlg).append(" ").append(ExecutorCreator.mStableFlg);
            Log.i(ExecutorCreator.TAG, builder.toString());
            if (ExecutorCreator.mStableFlg == 0) {
                ExecutorCreator.this.myUpdateSequence();
            }
            ExecutorCreator.this.releaseUpdateLock(true);
        }
    }

    protected Class<?> getNextExecutor() {
        Class<?> clazz = null;
        if (getRecordingMode() == 1) {
            if (this.mIsRecModeChanging) {
                clazz = StableExecutor.class;
            } else if (MediaNotificationManager.getInstance().isError()) {
                clazz = StableExecutor.class;
            } else if (!isSpinal()) {
                clazz = NormalExecutor.class;
            } else {
                clazz = SpinalExecutor.class;
            }
        } else if (4 == getRecordingMode()) {
            if (isIntervalRecEnable()) {
                if (this.mIsRecModeChanging) {
                    clazz = StableExecutor.class;
                } else if (!isSpinal()) {
                    clazz = IntervalRecExecutor.class;
                } else {
                    clazz = SpinalExecutor.class;
                }
            }
        } else if (this.mIsRecModeChanging) {
            clazz = MovieStableExecutor.class;
        } else {
            clazz = MovieShootingExecutor.class;
        }
        StringBuilder builder = STRBUILDS.get();
        builder.replace(0, builder.length(), LOG_CAMERA).append(LOG_GET_NEXT_EXECUTOR).append(": ").append(clazz);
        Log.i(TAG, builder.toString());
        return clazz;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean _myUpdateSequence(BaseShootingExecutor.ReleasedListener listener) {
        Log.i(TAG, LOG_MY_UPDATE_SEQUENCE);
        Class<?> clazz = getNextExecutor();
        if (this.mExecutor != null) {
            Log.d(TAG, this.mExecutor.getClass().getSimpleName() + " : " + clazz.getSimpleName());
            if (!this.mExecutor.getClass().equals(clazz)) {
                this.mExecutor.release(listener);
                return false;
            }
        }
        BaseShootingExecutor executor = getExecutor(clazz);
        IAdapter adapter = null;
        IProcess process = null;
        if (isCameraModeStill()) {
            adapter = getAdapter(null);
            process = getProcess(this.mCameraEx.getNormalCamera(), this.mCameraEx);
            if (!this.mIsRecModeChanging && this.mBaseExclusiveSetting != null) {
                this.mBaseExclusiveSetting.setCameraSetting(this.mCamSetting);
                this.mBaseExclusiveSetting.executeExlusiveSetting(isSpecial());
            }
        }
        setup(executor, adapter, process);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void myUpdateSequence() {
        _myUpdateSequence(this.mUpdateReleasedListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class UpdateReleasedListener implements BaseShootingExecutor.ReleasedListener {
        private UpdateReleasedListener() {
        }

        @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor.ReleasedListener
        public void onReleased() {
            ExecutorCreator.this.mExecutor = null;
            ExecutorCreator.this.myUpdateSequence();
        }
    }

    public boolean reupdateSequence() {
        if (101 != this.prepareStatus && 103 != this.prepareStatus) {
            return false;
        }
        stableSequence();
        updateSequence();
        return true;
    }

    public void stableSequence() {
        stableSequence(1);
    }

    public void stableSequence(BaseShootingExecutor.ReleasedListener listener) {
        stableSequence(listener, 1);
    }

    public void stableSequence(BaseShootingExecutor.ReleasedListener listener, int flg) {
        stableSequence(flg);
        if (listener != null) {
            listener.onReleased();
        }
    }

    public void stableSequence(int flg) {
        synchronized (this.mUpdateLock) {
            StringBuilder builder = STRBUILDS.get();
            builder.replace(0, builder.length(), LOG_STABLE_SEQUENCE).append(" ").append(flg);
            Log.i(TAG, builder.toString());
            if (this.mExecutor != null && this.mExecutor.needToBeStable(flg)) {
                this.mHandlerFromMain.post(new StableSequenceRunnable(flg));
                try {
                    this.mUpdateLock.wait(100000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                builder.replace(0, builder.length(), LOG_STABLE_SEQUENCE_NEEDLESS).append(LOG_EXECUTOR).append(" ").append(this.mExecutor).append(": ").append(flg);
                Log.i(TAG, builder.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class StableSequenceRunnable implements Runnable {
        private int mFlg;

        public StableSequenceRunnable(int flg) {
            this.mFlg = flg;
        }

        @Override // java.lang.Runnable
        public void run() {
            int oldFlg = ExecutorCreator.mStableFlg;
            ExecutorCreator.access$376(this.mFlg);
            StringBuilder builder = ExecutorCreator.STRBUILDS.get();
            builder.replace(0, builder.length(), ExecutorCreator.LOG_STABLE_SEQUENCE).append(" ").append(this.mFlg).append(" ").append(ExecutorCreator.mStableFlg);
            Log.i(ExecutorCreator.TAG, builder.toString());
            Class<? extends BaseShootingExecutor> clazz = ExecutorCreator.this.getStableConditionExecutor();
            if (oldFlg != 0 || ExecutorCreator.this.mExecutor.getClass() == clazz) {
                ExecutorCreator.this.releaseUpdateLock(false);
                return;
            }
            if (101 != ExecutorCreator.this.prepareStatus) {
                ExecutorCreator.this.releaseUpdateLock(false);
            }
            ExecutorCreator.this.mExecutor.release(ExecutorCreator.this.mStableReleasedListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class StableReleasedListener implements BaseShootingExecutor.ReleasedListener {
        private StableReleasedListener() {
        }

        @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor.ReleasedListener
        public void onReleased() {
            Log.i(ExecutorCreator.TAG, ExecutorCreator.LOG_ON_RELEASED);
            Class<? extends BaseShootingExecutor> clazz = ExecutorCreator.this.getStableConditionExecutor();
            BaseShootingExecutor executor = ExecutorCreator.this.getExecutor(clazz);
            ExecutorCreator.this.setup(executor, null, null);
            ExecutorCreator.this.releaseUpdateLock(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Class<? extends BaseShootingExecutor> getStableConditionExecutor() {
        if (isCameraModeMovie()) {
            if (101 != this.prepareStatus) {
                return MovieStableExecutor.class;
            }
            return getNextExecutor();
        }
        return StableExecutor.class;
    }

    public boolean isAssistApp() {
        return isInheritSetting();
    }

    public static String[] getMediaId() {
        if (MediaNotificationManager.getInstance().isNoCard()) {
            String[] ids = AvindexStore.getVirtualMediaIds();
            return ids;
        }
        String[] ids2 = AvindexStore.getExternalMediaIds();
        return ids2;
    }

    protected boolean isSpecial() {
        return false;
    }

    public void setExclusiveSetting(BaseExclusiveSetting excClass) {
        if (excClass != null) {
            this.mBaseExclusiveSetting = excClass;
        } else {
            this.mBaseExclusiveSetting = null;
            this.mBaseExclusiveSetting = new BaseExclusiveSetting();
        }
        updateSequence();
    }

    public boolean isSpinalZoom() {
        if (2 <= Environment.getVersionPfAPI()) {
            return isSpinalZoomSetting();
        }
        return true;
    }

    public boolean isEnableDigitalZoom() {
        if (1 <= Environment.getVersionPfAPI()) {
            return isInheritDigitalZoomSetting();
        }
        return true;
    }

    public boolean isAElockedOnAutoFocus() {
        return true;
    }

    public boolean isBulbEnabled() {
        return false;
    }

    public void waitChangingRecMode() {
        synchronized (this.lockObject) {
            if (!equals(Thread.currentThread())) {
                while (103 == this.prepareStatus) {
                    try {
                        this.lockObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.w(TAG, "Can NOT wait in Camera Thread");
            }
        }
    }

    public static boolean isIntervalRecEnable() {
        if (!Environment.isIntervalRecAPISupported() || 1 != ScalarProperties.getInt("ui.intervalrec.supported")) {
            return false;
        }
        return true;
    }

    public static boolean isLoopRecEnable() {
        if (!Environment.isLoopRecAPISupported() || 1 != ScalarProperties.getInt("ui.looprec.supported")) {
            return false;
        }
        return true;
    }

    public String[] getCautionMedia(int mediaStatus, int mediaType) {
        if (2 == mediaStatus || mediaStatus == 0) {
            BaseShootingExecutor executor = getSequence();
            String[] m = {executor.getRecordingMedia()};
            if (4 == getRecordingMode()) {
                return AvindexStore.getExternalMediaIds();
            }
            return m;
        }
        if (2 == mediaType || 1 == mediaType) {
            return AvindexStore.getExternalMediaIds();
        }
        return AvindexStore.getVirtualMediaIds();
    }
}
