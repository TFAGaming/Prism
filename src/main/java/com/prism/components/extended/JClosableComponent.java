package com.prism.components.extended;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.prism.Prism;
import com.prism.config.Config;
import com.prism.utils.ResourceUtil;

public class JClosableComponent extends JPanel {

    public static Prism prism = Prism.getInstance();

    public static enum ComponentType {
        SIDEBAR,
        LOWER_SIDEBAR
    }

    public final ComponentType type;

    public JClosableComponent(ComponentType type, JComponent header, JComponent component) {
        this.type = type;

        setLayout(new BorderLayout());

        JGradientPanel headerPanel = new JGradientPanel(Color.decode("#f0f0f0"), Color.decode("#bcbcbc"), JGradientPanel.Direction.BOTTOM_TO_TOP);
        headerPanel.setLayout(new BorderLayout());

        header.setOpaque(false);
        header.setBorder(new EmptyBorder(5, 5, 5, 0));

        headerPanel.add(header, BorderLayout.WEST);

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
                        case LOWER_SIDEBAR:
                            prism.config.set(Config.Key.SECONDARY_SPLITPANE_DIVIDER_LOCATION, prism.secondarySplitPane.getDividerLocation());
                            prism.secondarySplitPane.setDividerSize(0);
                            break;
                        case SIDEBAR:
                            prism.config.set(Config.Key.PRIMARY_SPLITPANE_DIVIDER_LOCATION, prism.secondarySplitPane.getDividerLocation());
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
