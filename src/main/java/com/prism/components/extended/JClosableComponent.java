package com.prism.components.extended;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.prism.Prism;
import com.prism.utils.ResourceUtil;

public class JClosableComponent extends JPanel {
    public static Prism prism = Prism.getInstance();

    public static enum ComponentType {
        SIDEBAR,
        LOWER_SIDEBAR
    }

    public final ComponentType type;

    public JClosableComponent(ComponentType type, List<JComponent> headerComponents, JComponent component) {
        this.type = type;

        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());

        JPanel secondaryHeaderPanel = new JPanel();
        secondaryHeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        for (JComponent headerComponent : headerComponents) {
            secondaryHeaderPanel.add(headerComponent);
        }

        headerPanel.add(secondaryHeaderPanel, BorderLayout.WEST);

        JButton closeButton = new JButton();
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setPreferredSize(new Dimension(16, 16));

        Image scaledImage = ResourceUtil.getIcon("icons/close_component.gif").getImage().getScaledInstance(12, 12,
                Image.SCALE_FAST);
        closeButton.setIcon(new ImageIcon(scaledImage));

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parent = JClosableComponent.this.getParent();

                if (parent != null) {
                    parent.remove(JClosableComponent.this);
                    parent.revalidate();
                    parent.repaint();

                    prism.removedComponents.add(JClosableComponent.this);

                    switch (JClosableComponent.this.getType()) {
                        case SIDEBAR:
                            prism.secondarySplitPane.setDividerSize(0);
                            break;
                        case LOWER_SIDEBAR:
                            prism.primarySplitPane.setDividerSize(0);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        headerPanel.add(closeButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        add(component, BorderLayout.CENTER);
    }

    public ComponentType getType() {
        return this.type;
    }
}