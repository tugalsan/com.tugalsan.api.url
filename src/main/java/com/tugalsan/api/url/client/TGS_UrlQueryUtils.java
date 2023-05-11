package com.tugalsan.api.url.client;

import com.tugalsan.api.crypto.client.*;
import com.tugalsan.api.string.client.*;

public class TGS_UrlQueryUtils {

    public static TGS_Url getUrlWithoutQuery(TGS_Url url) {
        var urlStr = url.toString();
        urlStr = urlStr.contains("?") ? urlStr.substring(0, urlStr.indexOf("?")) : urlStr;
        return TGS_Url.of(urlStr.contains("#") ? urlStr.substring(0, urlStr.indexOf("#")) : urlStr);
    }

    @Deprecated //not good practice, which param?
    public static TGS_Url removeLastParam(TGS_Url url) {
        var urlStr = url.toString();
        var idx = urlStr.lastIndexOf("&");
        if (idx != -1) {
            urlStr = urlStr.substring(0, idx);
        }
        return TGS_Url.of(urlStr);
    }

    //BASE64 A–Z, a–z, 0–9, +, / and =
    //URL SAFE -._~
    public static String readable_2_Param64UrlSafe(CharSequence paramValueReadable) {
        if (TGS_StringUtils.isNullOrEmpty(paramValueReadable)) {
            return null;
        }
        return TGS_CryptUtils.encrypt64(paramValueReadable)
                .replace("+", String.valueOf(TGS_UrlUtils.SAFE_CHARS_ALPHA().charAt(0)))
                .replace("/", String.valueOf(TGS_UrlUtils.SAFE_CHARS_ALPHA().charAt(1)))
                .replace("=", String.valueOf(TGS_UrlUtils.SAFE_CHARS_ALPHA().charAt(2)));
    }

    public static String param64UrlSafe_2_readable(CharSequence base64UrlSafe) {
        if (TGS_StringUtils.isNullOrEmpty(base64UrlSafe)) {
            return null;
        }
        return TGS_CryptUtils.decrypt64(base64UrlSafe.toString()
                .replace(String.valueOf(TGS_UrlUtils.SAFE_CHARS_ALPHA().charAt(0)), "+")
                .replace(String.valueOf(TGS_UrlUtils.SAFE_CHARS_ALPHA().charAt(1)), "/")
                .replace(String.valueOf(TGS_UrlUtils.SAFE_CHARS_ALPHA().charAt(2)), "="));
    }
}
