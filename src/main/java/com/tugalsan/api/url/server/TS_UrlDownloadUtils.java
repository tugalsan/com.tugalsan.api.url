package com.tugalsan.api.url.server;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import com.tugalsan.api.url.client.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.unsafe.client.*;

public class TS_UrlDownloadUtils {

    public static boolean isReacable(TGS_Url sourceURL) {
        return isReacable(sourceURL, 5);
    }

    public static boolean isReacable(TGS_Url sourceURL, int timeout) {
        var url = sourceURL.url.toString().replaceFirst("^https", "http");
        var urll = TGS_UnSafe.compile(() -> new URL(url), e -> null);
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
        } catch (Exception e) {
            return false;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public static String toText(TGS_Url sourceURL) {
        var bytes = toByteArray(sourceURL);
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }

    public static String toBase64(TGS_Url sourceURL) {
        var bytes = toByteArray(sourceURL);
        return bytes == null ? null : Base64.getEncoder().encodeToString(bytes);
    }

    public static Path toFile(TGS_Url sourceURL, Path destFile) {
        return TGS_UnSafe.compile(() -> {
            var url = new URL(sourceURL.url.toString());
            TGS_UnSafe.execute(() -> TS_FileUtils.deleteFileIfExists(destFile), e -> TGS_UnSafe.doNothing());//skipping accces denied exception
            try ( var fileOutputStream = new FileOutputStream(destFile.toFile());  var readableByteChannel = Channels.newChannel(url.openStream());) {
                var fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                return destFile;
            }
        }, e -> null);
    }

    public static byte[] toByteArray(TGS_Url sourceURL) {
        return TGS_UnSafe.compile(() -> {
            var url = new URL(sourceURL.url.toString());
            try ( var baos = new ByteArrayOutputStream();  var is = url.openStream();) {
                var byteChunk = new byte[8 * 1024];
                int n;
                while ((n = is.read(byteChunk)) > 0) {
                    baos.write(byteChunk, 0, n);
                }
                baos.flush();
                return baos.toByteArray();
            }
        }, e -> null);
    }
}
