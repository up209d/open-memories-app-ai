package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.NDfilterController;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class NDFilterIcon extends ActiveImage {
    public static final String ITEM_ID_NDFILTER = "NDfilter";
    private NotificationListener mListener;
    protected TypedArray mTypedArray;
    private static String TAG = "NDFilterIcon";
    protected static final HashMap<String, Integer> ICONS = new HashMap<>();

    static {
        ICONS.put("auto", 0);
        ICONS.put("on", 2);
        ICONS.put("off", 1);
    }

    public NDFilterIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListener = null;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.NDFilterIcon);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.NDFilterIcon.1
                private String[] TAGS = {CameraNotificationManager.NDFILTER_CHANGED};

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    NDFilterIcon.this.setVisibility(NDFilterIcon.this.isVisible() ? 0 : 4);
                    NDFilterIcon.this.refresh();
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.TAGS;
                }
            };
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        if (6 <= CameraSetting.getPfApiVersion()) {
            Drawable drw = null;
            String value = NDfilterController.getInstance().getValue("NDfilter");
            int id = ICONS.get(value).intValue();
            try {
                drw = this.mTypedArray.getDrawable(id);
            } catch (Exception e) {
                setVisibility(4);
            }
            setImageDrawable(drw);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public boolean isVisible() {
        if (6 > CameraSetting.getPfApiVersion() || NDfilterController.getInstance().getSupportedValueList() == null) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
