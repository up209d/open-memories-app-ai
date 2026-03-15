package com.sony.mexi.json.mouse;

import com.sony.mexi.json.mouse.ParserBase;

/* loaded from: classes.dex */
public class ParserMemo extends ParserBase {
    int cacheSize = 1;
    protected Cache[] caches;
    ParserBase.Phrase reuse;

    @Override // com.sony.mexi.json.mouse.ParserBase
    public void init(Source src) {
        super.init(src);
        for (Cache c : this.caches) {
            c.reset();
        }
    }

    public void setMemo(int m) {
        if ((m > 9) || (m < 1)) {
            throw new Error("m=" + m + " outside range 1-9");
        }
        this.cacheSize = m;
    }

    protected boolean saved(Cache c) {
        this.reuse = c.find();
        if (this.reuse != null) {
            return true;
        }
        begin(c.name, c.diag);
        c.save(this.current);
        return false;
    }

    protected boolean savedInner(Cache c) {
        this.reuse = c.find();
        if (this.reuse != null) {
            return true;
        }
        begin("", c.diag);
        c.save(this.current);
        return false;
    }

    protected boolean reuse() {
        if (this.reuse.success) {
            this.pos = this.reuse.end;
            this.current.end = this.pos;
            this.current.rhs.add(this.reuse);
            this.current.errMerge(this.reuse);
            return true;
        }
        this.current.errMerge(this.reuse);
        return false;
    }

    protected boolean reuseInner() {
        if (this.reuse.success) {
            this.pos = this.reuse.end;
            this.current.end = this.pos;
            this.current.rhs.addAll(this.reuse.rhs);
            this.current.errMerge(this.reuse);
            return true;
        }
        this.current.errMerge(this.reuse);
        return false;
    }

    protected boolean reusePred() {
        if (this.reuse.success) {
            return true;
        }
        this.current.errMerge(this.reuse);
        return false;
    }

    /* loaded from: classes.dex */
    protected class Cache {
        ParserBase.Phrase[] cache;
        public final String diag;
        int last;
        public final String name;

        public Cache(String name) {
            this.name = name;
            this.diag = name;
        }

        public Cache(String name, String diag) {
            this.name = name;
            this.diag = diag;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void reset() {
            this.cache = new ParserBase.Phrase[ParserMemo.this.cacheSize];
            this.last = 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void save(ParserBase.Phrase p) {
            this.last = (this.last + 1) % ParserMemo.this.cacheSize;
            this.cache[this.last] = p;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ParserBase.Phrase find() {
            for (ParserBase.Phrase p : this.cache) {
                if (p != null && p.start == ParserMemo.this.pos) {
                    return p;
                }
            }
            return null;
        }
    }
}
