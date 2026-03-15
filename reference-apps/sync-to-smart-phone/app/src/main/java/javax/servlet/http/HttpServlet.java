package javax.servlet.http;

import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/* loaded from: classes.dex */
public abstract class HttpServlet extends GenericServlet implements Serializable {
    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_get_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    protected long getLastModified(HttpServletRequest req) {
        return -1L;
    }

    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        NoBodyResponse response = new NoBodyResponse(resp);
        doGet(req, response);
        response.setContentLength();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_post_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_put_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_delete_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    private Method[] getAllDeclaredMethods(Class c) {
        if (c.equals(HttpServlet.class)) {
            return null;
        }
        Method[] parentMethods = getAllDeclaredMethods(c.getSuperclass());
        Method[] thisMethods = c.getDeclaredMethods();
        if (parentMethods != null && parentMethods.length > 0) {
            Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
            System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
            System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);
            return allMethods;
        }
        return thisMethods;
    }

    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Method[] methods = getAllDeclaredMethods(getClass());
        boolean ALLOW_GET = false;
        boolean ALLOW_HEAD = false;
        boolean ALLOW_POST = false;
        boolean ALLOW_PUT = false;
        boolean ALLOW_DELETE = false;
        for (Method m : methods) {
            if (m.getName().equals("doGet")) {
                ALLOW_GET = true;
                ALLOW_HEAD = true;
            }
            if (m.getName().equals("doPost")) {
                ALLOW_POST = true;
            }
            if (m.getName().equals("doPut")) {
                ALLOW_PUT = true;
            }
            if (m.getName().equals("doDelete")) {
                ALLOW_DELETE = true;
            }
        }
        String allow = null;
        if (ALLOW_GET && 0 == 0) {
            allow = METHOD_GET;
        }
        if (ALLOW_HEAD) {
            allow = allow == null ? METHOD_HEAD : String.valueOf(allow) + ", HEAD";
        }
        if (ALLOW_POST) {
            allow = allow == null ? METHOD_POST : String.valueOf(allow) + ", POST";
        }
        if (ALLOW_PUT) {
            allow = allow == null ? METHOD_PUT : String.valueOf(allow) + ", PUT";
        }
        if (ALLOW_DELETE) {
            allow = allow == null ? METHOD_DELETE : String.valueOf(allow) + ", DELETE";
        }
        if (1 != 0) {
            allow = allow == null ? METHOD_TRACE : String.valueOf(allow) + ", TRACE";
        }
        if (1 != 0) {
            allow = allow == null ? METHOD_OPTIONS : String.valueOf(allow) + ", OPTIONS";
        }
        resp.setHeader("Allow", allow);
    }

    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String responseString = "TRACE " + req.getRequestURI() + ExposureModeController.SOFT_SNAP + req.getProtocol();
        Enumeration reqHeaderEnum = req.getHeaderNames();
        while (reqHeaderEnum.hasMoreElements()) {
            String headerName = (String) reqHeaderEnum.nextElement();
            responseString = String.valueOf(responseString) + "\r\n" + headerName + AbstractSupportedChecker.COLON + req.getHeader(headerName);
        }
        String responseString2 = String.valueOf(responseString) + "\r\n";
        int responseLength = responseString2.length();
        resp.setContentType("message/http");
        resp.setContentLength(responseLength);
        ServletOutputStream out = resp.getOutputStream();
        out.print(responseString2);
        out.close();
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (method.equals(METHOD_GET)) {
            long lastModified = getLastModified(req);
            if (lastModified == -1) {
                doGet(req, resp);
                return;
            }
            long ifModifiedSince = req.getDateHeader(HEADER_IFMODSINCE);
            if (ifModifiedSince < (lastModified / 1000) * 1000) {
                maybeSetLastModified(resp, lastModified);
                doGet(req, resp);
                return;
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }
        if (method.equals(METHOD_HEAD)) {
            maybeSetLastModified(resp, getLastModified(req));
            doHead(req, resp);
            return;
        }
        if (method.equals(METHOD_POST)) {
            doPost(req, resp);
            return;
        }
        if (method.equals(METHOD_PUT)) {
            doPut(req, resp);
            return;
        }
        if (method.equals(METHOD_DELETE)) {
            doDelete(req, resp);
            return;
        }
        if (method.equals(METHOD_OPTIONS)) {
            doOptions(req, resp);
        } else {
            if (method.equals(METHOD_TRACE)) {
                doTrace(req, resp);
                return;
            }
            String errMsg = lStrings.getString("http.method_not_implemented");
            Object[] errArgs = {method};
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, MessageFormat.format(errMsg, errArgs));
        }
    }

    private void maybeSetLastModified(HttpServletResponse resp, long lastModified) {
        if (!resp.containsHeader(HEADER_LASTMOD) && lastModified >= 0) {
            resp.setDateHeader(HEADER_LASTMOD, lastModified);
        }
    }

    @Override // javax.servlet.GenericServlet, javax.servlet.Servlet
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            service(request, response);
        } catch (ClassCastException e) {
            throw new ServletException("non-HTTP request or response");
        }
    }
}
