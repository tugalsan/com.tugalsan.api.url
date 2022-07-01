package com.tugalsan.api.url.server;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import javax.servlet.http.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.url.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.unsafe.client.*;

public class TS_UrlUtils {

    final private static TS_Log d = TS_Log.of(TS_UrlUtils.class.getSimpleName());

    public static TGS_Url toUrl(HttpServletRequest rq) {
        var protocol = rq.getScheme();             // http
        var hostName = rq.getServerName();     // hostname.com
        var hostPort = rq.getServerPort();        // 80
        var appPath = rq.getContextPath();   // /mywebapp
        var subPath = rq.getServletPath();   // /servlet/MyServlet
        var pathInfo = rq.getPathInfo();         // /a/b;c=123
        var queryString = rq.getQueryString();          // d=789
        var url = new StringBuilder();
        url.append(protocol).append("://").append(hostName);
        if (hostPort != 80 && hostPort != 443) {
            url.append(":").append(hostPort);
        }
        url.append(appPath).append(subPath);
        if (pathInfo != null) {
            url.append(pathInfo);
        }
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return TGS_Url.of(url.toString());
    }

    public boolean isReachable(TGS_Url urlo, Integer optionalTimeOut) {
        URL url;
        try {
            url = new URL(urlo.toString());
        } catch (Exception e) {
            return false;
        }
        try {
            var connection = (HttpURLConnection) url.openConnection();
            if (optionalTimeOut != null) {
                connection.setConnectTimeout(optionalTimeOut);
                connection.setReadTimeout(optionalTimeOut);
            }
            connection.setRequestMethod("HEAD");
            var responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (Exception e) {
            return false;//I KNOW
        }
    }

    public Long getLengthInBytes(TGS_Url urlo) {
        URL url;
        try {
            url = new URL(urlo.toString());
        } catch (Exception e) {
            return null;
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            var l = conn.getContentLengthLong();
            conn.disconnect();
            return l;
        } catch (Exception e) {
            if (conn != null) {
                conn.disconnect();
            }
            return null;
        }
    }

    public InputStream newInputStream(TGS_Url url) {
        return TGS_UnSafe.compile(() -> new URL(url.toString()).openConnection().getInputStream());
    }

    public OutputStream newOutputStream(TGS_Url url) {
        return TGS_UnSafe.compile(() -> new URL(url.toString()).openConnection().getOutputStream());
    }

    public static TGS_Url toUrl(Path file) {
        return TGS_UnSafe.compile(
                () -> TGS_Url.of(file.toUri().toURL().toExternalForm()),
                exception -> null
        );
    }

    public static Path toPath(TGS_Url url) {
        return TS_PathUtils.toPath(url.toString());
    }

    public static boolean isUrl(CharSequence str) {
        try {
            new URL(str.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
