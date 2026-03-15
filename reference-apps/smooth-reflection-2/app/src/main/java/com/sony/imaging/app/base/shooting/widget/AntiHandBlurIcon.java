package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.List;

/* loaded from: classes.dex */
public class AntiHandBlurIcon extends ActiveImage {
    private static final int DEFAULT_SETTIGN_TYPE = 0;
    private static final String TAG = "AntiHandBlurIcon";
    NotificationListener mNotificationListener;
    private TypedArray mTypedArray;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public enum SteaydShotType {
        STEADY_SHOT_OFF,
        STEADY_SHOT_ON_ONLY_STILL,
        STEADY_SHOT_ON_MOVIE_STILL,
        STEADY_SHOT_ON_ONLY_MOVIE,
        STEADY_SHOT_NOTHING,
        STEADY_SHOT_UNKNOWN
    }

    public AntiHandBlurIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNotificationListener = null;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.AntiHandBlurIcon);
        setVisibility(4);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        Drawable draw = null;
        SteaydShotType Type = getSteaydShotType();
        if (Type == SteaydShotType.STEADY_SHOT_OFF) {
            draw = this.mTypedArray.getDrawable(0);
        } else if (Type == SteaydShotType.STEADY_SHOT_ON_ONLY_STILL) {
            draw = this.mTypedArray.getDrawable(1);
        } else if (Type == SteaydShotType.STEADY_SHOT_ON_MOVIE_STILL) {
            draw = this.mTypedArray.getDrawable(2);
        } else if (Type == SteaydShotType.STEADY_SHOT_ON_ONLY_MOVIE) {
            draw = this.mTypedArray.getDrawable(3);
        }
        if (draw != null) {
            setVisibility(0);
            setImageDrawable(draw);
        } else {
            setVisibility(4);
        }
    }

    protected SteaydShotType getSteaydShotType() {
        SteaydShotType steaydShotType = SteaydShotType.STEADY_SHOT_UNKNOWN;
        if (!isMovieMode()) {
            SteaydShotType type = getStillSteaydShotType();
            return type;
        }
        if (CameraSetting.getPfApiVersion() >= 2) {
            SteaydShotType type2 = getMovieSteaydShotType();
            return type2;
        }
        SteaydShotType type3 = SteaydShotType.STEADY_SHOT_NOTHING;
        return type3;
    }

    protected SteaydShotType getStillSteaydShotType() {
        SteaydShotType type = SteaydShotType.STEADY_SHOT_UNKNOWN;
        int settingType = 0;
        if (CameraSetting.getPfApiVersion() >= 2 && (settingType = ScalarProperties.getInt("ui.steady.shot")) == -1) {
            return type;
        }
        List<String> supported = AntiHandBlurController.getInstance().getSupportedValue(AntiHandBlurController.STILL);
        if (supported != null && supported.size() > 0) {
            String value = AntiHandBlurController.getInstance().getValue(AntiHandBlurController.STILL);
            if ("off".equals(value)) {
                type = SteaydShotType.STEADY_SHOT_OFF;
            } else if (AntiHandBlurController.SHOOT.equals(value)) {
                if (CameraSetting.getPfApiVersion() >= 2) {
                    if (settingType == 0) {
                        type = SteaydShotType.STEADY_SHOT_ON_MOVIE_STILL;
                    } else if (settingType == 1) {
                        type = SteaydShotType.STEADY_SHOT_ON_ONLY_STILL;
                    }
                } else {
                    type = SteaydShotType.STEADY_SHOT_ON_ONLY_STILL;
                }
            }
        } else {
            type = SteaydShotType.STEADY_SHOT_NOTHING;
        }
        return type;
    }

    protected SteaydShotType getMovieSteaydShotType() {
        SteaydShotType type = SteaydShotType.STEADY_SHOT_UNKNOWN;
        int settingType = ScalarProperties.getInt("ui.steady.shot");
        if (settingType == -1) {
            return type;
        }
        List<String> supported = AntiHandBlurController.getInstance().getSupportedValue("movie");
        if (supported != null && supported.size() > 0) {
            String value = AntiHandBlurController.getInstance().getValue("movie");
            if ("off".equals(value)) {
                type = SteaydShotType.STEADY_SHOT_OFF;
            } else if (settingType == 0) {
                type = SteaydShotType.STEADY_SHOT_ON_MOVIE_STILL;
            } else if (settingType == 1) {
                type = SteaydShotType.STEADY_SHOT_ON_ONLY_MOVIE;
            }
        } else {
            type = SteaydShotType.STEADY_SHOT_NOTHING;
        }
        return type;
    }

    protected boolean isMovieMode() {
        return 2 == ExecutorCreator.getInstance().getRecordingMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public NotificationListener getNotificationListener() {
        if (this.mNotificationListener == null) {
            this.mNotificationListener = new NotificationListener();
        }
        return this.mNotificationListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class NotificationListener extends ActiveImage.ActiveImageListener {
        private final String[] TAGS;

        protected NotificationListener() {
            super(AntiHandBlurIcon.this);
            this.TAGS = new String[]{CameraNotificationManager.ANTI_HAND_BLUR, CameraNotificationManager.DEVICE_LENS_CHANGED};
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
        public String[] addTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.ANTI_HAND_BLUR.equals(tag) || CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                AntiHandBlurIcon.this.refresh();
            } else {
                super.onNotify(tag);
            }
        }
    }
}
