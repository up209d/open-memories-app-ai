package com.sony.imaging.app.smoothreflection.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.widget.StillImageSizeIcon;
import com.sony.imaging.app.smoothreflection.R;
import com.sony.imaging.app.smoothreflection.common.SaUtil;
import com.sony.imaging.app.smoothreflection.shooting.SmoothReflectionCompositProcess;

/* loaded from: classes.dex */
public class SmoothReflectionStillImageSizeIcon extends StillImageSizeIcon {
    public SmoothReflectionStillImageSizeIcon(Context context) {
        this(context, null);
    }

    public SmoothReflectionStillImageSizeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.StillImageSizeIcon);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.StillImageSizeIcon, com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        Integer id;
        Log.d("CallCheck", "SmoothReflection StillImageSizeIcon refresh is called!!!!!");
        int resId = 0;
        try {
            String imagesize = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
            String quality = PictureQualityController.getInstance().getValue(null);
            if (!PictureQualityController.PICTURE_QUALITY_RAW.equals(quality) && (id = ICONS.get(imagesize)) != null) {
                if (SaUtil.isAVIP() && SmoothReflectionCompositProcess.fakeicon_flg) {
                    if (SmoothReflectionCompositProcess.keep_picture_size.equals(PictureSizeController.SIZE_L)) {
                        id = 0;
                    } else if (SmoothReflectionCompositProcess.keep_picture_size.equals(PictureSizeController.SIZE_M)) {
                        id = 1;
                    } else if (SmoothReflectionCompositProcess.keep_picture_size.equals(PictureSizeController.SIZE_S)) {
                        id = 2;
                    }
                }
                resId = this.mTypedArray.getResourceId(id.intValue(), 17305977);
            }
            if (resId == 0) {
                if (this.mFnMode) {
                    resId = 17303951;
                }
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        setImageResource(resId);
    }
}
