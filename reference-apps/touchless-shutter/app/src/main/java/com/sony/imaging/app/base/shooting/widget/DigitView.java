package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.Oscillator;

/* loaded from: classes.dex */
public class DigitView extends ActiveText implements Oscillator.OnPeriodListener {
    private static final String EMPTY_VALUE = "";
    private String mBlinkingCache;
    private Context mContext;
    private TextHighlighter mHighlighter;
    private static final int DEFAULT_NORMAL_COLOR = Color.rgb(221, 221, 221);
    private static final int DEFAULT_HIGHLIGHT_COLOR = Color.rgb(238, 136, 0);

    public DigitView(Context context) {
        super(context);
        this.mContext = context;
        initialize(DEFAULT_NORMAL_COLOR, DEFAULT_HIGHLIGHT_COLOR, true);
    }

    public DigitView(Context context, AttributeSet attrs) {
        this(context, attrs, true);
    }

    public DigitView(Context context, AttributeSet attrs, boolean setType) {
        super(context, attrs);
        int[] desiredAttributes = {R.attr.textColor, R.attr.textColorHighlight};
        TypedArray a = context.obtainStyledAttributes(attrs, desiredAttributes);
        int textColor = a.getColor(a.getIndex(0), DEFAULT_NORMAL_COLOR);
        int textColorHighlight = a.getColor(a.getIndex(1), DEFAULT_HIGHLIGHT_COLOR);
        a.recycle();
        this.mContext = context;
        initialize(textColor, textColorHighlight, setType);
    }

    private void initialize(int textColor, int textColorHighlight, boolean setType) {
        if (this.mContext != null) {
            this.mBlinkingCache = "";
            if (setType) {
                setTypeface(Typeface.UNIVERS);
            }
            if (this.mHighlighter == null) {
                this.mHighlighter = new TextHighlighter(textColor, textColorHighlight);
            }
        }
    }

    protected void hideIcon() {
        setCompoundDrawables(null, null, null, null);
    }

    public void setImage(Drawable drawable) {
        setCompoundDrawables(drawable, null, null, null);
    }

    public void setImage(int resId) {
        setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
    }

    public void setValue(String value) {
        this.mBlinkingCache = value;
        setText(value);
    }

    public void clear() {
        setCompoundDrawables(null, null, null, null);
        this.mBlinkingCache = "";
        setText("");
    }

    public void setUserChanging(boolean changing) {
        if (changing) {
            this.mHighlighter.startHighlight();
        } else {
            this.mHighlighter.stopHighlight();
        }
    }

    public void blink(boolean enable) {
        if (enable) {
            Oscillator.attach(8, this);
        } else {
            Oscillator.detach(8, this);
            setText(this.mBlinkingCache);
        }
    }

    public void highlight(boolean enable) {
        if (enable) {
            setTextColor(this.mHighlighter.mTextColorHighlight);
        } else {
            setTextColor(this.mHighlighter.mTextColor);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setTextAlign(int align) {
        setGravity(align);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class TextHighlighter extends Handler {
        private static final int HIGHLIGHT_DURATION_DEFAULT = 10000;
        static final int STOP_HIGHLIGHT = 0;
        private int mHighlightDuration = HIGHLIGHT_DURATION_DEFAULT;
        private int mTextColor;
        private int mTextColorHighlight;

        public TextHighlighter(int textColor, int textColorHighlight) {
            this.mTextColor = textColor;
            this.mTextColorHighlight = textColorHighlight;
        }

        void setHighlightDuration(int durationMillis) {
            this.mHighlightDuration = durationMillis;
        }

        void startHighlight() {
            if (hasMessages(0)) {
                removeMessages(0);
            }
            DigitView.this.setTextColor(this.mTextColorHighlight);
            sendEmptyMessageDelayed(0, this.mHighlightDuration);
        }

        void stopHighlight() {
            removeMessages(0);
            DigitView.this.setTextColor(this.mTextColor);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            DigitView.this.setTextColor(this.mTextColor);
        }
    }

    @Override // com.sony.imaging.app.util.Oscillator.OnPeriodListener
    public void onPeriod(int Hz, boolean highlow) {
        if (8 == Hz) {
            String value = highlow ? this.mBlinkingCache : "";
            setText(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        blink(false);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        return null;
    }
}
