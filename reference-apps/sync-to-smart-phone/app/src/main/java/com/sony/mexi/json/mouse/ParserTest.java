package com.sony.mexi.json.mouse;

import com.sony.imaging.app.fw.AppRoot;
import com.sony.mexi.json.mouse.ParserBase;
import com.sony.mexi.json.mouse.ParserMemo;
import java.util.BitSet;
import javax.servlet.http.HttpServletResponse;

/* loaded from: classes.dex */
public class ParserTest extends ParserMemo {
    int cacheSize = 1;
    public boolean traceError;
    public boolean traceInner;
    public boolean traceRules;

    protected ParserTest() {
    }

    @Override // com.sony.mexi.json.mouse.ParserMemo
    public void setMemo(int m) {
        if ((m > 9) || (m < 0)) {
            throw new Error("m=" + m + " outside range 0-9");
        }
        this.cacheSize = m;
    }

    @Override // com.sony.mexi.json.mouse.ParserBase
    public void setTrace(String trace) {
        super.setTrace(trace);
        this.traceRules = trace.indexOf(114) >= 0;
        this.traceInner = trace.indexOf(AppRoot.USER_KEYCODE.LEFT) >= 0;
        this.traceError = trace.indexOf(HttpServletResponse.SC_SWITCHING_PROTOCOLS) >= 0;
    }

    public Cache[] caches() {
        return (Cache[]) this.caches;
    }

    void trace(String s) {
        System.out.println(s);
    }

    protected boolean saved(Cache c) {
        c.calls++;
        if (this.traceRules) {
            trace(String.valueOf(this.source.where(this.pos)) + ": INIT " + c.name);
        }
        this.reuse = c.find();
        if (this.reuse != null) {
            c.reuse++;
            if (this.traceRules) {
                trace("REUSE " + (this.reuse.success ? "succ " : "fail "));
            }
            return true;
        }
        begin(c.name, c.diag);
        c.save(this.current);
        if (c.prevpos.get(this.pos)) {
            c.rescan++;
        } else {
            c.prevpos.set(this.pos);
        }
        return false;
    }

    protected boolean savedInner(Cache c) {
        c.calls++;
        if (this.traceInner) {
            trace(String.valueOf(this.source.where(this.pos)) + ": INIT " + c.name);
        }
        this.reuse = c.find();
        if (this.reuse != null) {
            c.reuse++;
            if (this.traceInner) {
                trace("REUSE " + (this.reuse.success ? "succ " : "fail "));
            }
            return true;
        }
        begin("", c.diag);
        c.save(this.current);
        if (c.prevpos.get(this.pos)) {
            c.rescan++;
        } else {
            c.prevpos.set(this.pos);
        }
        return false;
    }

    protected boolean accept(Cache c) {
        super.accept();
        traceAccept(c, this.traceRules);
        return true;
    }

    protected boolean acceptInner(Cache c) {
        super.acceptInner();
        traceAccept(c, this.traceInner);
        return true;
    }

    protected boolean acceptAnd(Cache c) {
        super.acceptAnd();
        traceAccept(c, this.traceInner);
        return true;
    }

    protected boolean acceptNot(Cache c) {
        super.acceptNot();
        traceAccept(c, this.traceInner);
        return true;
    }

    private void traceAccept(Cache c, boolean cond) {
        if (cond) {
            trace(String.valueOf(this.source.where(this.pos)) + ": ACCEPT " + c.name);
            if (this.traceError) {
                trace(String.valueOf(this.current.diag) + "  --" + this.current.errMsg());
            }
        }
        c.succ++;
    }

    protected boolean reject(Cache c) {
        int endpos = this.pos;
        super.reject();
        traceReject(c, this.traceRules, endpos);
        return false;
    }

    protected boolean rejectInner(Cache c) {
        int endpos = this.pos;
        super.rejectInner();
        traceReject(c, this.traceInner, endpos);
        return false;
    }

    protected boolean rejectAnd(Cache c) {
        int endpos = this.pos;
        super.rejectAnd();
        traceReject(c, this.traceInner, endpos);
        return false;
    }

    protected boolean rejectNot(Cache c) {
        int endpos = this.pos;
        super.rejectNot();
        traceReject(c, this.traceInner, endpos);
        return false;
    }

