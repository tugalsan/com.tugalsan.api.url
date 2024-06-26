package com.tugalsan.api.url.server;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import javax.servlet.http.*;
import com.tugalsan.api.url.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.unsafe.client.*;

public class TS_UrlUtils {

    final private static TS_Log d = TS_Log.of(TS_UrlUtils.class);

    public static String mime(TGS_Url img) {
        var typ = URLConnection.getFileNameMap().getContentTypeFor(TGS_UrlUtils.getFileNameFull(img));
        if (TGS_StringUtils.cmn().isPresent(typ) && typ.length() < 5) {
            return typ;
        }
        return TGS_UnSafe.call(() -> {
            var url = new URI(img.url.toString()).toURL();
            return url.openConnection().getContentType().replace(";charset=UTF-8", "");
        }, e -> {
            d.ct("mime(TGS_Url img)", e);
            return typ;
        });
    }
//    final private static TS_Log d = TS_Log.of(TS_UrlUtils.class);

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
        var url = TGS_UnSafe.call(() -> URI.create(urlo.toString()).toURL(), e -> null);
        if (url == null) {
            return false;
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            if (optionalTimeOut != null) {
                con.setConnectTimeout(optionalTimeOut);
                con.setReadTimeout(optionalTimeOut);
            }
            con.setRequestMethod("HEAD");
            var responseCode = con.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException e) {
            return false;//I KNOW
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public Long getLengthInBytes(TGS_Url urlo) {
        var url = TGS_UnSafe.call(() -> URI.create(urlo.toString()).toURL(), e -> null);
        if (url == null) {
            return null;
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            var l = conn.getContentLengthLong();
            conn.disconnect();
            return l;
        } catch (IOException e) {
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public InputStream newInputStream(TGS_Url url) {
        return TGS_UnSafe.call(() -> URI.create(url.toString()).toURL().openConnection().getInputStream());
    }

    public OutputStream newOutputStream(TGS_Url url) {
        return TGS_UnSafe.call(() -> URI.create(url.toString()).toURL().openConnection().getOutputStream());
    }

    public static TGS_Url toUrl(Path file) {
        return TGS_UnSafe.call(() -> TGS_Url.of(file.toUri().toURL().toExternalForm()), exception -> null);
    }

    public static TGS_Tuple2<Path, Exception> toPathOrError(TGS_Url url) {
        return TS_PathUtils.toPathOrError(url.toString());
    }

    public static boolean isUrl(CharSequence str) {
        return TGS_UnSafe.call(() -> {
            URI.create(str.toString()).toURL();
            return true;
        }, e -> false);
    }
}
