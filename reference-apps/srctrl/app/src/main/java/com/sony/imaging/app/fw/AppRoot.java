package com.sony.imaging.app.fw;

import android.app.Activity;
import android.app.DAConnectionManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.ClassDefinition;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.HDMIInfoWrapper;
import com.sony.imaging.app.util.OLEDWrapper;
import com.sony.imaging.app.util.PTag;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.didep.ScalarSystemManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/* loaded from: classes.dex */
public abstract class AppRoot extends Activity {
    private static final String BootFactorName = "param_boot_factor";
    private static final String FIELD_KEYCODE = "mKeyCode";
    private static final String FIELD_SCANCODE = "mScanCode";
    private static final String LOG_ACTION = " action: ";
    private static final String LOG_EVENT_TIME = " event time: ";
    private static final String LOG_FINISH_TIME = " finish time: ";
    private static final String LOG_KEY = "Key: ";
    private static final String LOG_THIN = "thin keycode: ";
    private static final String MSG_CONV_SCANCODE = "Unknown KeyCode: convert to ScanCode.";
    private static final int POSITION_UNKNOWN = -2147483647;
    private static final String SCANCODE_FIELD = "mScanCode";
    private static final String log_DLAppBoot = "DLApp Boot";
    private static final String log_DLAppBootRes = "DLApp Boot from Resume";
    private static final String log_DLAppShutdown = "DLApp Shutdown";
    private static final String log_layoutdoesnotclose = "There are the layouts which are not closed!";
    private static final String log_speacer = "  ";
    public StateHandle handle;
    protected IKeyDispatch mKeyDispatcher;
    View mView;
    public RootContainer root;
    private SystemReady systemReady;
    private static final String TAG = AppRoot.class.getSimpleName();
    private static final StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    static HashMap<Class<?>, ConstituentRecord> constituentTable = new HashMap<>(256);
    protected static boolean doAbortInCaseUncautghtException = false;
    private static Locale mLocale = Locale.getDefault();
    protected static Collection<Integer> sSlideKeys = new ArrayList();
    public ArrayList<Layout> layouts = new ArrayList<>(16);
    private KeyReceiver mCurrentState = new KeyReceiver();
    private MessageQueue.IdleHandler beepInitializeHandler = new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.fw.AppRoot.1
        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            BeepUtility.getInstance().prepare(AppRoot.this);
            return false;
        }
    };
    private boolean mCalledOnBoot = false;
    private EventHandler handler = new EventHandler();
    private HashMap<String, Object> data = new HashMap<>();
    private int mScanCode = -1;
    private int mPosition = POSITION_UNKNOWN;
    private int mAction = -1;
    private int mLastAction = -1;
    private long mFinishTime = -1;
    protected int mMerginTime = IntervalRecExecutor.INTVL_REC_INITIALIZED;
    protected Collection<Integer> mIgnoreThinKeys = getIgnoreThinKeys();

    /* loaded from: classes.dex */
    public enum FINISH_TYPE {
        ONLY_DACONNECTIONMANAGER,
        ONLY_ACTIVITY
    }

    /* loaded from: classes.dex */
    private interface IKeyDispatch {
        boolean dispatchKeyEvent(KeyEvent keyEvent);
    }

    public abstract OpenStateData _getStartApp();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void onCautionModeChanged(int i);

    static {
        sSlideKeys.add(Integer.valueOf(USER_KEYCODE.EV_DIAL_CHANGED));
        sSlideKeys.add(Integer.valueOf(USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED));
        sSlideKeys.add(Integer.valueOf(USER_KEYCODE.IRIS_DIAL_CHANGED));
        sSlideKeys.add(Integer.valueOf(USER_KEYCODE.MODE_DIAL_CHANGED));
        sSlideKeys.add(Integer.valueOf(USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED));
    }

    public static boolean getDoAbortInCaseUncautghtException() {
        return doAbortInCaseUncautghtException;
    }

    public void setCurrentState(KeyReceiver baseState) {
        this.mCurrentState = baseState;
    }

    public KeyReceiver getCurrentState() {
        return this.mCurrentState;
    }

    public void resetCurrentState() {
        this.mCurrentState = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class LayoutUpdater {
        private static final int ADD = 0;
        private static final String LOG_VIEW_CHANGED_FROM = " from ";
        private static final String LOG_VIEW_CHANGED_PREFIX = "View index changed : ";
        private static final String LOG_VIEW_CHANGED_TO = " to ";
        private static final int REMOVE = 1;
        private static final String TAG = "LayoutUpdater";
        private static final int UPDATE_FULL = 3;
        private static final int UPDATE_PARTIAL = 2;
        private static final int UPDATE_VIEW = 4;
        private static final String log_commit = "commit()";
        private static final String log_ftcommit = "ft.commit()";
        private static Activity mActivity;
        private static int commitCount = 0;
        private static ArrayList<Operation> operationList = new ArrayList<>();
        private static HashMap<Layout, Operation> reopening = new HashMap<>();
        private static ArrayList<Operation> executionList = new ArrayList<>(operationList);

        LayoutUpdater() {
        }

        static void initialize(Activity act) {
            mActivity = act;
        }

        static void terminate() {
            ((AppRoot) mActivity).handler.removeMessages(1);
            commitCount = 1;
            run();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public static class Operation {
            Bundle data;
            boolean execute;
            int id;
            Layout layout;
            int op;
            StackTraceElement[] stack;
            String tag;

            Operation(int o, Layout l) {
                this.stack = Thread.currentThread().getStackTrace();
                this.op = o;
                this.layout = l;
                this.execute = true;
                this.data = null;
            }

            Operation(int o, Layout l, Bundle aData) {
                this.stack = Thread.currentThread().getStackTrace();
                this.op = o;
                this.layout = l;
                this.execute = true;
                this.data = aData;
            }

            Operation(int o, int i, Layout l, String t, Bundle aData) {
                this.stack = Thread.currentThread().getStackTrace();
                this.op = o;
                this.layout = l;
                this.id = i;
                this.tag = t;
                this.execute = true;
                this.data = aData;
            }

            void dump() {
                String operation = "unknown";
                switch (this.op) {
                    case 0:
                        operation = "ADD";
                        break;
                    case 1:
                        operation = "REMOVE";
                        break;
                    case 2:
                        operation = "UPDATE_PARTIAL";
                        break;
                    case 3:
                        operation = "UPDATE_FULL";
                        break;
                    case 4:
                        operation = "UPDATE_VIEW";
                        break;
                }
                Log.e(LayoutUpdater.TAG, "Operation: " + operation);
                Log.e(LayoutUpdater.TAG, "Layout:    " + (this.layout == null ? "null" : this.layout.getClass().getSimpleName()));
                Log.e(LayoutUpdater.TAG, "Bundle:    " + (this.data == null ? "null" : this.data.toString()));
                int c = this.stack.length;
                for (int i = 0; i < c; i++) {
                    Log.e(LayoutUpdater.TAG, this.stack[i].toString());
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void commit() {
            commitCount++;
            Message msg = Message.obtain();
            msg.what = 1;
            ((AppRoot) mActivity).handler.sendMessageAtFrontOfQueue(msg);
            Log.d(TAG, log_commit);
        }

        static void run() {
            int i = commitCount - 1;
            commitCount = i;
            if (i == 0) {
                Log.d(TAG, log_ftcommit);
                reopening.clear();
                ArrayList<Layout> layouts = ((AppRoot) mActivity).layouts;
                int count = operationList.size();
                for (int i2 = 0; i2 < count; i2++) {
                    Operation target = operationList.get(i2);
                    if (target.execute) {
                        Layout layout = target.layout;
                        layout.data = target.data;
                        int j = i2 + 1;
                        while (true) {
                            if (j < count) {
                                Operation reference = operationList.get(j);
                                if (layout == reference.layout) {
                                    layout.data = reference.data;
                                    if (target.op == 0 && reference.op == 1) {
                                        target.execute = false;
                                        reference.execute = false;
                                        break;
                                    }
                                    if (target.op == 1 && reference.op == 0) {
                                        if (layouts.contains(layout)) {
                                            target.execute = false;
                                            reference.execute = false;
                                            if (reopening.containsKey(layout)) {
                                                reopening.remove(layout);
                                            }
                                            reopening.put(layout, reference);
                                        } else {
                                            target.execute = false;
                                        }
                                    } else if ((target.op == 3 && reference.op == 3) || (target.op == 2 && reference.op == 2)) {
                                        reference.execute = false;
                                    } else if (target.op == 3 && reference.op == 2) {
                                        reference.execute = false;
                                    } else if (target.op == 2 && reference.op == 3) {
                                        target.execute = false;
                                    }
                                }
                                j++;
                            }
                        }
                    }
                }
                ArrayList<Operation> swap = executionList;
                executionList = operationList;
                operationList = swap;
                operationList.clear();
                ArrayList<Layout> executed = new ArrayList<>(layouts.size() + executionList.size());
                ViewGroup main = (ViewGroup) ((AppRoot) mActivity).getView();
                Operation op = null;
                try {
                    int c = executionList.size();
                    for (int i3 = 0; i3 < c; i3++) {
                        op = executionList.get(i3);
                        if (!op.execute) {
                            if (reopening.containsValue(op) && layouts.remove(op.layout)) {
                                insert(layouts, op.layout);
                                op.layout.onReopened();
                            }
                        } else {
                            switch (op.op) {
                                case 0:
                                    if (layouts.contains(op.layout)) {
                                        layouts.remove(op.layout);
                                        insert(layouts, op.layout);
                                        op.layout.onReopened();
                                        break;
                                    } else {
                                        insert(layouts, op.layout);
                                        op.layout.start(mActivity);
                                        break;
                                    }
                                case 1:
                                    if (layouts.contains(op.layout)) {
                                        op.layout.stop();
                                        layouts.remove(op.layout);
                                        break;
                                    } else {
                                        Log.d(TAG, "The removed layout: " + op.layout.getClass().getSimpleName() + " is not opened");
                                        break;
                                    }
                                case 2:
                                    if (layouts.contains(op.layout)) {
                                        op.layout.restartPartial();
                                        break;
                                    } else {
                                        Log.d(TAG, "The updated(partial) layout: " + op.layout.getClass().getSimpleName() + " is not opened");
                                        break;
                                    }
                                case 3:
                                    if (layouts.contains(op.layout)) {
                                        op.layout.restartFull();
                                        break;
                                    } else {
                                        Log.d(TAG, "The updated(full) layout: " + op.layout.getClass().getSimpleName() + " is not opened");
                                        break;
                                    }
                                case 4:
                                    if (layouts.contains(op.layout)) {
                                        op.layout.detach();
                                        break;
                                    } else {
                                        Log.d(TAG, "The detached layout: " + op.layout.getClass().getSimpleName() + " is not opened");
                                        break;
                                    }
                            }
                        }
                        if (!executed.contains(op.layout)) {
                            executed.add(op.layout);
                        }
                    }
                    View lowerView = null;
                    Iterator i$ = layouts.iterator();
                    while (i$.hasNext()) {
                        Layout objectLayout = i$.next();
                        View view = objectLayout.getView();
                        if (view != null) {
                            if (executed.contains(objectLayout)) {
                                int objectIndex = main.indexOfChild(view);
                                int targetIndex = lowerView == null ? 0 : main.indexOfChild(lowerView) + 1;
                                if (objectIndex != targetIndex) {
                                    main.removeView(view);
                                    if (objectIndex < targetIndex) {
                                        targetIndex--;
                                    }
                                    StringBuilder log_string = AppRoot.sStringBuilder.get();
                                    log_string.replace(0, log_string.length(), LOG_VIEW_CHANGED_PREFIX).append(objectLayout.getClass().getSimpleName()).append(LOG_VIEW_CHANGED_FROM).append(objectIndex).append(LOG_VIEW_CHANGED_TO).append(targetIndex);
                                    Log.w(TAG, log_string.toString());
                                    main.addView(view, targetIndex);
                                }
                            }
                            lowerView = view;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((AppRoot) mActivity).dumpStates();
                    ((AppRoot) mActivity).dumpLayouts();
                    op.dump();
                    throw new RuntimeException("Application no longer exists.");
                }
            }
        }

        static void insert(ArrayList<Layout> layouts, Layout layout) {
            int targetDepth = layout.getDepth();
            int i = layouts.size();
            while (true) {
                if (i <= 0) {
                    break;
                }
                if (targetDepth < layouts.get(i - 1).getDepth()) {
                    i--;
                } else {
                    layouts.add(i, layout);
                    break;
                }
            }
            if (i == 0) {
                layouts.add(0, layout);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void remove(Layout layout) {
            Operation op = new Operation(1, layout);
            operationList.add(op);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void update(Layout layout, int type) {
            Operation op = null;
            switch (type) {
                case 1:
                    op = new Operation(2, layout, layout.data);
                    break;
                case 2:
                    op = new Operation(3, layout, layout.data);
                    break;
            }
            operationList.add(op);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void updateView(Layout layout) {
            Operation op = new Operation(4, layout, layout.data);
            operationList.add(op);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void add(int id, Layout layout, String tag, Bundle data) {
            Operation op = new Operation(0, id, layout, tag, data);
            operationList.add(op);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class StateUpdater {
        private static final int ADD = 0;
        private static final int REMOVE = 1;
        private static final int REPLACE = 2;
        private static final String TAG = "StateUpdater";
        private static final String log_commit = "state commit()";
        private static final String log_container_error = ") of this handle(state: ";
        private static final String log_container_prefix = "the container(state: ";
        private static final String log_container_suffix = ") is already expired";
        private static final String log_error = " to state: ";
        private static final String log_ftcommit = "ft.commit()";
        private static final String log_prefix = "this handle(state: ";
        private static final String log_suffix = ") already expired in ";
        private static Activity mActivity;
        private static int commitCount = 0;
        private static ArrayList<Operation> operationList = new ArrayList<>();
        private static ArrayList<Operation> executionList = new ArrayList<>(operationList);

        StateUpdater() {
        }

        static void initialize(Activity act) {
            mActivity = act;
        }

        static void terminate() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public static class Operation {
            ContainerState container;
            Bundle data;
            boolean execute;
            StateHandle handle;
            int op;
            StackTraceElement[] stack;
            State target;

            Operation(int o, ContainerState con, State l) {
                this.stack = Thread.currentThread().getStackTrace();
                this.op = o;
                this.target = l;
                this.handle = null;
                this.execute = true;
                this.data = null;
                this.container = con;
            }

            Operation(int o, ContainerState con, State l, StateHandle h, Bundle aData) {
                this.stack = Thread.currentThread().getStackTrace();
                this.op = o;
                this.target = l;
                this.handle = h;
                this.execute = true;
                this.data = aData;
                this.container = con;
            }

            void dump() {
                String operation = "unknown";
                switch (this.op) {
                    case 0:
                        operation = "ADD";
                        break;
                    case 1:
                        operation = "REMOVE";
                        break;
                    case 2:
                        operation = "REPLACE";
                        break;
                }
                Log.e(StateUpdater.TAG, "Operation: " + operation);
                Log.e(StateUpdater.TAG, "Target:    " + (this.target == null ? "null" : this.target.getClass().getSimpleName()));
                Log.e(StateUpdater.TAG, "Container: " + (this.container == null ? "null" : this.container.getClass().getSimpleName()));
                Log.e(StateUpdater.TAG, "Bundle:    " + (this.data == null ? "null" : this.data.toString()));
                Log.e(StateUpdater.TAG, "Handle:    alive: " + this.handle.isAlive() + " layer: " + this.handle.layer + " previous state: " + (this.handle.state == null ? "null" : this.handle.state.getClass().getSimpleName()));
                int c = this.stack.length;
                for (int i = 0; i < c; i++) {
                    Log.e(StateUpdater.TAG, this.stack[i].toString());
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void commit() {
            commitCount++;
            Message msg = Message.obtain();
            msg.what = 2;
            ((AppRoot) mActivity).handler.sendMessageAtFrontOfQueue(msg);
            Log.d(TAG, log_commit);
        }

        static void run() {
            int i = commitCount - 1;
            commitCount = i;
            if (i == 0) {
                Log.d(TAG, log_ftcommit);
                ArrayList<Operation> swap = executionList;
                executionList = operationList;
                operationList = swap;
                operationList.clear();
                Operation op = null;
                try {
                    int c = executionList.size();
                    for (int i2 = 0; i2 < c; i2++) {
                        op = executionList.get(i2);
                        State target = op.target;
                        ContainerState container = op.container;
                        switch (op.op) {
                            case 0:
                                if (!container.handle.isAlive()) {
                                    StringBuilder log_string = AppRoot.sStringBuilder.get();
                                    log_string.replace(0, log_string.length(), log_container_prefix).append(container.getClass().getSimpleName()).append(log_container_error).append(target.getClass().getSimpleName()).append(log_container_suffix);
                                    Log.e(TAG, log_string.toString());
                                    break;
                                } else {
                                    target.container = container;
                                    target.data = op.data;
                                    target.depth = container.getDepth() + 1;
                                    target.depthOffset = container.depthOffset / 2;
                                    int size = container.currentState.size();
                                    op.handle.layer = size == 0 ? 0 : container.currentState.get(size - 1).layer + 1;
                                    container.currentState.add(op.handle);
                                    target.handle = op.handle;
                                    target.start(mActivity);
                                    break;
                                }
                            case 1:
                                if (!op.handle.isAlive()) {
                                    StringBuilder log_string2 = AppRoot.sStringBuilder.get();
                                    log_string2.replace(0, log_string2.length(), log_prefix).append(op.handle.state.getClass().getSimpleName()).append(log_suffix).append(container.getClass().getSimpleName());
                                    Log.e(TAG, log_string2.toString());
                                    break;
                                } else {
                                    container.currentState.remove(op.handle);
                                    op.handle.state.remove();
                                    op.handle.expire();
                                    break;
                                }
                            case 2:
                                if (!container.handle.isAlive() || !op.handle.isAlive()) {
                                    StringBuilder log_string3 = AppRoot.sStringBuilder.get();
                                    log_string3.replace(0, log_string3.length(), log_prefix).append(target.handle.state.getClass().getSimpleName()).append(log_error).append(target.getClass().getSimpleName()).append(log_suffix).append(container.getClass().getSimpleName());
                                    Log.e(TAG, log_string3.toString());
                                    break;
                                } else {
                                    target.container = container;
                                    target.data = op.data;
                                    target.depth = container.getDepth() + 1;
                                    target.depthOffset = container.depthOffset / 2;
                                    State replaced = container.currentState.get(op.handle.layer).state;
                                    replaced.remove();
                                    replaced.handle = new StateHandle(-1, replaced);
                                    container.currentState.get(op.handle.layer).state = target;
                                    target.handle = op.handle;
                                    target.start(mActivity);
                                    break;
                                }
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((AppRoot) mActivity).dumpStates();
                    ((AppRoot) mActivity).dumpLayouts();
                    op.dump();
                    throw new RuntimeException("Application no longer exists.");
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void replace(ContainerState container, State target, StateHandle handle, Bundle data) {
            Operation op = new Operation(2, container, target, handle, data);
            operationList.add(op);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void remove(ContainerState container, StateHandle handle) {
            Operation op = new Operation(1, container, null, handle, null);
            operationList.add(op);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void add(ContainerState container, State target, StateHandle handle, Bundle data) {
            Operation op = new Operation(0, container, target, handle, data);
            operationList.add(op);
        }
    }

    void dumpStates() {
        if (this.root != null) {
            Log.e(TAG, "Current State Stack");
            dumpStates(this.root, "");
        } else {
            Log.e(TAG, "Current State is gone");
        }
    }

    void dumpStates(ContainerState target, String prefix) {
        Iterator i$ = target.currentState.iterator();
        while (i$.hasNext()) {
            StateHandle handle = i$.next();
            Log.e(TAG, prefix + "|-" + handle.state.getClass().getSimpleName());
            if (handle.state instanceof ContainerState) {
                dumpStates((ContainerState) handle.state, prefix + "| ");
            }
        }
    }

    void dumpLayouts() {
        Log.e(TAG, "Current Layout Stack");
        Iterator i$ = this.layouts.iterator();
        while (i$.hasNext()) {
            Layout layout = i$.next();
            Log.e(TAG, log_speacer + layout.getClass().getSimpleName());
        }
    }

    public View getView() {
        return this.mView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstituentRecord getConstituentRecord(Class<?> c) {
        return constituentTable.get(c);
    }

    public AppRoot() {
        this.systemReady = new SystemReady();
        PTag.startTimeTag(log_DLAppBoot);
        PTag.start(log_DLAppBoot);
        switch (Environment.DEVICE_TYPE) {
            case 3:
                this.mKeyDispatcher = new KeyDispatchForSyskaku();
                return;
            default:
                this.mKeyDispatcher = new KeyDispatchForQEMU();
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!doAbortInCaseUncautghtException) {
            Log.d(TAG, "Setting an UncaughtExceptionHandler.");
            Thread.setDefaultUncaughtExceptionHandler(new AppUncaughtExceptionHandler());
        }
        this.mView = new FrameLayout(this);
        this.mView.setId(getRootContainerId());
        setContentView(this.mView);
        ViewGroup.LayoutParams params = this.mView.getLayoutParams();
        params.width = -1;
        params.height = -1;
        this.mView.setLayoutParams(params);
        LayoutUpdater.initialize(this);
        StateUpdater.initialize(this);
        if (startSystemReadyOnCreate()) {
            this.systemReady.start();
        }
    }

    @Override // android.app.Activity
    protected final void onDestroy() {
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        HoldKeyServer.init(this);
        if (isLocaleChanged()) {
            onLocaleChanged();
        }
    }

    private boolean isLocaleChanged() {
        Locale locale = Locale.getDefault();
        if (mLocale.equals(locale)) {
            return false;
        }
        mLocale = locale;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onLocaleChanged() {
        Layout.clear();
    }

    @Override // android.app.Activity
    protected void onStop() {
        HoldKeyServer.term();
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onResume() {
        if (this.mAction == 2 && this.mLastAction == -1) {
            resetThinParams();
        }
        if (!this.systemReady.isReady()) {
            PTag.start(log_DLAppBootRes);
        }
        super.onResume();
        this.systemReady.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SystemReady implements ScalarSystemManager.SystemReadyEventListener {
        private static final int POLL_INTERVAL = 200;
        private static final int POLL_SYSTEM_READY_TIMEOUT = 2000;
        private long SystemReadyPollingStartInMilisec;
        private Handler mHandler;
        private boolean mSystemReady;
        private ScalarSystemManager ssManager;

        private SystemReady() {
            this.ssManager = null;
            this.mSystemReady = false;
            this.mHandler = null;
            this.SystemReadyPollingStartInMilisec = 0L;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class PollRunnable implements Runnable {
            private PollRunnable() {
            }

            @Override // java.lang.Runnable
            public void run() {
                Log.i(AppRoot.TAG, "system ready runnable");
                if (!ScalarSystemManager.isSystemReady()) {
                    if (SystemReady.this.mHandler != null) {
                        SystemReady.this.mHandler.postDelayed(new PollRunnable(), 200L);
                        return;
                    }
                    return;
                }
                SystemReady.this.onSystemReady();
            }
        }

        public void onSystemReady() {
            Log.i(AppRoot.TAG, "onSystemReady");
            if (this.mHandler != null) {
                this.mHandler.removeCallbacksAndMessages(null);
                this.mHandler = null;
            }
            if (this.ssManager != null) {
                callSystemReady();
                this.ssManager.unregisterSystemReadyObserver();
                this.ssManager = null;
            }
        }

        public boolean isReady() {
            return this.mSystemReady || this.ssManager != null;
        }

        private void callSystemReady() {
            RunStatus.setStatus(4);
            AppRoot.this.onSystemReady();
            this.mSystemReady = true;
        }

        public void start() {
            if (!this.mSystemReady && this.ssManager == null) {
                RunStatus.setStatus(1);
                if (ScalarSystemManager.isSystemReady()) {
                    callSystemReady();
                    return;
                }
                this.ssManager = new ScalarSystemManager();
                this.ssManager.registerSystemReadyObserver(this);
                if (this.mHandler == null) {
                    this.mHandler = new Handler();
                }
                this.SystemReadyPollingStartInMilisec = System.currentTimeMillis();
                this.mHandler.postDelayed(new PollRunnable(), 200L);
            }
        }

        public void stop() {
            if (this.mHandler != null) {
                this.mHandler.removeCallbacksAndMessages(null);
                this.mHandler = null;
            }
            if (this.ssManager != null) {
                this.ssManager.unregisterSystemReadyObserver();
                this.ssManager = null;
            }
            this.mSystemReady = false;
            RunStatus.setStatus(5);
        }
    }

    @Override // android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    protected boolean startSystemReadyOnCreate() {
        return true;
    }

    /* loaded from: classes.dex */
    public class OpenStateData {
        Bundle mData;
        String mName;

        public OpenStateData(String name) {
            this.mName = name;
        }

        public OpenStateData(AppRoot appRoot, String name, Bundle data) {
            this(name);
            this.mData = data;
        }
    }

    public void onSystemReady() {
        String bootFactor = getIntent().getStringExtra(BootFactorName);
        BootFactor.setBootFactor(bootFactor);
        BootFactor factor = BootFactor.get();
        onBoot(factor);
        this.root = new RootContainer();
        this.root.start(this);
        OpenStateData data = _getStartApp();
        this.handle = this.root.addChildState(data.mName, data.mData);
    }

    @Override // android.app.Activity
    public void finish() {
        finish(FINISH_TYPE.ONLY_DACONNECTIONMANAGER);
    }

    public void finish(FINISH_TYPE type) {
        PTag.start(log_DLAppShutdown);
        if (FINISH_TYPE.ONLY_DACONNECTIONMANAGER == type) {
            DAConnectionManager dacm = new DAConnectionManager(this);
            dacm.finish();
        }
        if (FINISH_TYPE.ONLY_ACTIVITY == type) {
            super.finish();
        }
        RunStatus.setStatus(3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onPause() {
        if (3 != RunStatus.getStatus()) {
            RunStatus.setStatus(2);
        }
        if (true == this.mCalledOnBoot) {
            onShutdown();
        }
        this.systemReady.stop();
        super.onPause();
        PTag.end(log_DLAppShutdown);
    }

    protected boolean doInputSkipKeyborad() {
        return Environment.isNewBizDeviceActionCam();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onBoot(BootFactor factor) {
        if (!this.mCalledOnBoot) {
            if (doInputSkipKeyborad()) {
                getWindow().setFlags(131072, 131072);
            }
            ClassDefinition.resolve(getApplicationContext());
            BeepUtility.getInstance().init();
            Looper.myQueue().addIdleHandler(this.beepInitializeHandler);
            OLEDWrapper.initialize(this);
            HDMIInfoWrapper.initialize();
            ApoWrapper.initialize(getApplicationContext());
            this.mCalledOnBoot = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onShutdown() {
        this.root.remove();
        this.root = null;
        StateUpdater.terminate();
        LayoutUpdater.terminate();
        int count = this.layouts.size();
        if (count != 0) {
            Log.w(TAG, log_layoutdoesnotclose);
            for (int i = 0; i < count; i++) {
                Log.w(TAG, log_speacer + this.layouts.get(i).getClass().getSimpleName());
            }
        }
        ApoWrapper.terminate();
        HDMIInfoWrapper.terminate();
        OLEDWrapper.terminate();
        CustomKeyMgr.getInstance().pause();
        Looper.myQueue().removeIdleHandler(this.beepInitializeHandler);
        BeepUtility.getInstance().term();
        this.mCalledOnBoot = false;
    }

    public final int getRootContainerId() {
        return Definition.APP_ROOT;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int ret = 0;
        Log.d(TAG, "onKeyDown " + event.getScanCode());
        int version = Environment.getVersionOfHW();
        if (2 <= version) {
            P1Key.onKeyDown(keyCode, event);
        }
        for (int i = this.layouts.size() - 1; i >= 0 && ret == 0; i--) {
            ret = this.layouts.get(i).dispatchKeyDown(keyCode, event);
            if (1 == ret) {
                playBeepMain(event);
            }
        }
        if (ret == 0 && this.root != null && 1 == (ret = this.root.dispatchKeyDown(keyCode, event))) {
            playBeepMain(event);
        }
        if (ret == 0) {
            ret = super.onKeyDown(keyCode, event) ? 1 : 0;
        }
        return ret != 0;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int ret = 0;
        Log.d(TAG, "onKeyUp " + event.getScanCode());
        for (int i = this.layouts.size() - 1; i >= 0 && ret == 0; i--) {
            ret = this.layouts.get(i).dispatchKeyUp(keyCode, event);
        }
        if (ret == 0 && this.root != null) {
            ret = this.root.dispatchKeyUp(keyCode, event);
        }
        if (ret == 0) {
            ret = super.onKeyUp(keyCode, event) ? 1 : 0;
        }
        resetCurrentState();
        return ret != 0;
    }

    private void playBeepMain(KeyEvent event) {
        String beepId = BeepUtility.getInstance().findBeep(event, Integer.valueOf(getCurrentState().getKeyBeepPattern()));
        String tmpBeepId = getCurrentState().changeKeyBeepId(event);
        if (tmpBeepId != null) {
            beepId = tmpBeepId;
        }
        if (beepId != null && !beepId.equals(BeepUtilityRsrcTable.BEEP_ID_NONE)) {
            BeepUtility.getInstance().playBeep(beepId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class EventHandler extends Handler {
        private static final String TAG = "EventHandler";
        private static final String log_layout = "LAYOUT_CHANGED";
        private static final String log_state = "STATE_CHANGED";

        EventHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            boolean ret = false;
            switch (msg.what) {
                case 1:
                    Log.d(TAG, log_layout);
                    LayoutUpdater.run();
                    boolean finish = false;
                    for (int i = AppRoot.this.layouts.size() - 1; !finish && i >= 0; i--) {
                        finish = AppRoot.this.layouts.get(i).setBackgroundDrawable();
                    }
                    return;
                case 2:
                    Log.d(TAG, log_state);
                    StateUpdater.run();
                    return;
                default:
                    if (0 == 0 && AppRoot.this.root != null) {
                        ret = AppRoot.this.root._handleMessage(msg);
                    }
                    if (!ret) {
                        Log.d(getClass().getSimpleName(), "This message isn't handled");
                        return;
                    }
                    return;
            }
        }
    }

    public Handler getHandler() {
        return this.handler;
    }

    public boolean setData(String name, Object o) {
        if (this.data.containsKey(name)) {
            return false;
        }
        this.data.put(name, o);
        return true;
    }

    public Object getData(String name) {
        return this.data.get(name);
    }

    public Object removeData(String name) {
        return this.data.remove(name);
    }

    /* loaded from: classes.dex */
    public static final class USER_KEYCODE {
        public static final int AEL = 532;
        public static final int AF_MF = 533;
        public static final int AF_MF_AEL = 638;
        public static final int AF_RANGE = 614;
        public static final int CENTER = 232;
        public static final int CUSTOM = 588;
        public static final int CUSTOM1 = 622;
        public static final int CUSTOM2 = 623;
        public static final int DELETE = 595;
        public static final int DIAL1_LEFT = 526;
        public static final int DIAL1_RIGHT = 525;
        public static final int DIAL1_STATUS = 524;
        public static final int DIAL2_LEFT = 529;
        public static final int DIAL2_RIGHT = 528;
        public static final int DIAL2_STATUS = 527;
        public static final int DIAL3_LEFT = 634;
        public static final int DIAL3_RIGHT = 635;
        public static final int DIAL3_STATUS = 633;
        public static final int DIGITAL_ZOOM = 609;
        public static final int DISP = 608;
        public static final int DOWN = 108;
        public static final int DRIVE_MODE = 605;
        public static final int EV_COMPENSATION = 602;
        public static final int EV_DIAL_CHANGED = 620;
        public static final int EXPAND_FOCUS = 607;
        public static final int EYE_SENSER = 519;
        public static final int FEL = 612;
        public static final int FN = 520;
        public static final int FOCUS = 636;
        public static final int FOCUS_HOLD = 603;
        public static final int FOCUS_MODE_DIAL_AF_A = 601;
        public static final int FOCUS_MODE_DIAL_AF_C = 599;
        public static final int FOCUS_MODE_DIAL_AF_S = 600;
        public static final int FOCUS_MODE_DIAL_CHANGED = 597;
        public static final int FOCUS_MODE_DIAL_DMF = 646;
        public static final int FOCUS_MODE_DIAL_MF = 598;
        public static final int IRIS_DIAL_CHANGED = 621;
        public static final int IR_MOVIE_REC = 643;
        public static final int IR_SHUTTER = 552;
        public static final int IR_SHUTTER_2SEC = 553;
        public static final int IR_TC_RESET = 656;
        public static final int IR_ZOOM_TELE = 579;
        public static final int IR_ZOOM_WIDE = 580;
        public static final int ISO = 681;
        private static final int KEY_CAPACITY = 128;
        public static final int LEFT = 105;
        public static final int LEFT_DOWN = 594;
        public static final int LEFT_UP = 593;
        public static final int LENS_APERTURE_RING_LEFT = 652;
        public static final int LENS_APERTURE_RING_RIGHT = 651;
        public static final int LENS_APERTURE_RING_STATUS = 650;
        public static final int LENS_ATTACH = 530;
        public static final int LENS_DETACH = 786;
        public static final int LENS_FOCUS_HOLD = 653;
        public static final int LENS_PARTS_OPERATION = 655;
        public static final int LENS_PARTS_STATE = 654;
        public static final int MENU = 514;
        public static final int MODE_A = 629;
        public static final int MODE_DIAL_3D = 543;
        public static final int MODE_DIAL_AEA = 539;
        public static final int MODE_DIAL_AES = 538;
        public static final int MODE_DIAL_AUTO = 535;
        public static final int MODE_DIAL_BOKS = 546;
        public static final int MODE_DIAL_CHANGED = 572;
        public static final int MODE_DIAL_CONT_PRIO_AE = 547;
        public static final int MODE_DIAL_CUSTOM = 541;
        public static final int MODE_DIAL_CUSTOM2 = 550;
        public static final int MODE_DIAL_CUSTOM3 = 551;
        public static final int MODE_DIAL_FLASH_OFF = 548;
        public static final int MODE_DIAL_HQAUTO = 536;
        public static final int MODE_DIAL_INVALID = 534;
        public static final int MODE_DIAL_MANUAL = 540;
        public static final int MODE_DIAL_MOVIE = 544;
        public static final int MODE_DIAL_PANORAMA = 542;
        public static final int MODE_DIAL_PE = 549;
        public static final int MODE_DIAL_PROGRAM = 537;
        public static final int MODE_DIAL_SCN = 545;
        public static final int MODE_M = 631;
        public static final int MODE_P = 628;
        public static final int MODE_S = 630;
        public static final int MOVIE_REC = 515;
        public static final int MOVIE_REC_2ND = 637;
        public static final int PEAKING = 625;
        public static final int PLAYBACK = 207;
        public static final int PREVIEW = 632;
        public static final int PROJECTOR = 626;
        public static final int RESET = 658;
        public static final int RIGHT = 106;
        public static final int RIGHT_DOWN = 592;
        public static final int RIGHT_UP = 591;
        public static final int RING_CLOCKWISE = 648;
        public static final int RING_COUNTERCW = 649;
        public static final int RING_STATUS = 647;
        public static final int S1_OFF = 772;
        public static final int S1_ON = 516;
        public static final int S2_OFF = 774;
        public static final int S2_ON = 518;
        public static final int SHOOTING_MODE = 624;
        public static final int SHUTTLE_LEFT = 523;
        public static final int SHUTTLE_RIGHT = 522;
        public static final int SK1 = 229;
        public static final int SK2 = 513;
        public static final int SLIDE_AEL_FOCUS_CHANGED = 589;
        public static final int SLIDE_AF_MF = 596;
        public static final int SMART_TELECON = 606;
        public static final int UM_MOVIE_REC = 616;
        public static final int UM_S1 = 613;
        public static final int UM_S2 = 615;
        public static final int UM_ZOOM_OFF = 617;
        public static final int UM_ZOOM_STATUS = 639;
        public static final int UM_ZOOM_TELE = 618;
        public static final int UM_ZOOM_WIDE = 619;
        public static final int UP = 103;
        public static final int WATER_HOUSING = 640;
        public static final int WB = 604;
        public static final int WIFI = 657;
        public static final int ZEBRA = 627;
        public static final int ZOOM_LEVER_TELE = 610;
        public static final int ZOOM_LEVER_WIDE = 611;
        private static ArrayList<Integer> sKeys;
        private static SparseIntArray sStatusKeyCodeTable = null;

        public static Iterator<Integer> getIterator() {
            initKeys();
            return sKeys.iterator();
        }

        private static void initKeys() {
            if (sKeys == null) {
                sKeys = new ArrayList<>(128);
                sKeys.add(103);
                sKeys.add(Integer.valueOf(DOWN));
                sKeys.add(Integer.valueOf(RIGHT));
                sKeys.add(Integer.valueOf(LEFT));
                sKeys.add(Integer.valueOf(CENTER));
                sKeys.add(Integer.valueOf(LENS_ATTACH));
                sKeys.add(Integer.valueOf(LENS_DETACH));
                sKeys.add(Integer.valueOf(FN));
                sKeys.add(Integer.valueOf(S1_ON));
                sKeys.add(Integer.valueOf(S2_ON));
                sKeys.add(Integer.valueOf(MOVIE_REC));
                sKeys.add(Integer.valueOf(SK1));
                sKeys.add(Integer.valueOf(SK2));
                sKeys.add(Integer.valueOf(SHUTTLE_RIGHT));
                sKeys.add(Integer.valueOf(SHUTTLE_LEFT));
                sKeys.add(Integer.valueOf(AEL));
                sKeys.add(Integer.valueOf(AF_MF));
                sKeys.add(Integer.valueOf(PLAYBACK));
                sKeys.add(Integer.valueOf(DIAL1_RIGHT));
                sKeys.add(Integer.valueOf(DIAL1_LEFT));
                sKeys.add(Integer.valueOf(DIAL2_RIGHT));
                sKeys.add(Integer.valueOf(DIAL2_LEFT));
                sKeys.add(Integer.valueOf(IR_SHUTTER));
                sKeys.add(Integer.valueOf(IR_SHUTTER_2SEC));
                sKeys.add(Integer.valueOf(IR_MOVIE_REC));
                sKeys.add(Integer.valueOf(IR_ZOOM_TELE));
                sKeys.add(Integer.valueOf(IR_ZOOM_WIDE));
                sKeys.add(Integer.valueOf(IR_TC_RESET));
                sKeys.add(Integer.valueOf(RIGHT_UP));
                sKeys.add(Integer.valueOf(RIGHT_DOWN));
                sKeys.add(Integer.valueOf(LEFT_UP));
                sKeys.add(Integer.valueOf(LEFT_DOWN));
                sKeys.add(Integer.valueOf(MENU));
                sKeys.add(Integer.valueOf(DELETE));
                sKeys.add(Integer.valueOf(SLIDE_AF_MF));
                sKeys.add(Integer.valueOf(FOCUS_MODE_DIAL_CHANGED));
                sKeys.add(Integer.valueOf(EV_COMPENSATION));
                sKeys.add(Integer.valueOf(ISO));
                sKeys.add(Integer.valueOf(WB));
                sKeys.add(Integer.valueOf(DRIVE_MODE));
                sKeys.add(Integer.valueOf(SMART_TELECON));
                sKeys.add(Integer.valueOf(EXPAND_FOCUS));
                sKeys.add(Integer.valueOf(DISP));
                sKeys.add(Integer.valueOf(PREVIEW));
                sKeys.add(Integer.valueOf(DIGITAL_ZOOM));
                sKeys.add(Integer.valueOf(ZOOM_LEVER_TELE));
                sKeys.add(Integer.valueOf(ZOOM_LEVER_WIDE));
                sKeys.add(Integer.valueOf(FEL));
                sKeys.add(Integer.valueOf(AF_RANGE));
                sKeys.add(Integer.valueOf(EV_DIAL_CHANGED));
                sKeys.add(Integer.valueOf(IRIS_DIAL_CHANGED));
                sKeys.add(Integer.valueOf(CUSTOM));
                sKeys.add(Integer.valueOf(CUSTOM1));
                sKeys.add(Integer.valueOf(CUSTOM2));
                sKeys.add(624);
                sKeys.add(Integer.valueOf(PEAKING));
                sKeys.add(Integer.valueOf(PROJECTOR));
                sKeys.add(Integer.valueOf(ZEBRA));
                sKeys.add(Integer.valueOf(WIFI));
                sKeys.add(Integer.valueOf(RESET));
                sKeys.add(Integer.valueOf(MODE_P));
                sKeys.add(Integer.valueOf(MODE_A));
                sKeys.add(Integer.valueOf(MODE_S));
                sKeys.add(Integer.valueOf(MODE_M));
                sKeys.add(Integer.valueOf(FOCUS));
                sKeys.add(Integer.valueOf(MOVIE_REC_2ND));
                sKeys.add(Integer.valueOf(FOCUS_HOLD));
                sKeys.add(Integer.valueOf(DIAL3_LEFT));
                sKeys.add(Integer.valueOf(DIAL3_RIGHT));
                sKeys.add(Integer.valueOf(RING_CLOCKWISE));
                sKeys.add(Integer.valueOf(RING_COUNTERCW));
                sKeys.add(Integer.valueOf(LENS_APERTURE_RING_LEFT));
                sKeys.add(Integer.valueOf(LENS_APERTURE_RING_RIGHT));
                sKeys.add(Integer.valueOf(LENS_FOCUS_HOLD));
                sKeys.add(Integer.valueOf(LENS_PARTS_OPERATION));
                sKeys.add(Integer.valueOf(LENS_PARTS_STATE));
                sKeys.add(Integer.valueOf(UM_S1));
                sKeys.add(Integer.valueOf(UM_S2));
                sKeys.add(Integer.valueOf(UM_MOVIE_REC));
                sKeys.add(Integer.valueOf(UM_ZOOM_OFF));
                sKeys.add(Integer.valueOf(UM_ZOOM_TELE));
                sKeys.add(Integer.valueOf(UM_ZOOM_WIDE));
                sKeys.add(Integer.valueOf(WATER_HOUSING));
                sKeys.add(Integer.valueOf(MODE_DIAL_CHANGED));
            }
        }

        private static void initStatusTable() {
            if (sStatusKeyCodeTable == null) {
                sStatusKeyCodeTable = new SparseIntArray();
                sStatusKeyCodeTable.put(DIAL1_LEFT, DIAL1_STATUS);
                sStatusKeyCodeTable.put(DIAL1_RIGHT, DIAL1_STATUS);
                sStatusKeyCodeTable.put(DIAL2_LEFT, DIAL2_STATUS);
                sStatusKeyCodeTable.put(DIAL2_RIGHT, DIAL2_STATUS);
                sStatusKeyCodeTable.put(DIAL3_LEFT, DIAL3_STATUS);
                sStatusKeyCodeTable.put(DIAL3_RIGHT, DIAL3_STATUS);
                sStatusKeyCodeTable.put(SHUTTLE_LEFT, 521);
                sStatusKeyCodeTable.put(SHUTTLE_RIGHT, 521);
                sStatusKeyCodeTable.put(RING_CLOCKWISE, RING_STATUS);
                sStatusKeyCodeTable.put(RING_COUNTERCW, RING_STATUS);
                sStatusKeyCodeTable.put(LENS_APERTURE_RING_LEFT, LENS_APERTURE_RING_STATUS);
                sStatusKeyCodeTable.put(LENS_APERTURE_RING_RIGHT, LENS_APERTURE_RING_STATUS);
                sStatusKeyCodeTable.put(UM_ZOOM_OFF, UM_ZOOM_STATUS);
                sStatusKeyCodeTable.put(UM_ZOOM_TELE, UM_ZOOM_STATUS);
                sStatusKeyCodeTable.put(UM_ZOOM_WIDE, UM_ZOOM_STATUS);
            }
        }

        public static KeyStatus getKeyStatus(int code) {
            initStatusTable();
            return ScalarInput.getKeyStatus(sStatusKeyCodeTable.get(code, code));
        }
    }

    /* loaded from: classes.dex */
    private class KeyDispatchForSyskaku implements IKeyDispatch {
        private KeyDispatchForSyskaku() {
        }

        @Override // com.sony.imaging.app.fw.AppRoot.IKeyDispatch
        public boolean dispatchKeyEvent(KeyEvent event) {
            boolean ret = AppRoot.this.superDispatchKeyEvent(event);
            return ret;
        }
    }

    /* loaded from: classes.dex */
    private class KeyDispatchForQEMU implements IKeyDispatch {
        private KeyDispatchForQEMU() {
        }

        @Override // com.sony.imaging.app.fw.AppRoot.IKeyDispatch
        public boolean dispatchKeyEvent(KeyEvent event) {
            int keyCode = 0;
            int scanCode = 0;
            switch (event.getKeyCode()) {
                case 7:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.MODE_DIAL_CHANGED;
                    break;
                case 8:
                    keyCode = 82;
                    scanCode = USER_KEYCODE.SK1;
                    break;
                case 9:
                    keyCode = 19;
                    scanCode = 103;
                    break;
                case 11:
                    keyCode = 21;
                    scanCode = USER_KEYCODE.LEFT;
                    break;
                case 12:
                case 66:
                    keyCode = 23;
                    scanCode = USER_KEYCODE.CENTER;
                    break;
                case 13:
                    keyCode = 22;
                    scanCode = USER_KEYCODE.RIGHT;
                    break;
                case 14:
                case HandoffOperationInfo.GET_CONTENT_COUNT /* 44 */:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.SK2;
                    break;
                case 15:
                    keyCode = 20;
                    scanCode = USER_KEYCODE.DOWN;
                    break;
                case 29:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.SHUTTLE_LEFT;
                    break;
                case 30:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.AEL;
                    break;
                case 31:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.S2_ON;
                    break;
                case 33:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.FN;
                    break;
                case 36:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.PLAYBACK;
                    break;
                case 37:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.MOVIE_REC;
                    break;
                case 39:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.EYE_SENSER;
                    break;
                case 40:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.IR_SHUTTER_2SEC;
                    break;
                case 42:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.IR_SHUTTER;
                    break;
                case HandoffOperationInfo.GET_CONTENT_LIST /* 45 */:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.LENS_ATTACH;
                    break;
                case HandoffOperationInfo.DELETE_CONTENT /* 46 */:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.DIAL1_LEFT;
                    break;
                case 47:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.SHUTTLE_RIGHT;
                    break;
                case 48:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.DIAL1_RIGHT;
                    break;
                case 49:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.DIAL2_RIGHT;
                    break;
                case 50:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.S2_OFF;
                    break;
                case HandoffOperationInfo.START_STREAMING /* 51 */:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.LENS_DETACH;
                    break;
                case HandoffOperationInfo.PAUSE_STREAMING /* 52 */:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.S1_OFF;
                    break;
                case HandoffOperationInfo.STOP_STREAMING /* 53 */:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.DIAL2_LEFT;
                    break;
                case HandoffOperationInfo.SEEK_STREAMING /* 54 */:
                    keyCode = 0;
                    scanCode = USER_KEYCODE.S1_ON;
                    break;
            }
            KeyEvent newEvent = event;
            if (keyCode != 0 || scanCode != 0) {
                newEvent = new KeyEvent(event.getDownTime(), event.getEventTime(), event.getAction(), keyCode, event.getRepeatCount(), event.getMetaState(), event.getDeviceId(), scanCode, event.getFlags());
            }
            if (newEvent != null) {
                AppRoot.this.superDispatchKeyEvent(newEvent);
            }
            return false;
        }
    }

    protected Collection<Integer> getIgnoreThinKeys() {
        return null;
    }

    private void resetThinParams() {
        this.mScanCode = -1;
        this.mAction = -1;
        this.mLastAction = -1;
        this.mFinishTime = -1L;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        KeyStatus status;
        boolean ret = false;
        int code = event.getScanCode();
        if (1 == Environment.getVersionOfHW() && (526 == code || 525 == code)) {
            code = 526 == code ? USER_KEYCODE.DIAL1_RIGHT : USER_KEYCODE.DIAL1_LEFT;
            try {
                Field scanCode = KeyEvent.class.getDeclaredField("mScanCode");
                scanCode.setAccessible(true);
                scanCode.set(event, Integer.valueOf(code));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            } catch (SecurityException e3) {
                e3.printStackTrace();
            }
        }
        int action = event.getAction();
        boolean needToHandle = false;
        if (HoldKeyServer.handle(event)) {
            this.mScanCode = -1;
            this.mPosition = POSITION_UNKNOWN;
            this.mAction = -1;
            this.mLastAction = -1;
            this.mFinishTime = -1L;
            ret = true;
        } else if (event.getScanCode() == 0 && 0 == event.getEventTime()) {
            this.mScanCode = -1;
            this.mPosition = POSITION_UNKNOWN;
            this.mAction = -1;
            this.mLastAction = -1;
            this.mFinishTime = -1L;
            needToHandle = true;
        } else if (code != this.mScanCode || (this.mIgnoreThinKeys != null && this.mIgnoreThinKeys.contains(Integer.valueOf(code)))) {
            this.mScanCode = code;
            this.mPosition = POSITION_UNKNOWN;
            this.mAction = action + 1;
            this.mLastAction = -1;
            needToHandle = true;
        } else {
            if (action == 0 && sSlideKeys.contains(Integer.valueOf(code)) && (status = ScalarInput.getKeyStatus(code)) != null && 1 == status.valid && this.mPosition != status.status) {
                this.mScanCode = code;
                this.mPosition = status.status;
                this.mAction = action + 1;
                this.mLastAction = -1;
                needToHandle = true;
            }
            if (!needToHandle) {
                if (((action + 1) & this.mAction) == 0) {
                    this.mAction |= action + 1;
                    this.mLastAction = action + 1;
                    needToHandle = true;
                } else {
                    long eventTime = event.getEventTime();
                    if (eventTime > this.mFinishTime - this.mMerginTime && this.mLastAction != action + 1) {
                        this.mAction = action + 1;
                        needToHandle = true;
                    } else if (event.getRepeatCount() > 0) {
                        KeyStatus key = ScalarInput.getKeyStatus(code);
                        if (key.valid == 1 && key.status == 1) {
                            this.mAction |= action + 1;
                            needToHandle = true;
                        }
                    } else {
                        ret = true;
                        StringBuilder log_string = sStringBuilder.get();
                        Log.i(TAG, log_string.replace(0, log_string.length(), LOG_THIN).append(code).append(LOG_ACTION).append(action).append(LOG_EVENT_TIME).append(event.getEventTime()).append(LOG_FINISH_TIME).append(this.mFinishTime).toString());
                        PTag.setTimeTag(log_string.toString(), 1);
                    }
                }
            }
        }
        if (needToHandle) {
            StringBuilder log_string2 = sStringBuilder.get();
            PTag.setTimeTag(log_string2.replace(0, log_string2.length(), LOG_KEY).append(code).append(LOG_ACTION).append(action).toString(), 1);
            boolean ret2 = this.mKeyDispatcher.dispatchKeyEvent(event);
            this.mFinishTime = SystemClock.uptimeMillis();
            return ret2;
        }
        return ret;
    }

    boolean superDispatchKeyEvent(KeyEvent event) {
        onUserInteraction();
        Window win = getWindow();
        View decor = win.getDecorView();
        boolean ret = event.dispatch(this, decor != null ? decor.getKeyDispatcherState() : null, this);
        return ret;
    }
}
