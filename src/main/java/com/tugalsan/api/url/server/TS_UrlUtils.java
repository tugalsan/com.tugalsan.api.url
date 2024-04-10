package com.tugalsan.api.url.server;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import javax.servlet.http.*;
import com.tugalsan.api.url.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;

public class TS_UrlUtils {

    final private static TS_Log d = TS_Log.of(TS_UrlUtils.class);

    public static TGS_UnionExcuse<String> mime(TGS_Url img) {
        var typ = URLConnection.getFileNameMap().getContentTypeFor(TGS_UrlUtils.getFileNameFull(img));
        if (TGS_StringUtils.isPresent(typ) && typ.length() < 5) {
            return TGS_UnionExcuse.of(typ);
        }
        try {
            var url = new URI(img.url.toString()).toURL();
            return TGS_UnionExcuse.of(url.openConnection().getContentType().replace(";charset=UTF-8", ""));
        } catch (IOException | URISyntaxException ex) {
            return new TGS_UnionExcuse(typ, ex);
        }
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

    public TGS_UnionExcuseVoid isReachable(TGS_Url urlo, Integer optionalTimeOut) {
        try {
            var url = URI.create(urlo.toString()).toURL();
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) url.openConnection();
                if (optionalTimeOut != null) {
                    con.setConnectTimeout(optionalTimeOut);
                    con.setReadTimeout(optionalTimeOut);
                }
                con.setRequestMethod("HEAD");
                var responseCode = con.getResponseCode();
                if (200 <= responseCode && responseCode <= 399) {
                    return TGS_UnionExcuseVoid.ofVoid();
                } else {
                    return TGS_UnionExcuseVoid.ofExcuse(d.className, "isReacable", "response code is %d".formatted(responseCode));
                }
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        } catch (IOException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
        }
    }

    public TGS_UnionExcuse< Long> getLengthInBytes(TGS_Url urlo) {
        try {
            var url = URI.create(urlo.toString()).toURL();
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("HEAD");
                var l = conn.getContentLengthLong();
                conn.disconnect();
                return TGS_UnionExcuse.of(l);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } catch (IOException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    public TGS_UnionExcuse<InputStream> newInputStream(TGS_Url url) {
        try {
            return TGS_UnionExcuse.of(URI.create(url.toString()).toURL().openConnection().getInputStream());
        } catch (IOException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    public TGS_UnionExcuse<OutputStream> newOutputStream(TGS_Url url) {
        try {
            return TGS_UnionExcuse.of(URI.create(url.toString()).toURL().openConnection().getOutputStream());
        } catch (IOException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuse<TGS_Url> toUrl(Path file) {
        try {
            return TGS_UnionExcuse.of(TGS_Url.of(file.toUri().toURL().toExternalForm()));
        } catch (MalformedURLException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuse<Path> toPathOrError(TGS_Url url) {
        return TS_PathUtils.toPathOrError(url.toString());
    }

    public static boolean isUrl(CharSequence str) {
        try {
            URI.create(str.toString()).toURL();
            return true;
        } catch (MalformedURLException ex) {
            return false;
        }
    }
}
