package com.sony.mexi.json.mouse;

/* loaded from: classes.dex */
public class SourceString implements Source {
    final String text;

    public SourceString(String s) {
        this.text = s;
    }

    @Override // com.sony.mexi.json.mouse.Source
    public boolean created() {
        return true;
    }

    @Override // com.sony.mexi.json.mouse.Source
    public int end() {
        return this.text.length();
    }

    @Override // com.sony.mexi.json.mouse.Source
    public char at(int p) {
        return this.text.charAt(p);
    }

    @Override // com.sony.mexi.json.mouse.Source
    public String at(int p, int q) {
        return this.text.substring(p, q);
    }

    @Override // com.sony.mexi.json.mouse.Source
    public String where(int p) {
        if (p > 15) {
            return "After '... " + this.text.substring(p - 15, p) + "'";
        }
        if (p > 0) {
            return "After '" + this.text.substring(0, p) + "'";
        }
        return "At start";
    }
}
