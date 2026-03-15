package com.sony.imaging.app.startrails;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.List;

/* loaded from: classes.dex */
public class UtilPFWorkaround {
    public static final String DSCHX90V = "DSC-HX90V";
    public static final String DSCRX100M4 = "DSC-RX100M4";
    public static final String DSCRX10M2 = "DSC-RX10M2";
    public static final String DSCWX500 = "DSC-WX500";
    public static final String ILCE7M2 = "ILCE-7M2";
    public static final String ILCE7RM2 = "ILCE-7RM2";
    public static final String ILCE7S = "ILCE-7S";
    public static final String UI_MODEL_NAME_DMH = "DSLR-15-01";
    public static final String UI_MODEL_NAME_DMHH = "DSLR-15-04";
    public static final String UI_MODEL_NAME_FV = "DSC-15-01";
    public static final String UI_MODEL_NAME_GC = "DSLR-15-02";
    public static final String UI_MODEL_NAME_ICV2 = "DSLR-15-03";
    public static final String UI_MODEL_NAME_KV = "DSC-15-03";
    public static final String UI_MODEL_NAME_ZV = "DSC-15-02";
    static String TAG = "UtilPFWorkaround";
    static String Version = "1.2";
    public static final String PF_VERSION = ScalarProperties.getString("version.platform");
    public static final int PF_MAJOR_VERSION = Integer.parseInt(PF_VERSION.substring(0, PF_VERSION.indexOf(StringBuilderThreadLocal.PERIOD)));
    public static final int PF_API_VERSION = Integer.parseInt(PF_VERSION.substring(0, PF_VERSION.indexOf(StringBuilderThreadLocal.PERIOD)));
    public static final String MODEL_NAME = ScalarProperties.getString("model.name");
    public static final String UI_MODEL_NAME = ScalarProperties.getString("ui.model.mame");
    public static boolean isHighSpeedLensPowerOffWorkaroundNeeded = isHighSpeedLensPowerOffWorkaroundNeeded();

    private static boolean isHighSpeedLensPowerOffWorkaroundNeeded() {
        Log.i(TAG, "ModelName: " + MODEL_NAME);
        boolean isDeviceHighSpeedLensPowerOff = "DSC-HX90V".equals(MODEL_NAME) || "DSC-WX500".equals(MODEL_NAME) || "DSC-RX100M4".equals(MODEL_NAME) || "DSC-RX10M2".equals(MODEL_NAME);
        boolean isHighSpeedOffFixed = (ScalarProperties.getInt("ui.modification.patch.bits0") & 1) == 1;
        if (!isDeviceHighSpeedLensPowerOff || isHighSpeedOffFixed) {
            return false;
        }
        Log.i(TAG, "isHighSpeedLensPowerOffWorkaroundNeeded:  true");
        return true;
    }

    public static boolean isDeviceNameModifyNeeded() {
        return 9 >= Environment.getVersionPfAPI();
    }

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
            boolean isVirtualMedia_PlayProblem_Occures = "DSC-HX90V".equals(MODEL_NAME) || "DSC-WX500".equals(MODEL_NAME) || "DSC-RX100M4".equals(MODEL_NAME) || "DSC-RX10M2".equals(MODEL_NAME) || "ILCE-7M2".equals(MODEL_NAME) || "ILCE-7RM2".equals(MODEL_NAME);
            Log.i(TAG, "model.name = " + MODEL_NAME);
            Log.i(TAG, "ui.model.mame   = " + UI_MODEL_NAME);
            if (playAfterShoot && isVirtualMedia_PlayProblem_Occures) {
                handlePictureSizeControllerPFIssue();
                return;
            } else {
                Log.e(TAG, "Work around not required for these models");
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
}
