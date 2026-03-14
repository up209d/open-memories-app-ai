package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class DRO_HDRModeIcon extends ActiveImage {
    private static final HashMap<String, Integer> ICON_IDS = new HashMap<>();
    private static final String LOG_RESOURCE_NOT_FOUND = "Resource is not found. id = ";
    private final String TAG;
    private NotificationListener mDroHdrListener;
    private TypedArray mTypedArray;

    static {
        ICON_IDS.put("off", 0);
        ICON_IDS.put(DROAutoHDRController.MODE_DRO_AUTO, 1);
        ICON_IDS.put(DROAutoHDRController.MODE_HDR_AUTO, 7);
        ICON_IDS.put(DROAutoHDRController.DRO_LEVEL_1, 2);
        ICON_IDS.put(DROAutoHDRController.DRO_LEVEL_2, 3);
        ICON_IDS.put(DROAutoHDRController.DRO_LEVEL_3, 4);
        ICON_IDS.put(DROAutoHDRController.DRO_LEVEL_4, 5);
        ICON_IDS.put(DROAutoHDRController.DRO_LEVEL_5, 6);
        ICON_IDS.put(DROAutoHDRController.HDR_EV_1, 8);
        ICON_IDS.put(DROAutoHDRController.HDR_EV_2, 9);
        ICON_IDS.put(DROAutoHDRController.HDR_EV_3, 10);
        ICON_IDS.put(DROAutoHDRController.HDR_EV_4, 11);
        ICON_IDS.put(DROAutoHDRController.HDR_EV_5, 12);
        ICON_IDS.put(DROAutoHDRController.HDR_EV_6, 13);
    }

    public DRO_HDRModeIcon(Context context) {
        this(context, null);
    }

    public DRO_HDRModeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = "DRO_HDRModeIcon";
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DRO_AUTO_HDR_Icon);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        String mode = DROAutoHDRController.getInstance().getValue();
        Drawable d = obtainDrawable(mode);
        setImageDrawable(d);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mDroHdrListener == null) {
            this.mDroHdrListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.DRO_HDRModeIcon.1
                private final String[] tags = {CameraNotificationManager.DRO_AUTO_HDR};

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    DRO_HDRModeIcon.this.setVisibility(DRO_HDRModeIcon.this.isVisible() ? 0 : 4);
                    DRO_HDRModeIcon.this.refresh();
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return this.tags;
                }
            };
        }
        return this.mDroHdrListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        List<String> supported = DROAutoHDRController.getInstance().getSupportedValue(DROAutoHDRController.MENU_ITEM_ID_DROHDR);
        if (supported == null || supported.size() == 0) {
            return false;
        }
        return !DROAutoHDRController.getInstance().isUnavailableSceneFactor(DROAutoHDRController.MENU_ITEM_ID_DROHDR);
    }

    private Drawable obtainDrawable(String mode) {
        Integer resId = ICON_IDS.get(mode);
        if (resId == null) {
            return null;
        }
        try {
            Drawable d = this.mTypedArray.getDrawable(resId.intValue());
            return d;
        } catch (Resources.NotFoundException e) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            Log.i("DRO_HDRModeIcon", builder.replace(0, builder.length(), LOG_RESOURCE_NOT_FOUND).append(resId).toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            return null;
        }
    }
}
