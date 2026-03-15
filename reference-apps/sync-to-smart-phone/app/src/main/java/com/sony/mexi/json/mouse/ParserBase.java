package com.sony.mexi.json.mouse;

import java.util.Iterator;
import java.util.Vector;

/* loaded from: classes.dex */
public class ParserBase implements CurrentRule {
    int endpos;
    int pos;
    protected SemanticsBase sem;
    Source source;
    protected String trace = "";
    Phrase current = null;

    public void init(Source src) {
        this.source = src;
        this.pos = 0;
        this.endpos = this.source.end();
        this.current = new Phrase("", "", 0);
    }

    @Override // com.sony.mexi.json.mouse.CurrentRule
    public Phrase lhs() {
        return this.current;
    }

    @Override // com.sony.mexi.json.mouse.CurrentRule
    public Phrase rhs(int i) {
        return this.current.rhs.elementAt(i);
    }

    @Override // com.sony.mexi.json.mouse.CurrentRule
    public int rhsSize() {
        return this.current.rhs.size();
    }

    @Override // com.sony.mexi.json.mouse.CurrentRule
    public String rhsText(int i, int j) {
        return this.source.at(rhs(i).start, rhs(j - 1).end);
    }

    public void setTrace(String trace) {
        this.trace = trace;
        this.sem.trace = trace;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean failure() {
        System.out.println(this.current.errMsg());
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void begin(String name) {
        Phrase p = new Phrase(name, name, this.pos);
        p.parent = this.current;
        this.current = p;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void begin(String name, String diag) {
        Phrase p = new Phrase(name, diag, this.pos);
        p.parent = this.current;
        this.current = p;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean accept() {
        Phrase p = pop();
        p.rhs = null;
        if (p.errPos == p.start) {
            p.errSet(p.diag, p.start);
        }
        p.success = true;
        this.current.end = this.pos;
        this.current.rhs.add(p);
        this.current.errMerge(p);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean acceptInner() {
        Phrase p = pop();
        p.success = true;
        this.current.end = this.pos;
        this.current.rhs.addAll(p.rhs);
        this.current.errMerge(p);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean acceptAnd() {
        Phrase p = pop();
        p.end = p.start;
        p.rhs = null;
        p.errClear();
        p.success = true;
        this.pos = p.start;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean acceptNot() {
        Phrase p = pop();
        p.rhs = null;
        p.errClear();
        p.success = true;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean reject() {
        Phrase p = pop();
        p.end = p.start;
        p.rhs = null;
        if (p.errPos == p.start) {
            p.errSet(p.diag, p.start);
        }
        p.success = false;
        this.current.errMerge(p);
        this.pos = p.start;
        return false;
    }

    protected boolean boolReject() {
        this.pos = this.current.start;
        this.current.end = this.pos;
        this.current.rhs.clear();
        this.current.errSet(this.current.diag, this.pos);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean rejectInner() {
        Phrase p = pop();
        p.end = p.start;
        p.rhs = null;
        p.success = false;
        this.current.errMerge(p);
        this.pos = p.start;
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean rejectAnd() {
        Phrase p = pop();
        p.rhs = null;
        p.errSet(p.diag, this.pos);
        p.success = false;
        this.current.errMerge(p);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean rejectNot() {
        Phrase p = pop();
        p.end = p.start;
        p.rhs = null;
        this.pos = p.start;
        p.errSet(p.diag, this.pos);
        p.success = false;
        this.current.errMerge(p);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean next(char ch) {
        return (this.pos >= this.endpos || this.source.at(this.pos) != ch) ? fail("'" + ch + "'") : consume(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean ahead(char ch) {
        if (this.pos >= this.endpos || this.source.at(this.pos) != ch) {
            return fail("'" + ch + "'");
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean aheadNot(char ch) {
        if (this.pos >= this.endpos || this.source.at(this.pos) != ch) {
            return true;
        }
        return fail("not '" + ch + "'");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean next(String s) {
        int lg = s.length();
        return (this.pos + lg > this.endpos || !this.source.at(this.pos, this.pos + lg).equals(s)) ? fail("'" + s + "'") : consume(lg);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean ahead(String s) {
        int lg = s.length();
        if (this.pos + lg > this.endpos || !this.source.at(this.pos, this.pos + lg).equals(s)) {
            return fail("'" + s + "'");
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean aheadNot(String s) {
        int lg = s.length();
        if (this.pos + lg > this.endpos || !this.source.at(this.pos, this.pos + lg).equals(s)) {
            return true;
        }
        return fail("not '" + s + "'");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean nextIn(String s) {
        return (this.pos >= this.endpos || s.indexOf(this.source.at(this.pos)) < 0) ? fail("[" + s + "]") : consume(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean aheadIn(String s) {
        if (this.pos >= this.endpos || s.indexOf(this.source.at(this.pos)) < 0) {
            return fail("[" + s + "]");
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean aheadNotIn(String s) {
        if (this.pos >= this.endpos || s.indexOf(this.source.at(this.pos)) < 0) {
            return true;
        }
        return fail("not [" + s + "]");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean nextIn(char a, char z) {
        return (this.pos >= this.endpos || this.source.at(this.pos) < a || this.source.at(this.pos) > z) ? fail("[" + a + "-" + z + "]") : consume(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean aheadIn(char a, char z) {
        if (this.pos >= this.endpos || this.source.at(this.pos) < a || this.source.at(this.pos) > z) {
            return fail("[" + a + "-" + z + "]");
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean aheadNotIn(char a, char z) {
        if (this.pos >= this.endpos || this.source.at(this.pos) < a || this.source.at(this.pos) > z) {
            return true;
        }
        return fail("not [" + a + "-" + z + "]");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean next() {
        return this.pos < this.endpos ? consume(1) : fail("any character");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean nextInt(int n) {
        return this.pos < this.endpos ? consume(n) : fail("any character");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean nextNotConsume() {
        return this.pos < this.endpos ? posNext(1) : fail("any character");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean ahead() {
        if (this.pos < this.endpos) {
            return true;
        }
        return fail("any character");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean aheadNot() {
        if (this.pos < this.endpos) {
            return fail("end of text");
        }
        return true;
    }

    private Phrase pop() {
        Phrase p = this.current;
        this.current = p.parent;
        p.parent = null;
        return p;
    }

    private boolean consume(int n) {
        Phrase p = new Phrase("", "", this.pos);
        this.pos += n;
        p.end = this.pos;
        this.current.rhs.add(p);
        this.current.end = this.pos;
        return true;
    }

    private boolean posNext(int n) {
        this.pos += n;
        this.current.end = this.pos;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean posPrevious(int n) {
        this.pos -= n;
        this.current.end = this.pos;
        return true;
    }

    private boolean fail(String msg) {
        this.current.errAdd(msg);
        return false;
    }

    /* loaded from: classes.dex */
    public class Phrase implements com.sony.mexi.json.mouse.Phrase {
        final String diag;
        int end;
        final String name;
        final int start;
        boolean success;
        Vector<Phrase> rhs = new Vector<>(10, 10);
        Object value = null;
        Phrase parent = null;
        int errPos = -1;
        Vector<String> errTxt = new Vector<>();

        Phrase(String name, String diag, int start) {
            this.name = name;
            this.diag = diag;
            this.start = start;
            this.end = start;
        }

        @Override // com.sony.mexi.json.mouse.Phrase
        public void put(Object o) {
            this.value = o;
        }

        @Override // com.sony.mexi.json.mouse.Phrase
        public Object get() {
            return this.value;
        }

        @Override // com.sony.mexi.json.mouse.Phrase
        public String text() {
            return ParserBase.this.source.at(this.start, this.end);
        }

        @Override // com.sony.mexi.json.mouse.Phrase
        public char charAt(int i) {
            return ParserBase.this.source.at(this.start + i);
        }

        @Override // com.sony.mexi.json.mouse.Phrase
        public boolean isEmpty() {
            return this.start == this.end;
        }

        @Override // com.sony.mexi.json.mouse.Phrase
        public boolean isA(String s) {
            return this.name.equals(s);
        }

        @Override // com.sony.mexi.json.mouse.Phrase
        public String errMsg() {
            return this.errPos < 0 ? "" : String.valueOf(ParserBase.this.source.where(this.errPos)) + ": expected " + listErr();
        }

        @Override // com.sony.mexi.json.mouse.Phrase
        public void errClear() {
            this.errTxt.clear();
            this.errPos = -1;
        }

        void errSet(String who, int where) {
            this.errTxt.clear();
            this.errTxt.add(who);
            this.errPos = where;
        }

        void errAdd(String who) {
            if (this.errPos <= ParserBase.this.pos) {
                if (this.errPos < ParserBase.this.pos) {
                    this.errTxt.clear();
                    this.errPos = ParserBase.this.pos;
                    this.errTxt.add(who);
                    return;
                }
                this.errTxt.add(who);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void errMerge(Phrase p) {
            if (p.errPos < ParserBase.this.pos && this.errPos < ParserBase.this.pos) {
                errClear();
                return;
            }
            if (p.errPos >= 0 && this.errPos <= p.errPos) {
                if (this.errPos < p.errPos) {
                    this.errTxt.clear();
                    this.errPos = p.errPos;
                    this.errTxt.addAll(p.errTxt);
                    return;
                }
                this.errTxt.addAll(p.errTxt);
            }
        }

        private String listErr() {
            StringBuilder sb = new StringBuilder();
            String sep = "";
            Vector<String> done = new Vector<>();
            Iterator<String> it = this.errTxt.iterator();
            while (it.hasNext()) {
                String s = it.next();
                if (!done.contains(s)) {
                    sb.append(sep);
                    toPrint(s, sb);
                    done.add(s);
                    sep = " or ";
                }
            }
            return sb.toString();
        }

        private void toPrint(String s, StringBuilder sb) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '\b':
                        sb.append("\\b");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    case '\n':
                        sb.append("\\n");
                        break;
                    case 11:
                    default:
                        if (c < ' ' || c > 256) {
                            String u = "000" + Integer.toHexString(c);
                            sb.append("\\u" + u.substring(u.length() - 4, u.length()));
                            break;
                        } else {
                            sb.append(c);
                            break;
                        }
                        break;
                    case '\f':
                        sb.append("\\f");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                }
            }
        }
    }
}
