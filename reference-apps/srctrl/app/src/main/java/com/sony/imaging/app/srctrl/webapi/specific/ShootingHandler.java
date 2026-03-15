package com.sony.imaging.app.srctrl.webapi.specific;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.MediaObserverAggregator;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class ShootingHandler {
    private static final String TAG = ShootingHandler.class.getSimpleName();
    private static ShootingHandler sHandler = new ShootingHandler();
    private WeakReference<MediaObserverAggregator> mediaObserverAggregatorRef;
    private ShootingStatus shootingStatus = ShootingStatus.READY;
    private Boolean onShutterResult = null;
    private WaitingStatus waitingStatus = WaitingStatus.NOT_WAITING;
    private Object sync = new Object();
    private Error error = Error.UNKNOWN;
    private CountDownLatch movieLatch = null;
    private ArrayList<String> urlList = new ArrayList<>();
    private ArrayList<String> urlListOnMemory = new ArrayList<>();

    /* loaded from: classes.dex */
    public enum Error {
        UNKNOWN,
        CANCELED,
        JPEG_TIMEOUT
    }

    /* loaded from: classes.dex */
    public enum ShootingStatus {
        READY,
        PROCESSING,
        DEVELOPING,
        FINISHED,
        FAILED
    }

    /* loaded from: classes.dex */
    public enum WaitingStatus {
        NOT_WAITING,
        WAITING,
        CANCELED
    }

    public static ShootingHandler getInstance() {
        return sHandler;
    }

    public void setMediaObserverAggregator(MediaObserverAggregator aggregator) {
        this.mediaObserverAggregatorRef = new WeakReference<>(aggregator);
    }

    public MediaObserverAggregator getMediaObserverAggregator() {
        if (this.mediaObserverAggregatorRef == null) {
            return null;
        }
        return this.mediaObserverAggregatorRef.get();
    }

    /* loaded from: classes.dex */
    public final class ShutterListenerEx implements ShutterListenerNotifier {
        public ShutterListenerEx() {
        }

        @Override // com.sony.imaging.app.srctrl.webapi.specific.ShutterListenerNotifier
        public void onShutterNotify(int status, CameraEx cam) {
            if (status == 0) {
                Log.v(ShootingHandler.TAG, "onShutter: Success");
                ShootingHandler.this.setOnShuttorResult(true);
                return;
            }
            if (status == 1) {
                Log.v(ShootingHandler.TAG, "onShutter: Canceled");
                ShootingHandler.this.error = Error.CANCELED;
            } else if (status == 2) {
                Log.v(ShootingHandler.TAG, "onShutter: Error");
                ShootingHandler.this.error = Error.UNKNOWN;
            }
            ShootingHandler.this.setShootingStatus(ShootingStatus.FAILED);
            ShootingHandler.this.setOnShuttorResult(false);
        }
    }

    public ShootingStatus createStillPicture() {
        if (WaitingStatus.WAITING.equals(this.waitingStatus)) {
            Log.v(TAG, "Wait until previous one is stopped");
            synchronized (this.sync) {
                try {
                    onDoublePolling();
                    this.sync.wait(5000L);
                    Log.v(TAG, "Previous one is finished: " + this.waitingStatus.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    setShootingStatus(ShootingStatus.FAILED);
                    return this.shootingStatus;
                }
            }
        }
        if (!ShootingStatus.PROCESSING.equals(this.shootingStatus) && !ShootingStatus.DEVELOPING.equals(this.shootingStatus)) {
            this.error = Error.UNKNOWN;
            takePicture(SRCtrlConstants.SHUTTER_TYPE_NORMAL);
        }
        long remainingWaitTime = 15000;
        synchronized (this.sync) {
            this.waitingStatus = WaitingStatus.WAITING;
            while (remainingWaitTime > 0 && (ShootingStatus.PROCESSING == this.shootingStatus || ShootingStatus.DEVELOPING == this.shootingStatus)) {
                try {
                    long startWaitTime = System.currentTimeMillis();
                    this.sync.wait(remainingWaitTime);
                    long endWaitTime = System.currentTimeMillis();
                    remainingWaitTime -= endWaitTime - startWaitTime;
                    if (remainingWaitTime < 0) {
                        remainingWaitTime = 0;
                    }
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                    setShootingStatus(ShootingStatus.FAILED);
                    return this.shootingStatus;
                }
            }
        }
        if (!WaitingStatus.CANCELED.equals(this.waitingStatus)) {
            this.waitingStatus = WaitingStatus.NOT_WAITING;
        }
        synchronized (this.sync) {
            this.sync.notifyAll();
        }
        Log.v(TAG, this.waitingStatus.toString());
        return this.shootingStatus;
    }

    private void takePicture(String shootingType) {
        setShootingStatus(ShootingStatus.PROCESSING);
        this.onShutterResult = null;
        Boolean move_capture_state_result = (Boolean) new OperationRequester().request(21, new ShutterListenerEx(), shootingType);
        if (move_capture_state_result == null || !move_capture_state_result.booleanValue()) {
            setShootingStatus(ShootingStatus.FAILED);
        }
    }

    public ShootingStatus startShooting(String shootingType) {
        if (WaitingStatus.WAITING.equals(this.waitingStatus)) {
            Log.v(TAG, "Wait until previous one is stopped");
            synchronized (this.sync) {
                try {
                    onDoublePolling();
                    this.sync.wait(5000L);
                    Log.v(TAG, "Previous one is finished: " + this.waitingStatus.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    setShootingStatus(ShootingStatus.FAILED);
                    return this.shootingStatus;
                }
            }
        }
        if (!ShootingStatus.PROCESSING.equals(this.shootingStatus) && !ShootingStatus.DEVELOPING.equals(this.shootingStatus)) {
            this.error = Error.UNKNOWN;
            takePicture(shootingType);
        }
        long remainingWaitTime = 15000;
        synchronized (this.sync) {
            this.waitingStatus = WaitingStatus.WAITING;
            while (remainingWaitTime > 0 && ((ShootingStatus.PROCESSING == this.shootingStatus || ShootingStatus.DEVELOPING == this.shootingStatus) && this.onShutterResult == null)) {
                try {
                    long startWaitTime = System.currentTimeMillis();
                    this.sync.wait(remainingWaitTime);
                    long endWaitTime = System.currentTimeMillis();
                    remainingWaitTime -= endWaitTime - startWaitTime;
                    if (remainingWaitTime < 0) {
                        remainingWaitTime = 0;
                    }
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                    setShootingStatus(ShootingStatus.FAILED);
                    return this.shootingStatus;
                }
            }
        }
        if (!WaitingStatus.CANCELED.equals(this.waitingStatus)) {
            this.waitingStatus = WaitingStatus.NOT_WAITING;
        }
        synchronized (this.sync) {
            this.sync.notifyAll();
        }
        Log.v(TAG, this.waitingStatus.toString());
        return this.shootingStatus;
    }

    public ShootingStatus startContShooting() {
        return startShooting(SRCtrlConstants.SHUTTER_TYPE_CONT);
    }

    public void stopContShooting() {
        Boolean stop_contshooting_result = (Boolean) new OperationRequester().request(36, new ShutterListenerEx());
        if (stop_contshooting_result == null || !stop_contshooting_result.booleanValue()) {
            setShootingStatus(ShootingStatus.FAILED);
        }
    }

    public ShootingStatus startBulbShooting() {
        return startShooting("BULB");
    }

    public void stopBlubShooting() {
        Boolean result = (Boolean) new OperationRequester().request(60, new ShutterListenerEx());
        if (result == null || !result.booleanValue()) {
            setShootingStatus(ShootingStatus.FAILED);
        }
    }

    /* loaded from: classes.dex */
    public final class MovieShutterListenerEx implements MovieCaptureListenerNotifier {
        public MovieShutterListenerEx() {
        }

        @Override // com.sony.imaging.app.srctrl.webapi.specific.MovieCaptureListenerNotifier
        public void onNotify(int status) {
            switch (status) {
                case 0:
                    Log.v(ShootingHandler.TAG, "MovieShutterListenerEx.onNotify: Success");
                    ShootingHandler.this.error = null;
                    break;
                case 1:
                    Log.e(ShootingHandler.TAG, "MovieShutterListenerEx.onNotify: Error");
                    ShootingHandler.this.error = Error.UNKNOWN;
                    break;
                default:
                    Log.e(ShootingHandler.TAG, "MovieShutterListenerEx.onNotify: Unknown");
                    ShootingHandler.this.error = Error.UNKNOWN;
                    break;
            }
            ShootingHandler.this.movieLatch.countDown();
        }
    }

    public ShootingStatus startMovieRec() {
        this.error = Error.UNKNOWN;
        this.movieLatch = new CountDownLatch(1);
        Boolean move_rec_start_state_result = (Boolean) new OperationRequester().request(28, new MovieShutterListenerEx());
        if (move_rec_start_state_result == null || !move_rec_start_state_result.booleanValue()) {
            return ShootingStatus.FAILED;
        }
        try {
            Log.v(TAG, "movieLatch.await: start");
            this.movieLatch.await(15000L, TimeUnit.MILLISECONDS);
            Log.v(TAG, "movieLatch.await: end");
            this.movieLatch = null;
            if (this.error != null) {
                return ShootingStatus.FAILED;
            }
            return ShootingStatus.PROCESSING;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ShootingStatus.FAILED;
        }
    }

    public boolean stopMovieRec() {
        Boolean rec_stop_result = (Boolean) new OperationRequester().request(29, new Object[0]);
        return rec_stop_result != null && rec_stop_result.booleanValue();
    }

    public Error getErrorStatus() {
        return this.error;
    }

    public ShootingStatus getShootingStatus() {
        return this.shootingStatus;
    }

    public WaitingStatus getWaitingStatus() {
        return this.waitingStatus;
    }

    public void onPauseCalled() {
        Log.v(TAG, "detected onPause is called.");
        setShootingStatus(ShootingStatus.FAILED);
    }

    public void onDoublePolling() {
        synchronized (this.sync) {
            Log.v(TAG, "detected double Polling.");
            this.waitingStatus = WaitingStatus.CANCELED;
            this.sync.notifyAll();
        }
    }

    public void setShootingStatus(ShootingStatus status) {
        synchronized (this.sync) {
            Log.v(TAG, "Changing Shooting Status from " + this.shootingStatus.name() + " to + " + status.name() + StringBuilderThreadLocal.PERIOD);
            this.shootingStatus = status;
            this.sync.notify();
        }
    }

    public void setOnShuttorResult(boolean result) {
        synchronized (this.sync) {
            Log.v(TAG, "Changing OnShuttor Status :" + result);
            this.onShutterResult = Boolean.valueOf(result);
            this.sync.notify();
        }
    }

    public String[] getUrlArrayResult() {
        String[] strArr;
        synchronized (this.urlList) {
            strArr = (String[]) this.urlList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
        }
        return strArr;
    }

    public void addToUrlList(String url) {
        synchronized (this.urlList) {
            this.urlList.add(url);
        }
    }

    public void clearUrlList() {
        synchronized (this.urlList) {
            this.urlList.clear();
        }
    }

    public String[] getUrlArrayOnMemoryResult() {
        String[] strArr;
        synchronized (this.urlListOnMemory) {
            strArr = (String[]) this.urlListOnMemory.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY);
        }
        return strArr;
    }

    public void addToUrlListOnMemory(String url) {
        synchronized (this.urlListOnMemory) {
            this.urlListOnMemory.add(0, url);
        }
    }

    public void notifyPictureUrl() {
        synchronized (this.urlList) {
            ParamsGenerator.updateTakePictureParams((String[]) this.urlList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY));
        }
        ServerEventHandler.getInstance().onServerStatusChanged();
    }

    public void notifyBulbPictureUrl() {
        synchronized (this.urlList) {
            ParamsGenerator.updateBulbShootingParams((String[]) this.urlList.toArray(SRCtrlConstants.s_EMPTY_STRING_ARRAY));
        }
        ServerEventHandler.getInstance().onServerStatusChanged();
    }
}
