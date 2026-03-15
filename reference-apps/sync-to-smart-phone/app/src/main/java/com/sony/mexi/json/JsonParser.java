package com.sony.mexi.json;

import com.sony.imaging.app.base.shooting.camera.parameters.BooleanSupportedChecker;
import com.sony.mexi.json.mouse.ParserBase;
import com.sony.mexi.json.mouse.Source;

/* loaded from: classes.dex */
public class JsonParser extends ParserBase {
    final JsonSemantics sem = new JsonSemantics();

    public JsValue getValue() {
        return this.sem.getValue();
    }

    public void clearValue() {
        this.sem.clearValue();
    }

    public JsonParser() {
        this.sem.rule = this;
        super.sem = this.sem;
    }

    public boolean parse(Source src) {
        super.init(src);
        this.sem.init();
        if (Json()) {
            return true;
        }
        return failure();
    }

    public JsonSemantics semantics() {
        return this.sem;
    }

    private boolean Json() {
        begin("Json");
        Space();
        if (!Value()) {
            return reject();
        }
        Space();
        if (!aheadNot()) {
            return reject();
        }
        this.sem.jsonValue();
        return accept();
    }

    private boolean Value() {
        begin("Value");
        if (!Undefined() && !Null() && !Boolean() && !Number() && !String() && !Array() && !Object()) {
            return reject();
        }
        this.sem.jsValue();
        return accept();
    }

    private boolean Undefined() {
        begin("Undefined");
        if (!next("undefined")) {
            return reject();
        }
        this.sem.jsUndefined();
        return accept();
    }

    private boolean Null() {
        begin("Null");
        if (!next("null")) {
            return reject();
        }
        this.sem.jsNull();
        return accept();
    }

    private boolean Boolean() {
        begin("Boolean");
        if (!next(BooleanSupportedChecker.TRUE) && !next("false")) {
            return reject();
        }
        this.sem.jsBoolean();
        return accept();
    }

    private boolean Number() {
        begin("Number");
        if (!Double() && !Integer()) {
            return reject();
        }
        this.sem.jsNumber();
        return accept();
    }

    private boolean Double() {
        begin("Double");
        Double_0();
        if (!Double_1() && !next("NaN") && !next("Infinity")) {
            return reject();
        }
        this.sem.jsDouble();
        return accept();
    }

    private boolean Double_0() {
        begin("");
        if (!next('-')) {
            return rejectInner();
        }
        Space();
        return acceptInner();
    }

    private boolean Double_1() {
        begin("");
        if (Int() && Frac()) {
            Exp();
            return acceptInner();
        }
        return rejectInner();
    }

    private boolean Integer() {
        begin("Integer");
        Integer_0();
        if (!Int() && !OctInt() && !HexInt()) {
            return reject();
        }
        this.sem.jsInteger();
        return accept();
    }

    private boolean Integer_0() {
        begin("");
        if (!next('-')) {
            return rejectInner();
        }
        Space();
        return acceptInner();
    }

    private boolean Int() {
        begin("Int");
        if (!Int_0() && !Digit()) {
            return reject();
        }
        return accept();
    }

    private boolean Int_0() {
        begin("");
        if (!nextIn('1', '9')) {
            return rejectInner();
        }
        if (!Digit()) {
            return rejectInner();
        }
        do {
        } while (Digit());
        return acceptInner();
    }

    private boolean OctInt() {
        begin("OctInt");
        if (!next('0')) {
            return reject();
        }
        if (!OctDigit()) {
            return reject();
        }
        do {
        } while (OctDigit());
        return accept();
    }

    private boolean HexInt() {
        begin("HexInt");
        if (!next("0x")) {
            return reject();
        }
        if (!HexDigit()) {
            return reject();
        }
        do {
        } while (HexDigit());
        return accept();
    }

    private boolean Frac() {
        begin("Frac");
        if (!next('.')) {
            return reject();
        }
        if (!Digit()) {
            return reject();
        }
        do {
        } while (Digit());
        return accept();
    }

    private boolean Exp() {
        begin("Exp");
        if (!next('e') && !next('E')) {
            return reject();
        }
        Exp_0();
        if (!Digit()) {
            return reject();
        }
        do {
        } while (Digit());
        return accept();
    }

    private boolean Exp_0() {
        begin("");
        if (!next('+') && !next('-')) {
            return rejectInner();
        }
        return acceptInner();
    }

    private boolean Digit() {
        begin("Digit");
        return !nextIn('0', '9') ? reject() : accept();
    }

    private boolean OctDigit() {
        begin("OctDigit");
        return !nextIn('0', '7') ? reject() : accept();
    }

    private boolean HexDigit() {
        begin("HexDigit");
        if (!nextIn('0', '9') && !nextIn('a', 'f') && !nextIn('A', 'F')) {
            return reject();
        }
        return accept();
    }

