package com.sony.imaging.app.base.shooting.camera.parameters;

import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import java.util.HashMap;

/* loaded from: classes.dex */
public class DigitalZoomTypeChecker extends StringValuesChecker {
    protected static HashMap<String, String> sKeyTable = new HashMap<>();

    static {
        sKeyTable.put(Keys.KEY_SMART_ZOOM, DigitalZoomController.DIGITAL_ZOOM_TYPE_SMART);
        sKeyTable.put(Keys.KEY_SUPER_RESOLUTION_ZOOM, DigitalZoomController.DIGITAL_ZOOM_TYPE_SUPER_RESOLUTION);
        sKeyTable.put(Keys.KEY_PRECISION_ZOOM, DigitalZoomController.DIGITAL_ZOOM_TYPE_PRECISION);
    }

    public DigitalZoomTypeChecker(String key) {
        super(key, Keys.KEY_SUPPORTED_DIGITAL_ZOOM_TYPES);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0044, code lost:            if (r4.hasMoreElements() == false) goto L15;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x004e, code lost:            if (r2.equals(r4.nextToken()) == false) goto L17;     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0050, code lost:            r1 = true;        r11.set(r8.mKey, r9);     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0056, code lost:            r0 = getBuilder();        r0.replace(0, r0.length(), r8.mKey).append(com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker.COLON).append(r9).append(" -> ").append(r1);        android.util.Log.i(com.sony.imaging.app.base.shooting.camera.parameters.DigitalZoomTypeChecker.TAG, r0.toString());     */
    /* JADX WARN: Code restructure failed: missing block: B:19:?, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x003e, code lost:            if (r2 != null) goto L9;     */
    @Override // com.sony.imaging.app.base.shooting.camera.parameters.StringValuesChecker, com.sony.imaging.app.base.shooting.camera.parameters.ISupportedParameterChecker
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void check(java.lang.String r9, android.hardware.Camera.Parameters r10, android.hardware.Camera.Parameters r11, android.hardware.Camera.Parameters r12) {
        /*
            r8 = this;
            if (r9 != 0) goto L3
        L2:
            return
        L3:
            r1 = 0
            java.lang.String r5 = r8.mSupportedKey
            java.lang.String r3 = r10.get(r5)
            if (r3 != 0) goto L2d
            java.lang.String r5 = com.sony.imaging.app.base.shooting.camera.parameters.DigitalZoomTypeChecker.TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "ValueChecker "
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = r8.mKey
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = ": supported is NULL"
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Log.w(r5, r6)
            goto L2
        L2d:
            java.util.StringTokenizer r4 = new java.util.StringTokenizer
            java.lang.String r5 = ","
            r4.<init>(r3, r5)
            java.util.HashMap<java.lang.String, java.lang.String> r5 = com.sony.imaging.app.base.shooting.camera.parameters.DigitalZoomTypeChecker.sKeyTable
            java.lang.String r6 = r8.mKey
            java.lang.Object r2 = r5.get(r6)
            java.lang.String r2 = (java.lang.String) r2
            if (r2 == 0) goto L56
        L40:
            boolean r5 = r4.hasMoreElements()
            if (r5 == 0) goto L56
            java.lang.String r5 = r4.nextToken()
            boolean r5 = r2.equals(r5)
            if (r5 == 0) goto L40
            r1 = 1
            java.lang.String r5 = r8.mKey
            r11.set(r5, r9)
        L56:
            java.lang.StringBuilder r0 = r8.getBuilder()
            r5 = 0
            int r6 = r0.length()
            java.lang.String r7 = r8.mKey
            java.lang.StringBuilder r5 = r0.replace(r5, r6, r7)
            java.lang.String r6 = ": "
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.StringBuilder r5 = r5.append(r9)
            java.lang.String r6 = " -> "
            java.lang.StringBuilder r5 = r5.append(r6)
            r5.append(r1)
            java.lang.String r5 = com.sony.imaging.app.base.shooting.camera.parameters.DigitalZoomTypeChecker.TAG
            java.lang.String r6 = r0.toString()
            android.util.Log.i(r5, r6)
            goto L2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.base.shooting.camera.parameters.DigitalZoomTypeChecker.check(java.lang.String, android.hardware.Camera$Parameters, android.hardware.Camera$Parameters, android.hardware.Camera$Parameters):void");
    }
}
