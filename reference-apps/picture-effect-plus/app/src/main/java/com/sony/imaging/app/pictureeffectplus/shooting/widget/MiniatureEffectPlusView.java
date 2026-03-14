package com.sony.imaging.app.pictureeffectplus.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.widget.MiniatureEffectView;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.NotificationTag;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class MiniatureEffectPlusView extends MiniatureEffectView {
    private static final String NOT_DISPLAY = "notdisplay";
    protected static final StringBuilder STRBUILD = new StringBuilder();
    private static final String TAG = "MiniatureEffectPlusView";
    protected String FUNC_NAME;
    NotificationListener mPictureEffectPlusChangeListener;

    public MiniatureEffectPlusView(Context context) {
        super(context);
        this.FUNC_NAME = "";
        this.mPictureEffectPlusChangeListener = new NotificationListener() { // from class: com.sony.imaging.app.pictureeffectplus.shooting.widget.MiniatureEffectPlusView.1
            private final String[] TAGS = {NotificationTag.PICTURE_EFFECT_PLUS_CHANGE};

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                MiniatureEffectPlusView.this.refresh();
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAGS;
            }
        };
    }

    public MiniatureEffectPlusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.FUNC_NAME = "";
        this.mPictureEffectPlusChangeListener = new NotificationListener() { // from class: com.sony.imaging.app.pictureeffectplus.shooting.widget.MiniatureEffectPlusView.1
            private final String[] TAGS = {NotificationTag.PICTURE_EFFECT_PLUS_CHANGE};

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                MiniatureEffectPlusView.this.refresh();
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAGS;
            }
        };
    }

    @Override // com.sony.imaging.app.base.shooting.widget.MiniatureEffectView
    protected String getFocusPosition() {
        String focusPosition = NOT_DISPLAY;
        PictureEffectPlusController cntl = PictureEffectPlusController.getInstance();
        int miniStatus = cntl.getMiniatureStatus();
        if (miniStatus != 0) {
            focusPosition = cntl.getMiniatureFocusArea();
            if (focusPosition.equals("auto")) {
                focusPosition = NOT_DISPLAY;
            }
        }
        this.FUNC_NAME = "getFocusPosition";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return focusPosition;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.MiniatureEffectView, com.sony.imaging.app.base.shooting.widget.SimulatingEERelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        addCameraNotificationListener(this.mPictureEffectPlusChangeListener);
        this.FUNC_NAME = "onAttachedToWindow";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.MiniatureEffectView, com.sony.imaging.app.base.shooting.widget.SimulatingEERelativeLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCameraNotificationListener(this.mPictureEffectPlusChangeListener);
        this.FUNC_NAME = "onDetachedFromWindow";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
    }
}
