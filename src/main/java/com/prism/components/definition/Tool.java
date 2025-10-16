package com.prism.components.definition;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

public class Tool {
    private UUID id;
    private String name;
    private String description;
    private String directoryPath; 
    private String shortcut;
    private List<String> arguments;

    public Tool() {
        this.arguments = new ArrayList<>();
    }

    public Tool(UUID id, String name, String description, File directory, String shortcut, List<String> arguments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.directoryPath = directory != null ? directory.getAbsolutePath() : ""; 
        this.shortcut = shortcut;
        this.arguments = arguments != null ? arguments : new ArrayList<>();
    }

    public Tool(String name, String description, File directory, String shortcut, List<String> arguments) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.directoryPath = directory != null ? directory.getAbsolutePath() : ""; 
        this.shortcut = shortcut;
        this.arguments = arguments != null ? arguments : new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public File getDirectory() {
        return directoryPath != null && !directoryPath.isEmpty() ? new File(directoryPath) : null;
    }
    
    public String getDirectoryPath() {
        return directoryPath;
    }

    public String getShortcut() {
        return shortcut;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDirectory(File directory) {
        this.directoryPath = directory != null ? directory.getAbsolutePath() : "";
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public void addArgument(String argument) {
        if (this.arguments == null) {
            this.arguments = new ArrayList<>();
        }
        this.arguments.add(argument);
    }

    @Override
    public String toString() {
        return String.format("Tool [Name: %s, Shortcut: %s, Dir: %s, Args: %d]", 
            name, shortcut, directoryPath, arguments.size());
    }
}
