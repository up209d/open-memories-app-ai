package com.sony.imaging.app.base.common.comparator;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.util.NotificationManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class ParameterComparator<TP> {
    protected static final String TAG = "ParameterComparator";
    List<CameraSetting.ComparatorCommand> mCommandList;
    private String mNotificationTag;

    protected abstract boolean isChanged(TP tp, TP tp2);

    protected NotificationManager getNotificationManager() {
        return CameraNotificationManager.getInstance();
    }

    public ParameterComparator(String tag) throws NullPointerException {
        this.mCommandList = null;
        if (tag == null) {
            throw new NullPointerException("tag is null.");
        }
        this.mCommandList = new ArrayList();
        this.mNotificationTag = tag;
    }

    public ParameterComparator(String tag, CameraSetting.ComparatorCommand command) throws NullPointerException {
        this(tag);
        this.mCommandList.add(command);
    }

    public ParameterComparator(String tag, List<CameraSetting.ComparatorCommand> commands) throws NullPointerException {
        this(tag);
        this.mCommandList = commands;
    }

    public final void compare(TP obj1, TP obj2) {
        if (obj1 != null || obj2 != null) {
            NotificationManager manager = getNotificationManager();
            if ((obj1 == null && obj2 != null) || ((obj1 != null && obj2 == null) || isChanged(obj1, obj2))) {
                manager.notify(this.mNotificationTag);
                if (!this.mCommandList.isEmpty()) {
                    for (CameraSetting.ComparatorCommand command : this.mCommandList) {
                        command.execute();
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static abstract class _SimpleComparator<T, TP> extends ParameterComparator<TP> {
        protected abstract T getValue(TP tp);

        public _SimpleComparator(String tag) {
            super(tag);
        }

        public _SimpleComparator(String tag, CameraSetting.ComparatorCommand command) {
            super(tag, command);
        }

        public _SimpleComparator(String tag, List<CameraSetting.ComparatorCommand> commands) {
            super(tag, commands);
        }

        @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator
        protected boolean isChanged(TP obj1, TP obj2) {
            T value1 = getValue(obj1);
            T value2 = getValue(obj2);
            if (value1 != null) {
                return !value1.equals(value2);
            }
            Log.i(ParameterComparator.TAG, "null is gotten " + getClass().getSimpleName());
            return value2 == null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class _ListComparator<T, TP> extends ParameterComparator<TP> {
        ArrayList<T> mList1;
        ArrayList<T> mList2;

        protected abstract void getValue(TP tp, List<T> list);

        public _ListComparator(String tag) {
            super(tag);
            this.mList1 = new ArrayList<>();
            this.mList2 = new ArrayList<>();
        }

        public _ListComparator(String tag, CameraSetting.ComparatorCommand command) {
            super(tag, command);
            this.mList1 = new ArrayList<>();
            this.mList2 = new ArrayList<>();
        }

        public _ListComparator(String tag, List<CameraSetting.ComparatorCommand> commands) {
            super(tag, commands);
            this.mList1 = new ArrayList<>();
            this.mList2 = new ArrayList<>();
        }

        public _ListComparator(String tag, int capacity) {
            super(tag);
            this.mList1 = new ArrayList<>(capacity);
            this.mList2 = new ArrayList<>(capacity);
        }

        public _ListComparator(String tag, List<CameraSetting.ComparatorCommand> commands, int capacity) {
            super(tag, commands);
            this.mList1 = new ArrayList<>(capacity);
            this.mList2 = new ArrayList<>(capacity);
        }

        @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator
        protected boolean isChanged(TP obj1, TP obj2) {
            this.mList1.clear();
            this.mList2.clear();
            getValue(obj1, this.mList1);
            getValue(obj2, this.mList2);
            return !this.mList1.equals(this.mList2);
        }
    }
}
