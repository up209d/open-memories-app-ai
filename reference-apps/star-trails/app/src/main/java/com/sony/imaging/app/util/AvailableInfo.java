package com.sony.imaging.app.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.hardware.Camera;
import android.util.SparseArray;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.sysutil.didep.Status;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public abstract class AvailableInfo {
    public static boolean DEBUG = false;
    private static AvailableInfoInhMgr defaultInfo = null;
    private static List<AvailableInfoImpl> additionalInfos = null;
    private static Context mContext = null;
    private static final String[] MEDIAOFFSET = {"_00", "_01", "_02", "_03", "_04", "_05", "_06", "_07"};
    private static StringBuilderThreadLocal mCacheKeyBuilder = new StringBuilderThreadLocal();
    private static HashMap<String, Integer> mediaIDs = new HashMap<>();
    private static HashMap<InhFactorInfo, List<IInhFactorChange>> mCallbackList = new HashMap<>();
    private static byte[] mPreviousFactor = null;
    protected static HashMap<IInhFactorChange, Status.InhFactorListenerMulti> mListenerMap = new HashMap<>();
    protected static SparseArray<String> mFactorIdArray = new SparseArray<>();
    private static ConcurrentHashMap<String, FactorsData> sFactorsDataCache = new ConcurrentHashMap<>();
    private static List<Object> sFactorsCacheFilter = Collections.unmodifiableList(new ArrayList());
    private static final Object sLockFactorsCacheFilter = new Object();

    /* loaded from: classes.dex */
    public interface IInhFactorChange {
        void onInhFactorChanged(String str, int i);
    }

    public static void initialize() {
        initialize(null);
    }

    public static void initialize(Context context) {
        if (defaultInfo == null) {
            defaultInfo = new AvailableInfoInhMgr();
            defaultInfo.initialize();
            if (8 <= Environment.getVersionPfAPI()) {
                mPreviousFactor = getFactors();
                byte[] newFilter = new byte[mPreviousFactor.length];
                for (int i = 0; i < mPreviousFactor.length - 1; i++) {
                    newFilter[i] = 0;
                }
                setInhFactorFilter(newFilter);
                defaultInfo.setInhFactorListener(new MyInhFactorListener());
            }
        }
        mContext = context;
    }

    public static void addDatabase(String DataBasePath) {
        if (additionalInfos == null) {
            additionalInfos = new ArrayList();
        } else {
            for (AvailableInfoImpl additionalInfo : additionalInfos) {
                additionalInfo.terminate();
            }
            additionalInfos.clear();
        }
        AvailableInfoImpl info = new AvailableInfoChangeable(mContext, DataBasePath);
        try {
            info.initialize();
            additionalInfos.add(info);
        } catch (SQLiteException e) {
            info.terminate();
        }
    }

    public static void terminate() {
        if (defaultInfo != null) {
            mCallbackList.clear();
            if (8 <= Environment.getVersionPfAPI()) {
                defaultInfo.setInhFactorListener(null);
            }
            defaultInfo.terminate();
            defaultInfo = null;
        }
        if (additionalInfos != null) {
            for (AvailableInfoImpl additionalInfo : additionalInfos) {
                additionalInfo.terminate();
            }
            additionalInfos.clear();
            additionalInfos = null;
        }
    }

    public static void update() {
        if (defaultInfo != null) {
            defaultInfo.update();
        }
    }

    public static void causeGC() {
        if (defaultInfo != null) {
            synchronized (defaultInfo) {
                System.gc();
            }
            return;
        }
        System.gc();
    }

    public static boolean isFactor(String factorID, String mediaID) {
        int offset = getMediaOffset(mediaID);
        return isFactor(factorID + MEDIAOFFSET[offset]);
    }

    public static boolean isFactor(String factorID) {
        boolean ret = false;
        if (defaultInfo != null) {
            ret = defaultInfo.isFactor(factorID);
        }
        if (!ret && additionalInfos != null) {
            for (AvailableInfoImpl additionalInfo : additionalInfos) {
                ret = additionalInfo.isFactor(factorID);
                if (ret) {
                    break;
                }
            }
        }
        return ret;
    }

    public static boolean isFactor(String factorID, Object... api) {
        boolean ret = false;
        if (defaultInfo != null) {
            Cursor featuresCursor = defaultInfo.getFeaturesCursor(api);
            Cursor factorsCursor = defaultInfo.getFactorsCursor(featuresCursor);
            ret = defaultInfo.isFactorWithCursor(factorID, factorsCursor);
            if (!ret && additionalInfos != null) {
                List<String> features = defaultInfo.getFeatures(featuresCursor);
                for (AvailableInfoImpl additionalInfo : additionalInfos) {
                    ret = additionalInfo.isInhibitionWithFactor(factorID, features);
                    if (ret) {
                        break;
                    }
                }
            }
            if (featuresCursor != null) {
                featuresCursor.close();
            }
            if (factorsCursor != null) {
                factorsCursor.close();
            }
        }
        return ret;
    }

    public static boolean isFactor(String factorID, Camera.Parameters p, CameraEx.ParametersModifier m, MediaRecorder.Parameters mp, AudioManager.Parameters ma, Object... api) {
        List<Object[]> list;
        boolean ret = false;
        if (defaultInfo != null && !(ret = defaultInfo.isFactor(factorID, (list = defaultInfo.getApiList(p, m, mp, ma, api)))) && additionalInfos != null) {
            for (Object[] objs : list) {
                FactorsData factorsData = getFactorsData(objs);
                for (AvailableInfoImpl additionalInfo : additionalInfos) {
                    ret = !additionalInfo.isInhibitionWithFactor(factorID, factorsData.getFeatures());
                    if (!ret) {
                        break;
                    }
                }
                if (!ret) {
                    break;
                }
            }
        }
        return ret;
    }

    public static boolean isFactor(String factorID, Camera.Parameters p, CameraEx.ParametersModifier m, Object... api) {
        return isFactor(factorID, p, m, null, null, api);
    }

    public static boolean isInhibition(String inhFeatureID, String mediaID) {
        int offset = getMediaOffset(mediaID);
        return isInhibition(inhFeatureID + MEDIAOFFSET[offset]);
    }

    public static boolean isInhibition(String inhFeatureID) {
        boolean ret = false;
        if (defaultInfo != null) {
            ret = defaultInfo.isInhibition(inhFeatureID);
        }
        if (!ret && additionalInfos != null) {
            for (AvailableInfoImpl additionalInfo : additionalInfos) {
                ret = additionalInfo.isInhibition(inhFeatureID);
                if (ret) {
                    break;
                }
            }
        }
        return ret;
    }

    public static boolean isAvailable(Object... api) {
        boolean ret = true;
        if (defaultInfo != null) {
            FactorsData factorsData = getFactorsData(api);
            ret = defaultInfo.isAvailable(factorsData.getFactors());
            if (ret && additionalInfos != null) {
                List<String> features = factorsData.getFeatures();
                for (AvailableInfoImpl additionalInfo : additionalInfos) {
                    ret = !additionalInfo.isInhibition(features);
                    if (!ret) {
                        break;
                    }
                }
            }
        }
        return ret;
    }

    public static boolean isAvailable(Camera.Parameters p, CameraEx.ParametersModifier m, MediaRecorder.Parameters mp, AudioManager.Parameters ma, Object... api) {
        List<Object[]> list;
        boolean ret = true;
        if (defaultInfo != null && (ret = defaultInfo.isAvailabel((list = defaultInfo.getApiList(p, m, mp, ma, api)))) && additionalInfos != null) {
            for (Object[] objs : list) {
                FactorsData factorsData = getFactorsData(objs);
                List<String> features = factorsData.getFeatures();
                for (AvailableInfoImpl additionalInfo : additionalInfos) {
                    ret = !additionalInfo.isInhibition(features);
                    if (!ret) {
                        break;
                    }
                }
                if (!ret) {
                    break;
                }
            }
        }
        return ret;
    }

    public static boolean isAvailable(Camera.Parameters p, CameraEx.ParametersModifier m, Object... api) {
        return isAvailable(p, m, null, null, api);
    }

    private static int getMediaOffset(String mediaID) {
        Integer offset = mediaIDs.get(mediaID);
        if (offset == null) {
            String[] _mID = {mediaID};
            int[] _ret = defaultInfo.getMediaOffsets(_mID);
            if (-1 == _ret[0]) {
                throw new InvalidParameterException("MediaID: " + mediaID + " doesn't exist!");
            }
            mediaIDs.put(mediaID, Integer.valueOf(_ret[0]));
            int ret = _ret[0];
            return ret;
        }
        int ret2 = offset.intValue();
        return ret2;
    }

    public static void setFactor(String factorID, boolean value) {
        if (additionalInfos != null) {
            for (AvailableInfoImpl additionalInfo : additionalInfos) {
                additionalInfo.setFactor(factorID, value);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InhFactorInfo {
        int mBit;
        int mIndex;
        String mfactorID;

        private InhFactorInfo() {
        }
    }

    public static void registerInhFactorListener(String factorID, IInhFactorChange callback) {
        if (8 <= Environment.getVersionPfAPI()) {
            byte[] currentFilter = getInhFactorFilter();
            InhFactorInfo inhFactorInfo = null;
            Map<InhFactorInfo, List<IInhFactorChange>> hash = mCallbackList;
            Iterator i$ = hash.keySet().iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                InhFactorInfo key = i$.next();
                if (key.mfactorID.equals(factorID)) {
                    inhFactorInfo = key;
                    break;
                }
            }
            if (inhFactorInfo == null) {
                inhFactorInfo = new InhFactorInfo();
            }
            byte[] newFilter = makeAddInhFactorFilter(factorID, currentFilter, inhFactorInfo);
            mPreviousFactor = getFactors();
            setInhFactorFilter(newFilter);
            putInhFactorInfoTable(inhFactorInfo, callback);
        }
    }

    public static void unregisterInhFactorListener(String factorID, IInhFactorChange callback) {
        if (8 <= Environment.getVersionPfAPI()) {
            byte[] currentFilter = getInhFactorFilter();
            InhFactorInfo inhFactorInfo = null;
            Map<InhFactorInfo, List<IInhFactorChange>> hash = mCallbackList;
            List<IInhFactorChange> value = null;
            Iterator i$ = hash.entrySet().iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                Map.Entry<InhFactorInfo, List<IInhFactorChange>> entry = i$.next();
                InhFactorInfo inhFactorInfo2 = entry.getKey();
                inhFactorInfo = inhFactorInfo2;
                if (inhFactorInfo.mfactorID.equals(factorID)) {
                    List<IInhFactorChange> value2 = entry.getValue();
                    value = value2;
                    break;
                }
            }
            if (value != null) {
                if (value.size() <= 1) {
                    byte[] newFilter = makeRemoveInhFactorFilter(factorID, currentFilter);
                    setInhFactorFilter(newFilter);
                    value.remove(callback);
                    mCallbackList.remove(inhFactorInfo);
                    return;
                }
                value.remove(callback);
            }
        }
    }

    private static byte[] getFactors() {
        if (defaultInfo == null) {
            return null;
        }
        byte[] ret = defaultInfo.getFactors();
        return ret;
    }

    private static byte[] getInhFactorFilter() {
        if (defaultInfo == null) {
            return null;
        }
        byte[] ret = defaultInfo.getInhFactorFilter();
        return ret;
    }

    private static byte[] makeAddInhFactorFilter(String factorID, byte[] currentFilter, InhFactorInfo inhFactorInfo) {
        int bit = -1;
        if (defaultInfo != null) {
            bit = defaultInfo.isFactorHelper(factorID);
        }
        int targetIndex = bit / 8;
        byte targetByte = currentFilter[targetIndex];
        int targetBit = bit % 8;
        byte tmpByte = (byte) (1 << targetBit);
        byte newByte = (byte) (targetByte | tmpByte);
        currentFilter[targetIndex] = newByte;
        inhFactorInfo.mfactorID = factorID;
        inhFactorInfo.mIndex = targetIndex;
        inhFactorInfo.mBit = targetBit;
        return currentFilter;
    }

    private static byte[] makeRemoveInhFactorFilter(String factorID, byte[] currentFilter) {
        int bit = -1;
        if (defaultInfo != null) {
            bit = defaultInfo.isFactorHelper(factorID);
        }
        int targetIndex = bit / 8;
        byte targetByte = currentFilter[targetIndex];
        int targetBit = bit % 8;
        byte tmpByte = (byte) (1 << targetBit);
        byte newByte = (byte) (targetByte ^ tmpByte);
        currentFilter[targetIndex] = newByte;
        return currentFilter;
    }

    private static boolean putInhFactorInfoTable(InhFactorInfo inhFactorInfo, IInhFactorChange callback) {
        boolean ret;
        List<IInhFactorChange> value = mCallbackList.get(inhFactorInfo);
        if (value != null) {
            if (value.contains(callback)) {
                return false;
            }
            ret = true;
        } else {
            value = new ArrayList<>();
            ret = true;
        }
        value.add(callback);
        mCallbackList.put(inhFactorInfo, value);
        return ret;
    }

    private static void setInhFactorFilter(byte[] inhFactorFilter) {
        if (defaultInfo != null) {
            defaultInfo.setInhFactorFilter(inhFactorFilter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MyInhFactorListener implements Status.InhFactorListener {
        private MyInhFactorListener() {
        }

        public void onChanged(byte[] inhFactor) {
            if (AvailableInfo.mCallbackList.size() > 0) {
                Map<InhFactorInfo, List<IInhFactorChange>> hash = AvailableInfo.mCallbackList;
                for (Map.Entry<InhFactorInfo, List<IInhFactorChange>> entry : hash.entrySet()) {
                    InhFactorInfo inhFactorInfo = entry.getKey();
                    byte previousByte = AvailableInfo.mPreviousFactor[inhFactorInfo.mIndex];
                    byte currentByte = inhFactor[inhFactorInfo.mIndex];
                    byte xorByte = (byte) (previousByte ^ currentByte);
                    byte targetByte = (byte) (1 << inhFactorInfo.mBit);
                    if ((xorByte & targetByte) != 0) {
                        List<IInhFactorChange> callbackList = entry.getValue();
                        for (IInhFactorChange callback : callbackList) {
                            callback.onInhFactorChanged(inhFactorInfo.mfactorID, (currentByte & targetByte) != 0 ? 1 : 0);
                        }
                    }
                }
            }
            byte[] unused = AvailableInfo.mPreviousFactor = inhFactor;
        }
    }

    public static void addInhFactorListener(String factorID, IInhFactorChange inhFactorChange) {
        int iFactorId;
        if (12 <= Environment.getVersionPfAPI() && defaultInfo != null && (iFactorId = defaultInfo.isFactorHelper(factorID)) != -1) {
            mFactorIdArray.append(iFactorId, factorID);
            Status.InhFactorListenerMulti inhFactorListenerConverter = mListenerMap.get(inhFactorChange);
            if (inhFactorListenerConverter == null) {
                inhFactorListenerConverter = new InhFactorListenerConverter(inhFactorChange);
                mListenerMap.put(inhFactorChange, inhFactorListenerConverter);
            }
            defaultInfo.setInhFactorListenerMulti(iFactorId, inhFactorListenerConverter);
        }
    }

    public static void removeInhFactorListener(IInhFactorChange appListener) {
        if (12 <= Environment.getVersionPfAPI()) {
            Status.InhFactorListenerMulti pfListener = mListenerMap.get(appListener);
            mListenerMap.remove(appListener);
            if (defaultInfo != null) {
                defaultInfo.unSetInhFactorListenerMulti(pfListener);
            }
        }
    }

    /* loaded from: classes.dex */
    protected static class InhFactorListenerConverter implements Status.InhFactorListenerMulti {
        protected IInhFactorChange mInhFactorChange;

        public InhFactorListenerConverter(IInhFactorChange inhFactorChange) {
            this.mInhFactorChange = inhFactorChange;
        }

        public void onChanged(int i, boolean flag) {
            String factorId = AvailableInfo.mFactorIdArray.get(i);
            this.mInhFactorChange.onInhFactorChanged(factorId, flag ? 1 : 0);
        }
    }

    /* loaded from: classes.dex */
    public static class FactorsData {
        List<byte[]> mfactors;
        List<String> mfeatures;

        public FactorsData(List<String> features, List<byte[]> factors) {
            this.mfeatures = Collections.unmodifiableList(features);
            this.mfactors = Collections.unmodifiableList(factors);
        }

        public List<String> getFeatures() {
            return this.mfeatures;
        }

        public List<byte[]> getFactors() {
            return this.mfactors;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:4:0x001d, code lost:            if (r2.moveToFirst() != false) goto L6;     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x001f, code lost:            r0 = r2.getBlob(0);        r1.add(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x002b, code lost:            if (r2.moveToNext() != false) goto L15;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static com.sony.imaging.app.util.AvailableInfo.FactorsData readFactorsData(java.lang.Object... r7) {
        /*
            com.sony.imaging.app.util.AvailableInfoInhMgr r6 = com.sony.imaging.app.util.AvailableInfo.defaultInfo
            android.database.Cursor r5 = r6.getFeaturesCursor(r7)
            com.sony.imaging.app.util.AvailableInfoInhMgr r6 = com.sony.imaging.app.util.AvailableInfo.defaultInfo
            java.util.List r4 = r6.getFeatures(r5)
            com.sony.imaging.app.util.AvailableInfoInhMgr r6 = com.sony.imaging.app.util.AvailableInfo.defaultInfo
            android.database.Cursor r2 = r6.getFactorsCursor(r5)
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            if (r2 == 0) goto L2d
            boolean r6 = r2.moveToFirst()
            if (r6 == 0) goto L2d
        L1f:
            r6 = 0
            byte[] r0 = r2.getBlob(r6)
            r1.add(r0)
            boolean r6 = r2.moveToNext()
            if (r6 != 0) goto L1f
        L2d:
            if (r5 == 0) goto L32
            r5.close()
        L32:
            if (r2 == 0) goto L37
            r2.close()
        L37:
            com.sony.imaging.app.util.AvailableInfo$FactorsData r3 = new com.sony.imaging.app.util.AvailableInfo$FactorsData
            r3.<init>(r4, r1)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.util.AvailableInfo.readFactorsData(java.lang.Object[]):com.sony.imaging.app.util.AvailableInfo$FactorsData");
    }

    public static FactorsData getFactorsData(Object... api) {
        StringBuilder builder = mCacheKeyBuilder.get();
        builder.replace(0, builder.length(), "");
        for (Object o : api) {
            builder.append(o);
        }
        String key = builder.toString();
        builder.trimToSize();
        FactorsData factorsData = sFactorsDataCache.get(key);
        if (factorsData == null) {
            factorsData = readFactorsData(api);
            if (checkCache(api)) {
                sFactorsDataCache.put(key, factorsData);
            }
        }
        return factorsData;
    }

    private static boolean checkCache(Object... api) {
        boolean z = false;
        if (api != null) {
            synchronized (sLockFactorsCacheFilter) {
                int len$ = api.length;
                int i$ = 0;
                while (true) {
                    if (i$ >= len$) {
                        break;
                    }
                    Object o = api[i$];
                    if (!sFactorsCacheFilter.contains(o)) {
                        i$++;
                    } else {
                        z = true;
                        break;
                    }
                }
            }
        }
        return z;
    }

    public static void setFactorsCacheFilter(Object[] filter) {
        synchronized (sLockFactorsCacheFilter) {
            sFactorsDataCache.clear();
            if (filter != null) {
                sFactorsCacheFilter = Collections.unmodifiableList(Arrays.asList(filter));
            } else {
                sFactorsCacheFilter = Collections.unmodifiableList(new ArrayList());
            }
        }
    }
}
