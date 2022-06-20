package com.tugalsan.api.url.server;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import com.tugalsan.api.url.client.*;
import com.tugalsan.api.file.server.*;

public class TS_UrlDownloadUtils {

    public static boolean isReacable(TGS_Url sourceURL) {
        return isReacable(sourceURL, 5);
    }

    public static boolean isReacable(TGS_Url sourceURL, int timeout) {
        try {
            var url = sourceURL.url.toString();
            url = url.replaceFirst("^https", "http");
            var connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            var responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (Exception exception) {
            //DO NOTHING
            return false;
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
        URL url;
        try {
            url = new URL(sourceURL.url.toString());
        } catch (Exception e) {
            return null;
        }
        TS_FileUtils.deleteFileIfExists(destFile);
        try ( var fileOutputStream = new FileOutputStream(destFile.toFile());  var readableByteChannel = Channels.newChannel(url.openStream());) {
            var fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            return destFile;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] toByteArray(TGS_Url sourceURL) {
        URL url;
        try {
            url = new URL(sourceURL.url.toString());
        } catch (Exception e) {
            return null;
        }
        try ( var baos = new ByteArrayOutputStream();  var is = url.openStream();) {
            var byteChunk = new byte[8 * 1024];
            int n;
            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
