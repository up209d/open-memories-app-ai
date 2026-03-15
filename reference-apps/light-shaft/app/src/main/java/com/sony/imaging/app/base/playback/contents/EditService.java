package com.sony.imaging.app.base.playback.contents;

/* loaded from: classes.dex */
public abstract class EditService {
    public static final int ERR_OVER_SELECTABLE_COUNT = -2;
    private static final String MSG_ONLY_CURRENT_GROUP_SUPPORTED = "Only curernt group is supported";
    public static final int OPERATION_CANCELLED = -1;
    public static final int SUCCEEDED = 0;
    protected ExecutionListener mListener;
    protected ContentsManager mMgr = ContentsManager.getInstance();

    /* loaded from: classes.dex */
    public interface ExecutionListener {
        void onEnd(int i);

        void onProgress(int i, int i2);
    }

    public abstract void cancel();

    public abstract int countSelected();

    public abstract void execute();

    public abstract int getSelectableCount();

    public abstract void initialize();

    public abstract boolean isGroupChecked();

    public abstract boolean isGroupSelectable();

    public abstract boolean isSelectable(int i);

    public abstract boolean isSelected(int i);

    public abstract int select(boolean z, int i);

    public abstract int selectGroup(boolean z);

    public abstract void terminate();

    public boolean isChecked(int file) {
        return isSelected(file);
    }

    public void registerListener(ExecutionListener listener) {
        this.mListener = listener;
    }

    public void unregisterListener(ExecutionListener listener) {
        if (this.mListener == listener) {
            this.mListener = null;
        }
    }

    public void notifyProgress(int progress, int total) {
        if (this.mListener != null) {
            this.mListener.onProgress(progress, total);
        }
    }

    public void notifyEnd(int result) {
        if (this.mListener != null) {
            this.mListener.onEnd(result);
        }
    }

    @Deprecated
    public int select(boolean selectIt, int group, int file) {
        if (this.mMgr.getGroupPosition() != group) {
            throw new UnsupportedOperationException(MSG_ONLY_CURRENT_GROUP_SUPPORTED);
        }
        return select(selectIt, file);
    }

    @Deprecated
    public boolean isSelectable(int group, int file) {
        if (this.mMgr.getGroupPosition() != group) {
            throw new UnsupportedOperationException(MSG_ONLY_CURRENT_GROUP_SUPPORTED);
        }
        return isSelectable(file);
    }

    @Deprecated
    public boolean isChecked(int group, int file) {
        if (this.mMgr.getGroupPosition() != group) {
            throw new UnsupportedOperationException(MSG_ONLY_CURRENT_GROUP_SUPPORTED);
        }
        return isChecked(file);
    }

    @Deprecated
    public boolean isSelected(int group, int file) {
        if (this.mMgr.getGroupPosition() != group) {
            throw new UnsupportedOperationException(MSG_ONLY_CURRENT_GROUP_SUPPORTED);
        }
        return isSelected(file);
    }

    @Deprecated
    public int selectGroup(boolean selectIt, int group) {
        if (this.mMgr.getGroupPosition() != group) {
            throw new UnsupportedOperationException(MSG_ONLY_CURRENT_GROUP_SUPPORTED);
        }
        return selectGroup(selectIt);
    }

    public boolean isGroupSelectable(int group) {
        if (this.mMgr.getGroupPosition() != group) {
            throw new UnsupportedOperationException(MSG_ONLY_CURRENT_GROUP_SUPPORTED);
        }
        return isGroupSelectable();
    }

    public boolean isGroupChecked(int group) {
        if (this.mMgr.getGroupPosition() != group) {
            throw new UnsupportedOperationException(MSG_ONLY_CURRENT_GROUP_SUPPORTED);
        }
        return isGroupChecked();
    }
}
