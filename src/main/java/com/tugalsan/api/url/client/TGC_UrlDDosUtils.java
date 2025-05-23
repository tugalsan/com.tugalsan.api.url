package com.tugalsan.api.url.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.random.client.TGS_RandomUtils;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.url.client.parser.TGS_UrlParser;

public class TGC_UrlDDosUtils {

    public static void attack(TGS_Url url, RequestCallback requestCallback) {
        var parser = TGS_UrlParser.of(url);
        parser.quary.setParameterValueUrlSafe("p", TGS_UrlQueryUtils.readable_2_Param64UrlSafe(
                String.valueOf(TGS_RandomUtils.nextInt(0, Integer.MAX_VALUE)))
        );
        TGS_FuncMTCUtils.run(() -> {
            var builder = new RequestBuilder(RequestBuilder.GET, URL.encode(parser.toString()));
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                }

                @Override
                public void onResponseReceived(Request request, Response response) {
                }
            });
        }, e -> TGS_FuncMTU.empty.run());
    }
}
