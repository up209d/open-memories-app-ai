package com.sony.imaging.app.avi.util;

/* loaded from: classes.dex */
public class MethodInfo {
    static final /* synthetic */ boolean $assertionsDisabled;
    static final StackTraceElement NULL_STACKTRACEELEMENT;
    StackTraceElement[] stack = new Throwable().getStackTrace();
    int startIndex;

    static {
        $assertionsDisabled = !MethodInfo.class.desiredAssertionStatus();
        NULL_STACKTRACEELEMENT = new StackTraceElement("", "", "", 0);
    }

    public MethodInfo() {
        int i = 0;
        while (true) {
            if (i >= this.stack.length) {
                break;
            }
            if (!"<init>".equals(this.stack[i].getMethodName())) {
                i++;
            } else {
                this.startIndex = i + 1;
                break;
            }
        }
        if (!$assertionsDisabled && this.stack == null) {
            throw new AssertionError();
        }
    }

    public String getCurrentName() {
        if ($assertionsDisabled || this.stack != null) {
            return getStack(this.startIndex).getMethodName();
        }
        throw new AssertionError();
    }

    public String getCallerName() {
        if ($assertionsDisabled || this.stack != null) {
            return getStack(this.startIndex + 1).getMethodName();
        }
        throw new AssertionError();
    }

    public String getCurrentClassName() {
        if ($assertionsDisabled || this.stack != null) {
            return extractClassName(getStack(this.startIndex).getClassName());
        }
        throw new AssertionError();
    }

    public String getCallerClassName() {
        if ($assertionsDisabled || this.stack != null) {
            return extractClassName(getStack(this.startIndex + 1).getClassName());
        }
        throw new AssertionError();
    }

    StackTraceElement getStack(int index) {
        return this.stack.length > index ? this.stack[index] : NULL_STACKTRACEELEMENT;
    }

    static String extractClassName(String name) {
        int n = name.lastIndexOf(46);
        return n < 0 ? name : name.substring(n + 1);
    }
}
