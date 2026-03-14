package com.sony.imaging.app.digitalfilter.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ShadingLevel extends DigitView {
    private ShadingLevelChangeListener mShadingLevelListener;

    public ShadingLevel(Context context) {
        this(context, null);
    }

    public ShadingLevel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mShadingLevelListener == null) {
            this.mShadingLevelListener = new ShadingLevelChangeListener();
        }
        return this.mShadingLevelListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        int borderId = GFCommonUtil.getInstance().getBorderId();
        int shadingLevel = GFCommonUtil.getInstance().getStrength(borderId) + 1;
        if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            shadingLevel -= 9;
        }
        setValue(GFCommonUtil.getInstance().getSignedInteger(shadingLevel));
    }

    /* loaded from: classes.dex */
    class ShadingLevelChangeListener implements NotificationListener {
        private String[] TAGS = {GFConstants.SHADING_LEVEL_CHANGE, GFConstants.UPDATE_APPSETTING};

        ShadingLevelChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            ShadingLevel.this.refresh();
        }
    }
}
