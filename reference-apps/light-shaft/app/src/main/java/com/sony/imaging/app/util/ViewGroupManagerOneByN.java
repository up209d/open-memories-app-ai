package com.sony.imaging.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class ViewGroupManagerOneByN {
    private static final String EMPTY = "";
    private static final String TAG = "ViewGroupManagerOneByN";
    private static HashMap<String, ExclusiveGroup> groups = new HashMap<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ExclusiveGroup {
        IRelativeLayoutGroup mMain;
        List<IRelativeLayoutGroup> mTargetList;

        private ExclusiveGroup() {
        }

        boolean setMain(IRelativeLayoutGroup view) {
            if (this.mMain != null) {
                return false;
            }
            this.mMain = view;
            return true;
        }

        void addTarget(IRelativeLayoutGroup view) {
            if (this.mTargetList == null) {
                this.mTargetList = new ArrayList();
            }
            this.mTargetList.add(view);
        }

        void removeMain(IRelativeLayoutGroup view) {
            if (this.mMain == view) {
                this.mMain = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void register(IRelativeLayoutGroup view, String group) {
        if (group != null && !"".equals(group)) {
            ExclusiveGroup exclusiveGroup = groups.get(group);
            if (exclusiveGroup == null) {
                exclusiveGroup = new ExclusiveGroup();
                groups.put(group, exclusiveGroup);
            }
            if (IRelativeLayoutGroup.NORMAL.equals(view.getGroupType())) {
                if (!exclusiveGroup.setMain(view)) {
                    throw new RuntimeException("already registered " + view.getClass().getSimpleName() + " in " + group);
                }
            } else {
                exclusiveGroup.addTarget(view);
            }
            updateStatus(exclusiveGroup);
        }
    }

    private static void updateStatus(ExclusiveGroup exclusiveGroup) {
        boolean isExcluding = exclusiveGroup.mMain != null && exclusiveGroup.mMain.isExcluding();
        if (exclusiveGroup.mTargetList != null) {
            for (IRelativeLayoutGroup target : exclusiveGroup.mTargetList) {
                target.onTargetStatusChanged(isExcluding);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void unregister(IRelativeLayoutGroup view, String group) {
        ExclusiveGroup exclusiveGroup;
        if (group != null && !"".equals(group) && (exclusiveGroup = groups.get(group)) != null) {
            if (IRelativeLayoutGroup.NORMAL.equals(view.getGroupType())) {
                exclusiveGroup.removeMain(view);
            } else if (IRelativeLayoutGroup.NOT_EXCLUSIVE.equals(view.getGroupType())) {
                exclusiveGroup.mTargetList.remove(view);
            }
            if (exclusiveGroup.mMain == null) {
                if (exclusiveGroup.mTargetList == null || exclusiveGroup.mTargetList.isEmpty()) {
                    groups.remove(group);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void updateStatus(IRelativeLayoutGroup view, String group) {
        ExclusiveGroup exclusiveGroup;
        if (group != null && !"".equals(group) && !IRelativeLayoutGroup.NOT_EXCLUSIVE.equals(view.getGroupType()) && (exclusiveGroup = groups.get(group)) != null) {
            updateStatus(exclusiveGroup);
        }
    }
}
