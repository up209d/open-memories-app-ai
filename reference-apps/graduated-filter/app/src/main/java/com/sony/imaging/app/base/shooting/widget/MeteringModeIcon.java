package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class MeteringModeIcon extends ActiveImage {
    private static final String LOG_MSG_ILLEGAL_SETTING = "Illegal setting. ";
    private static final String LOG_MSG_METERINGMODE = "metering mode = ";
    private static final String LOG_RESOURCE_NOT_FOUND = "Resource is not found. mode = ";
    private static final HashMap<String, Integer> RESID_DICTIONARY = new HashMap<>();
    private static final String TAG = "MeteringModeIcon";
    private NotificationListener mMeteringModeListener;
    private TypedArray mTypedArray;

    static {
        RESID_DICTIONARY.put(FocusAreaController.MULTI, 0);
        RESID_DICTIONARY.put("center-weighted", 1);
        RESID_DICTIONARY.put("center-weighted-average", 1);
        RESID_DICTIONARY.put(MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT, 2);
        RESID_DICTIONARY.put("center-spotnone", 2);
        RESID_DICTIONARY.put("center-spotstd", 3);
        RESID_DICTIONARY.put("center-spot3x", 4);
        RESID_DICTIONARY.put("entire-screen-average", 5);
        RESID_DICTIONARY.put("highlight-weighted", 6);
    }

    public MeteringModeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.MeteringModeIcon);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mMeteringModeListener == null) {
            this.mMeteringModeListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.MeteringModeIcon.1
                private String[] TAGS = {"MeteringMode", CameraNotificationManager.METERING_MODE_SPOT_SIZE};

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    MeteringModeIcon.this.setVisibility(MeteringModeIcon.this.isVisible() ? 0 : 4);
                    MeteringModeIcon.this.refresh();
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return this.TAGS;
                }
            };
        }
        return this.mMeteringModeListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        String mode = MeteringController.getInstance().getValue(null);
        String value = null;
        if (MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT.equals(mode)) {
            value = MeteringController.getInstance().getValue(MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT);
        }
        if (mode != null && value != null && !value.equals(mode)) {
            mode = mode + value;
        }
        Drawable d = obtainDrawable(mode);
        if (d == null) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_ILLEGAL_SETTING).append(LOG_MSG_METERINGMODE).append(mode);
            Log.w(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
        }
        setImageDrawable(d);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        List<String> supported = MeteringController.getInstance().getSupportedValue("MeteringMode");
        return (supported == null || supported.size() == 0 || MeteringController.getInstance().isUnavailableSceneFactor(null)) ? false : true;
    }

    private Drawable obtainDrawable(String mode) {
        Integer resId = RESID_DICTIONARY.get(mode);
        if (resId == null) {
            return null;
        }
        try {
            Drawable d = this.mTypedArray.getDrawable(resId.intValue());
            return d;
        } catch (Resources.NotFoundException e) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            Log.i(TAG, builder.replace(0, builder.length(), LOG_RESOURCE_NOT_FOUND).append(mode).toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            return null;
        }
    }
}
