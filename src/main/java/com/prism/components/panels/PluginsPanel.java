package com.prism.components.panels;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class PluginsPanel extends JPanel {
    public PluginsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        /*
         * for (JSONObject plugin : plugins) {
         * String name = plugin.optString("name", "Unknown Plugin");
         * String description = plugin.optString("description", "");
         * boolean enabled = plugin.optBoolean("enabled", false);
         * 
         * JPanel item = new JPanel(new BorderLayout());
         * item.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
         * item.setBackground(Color.WHITE);
         * 
         * JLabel lblName = new JLabel("<html><b>" + name + "</b><br><small>" +
         * description + "</small></html>");
         * JCheckBox chkEnabled = new JCheckBox();
         * chkEnabled.setSelected(enabled);
         * 
         * item.add(lblName, BorderLayout.CENTER);
         * item.add(chkEnabled, BorderLayout.EAST);
         * add(item);
         * 
         * add(Box.createVerticalStrut(5));
         * }
         */
    }
}
