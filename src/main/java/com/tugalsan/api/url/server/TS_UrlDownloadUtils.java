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
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import java.time.Duration;

public class TS_UrlDownloadUtils {

    final public static TS_Log d = TS_Log.of(false, TS_UrlDownloadUtils.class);

    public static TGS_UnionExcuseVoid isReacable(TGS_Url sourceURL) {
        return isReacable(sourceURL, 5);
    }

    public static TGS_UnionExcuseVoid isReacable(TGS_Url sourceURL, int timeout) {
        try {
            var url = sourceURL.url.toString().replaceFirst("^https", "http");
            var urll = URI.create(url).toURL();
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) urll.openConnection();
                con.setConnectTimeout(timeout);
                con.setReadTimeout(timeout);
                con.setRequestMethod("HEAD");
                var responseCode = con.getResponseCode();
                if (200 <= responseCode && responseCode <= 399) {
                    return TGS_UnionExcuseVoid.ofVoid();
                } else {
                    return TGS_UnionExcuseVoid.ofExcuse(d.className, "isReacable", "response code is %d".formatted(responseCode));
                }
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        } catch (IOException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
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
    public static TGS_UnionExcuse<String> toText(TGS_Url sourceURL, Duration timeout) {
        var u_bytes = toByteArray(sourceURL, timeout);
        if (u_bytes.isExcuse()) {
            return u_bytes.toExcuse();
        }
        if (d.infoEnable) {
            d.ci("toText", "bytes is null");
        }
        return TGS_UnionExcuse.of(new String(u_bytes.value(), StandardCharsets.UTF_8));
    }

    public static String toBase64_orEmpty(TGS_Url sourceURL) {
        return toBase64_orEmpty(sourceURL, null);
    }

    public static String toBase64_orEmpty(TGS_Url sourceURL, Duration timeout) {
        var bytes = toByteArray(sourceURL, timeout).orElse(null);
        return TGS_CryptUtils.encrypt64_orEmpty(bytes);
    }

    public static TGS_UnionExcuseVoid toFile(TGS_Url sourceURL, Path destFile) {
        return toFile(sourceURL, destFile, null);
    }

    public static TGS_UnionExcuseVoid toFile(TGS_Url sourceURL, Path destFile, Duration timeout) {
        var u_delete = TS_FileUtils.deleteFileIfExists(destFile);
        if (u_delete.isExcuse()) {
            return TGS_UnionExcuseVoid.ofExcuse(u_delete.excuse());
        }
        if (u_delete.isExcuse()) {
            return TGS_UnionExcuseVoid.ofExcuse(d.className, "toFile", "cannot delete %s".formatted(destFile.toString()));
        }
        URL url;
        try {
            url = URI.create(sourceURL.url.toString()).toURL();
        } catch (MalformedURLException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
        }
        if (timeout != null) {
            try {
                var con = url.openConnection();
                var ms = (int) timeout.toMillis();
                con.setConnectTimeout(ms);
                con.setReadTimeout(ms);
                try (var fileOutputStream = new FileOutputStream(destFile.toFile()); var is = con.getInputStream(); var readableByteChannel = Channels.newChannel(is);) {
                    var fileChannel = fileOutputStream.getChannel();
                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    var u_isEmptyFile = TS_FileUtils.isEmptyFile(destFile);
                    if (u_isEmptyFile.isExcuse()) {
                        return TGS_UnionExcuseVoid.ofExcuse(u_isEmptyFile.excuse());
                    }
                    if (!u_isEmptyFile.value()) {
                        return TGS_UnionExcuseVoid.ofVoid();
                    }
                    TS_FileUtils.deleteFileIfExists(destFile);
                    return TGS_UnionExcuseVoid.ofExcuse(d.className, "toFile", "file is downloaded as empty %s".formatted(sourceURL.toString()));
                }
            } catch (IOException ex) {
                return TGS_UnionExcuseVoid.ofExcuse(ex);
            }
        }
        try (var fileOutputStream = new FileOutputStream(destFile.toFile()); var readableByteChannel = Channels.newChannel(url.openStream());) {
            var fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            var u_isEmptyFile = TS_FileUtils.isEmptyFile(destFile);
            if (u_isEmptyFile.isExcuse()) {
                return TGS_UnionExcuseVoid.ofExcuse(u_isEmptyFile.excuse());
            }
            if (!u_isEmptyFile.value()) {
                return TGS_UnionExcuseVoid.ofVoid();
            }
            TS_FileUtils.deleteFileIfExists(destFile);
            return null;
        } catch (IOException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuse<byte[]> toByteArray(TGS_Url sourceURL, Duration timeout) {
        try {
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
                return TGS_UnionExcuse.of(byteArray);
            }
        } catch (IOException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }
}
