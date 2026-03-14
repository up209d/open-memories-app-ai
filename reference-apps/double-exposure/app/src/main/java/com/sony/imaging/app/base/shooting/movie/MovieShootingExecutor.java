package com.sony.imaging.app.base.shooting.movie;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.IAdapter;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class MovieShootingExecutor extends BaseShootingExecutor {
    public static final int ERROR_FACTOR_UNKNOWN = -1001;
    protected static final String LOG_CHANGE_MEDIA_FOR_MOVIE_REC = "changeMediaForMovieRec";
    protected static final String LOG_MOVIE_REC_ERROR = "MovieRecError";
    protected static final String LOG_ON_MOVIE_REC_MEDIA_CHANGE = "MovieRecMediaChangeRunnable";
    protected static final String LOG_START_MOVIE_REC = "startMovieRec";
    protected static final String LOG_START_MOVIE_REC_EXECUTION_ERROR = "startMovieRecExecutionError";
    protected static final String LOG_START_MOVIE_REC_FAILED = "startMovieRecFailed";
    protected static final String LOG_START_MOVIE_REC_MODE = "startMovieRecMode";
    protected static final String LOG_START_MOVIE_REC_SUCCEEDED = "startMovieRecSucceeded";
    protected static final String LOG_START_STREAM_WRITE = "startStreamWrite";
    protected static final String LOG_STOP_MOVIE_REC = "stopMovieRecMode";
    protected static final String LOG_STOP_MOVIE_REC_EXECUTION_ERROR = "stopMovieRecExecutionError";
    protected static final String LOG_STOP_MOVIE_REC_FAILED = "stopMovieRecFailed";
    protected static final String LOG_STOP_MOVIE_REC_MODE = "stopMovieRecMode";
    protected static final String LOG_STOP_STREAM_WRITE = "stopStreamWrite";
    public static final int RECTIME_INIT_VALUE = -1000;
    private static final String TAG = "MovieShootingExecutor";
    protected static AudioManager sAudioManager;
    protected static int sError;
    private static MediaRecorder.OnErrorListener sOnErrorListener;
    private static MediaRecorder.OnRecRemainListener sOnRecRemainListener;
    private static MediaRecorder.OnRecTimeListener sOnRecTimeListener;
    private static MediaRecorder.OnStreamWriteListener sOnStreamWriteListener;
    private static MyRecordListener mMyRecordListener = new MyRecordListener();
    private static MyStreamWriteListener mMyStreamWriteListener = new MyStreamWriteListener();
    private static MyRecTimeListener mMyRecTimeListener = new MyRecTimeListener();
    private static MyRecRemainTimeListener mMyRecRemainTimeListener = new MyRecRemainTimeListener();
    private static MyErrorListener mMyErrorListener = new MyErrorListener();
    private static MediaRecorder.OnRecordListener sOnRecordListener = null;
    protected static String sExternalMedia = AvindexStore.getExternalMediaIds()[0];
    private static boolean sIsMovieRecording = false;
    protected static MediaRecorder sMediaRecorder = null;
    protected static int sRecTime = -1000;
    protected static int sRecRemainTime = -1000;

    public static void prepareForMovieRec(MediaRecorder mediaRecorder, AudioManager audioManager) {
        sMediaRecorder = mediaRecorder;
        sAudioManager = audioManager;
        sMediaRecorder.setOnRecRemainListener(mMyRecRemainTimeListener);
        sMediaRecorder.setOnRecTimeListener(mMyRecTimeListener);
        sMediaRecorder.setOnRecordListener(mMyRecordListener);
        sMediaRecorder.setOnStreamWriteListener(mMyStreamWriteListener);
        sMediaRecorder.setOnErrorListener(mMyErrorListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void terminate() {
        super.terminate();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void release(BaseShootingExecutor.ReleasedListener listener) {
        super.release(listener);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void startMovieRec() {
        Runnable runnable = new MovieRecStartRunnable();
        sHandlerFromMain.post(runnable);
    }

    /* loaded from: classes.dex */
    protected static class MovieRecStartRunnable implements Runnable {
        protected MovieRecStartRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                MovieShootingExecutor.sMediaRecorder.start();
            } catch (Exception e) {
                MovieShootingExecutor.sNotify.requestNotify("startMovieRecExecutionError");
                MovieShootingExecutor.STRBUILD_CAMERA.replace(0, MovieShootingExecutor.STRBUILD_CAMERA.length(), "CAMERA :").append("startMovieRecExecutionError");
                Log.e(MovieShootingExecutor.TAG, MovieShootingExecutor.STRBUILD_CAMERA.toString());
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void stopMovieRec() {
        Runnable runnable = new MovieRecStopRunnable();
        sHandlerFromMain.post(runnable);
    }

    /* loaded from: classes.dex */
    protected static class MovieRecStopRunnable implements Runnable {
        protected MovieRecStopRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                MovieShootingExecutor.sMediaRecorder.stop();
            } catch (Exception e) {
                MovieShootingExecutor.sNotify.requestNotify("stopMovieRecExecutionError");
                MovieShootingExecutor.STRBUILD_CAMERA.replace(0, MovieShootingExecutor.STRBUILD_CAMERA.length(), "CAMERA :").append("stopMovieRecExecutionError");
                Log.e(MovieShootingExecutor.TAG, MovieShootingExecutor.STRBUILD_CAMERA.toString());
            }
        }
    }

    public static void setOnRecordListener(MediaRecorder.OnRecordListener callback) {
        sOnRecordListener = callback;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyRecordListener implements MediaRecorder.OnRecordListener {
        protected MyRecordListener() {
        }

        public void onStarted(boolean isSuccess, MediaRecorder mr) {
            MovieShootingExecutor.onRecStarted(isSuccess, mr);
        }

        public void onStopped(MediaRecorder mr) {
            MovieShootingExecutor.onRecStopped(mr);
        }
    }

    protected static void onRecStarted(boolean isSuccess, MediaRecorder mr) {
        Runnable runnable = new RecStartedRunnable(isSuccess, mr);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class RecStartedRunnable implements Runnable {
        protected boolean mIsSuccess;
        protected MediaRecorder mMr;

        public RecStartedRunnable(boolean isSuccess, MediaRecorder mr) {
            this.mIsSuccess = isSuccess;
            this.mMr = mr;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean unused = MovieShootingExecutor.sIsMovieRecording = this.mIsSuccess;
            if (this.mIsSuccess) {
                MovieShootingExecutor.sNotify.requestNotify("startMovieRecSucceeded");
                MovieShootingExecutor.STRBUILD_CAMERA.replace(0, MovieShootingExecutor.STRBUILD_CAMERA.length(), "UI :").append("startMovieRecSucceeded");
            } else {
                MovieShootingExecutor.sNotify.requestNotify("startMovieRecFailed");
                MovieShootingExecutor.STRBUILD_CAMERA.replace(0, MovieShootingExecutor.STRBUILD_CAMERA.length(), "UI :").append("startMovieRecFailed");
            }
            Log.i(MovieShootingExecutor.TAG, MovieShootingExecutor.STRBUILD_CAMERA.toString());
            if (MovieShootingExecutor.sOnRecordListener != null) {
                MovieShootingExecutor.sOnRecordListener.onStarted(this.mIsSuccess, this.mMr);
            }
        }
    }

    protected static void onRecStopped(MediaRecorder mr) {
        Runnable runnable = new RecStoppedRunnable(mr);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class RecStoppedRunnable implements Runnable {
        protected MediaRecorder mMr;

        public RecStoppedRunnable(MediaRecorder mr) {
            this.mMr = mr;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean unused = MovieShootingExecutor.sIsMovieRecording = false;
            MovieShootingExecutor.sNotify.requestNotify(CameraNotificationManager.MOVIE_REC_STOP);
            MovieShootingExecutor.STRBUILD_CAMERA.replace(0, MovieShootingExecutor.STRBUILD_CAMERA.length(), "UI :").append("stopMovieRecMode");
            Log.i(MovieShootingExecutor.TAG, MovieShootingExecutor.STRBUILD_CAMERA.toString());
            if (MovieShootingExecutor.sOnRecordListener != null) {
                MovieShootingExecutor.sOnRecordListener.onStopped(this.mMr);
            }
        }
    }

    public static void setOnStreamWriteListener(MediaRecorder.OnStreamWriteListener callback) {
        sOnStreamWriteListener = callback;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyStreamWriteListener implements MediaRecorder.OnStreamWriteListener {
        protected MyStreamWriteListener() {
        }

        public void onStarted(MediaRecorder mr) {
            MovieShootingExecutor.onStreamWriteStarted(mr);
        }

        public void onCompleted(MediaRecorder mr) {
            MovieShootingExecutor.onStreamWriteCompleted(mr);
        }
    }

    protected static void onStreamWriteStarted(MediaRecorder mr) {
        Runnable runnable = new StreamWriteStartedRunnable(mr);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class StreamWriteStartedRunnable implements Runnable {
        protected MediaRecorder mMr;

        public StreamWriteStartedRunnable(MediaRecorder mr) {
            this.mMr = mr;
        }

        @Override // java.lang.Runnable
        public void run() {
            MovieShootingExecutor.sNotify.requestNotify("startStreamWrite");
            MovieShootingExecutor.STRBUILD_CAMERA.replace(0, MovieShootingExecutor.STRBUILD_CAMERA.length(), "UI :").append("startStreamWrite");
            Log.i(MovieShootingExecutor.TAG, MovieShootingExecutor.STRBUILD_CAMERA.toString());
            if (MovieShootingExecutor.sOnStreamWriteListener != null) {
                MovieShootingExecutor.sOnStreamWriteListener.onStarted(this.mMr);
            }
        }
    }

    protected static void onStreamWriteCompleted(MediaRecorder mr) {
        Runnable runnable = new StreamWriteCompletedRunnable(mr);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class StreamWriteCompletedRunnable implements Runnable {
        protected MediaRecorder mMr;

        public StreamWriteCompletedRunnable(MediaRecorder mr) {
            this.mMr = mr;
        }

        @Override // java.lang.Runnable
        public void run() {
            MovieShootingExecutor.sNotify.requestNotify("stopStreamWrite");
            MovieShootingExecutor.STRBUILD_CAMERA.replace(0, MovieShootingExecutor.STRBUILD_CAMERA.length(), "UI :").append("stopStreamWrite");
            Log.i(MovieShootingExecutor.TAG, MovieShootingExecutor.STRBUILD_CAMERA.toString());
            if (MovieShootingExecutor.sOnStreamWriteListener != null) {
                MovieShootingExecutor.sOnStreamWriteListener.onCompleted(this.mMr);
            }
        }
    }

    public static void setOnRecTimeListener(MediaRecorder.OnRecTimeListener callback) {
        if (sOnRecTimeListener != callback) {
            sOnRecTimeListener = callback;
            onRecTimeChanged(sRecTime, sMediaRecorder);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyRecTimeListener implements MediaRecorder.OnRecTimeListener {
        protected MyRecTimeListener() {
        }

        public void onChanged(int time, MediaRecorder mr) {
            MovieShootingExecutor.sRecTime = time;
            MovieShootingExecutor.onRecTimeChanged(MovieShootingExecutor.sRecTime, MovieShootingExecutor.sMediaRecorder);
        }
    }

    protected static void onRecTimeChanged(int time, MediaRecorder mr) {
        Runnable runnable = new RecTimeChangedRunnable(time, mr);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class RecTimeChangedRunnable implements Runnable {
        protected MediaRecorder mMr;
        protected int mTime;

        public RecTimeChangedRunnable(int time, MediaRecorder mr) {
            this.mTime = time;
            this.mMr = mr;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (MovieShootingExecutor.sOnRecTimeListener != null) {
                MovieShootingExecutor.sOnRecTimeListener.onChanged(this.mTime, this.mMr);
            }
        }
    }

    public static void setOnRecRemainTimeListener(MediaRecorder.OnRecRemainListener callback) {
        if (sOnRecRemainListener != callback) {
            sOnRecRemainListener = callback;
            onRecRemainTimeChanged(sRecRemainTime, sMediaRecorder);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyRecRemainTimeListener implements MediaRecorder.OnRecRemainListener {
        protected MyRecRemainTimeListener() {
        }

        public void onChanged(int time, MediaRecorder mr) {
            MovieShootingExecutor.sRecRemainTime = time;
            MovieShootingExecutor.onRecRemainTimeChanged(MovieShootingExecutor.sRecRemainTime, MovieShootingExecutor.sMediaRecorder);
        }
    }

    protected static void onRecRemainTimeChanged(int time, MediaRecorder mr) {
        Runnable runnable = new RecRemainTimeChangedRunnable(time, mr);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class RecRemainTimeChangedRunnable implements Runnable {
        protected MediaRecorder mMr;
        protected int mTime;

        public RecRemainTimeChangedRunnable(int time, MediaRecorder mr) {
            this.mTime = time;
            this.mMr = mr;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (MovieShootingExecutor.sOnRecRemainListener != null) {
                MovieShootingExecutor.sOnRecRemainListener.onChanged(this.mTime, this.mMr);
            }
        }
    }

    public static void setOnErrorListener(MediaRecorder.OnErrorListener callback) {
        if (sOnErrorListener != callback) {
            sOnErrorListener = callback;
            onMovieRecError(sError, sMediaRecorder);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MyErrorListener implements MediaRecorder.OnErrorListener {
        protected MyErrorListener() {
        }

        public void onError(int error, MediaRecorder mr) {
            MovieShootingExecutor.sError = error;
            MovieShootingExecutor.onMovieRecError(MovieShootingExecutor.sError, MovieShootingExecutor.sMediaRecorder);
        }
    }

    protected static void onMovieRecError(int error, MediaRecorder mr) {
        Runnable runnable = new MovieRecErrorRunnable(error, mr);
        sHandlerToMain.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class MovieRecErrorRunnable implements Runnable {
        protected int mError;
        protected MediaRecorder mMr;

        public MovieRecErrorRunnable(int error, MediaRecorder mr) {
            this.mError = error;
            this.mMr = mr;
        }

        @Override // java.lang.Runnable
        public void run() {
            MovieShootingExecutor.STRBUILD_CAMERA.replace(0, MovieShootingExecutor.STRBUILD_CAMERA.length(), "UI :").append(MovieShootingExecutor.LOG_MOVIE_REC_ERROR);
            Log.i(MovieShootingExecutor.TAG, MovieShootingExecutor.STRBUILD_CAMERA.toString());
            if (MovieShootingExecutor.sOnErrorListener != null) {
                MovieShootingExecutor.sOnErrorListener.onError(this.mError, this.mMr);
            }
        }
    }

    public static boolean isMovieRecording() {
        return sIsMovieRecording;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    protected void myRelease() {
        sMediaRecorder.reset();
        if (true == sIsMovieRecording) {
            sIsMovieRecording = false;
            sNotify.requestNotify(CameraNotificationManager.MOVIE_REC_STOP);
            if (sOnRecordListener != null) {
                sOnRecordListener.onStopped(sMediaRecorder);
            }
        }
        tryRelease();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void setAdapter(IAdapter adapter) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void inquireKey(int key) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void stopPreview() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public void setRecordingMedia(String media, CameraEx.RecordingMediaChangeCallback cb) {
        sRecordingMediaChangeCallback = cb;
        STRBUILD_UI.replace(0, STRBUILD_UI.length(), "UI :").append("setRecordingMedia");
        Log.d(TAG, STRBUILD_UI.toString());
        synchronized (mediaLockObject) {
            sHandlerFromMain.post(new MyMovieRecordingMediaChangeRunnable());
            try {
                mediaLockObject.wait();
            } catch (Exception e) {
            }
        }
    }

    /* loaded from: classes.dex */
    protected static class MyMovieRecordingMediaChangeRunnable implements Runnable {
        protected MyMovieRecordingMediaChangeRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            MovieShootingExecutor.STRBUILD_CAMERA.replace(0, MovieShootingExecutor.STRBUILD_CAMERA.length(), "CAMERA :").append(MovieShootingExecutor.LOG_ON_MOVIE_REC_MEDIA_CHANGE);
            Log.i(MovieShootingExecutor.TAG, MovieShootingExecutor.STRBUILD_CAMERA.toString());
            MovieShootingExecutor.onDoneRecordingMediaChange(MovieShootingExecutor.sCameraEx);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public String getRecordingMedia() {
        return sExternalMedia;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor
    public boolean needToBeStable(int flg) {
        return (flg & (-3)) != 0;
    }
}
