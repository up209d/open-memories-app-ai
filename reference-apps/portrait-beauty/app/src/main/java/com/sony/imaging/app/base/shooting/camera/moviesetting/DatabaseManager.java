package com.sony.imaging.app.base.shooting.camera.moviesetting;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.sysutil.ScalarProperties;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public class DatabaseManager {
    public static final String COMMA = ",";
    public static final String DISPLAY_EVF = "display_evf";
    public static final String DISPLAY_PANEL = "display_panel";
    protected static final String FIELD_BITRATE_ID = "bitrate_res_id";
    protected static final String FIELD_CAUTION_ID = "cautionId";
    protected static final String FIELD_DEFAULT = "isDefault";
    protected static final String FIELD_EVF_RES = "evf";
    protected static final String FIELD_FORMAT = "format";
    protected static final String FIELD_FORMAT_ID = "format_res_id";
    protected static final String FIELD_FRAMERATE_ID = "framerate_res_id";
    protected static final String FIELD_GUIDE_RES = "guideRes";
    protected static final String FIELD_ICON_RES = "iconRes";
    protected static final String FIELD_IMAGE_ID = "image_ids";
    protected static final String FIELD_PANEL_RES = "panel";
    protected static final String FIELD_PF_PROFILE = "profile";
    protected static final String FIELD_PF_VALUE = "pf_value";
    protected static final String FIELD_PRIMATY_KEY = "_id";
    protected static final String FIELD_TEXT_RES = "textRes";
    protected static final String FIELD_TVSYS = "tvSys";
    protected static final String FIELD_TVSYS_CONV = "tvSysConv";
    protected static final String FIELD_VALUE = "value";
    protected static final int INDEX_ASPECT = 2;
    protected static final int INDEX_BITRATE = 3;
    protected static final int INDEX_FORMAT = 0;
    protected static final int INDEX_FRAMERATE = 4;
    protected static final int INDEX_VIDEO_SIZE = 1;
    public static final String ITEM_MOVIE_FORMAT = "movie_format";
    public static final String ITEM_MOVIE_SETEING = "record_setting";
    protected static final int PF_VER_SUPPORTS_DATABASE = 14;
    protected static final int PROFILE_COLUMN_NUM = 5;
    public static final String SETTING_BITRATE = "setting_bitrate";
    public static final String SETTING_FORMAT = "setting_format";
    public static final String SETTING_FRAMERATE = "setting_framerate";
    protected static final String TBL_FORMAT_TO_ICONID = "format2Icons";
    protected static final String TBL_FORMAT_TO_MENU = "format2MenuRes";
    protected static final String TBL_ICONID_TO_RES = "icons";
    protected static final String TBL_PROFILE_TO_FORMAT = "profile2Format";
    protected static final String TBL_PROFILE_TO_VALUE = "profile2Value";
    protected static final String TBL_VALUE_TO_ICONID = "value2Icons";
    protected static final String TBL_VALUE_TO_MENU = "value2MenuRes";
    protected static final String VAL_DEFAULT = "o";
    public static final String VAL_NTSC = "N";
    public static final String VAL_PAL = "P";
    protected WeakHashMap<String, ResourceData> mCashIconId2Resource;
    protected Context mContext;
    protected static boolean DEBUG = false;
    private static final String TAG = DatabaseManager.class.getSimpleName();
    protected static final String FIELD_PF_FORMAT = "outputFormat";
    protected static final String FIELD_PF_VIDEO_SIZE = "videoSize";
    protected static final String FIELD_PF_ASPECT = "videoAspectRatio";
    protected static final String FIELD_PF_BITRATE = "videoEncodingBitRate";
    protected static final String FIELD_PF_FRAMERATE = "videoFrameRate";
    protected static final String[] PROFILE_COLUMNS = {FIELD_PF_FORMAT, FIELD_PF_VIDEO_SIZE, FIELD_PF_ASPECT, FIELD_PF_BITRATE, FIELD_PF_FRAMERATE};
    private static DatabaseManager sInstance = null;
    protected DatabaseAccessorBase baseAccs = null;
    protected DatabaseAccessorBase eachAccs = null;
    protected String m_default_format = null;
    protected List<String> m_supported_formats = null;
    protected HashMap<String, List<String>> m_supported_values = null;
    protected HashMap<String, String> m_default_values = null;
    protected HashMap<String, String> m_profiles = null;
    protected HashMap<String, String> m_format_profiles = null;
    protected HashMap<String, String> m_tv_system_conv = null;
    protected HashMap<String, WeakHashMap<String, String>> mCashValue2IconId = new HashMap<>();

    /* loaded from: classes.dex */
    public static class DBMenuItem {
        public ArrayList<String> cautionID;
        public String guideRes;
        public String iconRes;
        public String textRes;
        public String value;
    }

    /* loaded from: classes.dex */
    public static class ResourceData {
        public Object mEvfRes;
        public Object mPanelRes;
    }

    protected DatabaseManager() {
        this.mCashValue2IconId.put(SETTING_BITRATE, new WeakHashMap<>());
        this.mCashValue2IconId.put(SETTING_FRAMERATE, new WeakHashMap<>());
        this.mCashValue2IconId.put(SETTING_FORMAT, new WeakHashMap<>());
        this.mCashIconId2Resource = new WeakHashMap<>();
        setInstance(this);
    }

    protected static void setInstance(DatabaseManager manager) {
        if (sInstance == null) {
            sInstance = manager;
        }
    }

    public static DatabaseManager getInstance() {
        if (sInstance == null) {
            new DatabaseManager();
        }
        return sInstance;
    }

    public DatabaseAccessorBase getEachDatabaseAccessor() {
        return null;
    }

    public void initialize(Context context) {
        if (Environment.getVersionPfAPI() >= 14) {
            this.mContext = context;
            this.baseAccs = new BaseDatabaseAccessor();
            this.eachAccs = getEachDatabaseAccessor();
            if (this.baseAccs != null) {
                this.baseAccs.initialize(context);
            }
            if (this.eachAccs != null) {
                this.eachAccs.initialize(context);
            }
            if (this.baseAccs != null) {
                this.baseAccs.openDatabase();
            }
            if (this.eachAccs != null) {
                this.eachAccs.openDatabase();
            }
            this.m_tv_system_conv = createTvSystemConvTable(1 == ScalarProperties.getInt("signal.frequency") ? VAL_NTSC : VAL_PAL);
            Log.i(TAG, "initialize");
        }
    }

    public void terminate() {
        if (this.baseAccs != null) {
            this.baseAccs.closeDatabase();
            this.baseAccs = null;
        }
        if (this.eachAccs != null) {
            this.eachAccs.closeDatabase();
            this.eachAccs = null;
        }
        this.m_default_format = null;
        this.m_default_values = null;
        this.m_supported_formats = null;
        this.m_supported_values = null;
        this.m_profiles = null;
        this.m_format_profiles = null;
        this.m_tv_system_conv = null;
        Log.i(TAG, "terminate");
    }

    public boolean isReady() {
        return this.baseAccs != null && this.baseAccs.isDbExisted();
    }

    protected Cursor searchData(String tablename, String columname, String columval) {
        return searchData(tablename, new String[]{columname}, new String[]{columval}, false, null);
    }

    protected Cursor searchData(String tablename, String[] columnames, String[][] columvals, String orderBy) {
        Cursor c = null;
        if (this.eachAccs != null) {
            c = this.eachAccs.searchData(tablename, columnames, columvals, orderBy);
        }
        if (c == null && this.baseAccs != null) {
            c = this.baseAccs.searchData(tablename, columnames, columvals, orderBy);
        }
        if (DEBUG) {
            Log.d(TAG, "searchData cursor:" + c);
        }
        return c;
    }

    protected Cursor searchData(String tablename, String[] columnames, String[] columvals, boolean acceptNull, String orderBy) {
        Cursor c = null;
        if (this.eachAccs != null) {
            c = this.eachAccs.searchData(tablename, columnames, columvals, acceptNull, orderBy);
        }
        if (c == null && this.baseAccs != null) {
            c = this.baseAccs.searchData(tablename, columnames, columvals, acceptNull, orderBy);
        }
        if (DEBUG) {
            Log.d(TAG, "searchData cursor:" + c);
        }
        return c;
    }

    public Cursor searchDistinctData(String tablename, String columname, String orderBy) {
        Cursor c = null;
        if (this.eachAccs != null) {
            c = this.eachAccs.searchDistinctData(tablename, columname, orderBy);
        }
        if (c == null && this.baseAccs != null) {
            c = this.baseAccs.searchDistinctData(tablename, columname, orderBy);
        }
        if (DEBUG) {
            Log.d(TAG, "searchDistinctData cursor:" + c);
        }
        return c;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0112, code lost:            if ((-1) != r20) goto L16;     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0118, code lost:            if (r9.isAfterLast() != false) goto L91;     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x011a, code lost:            r35 = r9.getString(r25);     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0120, code lost:            if (r35 == null) goto L32;     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0122, code lost:            android.util.Log.d(com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseManager.TAG, r35);        r12 = r9.getString(r21);     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0137, code lost:            if (r34.contains(r12) != false) goto L23;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0139, code lost:            r34.add(r12);     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x013e, code lost:            r28 = r5.get(r12);     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0144, code lost:            if (r28 != null) goto L26;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0146, code lost:            r28 = new java.util.ArrayList<>();        r5.put(r12, r28);     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0158, code lost:            if (r28.contains(r35) != false) goto L29;     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x015a, code lost:            r28.add(r35);        r29.put(r35, r9.getString(r24));     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0170, code lost:            r27 = r9.getString(r20);     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0180, code lost:            if (com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseManager.VAL_DEFAULT.equals(r27) == false) goto L32;     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0182, code lost:            r11.put(r12, r35);     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x018b, code lost:            if (r9.moveToNext() != false) goto L92;     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0257, code lost:            if ((-1) != r22) goto L55;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x025d, code lost:            if (r9.isAfterLast() != false) goto L94;     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x025f, code lost:            r35 = r9.getString(r25);     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x0265, code lost:            if (r35 == null) goto L84;     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0269, code lost:            if (com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseManager.DEBUG == false) goto L62;     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x026b, code lost:            android.util.Log.d(com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseManager.TAG, r35);     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0278, code lost:            if (r34.contains(r35) != false) goto L65;     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x027a, code lost:            r34.add(r35);     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x027d, code lost:            r27 = r9.getString(r20);     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x028d, code lost:            if (com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseManager.VAL_DEFAULT.equals(r27) == false) goto L68;     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x028f, code lost:            r10 = r35;     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x0291, code lost:            r31 = new com.sony.scalar.media.MediaRecorder.Parameters();        r30 = r9.getString(r23);     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x029c, code lost:            if (r30 == null) goto L71;     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x029e, code lost:            r31.setOutputFormat(r30);     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x02a5, code lost:            r37 = r9.getString(r26);     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x02ab, code lost:            if (r37 == null) goto L74;     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x02ad, code lost:            r31.setVideoSize(r37);     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x02b4, code lost:            r6 = r9.getString(r18);     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x02ba, code lost:            if (r6 == null) goto L77;     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x02bc, code lost:            r31.setVideoAspectRatio(r6);     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x02c1, code lost:            r7 = r9.getString(r19);     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x02c7, code lost:            if (r7 == null) goto L80;     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x02c9, code lost:            r31.setVideoEncodingBitRate(r7);     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x02ce, code lost:            r14 = r9.getString(r22);     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x02d4, code lost:            if (r14 == null) goto L83;     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x02d6, code lost:            r31.setVideoFrameRate(r14);     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x02db, code lost:            r13.put(r35, r31.flatten());     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x02ea, code lost:            if (r9.moveToNext() != false) goto L96;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void updateSupportedProfiles(java.util.List<com.sony.scalar.media.MediaRecorder.CamcorderProfile> r43) {
        /*
            Method dump skipped, instructions count: 785
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseManager.updateSupportedProfiles(java.util.List):void");
    }

    public List<String> getSupportedFormat() {
        return this.m_supported_formats;
    }

    public List<String> getSupportedSetting(String format) {
        if (format == null) {
            Collection<List<String>> values = this.m_supported_values.values();
            if (values == null || values.isEmpty()) {
                return null;
            }
            Iterator<List<String>> it = this.m_supported_values.values().iterator();
            List<String> l = new ArrayList<>();
            while (it.hasNext()) {
                l.addAll(it.next());
            }
            return l;
        }
        return this.m_supported_values.get(format);
    }

    public String getDefaultFormat() {
        return this.m_default_format;
    }

    public String getDefaultSetting(String format) {
        if (this.m_default_values != null) {
            return this.m_default_values.get(format);
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0034, code lost:            if ((-1) != r2) goto L12;     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x003a, code lost:            if (r0.isAfterLast() != false) goto L20;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x003c, code lost:            r3.put(r0.getString(r2), r0.getString(r1));     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x004b, code lost:            if (r0.moveToNext() != false) goto L22;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.util.HashMap<java.lang.String, java.lang.String> createTvSystemConvTable(java.lang.String r9) {
        /*
            r8 = this;
            r7 = -1
            java.lang.String r5 = "createTvSystemConvTable"
            com.sony.imaging.app.util.PTag.start(r5)
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            int r5 = com.sony.imaging.app.util.Environment.getVersionPfAPI()
            r6 = 2
            if (r5 < r6) goto L50
            java.lang.String r5 = "N"
            boolean r5 = r5.equals(r9)
            if (r5 == 0) goto L56
            java.lang.String r4 = "P"
        L1c:
            java.lang.String r5 = "profile2Value"
            java.lang.String r6 = "tvSys"
            android.database.Cursor r0 = r8.searchData(r5, r6, r4)
            if (r0 == 0) goto L50
            java.lang.String r5 = "value"
            int r2 = r0.getColumnIndex(r5)
            java.lang.String r5 = "tvSysConv"
            int r1 = r0.getColumnIndex(r5)
            if (r7 == r1) goto L4d
            if (r7 == r2) goto L4d
        L36:
            boolean r5 = r0.isAfterLast()
            if (r5 != 0) goto L4d
            java.lang.String r5 = r0.getString(r2)
            java.lang.String r6 = r0.getString(r1)
            r3.put(r5, r6)
            boolean r5 = r0.moveToNext()
            if (r5 != 0) goto L36
        L4d:
            r0.close()
        L50:
            java.lang.String r5 = "createTvSystemConvTable"
            com.sony.imaging.app.util.PTag.end(r5)
            return r3
        L56:
            java.lang.String r4 = "N"
            goto L1c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.base.shooting.camera.moviesetting.DatabaseManager.createTvSystemConvTable(java.lang.String):java.util.HashMap");
    }

    public String convertTvSystem(String setting) {
        if (this.m_tv_system_conv != null && this.m_tv_system_conv.containsKey(setting)) {
            return this.m_tv_system_conv.get(setting);
        }
        return setting;
    }

    public HashMap<String, String> getTvSystemConvTable(String targetTvSystem) {
        String current = 1 == ScalarProperties.getInt("signal.frequency") ? VAL_NTSC : VAL_PAL;
        if (current.equals(targetTvSystem)) {
            return this.m_tv_system_conv;
        }
        return createTvSystemConvTable(targetTvSystem);
    }

    public String profile2value(MediaRecorder.CamcorderProfile profile) {
        return profile2value(profile.outputFormat, profile.videoSize, profile.videoAspectRatio, profile.videoEncodingBitRate, profile.videoFrameRate);
    }

    public String profile2value(String outputFormat, String videoSize, String videoAspectRatio, String videoEncodingBitRate, String videoFrameRate) {
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        String profile = builder.replace(0, builder.length(), outputFormat).append(",").append(videoSize).append(",").append(videoAspectRatio).append(",").append(videoEncodingBitRate).append(",").append(videoFrameRate).toString();
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        Set<Map.Entry<String, String>> set = this.m_profiles.entrySet();
        for (Map.Entry<String, String> entry : set) {
            if (profile.equals(entry.getValue())) {
                String value = entry.getKey();
                return value;
            }
        }
        return null;
    }

    public MediaRecorder.CamcorderProfile value2profile(String value) {
        String profile = this.m_profiles.get(value);
        String[] profiles = profile.split(",");
        MediaRecorder.CamcorderProfile camcorderProfile = null;
        try {
            try {
                camcorderProfile = (MediaRecorder.CamcorderProfile) MediaRecorder.CamcorderProfile.class.newInstance();
            } catch (IllegalAccessException e) {
                Constructor<? extends MediaRecorder.CamcorderProfile> constructor = MediaRecorder.CamcorderProfile.class.getDeclaredConstructor(new Class[0]);
                constructor.setAccessible(true);
                camcorderProfile = (MediaRecorder.CamcorderProfile) constructor.newInstance(new Object[0]);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        if (camcorderProfile != null) {
            camcorderProfile.outputFormat = profiles[0];
            camcorderProfile.videoSize = profiles[1];
            camcorderProfile.videoAspectRatio = profiles[2];
            camcorderProfile.videoEncodingBitRate = profiles[3];
            camcorderProfile.videoFrameRate = profiles[4];
        }
        return camcorderProfile;
    }

    public MediaRecorder.Parameters format2parameters(String format) {
        String flattened = this.m_format_profiles.get(format);
        if (flattened == null) {
            return null;
        }
        MediaRecorder.Parameters params = new MediaRecorder.Parameters();
        params.unflatten(flattened);
        return params;
    }

    public String params2format(MediaRecorder.Parameters params) {
        PTag.start("params2format");
        String format = null;
        MediaRecorder.Parameters supported = new MediaRecorder.Parameters();
        Set<Map.Entry<String, String>> set = this.m_format_profiles.entrySet();
        for (Map.Entry<String, String> entry : set) {
            supported.unflatten(entry.getValue());
            String outputFormat = supported.getOutputFormat();
            String videoSize = supported.getVideoSize();
            String aspect = supported.getVideoAspectRatio();
            String bitrate = supported.getVideoEncodingBitRate();
            String frameRate = supported.getVideoFrameRate();
            if (outputFormat == null || outputFormat.equals(params.getOutputFormat())) {
                if (videoSize == null || videoSize.equals(params.getVideoSize())) {
                    if (aspect == null || aspect.equals(params.getVideoAspectRatio())) {
                        if (bitrate == null || bitrate.equals(params.getVideoEncodingBitRate())) {
                            if (frameRate == null || frameRate.equals(params.getVideoFrameRate())) {
                                String format2 = entry.getKey();
                                format = format2;
                                break;
                            }
                        }
                    }
                }
            }
        }
        PTag.end("params2format");
        return format;
    }

    public int getResourceId(String setting, String value, String display) {
        Cursor resID2Cursor;
        PTag.start("getResourceId " + value);
        if (value == null) {
            return 0;
        }
        int ret = 0;
        boolean isPanel = DISPLAY_PANEL.equals(display);
        WeakHashMap<String, String> map = this.mCashValue2IconId.get(setting);
        if (map == null) {
            Log.w(TAG, "getResourceId invalid setting : " + setting);
            return 0;
        }
        String imageId = map.get(value);
        if (imageId == null) {
            if (SETTING_FORMAT.equals(setting)) {
                Cursor resIdCursor = searchData(TBL_FORMAT_TO_ICONID, FIELD_VALUE, value);
                if (resIdCursor != null) {
                    int idxFormatId = resIdCursor.getColumnIndex(FIELD_FORMAT_ID);
                    if (idxFormatId != -1) {
                        imageId = resIdCursor.getString(idxFormatId);
                        map.put(value, imageId);
                    }
                    resIdCursor.close();
                }
            } else {
                Cursor resIdCursor2 = searchData(TBL_VALUE_TO_ICONID, FIELD_VALUE, value);
                if (resIdCursor2 != null) {
                    int idxBitrateId = resIdCursor2.getColumnIndex(FIELD_BITRATE_ID);
                    int idxFramerateId = resIdCursor2.getColumnIndex(FIELD_FRAMERATE_ID);
                    if (idxBitrateId != -1 && idxFramerateId != -1) {
                        String bitrateId = resIdCursor2.getString(idxBitrateId);
                        String framerateId = resIdCursor2.getString(idxFramerateId);
                        if (SETTING_BITRATE.equals(setting)) {
                            map.put(value, bitrateId);
                            imageId = bitrateId;
                            this.mCashValue2IconId.get(SETTING_FRAMERATE).put(value, framerateId);
                        } else {
                            map.put(value, framerateId);
                            imageId = framerateId;
                            this.mCashValue2IconId.get(SETTING_BITRATE).put(value, bitrateId);
                        }
                    }
                    resIdCursor2.close();
                }
            }
        }
        ResourceData data = this.mCashIconId2Resource.get(imageId);
        if (data != null) {
            Log.d(TAG, "getResourceId return from cache");
            Object resId = isPanel ? data.mPanelRes : data.mEvfRes;
            if (resId != null && (resId instanceof Integer)) {
                return ((Integer) resId).intValue();
            }
        } else if (imageId != null && (resID2Cursor = searchData(TBL_ICONID_TO_RES, FIELD_IMAGE_ID, imageId)) != null) {
            int indexPanel = resID2Cursor.getColumnIndex(FIELD_PANEL_RES);
            int indexEvf = resID2Cursor.getColumnIndex(FIELD_EVF_RES);
            if (indexPanel != -1 && indexEvf != -1) {
                data = new ResourceData();
                data.mPanelRes = resID2Cursor.getString(indexPanel);
                data.mEvfRes = resID2Cursor.getString(indexEvf);
                this.mCashIconId2Resource.put(imageId, data);
            }
            resID2Cursor.close();
        }
        if (data != null) {
            Object res = isPanel ? data.mPanelRes : data.mEvfRes;
            if (res != null && (res instanceof String)) {
                ret = this.mContext.getResources().getIdentifier((String) res, "drawable", this.mContext.getPackageName());
                if (ret == 0) {
                    ret = this.mContext.getResources().getIdentifier((String) res, "drawable", "android");
                }
                if (isPanel) {
                    data.mPanelRes = Integer.valueOf(ret);
                } else {
                    data.mEvfRes = Integer.valueOf(ret);
                }
            }
        }
        PTag.end("getResourceId");
        return ret;
    }

    public List<DBMenuItem> getMenuItem(String itemId) {
        Cursor itemIdCursor;
        List<DBMenuItem> subItemArray = null;
        if (itemId != null) {
            subItemArray = new ArrayList<>();
            String table = null;
            if ("movie_format".equals(itemId)) {
                table = TBL_FORMAT_TO_MENU;
            } else if ("record_setting".equals(itemId)) {
                table = TBL_VALUE_TO_MENU;
            }
            if (table != null && (itemIdCursor = searchData(table, null, null, false, "_id")) != null) {
                int columValue = itemIdCursor.getColumnIndex(FIELD_VALUE);
                int columTextRes = itemIdCursor.getColumnIndex(FIELD_TEXT_RES);
                int columGuideRes = itemIdCursor.getColumnIndex(FIELD_GUIDE_RES);
                int columIconResNum = itemIdCursor.getColumnIndex(FIELD_ICON_RES);
                int columCautionId = itemIdCursor.getColumnIndex(FIELD_CAUTION_ID);
                while (!itemIdCursor.isAfterLast()) {
                    DBMenuItem subMenuItem = new DBMenuItem();
                    subMenuItem.value = itemIdCursor.getString(columValue);
                    subMenuItem.textRes = itemIdCursor.getString(columTextRes);
                    subMenuItem.guideRes = itemIdCursor.getString(columGuideRes);
                    subMenuItem.iconRes = itemIdCursor.getString(columIconResNum);
                    ArrayList<String> cautionId = new ArrayList<>();
                    cautionId.add(itemIdCursor.getString(columCautionId));
                    subMenuItem.cautionID = cautionId;
                    subItemArray.add(subMenuItem);
                    if (DEBUG) {
                        Log.d(TAG, "put " + subMenuItem.value);
                    }
                    if (!itemIdCursor.moveToNext()) {
                        break;
                    }
                }
                itemIdCursor.close();
            }
        }
        return subItemArray;
    }
}
