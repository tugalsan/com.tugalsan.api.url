package com.tugalsan.api.url.client;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.tuple.client.*;
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

    public static Boolean getParameterValue(TGS_Url url, CharSequence paramName, Boolean defaultValue) {
        var strValue = getParameterValue(url, paramName.toString(), String.valueOf(defaultValue));
        return TGS_CastUtils.toBoolean(strValue, defaultValue);
    }

    public static Integer getParameterValue(TGS_Url url, CharSequence paramName, Integer defaultValue) {
        var strValue = getParameterValue(url, paramName.toString(), String.valueOf(defaultValue));
        return TGS_CastUtils.toInteger(strValue, defaultValue);
    }

    public static Long getParameterValue(TGS_Url url, CharSequence paramName, Long defaultValue) {
        var strValue = getParameterValue(url, paramName, String.valueOf(defaultValue));
        return TGS_CastUtils.toLong(strValue, defaultValue);
    }

    public static String getParameterValue(TGS_Url url, CharSequence paramName, CharSequence defaultValue) {
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
                d.ci("getParameters", "valueOnce", value);
                var defaultValueStr = defaultValue == null ? null : defaultValue.toString();
                var result = value == null || value.isEmpty() ? defaultValueStr : value;
                d.ci("getParameters", "valueTwice", result);
                return result;
            }
        }
        return defaultValue == null ? null : defaultValue.toString();
    }

    public static List<TGS_Tuple2<String, String>> getParameters(TGS_Url url) {
        var query = sliceQuery(url);
        d.ci("getParameters", "query", query);
        List<TGS_Tuple2<String, String>> parameters = TGS_ListUtils.of();
        Arrays.stream(query.split("&")).forEachOrdered(pair -> {
            d.ci("getParameters", "pair", pair);
            var pairParsed = pair.split("=");
            if (pairParsed.length != 2) {
                d.ce("getParameters", "skipping paramPair", pair, "of", url);
                return;
            }
            d.ci("getParameters", "pairParsed", pairParsed[0], pairParsed[1]);
            parameters.add(new TGS_Tuple2(pairParsed[0], pairParsed[1]));
        });
        return parameters;
    }
}
