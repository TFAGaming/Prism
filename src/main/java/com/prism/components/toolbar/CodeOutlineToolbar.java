package com.prism.components.toolbar;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.prism.Prism;
import com.prism.config.Config;

public class CodeOutlineToolbar extends JPanel {
    public static final Prism prism = Prism.getInstance();
    public static boolean ignoreComments = prism.config.getBoolean(Config.Key.CODE_OUTLINE_IGNORE_COMMENTS, true);

    public CodeOutlineToolbar(Prism prism) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 0));

        JCheckBox checkboxIgnoreComments = new JCheckBox("Ignore comments");
        checkboxIgnoreComments.setFocusable(false);
        checkboxIgnoreComments.setSelected(ignoreComments);
        checkboxIgnoreComments.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ignoreComments = checkboxIgnoreComments.isSelected();

                prism.config.set(Config.Key.CODE_OUTLINE_IGNORE_COMMENTS, ignoreComments);
            }
        });
        
        add(checkboxIgnoreComments);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
    }

    public static boolean getIgnoreComments() {
        return ignoreComments;
    }
}
