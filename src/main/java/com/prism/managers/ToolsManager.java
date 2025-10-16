package com.prism.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import com.prism.Prism;
import com.prism.components.definition.Tool;
import com.prism.components.frames.ErrorDialog;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ToolsManager {
    public static Prism prism = Prism.getInstance();

    private static final File TOOLS_FILE = Paths.get("tools.json").toFile();
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static List<Tool> tools = new ArrayList<>();
    
    public static void loadTools() {
        if (!TOOLS_FILE.exists()) {
            tools = new ArrayList<>();
            return;
        }
        
        try {
            tools = MAPPER.readValue(TOOLS_FILE, new TypeReference<List<Tool>>() {});
            
            for (Tool tool : tools) {
                prism.toolsList.addRow(tool);
            }

            System.out.println("Loaded tools: " + tools.size());

            prism.menuBar.refreshToolsMenu();
        } catch (Exception e) {
            new ErrorDialog(prism, e);
            
            tools = new ArrayList<>();
        }
    }

    public static void saveTools() {
        try {
            MAPPER.writeValue(TOOLS_FILE, tools);

            prism.menuBar.refreshToolsMenu();
        } catch (Exception e) {
            new ErrorDialog(prism, e);
        }
    }

    public static void addTool(Tool tool) {
        if (tool != null) {
            tools.add(tool);
            prism.toolsList.addRow(tool);
            saveTools();
        }
    }

    public static boolean updateTool(Tool updatedTool) {
        if (updatedTool == null) {
            return false;
        }
        
        UUID idToUpdate = updatedTool.getId();

        for (int i = 0; i < tools.size(); i++) {
            if (tools.get(i).getId().equals(idToUpdate)) {
                tools.set(i, updatedTool); 
                
                saveTools();
                if (prism.menuBar != null) {
                    prism.menuBar.refreshToolsMenu();
                }
                
                return true;
            }
        }
        
        return false;
    }

    public static void removeToolById(UUID toolId) {
        int indexToRemove = -1;

        for (int i = 0; i < tools.size(); i++) {
            if (tools.get(i).getId().equals(toolId)) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove != -1) {
            tools.remove(indexToRemove);
                        
            saveTools();
            
            if (prism.menuBar != null) {
                prism.menuBar.refreshToolsMenu();
            }
        }
    }

    public static boolean isNameUnique(String name) {
        for (int i = 0; i < tools.size(); i++) {
            if (tools.get(i).getName().equalsIgnoreCase(name)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isShortcutUnique(String shortcut) {
        for (int i = 0; i < tools.size(); i++) {
            if (tools.get(i).getShortcut().equalsIgnoreCase(shortcut)) {
                return false;
            }
        }

        return true;
    }

    public static List<Tool> getAllTools() {
        return tools;
    }
}
