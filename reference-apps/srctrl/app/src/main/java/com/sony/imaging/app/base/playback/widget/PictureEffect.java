package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class PictureEffect extends IconFileInfo {
    private static final int EFFECT_ARRAY_NUM = 32;
    private static final int PEFFECT_ERROR_1 = 1;
    private static final int PEFFECT_ERROR_2 = 2;
    private static final int PEFFECT_NOERROR = 0;
    private static final int VAL_PICTUREEFFECT_HDR_HIGH = 66;
    private static final int VAL_PICTUREEFFECT_HDR_LOW = 64;
    private static final int VAL_PICTUREEFFECT_HDR_NORMAL = 65;
    private static final int VAL_PICTUREEFFECT_HIGHCONTRAST_MONO = 13;
    private static final int VAL_PICTUREEFFECT_ILLUST_HIGH = 114;
    private static final int VAL_PICTUREEFFECT_ILLUST_LOW = 112;
    private static final int VAL_PICTUREEFFECT_ILLUST_MID = 113;
    private static final int VAL_PICTUREEFFECT_MINIATURE_AUTO = 48;
    private static final int VAL_PICTUREEFFECT_MINIATURE_BOTTOM = 51;
    private static final int VAL_PICTUREEFFECT_MINIATURE_HORIZONTAL = 50;
    private static final int VAL_PICTUREEFFECT_MINIATURE_LEFT = 52;
    private static final int VAL_PICTUREEFFECT_MINIATURE_RIGHT = 54;
    private static final int VAL_PICTUREEFFECT_MINIATURE_UP = 49;
    private static final int VAL_PICTUREEFFECT_MINIATURE_VERTICAL = 53;
    private static final int VAL_PICTUREEFFECT_PARTCOLOR_BLUE = 9;
    private static final int VAL_PICTUREEFFECT_PARTCOLOR_GREEN = 8;
    private static final int VAL_PICTUREEFFECT_PARTCOLOR_RED = 7;
    private static final int VAL_PICTUREEFFECT_PARTCOLOR_YELLOW = 10;
    private static final int VAL_PICTUREEFFECT_POP_COLOR = 2;
    private static final int VAL_PICTUREEFFECT_POSTERISATION_COLOR = 3;
    private static final int VAL_PICTUREEFFECT_POSTERISATION_MONO = 4;
    private static final int VAL_PICTUREEFFECT_RETROPHOTO = 5;
    private static final int VAL_PICTUREEFFECT_RICHTONE_MONO = 80;
    private static final int VAL_PICTUREEFFECT_SOFTFOCUS_HIGH = 34;
    private static final int VAL_PICTUREEFFECT_SOFTFOCUS_LOW = 32;
    private static final int VAL_PICTUREEFFECT_SOFTFOCUS_NORMAL = 33;
    private static final int VAL_PICTUREEFFECT_SOFTHIGHKEY = 6;
    private static final int VAL_PICTUREEFFECT_TOY_COOL = 17;
    private static final int VAL_PICTUREEFFECT_TOY_GREEN = 19;
    private static final int VAL_PICTUREEFFECT_TOY_MAGENTA = 20;
    private static final int VAL_PICTUREEFFECT_TOY_NORMAL = 16;
    private static final int VAL_PICTUREEFFECT_TOY_WARM = 18;
    private static final int VAL_PICTUREEFFECT_WATER_COLOR = 97;
    private static SparseArray<Integer> mDrawables;
    private static SparseArray<Integer> mErrDrawables = new SparseArray<>();
    boolean mIsOnlyErrDisplay;
    private TypedArray mTypedArray;

    static {
        mErrDrawables.put(VAL_PICTUREEFFECT_HDR_HIGH, 3);
        mErrDrawables.put(VAL_PICTUREEFFECT_HDR_NORMAL, 2);
        mErrDrawables.put(VAL_PICTUREEFFECT_HDR_LOW, 1);
        mErrDrawables.put(VAL_PICTUREEFFECT_RICHTONE_MONO, 4);
        mDrawables = new SparseArray<>(32);
        mDrawables.put(2, 5);
        mDrawables.put(3, 6);
        mDrawables.put(4, 7);
        mDrawables.put(5, 8);
        mDrawables.put(6, 9);
        mDrawables.put(7, 10);
        mDrawables.put(8, 11);
        mDrawables.put(9, 12);
        mDrawables.put(10, 13);
        mDrawables.put(13, 14);
        mDrawables.put(16, 15);
        mDrawables.put(17, 16);
        mDrawables.put(18, 17);
        mDrawables.put(19, 18);
        mDrawables.put(20, 19);
        mDrawables.put(32, 20);
        mDrawables.put(33, 21);
        mDrawables.put(34, 22);
        mDrawables.put(VAL_PICTUREEFFECT_MINIATURE_AUTO, 23);
        mDrawables.put(VAL_PICTUREEFFECT_MINIATURE_UP, 24);
        mDrawables.put(50, 25);
        mDrawables.put(51, 26);
        mDrawables.put(52, 27);
        mDrawables.put(53, 28);
        mDrawables.put(54, 29);
        mDrawables.put(VAL_PICTUREEFFECT_HDR_LOW, 30);
        mDrawables.put(VAL_PICTUREEFFECT_HDR_NORMAL, 31);
        mDrawables.put(VAL_PICTUREEFFECT_HDR_HIGH, 32);
        mDrawables.put(VAL_PICTUREEFFECT_RICHTONE_MONO, 33);
        mDrawables.put(VAL_PICTUREEFFECT_WATER_COLOR, 34);
        mDrawables.put(VAL_PICTUREEFFECT_ILLUST_LOW, 35);
        mDrawables.put(VAL_PICTUREEFFECT_ILLUST_MID, 36);
        mDrawables.put(VAL_PICTUREEFFECT_ILLUST_HIGH, 37);
    }

    public PictureEffect(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsOnlyErrDisplay = false;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.PictureEffect);
        this.mIsOnlyErrDisplay = this.mTypedArray.getBoolean(0, false);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        int pictureEffectMode = -1;
        int pictureEffectError = -1;
        if (info != null) {
            pictureEffectMode = info.getInt("MkNotePictureEffect");
            pictureEffectError = info.getInt("MkNotePictEffectError");
        }
        setValue(pictureEffectMode, pictureEffectError);
    }

    public void setValue(int pictureEffectMode, int pictureEffectError) {
        boolean isDisplay = false;
        switch (pictureEffectMode) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 13:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 32:
            case 33:
            case 34:
            case VAL_PICTUREEFFECT_MINIATURE_AUTO /* 48 */:
            case VAL_PICTUREEFFECT_MINIATURE_UP /* 49 */:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case VAL_PICTUREEFFECT_WATER_COLOR /* 97 */:
            case VAL_PICTUREEFFECT_ILLUST_LOW /* 112 */:
            case VAL_PICTUREEFFECT_ILLUST_MID /* 113 */:
            case VAL_PICTUREEFFECT_ILLUST_HIGH /* 114 */:
                if (!this.mIsOnlyErrDisplay) {
                    isDisplay = setNoError(pictureEffectMode);
                    break;
                }
                break;
            case VAL_PICTUREEFFECT_HDR_LOW /* 64 */:
            case VAL_PICTUREEFFECT_HDR_NORMAL /* 65 */:
            case VAL_PICTUREEFFECT_HDR_HIGH /* 66 */:
            case VAL_PICTUREEFFECT_RICHTONE_MONO /* 80 */:
                isDisplay = setErrorInclude(pictureEffectMode, pictureEffectError);
                break;
        }
        setVisibility(isDisplay ? 0 : 4);
    }

    private boolean setErrorInclude(int pictureEffectMode, int pictureEffectError) {
        if (pictureEffectError == 1 || pictureEffectError == 2) {
            setImageResource(this.mTypedArray.getResourceId(mErrDrawables.get(pictureEffectMode).intValue(), 0));
            return true;
        }
        if (pictureEffectError != 0) {
            return false;
        }
        setImageResource(this.mTypedArray.getResourceId(mDrawables.get(pictureEffectMode).intValue(), 0));
        if (this.mIsOnlyErrDisplay) {
            return false;
        }
        return true;
    }

    private boolean setNoError(int pictureEffectMode) {
        int resid = this.mTypedArray.getResourceId(mDrawables.get(pictureEffectMode).intValue(), 0);
        if (resid == 0) {
            return false;
        }
        setImageResource(resid);
        return true;
    }
}
