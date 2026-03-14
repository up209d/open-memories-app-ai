package com.sony.imaging.app.util;

import android.database.Cursor;
import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.sysutil.didep.Status;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class AvailableInfoInhMgr extends AvailableInfoImpl {
    private static final String AUDIOMANAGER_PARAMETERS = "AudioManager.Parameters";
    private static final String CAMERAEX_PARAMETERS = "CameraEx.ParametersModifier";
    private static final String CAMERA_PARAMETERS = "Camera.Parameters";
    private static final String DB_PATH = "/etc/InhInfo_InhMgr.db";
    private static final String EXIST_VERSION_TABLE = "select type from sqlite_master where type='table' and name='version';";
    private static final String GET_FACTORS = "select factor from inh_records where _id in ('";
    private static final String GET_FEATURES_INDEX = "select bfeatures, features from api_inh_records where apiname=='";
    private static final String GET_RELATION_VER1 = "select objectapi,originalapi from api_relation_table where targetapi=='";
    private static final String GET_RELATION_VER2 = "select objectapi,originalapi,objectclass from api_relation_table where targetapi=='";
    private static final String MEDIARECORDER_PARAMETERS = "MediaRecorder.Parameters";
    protected static final int PF_VER_AVOID_USING_MEMBER = 12;
    private static final String READ_VERSION = "select version from version;";
    private static final String TAG = "AvailableInfoInhMgr";
    protected boolean useMemberList;
    protected static Status mStatus = null;
    protected static int mVersion = -1;
    protected static VersionDispatcher mDispatcher = null;
    private final StringBuilderThreadLocal SELCTORS = new StringBuilderThreadLocal();
    private ArrayList<String> mParamAPI = new ArrayList<>();
    private ArrayList<String> mParamValue = new ArrayList<>();
    private ArrayList<String> mTargetAPI = new ArrayList<>();
    private ArrayList<String> mTargetValue = new ArrayList<>();
    private HashMap<String, String> mMustGetAPI = new HashMap<>();
    private HashMap<String, String> mMustGetClassName = new HashMap<>();

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public interface VersionDispatcher {
        Object[] isAvailableHelper(Camera.Parameters parameters, CameraEx.ParametersModifier parametersModifier, MediaRecorder.Parameters parameters2, AudioManager.Parameters parameters3, boolean z, ArrayList<String> arrayList, ArrayList<String> arrayList2);
    }

    public AvailableInfoInhMgr() {
        this.useMemberList = Environment.getVersionPfAPI() < 12;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.AvailableInfoImpl
    public void initialize() {
        super.initialize();
        if (mStatus == null) {
            mStatus = Status.create();
        }
        mVersion = readVersion();
        if (mVersion >= 2) {
            mDispatcher = new Version2();
        } else {
            mDispatcher = new Version1();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.AvailableInfoImpl
    public void terminate() {
        super.terminate();
        if (mStatus != null) {
            mStatus.release();
            mStatus = null;
        }
    }

    protected int readVersion() {
        Cursor c = this.mDB.rawQuery(EXIST_VERSION_TABLE, null);
        boolean exist = c.getCount() != 0;
        c.close();
        if (!exist) {
            return 1;
        }
        Cursor c2 = this.mDB.rawQuery(READ_VERSION, null);
        c2.moveToFirst();
        int version = c2.getInt(0);
        c2.close();
        return version;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] getInhFactorFilter() {
        return mStatus.getInhFactorFilter();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setInhFactorFilter(byte[] inhFactorFilter) {
        mStatus.setInhFactorFilter(inhFactorFilter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setInhFactorListener(Status.InhFactorListener listener) {
        mStatus.setInhFactorListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setInhFactorListenerMulti(int factorId, Status.InhFactorListenerMulti listener) {
        mStatus.setInhFactorListenerMulti(factorId, listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unSetInhFactorListenerMulti(Status.InhFactorListenerMulti listener) {
        mStatus.unSetInhFactorListenerMulti(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] getFactors() {
        return mStatus.getFactors();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.AvailableInfoImpl
    public synchronized void update() {
        this.currentFactor = mStatus.getFactors();
    }

    @Override // com.sony.imaging.app.util.AvailableInfoImpl
    protected String getDataBasePath() {
        return DB_PATH;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int[] getMediaOffsets(String[] medias) {
        return mStatus.getMediaOffsets(medias);
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x000a, code lost:            if (r5.moveToFirst() != false) goto L7;     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x000c, code lost:            r0 = r5.getBlob(0);        r1 = compare(r0, r4);     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0015, code lost:            if (r1 != false) goto L12;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x001b, code lost:            if (r5.moveToNext() != false) goto L14;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean isFactorWithCursor(int r4, android.database.Cursor r5) {
        /*
            r3 = this;
            r1 = 0
            r2 = -1
            if (r2 == r4) goto L1d
            if (r5 == 0) goto L1d
            boolean r2 = r5.moveToFirst()
            if (r2 == 0) goto L1d
        Lc:
            r2 = 0
            byte[] r0 = r5.getBlob(r2)
            boolean r1 = compare(r0, r4)
            if (r1 != 0) goto L1d
            boolean r2 = r5.moveToNext()
            if (r2 != 0) goto Lc
        L1d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.util.AvailableInfoInhMgr.isFactorWithCursor(int, android.database.Cursor):boolean");
    }

    private boolean isFactorWithCursor(int bit, List<byte[]> factors) {
        if (-1 == bit || factors == null) {
            return false;
        }
        int factorCount = factors.size();
        for (int i = 0; factorCount > i; i++) {
            if (compare(factors.get(i), bit)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isFactorWithCursor(String factorID, Cursor factors) {
        boolean ret = false;
        int bit = isFactorHelper(factorID);
        if (-1 != bit) {
            ret = compare(this.currentFactor, bit);
        }
        if (ret) {
            boolean ret2 = isFactorWithCursor(bit, factors);
            return ret2;
        }
        return ret;
    }

    private boolean isFactorHelper(String factorID, int bit, Object... api) {
        AvailableInfo.FactorsData factorsData = AvailableInfo.getFactorsData(api);
        boolean ret = isFactorWithCursor(bit, factorsData.getFactors());
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isFactor(String factorID, List<Object[]> list) {
        boolean ret = false;
        int bit = isFactorHelper(factorID);
        if (-1 != bit) {
            ret = compare(this.currentFactor, bit);
        }
        if (ret) {
            for (Object[] objs : list) {
                ret = isFactorHelper(factorID, bit, objs);
                if (!ret) {
                    break;
                }
            }
        }
        return ret;
    }

    public boolean isFactor(String factorID, Camera.Parameters p, CameraEx.ParametersModifier m, MediaRecorder.Parameters mp, AudioManager.Parameters ma, Object... api) {
        boolean ret = false;
        int bit = isFactorHelper(factorID);
        if (-1 != bit) {
            ret = compare(this.currentFactor, bit);
        }
        if (ret) {
            List<Object[]> list = getApiList(p, m, mp, ma, api);
            boolean ret2 = isFactor(factorID, list);
            return ret2;
        }
        return ret;
    }

    /* JADX WARN: Code restructure failed: missing block: B:4:0x0008, code lost:            if (r5.moveToFirst() != false) goto L6;     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x000a, code lost:            r0 = r5.getBlob(0);        r1 = compare(r0, r4.currentFactor);     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0014, code lost:            if (r1 != false) goto L13;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x001a, code lost:            if (r5.moveToNext() != false) goto L15;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected boolean isAvailable(android.database.Cursor r5) {
        /*
            r4 = this;
            r2 = 0
            r1 = 0
            if (r5 == 0) goto L1c
            boolean r3 = r5.moveToFirst()
            if (r3 == 0) goto L1c
        La:
            byte[] r0 = r5.getBlob(r2)
            byte[] r3 = r4.currentFactor
            boolean r1 = compare(r0, r3)
            if (r1 != 0) goto L1c
            boolean r3 = r5.moveToNext()
            if (r3 != 0) goto La
        L1c:
            if (r1 != 0) goto L1f
            r2 = 1
        L1f:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.util.AvailableInfoInhMgr.isAvailable(android.database.Cursor):boolean");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAvailable(List<byte[]> factors) {
        boolean ret = false;
        if (factors != null) {
            int factorCount = factors.size();
            int i = 0;
            while (true) {
                if (factorCount <= i) {
                    break;
                }
                if (!compare(factors.get(i), this.currentFactor)) {
                    i++;
                } else {
                    ret = true;
                    break;
                }
            }
        }
        return !ret;
    }

    public boolean isAvailable(Object... api) {
        AvailableInfo.FactorsData factorsData = AvailableInfo.getFactorsData(api);
        boolean ret = isAvailable(factorsData.getFactors());
        return ret;
    }

    public boolean isAvailabel(List<Object[]> list) {
        boolean ret = false;
        for (Object[] objs : list) {
            ret = isAvailable(objs);
            if (!ret) {
                break;
            }
        }
        return ret;
    }

    public boolean isAvailable(Camera.Parameters p, CameraEx.ParametersModifier m, MediaRecorder.Parameters mp, AudioManager.Parameters ma, Object... api) {
        List<Object[]> list = getApiList(p, m, mp, ma, api);
        boolean ret = isAvailabel(list);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<Object[]> getApiList(Camera.Parameters p, CameraEx.ParametersModifier m, MediaRecorder.Parameters mp, AudioManager.Parameters ma, Object... api) {
        int apiCount = api.length;
        boolean apiOnly = api[1] == null;
        ArrayList<String> paramAPI = this.mParamAPI;
        ArrayList<String> paramValue = this.mParamValue;
        if (!this.useMemberList) {
            paramAPI = new ArrayList<>(apiCount);
            paramValue = new ArrayList<>(apiCount);
        }
        paramAPI.clear();
        if (apiOnly) {
            for (int i = 0; i < apiCount; i++) {
                if (i % 2 == 0) {
                    paramAPI.add(String.valueOf(api[i]));
                }
            }
        } else {
            paramValue.clear();
            for (int i2 = 0; i2 < apiCount; i2++) {
                if (i2 % 2 == 0) {
                    paramAPI.add(String.valueOf(api[i2]));
                } else {
                    paramValue.add(String.valueOf(api[i2]));
                }
            }
        }
        return getApiList(p, m, mp, ma, apiOnly, paramAPI, paramValue);
    }

    protected List<Object[]> getApiList(Camera.Parameters p, CameraEx.ParametersModifier m, MediaRecorder.Parameters mp, AudioManager.Parameters ma, boolean apiOnly, ArrayList<String> paramAPI, ArrayList<String> paramValue) {
        List<Object[]> list = new ArrayList<>();
        while (paramAPI.size() != 0) {
            Object[] params = mDispatcher.isAvailableHelper(p, m, mp, ma, apiOnly, paramAPI, paramValue);
            list.add(params);
        }
        return list;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x002b, code lost:            if (r9.moveToNext() != false) goto L15;     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x000b, code lost:            if (r9.moveToFirst() != false) goto L6;     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x000d, code lost:            r1 = r9.getString(1);     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0012, code lost:            if (r1 == null) goto L11;     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0014, code lost:            r6 = r1.split(",");        r3 = r6.length;        r2 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x001d, code lost:            if (r2 >= r3) goto L16;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x001f, code lost:            r5 = r6[r2];        r4.add(r5);        r2 = r2 + 1;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List<java.lang.String> getFeatures(android.database.Cursor r9) {
        /*
            r8 = this;
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            if (r9 == 0) goto L2d
            boolean r7 = r9.moveToFirst()
            if (r7 == 0) goto L2d
        Ld:
            r7 = 1
            java.lang.String r1 = r9.getString(r7)
            if (r1 == 0) goto L27
            java.lang.String r7 = ","
            java.lang.String[] r6 = r1.split(r7)
            r0 = r6
            int r3 = r0.length
            r2 = 0
        L1d:
            if (r2 >= r3) goto L27
            r5 = r0[r2]
            r4.add(r5)
            int r2 = r2 + 1
            goto L1d
        L27:
            boolean r7 = r9.moveToNext()
            if (r7 != 0) goto Ld
        L2d:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.util.AvailableInfoInhMgr.getFeatures(android.database.Cursor):java.util.List");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Cursor getFeaturesCursor(Object... api) {
        int apiCount = api.length;
        boolean apiOnly = false;
        Object[] pair = new Object[apiCount / 2];
        for (int i = 0; i < apiCount; i += 2) {
            Object apiname = api[i];
            Object value = api[i + 1];
            if (value == null) {
                pair[i / 2] = apiname;
                if (i == 0) {
                    apiOnly = true;
                } else if (!apiOnly) {
                    throw new InvalidParameterException("The parameters are strange");
                }
            } else {
                pair[i / 2] = apiname + ":" + value;
            }
        }
        Arrays.sort(pair);
        StringBuilder selector = this.SELCTORS.get();
        selector.replace(0, selector.length(), GET_FEATURES_INDEX);
        for (Object obj : pair) {
            selector.append(obj).append(MovieFormatController.Settings.SEMI_COLON);
        }
        selector.append("'");
        if (AvailableInfo.DEBUG) {
            Log.i(TAG, "start query: " + ((Object) selector));
        }
        Cursor c = this.mDB.rawQuery(selector.toString(), null);
        if (AvailableInfo.DEBUG) {
            Log.i(TAG, PortraitBeautyConstants.FINISHTAG);
        }
        return c;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Cursor getFactorsCursor(Cursor c) {
        int count = c.getCount();
        if (count == 0 || !c.moveToFirst()) {
            return null;
        }
        byte[] raw = c.getBlob(0);
        if (raw.length == 0) {
            return null;
        }
        ByteBuffer b = ByteBuffer.wrap(raw);
        ShortBuffer features = b.asShortBuffer();
        StringBuilder selector = this.SELCTORS.get();
        selector.replace(0, selector.length(), GET_FACTORS);
        int featuresCount = features.capacity();
        for (int i = 0; i < featuresCount; i++) {
            selector.append((int) features.get(i)).append("'");
            if (i + 1 < featuresCount) {
                selector.append(",'");
            }
        }
        selector.append(LogHelper.MSG_CLOSE_BRACKET);
        if (AvailableInfo.DEBUG) {
            Log.i(TAG, "start query: " + ((Object) selector));
        }
        Cursor c2 = this.mDB.rawQuery(selector.toString(), null);
        if (AvailableInfo.DEBUG) {
            Log.i(TAG, PortraitBeautyConstants.FINISHTAG);
        }
        return c2;
    }

    /* loaded from: classes.dex */
    protected class Version1 implements VersionDispatcher {
        protected Version1() {
        }

        @Override // com.sony.imaging.app.util.AvailableInfoInhMgr.VersionDispatcher
        public Object[] isAvailableHelper(Camera.Parameters p, CameraEx.ParametersModifier m, MediaRecorder.Parameters mp, AudioManager.Parameters ma, boolean apiOnly, ArrayList<String> paramAPI, ArrayList<String> paramValue) {
            Object value;
            ArrayList<String> targetAPI = AvailableInfoInhMgr.this.mTargetAPI;
            ArrayList<String> targetValue = AvailableInfoInhMgr.this.mTargetValue;
            HashMap<String, String> mustGetAPI = AvailableInfoInhMgr.this.mMustGetAPI;
            if (!AvailableInfoInhMgr.this.useMemberList) {
                targetAPI = new ArrayList<>();
                targetValue = new ArrayList<>();
                mustGetAPI = new HashMap<>();
            }
            String API = paramAPI.remove(0);
            targetAPI.clear();
            targetAPI.add(API);
            if (!apiOnly) {
                targetValue.clear();
                targetValue.add(paramValue.remove(0));
            }
            StringBuilder selector = AvailableInfoInhMgr.this.SELCTORS.get();
            selector.replace(0, selector.length(), AvailableInfoInhMgr.GET_RELATION_VER1).append(API).append("'");
            if (AvailableInfo.DEBUG) {
                Log.i(AvailableInfoInhMgr.TAG, "start query: " + ((Object) selector));
            }
            Cursor c = AvailableInfoInhMgr.this.mDB.rawQuery(selector.toString(), null);
            if (AvailableInfo.DEBUG) {
                Log.i(AvailableInfoInhMgr.TAG, PortraitBeautyConstants.FINISHTAG);
            }
            int count = c.getCount();
            if (count != 0 && c.moveToFirst()) {
                mustGetAPI.clear();
                String[] getAPIs = c.getString(0).split(",");
                String[] originalAPI = c.getString(1).split(",");
                c.close();
                int originalCount = originalAPI.length;
                for (int j = 0; j < originalCount; j++) {
                    String currentOriginalAPI = originalAPI[j];
                    if (!mustGetAPI.containsKey(currentOriginalAPI)) {
                        boolean found = false;
                        int paramCount = paramAPI.size();
                        int k = 0;
                        while (true) {
                            if (k >= paramCount) {
                                break;
                            }
                            if (!currentOriginalAPI.equals(paramAPI.get(k))) {
                                k++;
                            } else {
                                found = true;
                                mustGetAPI.remove(currentOriginalAPI);
                                targetAPI.add(currentOriginalAPI);
                                if (!apiOnly) {
                                    targetValue.add(paramValue.remove(k));
                                }
                                paramAPI.remove(k);
                            }
                        }
                        if (!found) {
                            mustGetAPI.put(currentOriginalAPI, getAPIs[j]);
                        }
                    }
                }
                Set<String> key = mustGetAPI.keySet();
                for (String setAPI : key) {
                    targetAPI.add(setAPI);
                    if (!apiOnly) {
                        String getAPI = mustGetAPI.get(setAPI);
                        try {
                            Method func = m.getClass().getMethod(getAPI, (Class[]) null);
                            value = func.invoke(m, (Object[]) null);
                        } catch (Exception e) {
                            try {
                                Method func2 = p.getClass().getMethod(getAPI, (Class[]) null);
                                value = func2.invoke(p, (Object[]) null);
                            } catch (Exception e2) {
                                throw new InvalidParameterException("could not found " + getAPI + " api in Parameters");
                            }
                        }
                        targetValue.add(value.toString());
                    }
                }
            } else {
                c.close();
            }
            int targetCount = targetAPI.size();
            Object[] params = new Object[targetCount * 2];
            for (int j2 = 0; j2 < targetCount; j2++) {
                params[j2 * 2] = targetAPI.get(j2);
                params[(j2 * 2) + 1] = apiOnly ? null : targetValue.get(j2);
            }
            return params;
        }
    }

    /* loaded from: classes.dex */
    protected class Version2 implements VersionDispatcher {
        protected Version2() {
        }

        @Override // com.sony.imaging.app.util.AvailableInfoInhMgr.VersionDispatcher
        public Object[] isAvailableHelper(Camera.Parameters p, CameraEx.ParametersModifier parametersModifier, MediaRecorder.Parameters parameters, AudioManager.Parameters parameters2, boolean apiOnly, ArrayList<String> paramAPI, ArrayList<String> paramValue) {
            ArrayList<String> targetAPI = AvailableInfoInhMgr.this.mTargetAPI;
            ArrayList<String> targetValue = AvailableInfoInhMgr.this.mTargetValue;
            HashMap<String, String> mustGetAPI = AvailableInfoInhMgr.this.mMustGetAPI;
            HashMap<String, String> mustGetClassName = AvailableInfoInhMgr.this.mMustGetClassName;
            if (!AvailableInfoInhMgr.this.useMemberList) {
                targetAPI = new ArrayList<>();
                targetValue = new ArrayList<>();
                mustGetAPI = new HashMap<>();
                mustGetClassName = new HashMap<>();
            }
            String API = paramAPI.remove(0);
            targetAPI.clear();
            targetAPI.add(API);
            if (!apiOnly) {
                targetValue.clear();
                targetValue.add(paramValue.remove(0));
            }
            StringBuilder selector = AvailableInfoInhMgr.this.SELCTORS.get();
            selector.replace(0, selector.length(), AvailableInfoInhMgr.GET_RELATION_VER2).append(API).append("'");
            if (AvailableInfo.DEBUG) {
                Log.i(AvailableInfoInhMgr.TAG, "start query: " + ((Object) selector));
            }
            Cursor c = AvailableInfoInhMgr.this.mDB.rawQuery(selector.toString(), null);
            if (AvailableInfo.DEBUG) {
                Log.i(AvailableInfoInhMgr.TAG, PortraitBeautyConstants.FINISHTAG);
            }
            int count = c.getCount();
            if (count != 0 && c.moveToFirst()) {
                mustGetAPI.clear();
                mustGetClassName.clear();
                String[] getAPIs = c.getString(0).split(",");
                String[] originalAPI = c.getString(1).split(",");
                String[] classNames = c.getString(2).split(",");
                c.close();
                int originalCount = originalAPI.length;
                for (int j = 0; j < originalCount; j++) {
                    String currentOriginalAPI = originalAPI[j];
                    if (!mustGetAPI.containsKey(currentOriginalAPI)) {
                        boolean found = false;
                        int paramCount = paramAPI.size();
                        int k = 0;
                        while (true) {
                            if (k >= paramCount) {
                                break;
                            }
                            if (!currentOriginalAPI.equals(paramAPI.get(k))) {
                                k++;
                            } else {
                                found = true;
                                mustGetAPI.remove(currentOriginalAPI);
                                mustGetClassName.remove(currentOriginalAPI);
                                targetAPI.add(currentOriginalAPI);
                                if (!apiOnly) {
                                    targetValue.add(paramValue.remove(k));
                                }
                                paramAPI.remove(k);
                            }
                        }
                        if (!found) {
                            mustGetAPI.put(currentOriginalAPI, getAPIs[j]);
                            mustGetClassName.put(currentOriginalAPI, classNames[j]);
                        }
                    }
                }
                Set<String> key = mustGetAPI.keySet();
                for (String setAPI : key) {
                    targetAPI.add(setAPI);
                    if (!apiOnly) {
                        String getAPI = mustGetAPI.get(setAPI);
                        String className = mustGetClassName.get(setAPI);
                        Object target = null;
                        if (AvailableInfoInhMgr.CAMERA_PARAMETERS.equals(className)) {
                            target = p;
                        } else if (AvailableInfoInhMgr.CAMERAEX_PARAMETERS.equals(className)) {
                            target = parametersModifier;
                        } else if (AvailableInfoInhMgr.MEDIARECORDER_PARAMETERS.equals(className)) {
                            target = parameters;
                        } else if (AvailableInfoInhMgr.AUDIOMANAGER_PARAMETERS.equals(className)) {
                            target = parameters2;
                        }
                        try {
                            Method func = target.getClass().getMethod(getAPI, (Class[]) null);
                            Object value = func.invoke(target, (Object[]) null);
                            targetValue.add(value.toString());
                        } catch (Exception e) {
                            throw new InvalidParameterException("could not found " + getAPI + " api in " + className);
                        }
                    }
                }
            } else {
                c.close();
            }
            int targetCount = targetAPI.size();
            Object[] params = new Object[targetCount * 2];
            for (int j2 = 0; j2 < targetCount; j2++) {
                params[j2 * 2] = targetAPI.get(j2);
                params[(j2 * 2) + 1] = apiOnly ? null : targetValue.get(j2);
            }
            return params;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.AvailableInfoImpl
    public void setFactor(String factorID, boolean value) {
        Log.w(TAG, "_setFactor is not supported.");
    }
}
