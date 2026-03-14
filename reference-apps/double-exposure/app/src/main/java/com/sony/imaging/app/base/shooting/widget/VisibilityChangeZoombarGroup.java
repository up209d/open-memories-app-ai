package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public class VisibilityChangeZoombarGroup extends AbstractZoombarRefresh {
    private static final String ERROR = "error";
    private static final String INFO_ON = "info_on";
    private static final String INFO_SECOND = "info_second";
    private static final String SETTING = "setting";
    private static final String TAG = VisibilityChangeZoombarGroup.class.getSimpleName();
    private static final String ZOOM_BAR = "zoom_bar";
    private VisibilityChangeZoombarGroup mError;
    private VisibilityChangeZoombarGroup mInfoOn;
    private VisibilityChangeZoombarGroup mInfoSecond;
    private VisibilityChangeZoombarGroup mSetting;
    private TypedArray mTypedArray;
    protected int mWatchTarget;
    private VisibilityChangeZoombarGroup mZoomBar;
    private String mZoomBarGroupName;

    public VisibilityChangeZoombarGroup(Context context) {
        super(context, null);
        this.mZoomBar = null;
        this.mInfoOn = null;
        this.mError = null;
        this.mInfoSecond = null;
        this.mSetting = null;
        this.mWatchTarget = -1;
    }

    public VisibilityChangeZoombarGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mZoomBar = null;
        this.mInfoOn = null;
        this.mError = null;
        this.mInfoSecond = null;
        this.mSetting = null;
        this.mWatchTarget = -1;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VisibilityChangeZoombarGroup);
        this.mZoomBarGroupName = this.mTypedArray.getString(0);
        Log.d(TAG, "mZoomBarGroupName = " + this.mZoomBarGroupName);
        if (this.mZoomBarGroupName.equals(ZOOM_BAR)) {
            this.mZoomBar = this;
            this.mZoomBar.setVisibility(4);
        }
        if (this.mZoomBarGroupName.equals(INFO_ON)) {
            this.mInfoOn = this;
            this.mInfoOn.setVisibility(0);
        }
        if (this.mZoomBarGroupName.equals(ERROR)) {
            this.mError = this;
            this.mError.setVisibility(0);
        }
        if (this.mZoomBarGroupName.equals(INFO_SECOND)) {
            this.mInfoSecond = (VisibilityChangeZoombarGroup) findViewById(R.id.info_second);
            this.mInfoSecond.setVisibility(0);
        }
        if (this.mZoomBarGroupName.equals(SETTING)) {
            this.mSetting = (VisibilityChangeZoombarGroup) findViewById(R.id.setting);
            this.mSetting.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractZoombarRefresh, com.sony.imaging.app.util.AbstractRelativeLayoutGroup, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractZoombarRefresh, com.sony.imaging.app.util.AbstractRelativeLayoutGroup, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AbstractZoombarRefresh
    protected void updateVisibility(int visible) {
        if (visible != 0 && visible != 4) {
            visible = 4;
        }
        this.mZoomBarGroupName = this.mTypedArray.getString(0);
        Log.d(TAG, "updateVisibility = " + visible);
        Log.d(TAG, "updateVisibility = " + this.mZoomBarGroupName);
        if (this.mZoomBarGroupName.equals(ZOOM_BAR)) {
            this.mZoomBar = this;
            if (this.mZoomBar != null) {
                if (visible == 0) {
                    this.mZoomBar.setVisibility(0);
                } else {
                    this.mZoomBar.setVisibility(4);
                }
            }
        }
        if (this.mZoomBarGroupName.equals(INFO_ON)) {
            this.mInfoOn = this;
            if (this.mInfoOn != null) {
                if (visible == 0) {
                    this.mInfoOn.setVisibility(4);
                } else {
                    this.mInfoOn.setVisibility(0);
                }
            }
        }
        if (this.mZoomBarGroupName.equals(ERROR)) {
            this.mError = this;
            if (this.mError != null) {
                if (visible == 0) {
                    this.mError.setVisibility(4);
                } else {
                    this.mError.setVisibility(0);
                }
            }
        }
        if (this.mZoomBarGroupName.equals(INFO_SECOND)) {
            this.mInfoSecond = this;
            if (this.mInfoSecond != null) {
                if (visible == 0) {
                    this.mInfoSecond.setVisibility(4);
                } else {
                    this.mInfoSecond.setVisibility(0);
                }
            }
        }
        if (this.mZoomBarGroupName.equals(SETTING)) {
            this.mSetting = this;
            if (this.mSetting != null) {
                if (visible == 0) {
                    this.mSetting.setVisibility(4);
                } else {
                    this.mSetting.setVisibility(0);
                }
            }
        }
    }
}
