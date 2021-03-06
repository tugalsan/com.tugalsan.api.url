package com.tugalsan.api.url.client;

import com.tugalsan.api.compiler.client.*;
import com.tugalsan.api.unsafe.client.*;

public class TGC_UrlUtils {

    public static String parseIPAndPort(CharSequence url) {
        return TGS_UnSafe.compile(() -> {
            var urlStr = url.toString();
            var ss = TGS_UrlUtils.isSecure(url) ? "https://" : "http://";
            var s = urlStr.indexOf(ss);
            urlStr = urlStr.substring(s + ss.length());
            ss = "/";
            s = urlStr.indexOf(ss);
            return urlStr.substring(0, s);
        }, e -> null);
    }

    public static String parsePort(CharSequence ipAndPort) {
        return TGS_UnSafe.compile(() -> {
            var ipAndPortStr = ipAndPort.toString();
            var ss = ":";
            var s = ipAndPortStr.indexOf(ss);
            return ipAndPortStr.substring(s + 1);
        }, e -> null);
    }

    public static String parseIp(CharSequence ipAndPort) {
        return TGS_UnSafe.compile(() -> {
            var ipAndPortStr = ipAndPort.toString();
            var ss = ":";
            var s = ipAndPortStr.indexOf(ss);
            if (s == -1) {
                return ipAndPortStr;
            }
            return ipAndPortStr.substring(0, s);
        }, e -> null);
    }

    public static String getAppName(CharSequence url) {
        var appFolder = getUrlAppFolder(url);
        var appFolderCropped = appFolder.substring(0, appFolder.length() - 1);
        var idx = appFolderCropped.lastIndexOf('/');
        return appFolderCropped.substring(idx + 1);
    }

    public static String getUrlAppFolder(CharSequence url) {
        var urlWQ = TGS_UrlQueryUtils.getUrlWithoutQuery(url);
        var idx = urlWQ.lastIndexOf('/');
        return urlWQ.substring(0, idx + 1);
    }

    public static String getUrlWebappsFolder(CharSequence url) {
        var urlWithoutQueryStr = TGS_UrlQueryUtils.getUrlWithoutQuery(url);
        var idx = urlWithoutQueryStr.indexOf("/", "https://".length());
        return idx == -1 ? urlWithoutQueryStr + "/" : urlWithoutQueryStr.substring(0, idx + 1);
    }
}
