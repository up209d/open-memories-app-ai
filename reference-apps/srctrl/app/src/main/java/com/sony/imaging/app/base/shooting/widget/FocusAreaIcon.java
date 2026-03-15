package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class FocusAreaIcon extends ActiveImage {
    private static final String LOG_INVALID = "Invalid value of FocusAreaSetting. Set FocusAreaIcon to Invisible";
    private static final String LOG_RESOURCE_NOT_FOUND = "Resource is not found. id = ";
    private static HashMap<String, Integer> valueMap = new HashMap<>();
    private String TAG;
    private final String[] TAGS;
    boolean isDisableBySceneFactor;
    private CameraSetting mCameraSetting;
    private FocusAreaController mFocusAreaController;
    private NotificationListener mListener;
    private TypedArray mTypedArray;

    static {
        valueMap.put(FocusAreaController.MULTI, 0);
        valueMap.put(FocusAreaController.CENTER_WEIGHTED, 1);
        valueMap.put("flex-spot", 2);
        valueMap.put(FocusAreaController.WIDE, 3);
        valueMap.put(FocusAreaController.FIX_CENTER, 4);
        valueMap.put(FocusAreaController.LOCAL, 6);
    }

    public FocusAreaIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = "FocusAreaIcon";
        this.mListener = null;
        this.isDisableBySceneFactor = false;
        this.TAGS = new String[]{CameraNotificationManager.AUTO_FOCUS_AREA, CameraNotificationManager.FOCUS_CHANGE, CameraNotificationManager.ZOOM_INFO_CHANGED, CameraNotificationManager.DEVICE_LENS_CHANGED};
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusAreaIcon);
        this.mFocusAreaController = FocusAreaController.getInstance();
        this.mCameraSetting = CameraSetting.getInstance();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.FocusAreaIcon.1
                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.AUTO_FOCUS_AREA.equals(tag) || CameraNotificationManager.FOCUS_CHANGE.equals(tag) || CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                        FocusAreaIcon.this.refresh();
                    } else {
                        super.onNotify(tag);
                    }
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return FocusAreaIcon.this.TAGS;
                }
            };
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        List<String> supported = this.mFocusAreaController.getSupportedValue(FocusAreaController.TAG_FOCUS_AREA);
        if (supported == null || supported.size() == 0) {
            return false;
        }
        if (this.mFnMode || CameraSetting.getPfApiVersion() >= 2) {
            return true;
        }
        String focusMode = FocusModeController.getInstance().getValue();
        boolean ret = true & (FocusModeController.MANUAL.equals(focusMode) ? false : true);
        return ret;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        if (!isVisible()) {
            setVisibility(4);
            return;
        }
        String s = null;
        setVisibility(0);
        if (1 == this.mFocusAreaController.getSensorType() && this.mFocusAreaController.isDigitalZoomMagOverAFAreaChangeToCenter()) {
            s = FocusAreaController.FIX_CENTER;
        } else {
            try {
                s = this.mFocusAreaController.getValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Integer rID = valueMap.get(s);
        if (rID != null) {
            Drawable d = obtainDrawable(rID.intValue());
            setImageDrawable(d);
        } else {
            Log.e(this.TAG, LOG_INVALID);
            setVisibility(4);
        }
    }

    private Drawable obtainDrawable(int id) {
        try {
            Drawable d = this.mTypedArray.getDrawable(id);
            return d;
        } catch (Resources.NotFoundException e) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            Log.i(this.TAG, builder.replace(0, builder.length(), LOG_RESOURCE_NOT_FOUND).append(id).toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            return null;
        }
    }
}
