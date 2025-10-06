package com.prism.utils;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public class ResourceUtil {

    public static ImageIcon getIcon(String resourcePath) {
        URL url = ResourceUtil.class.getClassLoader().getResource(resourcePath);

        if (url != null) {
            return new ImageIcon(url);
        }

        return null;
    }

    public static ImageIcon getIcon(String resourcePath, int size) {
        URL url = ResourceUtil.class.getClassLoader().getResource(resourcePath);

        if (url != null) {
            ImageIcon imageIcon = new ImageIcon(url);

            Image image = imageIcon.getImage();
            Image newImg = image.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(newImg);

            return imageIcon;
        }

        return null;
    }
}
