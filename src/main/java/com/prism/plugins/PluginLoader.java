package com.prism.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.prism.Prism;
import com.prism.components.frames.WarningDialog;
import com.prism.plugins.data.PluginAutocomplete;

public class PluginLoader {

    private final List<Plugin> plugins = new ArrayList<>();
    private final File directory;

    public PluginLoader(File directory) {
        this.directory = directory;
    }

    public void loadPlugins() {
        plugins.clear();

        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs();
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                try {
                    String content = Files.readString(Path.of(file.getPath()));

                    JSONObject json = new JSONObject(content);
                    Plugin plugin = new Plugin(json, file);

                    plugins.add(plugin);
                } catch (Exception e) {
                    new WarningDialog(Prism.getInstance(), e);
                }
            }
        }
    }

    public PluginAutocomplete getMergedAutocomplete() {
        JSONObject obj = new JSONObject();

        for (Plugin plugin : plugins) {
            if (plugin.getEnabled()) {
                PluginAutocomplete autocomplete = plugin.getAutocomplete();

                deepMerge(autocomplete.getJSON(), obj);
            }
        }

        return new PluginAutocomplete(obj);
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    private JSONObject deepMerge(JSONObject source, JSONObject target) {
        for (String key : JSONObject.getNames(source)) {
            Object value = source.get(key);
            
            if (!target.has(key)) {
                target.put(key, value);
            } else {
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject) value;
                    deepMerge(valueJson, target.getJSONObject(key));
                } else {
                    target.put(key, value);
                }
            }
        }

        return target;
    }
}
