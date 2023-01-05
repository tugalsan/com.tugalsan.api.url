package com.tugalsan.api.url.server;

import java.util.List;

@Deprecated //TODO
public class TS_UrlDownloadBean<T> {
    T value;
    List<String> headers;
    int statusCode;
    
//    public static TS_UrlDownloadBean<String> ofStr(){
//        TS_UrlDownloadBean<String> r = new TS_UrlDownloadBean();
        //            HttpResponse<String> response = httpClient.send(request,
//                    HttpResponse.BodyHandlers.ofString());
//            TS_UrlDownloadBean<String> result = TS_UrlDownloadBean();
//            System.out.println("Status code: " + response.statusCode());
//            System.out.println("Headers: " + response.headers().allValues("content-type"));
//            System.out.println("Body: " + response.body());
//            return Optional.of(response.body());
//    }
}
