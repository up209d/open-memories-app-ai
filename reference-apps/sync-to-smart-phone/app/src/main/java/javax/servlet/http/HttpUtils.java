package javax.servlet.http;

import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class HttpUtils {
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);

    public static Hashtable parseQueryString(String s) {
        String[] valArray;
        if (s == null) {
            throw new IllegalArgumentException();
        }
        Hashtable ht = new Hashtable();
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            int pos = pair.indexOf(61);
            if (pos == -1) {
                throw new IllegalArgumentException();
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1, pair.length()), sb);
            if (ht.containsKey(key)) {
                String[] oldVals = (String[]) ht.get(key);
                valArray = new String[oldVals.length + 1];
                for (int i = 0; i < oldVals.length; i++) {
                    valArray[i] = oldVals[i];
                }
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[]{val};
            }
            ht.put(key, valArray);
        }
        return ht;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0046, code lost:            r0 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0050, code lost:            throw new java.lang.IllegalArgumentException(r0.getMessage());     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.Hashtable parsePostData(int r8, javax.servlet.ServletInputStream r9) {
        /*
            if (r8 > 0) goto L8
            java.util.Hashtable r6 = new java.util.Hashtable
            r6.<init>()
        L7:
            return r6
        L8:
            if (r9 != 0) goto L10
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            r6.<init>()
            throw r6
        L10:
            byte[] r5 = new byte[r8]
            r3 = 0
        L13:
            int r6 = r8 - r3
            int r1 = r9.read(r5, r3, r6)     // Catch: java.io.IOException -> L29
            if (r1 > 0) goto L34
            java.util.ResourceBundle r6 = javax.servlet.http.HttpUtils.lStrings     // Catch: java.io.IOException -> L29
            java.lang.String r7 = "err.io.short_read"
            java.lang.String r2 = r6.getString(r7)     // Catch: java.io.IOException -> L29
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException     // Catch: java.io.IOException -> L29
            r6.<init>(r2)     // Catch: java.io.IOException -> L29
            throw r6     // Catch: java.io.IOException -> L29
        L29:
            r0 = move-exception
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.String r7 = r0.getMessage()
            r6.<init>(r7)
            throw r6
        L34:
            int r3 = r3 + r1
            int r6 = r8 - r3
            if (r6 > 0) goto L13
            java.lang.String r4 = new java.lang.String     // Catch: java.io.UnsupportedEncodingException -> L46
            r6 = 0
            java.lang.String r7 = "8859_1"
            r4.<init>(r5, r6, r8, r7)     // Catch: java.io.UnsupportedEncodingException -> L46
            java.util.Hashtable r6 = parseQueryString(r4)     // Catch: java.io.UnsupportedEncodingException -> L46
            goto L7
        L46:
            r0 = move-exception
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.String r7 = r0.getMessage()
            r6.<init>(r7)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.servlet.http.HttpUtils.parsePostData(int, javax.servlet.ServletInputStream):java.util.Hashtable");
    }

    private static String parseName(String s, StringBuffer sb) {
        sb.setLength(0);
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            switch (c) {
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                        break;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException e2) {
                        String rest = s.substring(i);
                        sb.append(rest);
                        if (rest.length() != 2) {
                            break;
                        } else {
                            i++;
                            break;
                        }
                    }
                case '+':
                    sb.append(' ');
                    break;
                default:
                    sb.append(c);
                    break;
            }
            i++;
        }
        return sb.toString();
    }

    public static StringBuffer getRequestURL(HttpServletRequest req) {
        StringBuffer url = new StringBuffer();
        String scheme = req.getScheme();
        int port = req.getServerPort();
        String urlPath = req.getRequestURI();
        url.append(scheme);
        url.append("://");
        url.append(req.getServerName());
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            url.append(':');
            url.append(req.getServerPort());
        }
        url.append(urlPath);
        return url;
    }
}
