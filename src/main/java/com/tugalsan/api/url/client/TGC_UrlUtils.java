package com.tugalsan.api.url.client;

import static com.tugalsan.api.url.client.TGS_UrlUtils.isSecure;

public class TGC_UrlUtils {

    public static String parseIPAndPort(CharSequence url) {
        var urlStr = url.toString();
        try {
            var ss = isSecure(url) ? "https://" : "http://";
            var s = urlStr.indexOf(ss);
            urlStr = urlStr.substring(s + ss.length());
            ss = "/";
            s = urlStr.indexOf(ss);
            return urlStr.substring(0, s);
        } catch (Exception e) {
            return null;
        }
    }

    public static String parsePort(CharSequence ipAndPort) {
        try {
            var ipAndPortStr = ipAndPort.toString();
            var ss = ":";
            var s = ipAndPortStr.indexOf(ss);
            return ipAndPortStr.substring(s + 1);
        } catch (Exception e) {
            return null;
        }
    }

    public static String parseIp(CharSequence ipAndPort) {
        try {
            var ipAndPortStr = ipAndPort.toString();
            var ss = ":";
            var s = ipAndPortStr.indexOf(ss);
            if (s == -1) {
                return ipAndPortStr;
            }
            return ipAndPortStr.substring(0, s);
        } catch (Exception e) {
            return null;
        }
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
