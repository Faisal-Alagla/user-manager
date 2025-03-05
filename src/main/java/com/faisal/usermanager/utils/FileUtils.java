package com.faisal.usermanager.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileUtils {

    public static boolean hasValidExtension(String filename, String[] allowedExtensions) {
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        for (String allowedExt : allowedExtensions) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidImageFormat(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return isPng(bytes) || isJpeg(bytes);
    }

    public static boolean isPng(byte[] bytes) {
        return bytes.length >= 8 &&
                bytes[0] == (byte) 0x89 &&
                bytes[1] == (byte) 0x50 && // P
                bytes[2] == (byte) 0x4E && // N
                bytes[3] == (byte) 0x47;   // G
    }

    public static boolean isJpeg(byte[] bytes) {
        return bytes.length >= 2 &&
                bytes[0] == (byte) 0xFF &&
                bytes[1] == (byte) 0xD8;
    }
}