package com.sony.mexi.json.mouse;

/* loaded from: classes.dex */
public class SemanticsBase {
    public CurrentRule rule;
    public String trace = "";

    public void init() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Phrase lhs() {
        return this.rule.lhs();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int rhsSize() {
        return this.rule.rhsSize();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Phrase rhs(int i) {
        return this.rule.rhs(i);
    }

    protected String rhsText(int i, int j) {
        return this.rule.rhsText(i, j);
    }
}
