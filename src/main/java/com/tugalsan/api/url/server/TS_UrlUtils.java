package com.tugalsan.api.url.server;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import javax.servlet.http.*;
import com.tugalsan.api.url.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.function.client.maythrow.checkedexceptions.TGS_FuncMTCEUtils;
import com.tugalsan.api.function.client.maythrow.uncheckedexceptions.TGS_FuncMTUCE_OutTyped_In1;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.url.client.parser.TGS_UrlParser;
import java.util.Arrays;

public class TS_UrlUtils {

    final private static TS_Log d = TS_Log.of(TS_UrlUtils.class);

    public static TGS_UnionExcuse<String> mime(TGS_Url urlFile) {
        {
            var typeByFileNameMap = TGS_FuncMTCEUtils.call(() -> {
                var type = URLConnection.getFileNameMap().getContentTypeFor(TGS_UrlUtils.getFileNameFull(urlFile)).replace(";charset=UTF-8", "");
                return TGS_StringUtils.cmn().isPresent(type) && type.length() < 5 ? type : null;
            }, e -> null);
            if (typeByFileNameMap != null) {
                return TGS_UnionExcuse.of(typeByFileNameMap);
            }
        }
        {
            var typeByURLConnection = TGS_FuncMTCEUtils.call(() -> {
                var url = new URI(urlFile.url.toString()).toURL();
                return url.openConnection().getContentType().replace(";charset=UTF-8", "");
            }, e -> null);
            if (typeByURLConnection != null) {
                return TGS_UnionExcuse.of(typeByURLConnection);
            }
        }
        {
            var typeByAddon = mime_addon.stream().map(addon -> addon.call(urlFile)).filter(type -> type != null).findAny().orElse(null);
            if (typeByAddon != null) {
                return TGS_UnionExcuse.of(typeByAddon);
            }
        }
        return TGS_UnionExcuse.ofExcuse(d.className, "mime", "Cannot detect type for " + urlFile);
    }

    private static String mime_with_param(TGS_Url urlFile, String paramName) {
        var param = TGS_UrlParser.of(urlFile).quary.params.stream()
                .filter(p -> p.name.toString().equals(paramName))
                .findAny().orElse(null);
        if (param == null) {
            return null;
        }
        return URLConnection.getFileNameMap().getContentTypeFor(TGS_UrlQueryUtils.param64UrlSafe_2_readable(param.valueSafe));
    }

    public static void mime_addon_with_params(String... paramNames) {
        Arrays.asList(paramNames).forEach(paramName -> {
            mime_addon.add(urlFile -> mime_with_param(urlFile, paramName));
        });
    }
    final private static TS_ThreadSyncLst<TGS_FuncMTUCE_OutTyped_In1<String, TGS_Url>> mime_addon = TS_ThreadSyncLst.ofSlowWrite();

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
        var url = TGS_FuncMTCEUtils.call(() -> URI.create(urlo.toString()).toURL(), e -> null);
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
        var url = TGS_FuncMTCEUtils.call(() -> URI.create(urlo.toString()).toURL(), e -> null);
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
        return TGS_FuncMTCEUtils.call(() -> URI.create(url.toString()).toURL().openConnection().getInputStream());
    }

    public OutputStream newOutputStream(TGS_Url url) {
        return TGS_FuncMTCEUtils.call(() -> URI.create(url.toString()).toURL().openConnection().getOutputStream());
    }

    public static TGS_UnionExcuse<TGS_Url> toUrl(Path file) {
        return TGS_FuncMTCEUtils.call(() -> {
            var url = TGS_Url.of(file.toUri().toURL().toExternalForm());
            return TGS_UnionExcuse.of(url);
        }, e -> TGS_UnionExcuse.ofExcuse(e));
    }

    public static TGS_UnionExcuse<Path> toPath(TGS_Url url) {
        return TS_PathUtils.toPath(url.toString());
    }

    public static boolean isUrl(CharSequence str) {
        return TGS_FuncMTCEUtils.call(() -> {
            URI.create(str.toString()).toURL();
            return true;
        }, e -> false);
    }
}
