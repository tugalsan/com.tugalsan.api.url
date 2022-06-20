package com.tugalsan.api.url.client;

import java.util.*;
import com.google.gwt.user.client.*;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.pack.client.*;

public class TGC_UrlQueryCurrentUtils {

    final private static TGC_Log d = TGC_Log.of(TGC_UrlQueryUtils.class.getSimpleName());

    public static String getUrlQuery() {
        var query = Window.Location.getQueryString();
        return query == null || query.isEmpty() ? "" : query;
    }

    public static Boolean getParameterValue(CharSequence paramName, Boolean defaultValue) {
        var strValue = getParameterValue(paramName.toString(), String.valueOf(defaultValue));
        return TGS_CastUtils.toBoolean(strValue, defaultValue);
    }

    public static Integer getParameterValue(CharSequence paramName, Integer defaultValue) {
        var strValue = getParameterValue(paramName.toString(), String.valueOf(defaultValue));
        return TGS_CastUtils.toInteger(strValue, defaultValue);
    }

    public static Long getParameterValue(CharSequence paramName, Long defaultValue) {
        var strValue = getParameterValue(paramName, String.valueOf(defaultValue));
        return TGS_CastUtils.toLong(strValue, defaultValue);
    }

    public static String getParameterValue(CharSequence paramName, String defaultValue) {
        var value = Window.Location.getParameter(paramName.toString());
        d.ci("getParameterValue", "paramName", paramName, "value", value);
        return value == null || value.isEmpty() ? defaultValue : value;
    }

    public static List<TGS_Pack2<String, String>> getParameters(CharSequence defaultValue) {
        List<TGS_Pack2<String, String>> parameters = TGS_ListUtils.of();
        var map = Window.Location.getParameterMap();
        map.entrySet().forEach(entry -> {
            var key = entry.getKey();
            var values = entry.getValue();
            if (values.isEmpty() || values.get(0) == null) {
                parameters.add(new TGS_Pack2(key, defaultValue.toString()));
            }
            parameters.add(new TGS_Pack2(key, values.get(0)));
        });
        return parameters;
    }
}
