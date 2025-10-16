package com.prism.components.toolbar;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.prism.Prism;
import com.prism.utils.ResourceUtil;

public class FileExplorerToolbar extends JToolBar {

    public FileExplorerToolbar(Prism prism) {
        setFloatable(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 0));

        JButton buttonNewFile = createButton(ResourceUtil.getIcon("icons/new_file.png"), "New File");
        buttonNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File targetDir = prism.fileExplorer.getSelectedFile();

                if (targetDir == null) {
                    return;
                }

                if (!targetDir.isDirectory()) {
                    targetDir = targetDir.getParentFile();
                }

                prism.fileExplorer.newFile(false);
            }
        });

        JButton buttonNewFolder = createButton(ResourceUtil.getIcon("icons/new_folder.gif"), "New Folder");
        buttonNewFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File targetDir = prism.fileExplorer.getSelectedFile();

                if (targetDir == null) {
                    return;
                }

                if (!targetDir.isDirectory()) {
                    targetDir = targetDir.getParentFile();
                }

                prism.fileExplorer.newFile(true);
            }
        });

        JButton buttonRefresh = createButton(ResourceUtil.getIcon("icons/refresh.gif"), "Refresh");
        buttonRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prism.fileExplorer.refresh();
            }
        });

        add(buttonNewFile);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonNewFolder);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonRefresh);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
    }

    private JButton createButton(ImageIcon buttonIcon, String tooltip) {
        JButton button = new JButton();

        if (tooltip != null) {
            button.setToolTipText(tooltip);
        }

        button.setPreferredSize(new Dimension(20, 20));

        if (buttonIcon != null) {
            Image scaledImage = buttonIcon.getImage().getScaledInstance(16, 16, Image.SCALE_FAST);
            button.setIcon(new ImageIcon(scaledImage));
        }

        button.setFocusPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}
