package com.prism.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.prism.Prism;
import com.prism.components.frames.ErrorDialog;

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
        OPEN_RECENT_FILES(22),
        CHECK_FOR_UPDATES(23),
        MAX_FILE_SIZE_FOR_WARNING(24),
        WARN_BEFORE_OPENING_LARGE_FILES(25),
        SHOW_MATCHED_BRACKET_POPUP(26),
        SHOW_LINE_NUMBERS(27),
        CODE_OUTLINE_IGNORE_COMMENTS(28),
        FILE_EXPLORER_USE_SYSTEM_ICONS(29),
        AUTOCOMPLETE_ENABLED(30),
        AUTOCOMPLETE_AUTO_POPUP_ENABLED(31),
        PRIMARY_SPLITPANE_DIVIDER_LOCATION(32),
        SECONDARY_SPLITPANE_DIVIDER_LOCATION(33),
        RECENT_OPENED_FILES(34),
        SIDEBAR_CLOSABLE_COMPONENT_OPENED(35),
        LOWER_SIDEBAR_CLOSABLE_COMPONENT_OPENED(36),

        // Token color keys
        ANNOTATION(100),
        RESERVED_WORD(101),
        STRING_DOUBLE_QUOTE(102),
        CHARACTER(103),
        BACKQUOTE(104),
        BOOLEAN(105),
        NUMBER_INTEGER_DECIMAL(106),
        NUMBER_FLOAT(107),
        NUMBER_HEXADECIMAL(108),
        REGULAR_EXPRESSION(109),
        MULTI_LINE_COMMENT(110),
        DOCUMENTATION_COMMENT(111),
        EOL_COMMENT(112),
        SEPERATOR(113),
        OPERATOR(114),
        IDENTIFIER(115),
        VARIABLE(116),
        FUNCTION(117),
        PREPROCESSOR(118),
        MARKUP_CDATA(119),
        MARKUP_COMMENT(120),
        MARKUP_DTD(121),
        MARKUP_TAG_ATTRIBUTE(122),
        MARKUP_TAG_ATTRIBUTE_VALUE(123),
        MARKUP_TAG_DELIMITER(124),
        MARKUP_TAG_NAME(125);

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

        try {
            save();
        } catch (Exception e1) {
            ErrorDialog.showErrorDialog(Prism.getInstance(), e1);
        }
    }

    public void set(Key key, int value) {
        configMap.put(key.getId(), Integer.toString(value));

        try {
            save();
        } catch (Exception e1) {
            ErrorDialog.showErrorDialog(Prism.getInstance(), e1);
        }
    }

    public void set(Key key, double value) {
        configMap.put(key.getId(), Double.toString(value));

        try {
            save();
        } catch (Exception e1) {
            ErrorDialog.showErrorDialog(Prism.getInstance(), e1);
        }
    }

    public void set(Key key, boolean value) {
        configMap.put(key.getId(), Boolean.toString(value));

        try {
            save();
        } catch (Exception e1) {
            ErrorDialog.showErrorDialog(Prism.getInstance(), e1);
        }
    }

    public void set(Key key, String[] values) {
        configMap.put(key.getId(), String.join("\\|", values));

        try {
            save();
        } catch (Exception e1) {
            ErrorDialog.showErrorDialog(Prism.getInstance(), e1);
        }
    }

    public String getString(Key key) {
        return configMap.get(key.getId());
    }

    public String getString(Key key, String defaultValue) {
        return configMap.getOrDefault(key.getId(), defaultValue);
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
        return (v != null && !v.isEmpty()) ? v.split("\\|") : new String[0];
    }

    public void save() throws IOException {
        Properties props = new Properties();
        for (Map.Entry<Integer, String> entry : configMap.entrySet()) {
            props.setProperty(String.valueOf(entry.getKey()), entry.getValue());
        }
        try (FileOutputStream fos = new FileOutputStream(this.configFile)) {
            props.store(fos, "Prism Configuration, DO NOT MODIFY IF YOU KNOW WHAT YOU ARE DOING");
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
