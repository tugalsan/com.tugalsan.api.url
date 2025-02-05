package com.tugalsan.api.url.server;

import com.tugalsan.api.crypto.client.TGS_CryptUtils;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.channels.*;
import java.nio.file.*;
import com.tugalsan.api.url.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.unsafe.client.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TS_UrlDownloadUtils {

    final public static TS_Log d = TS_Log.of(false, TS_UrlDownloadUtils.class);

    public static TGS_UnionExcuse<TGS_Time> getTimeLastModified_withoutDownloading(TGS_Url sourceURL) {
        return TGS_UnSafe.call(() -> {
            var url = new URI(sourceURL.toString()).toURL();
            var connection = (HttpURLConnection) url.openConnection();
            var lngLastModified = connection.getLastModified();
            connection.disconnect();
            var zdtLastModified = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lngLastModified), ZoneId.of("GMT"));
            var millisLastModified = zdtLastModified.toInstant().toEpochMilli();
            var timeLastModified = TGS_Time.ofMillis(millisLastModified);
            return TGS_UnionExcuse.of(timeLastModified);
        }, e -> TGS_UnionExcuse.ofExcuse(e));
    }

    public static boolean isReacable(TGS_Url sourceURL) {
        return isReacable(sourceURL, 5);
    }

    public static boolean isReacable(TGS_Url sourceURL, int timeout) {
        var url = sourceURL.url.toString().replaceFirst("^https", "http");
        var urll = TGS_UnSafe.call(() -> URI.create(url).toURL(), e -> null);
        if (urll == null) {
            return false;
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) urll.openConnection();
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
            con.setRequestMethod("HEAD");
            var responseCode = con.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException e) {
            return false;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

//TODO HTTP JDK12  HOW TO DOWNLAOD: https://www.tutorialspoint.com/java11/java11_standard_httpclient.htm
//    public static Optional<TS_UrlDownloadBean<String>> toText(TGS_Url sourceURL, Optional<Duration> timeout) {
//        var httpClient = HttpClient.newBuilder()
//                .version(HttpClient.Version.HTTP_2);
//        if (timeout.isPresent()) {
//            httpClient.connectTimeout(timeout.get());
//        }
//        httpClient.build();
//        return TGS_UnSafe.call(() -> {
//            var request = HttpRequest.newBuilder()
    ////                    .timeout(Duration.ofMinutes(1))
//                    .GET()
//                    .uri(URI.create("https://www.google.com"))
//                    .build();
//        }, e -> {
//            e.printStackTrace();
//            return Optional.empty();
//        });
//    }
//    public static String toText(TGS_Url sourceURL) {
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://openjdk.org/"))
//                .timeout(Duration.ofMinutes(1))
//                .header("Content-Type", "application/json")
//                .POST(BodyPublishers.ofFile(Paths.get("file.json")))
//                .build();
////        client.sendAsync(request, BodyHandlers.ofString())
////                .thenApply(HttpResponse::body)
////                .thenAccept(System.out::println)
////                .join();
//        client.send(request, BodyHandlers.ofString());
//        System.out.println(response.statusCode());
//        System.out.println(response.body());
//    }
//    dont use locks!!!    
//    public static String toText(TGS_Url sourceURL) {
//        return toText(sourceURL, null);
//    }
    public static String toText(TGS_Url sourceURL, Duration timeout) {
        var bytes = toByteArray(sourceURL, timeout);
        if (d.infoEnable && bytes == null) {
            d.ci("toText", "bytes is null");
        }
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }

    public static String toBase64(TGS_Url sourceURL) {
        return toBase64(sourceURL, null);
    }

    public static String toBase64(TGS_Url sourceURL, Duration timeout) {
        var bytes = toByteArray(sourceURL, timeout);
        return TGS_CryptUtils.encrypt64(bytes);
    }

    public static TGS_UnionExcuseVoid toFile(TGS_Url sourceURL, Path destFile) {
        return toFile(sourceURL, destFile, null);
    }

    public static TGS_UnionExcuseVoid toFile(TGS_Url sourceURL, Path destFile, Duration timeout) {
        return TGS_UnSafe.call(() -> {
            var u = TS_FileUtils.deleteFileIfExists(destFile);
            if (u.isExcuse()) {
                return u;
            }
            var url = URI.create(sourceURL.url.toString()).toURL();
            if (timeout != null) {
                var con = url.openConnection();
                var ms = (int) timeout.toMillis();
                con.setConnectTimeout(ms);
                con.setReadTimeout(ms);
                try (var fileOutputStream = new FileOutputStream(destFile.toFile()); var is = con.getInputStream(); var readableByteChannel = Channels.newChannel(is);) {
                    var fileChannel = fileOutputStream.getChannel();
                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    if (TS_FileUtils.isEmptyFile(destFile)) {
                        TS_FileUtils.deleteFileIfExists(destFile);
                        return TGS_UnionExcuseVoid.ofExcuse(d.className, "toFile", "TS_FileUtils.isEmptyFile(destFile)");
                    }
                    return TGS_UnionExcuseVoid.ofVoid();
                }
            }
            try (var fileOutputStream = new FileOutputStream(destFile.toFile()); var readableByteChannel = Channels.newChannel(url.openStream());) {
                var fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                if (TS_FileUtils.isEmptyFile(destFile)) {
                    TS_FileUtils.deleteFileIfExists(destFile);
                    return TGS_UnionExcuseVoid.ofExcuse(d.className, "toFile", "TS_FileUtils.isEmptyFile(destFile)");
                }
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofVoid());
    }

    public static byte[] toByteArray(TGS_Url sourceURL, Duration timeout) {
        return TGS_UnSafe.call(() -> {
            var url = URI.create(sourceURL.url.toString()).toURL();
            d.ci("toByteArray", "url", url);
            var con = url.openConnection();
            d.ci("toByteArray", "con", "open");
            if (timeout != null) {
                var ms_long = timeout.toMillis();
                if (ms_long <= Integer.MAX_VALUE) {
                    var ms_int = (int) ms_long;
                    d.ci("toByteArray", "timeout ms", ms_int);
                    con.setConnectTimeout(ms_int);
                    con.setReadTimeout(ms_int);
                }
            }
            d.ci("toByteArray", "read byte", "started");
            try (var baos = new ByteArrayOutputStream(); var is = con.getInputStream();) {
                var byteChunk = new byte[8 * 1024];
                int n;
                while ((n = is.read(byteChunk)) > 0) {
                    d.ci("toByteArray", "read byte", "reading...");
                    baos.write(byteChunk, 0, n);
                }
                baos.flush();
                var byteArray = baos.toByteArray();
                d.ci("toByteArray", "read byte", "ended", byteArray.length, new String(byteArray, StandardCharsets.UTF_8));
                return byteArray;
            }
        }, e -> {
            if (d.infoEnable) {
                d.ct("toByteArray", e);
            }
            return null;
        });
    }
}
