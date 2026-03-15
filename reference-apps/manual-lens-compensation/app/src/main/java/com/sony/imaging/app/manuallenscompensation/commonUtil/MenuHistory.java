package com.sony.imaging.app.manuallenscompensation.commonUtil;

import java.util.Stack;

/* loaded from: classes.dex */
public class MenuHistory {
    private static final String TAG = AppLog.getClassName();
    private static MenuHistory sMenuHistory = null;
    private Stack<String> mMoveHistory;

    public static MenuHistory getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sMenuHistory == null) {
            sMenuHistory = new MenuHistory();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sMenuHistory;
    }

    private MenuHistory() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mMoveHistory = new Stack<>();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void clearMenuHistory() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mMoveHistory != null) {
            this.mMoveHistory.clear();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void pushMenuItem(String menuHistoryItem) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mMoveHistory.add(menuHistoryItem);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String popMenuItem() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        if (this.mMoveHistory.isEmpty()) {
            return null;
        }
        return this.mMoveHistory.pop();
    }
}
