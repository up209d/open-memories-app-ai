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
public class SubLcdTextView extends AbstractSubLCDView {
    private static final String MSG_DRAW = "draw subLcd ";
    private static final String TAG = "SubLcdTextView";
    protected String mLid;
    protected String mPattern;
    protected String mText;

    public SubLcdTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs);
    }

    public SubLcdTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs);
    }

    @Deprecated
    public SubLcdTextView(Context context) {
        super(context);
    }

    public SubLcdTextView(Context context, String lid) {
        super(context);
        this.mLid = lid;
        this.mPattern = "PTN_ON";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initAttribute(Context context, AttributeSet attrs) {
        TypedArray a_base = context.obtainStyledAttributes(attrs, R.styleable.SubLcd);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SubLcdText);
        this.mLid = a_base.getString(0);
        this.mPattern = a_base.getString(2);
        if (this.mPattern == null) {
            this.mPattern = "PTN_ON";
        }
        CharSequence text = a.getText(0);
        setText(text);
        a.recycle();
        a_base.recycle();
    }

    public void setText(CharSequence text, String pattern) {
        if (text == null) {
            text = "";
        }
        if (pattern == null) {
            pattern = "PTN_OFF";
        }
        boolean isChanged = (text.equals(this.mText) && pattern.equals(this.mPattern)) ? false : true;
        this.mText = text.toString();
        this.mPattern = pattern;
        if (isChanged && getVisibility() == 0) {
            SubLcdManager.getInstance().requestDraw();
        }
    }

    public void setText(CharSequence text) {
        setText(text, this.mPattern);
    }

    public void setText(int resid) {
        CharSequence text = "";
        if (resid > 0) {
            text = getContext().getResources().getText(resid);
        }
        setText(text);
    }

    public void setText(int resid, String pattern) {
        CharSequence text = "";
        if (resid > 0) {
            text = getContext().getResources().getText(resid);
        }
        setText(text, pattern);
    }

    public void setPattern(String pattern) {
        setText(this.mText, pattern);
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
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), MSG_DRAW).append(this.mLid).append(" : ").append(isVisible).append(", ").append(this.mText).append(", ").append(pattern);
        PTag.start(builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        if (isVisible) {
            elem = SubLcdManager.Element.makeText(this.mLid, this.mText, pattern);
        }
        PTag.end(MSG_DRAW, 1);
        return elem;
    }
}
