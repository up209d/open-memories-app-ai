package com.sony.imaging.app.pictureeffectplus.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.NotificationTag;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class PictureEffectPlusIconBackground extends ActiveImage {
    private static final String PICTUREEFFECTPLUS = "ApplicationTop";
    private static final String TAG = "PictureEffectPlusIconBackground";
    private static ArrayList<String> USE_BACKGROUND = new ArrayList<>();
    String itemId;
    private ActiveImage.ActiveImageListener mListener;

    static {
        USE_BACKGROUND.add(PictureEffectPlusController.MODE_MINIATURE_PLUS);
        USE_BACKGROUND.add(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS);
        USE_BACKGROUND.add(PictureEffectPlusController.MODE_PART_COLOR_PLUS);
    }

    public PictureEffectPlusIconBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVisibility(4);
        Log.i(TAG, "PictureEffectPlusIconBackground constructor");
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.pictureeffectplus.shooting.widget.PictureEffectPlusIconBackground.1
                private final String[] TAGS = {NotificationTag.PICTURE_EFFECT_PLUS_CHANGE, CameraNotificationManager.PICTURE_EFFECT_CHANGE};

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    super.onNotify(tag);
                    PictureEffectPlusIconBackground.this.refresh();
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return this.TAGS;
                }
            };
        }
        Log.i(TAG, "getNotificationListener");
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        String mode = PictureEffectPlusController.getInstance().getValue("ApplicationTop");
        if (USE_BACKGROUND.contains(mode)) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }
}
