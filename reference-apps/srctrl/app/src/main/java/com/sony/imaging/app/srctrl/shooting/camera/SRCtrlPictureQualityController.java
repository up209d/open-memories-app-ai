package com.sony.imaging.app.srctrl.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class SRCtrlPictureQualityController extends PictureQualityController {
    public static final String TAG = SRCtrlPictureQualityController.class.getName();
    private boolean mIsMounted;

    @Override // com.sony.imaging.app.base.shooting.camera.PictureQualityController, com.sony.imaging.app.base.menu.IController
    public synchronized List<String> getAvailableValue(String tag) {
        List<String> available;
        available = super.getAvailableValue(tag);
        if (CameraSetting.getPfApiVersion() < 2) {
            Iterator<String> itr = available.iterator();
            while (itr.hasNext()) {
                String s = itr.next();
                boolean remove = false;
                if (!this.mIsMounted && PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(s)) {
                    remove = true;
                }
                if (remove) {
                    itr.remove();
                    Log.v(TAG, "Remove RAW-JPEG because media isn't mounted");
                }
            }
        }
        return available;
    }

    public synchronized void setMounted(boolean bMounted) {
        this.mIsMounted = bMounted;
    }
}
