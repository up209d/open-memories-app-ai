package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.StillFolderViewMode;

/* loaded from: classes.dex */
public class ViewModeIcon extends IconFileInfo {
    public ViewModeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo, com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{ContentsManager.NOTIFICATION_TAG_CURRENT_FILE};
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        Class<?> viewMode = this.mMgr.getViewMode();
        setValue(viewMode);
    }

    public void setValue(Class<?> viewMode) {
        boolean isDisplay = false;
        if (viewMode != null && viewMode.equals(StillFolderViewMode.class)) {
            setImageResource(R.drawable.ic_contact_picture_2);
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
