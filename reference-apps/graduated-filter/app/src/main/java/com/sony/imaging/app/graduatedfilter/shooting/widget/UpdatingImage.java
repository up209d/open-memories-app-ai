package com.sony.imaging.app.graduatedfilter.shooting.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class UpdatingImage extends ActiveImage {
    private static AnimationDrawable aDrawable;
    private final String TAG;
    private UpdatingChangedListener mUpdatingChangedListener;

    public UpdatingImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        this.mUpdatingChangedListener = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        aDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.progress_animation);
        setImageDrawable(aDrawable);
        aDrawable.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setImageDrawable(null);
        aDrawable.stop();
        aDrawable = null;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        if (GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
            setVisibility(4);
        } else {
            setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mUpdatingChangedListener == null) {
            this.mUpdatingChangedListener = new UpdatingChangedListener();
        }
        return this.mUpdatingChangedListener;
    }

    /* loaded from: classes.dex */
    class UpdatingChangedListener implements NotificationListener {
        private String[] TAGS = {GFConstants.CHECKE_UPDATE_STATUS};

        UpdatingChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (GFConstants.CHECKE_UPDATE_STATUS.equalsIgnoreCase(tag)) {
                UpdatingImage.this.refresh();
            }
        }
    }
}