    private boolean String() {
        begin("String");
        if (!String_1()) {
            return reject();
        }
        this.sem.jsString();
        return accept();
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0022, code lost:            if (next('\"') != false) goto L13;     */
    /* JADX WARN: Code restructure failed: missing block: B:15:?, code lost:            return rejectInner();     */
    /* JADX WARN: Code restructure failed: missing block: B:17:?, code lost:            return acceptInner();     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0016, code lost:            if (CharAll() == false) goto L8;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x001c, code lost:            if (Char() != false) goto L15;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean String_1() {
        /*
            r2 = this;
            r1 = 34
            java.lang.String r0 = ""
            r2.begin(r0)
            boolean r0 = r2.next(r1)
            if (r0 != 0) goto L12
            boolean r0 = r2.rejectInner()
        L11:
            return r0
        L12:
            boolean r0 = r2.CharAll()
            if (r0 != 0) goto L1e
        L18:
            boolean r0 = r2.Char()
            if (r0 != 0) goto L18
        L1e:
            boolean r0 = r2.next(r1)
            if (r0 != 0) goto L29
            boolean r0 = r2.rejectInner()
            goto L11
        L29:
            boolean r0 = r2.acceptInner()
            goto L11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.mexi.json.JsonParser.String_1():boolean");
    }

    private boolean CharAll() {
        begin("CharAll");
        if (!ErrorCheck()) {
            return reject();
        }
        this.sem.jsCharText();
        return accept();
    }

    private boolean ErrorCheck() {
        int stringLen = 0;
        boolean flagLoop = true;
        boolean flagCharSequence = false;
        while (flagLoop) {
            boolean flagEscaped = false;
            boolean flagNormalChar = false;
            if (!next('\\')) {
                flagEscaped = true;
            } else {
                flagCharSequence = true;
                flagLoop = false;
            }
            if (!flagCharSequence) {
                if (!aheadNotIn("\"\\\n")) {
                    flagNormalChar = true;
                }
                if (!flagNormalChar && !nextNotConsume()) {
                    flagNormalChar = true;
                }
                if (flagEscaped && flagNormalChar) {
                    flagLoop = false;
                    posPrevious(stringLen);
                    nextInt(stringLen);
                    stringLen = 0;
                } else if (!flagNormalChar) {
                    this.sem.jsNormalChar();
                }
                stringLen++;
            }
        }
        return !flagCharSequence;
    }

    private boolean Char() {
        begin("Char");
        if (!Escaped() && !Unicode() && !NormalChar()) {
            return reject();
        }
        this.sem.jsChar();
        return accept();
    }

    private boolean Escaped() {
        begin("Escaped");
        if (!next('\\')) {
            return reject();
        }
        if (!next('\\') && !next('/') && !next('\"') && !next('\'') && !next('0') && !next('b') && !next('t') && !next('n') && !next('f') && !next('r')) {
            return reject();
        }
        this.sem.jsEscaped();
        return accept();
    }

    private boolean Unicode() {
        begin("Unicode");
        if (next('\\') && next('u') && HexDigit() && HexDigit() && HexDigit() && HexDigit()) {
            this.sem.jsUnicode();
            return accept();
        }
        return reject();
    }

    private boolean NormalChar() {
        begin("NormalChar");
        if (aheadNotIn("\"\\\n") && next()) {
            this.sem.jsNormalChar();
            return accept();
        }
        return reject();
    }

    private boolean Array() {
        begin("Array");
        if (!Array_0() && !Array_1()) {
            return reject();
        }
        this.sem.jsArray();
        return accept();
    }

    private boolean Array_0() {
        begin("");
        if (!next('[')) {
            return rejectInner();
        }
        do {
        } while (Array_2());
        Space();
        if (!Value()) {
            return rejectInner();
        }
        Space();
        return !next(']') ? rejectInner() : acceptInner();
    }

    private boolean Array_1() {
        begin("");
        if (!next('[')) {
            return rejectInner();
        }
        Space();
        return !next(']') ? rejectInner() : acceptInner();
    }

    private boolean Array_2() {
        begin("");
        Space();
        if (!Value()) {
            return rejectInner();
        }
        Space();
        return !next(',') ? rejectInner() : acceptInner();
    }

    private boolean Object() {
        begin("Object");
        if (!Object_0() && !Object_1()) {
            return reject();
        }
        this.sem.jsObject();
        return accept();
    }

    private boolean Object_0() {
        begin("");
        if (!next('{')) {
            return rejectInner();
        }
        do {
        } while (Object_2());
        Space();
        if (!Pair()) {
            return rejectInner();
        }
        Space();
        return !next('}') ? rejectInner() : acceptInner();
    }

    private boolean Object_1() {
        begin("");
        if (!next('{')) {
            return rejectInner();
        }
        Space();
        return !next('}') ? rejectInner() : acceptInner();
    }

    private boolean Object_2() {
        begin("");
        Space();
        if (!Pair()) {
            return rejectInner();
        }
        Space();
        return !next(',') ? rejectInner() : acceptInner();
    }

    private boolean Pair() {
        begin("Pair");
        if (!String()) {
            return reject();
        }
        Space();
        if (!next(':')) {
            return reject();
        }
        Space();
        if (!Value()) {
            return reject();
        }
        this.sem.jsPair();
        return accept();
    }

    private boolean Space() {
        begin("Space");
        do {
        } while (Space_0());
        return accept();
    }

    private boolean Space_0() {
        begin("");
        if (!next(' ') && !next('\t') && !next('\n')) {
            return rejectInner();
        }
        return acceptInner();
    }
}
