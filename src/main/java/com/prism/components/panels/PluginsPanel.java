package com.prism.components.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import com.prism.Prism;
import com.prism.managers.FileManager;
import com.prism.plugins.Plugin;

public class PluginsPanel extends JPanel {
    public Prism prism = Prism.getInstance();

    public PluginsPanel(List<Plugin> plugins) {
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < plugins.size(); i++) {
            Plugin plugin = plugins.get(i);
            
            JPanel pluginPanel = createPluginPanel(plugin);
            pluginPanel.setAlignmentX(Component.LEFT_ALIGNMENT); 
            contentPanel.add(pluginPanel);

            if (i < plugins.size() - 1) {
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setAlignmentX(Component.LEFT_ALIGNMENT);
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                contentPanel.add(separator);
            }
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);

        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private static JPanel createPluginPanel(Plugin plugin) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel(String.format("<html><b>%s</b></html>", plugin.getName()));
        nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        panel.add(nameLabel, gbc);

        JCheckBox enabledCheckbox = new JCheckBox("Enabled");
        enabledCheckbox.setFocusable(false);
        enabledCheckbox.setSelected(plugin.getEnabled());
        enabledCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                plugin.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(enabledCheckbox, gbc);

        JLabel descriptionLabel = new JLabel(
                "<html><p style='width:99%;'>" + plugin.getDescription() + "</p></html>");
        descriptionLabel.setFont(new Font(descriptionLabel.getFont().getName(), Font.PLAIN, 10));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(descriptionLabel, gbc);

        JButton editButton = new JButton("Edit JSON");
        editButton.setFocusable(false);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openFile(plugin.getFile());
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0; 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(editButton, gbc);

        return panel;
    }
}