package com.sony.scalar.webapi.lib.authlib;

import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/* loaded from: classes.dex */
public class AuthLibManager {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$sony$scalar$webapi$lib$authlib$EnableMethodsState;
    private static AuthLibManager sInstance;
    private String mDg;
    private EnableMethodsHookHandler mHookHandler;
    private boolean mIsReadyFlag;
    private String mSalt;
    private EnableMethodsState mState;
    private ArrayList<EnableMethodsStateListener> mStateListenerList;
    private String mVal;
    private ArrayList<String> mEnableMethods = new ArrayList<>();
    private ArrayList<String> mPrivateMethods = new ArrayList<>();

    static /* synthetic */ int[] $SWITCH_TABLE$com$sony$scalar$webapi$lib$authlib$EnableMethodsState() {
        int[] iArr = $SWITCH_TABLE$com$sony$scalar$webapi$lib$authlib$EnableMethodsState;
        if (iArr == null) {
            iArr = new int[EnableMethodsState.valuesCustom().length];
            try {
                iArr[EnableMethodsState.AUTHENTICATED.ordinal()] = 4;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[EnableMethodsState.INITIAL.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[EnableMethodsState.RANDOMHASH_REQUEST.ordinal()] = 2;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[EnableMethodsState.VERIFY_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e4) {
            }
            $SWITCH_TABLE$com$sony$scalar$webapi$lib$authlib$EnableMethodsState = iArr;
        }
        return iArr;
    }

    private AuthLibManager() {
    }

    public static synchronized AuthLibManager getInstance() {
        AuthLibManager authLibManager;
        synchronized (AuthLibManager.class) {
            if (sInstance == null) {
                sInstance = new AuthLibManager();
            }
            authLibManager = sInstance;
        }
        return authLibManager;
    }

    public void initialize() {
        initialize(Long.toString(System.nanoTime()));
    }

    public synchronized void uninitialize() {
        this.mEnableMethods.clear();
        this.mPrivateMethods.clear();
        this.mDg = "";
        this.mState = EnableMethodsState.INITIAL;
        this.mStateListenerList.clear();
        this.mIsReadyFlag = false;
    }

    public synchronized void initialize(String salt) {
        this.mEnableMethods.clear();
        this.mPrivateMethods.clear();
        String val = "";
        for (int i = 0; i < AuthConfig.ARRAY.length; i++) {
            int l = (-86991901) ^ AuthConfig.ARRAY[i];
            val = String.valueOf(val) + Integer.toHexString(l);
        }
        this.mVal = val;
        this.mSalt = salt;
        if (this.mSalt == null) {
            this.mSalt = Long.toString(System.nanoTime());
        }
        this.mDg = "";
        this.mState = EnableMethodsState.INITIAL;
        this.mStateListenerList = new ArrayList<>();
        this.mIsReadyFlag = true;
    }

    public void setData(int[] array) {
        int[] test = new int[5];
        String val = "";
        for (int i = 0; i < array.length; i++) {
            int l = (-86991901) ^ array[i];
            val = String.valueOf(val) + Integer.toHexString(l);
            test[i] = l;
        }
        this.mVal = val;
    }

    public void setReadyFlag(boolean flag) {
        this.mIsReadyFlag = flag;
    }

    public boolean isReady() {
        return this.mIsReadyFlag;
    }

    public void setEnableMethodsHookHandler(EnableMethodsHookHandler handler) {
        this.mHookHandler = handler;
    }

    public void clearEnableMethodsHookHandler() {
        this.mHookHandler = null;
    }

    public boolean hookHandler(String devName, String devId, String methods, String sg, String dg) {
        if (this.mHookHandler == null) {
            return false;
        }
        return this.mHookHandler.hookHandler(devName, devId, methods, sg, dg);
    }

    public synchronized void setEnableMethodsState(EnableMethodsState state, String devName, String devId, String methods, String sg) {
        if (this.mStateListenerList != null) {
            Iterator<EnableMethodsStateListener> it = this.mStateListenerList.iterator();
            while (it.hasNext()) {
                EnableMethodsStateListener listener = it.next();
                listener.onStateChange(state, devName, devId, methods, sg);
            }
        }
        switch ($SWITCH_TABLE$com$sony$scalar$webapi$lib$authlib$EnableMethodsState()[state.ordinal()]) {
            case 3:
                this.mDg = "";
                this.mState = state.accept();
                setEnableMethodsState(this.mState, devName, devId, methods, sg);
                break;
            case 4:
                this.mDg = "";
                this.mState = state;
                break;
            default:
                this.mState = state;
                break;
        }
    }

    public synchronized void notifyError(int responseCode, String devName, String devId, String methods, String sg) {
        if (this.mStateListenerList != null) {
            Iterator<EnableMethodsStateListener> it = this.mStateListenerList.iterator();
            while (it.hasNext()) {
                EnableMethodsStateListener listener = it.next();
                listener.onError(responseCode, devName, devId, methods, sg);
            }
        }
    }

    public EnableMethodsState getEnableMethodsState() {
        return this.mState;
    }

    public synchronized void addEnableMethodsStateListener(EnableMethodsStateListener listener) {
        this.mStateListenerList.add(listener);
    }

    public synchronized void removeEnableMethodsEventListener(EnableMethodsStateListener listener) {
        this.mStateListenerList.remove(listener);
    }

    public synchronized void removeAllEnableMethodsEventListener() {
        this.mStateListenerList.clear();
    }

    public synchronized void setPrivateMethods(ArrayList<String> methods) {
        this.mPrivateMethods = new ArrayList<>(methods);
    }

    public ArrayList<String> getPrivateMethods() {
        return new ArrayList<>(this.mPrivateMethods);
    }

    public synchronized void clearPrivateMethods() {
        this.mPrivateMethods.clear();
    }

    public boolean isPrivateMethod(String method) {
        return this.mPrivateMethods.contains(method);
    }

    public synchronized void setEnableMethods(ArrayList<String> methods) {
        this.mEnableMethods = methods;
    }

    public ArrayList<String> getEnableMethods() {
        return this.mEnableMethods;
    }

    public void clearEnableMethods() {
        this.mEnableMethods.clear();
    }

    public boolean isAuthenticated(String method) {
        return this.mEnableMethods.contains(method);
    }

    public String generateHashCode() {
        return AuthUtil.generateRandomStr(this.mSalt);
    }

    public void setDg(String dg) {
        this.mDg = dg;
    }

    public boolean verifyAuthentication(String devName, String devId, String methods, String devHash2) {
        if (this.mDg.equals("")) {
            return false;
        }
        String hashStr = String.valueOf(devName) + devId + methods;
        String devHash1 = AuthUtil.generateHashCode(hashStr, this.mVal);
        byte[] hash = AuthUtil.generateDevHash2(String.valueOf(devHash1) + this.mDg);
        try {
            byte[] decoded_sg = Base64.decode(devHash2.getBytes("UTF-8"), 2);
            return Arrays.equals(hash, decoded_sg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
