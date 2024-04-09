package com.tugalsan.api.url.client;

import com.google.gwt.http.client.*;
import com.tugalsan.api.runnable.client.TGS_RunnableType1;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.union.client.TGS_Union;

public class TGC_UrlRequestUtils {

    final private static TGC_Log d = TGC_Log.of(TGC_UrlRequestUtils.class);

    public static int getStatusCodeOk() {
        return 200;
    }

    public static TGS_Union<Boolean> get(TGS_Url url, TGS_RunnableType1<Response> onResponse) {
        try {
            var builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url.toString()));
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    d.ce("get.onError", "ERROR: Couldn't connect to server (could be timeout, SOP violation, etc.");
                    onResponse.run(null);
                }

                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        d.ci("get.onResponseReceived", "ok");
                    } else {
                        d.ce("get.onResponseReceived", "status", response.getStatusCode(), response.getStatusText());
                    }
                    onResponse.run(response);
                }
            });
            return TGS_Union.of(true);
        } catch (RequestException ex) {
            d.ce("post.onError", "ERROR: Couldn't connect to server");
            d.ce("post.onError", ex);
            onResponse.run(null);
            return TGS_Union.ofExcuse(ex);
        }
    }

    public static TGS_Union<Boolean> post(TGS_Url url, CharSequence requestData, TGS_RunnableType1<Response> onResponse) {
        try {
            var builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url.toString()));
            builder.setHeader("Content-type", "application/x-www-form-urlencoded");
            builder.sendRequest(requestData.toString(), new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    d.ce("post.onError", "ERROR: Couldn't connect to server (could be timeout, SOP violation, etc.");
                    onResponse.run(null);
                }

                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        d.ci("post.onError", "ok");
                    } else {
                        d.ce("post.onResponseReceived", "status", response.getStatusCode(), response.getStatusText());
                    }
                    onResponse.run(response);
                }
            });
            return TGS_Union.of(true);
        } catch (RequestException ex) {
            d.ce("post.onError", "ERROR: Couldn't connect to server");
            d.ce("post.onError", ex);
            onResponse.run(null);
            return TGS_Union.ofExcuse(ex);
        }
    }
}
