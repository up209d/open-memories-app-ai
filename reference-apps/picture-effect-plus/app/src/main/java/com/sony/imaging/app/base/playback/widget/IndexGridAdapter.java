package com.sony.imaging.app.base.playback.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.ViewMode;

/* loaded from: classes.dex */
public class IndexGridAdapter extends BaseAdapter {
    public static final int MOVE_DIRECTION_NEXT = 1;
    public static final int MOVE_DIRECTION_PREVIOUS = -1;
    public static final int POSITION_FIRST = -1;
    public static final int POSITION_LAST = -2;

    @Override // android.widget.Adapter
    public int getCount() {
        return ContentsManager.getInstance().getContentsCount();
    }

    @Override // android.widget.Adapter
    public Object getItem(int arg0) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public int getPosition() {
        return ContentsManager.getInstance().getContentsPosition();
    }

    public boolean setPosition(int position) {
        return ContentsManager.getInstance().moveTo(position);
    }

    public boolean changeGroup(int direction, int contentsPosition) {
        return ContentsManager.getInstance().moveByHelper(new ChangeGroupHelper(direction, contentsPosition));
    }

    public boolean changeGroupInLastLine(int direction, int columnPosition, int columnCount) {
        return ContentsManager.getInstance().moveByHelper(new ChangeGroupInLastLineHelper(direction, columnPosition, columnCount));
    }

    /* loaded from: classes.dex */
    public static class ChangeGroupHelper implements ContentsManager.MoveHelper {
        private final int mContentsPosition;
        private final int mDirection;

        public ChangeGroupHelper(int direction, int contentsPosition) {
            this.mDirection = direction;
            this.mContentsPosition = contentsPosition;
        }

        @Override // com.sony.imaging.app.base.playback.contents.ContentsManager.MoveHelper
        public boolean apply(ViewMode viewMode) {
            boolean result = false;
            if (this.mDirection == 1) {
                result = viewMode.moveGroupToNext(true);
            } else if (this.mDirection == -1) {
                result = viewMode.moveGroupToPrevious(true);
            }
            if (result) {
                int count = viewMode.getContentsCount();
                if (count == 0) {
                    return false;
                }
                int position = -1;
                if (this.mContentsPosition == -1) {
                    position = 0;
                } else if (this.mContentsPosition == -2) {
                    position = count - 1;
                } else if (this.mContentsPosition >= 0) {
                    position = this.mContentsPosition < count ? this.mContentsPosition : count - 1;
                }
                if (position < 0) {
                    result = false;
                } else {
                    result = viewMode.moveTo(position);
                }
            }
            return result;
        }
    }

    /* loaded from: classes.dex */
    public static class ChangeGroupInLastLineHelper implements ContentsManager.MoveHelper {
        private final int mColumnCount;
        private final int mColumnPosition;
        private final int mDirection;

        public ChangeGroupInLastLineHelper(int direction, int columnPosition, int columnCount) {
            this.mDirection = direction;
            this.mColumnPosition = columnPosition;
            this.mColumnCount = columnCount;
        }

        @Override // com.sony.imaging.app.base.playback.contents.ContentsManager.MoveHelper
        public boolean apply(ViewMode viewMode) {
            boolean result = false;
            if (this.mDirection == 1) {
                result = viewMode.moveGroupToNext(true);
            } else if (this.mDirection == -1) {
                result = viewMode.moveGroupToPrevious(true);
            }
            if (result) {
                int last = viewMode.getContentsCount() - 1;
                if (last < 0) {
                    return false;
                }
                int lastColumn = last % this.mColumnCount;
                int position = lastColumn > this.mColumnPosition ? (last - lastColumn) + this.mColumnPosition : last;
                result = viewMode.moveTo(position);
            }
            return result;
        }
    }
}
