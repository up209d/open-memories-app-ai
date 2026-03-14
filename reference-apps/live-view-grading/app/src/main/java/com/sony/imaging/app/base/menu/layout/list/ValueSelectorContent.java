package com.sony.imaging.app.base.menu.layout.list;

/* loaded from: classes.dex */
public class ValueSelectorContent {
    private Integer guideContentStringId;
    private Integer guideTitleStringId;
    private int valueStringId;

    public ValueSelectorContent(int valueStringId) {
        this(valueStringId, null, null);
    }

    public ValueSelectorContent(int valueStringId, Integer guideTitleStringId, Integer guideContentStringId) {
        this.guideTitleStringId = null;
        this.guideContentStringId = null;
        this.valueStringId = valueStringId;
        this.guideTitleStringId = guideTitleStringId;
        this.guideContentStringId = guideContentStringId;
    }

    public int getValueStringId() {
        return this.valueStringId;
    }

    public boolean isGuideEnabled() {
        return (this.guideContentStringId == null || this.guideTitleStringId == null) ? false : true;
    }

    public Integer getGuideContentStringId() {
        return this.guideContentStringId;
    }

    public Integer getGuideTitleStringId() {
        return this.guideTitleStringId;
    }
}
