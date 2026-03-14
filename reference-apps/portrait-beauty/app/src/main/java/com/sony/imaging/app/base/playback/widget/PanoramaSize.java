package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class PanoramaSize extends IconFileInfo {
    private static final int PBPANORAMASIZE_16_9_WIDTH = 1920;
    private static final int PBPANORAMASIZE_16_9_HEIGHT = 1080;
    private static final int[][] STD_SIZE_TBL = {new int[]{8192, 1856}, new int[]{3872, 2160}, new int[]{4912, PBPANORAMASIZE_16_9_HEIGHT}};
    private static final int[][] WIDE_SIZE_TBL = {new int[]{12416, 1856}, new int[]{5536, 2160}, new int[]{7152, PBPANORAMASIZE_16_9_HEIGHT}};

    public PanoramaSize(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        long imgWidth = -1;
        long imgHeight = -1;
        int contentType = 0;
        if (info != null) {
            imgWidth = info.getLong("ImageWidth");
            imgHeight = info.getLong("ImageLength");
            contentType = info.getInt("ContentType");
        }
        setValue(contentType, imgWidth, imgHeight);
    }

    public void setValue(int contentType, long imgWidth, long imgHeight) {
        boolean isDisplay = false;
        switch (contentType) {
            case 9:
            case 12:
                for (int i = 0; i < STD_SIZE_TBL.length; i++) {
                    if (imgWidth == STD_SIZE_TBL[i][0] && imgHeight == STD_SIZE_TBL[i][1]) {
                        setImageResource(R.drawable.immersive_cling_bg_circ);
                        isDisplay = true;
                    }
                }
                for (int i2 = 0; i2 < WIDE_SIZE_TBL.length; i2++) {
                    if (imgWidth == WIDE_SIZE_TBL[i2][0] && imgHeight == WIDE_SIZE_TBL[i2][1]) {
                        setImageResource(R.drawable.input_extract_action_bg_normal_material_dark);
                        isDisplay = true;
                    }
                }
                break;
            case 11:
                if (imgWidth == 1920 && imgHeight == 1080) {
                    setImageResource(R.drawable.item_background_borderless_material_dark);
                    isDisplay = true;
                    break;
                }
                break;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
