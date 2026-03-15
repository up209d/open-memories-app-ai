package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MeteringModeIcon extends ActiveImage {
    private static final String LOG_MSG_ILLEGAL_SETTING = "Illegal setting. ";
    private static final String LOG_MSG_METERINGMODE = "metering mode = ";
    private static final String LOG_RESOURCE_NOT_FOUND = "Resource is not found. mode = ";
    private static final String TAG = "MeteringModeIcon";
    private NotificationListener mMeteringModeListener;
    private TypedArray mTypedArray;
    private static final StringBuilder STRBUILD = new StringBuilder();
    private static final HashMap<String, Integer> RESID_DICTIONARY = new HashMap<>();

    static {
        RESID_DICTIONARY.put(FocusAreaController.MULTI, 0);
        RESID_DICTIONARY.put("center-weighted", 1);
        RESID_DICTIONARY.put("center-weighted-average", 1);
        RESID_DICTIONARY.put("center-spot", 2);
    }

    public MeteringModeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.MeteringModeIcon);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mMeteringModeListener == null) {
            this.mMeteringModeListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.MeteringModeIcon.1
                private String[] TAGS = {"MeteringMode"};

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
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
        String ms = MeteringController.getInstance().getValue(null);
        Drawable d = obtainDrawable(ms);
        if (d == null) {
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_ILLEGAL_SETTING).append(LOG_MSG_METERINGMODE).append(ms);
            Log.w(TAG, STRBUILD.toString());
        }
        setImageDrawable(d);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        return !MeteringController.getInstance().isUnavailableSceneFactor(null);
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
            Log.i(TAG, STRBUILD.replace(0, STRBUILD.length(), LOG_RESOURCE_NOT_FOUND).append(mode).toString());
            return null;
        }
    }
}
