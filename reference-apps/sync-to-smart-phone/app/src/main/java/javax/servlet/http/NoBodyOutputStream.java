package javax.servlet.http;

import java.io.IOException;
import java.util.ResourceBundle;
import javax.servlet.ServletOutputStream;

/* compiled from: HttpServlet.java */
/* loaded from: classes.dex */
class NoBodyOutputStream extends ServletOutputStream {
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
    private int contentLength = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getContentLength() {
        return this.contentLength;
    }

    @Override // java.io.OutputStream
    public void write(int b) {
        this.contentLength++;
    }

    @Override // java.io.OutputStream
    public void write(byte[] buf, int offset, int len) throws IOException {
        if (len >= 0) {
            this.contentLength += len;
        } else {
            lStrings.getString("err.io.negativelength");
            throw new IOException("negative length");
        }
    }
}
