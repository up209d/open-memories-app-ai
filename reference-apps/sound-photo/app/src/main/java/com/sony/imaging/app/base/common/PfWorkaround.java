package com.sony.imaging.app.base.common;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.PfBugAvailability;
import com.sony.scalar.provider.AvindexStore;
import java.util.List;

/* loaded from: classes.dex */
public class PfWorkaround {
    static String TAG = "PfWorkaround";

    public static void setVirtualMediaIds(boolean playAfterShoot) {
        Log.i(TAG, "setVirtualMediaIds");
        ShootingExecutor executor = (ShootingExecutor) ExecutorCreator.getInstance().getSequence();
        if (executor == null) {
            Log.e(TAG, "change to virtual media failed.  getSequence is null.");
            return;
        }
        String[] ids = AvindexStore.getVirtualMediaIds();
        if (ids != null && ids[0] != null) {
            executor.setRecordingMedia(ids[0], null);
            if (playAfterShoot && PfBugAvailability.virtualMediaPlay) {
                handlePictureSizeControllerPFIssue();
                return;
            } else {
                Log.i(TAG, "Work around not required for these models");
                return;
            }
        }
        Log.e(TAG, "change to virtual media failed");
    }

    private static void handlePictureSizeControllerPFIssue() {
        List<String> picture_size_list = PictureSizeController.getInstance().getAvailableValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
        if (picture_size_list.size() >= 2) {
            String still_picture_size = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
            Log.e(TAG, "execute work around due to picture size option is enough");
            int org_size_index = picture_size_list.indexOf(still_picture_size);
            picture_size_list.remove(org_size_index);
            String temp_picture_size = picture_size_list.get(0);
            PictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, temp_picture_size);
            PictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE, still_picture_size);
            return;
        }
        Log.e(TAG, "execute work around due to picture size option is not enough");
    }

    public static boolean sleepUntilSdWriteEnd(long timeout) {
        Log.i(TAG, "INH_FACTOR_STILL_WRITING check ");
        AvailableInfo.update();
        long timer_start = System.currentTimeMillis();
        while (MediaNotificationManager.getInstance().isMounted() && AvailableInfo.isFactor("INH_FACTOR_STILL_WRITING")) {
            if (timeout > 0 && System.currentTimeMillis() - timer_start > timeout) {
                Log.w(TAG, "Still writing to media.   sleep timeout");
                return true;
            }
            Log.w(TAG, "Still writing to media.   100msec sleep");
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AvailableInfo.update();
        }
        return false;
    }

    public static void sleepUntilCaptureEnd() {
        Log.i(TAG, "INH_FACTOR_STILL_CAPTURING check ");
        AvailableInfo.update();
        while (AvailableInfo.isFactor("INH_FACTOR_STILL_CAPTURING")) {
            Log.w(TAG, "Still capturing.   100msec sleep");
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AvailableInfo.update();
        }
    }
}
