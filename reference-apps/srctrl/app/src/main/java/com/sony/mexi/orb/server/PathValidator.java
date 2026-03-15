package com.sony.mexi.orb.server;

import java.net.URI;
import java.net.URISyntaxException;

/* loaded from: classes.dex */
public final class PathValidator {
    private PathValidator() {
    }

    public static void throwForInvalidPath(String path) {
        URI uri = URI.create(path);
        if (uri.isAbsolute()) {
            throw new IllegalArgumentException("Absolute URI is unacceptable");
        }
        if (path.endsWith("/")) {
            throw new IllegalArgumentException("Path with trailing slash from server app is rejected, to simplify HTTP client request URI normalization process.");
        }
        if (uri.getQuery() != null) {
            throw new IllegalArgumentException("URL query is unacceptable");
        }
        if (uri.getFragment() != null) {
            throw new IllegalArgumentException("URL fragment is unacceptable");
        }
    }

    public static String toNormalizedPath(String rawUri) throws URISyntaxException {
        if (rawUri == null) {
            throw new URISyntaxException("null", "null URI can't be handled");
        }
        URI uri = new URI(rawUri).normalize();
        String path = uri.getPath();
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }
}
