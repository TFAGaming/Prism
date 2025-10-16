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
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.prism.Prism;

public class TasksToolbar extends JPanel {

    public TasksToolbar(Prism prism) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 0));

        JButton buttonNewTask = createButton("New", null, "New Task");
        buttonNewTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });

        JButton buttonEditTask = createButton("Edit", null, "Edit Task");
        buttonEditTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });

        JButton buttonDeleteTask = createButton("Delete", null, "Delete Task");
        buttonDeleteTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });

        JCheckBox checkboxSendNotification = new JCheckBox("Send notification on Windows?");
        checkboxSendNotification.setFocusable(true);
        checkboxSendNotification.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        add(buttonNewTask);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonEditTask);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonDeleteTask);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(checkboxSendNotification);

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
