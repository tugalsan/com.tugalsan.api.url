package com.tugalsan.api.url.server;

import java.net.*;
import java.nio.charset.*;

public class TS_UrlQueryUtils {

    public static String toParamValueSafe(CharSequence paramValueReadable) {
        if (paramValueReadable == null) {
            return null;
        }
        return URLEncoder.encode(paramValueReadable.toString(), StandardCharsets.UTF_8).replace("\\+", "%20");
    }

    public static String toParamValueReadable(CharSequence paramValueSafe) {
        if (paramValueSafe == null) {
            return null;
        }
        return URLDecoder.decode(paramValueSafe.toString(), StandardCharsets.UTF_8);
    }
}
