package com.prism.plugins;

import java.io.File;
import java.io.IOException;

import org.json.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.prism.plugins.data.PluginAutocomplete;

public class Plugin {
    private final JSONObject json;
    private final File sourceFile;

    private String name;
    private String description;
    private boolean enabled;

    private PluginAutocomplete autocomplete;

    public Plugin(JSONObject json, File sourceFile) {
        this.json = json;
        this.sourceFile = sourceFile;

        this.name = json.optString("name", "Unnamed Plugin");
        this.description = json.optString("description", "No description provided");
        this.enabled = json.optBoolean("enabled", false);

        this.autocomplete = new PluginAutocomplete(json.optJSONObject("autocomplete"));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateEnabledValue();
    }

    public PluginAutocomplete getAutocomplete() {
        return this.autocomplete;
    }

    public JSONObject getAutocompleteJSON() {
        return this.autocomplete.getJSON();
    }

    public File getFile() {
        return sourceFile;
    }

    private void updateEnabledValue() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

            json.put("enabled", this.enabled);

            writer.writeValue(sourceFile, json.toMap());
        } catch (IOException e) {
        }
    }
}