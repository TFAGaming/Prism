package com.prism.managers;

import java.util.ArrayList;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.templates.CodeTemplate;
import org.fife.ui.rsyntaxtextarea.templates.StaticCodeTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import com.prism.Prism;
import com.prism.utils.ResourceUtil;

public class AutocompleteManager {
    public static Prism prism = Prism.getInstance();

    public static void getBasic(DefaultCompletionProvider provider, String lang) {
        JSONObject autocomplete = prism.pluginLoader.getMergedAutocomplete().getJSON();

        if (autocomplete == null) {
            return;
        }

        for (String language : autocomplete.keySet()) {
            JSONObject langObject = autocomplete.optJSONObject(language);

            if (langObject == null || !language.equalsIgnoreCase(lang)) {
                continue;
            }

            JSONArray basics = langObject.optJSONArray("basic");
            if (basics != null) {
                for (int i = 0; i < basics.length(); i++) {
                    JSONObject basic = basics.optJSONObject(i);
                    if (basic == null) {
                        continue;
                    }

                    String keyword = basic.optString("keyword");
                    String type = basic.optString("type");

                    if (keyword == null || keyword.isEmpty() || type == null || type.isEmpty()) {
                        continue;
                    }

                    BasicCompletion completion = new BasicCompletion(provider, keyword);

                    switch (type.toLowerCase()) {
                        case "datatype":
                            completion.setIcon(ResourceUtil.getIcon("icons/datatype.gif"));
                            break;
                        case "literal":
                            completion.setIcon(ResourceUtil.getIcon("icons/literal.gif"));
                            break;
                        case "keyword":
                            completion.setIcon(ResourceUtil.getIcon("icons/keyword.gif"));
                            break;
                    }

                    provider.addCompletion(completion);
                }
            }
        }
    }

    public static void getShorthand(DefaultCompletionProvider provider, String lang) {
        JSONObject autocomplete = prism.pluginLoader.getMergedAutocomplete().getJSON();

        if (autocomplete == null) {
            return;
        }

        for (String language : autocomplete.keySet()) {
            JSONObject langObject = autocomplete.optJSONObject(language);

            if (langObject == null || !language.equalsIgnoreCase(lang)) {
                continue;
            }

            JSONArray shorthands = langObject.optJSONArray("shorthand");
            if (shorthands != null) {
                for (int i = 0; i < shorthands.length(); i++) {
                    JSONObject shorthand = shorthands.optJSONObject(i);
                    if (shorthand == null) {
                        continue;
                    }

                    String input = shorthand.optString("input");
                    String replacement = shorthand.optString("replacement");
                    String desc = shorthand.optString("description");

                    ShorthandCompletion completion = new ShorthandCompletion(provider, input, replacement, desc);

                    completion.setIcon(ResourceUtil.getIcon("icons/shorthand.gif"));
                    
                    provider.addCompletion(completion);
                }
            }

            return;
        }
    }

    /**
     * @deprecated No longer used
     */
    public static ArrayList<CodeTemplate> getTemplates(String lang) {
        JSONObject autocomplete = prism.pluginLoader.getMergedAutocomplete().getJSON();

        if (autocomplete == null) {
            return null;
        }

        for (String language : autocomplete.keySet()) {
            JSONObject langObject = autocomplete.optJSONObject(language);

            if (langObject == null || !language.equalsIgnoreCase(lang)) {
                continue;
            }

            JSONArray templates = langObject.optJSONArray("templates");
            if (templates != null) {
                ArrayList<CodeTemplate> list = new ArrayList<CodeTemplate>();

                for (int i = 0; i < templates.length(); i++) {
                    JSONObject template = templates.optJSONObject(i);
                    if (template == null) {
                        continue;
                    }

                    String id = template.optString("id");
                    JSONObject caret = template.optJSONObject("caret");
                    String before = caret != null ? caret.optString("before", "") : "";
                    String after = caret != null ? caret.optString("after", "") : null;

                    CodeTemplate ct = new StaticCodeTemplate(id, before, after);

                    list.add(ct);
                }

                return list;
            }

            return null;
        }

        return null;
    }
}