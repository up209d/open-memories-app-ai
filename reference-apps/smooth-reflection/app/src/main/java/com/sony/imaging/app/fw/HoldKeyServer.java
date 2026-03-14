package com.sony.imaging.app.fw;

import android.app.Activity;
import android.os.Handler;
import android.util.SparseArray;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class HoldKeyServer {
    public static final int CLEAR_HOLD = 3;
    public static final int CLEAR_OBSERVE = 5;
    public static final int HOLDMODE_OFF = 0;
    public static final int HOLDMODE_ROLLUP = 2;
    public static final int HOLDMODE_THINOUT = 1;
    public static final int OBSERVE = 4;
    private static Activity mAct;
    private static Handler handler = null;
    private static SparseArray<Boolean> mObserveKeys = new SparseArray<>();
    private static SparseArray<Object> holdingKeys = new SparseArray<>();
    private static KeySender mKeySender = new KeySender();

    public static void holdKey(int scanCode, int holdMode) {
        switch (holdMode) {
            case 0:
                holdoff(scanCode);
                return;
            case 1:
                Object o = holdingKeys.get(scanCode);
                if (o != null) {
                    if (isHoldRollup(o)) {
                        holdoff(scanCode);
                        holdThinout(scanCode);
                        return;
                    }
                    return;
                }
                holdThinout(scanCode);
                return;
            case 2:
                Object o2 = holdingKeys.get(scanCode);
                if (o2 != null) {
                    if (!isHoldRollup(o2)) {
                        holdoff(scanCode);
                        holdRollup(scanCode);
                        return;
                    }
                    return;
                }
                holdRollup(scanCode);
                return;
            case 3:
                clearhold(scanCode);
                return;
            case 4:
                Object o3 = mObserveKeys.get(scanCode);
                if (o3 == null) {
                    mObserveKeys.put(scanCode, false);
                    return;
                }
                return;
            case 5:
                Object o4 = mObserveKeys.get(scanCode);
                if (o4 != null) {
                    mObserveKeys.delete(scanCode);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public static boolean getObserveKeyInfo(int scanCode) {
        Boolean info = mObserveKeys.get(scanCode);
        if (info == null) {
            return false;
        }
        boolean ret = info.booleanValue();
        return ret;
    }

    private static boolean isHoldRollup(Object o) {
        return o instanceof ArrayList;
    }

    private static void holdThinout(int scanCode) {
        KeyEvent[] keys = {null, null};
        holdingKeys.put(scanCode, keys);
    }

    private static void holdRollup(int scanCode) {
        holdingKeys.put(scanCode, new ArrayList());
    }

    private static void clearhold(int scanCode) {
        holdingKeys.delete(scanCode);
    }

    private static void holdoff(int scanCode) {
        Object o = holdingKeys.get(scanCode);
        holdingKeys.delete(scanCode);
        mKeySender.kick(o);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void init(Activity act) {
        mAct = act;
        handler = new Handler();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void term() {
        mAct = null;
        holdingKeys.clear();
        mObserveKeys.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean handle(KeyEvent event) {
        boolean ret = false;
        Object o = holdingKeys.get(event.getScanCode());
        if (o != null) {
            ret = true;
            if (o instanceof ArrayList) {
                ArrayList<KeyEvent> keys = (ArrayList) o;
                keys.add(event);
            } else {
                KeyEvent[] keys2 = (KeyEvent[]) o;
                keyHoldHelperAdd(keys2, event);
            }
        }
        int scanCode = event.getScanCode();
        Boolean info = mObserveKeys.get(scanCode);
        if (info != null && event.getAction() == 0) {
            mObserveKeys.put(scanCode, true);
        }
        return ret;
    }

    private static void keyHoldHelperAdd(KeyEvent[] keys, KeyEvent event) {
        if (keys[0] == null) {
            keys[0] = event;
            return;
        }
        if (keys[1] == null) {
            if (keys[0].getAction() == 0 && event.getAction() == 0) {
                keys[0] = event;
                return;
            } else {
                keys[1] = event;
                return;
            }
        }
        if (event.getAction() == 0) {
            if (keys[0].getAction() == 0 && 1 == keys[1].getAction()) {
                keys[0] = event;
                keys[1] = null;
            }
            if (1 == keys[0].getAction() && keys[1].getAction() == 0) {
                keys[1] = event;
                return;
            }
            return;
        }
        if (1 == keys[0].getAction() && keys[1].getAction() == 0) {
            keys[0] = event;
            keys[1] = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class KeySender implements Runnable {
        ArrayList<Object> keys;

        private KeySender() {
            this.keys = new ArrayList<>();
        }

        public void kick(Object keys) {
            if (keys != null) {
                this.keys.add(keys);
                HoldKeyServer.handler.postAtTime(this, 1L);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            Object keys = this.keys.remove(0);
            if (keys instanceof ArrayList) {
                Iterator i$ = ((ArrayList) keys).iterator();
                while (i$.hasNext()) {
                    HoldKeyServer.mAct.dispatchKeyEvent(i$.next());
                }
                return;
            }
            KeyEvent[] sends = (KeyEvent[]) keys;
            for (KeyEvent event : sends) {
                if (event != null) {
                    HoldKeyServer.mAct.dispatchKeyEvent(event);
                }
            }
        }
    }
}
