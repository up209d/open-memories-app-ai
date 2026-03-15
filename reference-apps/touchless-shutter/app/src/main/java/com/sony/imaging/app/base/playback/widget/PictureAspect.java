package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class PictureAspect extends IconFileInfo {
    private static final long AUTOREVIEW_ASPECT_16_9 = 2;
    private static final long AUTOREVIEW_ASPECT_1_1 = 3;
    private static final long AUTOREVIEW_ASPECT_3_2 = 1;
    private static final long AUTOREVIEW_ASPECT_4_3 = 0;

    public PictureAspect(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        int contentType = 0;
        int aspect = 3;
        if (info != null) {
            setVisibility(0);
            contentType = info.getInt("ContentType");
            aspect = info.getInt("AspectRatio");
        }
        setValue(aspect, contentType);
    }

    public void setValue(int imgAspect, int contentType) {
        boolean isDisplay;
        int resId;
        if (1 == contentType || 2 == contentType || 3 == contentType || 4 == contentType || 16 == contentType || 9 == contentType || 11 == contentType || 12 == contentType) {
            isDisplay = false;
        } else {
            switch (imgAspect) {
                case 0:
                    isDisplay = true;
                    resId = R.drawable.sym_keyboard_num0_no_plus;
                    break;
                case 1:
                    isDisplay = true;
                    resId = 17303924;
                    break;
                case 2:
                    isDisplay = true;
                    resId = 17306178;
                    break;
                case 10:
                    isDisplay = true;
                    resId = 17305632;
                    break;
                default:
                    isDisplay = false;
                    resId = -1;
                    break;
            }
            if (-1 != resId) {
                setImageResource(resId);
            }
        }
        setVisibility(isDisplay ? 0 : 4);
    }

    public void setAutoReviewValue(long imgAspect) {
        if (imgAspect == 2) {
            setValue(1, 5);
            return;
        }
        if (imgAspect == AUTOREVIEW_ASPECT_3_2) {
            setValue(10, 5);
        } else if (imgAspect == 0) {
            setValue(0, 5);
        } else if (imgAspect == 3) {
            setValue(2, 5);
        }
    }
}
