package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public class ValueImage extends ImageView {
    private ArrayList<String> mArray;
    private String mCurrentValue;
    private HashMap<String, Drawable> mImageMap;
    private TypedArray mImageType;

    public ValueImage(Context context) {
        super(context);
        this.mArray = null;
        this.mImageMap = new HashMap<>();
        this.mCurrentValue = "";
    }

    public ValueImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mArray = null;
        this.mImageMap = new HashMap<>();
        this.mCurrentValue = "";
    }

    public void setValueImage(ArrayList<String> array, int imageArray) {
        this.mImageType = getResources().obtainTypedArray(imageArray);
        this.mArray = array;
        if (this.mImageType != null && this.mArray != null) {
            int arraySize = this.mArray.size();
            int typeStartIndex = this.mImageType.length() - (arraySize / 2);
            for (int i = 0; arraySize > i; i++) {
                this.mImageMap.put(this.mArray.get(i), this.mImageType.getDrawable(typeStartIndex - i));
            }
            updateImage(null);
        }
    }

    public void setImageType(int imageArray) {
        if (this.mArray != null) {
            this.mImageType = getResources().obtainTypedArray(imageArray);
            int arraySize = this.mArray.size();
            int typeStartIndex = this.mImageType.length() - (arraySize / 2);
            for (int i = 0; arraySize > i; i++) {
                this.mImageMap.put(this.mArray.get(i), this.mImageType.getDrawable(typeStartIndex - i));
            }
            updateImage(null);
        }
    }

    public void updateImage(String str) {
        if (str == null) {
            str = this.mCurrentValue;
        } else if (str.isEmpty()) {
            return;
        }
        Drawable d = this.mImageMap.get(str);
        setImageDrawable(d);
        this.mCurrentValue = str;
    }
}
