package com.sony.imaging.app.base.playback.contents;

import com.sony.imaging.app.base.playback.contents.ContentsManager;

/* loaded from: classes.dex */
public class MoveHelperToSearchInfo implements ContentsManager.MoveHelper {
    private SearchInfo mSearchData;

    public MoveHelperToSearchInfo(SearchInfo searchData) {
        this.mSearchData = searchData;
    }

    @Override // com.sony.imaging.app.base.playback.contents.ContentsManager.MoveHelper
    public boolean apply(ViewMode viewMode) {
        int position;
        if (this.mSearchData == null || -1 == (position = viewMode.findGroup(this.mSearchData))) {
            return false;
        }
        boolean result = viewMode.moveGroupTo(position);
        if (result) {
            int position2 = viewMode.findContents(this.mSearchData);
            if (-1 != position2) {
                return viewMode.moveTo(position2);
            }
            return false;
        }
        return result;
    }
}
