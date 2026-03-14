package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.SubLcdManager;
import com.sony.imaging.app.util.PTag;
import com.sony.imaging.app.util.StringBuilderThreadLocal;

/* loaded from: classes.dex */
public class SubLcdIconView extends AbstractSubLCDView {
    private static final String MSG_DRAW = "draw subLcd ";
    private static final String TAG = "SubLcdIconView";
    protected String mLid;
    protected String mLkid;
    protected String mPattern;

    public SubLcdIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mLkid = "";
        initAttribute(context, attrs);
    }

    public SubLcdIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLkid = "";
        initAttribute(context, attrs);
    }

    @Deprecated
    public SubLcdIconView(Context context) {
        super(context);
        this.mLkid = "";
    }

    public SubLcdIconView(Context context, String lid) {
        super(context);
        this.mLkid = "";
        this.mLid = lid;
        this.mPattern = "PTN_ON";
    }

    protected void initAttribute(Context context, AttributeSet attrs) {
        TypedArray a_base = context.obtainStyledAttributes(attrs, R.styleable.SubLcd);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SubLcdIcon);
        this.mLid = a_base.getString(0);
        this.mLkid = a.getString(0);
        if (this.mLkid == null) {
            this.mLkid = "";
        }
        this.mPattern = a_base.getString(2);
        if (this.mPattern == null) {
            this.mPattern = "PTN_ON";
        }
        a_base.recycle();
        a.recycle();
    }

    public void setLkid(String lkid, String pattern) {
        if (lkid == null) {
            lkid = "";
        }
        if (pattern == null) {
            pattern = "PTN_OFF";
        }
        boolean isChanged = (lkid.equals(this.mLkid) && pattern.equals(this.mPattern)) ? false : true;
        this.mLkid = lkid;
        this.mPattern = pattern;
        if (isChanged && getVisibility() == 0) {
            SubLcdManager.getInstance().requestDraw();
        }
    }

    public void setLkid(String lkid) {
        setLkid(lkid, this.mPattern);
    }

    public void setPattern(String pattern) {
        setLkid(this.mLkid, pattern);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        SubLcdManager.getInstance().requestDraw();
        super.onVisibilityChanged(changedView, visibility);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override // com.sony.imaging.app.base.common.ISubLcdDrawer
    public String getLId() {
        return this.mLid;
    }

    public String getPattern() {
        return this.mPattern;
    }

    @Override // com.sony.imaging.app.base.common.ISubLcdDrawer
    public SubLcdManager.Element getSubLcdElement(String blink) {
        String pattern = this.mPattern;
        if (blink != null && (pattern == null || "PTN_ON".equals(pattern))) {
            pattern = blink;
        }
        boolean isVisible = getVisibility() == 0;
        SubLcdManager.Element elem = null;
        if (this.mLid != null && this.mLkid != null) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), MSG_DRAW).append(this.mLid).append(" : ").append(isVisible).append(", ").append(this.mLkid).append(", ").append(pattern);
            PTag.start(builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            if (isVisible) {
                if (pattern != null) {
                    elem = SubLcdManager.Element.makeIcon(this.mLid, this.mLkid, pattern);
                } else {
                    elem = SubLcdManager.Element.makeIcon(this.mLid, this.mLkid);
                }
            }
            PTag.end(MSG_DRAW, 1);
        }
        return elem;
    }
}
