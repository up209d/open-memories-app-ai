package com.sony.imaging.app.pictureeffectplus.playback.layout;

import com.sony.imaging.app.base.playback.layout.EditorProcessingLayoutBase;
import com.sony.imaging.app.pictureeffectplus.R;

/* loaded from: classes.dex */
public class DeleteMultipleProcessingLayout extends EditorProcessingLayoutBase {
    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.pb_layout_cmn_singlepb_image_noinfo;
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorProcessingLayoutBase
    protected int getProgressBarResource() {
        return R.id.executing_progress;
    }
}
