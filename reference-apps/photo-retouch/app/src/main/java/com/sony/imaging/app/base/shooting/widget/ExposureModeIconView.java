package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;
import java.util.HashMap;

/* loaded from: classes.dex */
public class ExposureModeIconView extends ActiveImage {
    private static final String LOG_RESOURCE_NOT_FOUND = "Resource is not found. mode = ";
    private static final String PTAG_DLAPP_BOOT = "DLApp Boot. draw ExposureModeIcon";
    private static final String TAG = "ExposureModeIconView";
    private static int TIMEOUT = 0;
    private static final int TIMEOUT_DEFAULT = 4000;
    private Runnable disappear;
    private Handler handler;
    Context mContext;
    private NotificationListener mSceneModeListener;
    private TypedArray mTypedArray;
    private boolean mWillDisappear;
    private static StringBuilder STRBUILD = new StringBuilder();
    private static final HashMap<String, Integer> ICON_IDS = new HashMap<>();

    static {
        ICON_IDS.put("portrait", 0);
        ICON_IDS.put(ExposureModeController.ANTI_MOTION_BLUR, 1);
        ICON_IDS.put(ExposureModeController.SPORTS, 2);
        ICON_IDS.put(ExposureModeController.PET, 3);
        ICON_IDS.put(ExposureModeController.GOURMET, 4);
        ICON_IDS.put(ExposureModeController.MACRO, 5);
        ICON_IDS.put("landscape", 6);
        ICON_IDS.put("sunset", 7);
        ICON_IDS.put("twilight", 8);
        ICON_IDS.put(ExposureModeController.TWILIGHT_PORTRAIT, 9);
        ICON_IDS.put(ExposureModeController.HAND_HELD_TWILIGHT, 10);
        ICON_IDS.put(ExposureModeController.FIREWORKS, 11);
        ICON_IDS.put(ExposureModeController.HIGH_SENSITIVITY, 12);
        ICON_IDS.put(ExposureModeController.BEACH, 16);
        ICON_IDS.put(ExposureModeController.SNOW, 17);
        ICON_IDS.put(ExposureModeController.BACK_LIGHT, 18);
        ICON_IDS.put(ExposureModeController.SOFT_SKIN, 19);
        ICON_IDS.put(ExposureModeController.UNDERWATER, 20);
        ICON_IDS.put(ExposureModeController.ADVANCE_SPORTS, 21);
        if (ExposureModeController.isMovieMainFeature()) {
            ICON_IDS.put(ExposureModeController.PROGRAM_AUTO_MODE, 33);
            ICON_IDS.put("Aperture", 34);
            ICON_IDS.put(ExposureModeController.SHUTTER_MODE, 35);
            ICON_IDS.put(ExposureModeController.MANUAL_MODE, 36);
        } else {
            ICON_IDS.put(ExposureModeController.PROGRAM_AUTO_MODE, 22);
            ICON_IDS.put("Aperture", 24);
            ICON_IDS.put(ExposureModeController.SHUTTER_MODE, 25);
            ICON_IDS.put(ExposureModeController.MANUAL_MODE, 26);
        }
        ICON_IDS.put(ExposureModeController.INTELLIGENT_AUTO_MODE, 27);
        ICON_IDS.put(ExposureModeController.MOVIE_PROGRAM_AUTO_MODE, 28);
        ICON_IDS.put(ExposureModeController.MOVIE_APERATURE_MODE, 29);
        ICON_IDS.put(ExposureModeController.MOVIE_SHUTTER_MODE, 30);
        ICON_IDS.put(ExposureModeController.MOVIE_MANUAL_MODE, 31);
        ICON_IDS.put(ExposureModeController.MOVIE_AUTO, 32);
        ICON_IDS.put(ExposureModeController.MOVIE_PORTRAIT, 37);
        ICON_IDS.put(ExposureModeController.MOVIE_LANDSCAPE, 38);
        ICON_IDS.put(ExposureModeController.MOVIE_TWILIGHT, 39);
        ICON_IDS.put(ExposureModeController.MOVIE_BEACH, 40);
        ICON_IDS.put(ExposureModeController.MOVIE_SNOW, 41);
        ICON_IDS.put(ExposureModeController.MOVIE_FIREWORKS, 42);
        ICON_IDS.put(ExposureModeController.MOVIE_HIGH_SENSITIVITY, 43);
        TIMEOUT = TIMEOUT_DEFAULT;
    }

    public ExposureModeIconView(Context context) {
        super(context);
        this.handler = new Handler();
        this.disappear = new Runnable() { // from class: com.sony.imaging.app.base.shooting.widget.ExposureModeIconView.1
            @Override // java.lang.Runnable
            public void run() {
                ExposureModeIconView.this.setVisibility(4);
            }
        };
    }

    public ExposureModeIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.handler = new Handler();
        this.disappear = new Runnable() { // from class: com.sony.imaging.app.base.shooting.widget.ExposureModeIconView.1
            @Override // java.lang.Runnable
            public void run() {
                ExposureModeIconView.this.setVisibility(4);
            }
        };
        this.mContext = context;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ExposureModeIconView);
    }

    public void setAutoDisappear(boolean b) {
        this.mWillDisappear = b;
        setVisibility(0);
        this.handler.removeCallbacks(this.disappear);
        if (b) {
            this.handler.postDelayed(this.disappear, TIMEOUT);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        PTag.end(PTAG_DLAPP_BOOT);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mWillDisappear) {
            this.handler.removeCallbacks(this.disappear);
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.mWillDisappear) {
            this.handler.removeCallbacks(this.disappear);
            this.handler.postDelayed(this.disappear, TIMEOUT);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mSceneModeListener == null) {
            this.mSceneModeListener = new ExpModeIconNotificationListener();
        }
        return this.mSceneModeListener;
    }

    /* loaded from: classes.dex */
    protected class ExpModeIconNotificationListener extends ActiveImage.ActiveImageListener {
        private final String[] tags;

        protected ExpModeIconNotificationListener() {
            super();
            this.tags = new String[]{CameraNotificationManager.PROGRAM_LINE_CHANGE};
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
        public String[] addTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.SCENE_MODE.equals(tag)) {
                ExposureModeIconView.this.refresh();
            } else {
                if (CameraNotificationManager.PROGRAM_LINE_CHANGE.equals(tag)) {
                    if (ExposureModeIconView.this.getVisibility() == 0) {
                        ExposureModeIconView.this.updateIcon();
                        return;
                    }
                    return;
                }
                super.onNotify(tag);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        updateIcon();
        if (this.mWillDisappear) {
            setVisibility(0);
            this.handler.removeCallbacks(this.disappear);
            this.handler.postDelayed(this.disappear, TIMEOUT);
        }
    }

    protected void updateIcon() {
        String value = ExposureModeController.getInstance().getValue(null);
        if (value != null) {
            Drawable d = obtainDrawable(value);
            setImageDrawable(d);
        }
    }

    private Drawable obtainDrawable(String mode) {
        Integer id = ICON_IDS.get(mode);
        if (mode.equals(ExposureModeController.PROGRAM_AUTO_MODE)) {
            boolean pshift = CameraSetting.getInstance().isProgramLineAdjusted();
            if (pshift) {
                id = 23;
            }
        }
        if (id == null) {
            return null;
        }
        try {
            Drawable d = this.mTypedArray.getDrawable(id.intValue());
            return d;
        } catch (Resources.NotFoundException e) {
            Log.i(TAG, STRBUILD.replace(0, STRBUILD.length(), LOG_RESOURCE_NOT_FOUND).append(mode).toString());
            return null;
        }
    }
}
