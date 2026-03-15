package javax.servlet;

/* loaded from: classes.dex */
public class UnavailableException extends ServletException {
    private boolean permanent;
    private int seconds;
    private Servlet servlet;

    public UnavailableException(Servlet servlet, String msg) {
        super(msg);
        this.servlet = servlet;
        this.permanent = true;
    }

    public UnavailableException(int seconds, Servlet servlet, String msg) {
        super(msg);
        this.servlet = servlet;
        if (seconds <= 0) {
            this.seconds = -1;
        } else {
            this.seconds = seconds;
        }
        this.permanent = false;
    }

    public UnavailableException(String msg) {
        super(msg);
        this.permanent = true;
    }

    public UnavailableException(String msg, int seconds) {
        super(msg);
        if (seconds <= 0) {
            this.seconds = -1;
        } else {
            this.seconds = seconds;
        }
        this.permanent = false;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    public int getUnavailableSeconds() {
        if (this.permanent) {
            return -1;
        }
        return this.seconds;
    }
}
