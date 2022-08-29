package com.tugalsan.api.url.server;

import java.util.*;
import javax.servlet.http.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.url.client.*;

public class TS_UrlServletRequestUtils {

    final private static TS_Log d = TS_Log.of(TS_UrlServletRequestUtils.class);

    public static String getParameterValueFrom64(HttpServletRequest rq, CharSequence paramName) {
        var val64Safe = getParameterValue(rq, paramName, false);
        d.ci("getParameterValueFrom64", paramName, "val64Safe", val64Safe);
        var valReadable = TGS_UrlQueryUtils.param64UrlSafe_2_readable(val64Safe);
        d.ci("getParameterValueFrom64", paramName, "valReadable", valReadable);
        return valReadable;
    }

    @Deprecated //NOT CHARACTER SAFE, USE getParameterValueFrom64 instead!
    public static String getParameterValue(HttpServletRequest rq, CharSequence paramName, boolean readable) {
        var paramNameStr = paramName.toString();
        var paramVal = rq.getParameter(paramNameStr);
        if (!readable) {
            return paramVal;
        }
        var paramValReadable = TS_UrlQueryUtils.toParamValueReadable(paramVal);
//        d.ce("getParameterValue", "paramNameStr/paramVal/paramValReadable", paramNameStr, paramVal, paramValReadable);
        return TGS_StringUtils.toNullIfEmpty(paramValReadable);
    }

    public static List<String> getParameterNames(HttpServletRequest rq) {
        return TGS_StreamUtils.toList(TGS_StreamUtils.of(rq.getParameterNames()));
    }

    public static String getURLStringUnsafe(HttpServletRequest rq) {
        var scheme = rq.getScheme();             // http
        var serverName = rq.getServerName();     // hostname.com
        var serverPort = rq.getServerPort();        // 80
        var contextPath = rq.getContextPath();   // /mywebapp
        var servletPath = rq.getServletPath();   // /servlet/MyServlet
        var pathInfo = rq.getPathInfo();         // /a/b;c=123
        var queryString = rq.getQueryString();          // d=789

        // Reconstruct original requesting URL
        var url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath).append(servletPath);
        if (pathInfo != null) {
            url.append(pathInfo);
        }
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }

    /*
    public static String getFullURL(HttpServletRequest request) {
    StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
    String queryString = request.getQueryString();

    if (queryString == null) {
        return requestURL.toString();
    } else {
        return requestURL.append('?').append(queryString).toString();
    }
}
     */
}
