package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class CreativeStyleIcon extends ActiveImage {
    private static final HashMap<String, Integer> RESID_DICTIONARY_CS_MODE = new HashMap<>();
    private static final HashMap<String, Integer> RESID_DICTIONARY_CS_VALUE;
    private static final String TAG = "CreativeStyleIcon";
    private int mAlpha;
    private int mContrastOffsetX;
    private int mContrastOffsetY;
    private ActiveImage.ActiveImageListener mListener;
    private int mSaturationOffsetX;
    private int mSaturationOffsetY;
    private int mSharpnessOffsetX;
    private int mSharpnessOffsetY;
    private TypedArray mTypedArray;

    static {
        RESID_DICTIONARY_CS_MODE.put("standard", 6);
        RESID_DICTIONARY_CS_MODE.put("vivid", 7);
        RESID_DICTIONARY_CS_MODE.put(CreativeStyleController.NEUTRAL, 8);
        RESID_DICTIONARY_CS_MODE.put("clear", 9);
        RESID_DICTIONARY_CS_MODE.put(CreativeStyleController.DEEP, 10);
        RESID_DICTIONARY_CS_MODE.put(CreativeStyleController.LIGHT, 11);
        RESID_DICTIONARY_CS_MODE.put("portrait", 12);
        RESID_DICTIONARY_CS_MODE.put("landscape", 13);
        RESID_DICTIONARY_CS_MODE.put("sunset", 14);
        RESID_DICTIONARY_CS_MODE.put(CreativeStyleController.NIGHT, 15);
        RESID_DICTIONARY_CS_MODE.put(CreativeStyleController.RED_LEAVES, 16);
        RESID_DICTIONARY_CS_MODE.put("mono", 17);
        RESID_DICTIONARY_CS_MODE.put(CreativeStyleController.SEPIA, 18);
        RESID_DICTIONARY_CS_VALUE = new HashMap<>();
        RESID_DICTIONARY_CS_VALUE.put("-3", 25);
        RESID_DICTIONARY_CS_VALUE.put("-2", 24);
        RESID_DICTIONARY_CS_VALUE.put("-1", 23);
        RESID_DICTIONARY_CS_VALUE.put(ISOSensitivityController.ISO_AUTO, 22);
        RESID_DICTIONARY_CS_VALUE.put("1", 21);
        RESID_DICTIONARY_CS_VALUE.put("2", 20);
        RESID_DICTIONARY_CS_VALUE.put("3", 19);
    }

    public CreativeStyleIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContrastOffsetX = 0;
        this.mContrastOffsetY = 0;
        this.mSaturationOffsetX = 0;
        this.mSaturationOffsetY = 0;
        this.mSharpnessOffsetX = 0;
        this.mSharpnessOffsetY = 0;
        this.mAlpha = BatteryIcon.BATTERY_STATUS_CHARGING;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CreativeStyle);
        this.mContrastOffsetX = this.mTypedArray.getDimensionPixelSize(0, 0);
        this.mContrastOffsetY = this.mTypedArray.getDimensionPixelSize(1, 0);
        this.mSaturationOffsetX = this.mTypedArray.getDimensionPixelSize(2, 0);
        this.mSaturationOffsetY = this.mTypedArray.getDimensionPixelSize(3, 0);
        this.mSharpnessOffsetX = this.mTypedArray.getDimensionPixelSize(4, 0);
        this.mSharpnessOffsetY = this.mTypedArray.getDimensionPixelSize(5, 0);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.CreativeStyleIcon.1
                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.CREATIVE_STYLE_CHANGE.equals(tag)) {
                        CreativeStyleIcon.this.refresh();
                    } else {
                        super.onNotify(tag);
                    }
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return new String[]{CameraNotificationManager.CREATIVE_STYLE_CHANGE};
                }
            };
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        CreativeStyleController ctrl = CreativeStyleController.getInstance();
        List<String> supported = ctrl.getSupportedValue(CreativeStyleController.CREATIVESTYLE);
        if (supported == null || supported.size() == 0) {
            return false;
        }
        return !ctrl.isUnavailableSceneFactor(CreativeStyleController.CREATIVESTYLE);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        Drawable style = null;
        CreativeStyleController ctrl = CreativeStyleController.getInstance();
        String csMode = ctrl.getValue(CreativeStyleController.CREATIVESTYLE);
        Integer resId = RESID_DICTIONARY_CS_MODE.get(csMode);
        if (resId != null) {
            style = this.mTypedArray.getDrawable(resId.intValue());
        } else {
            Log.e(TAG, "Invalid value of CreativeStyleSetting Mode");
        }
        setImageDrawable(style);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        Bitmap contrast = null;
        Bitmap saturation = null;
        Bitmap sharpness = null;
        CreativeStyleController ctrl = CreativeStyleController.getInstance();
        String csMode = ctrl.getValue(CreativeStyleController.CREATIVESTYLE);
        CreativeStyleController.CreativeStyleOptions csValue = (CreativeStyleController.CreativeStyleOptions) ctrl.getDetailValue();
        CreativeStyleController.CreativeStyleOptionsAvailable optionAvailable = (CreativeStyleController.CreativeStyleOptionsAvailable) ctrl.getOptionAvailable(csMode);
        Integer resId = RESID_DICTIONARY_CS_VALUE.get(String.valueOf(csValue.contrast));
        if (resId != null && optionAvailable.mContrast) {
            Drawable option = this.mTypedArray.getDrawable(resId.intValue());
            contrast = ((BitmapDrawable) option).getBitmap();
        }
        Integer resId2 = RESID_DICTIONARY_CS_VALUE.get(String.valueOf(csValue.saturation));
        if (resId2 != null && optionAvailable.mSaturation) {
            Drawable option2 = this.mTypedArray.getDrawable(resId2.intValue());
            saturation = ((BitmapDrawable) option2).getBitmap();
        }
        Integer resId3 = RESID_DICTIONARY_CS_VALUE.get(String.valueOf(csValue.sharpness));
        if (resId3 != null && optionAvailable.mSharpness) {
            Drawable option3 = this.mTypedArray.getDrawable(resId3.intValue());
            sharpness = ((BitmapDrawable) option3).getBitmap();
        }
        if ((contrast == null && optionAvailable.mContrast) || ((saturation == null && optionAvailable.mSaturation) || (sharpness == null && optionAvailable.mSharpness))) {
            Log.e(TAG, "Invalid value of CreativeStyleSetting Value");
        }
        Paint paint = new Paint();
        paint.setAlpha(this.mAlpha);
        if (contrast != null) {
            canvas.drawBitmap(contrast, this.mContrastOffsetX, this.mContrastOffsetY, paint);
        }
        if (saturation != null) {
            canvas.drawBitmap(saturation, this.mSaturationOffsetX, this.mSaturationOffsetY, paint);
        }
        if (sharpness != null) {
            canvas.drawBitmap(sharpness, this.mSharpnessOffsetX, this.mSharpnessOffsetY, paint);
        }
        canvas.restore();
    }

    @Override // android.widget.ImageView
    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
        super.setAlpha(alpha);
    }
}
