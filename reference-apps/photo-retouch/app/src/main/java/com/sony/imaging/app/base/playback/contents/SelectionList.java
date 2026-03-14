package com.sony.imaging.app.base.playback.contents;

import com.sony.imaging.app.base.playback.LogHelper;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public class SelectionList {
    private static Index mTemporaryIndex = new Index(-1, -1);
    private Map<Index, ContentsIdentifier> mMap = new TreeMap();
    private int mSelectable;

    /* loaded from: classes.dex */
    public static class Index implements Comparable<Index> {
        private static final int HASH = 32713;
        int mGroup = -1;
        int mFile = -1;

        public Index(int group, int file) {
            set(group, file);
        }

        public void set(int group, int file) {
            this.mGroup = group;
            this.mFile = file;
        }

        public int hashCode() {
            return (this.mGroup * HASH) + this.mFile;
        }

        public boolean equals(Object o) {
            if (o == null || !o.getClass().isInstance(Index.class)) {
                return false;
            }
            Index i = (Index) o;
            return this.mGroup == i.mGroup && this.mFile == i.mFile;
        }

        public String toString() {
            return LogHelper.getScratchBuilder(LogHelper.MSG_OPEN_BRACKET).append(this.mGroup).append(LogHelper.MSG_COMMA).append(this.mFile).append(LogHelper.MSG_CLOSE_BRACKET).toString();
        }

        @Override // java.lang.Comparable
        public int compareTo(Index another) {
            return this.mGroup == another.mGroup ? this.mFile - another.mFile : this.mGroup - another.mGroup;
        }
    }

    public SelectionList(int selectable) {
        this.mSelectable = selectable;
    }

    public void close() {
        this.mMap.clear();
    }

    public boolean add(int group, int file, ContentsIdentifier id) {
        if (this.mMap.size() >= this.mSelectable) {
            return false;
        }
        Index index = new Index(group, file);
        if (this.mMap.containsKey(index)) {
            this.mMap.remove(index);
        }
        this.mMap.put(index, id);
        return true;
    }

    public void remove(int group, int file) {
        mTemporaryIndex.set(group, file);
        if (this.mMap.containsKey(mTemporaryIndex)) {
            this.mMap.remove(mTemporaryIndex);
        }
    }

    public boolean isSelected(int group, int file) {
        mTemporaryIndex.set(group, file);
        return this.mMap.containsKey(mTemporaryIndex);
    }

    public int count() {
        return this.mMap.size();
    }

    public Iterator<ContentsIdentifier> iterate() {
        return this.mMap.values().iterator();
    }
}
