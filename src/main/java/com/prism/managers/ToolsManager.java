package com.prism.managers;

import java.util.ArrayList;
import java.util.List;

import com.prism.Prism;
import com.prism.components.frames.EditToolFrame;

public class ToolsManager {
    public static Prism prism = Prism.getInstance();

    public static List<EditToolFrame.Tool> tools = new ArrayList<>();

    public static void addTool(EditToolFrame.Tool tool) {
        tools.add(tool);

        prism.toolsList.addRow(tool);
    }

    public static void removeTool(EditToolFrame.Tool tool) {
        tools.remove(tool);

        for (int i = 0; i < tools.size(); i++) {
            if (tools.get(i) == tool) {
                prism.toolsList.removeRow(i);
            }
        }
    }
}
