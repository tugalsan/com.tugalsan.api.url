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
import com.tugalsan.api.unsafe.client.*;
import java.time.Duration;

public class TS_UrlDownloadUtils {

    final public static TS_Log d = TS_Log.of(false, TS_UrlDownloadUtils.class);

    public static boolean isReacable(TGS_Url sourceURL) {
        return isReacable(sourceURL, 5);
    }

    public static boolean isReacable(TGS_Url sourceURL, int timeout) {
        var url = sourceURL.url.toString().replaceFirst("^https", "http");
        var urll = TGS_UnSafe.call(() -> new URL(url), e -> null);
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
    public static String toText(TGS_Url sourceURL) {
        return toText(sourceURL, null);
    }

    public static String toText(TGS_Url sourceURL, Duration timeout) {
        var bytes = toByteArray(sourceURL, timeout);
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }

    public static String toBase64(TGS_Url sourceURL) {
        return toBase64(sourceURL, null);
    }

    public static String toBase64(TGS_Url sourceURL, Duration timeout) {
        var bytes = toByteArray(sourceURL, timeout);
        return TGS_CryptUtils.encrypt64(bytes);
    }

    public static Path toFile(TGS_Url sourceURL, Path destFile) {
        return toFile(sourceURL, destFile, null);
    }

    public static Path toFile(TGS_Url sourceURL, Path destFile, Duration timeout) {
        return TGS_UnSafe.call(() -> {
            if (!TS_FileUtils.deleteFileIfExists(destFile, true)) {
                d.ce("toFile", "cannot delete destFile file, skipped!", sourceURL, destFile);
                return null;
            }
            var url = new URL(sourceURL.url.toString());
            if (timeout != null) {
                var con = url.openConnection();
                var ms = (int) timeout.toMillis();
                con.setConnectTimeout(ms);
                con.setReadTimeout(ms);
                try (var fileOutputStream = new FileOutputStream(destFile.toFile()); var is = con.getInputStream(); var readableByteChannel = Channels.newChannel(is);) {
                    var fileChannel = fileOutputStream.getChannel();
                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    if (!TS_FileUtils.isEmptyFile(destFile)) {
                        return destFile;
                    }
                    TS_FileUtils.deleteFileIfExists(destFile);
                    return null;
                }
            }
            try (var fileOutputStream = new FileOutputStream(destFile.toFile()); var readableByteChannel = Channels.newChannel(url.openStream());) {
                var fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                if (!TS_FileUtils.isEmptyFile(destFile)) {
                    return destFile;
                }
                TS_FileUtils.deleteFileIfExists(destFile);
                return null;
            }
        }, e -> null);
    }

    public static byte[] toByteArray(TGS_Url sourceURL) {
        return toByteArray(sourceURL, null);
    }

    public static byte[] toByteArray(TGS_Url sourceURL, Duration timeout) {
        return TGS_UnSafe.call(() -> {
            var url = new URL(sourceURL.url.toString());
            d.ci("toByteArray", "url", url);
            var con = url.openConnection();
            d.ci("toByteArray", "con", "open");
            if (timeout != null) {
                var ms = (int) timeout.toMillis();
                d.ci("toByteArray", "timeout ms", ms);
                con.setConnectTimeout(ms);
                con.setReadTimeout(ms);
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
                d.ci("toByteArray", "read byte", "ended", byteArray.length);
                return byteArray;
            }
        }, e -> null);
    }
}
