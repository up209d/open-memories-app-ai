package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.contents.AVIndexContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.shooting.widget.NumberImage;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class PictureSize extends NumberImage implements NotificationListener {
    private static final char CHAR_DOT = '.';
    private static final char CHAR_M = 'm';
    private static final char CHAR_VGA = 'v';
    private static final float F_MPIXEL = 1000000.0f;
    private static final int INDEX_DOT = 10;
    private static final int INDEX_M = 11;
    private static final int INDEX_VGA = 12;
    protected static final long LONG_NONE = -1;
    private static final String STR_FORMAT_DOUBLE_DIGIT = "%.0fm";
    private static final String STR_FORMAT_SINGLE_DIGIT = "%.1fm";
    private static final String STR_VGA = "v";
    private static final float TH_DOUBLE_DIGIT = 9.95f;
    private static final float TH_MIN = 0.05f;
    private static final int VGA_H = 480;
    private static final int VGA_W = 640;
    protected TypedArray mImageArray;
    protected final ContentsManager mMgr;
    protected final boolean mRequestNotification;

    public PictureSize(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.IconFileInfo);
        this.mRequestNotification = attr.getBoolean(0, true);
        attr.recycle();
        this.mMgr = ContentsManager.getInstance();
        setGravity(3);
    }

    public void setContentInfo(ContentInfo info) {
        int visibility = 4;
        if (info != null) {
            long imgWidth = info.getLong("ImageWidth");
            long imgHeight = info.getLong("ImageLength");
            int contentType = info.getInt("ContentType");
            int aspect = info.getInt("AspectRatio");
            if ((7 == contentType || 6 == contentType || 10 == contentType || 13 == contentType || 11 == contentType || 8 == contentType || 5 == contentType) && !AVIndexContentInfo.isPanorama(info)) {
                if (640 == imgWidth && 480 == imgHeight) {
                    makeImageFromString(STR_VGA, this.mImageArray);
                    visibility = 0;
                } else if (1 == aspect || 2 == aspect || 10 == aspect || aspect == 0) {
                    float fImageSize = ((float) (imgWidth * imgHeight)) / F_MPIXEL;
                    if (TH_MIN <= fImageSize) {
                        String value = TH_DOUBLE_DIGIT > fImageSize ? String.format(STR_FORMAT_SINGLE_DIGIT, Float.valueOf(fImageSize)) : String.format(STR_FORMAT_DOUBLE_DIGIT, Float.valueOf(fImageSize));
                        makeImageFromString(value, this.mImageArray);
                        visibility = 0;
                    }
                }
            }
        }
        setVisibility(visibility);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.NumberImage
    public int setNumber(char ch) {
        if ('m' == ch) {
            return 11;
        }
        if ('.' == ch) {
            return 10;
        }
        if ('v' == ch) {
            return 12;
        }
        int num = this.mImageArray.length();
        return num;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{ContentsManager.NOTIFICATION_TAG_CURRENT_FILE};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        setContentInfo((ContentInfo) this.mMgr.getValue(ContentsManager.NOTIFICATION_TAG_CURRENT_FILE));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mRequestNotification) {
            ContentsManager.getInstance().setNotificationListener(this);
            setVisibility(4);
        }
        Resources res = getResources();
        this.mImageArray = res.obtainTypedArray(R.array.OSD_IMAGE_SIZE);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        if (this.mImageArray != null) {
            this.mImageArray.recycle();
            this.mImageArray = null;
        }
        if (this.mRequestNotification) {
            ContentsManager.getInstance().removeNotificationListener(this);
        }
        super.onDetachedFromWindow();
    }
}
