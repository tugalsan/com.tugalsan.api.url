package com.tugalsan.api.url.client;

import com.google.gwt.http.client.*;
import com.tugalsan.api.executable.client.TGS_ExecutableType1;
import com.tugalsan.api.log.client.TGC_Log;

public class TGC_UrlRequestUtils {

    final private static TGC_Log d = TGC_Log.of(TGC_UrlRequestUtils.class.getSimpleName());

    public static int getStatusCodeOk() {
        return 200;
    }

    public static void get(CharSequence url, TGS_ExecutableType1<Response> onResponse) {
        var builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url.toString()));
//        builder.setHeader("Content-type", "application/x-www-form-urlencoded");
        try {
            var request = builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    d.ce("get.onError", "ERROR: Couldn't connect to server (could be timeout, SOP violation, etc.");
                    onResponse.execute(null);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        d.ci("get.onResponseReceived", "ok");
                    } else {
                        d.ce("get.onResponseReceived", "status", response.getStatusCode(), response.getStatusText());
                    }
                    onResponse.execute(response);
                }
            });
        } catch (RequestException e) {
            d.ce("get.onError", "ERROR: Couldn't connect to server");
            d.ce("get.onError", e);
            onResponse.execute(null);
        }
    }

    public static void post(CharSequence url, CharSequence requestData, TGS_ExecutableType1<Response> onResponse) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url.toString()));
        builder.setHeader("Content-type", "application/x-www-form-urlencoded");
        try {
            var request = builder.sendRequest(requestData.toString(), new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    d.ce("post.onError", "ERROR: Couldn't connect to server (could be timeout, SOP violation, etc.");
                    onResponse.execute(null);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        d.ci("post.onError", "ok");
                    } else {
                        d.ce("post.onResponseReceived", "status", response.getStatusCode(), response.getStatusText());
                    }
                    onResponse.execute(response);
                }
            });
        } catch (RequestException e) {
            d.ce("post.onError", "ERROR: Couldn't connect to server");
            d.ce("post.onError", e);
            onResponse.execute(null);
        }
    }
}
