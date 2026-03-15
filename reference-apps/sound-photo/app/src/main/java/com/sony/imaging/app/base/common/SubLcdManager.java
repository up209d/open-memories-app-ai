package com.sony.imaging.app.base.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.widget.SubLcdMute;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.indicator.Parameter;
import com.sony.scalar.hardware.indicator.SubLCD;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class SubLcdManager {
    private static final String MSG_DISAPPEAR_UNUSED = "disappear unused elem : ";
    private static final String MSG_DRAW = "drawSubLcd";
    private static final String MSG_MUTED = "mute found : ";
    private static final int PF_VER_SUBLCD_PERFORMANCE_IMPROVEMENT = 12;
    protected static final int REQ_DRAW = 1;
    private static final String TAG = "SubLcdManager";
    protected static SubLcdManager sInstance;
    protected Context mContext;
    protected List<String> mLastDrawn;
    protected ViewGroup mRoot;
    private static final StringBuilderThreadLocal sBuilder = new StringBuilderThreadLocal();
    public static boolean DEBUG = false;
    protected List<BlinkHandle> blinkHandles = new ArrayList();
    protected HOLD_MODE mHoldMode = HOLD_MODE.OFF;
    protected boolean mIsDrawRequested = false;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.sony.imaging.app.base.common.SubLcdManager.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    removeMessages(1);
                    SubLcdManager.this.dispatchDraw(true);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /* loaded from: classes.dex */
    public enum HOLD_MODE {
        ON,
        OFF,
        OFF_REDRAW_IF_REQUESTED
    }

    /* loaded from: classes.dex */
    public class BlinkHandle {
        String mPatten;

        public BlinkHandle(String pattern) {
            this.mPatten = pattern;
        }
    }

    /* loaded from: classes.dex */
    public static class Element {
        boolean isText;
        String lid;
        String lkid;
        String pattern;
        String text;

        public static Element makeText(String lid, String text, String pattern) {
            Element elem = new Element();
            elem.lid = lid;
            elem.text = text;
            elem.pattern = pattern;
            elem.isText = true;
            return elem;
        }

        public static Element makeText(String lid, String text) {
            return makeText(lid, text, "PTN_ON");
        }

        public static Element makeIcon(String lid, String lkid, String pattern) {
            Element elem = new Element();
            elem.lid = lid;
            elem.lkid = lkid;
            elem.pattern = pattern;
            elem.isText = false;
            return elem;
        }

        public static Element makeIcon(String lid, String text) {
            return makeIcon(lid, text, null);
        }
    }

    public static SubLcdManager getInstance() {
        if (sInstance == null) {
            new SubLcdManager();
        }
        return sInstance;
    }

    private void setInstance(SubLcdManager instance) {
        if (sInstance == null) {
            sInstance = instance;
        }
    }

    protected SubLcdManager() {
        setInstance(this);
    }

    public void init(Context context, ViewGroup root) {
        if (Environment.getVersionPfAPI() >= 12) {
            SubLCD.initialize();
        }
        this.mContext = context;
        this.mRoot = root;
        this.mHoldMode = HOLD_MODE.OFF;
        this.mIsDrawRequested = false;
        this.blinkHandles.clear();
    }

    public void terminate() {
        this.mHandler.removeCallbacksAndMessages(null);
        this.mContext = null;
        this.mRoot = null;
        this.mLastDrawn = null;
        if (Environment.getVersionPfAPI() >= 12) {
            SubLCD.release();
        }
    }

    protected void dispatchDraw(boolean disapperRequested) {
        PTag.start(MSG_DRAW);
        LinkedHashMap<String, List<ISubLcdDrawer>> map = new LinkedHashMap<>();
        createMap(this.mRoot, map);
        Collection<List<ISubLcdDrawer>> entries = map.values();
        ArrayList<String> drawnElems = new ArrayList<>();
        List<Parameter> sublcdParams = new ArrayList<>();
        for (List<ISubLcdDrawer> list : entries) {
            Iterator i$ = list.iterator();
            while (true) {
                if (i$.hasNext()) {
                    ISubLcdDrawer drawer = i$.next();
                    if (DEBUG) {
                        PTag.start("draw " + drawer);
                    }
                    Element elem = drawer.getSubLcdElement(this.blinkHandles.size() > 0 ? this.blinkHandles.get(0).mPatten : null);
                    if (elem != null && !drawnElems.contains(elem.lid)) {
                        if (elem.isText) {
                            sublcdParams.add(new SubLCD.TextParameter(elem.lid, elem.text, true, elem.pattern));
                        } else if (elem.pattern != null) {
                            sublcdParams.add(new SubLCD.IconParameter(elem.lid, elem.lkid, true, elem.pattern));
                        } else {
                            sublcdParams.add(new SubLCD.IconParameter(elem.lid, elem.lkid, true));
                        }
                        if (this.mLastDrawn != null) {
                            this.mLastDrawn.remove(elem.lid);
                        }
                        drawnElems.add(elem.lid);
                    }
                }
            }
        }
        if (this.mLastDrawn != null) {
            if (disapperRequested) {
                StringBuilder builder = sBuilder.get();
                for (String lid : this.mLastDrawn) {
                    builder.replace(0, builder.length(), MSG_DISAPPEAR_UNUSED).append(lid);
                    Log.d(TAG, builder.toString());
                    sublcdParams.add(new Parameter(lid, false));
                }
            } else {
                drawnElems.addAll(this.mLastDrawn);
            }
        }
        if (this.mLastDrawn == null) {
            SubLCD.setAllSegmentState(false);
        }
        if (!sublcdParams.isEmpty()) {
            SubLCD.setState(sublcdParams);
        }
        this.mLastDrawn = drawnElems;
        PTag.end(MSG_DRAW);
    }

    protected boolean createMap(ViewGroup parent, Map<String, List<ISubLcdDrawer>> map) {
        boolean isMuteExist = false;
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            Object childAt = parent.getChildAt(i);
            if (SubLcdMute.class.isInstance(childAt)) {
                StringBuilder builder = sBuilder.get();
                builder.replace(0, builder.length(), MSG_MUTED).append(childAt);
                Log.i(TAG, builder.toString());
                return true;
            }
            if (childAt instanceof ISubLcdDrawer) {
                if (DEBUG) {
                    Log.i(TAG, "create map : " + childAt);
                }
                ISubLcdDrawer elem = (ISubLcdDrawer) childAt;
                List<ISubLcdDrawer> l = map.get(elem.getLId());
                if (l == null) {
                    l = new ArrayList<>();
                    map.put(elem.getLId(), l);
                }
                l.add(elem);
            }
            if ((childAt instanceof ViewGroup) && (isMuteExist = createMap((ViewGroup) childAt, map))) {
                break;
            }
        }
        return isMuteExist;
    }

    public BlinkHandle blinkAll(String pattern) {
        BlinkHandle handle = new BlinkHandle(pattern);
        this.blinkHandles.add(handle);
        requestDraw();
        return handle;
    }

    public void stopBlink(BlinkHandle handle) {
        this.blinkHandles.remove(handle);
        requestDraw();
    }

    public void requestDraw() {
        if (this.mRoot != null) {
            if (HOLD_MODE.ON.equals(this.mHoldMode)) {
                this.mIsDrawRequested = true;
            } else {
                this.mHandler.sendMessageAtTime(Message.obtain(this.mHandler, 1), 3L);
            }
        }
    }

    public void requestDrawAtFrontOfQueue() {
        if (this.mRoot != null) {
            if (HOLD_MODE.ON.equals(this.mHoldMode)) {
                this.mIsDrawRequested = true;
            } else {
                this.mHandler.sendMessageAtFrontOfQueue(Message.obtain(this.mHandler, 1));
            }
        }
    }

    public void holdDrawing(HOLD_MODE mode) {
        if (!this.mHoldMode.equals(mode)) {
            this.mHoldMode = mode;
            switch (mode) {
                case ON:
                    if (this.mHandler.hasMessages(1)) {
                        this.mIsDrawRequested = true;
                    }
                    this.mHandler.removeMessages(1);
                    return;
                case OFF_REDRAW_IF_REQUESTED:
                    if (this.mIsDrawRequested) {
                        requestDraw();
                    }
                    this.mIsDrawRequested = false;
                    return;
                case OFF:
                    this.mIsDrawRequested = false;
                    return;
                default:
                    return;
            }
        }
    }
}
