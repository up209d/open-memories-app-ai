package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class ISOSensitivityView extends DigitView {
    public static final String AUTO_ISO_SENSITIVITY_STRING = "AUTO";
    private static final String ISO_ICON = "iso_icon";
    private static final String LOWER_LINE = "lower_line";
    private static final HashMap<String, Integer> RESID_DICTIONARY_EXPANDED_ISO_LINE_EVF;
    private static final HashMap<String, Integer> RESID_DICTIONARY_EXPANDED_ISO_LINE_PANEL = new HashMap<>();
    private static final String UPPER_LINE = "upper_line";
    private final String FORMAT;
    Drawable mIcon;
    private int mIconVTopOffset;
    private ISOSensitivityListener mIsoListener;
    private String mIsoValue;
    private int mLine2DigitsH;
    private int mLine2DigitsW;
    private int mLine3DigitsH;
    private int mLine3DigitsW;
    private int mLowerLineXOffset;
    private int mLowerLineYOffset;
    private int mUpperLineXOffset;
    private int mUpperLineYOffset;

    static {
        RESID_DICTIONARY_EXPANDED_ISO_LINE_PANEL.put(UPPER_LINE, 17306072);
        RESID_DICTIONARY_EXPANDED_ISO_LINE_PANEL.put(LOWER_LINE, 17306072);
        RESID_DICTIONARY_EXPANDED_ISO_LINE_PANEL.put(ISO_ICON, Integer.valueOf(R.drawable.ic_popup_sync_5));
        RESID_DICTIONARY_EXPANDED_ISO_LINE_EVF = new HashMap<>();
        RESID_DICTIONARY_EXPANDED_ISO_LINE_EVF.put(UPPER_LINE, 17306080);
        RESID_DICTIONARY_EXPANDED_ISO_LINE_EVF.put(LOWER_LINE, 17306080);
        RESID_DICTIONARY_EXPANDED_ISO_LINE_EVF.put(ISO_ICON, 17305836);
    }

    public ISOSensitivityView(Context context) {
        this(context, null);
    }

    public ISOSensitivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIconVTopOffset = 0;
        this.mUpperLineXOffset = 0;
        this.mUpperLineYOffset = 0;
        this.mLowerLineXOffset = 0;
        this.mLowerLineYOffset = 0;
        this.mLine2DigitsW = 0;
        this.mLine2DigitsH = 0;
        this.mLine3DigitsW = 0;
        this.mLine3DigitsH = 0;
        this.FORMAT = getResources().getString(17043034);
        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{R.attr.src});
        this.mIcon = a.getDrawable(a.getIndex(0));
        a.recycle();
        TypedArray attr = context.obtainStyledAttributes(attrs, com.sony.imaging.app.base.R.styleable.ISO);
        int iconHeight = attr.getDimensionPixelSize(8, 0);
        int iconWidth = attr.getDimensionPixelSize(7, 0);
        this.mIconVTopOffset = attr.getDimensionPixelSize(5, 0);
        this.mUpperLineXOffset = attr.getDimensionPixelSize(11, 0);
        this.mUpperLineYOffset = attr.getDimensionPixelSize(12, 0);
        this.mLowerLineXOffset = attr.getDimensionPixelSize(13, 0);
        this.mLowerLineYOffset = attr.getDimensionPixelSize(14, 0);
        this.mLine2DigitsW = attr.getDimensionPixelSize(15, 0);
        this.mLine2DigitsH = attr.getDimensionPixelSize(16, 0);
        this.mLine3DigitsW = attr.getDimensionPixelSize(17, 0);
        this.mLine3DigitsH = attr.getDimensionPixelSize(18, 0);
        this.mIcon.setBounds(0, this.mIconVTopOffset, iconWidth, this.mIconVTopOffset + iconHeight);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mIsoListener == null) {
            this.mIsoListener = new ISOSensitivityListener();
        }
        return this.mIsoListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        setImage(this.mIcon);
        ISOSensitivityController iso = ISOSensitivityController.getInstance();
        String value = iso.getValue(ISOSensitivityController.MENU_ITEM_ID_ISO);
        if (value.equals(ISOSensitivityController.ISO_AUTO)) {
            if (AELController.getInstance().isAELock()) {
                value = iso.getAutoValue(ISOSensitivityController.MENU_ITEM_ID_ISO);
                if (value.equals(ISOSensitivityController.ISO_AUTO)) {
                    value = AUTO_ISO_SENSITIVITY_STRING;
                }
            } else {
                value = AUTO_ISO_SENSITIVITY_STRING;
            }
        }
        this.mIsoValue = value;
        if (value.equals(ISOSensitivityController.ISO_50Expanded)) {
            value = ISOSensitivityController.ISO_50;
        } else if (value.equals(ISOSensitivityController.ISO_64Expanded)) {
            value = ISOSensitivityController.ISO_64;
        } else if (value.equals(ISOSensitivityController.ISO_80Expanded)) {
            value = ISOSensitivityController.ISO_80;
        } else if (value.equals(ISOSensitivityController.ISO_100Expanded)) {
            value = ISOSensitivityController.ISO_100;
        }
        setValue(String.format(this.FORMAT, value));
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int digits = 0;
        if (this.mIsoValue.equals(ISOSensitivityController.ISO_50Expanded)) {
            digits = 2;
        } else if (this.mIsoValue.equals(ISOSensitivityController.ISO_64Expanded)) {
            digits = 2;
        } else if (this.mIsoValue.equals(ISOSensitivityController.ISO_80Expanded)) {
            digits = 2;
        } else if (this.mIsoValue.equals(ISOSensitivityController.ISO_100Expanded)) {
            digits = 3;
        }
        if (digits != 0) {
            int device = DisplayModeObserver.getInstance().getActiveDevice();
            HashMap<String, Integer> RESID_DICTIONARY = getHashMap(device);
            if (RESID_DICTIONARY != null) {
                Integer resId = RESID_DICTIONARY.get(UPPER_LINE);
                Bitmap upperBitmap = BitmapFactory.decodeResource(getResources(), resId.intValue());
                Integer resId2 = RESID_DICTIONARY.get(LOWER_LINE);
                Bitmap lowerBitmap = BitmapFactory.decodeResource(getResources(), resId2.intValue());
                if (upperBitmap != null && lowerBitmap != null) {
                    NinePatchDrawable upperImg = new NinePatchDrawable(upperBitmap, upperBitmap.getNinePatchChunk(), null, null);
                    NinePatchDrawable lowerImg = new NinePatchDrawable(lowerBitmap, lowerBitmap.getNinePatchChunk(), null, null);
                    Rect upperSize = null;
                    Rect lowerSize = null;
                    if (digits == 2) {
                        upperSize = new Rect(this.mUpperLineXOffset, this.mUpperLineYOffset, this.mUpperLineXOffset + this.mLine2DigitsW, this.mUpperLineYOffset + this.mLine2DigitsH);
                        lowerSize = new Rect(this.mLowerLineXOffset, this.mLowerLineYOffset, this.mLowerLineXOffset + this.mLine2DigitsW, this.mLowerLineYOffset + this.mLine2DigitsH);
                    } else if (digits == 3) {
                        upperSize = new Rect(this.mUpperLineXOffset, this.mUpperLineYOffset, this.mUpperLineXOffset + this.mLine3DigitsW, this.mUpperLineYOffset + this.mLine3DigitsH);
                        lowerSize = new Rect(this.mLowerLineXOffset, this.mLowerLineYOffset, this.mLowerLineXOffset + this.mLine3DigitsW, this.mLowerLineYOffset + this.mLine3DigitsH);
                    }
                    if (upperSize != null && lowerSize != null) {
                        upperImg.setBounds(upperSize);
                        lowerImg.setBounds(lowerSize);
                        upperImg.draw(canvas);
                        lowerImg.draw(canvas);
                    }
                }
            }
        }
        canvas.restore();
    }

    protected HashMap<String, Integer> getHashMap(int device) {
        if (device == 0 || device == 2) {
            HashMap<String, Integer> hash = RESID_DICTIONARY_EXPANDED_ISO_LINE_PANEL;
            return hash;
        }
        if (device != 1) {
            return null;
        }
        HashMap<String, Integer> hash2 = RESID_DICTIONARY_EXPANDED_ISO_LINE_EVF;
        return hash2;
    }

    /* loaded from: classes.dex */
    class ISOSensitivityListener implements NotificationListener {
        private final String[] TAGS = {CameraNotificationManager.ISO_SENSITIVITY, CameraNotificationManager.ISO_SENSITIVITY_AUTO, CameraNotificationManager.AE_LOCK, CameraNotificationManager.REC_MODE_CHANGED};

        ISOSensitivityListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(CameraNotificationManager.ISO_SENSITIVITY) || tag.equals(CameraNotificationManager.ISO_SENSITIVITY_AUTO) || tag.equals(CameraNotificationManager.AE_LOCK) || tag.equals(CameraNotificationManager.REC_MODE_CHANGED)) {
                ISOSensitivityView.this.refresh();
            }
        }
    }
}
