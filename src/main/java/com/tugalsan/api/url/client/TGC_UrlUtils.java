package com.tugalsan.api.url.client;

public class TGC_UrlUtils {

    public static String parseIPAndPort(TGS_Url url) {
        var urlStr = url.toString();
        var ss = TGS_UrlUtils.isSecure(url) ? "https://" : "http://";
        var s = urlStr.indexOf(ss);
        urlStr = urlStr.substring(s + ss.length());
        ss = "/";
        s = urlStr.indexOf(ss);
        return urlStr.substring(0, s);
    }

    public static String parsePort(CharSequence ipAndPort) {
        var ipAndPortStr = ipAndPort.toString();
        var ss = ":";
        var s = ipAndPortStr.indexOf(ss);
        return ipAndPortStr.substring(s + 1);
    }

    public static String parseIp(CharSequence ipAndPort) {
        var ipAndPortStr = ipAndPort.toString();
        var ss = ":";
        var s = ipAndPortStr.indexOf(ss);
        if (s == -1) {
            return ipAndPortStr;
        }
        return ipAndPortStr.substring(0, s);
    }

    public static String getAppName(TGS_Url url) {
        var appFolder = getUrlAppFolder(url).toString();
        var appFolderCropped = appFolder.substring(0, appFolder.length() - 1);
        var idx = appFolderCropped.lastIndexOf('/');
        return appFolderCropped.substring(idx + 1);
    }

    public static TGS_Url getUrlAppFolder(TGS_Url url) {
        var urlWQ = TGS_UrlQueryUtils.getUrlWithoutQuery(url).toString();
        var idx = urlWQ.lastIndexOf('/');
        return TGS_Url.of(urlWQ.substring(0, idx + 1));
    }

    public static TGS_Url getUrlWebappsFolder(TGS_Url url) {
        var urlWithoutQueryStr = TGS_UrlQueryUtils.getUrlWithoutQuery(url).toString();
        var idx = urlWithoutQueryStr.indexOf("/", "https://".length());
        return TGS_Url.of(idx == -1 ? urlWithoutQueryStr + "/" : urlWithoutQueryStr.substring(0, idx + 1));
    }
}
