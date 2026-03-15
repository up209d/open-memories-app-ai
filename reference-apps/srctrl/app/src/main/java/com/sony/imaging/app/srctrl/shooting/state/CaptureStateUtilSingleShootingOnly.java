package com.sony.imaging.app.srctrl.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.srctrl.shooting.SRCtrlExecutorCreator;
import com.sony.imaging.app.srctrl.util.MediaObserverAggregator;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.webapi.servlet.PostviewResourceLoader;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.sysutil.TimeUtil;

/* loaded from: classes.dex */
public class CaptureStateUtilSingleShootingOnly extends CaptureStateUtil {
    private static final String TAG = CaptureStateUtilSingleShootingOnly.class.getSimpleName();
    static boolean jpeg_generation_finished;
    static int numOfBurstPictures;
    static int numOfFinishedPictures;
    static boolean useFinalPicture;

    @Override // com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil
    public void init() {
        Log.v(TAG, "init");
    }

    @Override // com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil
    public void term() {
        Log.v(TAG, "term");
    }

    @Override // com.sony.imaging.app.srctrl.shooting.state.CaptureStateUtil
    public void startCapture() {
        int remaining;
        PostviewResourceLoader.initData();
        numOfFinishedPictures = 0;
        numOfBurstPictures = 1;
        useFinalPicture = false;
        jpeg_generation_finished = false;
        ShootingHandler shootingHandler = ShootingHandler.getInstance();
        shootingHandler.clearUrlList();
        shootingHandler.setShootingStatus(ShootingHandler.ShootingStatus.PROCESSING);
        CaptureStateUtil.mIsNoCard = MediaNotificationManager.getInstance().isNoCard();
        String mediaId = SRCtrlExecutorCreator.getRecordingMedia();
        ShootingExecutor.setJpegListener(new JpegListenerEx(mediaId));
        if (DROAutoHDRController.MENU_ITEM_ID_HDR.equals(DROAutoHDRController.getInstance().getValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR))) {
            numOfBurstPictures++;
            useFinalPicture = true;
        }
        if (MediaObserverAggregator.isExternalMediaMounted() && (remaining = MediaNotificationManager.getInstance().getRemaining()) < numOfBurstPictures) {
            numOfBurstPictures = remaining;
        }
        MediaObserverAggregator mediaObservers = ShootingHandler.getInstance().getMediaObserverAggregator();
        int initialRecordingCount = 0;
        if (mediaObservers != null) {
            initialRecordingCount = mediaObservers.getInitialContentsCount(mediaId);
        }
        if (-1 == initialRecordingCount) {
            Log.e(TAG, "MediaObserver may not be initialized or target media(ID=" + mediaId + ") may not be mounted so that initial contents count cannot be obtained.");
            ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.FAILED);
        }
    }

    /* loaded from: classes.dex */
    private final class JpegListenerEx implements CameraEx.JpegListener {
        private String mediaId;

        public JpegListenerEx(String id) {
            this.mediaId = id;
        }

        public void onPictureTaken(byte[] jpeg, CameraEx cam) {
            Log.v(CaptureStateUtilSingleShootingOnly.TAG, "onPictureTaken in JpegListenerEx");
            CaptureStateUtilSingleShootingOnly.jpeg_generation_finished = true;
            if (CaptureStateUtilSingleShootingOnly.useFinalPicture) {
                if (CaptureStateUtilSingleShootingOnly.numOfFinishedPictures + 1 == CaptureStateUtilSingleShootingOnly.numOfBurstPictures) {
                    addToServer(jpeg);
                }
            } else {
                addToServer(jpeg);
            }
            CaptureStateUtilSingleShootingOnly.numOfFinishedPictures++;
            MediaObserverAggregator mediaObservers = ShootingHandler.getInstance().getMediaObserverAggregator();
            if (mediaObservers != null) {
                mediaObservers.increaseExpectedStoredPictures(this.mediaId, 1);
            }
            Log.v(CaptureStateUtilSingleShootingOnly.TAG, "Finished: " + CaptureStateUtilSingleShootingOnly.numOfFinishedPictures + ", Full: " + CaptureStateUtilSingleShootingOnly.numOfBurstPictures);
            if (CaptureStateUtilSingleShootingOnly.numOfFinishedPictures == CaptureStateUtilSingleShootingOnly.numOfBurstPictures) {
                ShootingHandler.getInstance().setShootingStatus(ShootingHandler.ShootingStatus.DEVELOPING);
            }
        }

        private void addToServer(byte[] jpeg) {
            PlainCalendar cal = TimeUtil.getCurrentCalendar();
            String filename_base = SRCtrlConstants.POSTVIEW_FILENAME_PREFIX + String.format("%04d", Integer.valueOf(cal.year)) + String.format("%02d", Integer.valueOf(cal.month)) + String.format("%02d", Integer.valueOf(cal.day)) + SRCtrlConstants.URI_CONTENT_ID_SEPARATOR + String.format("%02d", Integer.valueOf(cal.hour)) + String.format("%02d", Integer.valueOf(cal.minute)) + String.format("%02d", Integer.valueOf(cal.second)) + SRCtrlConstants.URI_CONTENT_ID_SEPARATOR;
            String url = filename_base + CaptureStateUtilSingleShootingOnly.numOfFinishedPictures + ".JPG";
            ShootingHandler.getInstance().addToUrlList(SRCtrlConstants.POSTVIEW_DIRECTORY + url);
            PostviewResourceLoader.addPicture(jpeg, url);
            Log.v(CaptureStateUtilSingleShootingOnly.TAG, url);
        }
    }
}
