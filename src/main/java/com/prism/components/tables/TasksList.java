package com.prism.components.tables;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.prism.components.definition.Tool;
import com.prism.managers.ToolsManager;

public class TasksList extends JPanel {
    public JTable table;
    public DefaultTableModel model;

    public String[] columns = { "File", "Line", "Name", "Description" };

    public TasksList() {
        setLayout(new BorderLayout());

        Object[][] data = {};

        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFocusable(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(10);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setModel(model);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public Tool getSelectedTool() {
        int column = table.getSelectedColumn();

        if (column == -1) {
            return null;
        }

        return ToolsManager.tools.get(column);
    }

    public void addRow(Tool tool) {
        model.addRow(new Object[] { tool.getName(), tool.getDescription() });
    }

    public void removeRow(int rowIndex) {
        model.removeRow(rowIndex);
    }
}
