package com.sony.imaging.app.base.caution;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.base.caution.Info;
import com.sony.scalar.sysutil.didep.Caution;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class CautionUtilityClass {
    private static final String TAG = CautionUtilityClass.class.getSimpleName();
    private static Caution exCaution = new Caution();
    private static CautionUtilityClass cautionUtilityClass = new CautionUtilityClass();
    private static int[] DiademCautionTrigger = {Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID};
    private static int[] DLAppCautionTrigger = {Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID};
    private static int[] BootFailedCautionTrigger = {Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID};
    private static int[] CurrentCautionId = {Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID};
    private static ArrayList<cautionData> currentCautionData = new ArrayList<>();
    public DatabaseManager dbManager = DatabaseManager.getInstance();
    private int cameraMode = 2;
    private String[] currentMedia = {"0000"};
    private Caution.CautionCallback exCallback = new Caution.CautionCallback() { // from class: com.sony.imaging.app.base.caution.CautionUtilityClass.1
        public void onCallback(int[] list) {
            CautionUtilityClass.this.exCallback(list);
        }
    };
    private List<CautionCallback> CautionCallbackList = new ArrayList();
    private List<triggerCautionCallback> triggerCallbackList = new ArrayList();
    private _ExCallback _exCallback = new _ExCallback();
    private EventReducer exCallbackReducer = new EventReducer(this._exCallback);

    /* loaded from: classes.dex */
    public interface CautionCallback {
        void onCallback(int[] iArr);
    }

    /* loaded from: classes.dex */
    public interface triggerCautionCallback {
        void onCallback();
    }

    private CautionUtilityClass() {
        System.out.println("CautionUtilityClass created");
    }

    public static CautionUtilityClass getInstance() {
        return cautionUtilityClass;
    }

    public void initialized(Context context) {
        this.cameraMode = 2;
        this.currentMedia = new String[]{"0000"};
        this.dbManager.initialize(context);
        initCautionArray();
        RequestOperator.initialize();
        currentCautionData.clear();
    }

    public void terminated() {
        this.exCallbackReducer.clear();
        RequestOperator.terminate();
        initCautionArray();
        this.dbManager.terminate();
    }

    private void initCautionArray() {
        for (int i = 0; i < 6; i++) {
            DiademCautionTrigger[i] = 65535;
            DLAppCautionTrigger[i] = 65535;
            BootFailedCautionTrigger[i] = 65535;
            CurrentCautionId[i] = 65535;
        }
    }

    public void registerCallback(CautionCallback cb) {
        if (this.CautionCallbackList.isEmpty()) {
            executeCallback(CurrentCautionId);
        }
        addCallback(cb);
        Log.i(TAG, "registerCallback:" + cb);
    }

    public void unregisterCallback(CautionCallback cb) {
        removeCallback(cb);
        Log.i(TAG, "unregisterCallback:" + cb);
    }

    public void requestTrigger(int cautionId) {
        Log.i(TAG, "requestTrigger cautionId:" + cautionId);
        RequestOperator.commit(RequestOperator.createTrigger(0, cautionId, -1));
    }

    void _requestTrigger(int cautionId) {
        Log.i(TAG, "execute requestTrigger cautionId:" + cautionId);
        if ((cautionId & 32768) == 32768) {
            Caution.SetTrigger(cautionId, 1, true);
            return;
        }
        if ((cautionId & 131072) == 131072) {
            RsrcBase[] rsrcData = getResourceInfo(cautionId);
            if (rsrcData[0] != null) {
                for (RsrcBase rsrcBase : rsrcData) {
                    DLAppCautionTrigger[rsrcBase.cautionType] = cautionId;
                }
                CautionProcessing();
                return;
            }
            return;
        }
        Caution.SetTrigger(cautionId, 1, true);
    }

    public void disapperTrigger(int cautionId) {
        Log.i(TAG, "disapperTrigger cautionId:" + cautionId);
        RequestOperator.commit(RequestOperator.createTrigger(1, cautionId, -1));
    }

    void _disapperTrigger(int cautionId) {
        Log.i(TAG, "execute disapperTrigger cautionId:" + cautionId);
        if ((cautionId & 32768) == 32768) {
            Caution.SetTrigger(cautionId, 1, false);
            return;
        }
        if ((cautionId & 131072) == 131072) {
            RsrcBase[] rsrcData = getResourceInfo(cautionId);
            if (rsrcData[0] != null) {
                for (RsrcBase rsrcBase : rsrcData) {
                    DLAppCautionTrigger[rsrcBase.cautionType] = 65535;
                }
                CautionProcessing();
                return;
            }
            return;
        }
        if ((262144 & cautionId) == 262144) {
            BootFailedCautionTrigger[1] = 65535;
            CautionProcessing();
        } else {
            Caution.SetTrigger(cautionId, 1, false);
        }
    }

    public void disapperTrigger(int cautionId, int type) {
        Log.i(TAG, "disapperTrigger cautionId:" + cautionId);
        RequestOperator.commit(RequestOperator.createTrigger(1, cautionId, type));
    }

    void _disapperTrigger(int cautionId, int type) {
        Log.i(TAG, "execute disapperTrigger cautionId:" + cautionId);
        if ((cautionId & 131072) == 131072) {
            DLAppCautionTrigger[type] = 65535;
            CautionProcessing();
        } else if ((cautionId & Info.kind.BOOT_FAILED) == 262144) {
            BootFailedCautionTrigger[type] = 65535;
            CautionProcessing();
        } else {
            Caution.SetTrigger(cautionId, convertCautionTypeToExCautionId(type), false);
            Log.i(TAG, "SetTrigger Disappear cautionId:" + cautionId);
        }
    }

    public void requestFactor(int factorId) {
        Log.i(TAG, "requestFactor factorId:" + factorId);
        RequestOperator.commit(RequestOperator.createFactor(2, factorId));
    }

    void _requestFactor(int factorId) {
        Caution.SetFactor(factorId, true);
        Log.i(TAG, "execute requestFactor factorId:" + factorId);
    }

    public void disapperFactor(int factorId) {
        Log.i(TAG, "disapperFactor factorId:" + factorId);
        RequestOperator.commit(RequestOperator.createFactor(3, factorId));
    }

    void _disapperFactor(int factorId) {
        Caution.SetFactor(factorId, false);
        Log.i(TAG, "execute disapperFactor factorId:" + factorId);
    }

    public void setModeAndMedia(int mode, String[] media) {
        this.currentMedia = media;
        setMode(mode);
    }

    public void setMode(int mode) {
        Log.i(TAG, "setMode mode:" + mode + " media: " + this.currentMedia[0]);
        _setMode(mode);
    }

    void _setMode(int mode) {
        this.cameraMode = mode;
        Caution.SetMode(this.cameraMode, this.currentMedia);
        Log.i(TAG, "execute setMode mode:" + mode + " media: " + this.currentMedia[0]);
    }

    public int getMode() {
        Log.i(TAG, "getMode mode:" + this.cameraMode);
        return this.cameraMode;
    }

    public void setMedia(String[] media) {
        this.currentMedia = media;
        Caution.SetMode(this.cameraMode, this.currentMedia);
        Log.i(TAG, "setMedia media:" + Arrays.toString(media));
    }

    public String[] getMedia() {
        Log.i(TAG, "getMedia media:" + Arrays.toString(this.currentMedia));
        return this.currentMedia;
    }

    public RsrcBase[] getResourceInfo(int cautionId) {
        RsrcBase[] rsrcData;
        Log.i(TAG, "getResourceInfo cautionId:" + cautionId);
        int kind = judgeIdKind(cautionId);
        if (kind == 262144) {
            rsrcData = new RsrcBase[1];
            int inhFactorId = cautionId - Info.kind.BOOT_FAILED;
            Cursor c = this.dbManager.searchData("inh_factor_id", Integer.toString(inhFactorId), "bootFailedFactorIds", kind);
            if (c != null) {
                rsrcData[0] = new RsrcBase(c, 1, kind);
                c.close();
            }
        } else {
            try {
                Cursor c2 = this.dbManager.searchData("val", Integer.toString(cautionId), "cautionId2ResourceId", kind);
                ArrayList<String> rsrcId = new ArrayList<>();
                ArrayList<Integer> cautionType = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    String str = getRsrcId(c2, i);
                    if (str != null) {
                        rsrcId.add(str);
                        cautionType.add(Integer.valueOf(i));
                    }
                }
                if (c2 != null) {
                    c2.close();
                }
                rsrcData = new RsrcBase[rsrcId.size()];
                if (rsrcId.size() != 0) {
                    for (int i2 = 0; i2 < rsrcId.size(); i2++) {
                        if (rsrcId.get(i2) != null) {
                            Cursor c3 = this.dbManager.searchData("resourceId", rsrcId.get(i2), "resourceId2CautionData", kind);
                            rsrcData[i2] = new RsrcBase(c3, cautionType.get(i2).intValue(), kind);
                            c3.close();
                        }
                    }
                }
            } catch (CursorIndexOutOfBoundsException e) {
                return null;
            }
        }
        return rsrcData;
    }

    public RsrcBase[] getResourceInfo(int cautionId, int type) {
        Cursor c;
        Log.i(TAG, "getResourceInfo cautionId:" + cautionId + "type:" + type);
        RsrcBase[] rsrcData = new RsrcBase[1];
        String rsrcId = null;
        int kind = judgeIdKind(cautionId);
        if (kind == 262144) {
            int inhFactorId = cautionId - Info.kind.BOOT_FAILED;
            Cursor c2 = this.dbManager.searchData("inh_factor_id", Integer.toString(inhFactorId), "bootFailedFactorIds", kind);
            if (c2 != null) {
                rsrcData[0] = new RsrcBase(c2, type, kind);
                c2.close();
            }
        } else {
            Cursor c3 = this.dbManager.searchData("val", Integer.toString(cautionId), "cautionId2ResourceId", kind);
            if (c3 != null) {
                rsrcId = getRsrcId(c3, type);
                c3.close();
            }
            if (rsrcId != null && (c = this.dbManager.searchData("resourceId", rsrcId, "resourceId2CautionData", kind)) != null) {
                rsrcData[0] = new RsrcBase(c, type, kind);
                c.close();
            }
        }
        return rsrcData;
    }

    public int judgeIdKind(int cautionId) {
        int kind = 0;
        if ((cautionId & 131072) == 131072) {
            kind = 131072;
        } else if ((cautionId & Info.kind.BOOT_FAILED) == 262144) {
            kind = Info.kind.BOOT_FAILED;
        }
        Log.i(TAG, "judgeKind kind:" + kind);
        return kind;
    }

    private String getRsrcId(Cursor c, int type) {
        String str = null;
        String columnName = null;
        if (c != null) {
            if (type == 0) {
                columnName = "exclusion";
            } else if (type == 1) {
                columnName = "trigger";
            } else if (type == 2) {
                columnName = "stay";
            } else if (type == 3) {
                columnName = "selfdiag";
            } else if (type == 4) {
                columnName = "menu";
            } else if (type == 5) {
                columnName = "icon";
            }
            c.moveToFirst();
            do {
                str = c.getString(c.getColumnIndex(columnName));
                if (str != null) {
                    break;
                }
            } while (c.moveToNext());
        }
        return str;
    }

    private boolean getPriority(int cautionId) {
        Log.i(TAG, "getPriority cautionId:" + cautionId);
        boolean priority = false;
        int kind = judgeIdKind(cautionId);
        if (kind != 131072) {
            Log.e(TAG, "getPriority error kind:" + kind);
            return false;
        }
        Cursor c = this.dbManager.searchData("val", Integer.toString(cautionId), "cautionId2ResourceId", kind);
        String str = c.getString(c.getColumnIndex("cautionId"));
        c.close();
        try {
            Cursor c2 = this.dbManager.searchData("cautionId", str, "priority", kind);
            if (c2 != null) {
                int columnum = c2.getColumnIndex("priority");
                int DlAppPriority = c2.getInt(columnum);
                c2.close();
                if (DlAppPriority == 1) {
                    priority = true;
                }
            }
            return priority;
        } catch (SQLiteException e) {
            Log.i(TAG, "getPriority no data in priority table");
            return false;
        }
    }

    private int convertCautionTypeToExCautionId(int type) {
        Log.i(TAG, "convertCautionTypeToExcautionId type:" + type);
        switch (type) {
            case 0:
                return 16;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 8;
            case 4:
                return 32;
            case 5:
                return 4;
            default:
                Log.e(TAG, "convertCautionTypeToExCautionId error caution type");
                return Info.INVALID_CAUTION_ID;
        }
    }

    public int[] CurrentCautionId() {
        Log.i(TAG, "CurrentCautionId =" + Arrays.toString(CurrentCautionId));
        return CurrentCautionId;
    }

    public void initCurrentCautionId() {
        Log.i(TAG, "initCurrentCautionId");
        int[] initCautionId = {Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID, Info.INVALID_CAUTION_ID};
        judgeChangeCautionId(initCautionId);
        for (int i = 0; i < 6; i++) {
            CurrentCautionId[i] = initCautionId[i];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCurrentCautionData(int type, int kind, int id, Runnable terminate) {
        Log.i(TAG, "setCurrentCautionData type:" + type + "kind:" + kind + "id:" + id);
        cautionData cData = new cautionData();
        cData.type = type;
        cData.kind = kind;
        cData.maxPriorityId = id;
        cData.terminate = terminate;
        int i = 0;
        int count = currentCautionData.size();
        while (true) {
            if (i >= count) {
                break;
            }
            cautionData c = currentCautionData.get(i);
            if (c.type < cData.type) {
                i++;
            } else {
                currentCautionData.add(i, cData);
                cData = null;
                break;
            }
        }
        if (cData != null) {
            currentCautionData.add(cData);
        }
    }

    public cautionData getCurrentCautionData() {
        int size = currentCautionData.size();
        if (size != 0) {
            return currentCautionData.get(0);
        }
        return null;
    }

    public void removeCurrentCautionData(int[] cautionId) {
        int count = currentCautionData.size();
        int i = 0;
        Log.i(TAG, "removeCurrentCautionData count: " + Integer.toString(count));
        while (i != count) {
            cautionData c = currentCautionData.get(i);
            if (CurrentCautionId[c.type] != c.maxPriorityId) {
                currentCautionData.remove(i);
                Log.i(TAG, "removeCurrentCautionData c.terminate.run : " + c.terminate);
                if (c.terminate != null) {
                    c.terminate.run();
                }
                count--;
            } else {
                i++;
            }
        }
    }

    /* loaded from: classes.dex */
    public class cautionData {
        public int kind;
        public int maxPriorityId;
        Runnable terminate;
        public int type;

        public cautionData() {
        }
    }

    public void registerCallbackTriggerDisapper(triggerCautionCallback cb) {
        Log.i(TAG, "registerCallbackTriggerDisapper triggerCautionCallback:" + cb);
        addCallback(cb);
    }

    public void unregisterCallbackTriggerDisapper(triggerCautionCallback cb) {
        removeCallback(cb);
        Log.i(TAG, "unregisterCallbackTriggerDisapper triggerCautionCallback:" + cb);
    }

    public void bootFailFactor(int inhFactor) {
        Log.i(TAG, "bootFailFactor : factor =" + inhFactor);
        BootFailedCautionTrigger[1] = Info.kind.BOOT_FAILED + inhFactor;
        CautionProcessing();
    }

    public void bootFailedFactorDisapper() {
        Log.i(TAG, "bootFailedFactorDisapper");
        BootFailedCautionTrigger[1] = 65535;
        CautionProcessingFunction.executeTerminate();
    }

    public void terminateBootFailedCaution() {
        Log.i(TAG, "terminateBootFailedCaution");
        for (int i = 0; i < BootFailedCautionTrigger.length; i++) {
            BootFailedCautionTrigger[i] = 65535;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void exCallbackFunc(int[] cautionId) {
        Log.i(TAG, "Diadem Callback cautionId:" + Arrays.toString(cautionId));
        DiademCautionTrigger[0] = cautionId[4];
        DiademCautionTrigger[5] = cautionId[2];
        DiademCautionTrigger[4] = cautionId[5];
        DiademCautionTrigger[3] = cautionId[3];
        DiademCautionTrigger[2] = cautionId[1];
        DiademCautionTrigger[1] = cautionId[0];
    }

    public void exCallback(int[] list) {
        Log.i(TAG, "exCallback list:" + Arrays.toString(list));
        this._exCallback.list = list;
        this.exCallbackReducer.push();
    }

    public void registerExCallback() {
        try {
            exCaution.setCallback(this.exCallback);
        } catch (Exception e) {
            Log.i(TAG, "exCaution setCallback regist Exception error");
            e.printStackTrace();
        }
        Log.i(TAG, "registerExCallback");
    }

    public void unregisterExCallback() {
        try {
            exCaution.setCallback((Caution.CautionCallback) null);
        } catch (Exception e) {
            Log.i(TAG, "exCaution setCallback unregist Exception error");
            e.printStackTrace();
        }
        Log.i(TAG, "unregisterExCallback");
    }

    private int[] judgePriority() {
        int[] cautionId = new int[6];
        for (int i = 0; i < 6; i++) {
            if (DiademCautionTrigger[i] == 65535 && DLAppCautionTrigger[i] == 65535) {
                cautionId[i] = 65535;
            } else if (DiademCautionTrigger[i] == 65535) {
                cautionId[i] = DLAppCautionTrigger[i];
            } else if (DLAppCautionTrigger[i] == 65535) {
                cautionId[i] = DiademCautionTrigger[i];
            } else {
                boolean priority = getPriority(DLAppCautionTrigger[i]);
                if (priority) {
                    cautionId[i] = DLAppCautionTrigger[i];
                } else {
                    cautionId[i] = DiademCautionTrigger[i];
                }
            }
        }
        Log.i(TAG, "judgePriority cautionId:" + Arrays.toString(cautionId));
        return cautionId;
    }

    private void judgeChangeCautionId(int[] cautionId) {
        Log.i(TAG, "judgeChangeTriggerId cautionId:" + Arrays.toString(cautionId));
        if (getCurrentCautionData() != null) {
            if (CurrentCautionId[1] != 65535 && getCurrentCautionData().type == 1) {
                if (cautionId[1] != CurrentCautionId[1]) {
                    executeCallback();
                }
            } else if (CurrentCautionId[2] != 65535 && getCurrentCautionData().type == 2 && cautionId[2] != CurrentCautionId[2]) {
                executeCallback();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void CautionProcessing() {
        int[] cautionId;
        int cnt = 0;
        for (int i = 0; i < BootFailedCautionTrigger.length; i++) {
            if (BootFailedCautionTrigger[i] != 65535) {
                cnt++;
            }
        }
        if (cnt != 0) {
            cautionId = Arrays.copyOf(BootFailedCautionTrigger, BootFailedCautionTrigger.length);
        } else {
            cautionId = judgePriority();
        }
        executeCallback(cautionId);
        judgeChangeCautionId(cautionId);
        CurrentCautionId = cautionId;
        removeCurrentCautionData(cautionId);
        Log.i(TAG, "CautionProcessing CurrentCautionId:" + Arrays.toString(CurrentCautionId));
    }

    private void addCallback(CautionCallback cb) {
        this.CautionCallbackList.add(cb);
        Log.i(TAG, "addCallback cb:" + cb);
    }

    private void addCallback(triggerCautionCallback cb) {
        this.triggerCallbackList.add(cb);
        Log.i(TAG, "addCallback cb:" + cb);
    }

    private void removeCallback(CautionCallback cb) {
        this.CautionCallbackList.remove(cb);
        Log.i(TAG, "removeCallback cb:" + cb);
    }

    private void removeCallback(triggerCautionCallback cb) {
        this.triggerCallbackList.remove(cb);
        Log.i(TAG, "removeCallback cb:" + cb);
    }

    private void executeCallback(int[] cautionId) {
        ArrayList<CautionCallback> list = (ArrayList) ((ArrayList) this.CautionCallbackList).clone();
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            CautionCallback cb = i$.next();
            cb.onCallback(cautionId);
            Log.i(TAG, "executeCallback cautionId:" + Arrays.toString(cautionId));
        }
    }

    private void executeCallback() {
        ArrayList<triggerCautionCallback> list = (ArrayList) ((ArrayList) this.triggerCallbackList).clone();
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            triggerCautionCallback cb = i$.next();
            cb.onCallback();
            Log.i(TAG, "executeCallback");
        }
    }

    public void setTerminateCaution(CautionProcessingFunction.terminateCaution terminate) {
        CautionProcessingFunction.setTerminateCaution(terminate);
    }

    public void executeTerminate() {
        CautionProcessingFunction.executeTerminate();
    }

    public void setDispatchKeyEvent(int cautionId, IkeyDispatchEach mkey) {
        CautionProcessingFunction.setDispatchKeyEvent(cautionId, mkey);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class _ExCallback implements Runnable {
        int[] list;

        _ExCallback() {
        }

        @Override // java.lang.Runnable
        public void run() {
            CautionUtilityClass.this.exCallbackFunc(this.list);
            CautionUtilityClass.this.CautionProcessing();
            Log.i(CautionUtilityClass.TAG, "execute exCallback list:" + Arrays.toString(this.list));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class EventReducer {
        private int count = 0;
        private Handler handler = new Handler() { // from class: com.sony.imaging.app.base.caution.CautionUtilityClass.EventReducer.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (EventReducer.access$306(EventReducer.this) == 0) {
                    EventReducer.this.run.run();
                }
            }
        };
        private Runnable run;

        static /* synthetic */ int access$306(EventReducer x0) {
            int i = x0.count - 1;
            x0.count = i;
            return i;
        }

        public EventReducer(Runnable run) {
            this.run = run;
        }

        public void push() {
            this.run.run();
        }

        public void clear() {
            this.handler.removeMessages(0);
            this.count = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class RequestOperator {
        private static final int DISAPPER_FACTOR = 3;
        private static final int DISAPPER_TRIGGER = 1;
        private static final int INVALID = -1;
        private static final int REQUEST_FACTOR = 2;
        private static final int REQUEST_TRIGGER = 0;
        private static final int SET_MODE = 4;
        private static final String TAG = "CautionRequestOperator";
        private static Handler handler = new Handler() { // from class: com.sony.imaging.app.base.caution.CautionUtilityClass.RequestOperator.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (RequestOperator.access$506() == 0) {
                    Log.i(RequestOperator.TAG, "run()");
                    CautionUtilityClass caution = CautionUtilityClass.getInstance();
                    ArrayList<Operation> executionList = new ArrayList<>(RequestOperator.operationList);
                    RequestOperator.operationList.clear();
                    Iterator i$ = executionList.iterator();
                    while (i$.hasNext()) {
                        Operation op = i$.next();
                        if (4 == op.op) {
                            op.executed = true;
                            caution._setMode(op.mode);
                        }
                    }
                    Iterator i$2 = executionList.iterator();
                    while (i$2.hasNext()) {
                        Operation op2 = i$2.next();
                        if (!op2.executed) {
                            switch (op2.op) {
                                case 0:
                                    caution._requestTrigger(op2.cautionId);
                                    break;
                                case 1:
                                    if (-1 == op2.type) {
                                        caution._disapperTrigger(op2.cautionId);
                                        break;
                                    } else {
                                        caution._disapperTrigger(op2.cautionId, op2.type);
                                        break;
                                    }
                                case 2:
                                    caution._requestFactor(op2.factorId);
                                    break;
                                case 3:
                                    caution._disapperFactor(op2.factorId);
                                    break;
                            }
                        }
                    }
                }
            }
        };
        private static int commitCount = 0;
        private static ArrayList<Operation> operationList = new ArrayList<>();

        RequestOperator() {
        }

        static /* synthetic */ int access$506() {
            int i = commitCount - 1;
            commitCount = i;
            return i;
        }

        static void initialize() {
        }

        static void terminate() {
            while (true == handler.hasMessages(0)) {
                commitCount = 1;
                handler.removeMessages(0);
                handler.handleMessage(null);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public static class Operation {
            int cautionId;
            boolean executed = false;
            int factorId;
            int mode;
            int op;
            int type;

            Operation(int op) {
                this.op = op;
            }
        }

        static Operation createTrigger(int op, int cautionId, int type) {
            Operation ret = new Operation(op);
            ret.cautionId = cautionId;
            ret.type = type;
            return ret;
        }

        static Operation createFactor(int op, int factorId) {
            Operation ret = new Operation(op);
            ret.factorId = factorId;
            return ret;
        }

        static Operation createMode(int op, int mode) {
            Operation ret = new Operation(op);
            ret.mode = mode;
            return ret;
        }

        static void commit(Operation op) {
            Log.i(TAG, "commit: " + commitCount);
            commitCount++;
            operationList.add(op);
            handler.sendEmptyMessage(0);
        }
    }
}
