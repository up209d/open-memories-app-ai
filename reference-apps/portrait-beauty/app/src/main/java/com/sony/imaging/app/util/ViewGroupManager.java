package com.sony.imaging.app.util;

import java.util.HashMap;

/* loaded from: classes.dex */
public class ViewGroupManager {
    private static final String EMPTY = "";
    private static HashMap<String, IRelativeLayoutGroup[]> groups = new HashMap<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void register(IRelativeLayoutGroup view, String group) {
        if (group != null && !"".equals(group)) {
            IRelativeLayoutGroup[] views = groups.get(group);
            if (views == null) {
                groups.put(group, new IRelativeLayoutGroup[]{view, null});
            } else {
                if (views[1] != null) {
                    throw new RuntimeException("already registered " + views[0].getClass().getSimpleName() + " and " + views[1].getClass().getSimpleName() + " in " + group);
                }
                views[1] = view;
                updateStatus(views[0], view);
            }
        }
    }

    private static void updateStatus(IRelativeLayoutGroup src, IRelativeLayoutGroup tgt) {
        if (tgt != null) {
            boolean isExcluding = src != null && src.isExcluding();
            tgt.onTargetStatusChanged(isExcluding);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void unregister(IRelativeLayoutGroup view, String group) {
        IRelativeLayoutGroup[] views;
        if (group != null && !"".equals(group) && (views = groups.get(group)) != null) {
            if (views[0] == view) {
                if (views[1] == null) {
                    groups.remove(group);
                    return;
                }
                views[0] = views[1];
                views[1] = null;
                updateStatus((IRelativeLayoutGroup) null, views[0]);
                return;
            }
            views[1] = null;
            if (views[0] != null) {
                updateStatus((IRelativeLayoutGroup) null, views[0]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void updateStatus(IRelativeLayoutGroup view, String group) {
        IRelativeLayoutGroup[] views;
        if (group != null && !"".equals(group) && (views = groups.get(group)) != null) {
            if (views[0] == view) {
                updateStatus(view, views[1]);
            } else {
                updateStatus(view, views[0]);
            }
        }
    }
}