    private void traceReject(Cache c, boolean cond, int endpos) {
        if (cond) {
            trace(String.valueOf(this.source.where(endpos)) + ": REJECT " + c.name);
            if (this.traceError) {
                trace(String.valueOf(this.current.diag) + "  --" + this.current.errMsg());
            }
        }
        if (this.pos != endpos) {
            int b = endpos - this.pos;
            c.back++;
            c.totback += b;
            if (b > c.maxback) {
                c.maxback = b;
                c.maxbpos = this.pos;
                return;
            }
            return;
        }
        c.fail++;
    }

    protected boolean next(char ch, Cache c) {
        int endpos = this.pos;
        boolean succ = super.next(ch);
        return traceTerm(endpos, succ, c);
    }

    protected boolean ahead(char ch, Cache c) {
        int endpos = this.pos;
        boolean succ = super.ahead(ch);
        return traceTerm(endpos, succ, c);
    }

    protected boolean aheadNot(char ch, Cache c) {
        int endpos = this.pos;
        boolean succ = super.aheadNot(ch);
        return traceTerm(endpos, succ, c);
    }

    protected boolean next(String s, Cache c) {
        int endpos = this.pos;
        boolean succ = super.next(s);
        return traceTerm(endpos, succ, c);
    }

    protected boolean ahead(String s, Cache c) {
        int endpos = this.pos;
        boolean succ = super.ahead(s);
        return traceTerm(endpos, succ, c);
    }

    protected boolean aheadNot(String s, Cache c) {
        int endpos = this.pos;
        boolean succ = super.aheadNot(s);
        return traceTerm(endpos, succ, c);
    }

    protected boolean nextIn(String s, Cache c) {
        int endpos = this.pos;
        boolean succ = super.nextIn(s);
        return traceTerm(endpos, succ, c);
    }

    protected boolean aheadIn(String s, Cache c) {
        int endpos = this.pos;
        boolean succ = super.aheadIn(s);
        return traceTerm(endpos, succ, c);
    }

    protected boolean aheadNotIn(String s, Cache c) {
        int endpos = this.pos;
        boolean succ = super.aheadNotIn(s);
        return traceTerm(endpos, succ, c);
    }

    protected boolean nextIn(char a, char z, Cache c) {
        int endpos = this.pos;
        boolean succ = super.nextIn(a, z);
        return traceTerm(endpos, succ, c);
    }

    protected boolean aheadIn(char a, char z, Cache c) {
        int endpos = this.pos;
        boolean succ = super.aheadIn(a, z);
        return traceTerm(endpos, succ, c);
    }

    protected boolean aheadNotIn(char a, char z, Cache c) {
        int endpos = this.pos;
        boolean succ = super.aheadNotIn(a, z);
        return traceTerm(endpos, succ, c);
    }

    protected boolean next(Cache c) {
        int endpos = this.pos;
        boolean succ = super.next();
        return traceTerm(endpos, succ, c);
    }

    protected boolean ahead(Cache c) {
        int endpos = this.pos;
        boolean succ = super.ahead();
        return traceTerm(endpos, succ, c);
    }

    protected boolean aheadNot(Cache c) {
        int endpos = this.pos;
        boolean succ = super.aheadNot();
        return traceTerm(endpos, succ, c);
    }

    private boolean traceTerm(int endpos, boolean succ, Cache c) {
        c.calls++;
        if (c.prevpos.get(endpos)) {
            c.rescan++;
        } else {
            c.prevpos.set(endpos);
        }
        if (succ) {
            c.succ++;
            return true;
        }
        c.fail++;
        return false;
    }

    /* loaded from: classes.dex */
    public class Cache extends ParserMemo.Cache {
        public int back;
        public int calls;
        public int fail;
        public int maxback;
        public int maxbpos;
        BitSet prevpos;
        public int rescan;
        public int reuse;
        public int succ;
        public int totback;

        public Cache(String name) {
            super(name);
        }

        public Cache(String name, String diag) {
            super(name, diag);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.sony.mexi.json.mouse.ParserMemo.Cache
        public void save(ParserBase.Phrase p) {
            if (ParserTest.this.cacheSize != 0) {
                super.save(p);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.sony.mexi.json.mouse.ParserMemo.Cache
        public ParserBase.Phrase find() {
            if (ParserTest.this.cacheSize == 0) {
                return null;
            }
            return super.find();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.sony.mexi.json.mouse.ParserMemo.Cache
        public void reset() {
            super.reset();
            this.calls = 0;
            this.rescan = 0;
            this.reuse = 0;
            this.succ = 0;
            this.fail = 0;
            this.back = 0;
            this.totback = 0;
            this.maxback = 0;
            this.maxbpos = 0;
            this.prevpos = new BitSet(60000);
        }
    }
}
