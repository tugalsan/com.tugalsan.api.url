package com.tugalsan.api.url.server;

import com.tugalsan.api.crypto.client.TGS_CryptUtils;
import com.tugalsan.api.file.client.TGS_FileUtilsTur;
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
import com.tugalsan.api.url.client.parser.TGS_UrlParser;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class TS_UrlDownloadUtils {

    final public static TS_Log d = TS_Log.of(false, TS_UrlDownloadUtils.class);

    public static void toFolder(List<TGS_Url> urls, Path destFolder, Duration timeout) {
        urls.parallelStream().forEach(url -> {
            var parser = TGS_UrlParser.of(url);
            var fileLoc = destFolder.resolve(TGS_FileUtilsTur.toSafe(parser.toString()));
            var u_time = TS_UrlDownloadUtils.getTimeLastModified_withoutDownloading(url);

            //SLOWER IMPL
            if (u_time.isExcuse()) {
                var now = TGS_Time.of();

                d.ce("main", "time", url, "TIME_FETCH_FAILED");
                if (!TS_FileUtils.isExistFile(fileLoc)) {
                    d.cr("main", url, fileLoc);
                    var u_download = TS_UrlDownloadUtils.toFile(url, fileLoc, timeout);
                    if (u_download.isExcuse()) {
                        d.ce("main", url, "u_download", u_download.excuse().getMessage());
                        return;
                    }
                    TS_FileUtils.setTimeCreationTime(fileLoc, now);
                    TS_FileUtils.setTimeAccessTime(fileLoc, now);
                    TS_FileUtils.setTimeLastModified(fileLoc, now);
                    d.cr("main", url, "NOT_EXISTS", "DOWNLOADED");
                    return;
                }

                var u_fileTmp = TS_FileUtils.createFileTemp();
                if (u_fileTmp.isExcuse()) {
                    d.ce("main", url, "u_fileTmp", u_fileTmp.excuse().getMessage());
                    return;
                }
                var u_download = TS_UrlDownloadUtils.toFile(url, u_fileTmp.value(), timeout);
                if (u_download.isExcuse()) {
                    d.ce("main", url, "u_download", u_download.excuse().getMessage());
                    return;
                }
                TS_FileUtils.setTimeCreationTime(u_fileTmp.value(), now);
                TS_FileUtils.setTimeAccessTime(u_fileTmp.value(), now);
                TS_FileUtils.setTimeLastModified(u_fileTmp.value(), now);
                var u_same = TS_FileUtils.hasSameContent(u_fileTmp.value(), fileLoc, true);
                if (u_same.isExcuse()) {
                    d.ce("main", "u_same", u_same.excuse().getMessage());
                    return;
                }
                if (u_same.value()) {
                    d.cr("main", url, "BY CONTENT", "ALREADY EXISTS");
                    TS_FileUtils.deleteFileIfExists(u_fileTmp.value());
                    return;
                } else {
                    TS_FileUtils.rename(fileLoc, TS_FileUtils.getNameLabel(fileLoc) + "-"
                            + now.toString_YYYYMMDD_HHMMSS() + "."
                            + TS_FileUtils.getNameType(fileLoc));
                    TS_FileUtils.moveAs(u_fileTmp.value(), fileLoc, false);
                    d.ce("main", url, "BY CONTENT", "NEW FOUND");
                }
                return;
            }

            //FASTER IMPL
            d.cr("main", url, "u_time", u_time.value().toString_YYYY_MM_DD_HH_MM_SS());
            if (TS_FileUtils.isExistFile(fileLoc)) {
                var f_time = TS_FileUtils.getTimeLastModified(fileLoc);
                d.cr("main", url, "f_time", f_time.toString_YYYY_MM_DD_HH_MM_SS());
                if (f_time.equals(u_time.value())) {
                    d.cr("main", url, "BY TIME", "ALREADY EXISTS");
                } else {
                    d.ce("main", url, "BY TIME", "NEW FOUND");
                    var bakName = TS_FileUtils.getNameLabel(fileLoc) + "-"
                            + TS_FileUtils.getTimeLastModified(fileLoc) + "."
                            + TS_FileUtils.getNameType(fileLoc);
                    TS_FileUtils.rename(fileLoc, bakName);
                    var u_download = TS_UrlDownloadUtils.toFile(url, fileLoc, timeout);
                    if (u_download.isExcuse()) {
                        d.ce("main", url, "u_download", u_download.excuse().getMessage());
                        return;
                    }
                    TS_FileUtils.setTimeCreationTime(fileLoc, u_time.value());
                    TS_FileUtils.setTimeAccessTime(fileLoc, u_time.value());
                    TS_FileUtils.setTimeLastModified(fileLoc, u_time.value());
                    d.ce("main", url, "BY TIME", "NEW FOUND", "DOWNLOADED");
                    var u_same = TS_FileUtils.hasSameContent(fileLoc, fileLoc.resolveSibling(bakName), true);
                    if (u_same.isExcuse()) {
                        d.ce("main", "u_same", u_same.excuse().getMessage());
                        return;
                    }
                    if (u_same.value()) {
                        TS_FileUtils.deleteFileIfExists(fileLoc.resolveSibling(bakName));
                        d.ce("main", url, "BY TIME", "NEW FOUND", "DOWNLOADED", "AND DELETED");
                    }
                }
                return;
            }
            d.cr("main", url, fileLoc);
            var u_download = TS_UrlDownloadUtils.toFile(url, fileLoc, timeout);
            if (u_download.isExcuse()) {
                d.ce("main", url, "u_download", u_download.excuse().getMessage());
                return;
            }
            TS_FileUtils.setTimeCreationTime(fileLoc, u_time.value());
            TS_FileUtils.setTimeAccessTime(fileLoc, u_time.value());
            TS_FileUtils.setTimeLastModified(fileLoc, u_time.value());
            d.cr("main", url, "DOWNLOADED");
        });
    }

    public static TGS_UnionExcuse<TGS_Time> getTimeLastModified_withoutDownloading(TGS_Url sourceURL) {
        return TGS_UnSafe.call(() -> {
            var url = new URI(sourceURL.toString()).toURL();
            HttpURLConnection connection = null;
            long lngLastModified;
            try {
                connection = (HttpURLConnection) url.openConnection();
                lngLastModified = connection.getLastModified();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            if (lngLastModified == 0) {
                TGS_UnSafe.thrw(d.className, "getTimeLastModified_withoutDownloading", "(HttpURLConnection) url.openConnection().getLastModified() return 0");
            }
            d.cr("getTimeLastModified_withoutDownloading", "lngLastModified", lngLastModified);
            var zdtLastModified = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lngLastModified), ZoneId.of("GMT"));
            var millisLastModified = zdtLastModified.toInstant().toEpochMilli();
            d.cr("getTimeLastModified_withoutDownloading", "millisLastModified", millisLastModified);
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
