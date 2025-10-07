package com.prism.components.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.prism.Prism;
import com.prism.utils.ResourceUtil;

public class LoadingFrame extends JFrame {
    public Prism prism = Prism.getInstance();

    public String VERSION = Prism.getVersion();
    public String AUTHOR = "TFAGaming";
    public String YEAR = "2025";

    public LoadingFrame() {
        setTitle("Prism");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new FlowLayout());

        setIconImage(ResourceUtil.getIcon("icons/Prism.png").getImage());

        add(createPanel());

        pack();
        
        setLocationRelativeTo(null);
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(ResourceUtil.getIcon("icons/Prism.png", 64));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.add(iconLabel, BorderLayout.CENTER);
        logoPanel.setPreferredSize(new Dimension(80, 80));

        JLabel titleLabel = new JLabel(
                "<html><center><b>Prism</b><br><font size='-1'>Version " + VERSION + "</font></center></html>");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.add(logoPanel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(250, 20));

        JLabel copyrightLabel = new JLabel("Copyright \u00a9 " + YEAR + " " + AUTHOR + ". All rights reserved.");
        copyrightLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.add(progressBar, BorderLayout.CENTER);
        contentPanel.add(copyrightLabel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }
}