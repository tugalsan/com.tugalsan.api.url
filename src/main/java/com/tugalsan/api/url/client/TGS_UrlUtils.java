package com.tugalsan.api.url.client;

//import com.google.gwt.http.client.URL;
//import com.google.gwt.http.client.UrlBuilder;
import java.util.*;
import com.tugalsan.api.pack.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.url.client.parser.*;

public class TGS_UrlUtils {

    public static boolean isHackedUrl(CharSequence string) {
        if (string == null) {
            return false;
        }
        var str = string.toString();
        if (str.isEmpty()) {
            return false;
        }
        return str.contains("../") || str.contains("..\\") || str.startsWith("/") || str.startsWith("\\");
    }

    public static String convertFileLoc2HttpLoc(CharSequence networkFile) {
        return "file:///".concat(networkFile.toString());
    }

    public static String constructURL(CharSequence urlWithoutQuery, List<TGS_Pack2<String, String>> parametersSafe) {
        var sb = new StringBuilder(urlWithoutQuery);
        sb.append("?");
        parametersSafe.stream().forEachOrdered(pair -> sb.append(TGS_StringUtils.concat(pair.value0, "=", pair.value1, "&")));
        return sb.substring(0, sb.length() - 1);
    }

    public static boolean isValidUrl(CharSequence link) {
        var linkStr = link.toString();
        return (linkStr.startsWith("https://") || linkStr.startsWith("http://") || linkStr.startsWith("file://") || linkStr.startsWith("url://"));
    }

    public static boolean isSecure(CharSequence url) {
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

    public static String getFileNameType(TGS_Url url) {
        var fullName = url.url.toString();
        var i = fullName.lastIndexOf('.');
        if (i == 0) {
            return fullName.substring(i + 1);
        }
        if (i == -1) {
            return "";
        }
        return fullName.substring(i + 1);
    }
}
