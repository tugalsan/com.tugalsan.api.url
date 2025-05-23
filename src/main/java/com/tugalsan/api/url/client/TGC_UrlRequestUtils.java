package com.tugalsan.api.url.client;

import com.google.gwt.http.client.*;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;

import com.tugalsan.api.log.client.TGC_Log;


public class TGC_UrlRequestUtils {

    final private static TGC_Log d = TGC_Log.of(TGC_UrlRequestUtils.class);

    public static int getStatusCodeOk() {
        return 200;
    }

    public static void async_get(TGS_Url url, TGS_FuncMTU_In1<Response> onResponse) { 
        TGS_FuncMTCUtils.run(() -> {
            var builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url.toString()));
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    d.ce("async_get.onError", "ERROR: Couldn't connect to server (could be timeout, SOP violation, etc.");
                    onResponse.run(null);
                }

                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        d.ci("async_get.onResponseReceived", "ok");
                    } else {
                        d.ce("async_get.onResponseReceived", "status", response.getStatusCode(), response.getStatusText());
                    }
                    onResponse.run(response);
                }
            });
        }, e -> {
            d.ce("async_get.onError", "ERROR: Couldn't connect to server");
            d.ce("async_get.onError", e);
            onResponse.run(null);
        });
    }

    public static void async_post(TGS_Url url, CharSequence requestData, TGS_FuncMTU_In1<Response> onResponse) {
        TGS_FuncMTCUtils.run(() -> {
            var builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url.toString()));
            builder.setHeader("Content-type", "application/x-www-form-urlencoded");
            builder.sendRequest(requestData.toString(), new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    d.ce("async_post.onError", "ERROR: Couldn't connect to server (could be timeout, SOP violation, etc.");
                    onResponse.run(null);
                }

                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        d.ci("async_post.onError", "ok");
                    } else {
                        d.ce("async_post.onResponseReceived", "status", response.getStatusCode(), response.getStatusText());
                    }
                    onResponse.run(response);
                }
            });
        }, e -> {
            d.ce("async_post.onError", "ERROR: Couldn't connect to server");
            d.ce("async_post.onError", e);
            onResponse.run(null);
        });
    }
}
