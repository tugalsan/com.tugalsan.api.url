package com.tugalsan.api.url.client;

import java.util.*;
import com.google.gwt.user.client.*;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.url.TGS_UrlParameterSafe;

public class TGC_UrlQueryCurrentUtils {

    final private static TGC_Log d = TGC_Log.of(TGC_UrlQueryUtils.class);

    public static String getUrlQuery() {
        var query = Window.Location.getQueryString();
        return query == null || query.isEmpty() ? "" : query;
    }

    public static TGS_UnionExcuse<Boolean> getParameterValueBoolean(CharSequence paramName) {
        var strValue = getParameterValueString(paramName);
        if (strValue.isExcuse()) {
            return strValue.toExcuse();
        }
        return TGS_CastUtils.toBoolean(strValue.value());
    }

    public static TGS_UnionExcuse<Integer> getParameterValueInteger(CharSequence paramName) {
        var strValue = getParameterValueString(paramName);
        if (strValue.isExcuse()) {
            return strValue.toExcuse();
        }
        return TGS_CastUtils.toInteger(strValue.value());
    }

    public static TGS_UnionExcuse<Long> getParameterValueLong(CharSequence paramName) {
        var strValue = getParameterValueString(paramName);
        if (strValue.isExcuse()) {
            return strValue.toExcuse();
        }
        return TGS_CastUtils.toLong(strValue.value());
    }

    public static TGS_UnionExcuse<String> getParameterValueString(CharSequence paramName) {
        var value = Window.Location.getParameter(paramName.toString());
        d.ci("getParameterValue", "paramName", paramName, "value", value);
        if (TGS_StringUtils.isNullOrEmpty(value)) {
            TGS_UnionExcuse.ofExcuse(d.className, "getParameterValueString", "TGS_StringUtils.isNullOrEmpty(value)");
        }
        return TGS_UnionExcuse.of(value);
    }

    public static List<TGS_UrlParameterSafe> getParameters(CharSequence defaultValue) {
        List<TGS_UrlParameterSafe> parameters = TGS_ListUtils.of();
        var map = Window.Location.getParameterMap();
        map.entrySet().forEach(entry -> {
            var key = entry.getKey();
            var values = entry.getValue();
            if (values.isEmpty() || values.get(0) == null) {
                parameters.add(new TGS_UrlParameterSafe(key, defaultValue.toString()));
            }
            parameters.add(new TGS_UrlParameterSafe(key, values.get(0)));
        });
        return parameters;
    }
}
