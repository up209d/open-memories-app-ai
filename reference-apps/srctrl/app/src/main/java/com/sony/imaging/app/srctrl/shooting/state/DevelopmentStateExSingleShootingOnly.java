package com.sony.imaging.app.srctrl.shooting.state;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.srctrl.shooting.SRCtrlExecutorCreator;
import com.sony.imaging.app.srctrl.util.MediaObserver;
import com.sony.imaging.app.srctrl.util.MediaObserverAggregator;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.cameraproxy.CameraProxyPostviewImageSize;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.util.PTag;
import java.util.List;

/* loaded from: classes.dex */
public class DevelopmentStateExSingleShootingOnly extends DevelopmentStateExBase {
    protected static final String CURRENT_STATE_NAME = "DevelopmentEx";
    private static final String TAG = DevelopmentStateExSingleShootingOnly.class.getSimpleName();
    private static final int WAIT_FLUSH_FILE_DELAY_MSEC = 100;
    private WaitFlushFileRunnable waitFlushFileRunnable = new WaitFlushFileRunnable();

    @Override // com.sony.imaging.app.srctrl.shooting.state.DevelopmentStateExBase, com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.sony.imaging.app.srctrl.shooting.state.DevelopmentStateExBase, com.sony.imaging.app.base.shooting.DevelopmentState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        try {
            ShootingExecutor.setJpegListener(null);
        } catch (RuntimeException e) {
            Log.e(TAG, "RuntimeException at setJpegListner(null)");
        }
        getHandler().removeCallbacks(this.waitFlushFileRunnable);
        removeData(CaptureStateUtil.KEY_NUMOFBURSTPICTURES);
        removeData(CaptureStateUtil.KEY_NUMOFFINISHEDPICTURES);
        removeData(CaptureStateUtil.KEY_USEFINALPICTURE);
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState
    protected void onPreviewStart() {
        PTag.end("ee start is called");
        this.waitFlushFileRunnable.postToHandler("EE", 0L);
    }

    @Override // com.sony.imaging.app.base.shooting.DevelopmentState
    protected void onPictureReveiwStart() {
        PTag.end("autoreview start is called");
        this.waitFlushFileRunnable.run();
    }

    /* loaded from: classes.dex */
    private class WaitFlushFileRunnable implements Runnable {
        String nextState;
        Bundle returnBundle;

        private WaitFlushFileRunnable() {
            this.nextState = AutoReviewState.STATE_NAME;
            this.returnBundle = null;
        }

        @Override // java.lang.Runnable
        public void run() {
            ShootingHandler shootingHandler = ShootingHandler.getInstance();
            boolean isNoCard = MediaNotificationManager.getInstance().isNoCard();
            if (!CaptureStateUtil.mIsNoCard && isNoCard) {
                Log.v(DevelopmentStateExSingleShootingOnly.TAG, "External media has been removed.");
                shootingHandler.setShootingStatus(ShootingHandler.ShootingStatus.FAILED);
                DevelopmentStateExSingleShootingOnly.this.setNextState(this.nextState, this.returnBundle);
                return;
            }
            if (ShootingHandler.ShootingStatus.PROCESSING == shootingHandler.getShootingStatus()) {
                Log.v(DevelopmentStateExSingleShootingOnly.TAG, "Shooting status is during processing yet.  Wait for the completion...");
                postToHandler(this.nextState, 100L);
                DevelopmentStateExSingleShootingOnly.this.notifyProgressStatus(0.1d);
                return;
            }
            if (!CaptureStateUtilSingleShootingOnly.jpeg_generation_finished) {
                Log.v(DevelopmentStateExSingleShootingOnly.TAG, "onPictureTaken() of JpegListener has not been called during processing yet.  Wait for the completion...");
                postToHandler(this.nextState, 100L);
                DevelopmentStateExSingleShootingOnly.this.notifyProgressStatus(0.2d);
                return;
            }
            if (ShootingHandler.ShootingStatus.DEVELOPING != shootingHandler.getShootingStatus()) {
                Log.e(DevelopmentStateExSingleShootingOnly.TAG, "Invalid state.  ShootingStatus might have been in error already: " + shootingHandler.getShootingStatus().name());
                DevelopmentStateExSingleShootingOnly.this.setNextState(this.nextState, this.returnBundle);
                return;
            }
            if (!DevelopmentStateExSingleShootingOnly.this.isPostviewActualFile()) {
                Log.v(DevelopmentStateExSingleShootingOnly.TAG, "No need to wait for the file flush.");
                shootingHandler.setShootingStatus(ShootingHandler.ShootingStatus.FINISHED);
                DevelopmentStateExSingleShootingOnly.this.setNextState(this.nextState, this.returnBundle);
                if (CaptureStateUtil.isBlibShootingMode()) {
                    shootingHandler.notifyBulbPictureUrl();
                    return;
                } else {
                    if (!CaptureStateUtil.remote_shooting_mode) {
                        shootingHandler.notifyPictureUrl();
                        return;
                    }
                    return;
                }
            }
            if (DevelopmentStateExSingleShootingOnly.this.shouldWaitForFlusingFile()) {
                Log.v(DevelopmentStateExSingleShootingOnly.TAG, "Need to retry flushing the file.");
                postToHandler(this.nextState, 100L);
                DevelopmentStateExSingleShootingOnly.this.notifyProgressStatus(0.75d);
                return;
            }
            Log.v(DevelopmentStateExSingleShootingOnly.TAG, "Complete flushing the file.");
            DevelopmentStateExSingleShootingOnly.this.setNextState(this.nextState, this.returnBundle);
            if (CaptureStateUtil.isBlibShootingMode()) {
                shootingHandler.notifyBulbPictureUrl();
            } else if (!CaptureStateUtil.remote_shooting_mode) {
                shootingHandler.notifyPictureUrl();
            }
        }

        void postToHandler(String nextState, long delay) {
            this.nextState = nextState;
            Handler h = DevelopmentStateExSingleShootingOnly.this.getHandler();
            h.removeCallbacks(this);
            h.postDelayed(this, delay);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldWaitForFlusingFile() {
        ShootingHandler shootingHandler = ShootingHandler.getInstance();
        MediaObserverAggregator mediaObservers = shootingHandler.getMediaObserverAggregator();
        if (mediaObservers == null) {
            Log.e(TAG, "MediaObserverAggregator are needed, but it has gone (=null).");
            shootingHandler.setShootingStatus(ShootingHandler.ShootingStatus.FAILED);
            return false;
        }
        String mediaId = SRCtrlExecutorCreator.getRecordingMedia();
        mediaObservers.invokeFlushingMediaDatabase(mediaId);
        int initialRecordingCount = mediaObservers.getInitialContentsCount(mediaId);
        int currentRecordingCount = mediaObservers.getCurrentContentsCount(mediaId);
        int expectedStoredPictures = mediaObservers.getExpectedStoredPictures(mediaId);
        Log.v(TAG, "CONDITION: Current pics=" + currentRecordingCount + ", Initial pics=" + initialRecordingCount + ", Expected pics=" + expectedStoredPictures);
        if (-1 == currentRecordingCount || -1 == expectedStoredPictures) {
            Log.e(TAG, "Media may have been unmounted.");
            shootingHandler.setShootingStatus(ShootingHandler.ShootingStatus.FAILED);
            return false;
        }
        if (currentRecordingCount - initialRecordingCount == expectedStoredPictures) {
            Log.v(TAG, "File flushing was done.");
            List<String> files = mediaObservers.getImageFileList(mediaId, CaptureStateUtilSingleShootingOnly.numOfBurstPictures);
            if (files == null) {
                Log.e(TAG, "Cannot retrieve stored files.  Media may have been unmounted.");
                shootingHandler.setShootingStatus(ShootingHandler.ShootingStatus.FAILED);
                return false;
            }
            shootingHandler.clearUrlList();
            if (CaptureStateUtilSingleShootingOnly.useFinalPicture) {
                shootingHandler.addToUrlList(SRCtrlConstants.POSTVIEW_DIRECTORY_ON_MEMORY + files.get(0));
            }
            for (String file : files) {
                String url = SRCtrlConstants.POSTVIEW_DIRECTORY_ON_MEMORY + file;
                if (!CaptureStateUtilSingleShootingOnly.useFinalPicture) {
                    shootingHandler.addToUrlList(url);
                }
                shootingHandler.addToUrlListOnMemory(url);
            }
            shootingHandler.setShootingStatus(ShootingHandler.ShootingStatus.FINISHED);
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPostviewActualFile() {
        MediaObserver.MediaType mediaType;
        String mediaId = SRCtrlExecutorCreator.getRecordingMedia();
        MediaObserverAggregator mediaObservers = ShootingHandler.getInstance().getMediaObserverAggregator();
        if (mediaObservers != null && (MediaObserver.MediaType.INTERNAL == (mediaType = mediaObservers.getMediaType(mediaId)) || MediaObserver.MediaType.EXTERNAL == mediaType)) {
            if (CameraProxyPostviewImageSize.isSizeOriginal()) {
                return true;
            }
            Log.e(TAG, "Can't produce the specified size.  Use the default size.");
        }
        return false;
    }
}
