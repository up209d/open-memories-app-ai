package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.webapi.util.AttachedLensInfo;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class CameraOperationAttachedLensInfo {
    private static final String TAG = CameraOperationAttachedLensInfo.class.getSimpleName();
    private static WeakReference<NotificationListener> s_AttachedLensInfoListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_AttachedLensInfoListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationAttachedLensInfo.1
                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.DEVICE_LENS_ATTACH_DETACH};
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    Log.v(CameraOperationAttachedLensInfo.TAG, "onNotify");
                    CameraEx.LensInfo lensinfo = CameraSetting.getInstance().getLensInfo();
                    if (lensinfo != null) {
                        if (SRCtrlEnvironment.getInstance().isEnableLensID()) {
                            AttachedLensInfo.setLensInfo(lensinfo.LensName, lensinfo.LensId, lensinfo.LensVersion);
                        } else {
                            AttachedLensInfo.setLensInfo(lensinfo.LensName);
                        }
                    }
                }
            };
            s_AttachedLensInfoListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }
}
