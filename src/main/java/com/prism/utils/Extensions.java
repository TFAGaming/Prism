package com.prism.utils;

import java.io.File;

public class Extensions {
    public static boolean isImageFormat(File file) {
        String extension = getFileExtension(file);

        switch (extension) {
            case "png":
            case "jpeg":
            case "jpg":
                return true;
            default:
                return false;
        }
    }

    private static String getFileExtension(File file) {
        if (file == null || file.getName() == null) {
            return "";
        }

        String name = file.getName();

        int lastDot = name.lastIndexOf('.');

        if (lastDot == -1 || lastDot == name.length() - 1) {
            return "";
        }

        return name.substring(lastDot + 1).toLowerCase();
    }
}
