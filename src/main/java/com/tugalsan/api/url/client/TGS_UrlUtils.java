package com.tugalsan.api.url.client;

//import com.google.gwt.http.client.URL;
//import com.google.gwt.http.client.UrlBuilder;
import java.util.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.url.client.parser.*;

public class TGS_UrlUtils {

    public static boolean isChildOf(TGS_Url base, TGS_Url self) {
        if (self.toString().startsWith("../")) {
            return false;
        }
        if (self.toString().startsWith("http:")) {
            return false;
        }
        if (self.toString().startsWith("https:")) {
            return false;
        }
        if (self.toString().startsWith("ftp")) {
            return false;
        }
        if (self.toString().startsWith("ftp:")) {
            return false;
        }
        if (self.toString().startsWith("file:")) {
            return false;
        }
        return true;
    }

    public static TGS_Url trimAnchor(TGS_Url url) {
        var idx = url.toString().indexOf("#");
        if (idx == -1) {
            return url;
        }
        return TGS_Url.of(url.toString().substring(0, idx));
    }

    public static TGS_Url toFull(TGS_Url base, TGS_Url self) {
        if (self.toString().startsWith(base.toString())) {
            return self;
        }
        if (!isChildOf(base, self)) {
            return self;
        }
        if (!base.toString().endsWith("/") && !self.toString().startsWith("/")) {
            return TGS_Url.of(base.toString() + "/" + self.toString());
        }
        return TGS_Url.of(base.toString() + self.toString());
    }

    public static boolean isHackedUrl(TGS_Url url) {
        if (url == null) {
            return false;
        }
        var str = url.toString();
        if (TGS_StringUtils.cmn().isNullOrEmpty(str)) {
            return false;
        }
        return str.contains("../") || str.contains("..\\") || str.startsWith("/") || str.startsWith("\\");
    }

    public static String convertFileLoc2HttpLoc(CharSequence networkFile) {
        return "file:///".concat(networkFile.toString());
    }

    public static String constructURL(TGS_Url urlWithoutQuery, List<TGS_Tuple2<String, String>> parametersSafe) {
        var sb = new StringBuilder(urlWithoutQuery.toString());
        sb.append("?");
        parametersSafe.stream().forEachOrdered(pair -> sb.append(TGS_StringUtils.cmn().concat(pair.value0, "=", pair.value1, "&")));
        return sb.substring(0, sb.length() - 1);
    }

    public static boolean isValidUrl(TGS_Url url) {
        var urls = url.toString();
        return (urls.startsWith("https://") || urls.startsWith("http://") || urls.startsWith("file://") || urls.startsWith("url://"));
    }

    public static boolean isSecure(TGS_Url url) {
        var urlStr = url.toString();
        return urlStr.startsWith("https://") || urlStr.startsWith("ftps://");
    }

    public static String SAFE_CHARS_ALPHA() {
        return "_-.";
    }//REQ: TGS_UrlQueryUtils -> min three chars

    public static String getAppName(TGS_Url url) {
        var parser = TGS_UrlParser.of(url);
        if (parser.path.paths.isEmpty()) {
            return null;
        }
        return parser.path.paths.get(0);
    }

    public static String getFileNameLabel(TGS_Url url) {
        var fileNameFull = getFileNameFull(url);
        var i = fileNameFull.lastIndexOf('.');
        return fileNameFull.substring(0, i);
    }

    public static String getFileNameFull(TGS_Url url) {
        var fullUrl = url.url.toString();
        var i = fullUrl.lastIndexOf('/');
        if (i == 0) {
            return fullUrl.substring(i + 1);
        }
        if (i == -1) {
            return "";
        }
        return fullUrl.substring(i + 1);
    }

    public static String getFileNameType(TGS_Url url) {
        var fullUrl = url.url.toString();
        var i = fullUrl.lastIndexOf('.');
        if (i == 0) {
            return fullUrl.substring(i + 1);
        }
        if (i == -1) {
            return "";
        }
        return fullUrl.substring(i + 1);
    }
}
