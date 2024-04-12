package com.tugalsan.api.url.client;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.url.TGS_UrlParameterSafe;
import java.util.*;

public class TGC_UrlQueryUtils {

    final private static TGC_Log d = TGC_Log.of(TGC_UrlQueryUtils.class);

    public static String sliceQuery(TGS_Url url) {
        var urlStr = url.toString();
        return urlStr.contains("?") ? urlStr.substring(urlStr.indexOf("?") + 1) : "";
    }

    public native static String toParamValueSafe(CharSequence query) /*-{
  encodeURI(query);
}-*/;
//    {
//        var queryStr = query.toString();
//        var size = toSafeQuery_pack.size();
//        for (var i = 0; i < size; i++) {
//            var pack = toSafeQuery_pack.get(i);
//            queryStr = queryStr.replaceAll(pack.value0, pack.value1);
//        }
//        return queryStr;
//    }
//    final private static List<TGS_Tuple2<String, String>> toSafeQuery_pack = TGS_ListUtils.of(
//            new TGS_Tuple2("ş", "%C5%9F"),
//            new TGS_Tuple2("Ş", "%C5%9E"),
//            new TGS_Tuple2("ü", "%C3%BC"),
//            new TGS_Tuple2("Ü", "%C3%9C"),
//            new TGS_Tuple2("ç", "%C3%87"),
//            new TGS_Tuple2("ğ", "%C4%9F"),
//            new TGS_Tuple2("Ğ", "%C4%9E"),
//            new TGS_Tuple2("ı", "%C4%B1"),
//            new TGS_Tuple2("İ", "%C4%B0"),
//            new TGS_Tuple2(":", "%3A"),
//            new TGS_Tuple2("/", "%2F"),
//            new TGS_Tuple2(" ", "%20")
//    );

    public static TGS_UnionExcuse<Boolean> getParameterValueBoolean(TGS_Url url, CharSequence paramName) {
        var strValue = getParameterValueString(url, paramName);
        if (strValue.isExcuse()) {
            return strValue.toExcuse();
        }
        return TGS_CastUtils.toBoolean(strValue.value());
    }

    public static TGS_UnionExcuse<Integer> getParameterValueInteger(TGS_Url url, CharSequence paramName, Integer defaultValue) {
        var strValue = getParameterValueString(url, paramName);
        if (strValue.isExcuse()) {
            return strValue.toExcuse();
        }
        return TGS_CastUtils.toInteger(strValue.value());
    }

    public static TGS_UnionExcuse<Long> getParameterValueLong(TGS_Url url, CharSequence paramName) {
        var strValue = getParameterValueString(url, paramName);
        if (strValue.isExcuse()) {
            return strValue.toExcuse();
        }
        return TGS_CastUtils.toLong(strValue.value());
    }

    public static TGS_UnionExcuse<String> getParameterValueString(TGS_Url url, CharSequence paramName) {
        var query = sliceQuery(url);
        d.ci("getParameters", "query", query);
        for (var pair : query.split("&")) {
            d.ci("getParameters", "pair", pair);
            var pairParsed = pair.split("=");
            if (pairParsed.length != 2) {
                d.ce("getParameterValue", "skipping paramPair", pair, "of", url);
                continue;
            }
            d.ci("getParameters", "pairParsed", pairParsed[0], pairParsed[1]);
            if (Objects.equals(pairParsed[0], paramName)) {
                d.ci("getParameters", "found", pairParsed[0], paramName);
                var value = pairParsed[1];
                d.ci("getParameters", "value", value);
                if (TGS_StringUtils.isNullOrEmpty(value)) {
                    return TGS_UnionExcuse.ofExcuse(TGC_UrlQueryUtils.class.getSimpleName(), "getParameterValue", "TGS_StringUtils.isNullOrEmpty(value)");
                }
                return TGS_UnionExcuse.of(value);
            }
        }
        return TGS_UnionExcuse.ofExcuse(TGC_UrlQueryUtils.class.getSimpleName(), "getParameterValue", "not found");
    }

    public static List<TGS_UrlParameterSafe> getParameters(TGS_Url url) {
        var query = sliceQuery(url);
        d.ci("getParameters", "query", query);
        List<TGS_UrlParameterSafe> parameters = TGS_ListUtils.of();
        Arrays.stream(query.split("&")).forEachOrdered(pair -> {
            d.ci("getParameters", "pair", pair);
            var pairParsed = pair.split("=");
            if (pairParsed.length != 2) {
                d.ce("getParameters", "skipping paramPair", pair, "of", url);
                return;
            }
            d.ci("getParameters", "pairParsed", pairParsed[0], pairParsed[1]);
            parameters.add(new TGS_UrlParameterSafe(pairParsed[0], pairParsed[1]));
        });
        return parameters;
    }
}
