package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.BatteryIcon;

/* loaded from: classes.dex */
public class NumberImage extends LinearLayout {
    private static final int DEFAULTNUMBER = 10;
    private static final String LOG_MSG_IMAGENOTFOUND = "Image not found.";
    static final String TAG = "NumberImage";
    protected int mAlpha;

    public NumberImage(Context context) {
        super(context);
        setLayoutParams();
        this.mAlpha = BatteryIcon.BATTERY_STATUS_CHARGING;
    }

    public NumberImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NumberImage);
        String str = ta.getString(1);
        int arrayresid = ta.getResourceId(0, 0);
        TypedArray tadrawable = getResources().obtainTypedArray(arrayresid);
        makeImageFromString(str, tadrawable);
        this.mAlpha = BatteryIcon.BATTERY_STATUS_CHARGING;
    }

    private void setLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        setOrientation(0);
        setLayoutParams(params);
    }

    public final void makeImageFromString(String str, TypedArray arrayresid) {
        int num;
        removeAllViewsInLayout();
        int maxImageNumber = arrayresid.length();
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                num = Integer.parseInt("" + str.charAt(i));
            } else {
                num = setNumber(str.charAt(i));
            }
            if (num >= maxImageNumber) {
                Log.d(TAG, LOG_MSG_IMAGENOTFOUND);
            } else {
                ImageView image = new ImageView(getContext());
                image.setImageDrawable(arrayresid.getDrawable(num));
                image.setAlpha(this.mAlpha);
                addView(image);
            }
        }
        invalidate();
    }

    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
        int ct = getChildCount();
        for (int i = 0; i < ct; i++) {
            View v = getChildAt(i);
            if (v instanceof ImageView) {
                ((ImageView) v).setAlpha(alpha);
            }
        }
    }

    public final void makeImageFromStringLapover(String str, TypedArray arrayresid, int paddingleft, int paddingtop) {
        int num;
        removeAllViewsInLayout();
        int maxImageNumber = arrayresid.length();
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                num = Integer.parseInt("" + str.charAt(i));
            } else {
                num = setNumber(str.charAt(i));
            }
            if (num >= maxImageNumber) {
                Log.d(TAG, LOG_MSG_IMAGENOTFOUND);
            } else {
                ImageView image = new ImageView(getContext());
                Drawable drawable = arrayresid.getDrawable(num);
                image.setImageDrawable(drawable);
                image.setAlpha(this.mAlpha);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                if (i == 0) {
                    lp.leftMargin = 0;
                    lp.topMargin = 0;
                } else {
                    lp.leftMargin = paddingleft;
                    lp.topMargin = paddingtop;
                }
                addView(image, lp);
            }
        }
        invalidate();
    }

    public int setNumber(char ch) {
        return 10;
    }
}
