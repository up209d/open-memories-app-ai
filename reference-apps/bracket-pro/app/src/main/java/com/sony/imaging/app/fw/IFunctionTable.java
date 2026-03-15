package com.sony.imaging.app.fw;

/* loaded from: classes.dex */
public interface IFunctionTable {

    /* loaded from: classes.dex */
    public interface IFunctionInfo {
        public static final int DUMMY_IMAGE_ID = -1;

        int getImageId();

        String getItemId();

        boolean isValid();
    }

    IFunctionInfo getFunctionInfo(IKeyFunction iKeyFunction);
}
