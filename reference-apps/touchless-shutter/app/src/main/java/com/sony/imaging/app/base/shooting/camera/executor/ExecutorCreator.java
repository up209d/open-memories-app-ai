package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.common.AudioSetting;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.LogHelper;
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
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public abstract class ExecutorCreator extends HandlerThread {
    private static final int FINALIZED = 102;
    private static final int INITIALIZED = 101;
    private static final String LOG_ADAPTER = "adapter";
    protected static final String LOG_CAMERA = "CAMERA :";
    private static final String LOG_COLON = ": ";
    private static final String LOG_EXECUTOR = "executor";
    private static final String LOG_GET_NEXT_EXECUTOR = "getNextExecutor";
    private static final String LOG_INITIAL_RUNNABLE = "Initial Runnable: ";
    private static final String LOG_MOVIE = "REC_MODE_MOVIE";
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
    private static final String LOG_STILL_REC_PREPARED_RUNNABLE = "StillRecPreparedRunnable";
    private static final String LOG_STOP = "stop";
    private static final String LOG_TERMINATE = "terminate";
    protected static final String LOG_UI = "UI :";
    private static final String LOG_UNSTABLE = "REC_MODE_UNSTABLE";
    private static final String LOG_UPDATE_SEQUENCE = "updateSequence : ";
    public static final int MEDIA_STABLE = 2;
    private static final String PTAG_OPENED_CAMERA = "displayed EE screen";
    private static final String PTAG_STARTED_DIRECT_SHUTTER = "DLApp Boot. called startDirectShutter";
    public static final int REC_MODE_BOTH = 3;
    public static final int REC_MODE_MOVIE = 2;
    public static final int REC_MODE_NONE = 0;
    public static final int REC_MODE_STILL = 1;
    public static final int REC_MODE_UNSTABLE = 255;
    private static final int REOPENING = 103;
    public static final int STANDARD_STABLE = 1;
    private static final String TAG = "ExecutorCreator";
    private static final int UNINITIALIZED = 100;
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
    protected volatile boolean mIsRecModeChanging;
    protected MediaRecorder mMediaRecorder;
    private MovieShootingExecutor mMovieShootingExecutor;
    private MovieStableExecutor mMovieStableExecutor;
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
    private int prepareStatus;
    private static ExecutorCreator sDummy = new DummyExecutorCreator();
    private static ExecutorCreator sCreator = null;
    private static final StringBuilder STRBUILD_UI = new StringBuilder();
    private static final StringBuilder STRBUILD_CAMERA = new StringBuilder();
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
        this.mSupportingRecMode = 255;
        this.mRecMode = 255;
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
        this.mMediaRecorder = null;
        this.mAudioManager = null;
        this.lockObject = new Object();
        this.prepareStatus = 100;
        this.mInitRunnable = new InitRunnable();
        this.mUpdated = false;
        this.mHaltRunnable = new HaltRunnable();
        this.mStoppedListener = new StoppedListener();
        this.mOpenCallbackEC = null;
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
                        if (recmode == 1) {
                            opts.setRecordingMode(0);
                            opts.setTargetMedia(media[0]);
                            ExecutorCreator.STRBUILD_CAMERA.replace(0, ExecutorCreator.STRBUILD_CAMERA.length(), ExecutorCreator.LOG_INITIAL_RUNNABLE).append(ExecutorCreator.LOG_STILL);
                        } else if (recmode != 2) {
                            ExecutorCreator.STRBUILD_CAMERA.replace(0, ExecutorCreator.STRBUILD_CAMERA.length(), ExecutorCreator.LOG_INITIAL_RUNNABLE).append(ExecutorCreator.LOG_UNSTABLE);
                        } else {
                            opts.setRecordingMode(1);
                            ExecutorCreator.STRBUILD_CAMERA.replace(0, ExecutorCreator.STRBUILD_CAMERA.length(), ExecutorCreator.LOG_INITIAL_RUNNABLE).append(ExecutorCreator.LOG_MOVIE);
                        }
                        Log.i(ExecutorCreator.TAG, ExecutorCreator.STRBUILD_CAMERA.toString());
                    } else {
                        opts.setTargetMedia(media[0]);
                    }
                    CameraEx cameraEx = CameraEx.open(0, opts);
                    PTag.end(ExecutorCreator.PTAG_OPENED_CAMERA);
                    ExecutorCreator.this.mCameraEx = cameraEx;
                    if (Environment.isMovieAPISupported()) {
                        ExecutorCreator.this.mMediaRecorder = new MediaRecorder();
                        ExecutorCreator.this.mAudioManager = AudioSetting.getInstance().getAudioManager();
                        if (ExecutorCreator.this.getRecordingMode() == 2) {
                            ExecutorCreator.this.mMediaRecorder.setVideoSource(1);
                            ExecutorCreator.this.mMediaRecorder.setVideoSource(1);
                            ExecutorCreator.this.mMediaRecorder.setOutputMedia(AvindexStore.getExternalMediaIds()[0]);
                            ExecutorCreator.this.mMediaRecorder.setCamera(ExecutorCreator.this.mCameraEx);
                            MovieShootingExecutor.prepareForMovieRec(ExecutorCreator.this.mMediaRecorder, ExecutorCreator.this.mAudioManager);
                            ExecutorCreator.this.mMediaRecorder.prepare();
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
                    ExecutorCreator.this.prepareStatus = ExecutorCreator.INITIALIZED;
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
            while (this.prepareStatus != INITIALIZED) {
                try {
                    this.lockObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.mHandlerFromMain.post(this.mHaltRunnable);
            while (this.prepareStatus != FINALIZED) {
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
                ExecutorCreator.this.prepareStatus = ExecutorCreator.FINALIZED;
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
                while (INITIALIZED != this.prepareStatus) {
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

    public boolean isRecordingModeChanging() {
        return this.mIsRecModeChanging;
    }

    public int getRecordingMode() {
        return this.mRecMode;
    }

    public int getRecordingModeFromBackup() {
        String recMode = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_RECORDING_MODE, "");
        if ("".equals(recMode)) {
            recMode = ExposureModeController.isMovieMainFeature() ? BaseBackUpKey.MODE_MOVIE : BaseBackUpKey.MODE_STILL;
        }
        return BaseBackUpKey.MODE_MOVIE.equals(recMode) ? 2 : 1;
    }

    public void setRecordingModeToBackup(int mode) {
        String recMode = 1 == mode ? BaseBackUpKey.MODE_STILL : BaseBackUpKey.MODE_MOVIE;
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_RECORDING_MODE, recMode);
    }

    public void setRecordingMode(int mode, CameraEx.OpenCallback callback) {
        Log.i(TAG, LOG_SETRECORDINGMODE + mode);
        synchronized (this.lockObject) {
            while (this.prepareStatus == 103) {
                try {
                    this.lockObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int currentMode = getRecordingMode();
            this.mRecMode = mode;
            setRecordingModeToBackup(mode);
            if (this.prepareStatus == INITIALIZED) {
                if (mode == currentMode) {
                    if (callback != null) {
                        callback.onReopened(this.mCameraEx);
                    }
                    return;
                }
                this.prepareStatus = 103;
                int cameraSettingMode = 255;
                if (mode == 1) {
                    cameraSettingMode = 1;
                } else if (mode == 2) {
                    cameraSettingMode = 2;
                }
                this.mCamSetting.reopeningCamera(cameraSettingMode);
                AudioSetting.getInstance().onCameraReopening();
                this.mIsRecModeChanging = true;
                this.mNotify.requestNotify(CameraNotificationManager.REC_MODE_CHANGING);
                reopen(callback);
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
                int recmode = ExecutorCreator.this.getRecordingMode();
                options.setPreview(ExecutorCreator.this.isImmediatelyEEStart());
                options.setInheritSetting(ExecutorCreator.this.isInheritSetting());
                String[] media = ExecutorCreator.getMediaId();
                BaseShootingExecutor.sCurrentMedia = media[0];
                if (1 == recmode) {
                    ExecutorCreator.this.mMediaRecorder.reset();
                    options.setRecordingMode(0);
                    options.setTargetMedia(media[0]);
                } else if (2 == recmode) {
                    options.setRecordingMode(1);
                }
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
            synchronized (ExecutorCreator.this.lockObject) {
                ExecutorCreator.this.mCameraEx = cameraEx;
                int recmode = ExecutorCreator.this.getRecordingMode();
                if (1 == recmode) {
                    ExecutorCreator.STRBUILD_CAMERA.replace(0, ExecutorCreator.STRBUILD_CAMERA.length(), ExecutorCreator.LOG_CAMERA).append(ExecutorCreator.LOG_STILL_REC_PREPARED_RUNNABLE);
                    Log.i(ExecutorCreator.TAG, ExecutorCreator.STRBUILD_CAMERA.toString());
                    ExecutorCreator.this.mIsRecModeChanging = false;
                    ExecutorCreator.this.mCamSetting.onCameraReopened(recmode);
                    AudioSetting.getInstance().onCameraReopened();
                    if (ExecutorCreator.mStableFlg == 0) {
                        ExecutorCreator.this.myUpdateSequence();
                    }
                    if (ExecutorCreator.this.mOpenCallbackEC != null) {
                        ExecutorCreator.this.mHandlerToMain.post(new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator.MyOpenCallback.1
                            @Override // java.lang.Runnable
                            public void run() {
                                ExecutorCreator.this.mOpenCallbackEC.onReopened(ExecutorCreator.this.mCameraEx);
                            }
                        });
                    }
                    ExecutorCreator.this.prepareStatus = ExecutorCreator.INITIALIZED;
                    ExecutorCreator.this.mNotify.requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
                    ExecutorCreator.this.lockObject.notifyAll();
                } else if (2 == recmode) {
                    ExecutorCreator.STRBUILD_CAMERA.replace(0, ExecutorCreator.STRBUILD_CAMERA.length(), ExecutorCreator.LOG_CAMERA).append(ExecutorCreator.LOG_MOVIE_REC_PREPARED_RUNNABLE);
                    Log.i(ExecutorCreator.TAG, ExecutorCreator.STRBUILD_CAMERA.toString());
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
                ExecutorCreator.STRBUILD_CAMERA.replace(0, ExecutorCreator.STRBUILD_CAMERA.length(), ExecutorCreator.LOG_CAMERA).append(ExecutorCreator.LOG_ON_PREPARED);
                Log.i(ExecutorCreator.TAG, ExecutorCreator.STRBUILD_CAMERA.toString());
                ExecutorCreator.this.mMediaRecorder = mr;
                ExecutorCreator.this.mIsRecModeChanging = false;
                int recmode = ExecutorCreator.this.getRecordingMode();
                ExecutorCreator.this.mCamSetting.onCameraReopened(recmode);
                AudioSetting.getInstance().onCameraReopened();
                ExecutorCreator.this.myUpdateSequence();
                if (ExecutorCreator.this.mOpenCallbackEC != null) {
                    ExecutorCreator.this.mHandlerToMain.post(new Runnable() { // from class: com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator.MyMovieRecPreparedListener.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ExecutorCreator.this.mOpenCallbackEC.onReopened(ExecutorCreator.this.mCameraEx);
                        }
                    });
                }
                ExecutorCreator.this.prepareStatus = ExecutorCreator.INITIALIZED;
                ExecutorCreator.this.mNotify.requestNotify(CameraNotificationManager.REC_MODE_CHANGED);
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
            BaseShootingExecutor executor6 = (BaseShootingExecutor) obj;
            return executor6;
        }
        BaseShootingExecutor executor7 = new StubExecutor();
        return executor7;
    }

    protected IAdapter getAdapter(String name) {
        Camera camera = this.mCameraEx.getNormalCamera();
        if (getRecordingMode() == 2) {
            return null;
        }
        if (!isSpinal()) {
            if (isSpecial()) {
                IAdapter adapter = new CaptureImagingAdapterImpl(camera, this.mCameraEx);
                return adapter;
            }
            IAdapter adapter2 = new CaptureAdapterImpl(camera, this.mCameraEx);
            return adapter2;
        }
        if (!isSpecial()) {
            return null;
        }
        IAdapter adapter3 = new ImagingAdapterImpl(camera, this.mCameraEx);
        return adapter3;
    }

    protected BaseShootingExecutor setup(BaseShootingExecutor executor, IAdapter adapter, IProcess process) {
        STRBUILD_CAMERA.replace(0, STRBUILD_CAMERA.length(), LOG_CAMERA).append(LOG_SET_UP).append(" ").append(LOG_EXECUTOR).append(executor).append(": ").append(LOG_ADAPTER).append(adapter).append(": ").append(LOG_PROCESS).append(process).append(": ");
        Log.i(TAG, STRBUILD_CAMERA.toString());
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
            while (INITIALIZED != this.prepareStatus) {
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
            STRBUILD_UI.replace(0, STRBUILD_UI.length(), LOG_UPDATE_SEQUENCE).append(" ").append(flg);
            Log.i(TAG, STRBUILD_UI.toString());
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
            ExecutorCreator.STRBUILD_CAMERA.replace(0, ExecutorCreator.STRBUILD_CAMERA.length(), ExecutorCreator.LOG_UPDATE_SEQUENCE).append(" ").append(this.mFlg).append(" ").append(ExecutorCreator.mStableFlg);
            Log.i(ExecutorCreator.TAG, ExecutorCreator.STRBUILD_CAMERA.toString());
            if (ExecutorCreator.mStableFlg == 0) {
                ExecutorCreator.this.myUpdateSequence();
            }
            ExecutorCreator.this.releaseUpdateLock(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> getNextExecutor() {
        Class<?> clazz;
        if (getRecordingMode() == 1) {
            if (this.mIsRecModeChanging) {
                clazz = StableExecutor.class;
            } else if (!isSpinal()) {
                clazz = NormalExecutor.class;
            } else {
                clazz = SpinalExecutor.class;
            }
        } else if (this.mIsRecModeChanging) {
            clazz = MovieStableExecutor.class;
        } else {
            clazz = MovieShootingExecutor.class;
        }
        STRBUILD_CAMERA.replace(0, STRBUILD_CAMERA.length(), LOG_CAMERA).append(LOG_GET_NEXT_EXECUTOR).append(": ").append(clazz);
        Log.i(TAG, STRBUILD_CAMERA.toString());
        return clazz;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean _myUpdateSequence(BaseShootingExecutor.ReleasedListener listener) {
        STRBUILD_CAMERA.replace(0, STRBUILD_CAMERA.length(), LOG_CAMERA).append(LOG_MY_UPDATE_SEQUENCE);
        Log.i(TAG, STRBUILD_CAMERA.toString());
        Class<?> clazz = getNextExecutor();
        if (this.mExecutor != null) {
            Log.d(TAG, this.mExecutor.getClass().getSimpleName() + LogHelper.MSG_COLON + clazz.getSimpleName());
            if (!this.mExecutor.getClass().equals(clazz)) {
                this.mExecutor.release(listener);
                return false;
            }
        }
        BaseShootingExecutor executor = getExecutor(clazz);
        IAdapter adapter = null;
        IProcess process = null;
        if (getRecordingMode() != 2) {
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
        if (INITIALIZED != this.prepareStatus && 103 != this.prepareStatus) {
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
            STRBUILD_UI.replace(0, STRBUILD_UI.length(), LOG_STABLE_SEQUENCE).append(" ").append(flg);
            Log.i(TAG, STRBUILD_UI.toString());
            if (this.mExecutor != null && this.mExecutor.needToBeStable(flg)) {
                this.mHandlerFromMain.post(new StableSequenceRunnable(flg));
                try {
                    this.mUpdateLock.wait(100000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                STRBUILD_UI.replace(0, STRBUILD_UI.length(), LOG_STABLE_SEQUENCE_NEEDLESS).append(LOG_EXECUTOR).append(" ").append(this.mExecutor).append(": ").append(flg);
                Log.i(TAG, STRBUILD_UI.toString());
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
            ExecutorCreator.STRBUILD_CAMERA.replace(0, ExecutorCreator.STRBUILD_CAMERA.length(), ExecutorCreator.LOG_STABLE_SEQUENCE).append(" ").append(this.mFlg).append(" ").append(ExecutorCreator.mStableFlg);
            Log.i(ExecutorCreator.TAG, ExecutorCreator.STRBUILD_CAMERA.toString());
            Class<? extends BaseShootingExecutor> clazz = ExecutorCreator.this.getStableConditionExecutor();
            if (oldFlg != 0 || ExecutorCreator.this.mExecutor.getClass() == clazz) {
                ExecutorCreator.this.releaseUpdateLock(false);
                return;
            }
            if (ExecutorCreator.INITIALIZED != ExecutorCreator.this.prepareStatus) {
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
        if (getRecordingMode() == 2) {
            if (INITIALIZED != this.prepareStatus) {
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
}
