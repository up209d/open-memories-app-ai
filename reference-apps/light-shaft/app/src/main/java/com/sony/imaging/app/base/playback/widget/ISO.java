package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class ISO extends LabelFixedFontFileInfo {
    private static final int MAX_DISPLAY_VALUE = 999999;
    private static final String MAX_TAG_ISO = "65535";
    private static final String MSG_VALUE_IS_NOT_NUMBER = "Iso value is not number : ";
    private static final String MSG_VALUE_TOO_LARGE = "Iso value is too large to display : ";
    private static final Long MULTISHOT_NR = 1L;
    private static final String TAG = "ISO";
    private String mISO;
    private int mIconHeight;
    private int mIconMarginLeft;
    private int mIconMarginTop;
    private int mIconWidth;
    private boolean mIsMultiShot;

    public ISO(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mISO = null;
        this.mIsMultiShot = false;
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ISO);
        this.mIconWidth = attr.getDimensionPixelSize(7, 0);
        this.mIconHeight = attr.getDimensionPixelSize(8, 0);
        this.mIconMarginLeft = attr.getDimensionPixelSize(9, 0);
        this.mIconMarginTop = attr.getDimensionPixelSize(10, 0);
        int isoTxtWidth = attr.getDimensionPixelSize(0, 0);
        int isoTxtHeight = attr.getDimensionPixelSize(1, 0);
        int isoTxtMarginLeft = attr.getDimensionPixelSize(2, 0);
        int isoTxtMarginTop = attr.getDimensionPixelSize(3, 0);
        Drawable icon = getResources().getDrawable(android.R.drawable.ic_popup_sync_5);
        icon.setBounds(this.mIconMarginLeft, this.mIconMarginTop, this.mIconWidth, this.mIconHeight);
        setCompoundDrawables(icon, null, null, null);
        RelativeLayout.LayoutParams paramsTextView = new RelativeLayout.LayoutParams(isoTxtWidth, isoTxtHeight);
        paramsTextView.leftMargin = isoTxtMarginLeft;
        paramsTextView.topMargin = isoTxtMarginTop;
        setLayoutParams(paramsTextView);
    }

    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo
    public void setContentInfo(ContentInfo info) {
        String iso = null;
        boolean isMultiShot = false;
        if (info != null) {
            iso = info.getString("ISOSpeed");
            if (iso == null) {
                iso = info.getString("ISOSpeedRatings");
                if (MAX_TAG_ISO.equals(iso)) {
                    iso = null;
                }
            }
            isMultiShot = MULTISHOT_NR.longValue() == info.getLong("MkNoteMultiFrameNR");
        }
        setValue(iso, isMultiShot);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mISO = null;
        if (this.mIsMultiShot) {
            Drawable icon = getResources().getDrawable(android.R.drawable.ic_popup_sync_5);
            icon.setBounds(this.mIconMarginLeft, this.mIconMarginTop, this.mIconWidth, this.mIconHeight);
            setCompoundDrawables(icon, null, null, null);
        }
        this.mIsMultiShot = false;
        super.onAttachedToWindow();
    }

    public void setValue(String iso, boolean isMultishot) {
        try {
            int int_iso = Integer.parseInt(iso);
            if (MAX_DISPLAY_VALUE < int_iso) {
                Log.w(TAG, MSG_VALUE_TOO_LARGE + iso);
                iso = null;
            }
        } catch (Exception e) {
            Log.w(TAG, MSG_VALUE_IS_NOT_NUMBER + iso);
            iso = null;
        }
        if (iso != null) {
            if (!iso.equals(this.mISO)) {
                setText(String.format(getResources().getString(android.R.string.config_appsAuthorizedForSharedAccounts), Integer.valueOf(Integer.parseInt(iso))));
            }
            if (this.mIsMultiShot != isMultishot) {
                Drawable icon = getResources().getDrawable(isMultishot ? 17303904 : android.R.drawable.ic_popup_sync_5);
                icon.setBounds(this.mIconMarginLeft, this.mIconMarginTop, this.mIconWidth, this.mIconHeight);
                setCompoundDrawables(icon, null, null, null);
            }
            setVisibility(0);
        } else {
            setVisibility(4);
        }
        this.mISO = iso;
        this.mIsMultiShot = isMultishot;
    }

    public void setValue(long iso) {
        setValue(String.valueOf(iso), false);
    }
}
