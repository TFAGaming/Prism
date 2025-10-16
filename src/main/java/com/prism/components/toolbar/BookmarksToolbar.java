package com.prism.components.toolbar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.prism.Prism;
import com.prism.managers.TextAreaManager;

public class BookmarksToolbar extends JPanel {

    public BookmarksToolbar(Prism prism) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 0));

        JButton buttonRefreshAll = createButton("Refresh", null, "Reload All Bookmarks");
        buttonRefreshAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prism.bookmarks.updateTreeData(TextAreaManager.getBookmarksOfAllFiles());
            }
        });

        JButton buttonClearAll = createButton("Clear All", null, "Clear All Bookmarks");
        buttonClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        add(buttonRefreshAll);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonClearAll);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
    }

    private JButton createButton(String label, ImageIcon buttonIcon, String tooltip) {
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(80, 25));
        button.setFocusable(true);

        if (tooltip != null) {
            button.setToolTipText(tooltip);
        }

        if (buttonIcon != null) {
            Image scaledImage = buttonIcon.getImage().getScaledInstance(16, 16, Image.SCALE_FAST);
            button.setIcon(new ImageIcon(scaledImage));
        }

        return button;
    }
}
