package com.prism.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {
    private final File configFile;

    public enum Key {
        WINDOW_EXTENDED(1),
        WINDOW_WIDTH(2),
        WINDOW_HEIGHT(3),
        WINDOW_POSITION_X(4),
        WINDOW_POSITION_Y(5),
        DIRECTORY_PATH(6),
        ANTI_ALIASING_ENABLED(7),
        TAB_SIZE(8),
        AUTO_INDENT_ENABLED(9),
        CLOSE_CURLY_BRACES(10),
        CLOSE_MARKUP_TAGS(11),
        BOOK_MARKS(12),
        BRACKET_MATCHING_ENABLED(13),
        MARK_OCCURRENCES(14),
        FADE_CURRENT_LINE_HIGHLIGHT(15),
        HIGHLIGHT_CURRENT_LINE(16),
        WORD_WRAP_ENABLED(17),
        WORD_WRAP_STYLE_WORD(18),
        AUTO_COMPLETION_ENABLED(19),
        CODE_FOLDING_ENABLED(20),
        TEXTAREA_ZOOM(21),
        RECENT_FILES(22);

        private final int id;

        Key(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Key fromId(int id) {
            for (Key k : values()) {
                if (k.id == id) {
                    return k;
                }
            }

            return null;
        }
    }

    public Config(File file) {
        this.configFile = file;
    }

    private final Map<Integer, String> configMap = new HashMap<>();

    public void set(Key key, String value) {
        configMap.put(key.getId(), value);
    }

    public void set(Key key, int value) {
        configMap.put(key.getId(), Integer.toString(value));
    }

    public void set(Key key, double value) {
        configMap.put(key.getId(), Double.toString(value));
    }

    public void set(Key key, boolean value) {
        configMap.put(key.getId(), Boolean.toString(value));
    }

    public void set(Key key, String[] values) {
        configMap.put(key.getId(), String.join(",", values));
    }

    public String getString(Key key) {
        return configMap.get(key.getId());
    }

    public int getInt(Key key, int defaultValue) {
        try {
            return Integer.parseInt(configMap.get(key.getId()));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(Key key, double defaultValue) {
        try {
            return Double.parseDouble(configMap.get(key.getId()));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(Key key, boolean defaultValue) {
        String v = configMap.get(key.getId());
        return (v != null) ? Boolean.parseBoolean(v) : defaultValue;
    }

    public String[] getStringArray(Key key) {
        String v = configMap.get(key.getId());
        return (v != null && !v.isEmpty()) ? v.split(",") : new String[0];
    }

    public void save() throws IOException {
        Properties props = new Properties();
        for (Map.Entry<Integer, String> entry : configMap.entrySet()) {
            props.setProperty(String.valueOf(entry.getKey()), entry.getValue());
        }
        try (FileOutputStream fos = new FileOutputStream(this.configFile)) {
            props.store(fos, "Application Configuration");
        }
    }

    public void load() throws IOException {
        if (!configFile.exists())
            return;

        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(this.configFile)) {
            props.load(fis);
        }

        configMap.clear();

        for (String keyStr : props.stringPropertyNames()) {
            int key = Integer.parseInt(keyStr);
            configMap.put(key, props.getProperty(keyStr));
        }
    }
}
