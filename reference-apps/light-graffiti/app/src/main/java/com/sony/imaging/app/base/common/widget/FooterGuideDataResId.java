package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class FooterGuideDataResId implements IFooterGuideData {
    protected Context mContext;
    protected int mStrIdEmt;
    protected int mStrIdP1;

    public FooterGuideDataResId(Context context, int strId) {
        this.mContext = context;
        this.mStrIdEmt = strId;
        this.mStrIdP1 = strId;
    }

    public FooterGuideDataResId(Context context, int strP1, int strEmt) {
        this.mContext = context;
        this.mStrIdP1 = strP1;
        this.mStrIdEmt = strEmt;
    }

    @Override // com.sony.imaging.app.base.common.widget.IFooterGuideData
    public CharSequence getText() {
        int hwVersion = Environment.getVersionOfHW();
        int resid = 1 == hwVersion ? this.mStrIdEmt : this.mStrIdP1;
        if (resid == 0) {
            return null;
        }
        return this.mContext.getResources().getText(resid);
    }
}
