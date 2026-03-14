package com.sony.imaging.app.digitalfilter.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveText;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class UpdatingText extends ActiveText {
    private final String TAG;
    private UpdatingChangedListener mUpdatingChangedListener;

    public UpdatingText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        this.mUpdatingChangedListener = null;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        if (GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
            setVisibility(4);
        } else {
            setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
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
                UpdatingText.this.refresh();
            }
        }
    }
}
