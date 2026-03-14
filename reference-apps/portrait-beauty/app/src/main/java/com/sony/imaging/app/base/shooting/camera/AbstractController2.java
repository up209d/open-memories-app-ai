package com.sony.imaging.app.base.shooting.camera;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import java.util.List;

/* loaded from: classes.dex */
public abstract class AbstractController2<PF> extends AbstractController {
    private static final String LOG_MSG_ILLEGAL_SETTING = "Illegal setting. ";
    private static final String LOG_MSG_INVALID_ARGUMENT = "Invalid argument. ";
    private static final String LOG_MSG_VALUE = "value = ";
    private static final String TAG = "AbstractController2";

    protected abstract PF convertApp2PF(String str);

    protected abstract String convertPF2App(PF pf);

    @Override // com.sony.imaging.app.base.menu.IController
    public abstract List<String> getAvailableValue(String str);

    protected abstract PF getPFValue(String str);

    @Override // com.sony.imaging.app.base.menu.IController
    public abstract List<String> getSupportedValue(String str);

    protected abstract void setValueToPF(String str, PF pf);

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        PF pfValue = convertApp2PF(value);
        if (pfValue == null) {
            illgalValueExecution(tag);
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_VALUE).append(value);
            Log.w(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            throw new IllegalArgumentException();
        }
        setValueToPF(tag, pfValue);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        String appValue = convertPF2App(getPFValue(tag));
        if (appValue == null) {
            notSupportedExecution(tag);
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_ILLEGAL_SETTING).append(LOG_MSG_VALUE).append(getPFValue(tag));
            Log.w(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            throw new IController.NotSupportedException();
        }
        return appValue;
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x0017  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void notSupportedExecution(java.lang.String r6) {
        /*
            r5 = this;
            java.util.List r3 = r5.getSupportedValue(r6)
            java.util.List r0 = r5.getAvailableValue(r6)
            if (r3 == 0) goto Lc
            if (r0 != 0) goto Ld
        Lc:
            return
        Ld:
            java.util.Iterator r1 = r3.iterator()
        L11:
            boolean r4 = r1.hasNext()
            if (r4 == 0) goto Lc
            java.lang.Object r2 = r1.next()
            java.lang.String r2 = (java.lang.String) r2
            boolean r4 = r0.contains(r2)
            if (r4 == 0) goto L11
            goto L11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.base.shooting.camera.AbstractController2.notSupportedExecution(java.lang.String):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x0017  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void illgalValueExecution(java.lang.String r6) {
        /*
            r5 = this;
            java.util.List r3 = r5.getSupportedValue(r6)
            java.util.List r0 = r5.getAvailableValue(r6)
            if (r3 == 0) goto Lc
            if (r0 != 0) goto Ld
        Lc:
            return
        Ld:
            java.util.Iterator r1 = r3.iterator()
        L11:
            boolean r4 = r1.hasNext()
            if (r4 == 0) goto Lc
            java.lang.Object r2 = r1.next()
            java.lang.String r2 = (java.lang.String) r2
            boolean r4 = r0.contains(r2)
            if (r4 == 0) goto L11
            goto L11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.base.shooting.camera.AbstractController2.illgalValueExecution(java.lang.String):void");
    }
}
