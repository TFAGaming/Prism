package com.prism.components.textarea;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fife.ui.rtextarea.RTextScrollPane;

import com.prism.Prism;
import com.prism.components.files.PrismFile;
import com.prism.managers.FileManager;
import com.prism.managers.TextAreaManager;
import com.prism.utils.ResourceUtil;

public class TextAreaTabbedPane extends JTabbedPane {

    public Prism prism = Prism.getInstance();

    public TextAreaTabbedPane() {
        super();

        setFocusable(true);
        setTabLayoutPolicy(SCROLL_TAB_LAYOUT);

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                PrismFile file = getCurrentFile();

                if (file != null) {
                    FileManager.updateTitle(file);
                }
            }
        });
    }

    public void addTextAreaTab(PrismFile file) {
        RTextScrollPane scrollPane = new RTextScrollPane(file.getTextArea());

        TextAreaManager.setGutter(scrollPane);

        file.setScrollPane(scrollPane);

        addTab(file.getFileName(), scrollPane);

        addFeaturesToTab(file);
    }

    public void addImageViewerTab(PrismFile file) {
        addTab(file.getFileName(), file.getImageViewerContainer());

        addFeaturesToTab(file);
    }

    public void removeTextAreaTab(TextArea textArea) {
        int index = findIndexByTextArea(textArea);

        if (index != -1) {
            removeTabAt(index);
        }
    }

    public void removeTextAreaTab(PrismFile file) {
        int index = findIndexByPrismFile(file);

        if (index != -1) {
            removeTabAt(index);
        }
    }

    public void redirectUserToTab(PrismFile file) {
        int index = findIndexByPrismFile(file);

        if (index != -1) {
            setSelectedIndex(index);
        }
    }

    public int findIndexByTextArea(TextArea textArea) {
        int index = indexOfComponent(textArea);

        return index;
    }

    public int findIndexByPrismFile(PrismFile file) {
        for (int i = 0; i < getTabCount(); i++) {
            Component componentIndex = getComponentAt(i);

            if (componentIndex instanceof RTextScrollPane) {
                RTextScrollPane scrollPane = (RTextScrollPane) componentIndex;
                TextArea textArea = (TextArea) scrollPane.getViewport().getView();

                if (textArea == file.getTextArea()) {
                    return i;
                }
            } else {
                ImageViewerContainer container = (ImageViewerContainer) componentIndex;

                if (container == file.getImageViewerContainer()) {
                    return i;
                }
            }
        }

        return -1;
    }

    public void closeAllTabs() {
        int size = FileManager.files.size();

        for (int index = size - 1; index >= 0; index--) {
            closeTabByIndex(index, true);
        }
    }

    public void closeTabByIndex(int index, boolean... openNewFileIfAllTabsAreClosed) {
        if (index < 0 || index >= getTabCount()) {
            return;
        }

        PrismFile fileIndex = getFileFromIndex(index);

        if (!fileIndex.isSaved()) {
            int response = JOptionPane.showConfirmDialog(Prism.getInstance(),
                    "This file is marked with new changes, do you want to save it?", "File Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                FileManager.saveFile();
            } else if (response == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        removeTabAt(index);

        FileManager.files.remove(index);

        if (openNewFileIfAllTabsAreClosed.length == 1 && openNewFileIfAllTabsAreClosed[0]) {
            openNewFileIfAllTabsAreClosed();
        }

        if (getTabCount() > 0) {
            PrismFile file = getCurrentFile();

            FileManager.updateTitle(file);
        }

        prism.bookmarks.updateTreeData(TextAreaManager.getBookmarksOfAllFiles());
    }

    public void updateTitle(PrismFile file) {
        int index = findIndexByPrismFile(file);

        if (index != -1) {
            JPanel tabPanel = (JPanel) getTabComponentAt(index);

            if (tabPanel != null && tabPanel.getComponent(0) instanceof JLabel) {
                JLabel tabTitle = (JLabel) tabPanel.getComponent(0);
                tabTitle.setText(file.getFileName() + (!file.isSaved() ? "*" : ""));
            }
        }
    }

    public PrismFile getCurrentFile() {
        return getFileFromIndex(getSelectedIndex());
    }

    public PrismFile getFileFromIndex(int index) {
        if (index < 0 || index >= getTabCount()) {
            return null;
        }

        return FileManager.files.get(index);
    }

    public void openNewFileIfAllTabsAreClosed() {
        if (getTabCount() == 0) {
            FileManager.openNewFile();
        }
    }

    public void addFeaturesToTab(PrismFile file) {
        int index = findIndexByPrismFile(file);

        if (index != -1) {
            addFeaturesToTab(index, file.getIcon());
        }
    }

    public void addFeaturesToTab(int index, Icon icon) {
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);
        tabPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel tabTitle = new JLabel(getTitleAt(index), icon, JLabel.LEFT);
        tabTitle.setIconTextGap(5);
        tabTitle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event)) {
                    JPopupMenu contextMenu = new JPopupMenu();

                    JMenuItem closeItem = new JMenuItem("Close");
                    closeItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int index = indexOfTabComponent(tabPanel);

                            closeTabByIndex(index, true);
                        }
                    });

                    JMenuItem closeAllItem = new JMenuItem("Close All");
                    closeAllItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            closeAllTabs();
                        }
                    });

                    JMenuItem copyPathItem = new JMenuItem("Copy Path");
                    copyPathItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int tabIndex = indexOfTabComponent(tabPanel);
                            PrismFile prismFile = FileManager.files.get(tabIndex);

                            if (prismFile == null) {
                                return;
                            }

                            File file = prismFile.getFile();

                            if (file == null) {
                                return;
                            }

                            copyToClipboard(file.getAbsolutePath());
                        }
                    });

                    contextMenu.add(closeItem);
                    contextMenu.add(closeAllItem);
                    contextMenu.addSeparator();
                    contextMenu.add(copyPathItem);

                    Point point = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), tabPanel);
                    contextMenu.show(tabPanel, point.x, point.y);
                } else if (SwingUtilities.isLeftMouseButton(event)) {
                    setSelectedIndex(indexOfTabComponent(tabPanel));
                }
            }
        });

        JButton closeButton = new JButton("  ✕");
        closeButton.setPreferredSize(new Dimension(17, 17));
        closeButton.setFocusable(true);
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = indexOfTabComponent(tabPanel);

                closeTabByIndex(index, true);
            }
        });

        tabPanel.add(tabTitle, BorderLayout.WEST);
        tabPanel.add(closeButton, BorderLayout.EAST);

        setTabComponentAt(index, tabPanel);
    }

    public void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public class ImagePanel extends JPanel {

        private BufferedImage image;
        private double scale = 1.0;
        private final String imagePath;

        public ImagePanel(String path) {
            this.imagePath = path;
            try {
                this.image = ImageIO.read(new File(path));

                if (this.image == null) {
                    System.err.println("Error: Could not read image format for " + path);
                    createPlaceholderImage("Error reading image format.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                createPlaceholderImage("Failed to load image: " + new File(path).getName());
            }

            setLayout(new BorderLayout());
        }

        private void createPlaceholderImage(String message) {
            int width = 400;
            int height = 300;
            this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = this.image.createGraphics();
            g2d.fillRect(0, 0, width, height);

            g2d.setFont(new Font("Inter", Font.BOLD, 16));

            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(message)) / 2;
            int y = (fm.getAscent() + (height - fm.getHeight()) / 2);
            g2d.drawString(message, x, y);

            g2d.dispose();
        }

        public void setScale(double newScale) {
            if (newScale > 0.1 && newScale < 10.0) {
                this.scale = newScale;
                revalidate();
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                Graphics2D g2d = (Graphics2D) g.create();

                int scaledWidth = (int) (image.getWidth() * scale);
                int scaledHeight = (int) (image.getHeight() * scale);

                int x = (getWidth() - scaledWidth) / 2;
                int y = (getHeight() - scaledHeight) / 2;

                g2d.drawImage(image, x, y, scaledWidth, scaledHeight, this);

                g2d.dispose();
            }
        }

        @Override
        public Dimension getPreferredSize() {
            if (image == null) {
                return new Dimension(500, 400);
            }

            return new Dimension(
                    (int) (image.getWidth() * scale),
                    (int) (image.getHeight() * scale));
        }

        public double getScale() {
            return scale;
        }
    }

    public class ImageViewerContainer extends JPanel {

        private final ImagePanel imagePanel;
        private final JLabel zoomLabel;

        public ImageViewerContainer(String imagePath) {
            super(new BorderLayout());

            this.imagePanel = new ImagePanel(imagePath);
            this.zoomLabel = new JLabel("Zoom: 100%", SwingConstants.CENTER);

            JScrollPane scrollPane = new JScrollPane(imagePanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            addInteractiveListeners(scrollPane);

            JToolBar zoomBar = createZoomToolBar();

            add(zoomBar, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        private JToolBar createZoomToolBar() {
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Zoom In Button
            JButton zoomInButton = new JButton();
            zoomInButton.setIcon(ResourceUtil.getIcon("icons/zoom_in.png"));
            zoomInButton.setFocusable(true);
            zoomInButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            zoomInButton.addActionListener(e -> {
                imagePanel.setScale(imagePanel.getScale() * 1.2);
                updateZoomLabel();
            });

            // Zoom Out Button
            JButton zoomOutButton = new JButton();
            zoomOutButton.setIcon(ResourceUtil.getIcon("icons/zoom_out.png"));
            zoomOutButton.setFocusable(true);
            zoomOutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            zoomOutButton.addActionListener(e -> {
                imagePanel.setScale(imagePanel.getScale() / 1.2);
                updateZoomLabel();
            });

            // Reset Button
            JButton resetButton = new JButton("1:1");
            resetButton.setFocusable(true);
            resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            resetButton.addActionListener(e -> {
                imagePanel.setScale(1.0);
                updateZoomLabel();
            });

            toolBar.add(zoomInButton);
            toolBar.add(Box.createHorizontalStrut(5));
            toolBar.add(zoomOutButton);
            toolBar.add(Box.createHorizontalStrut(10));
            toolBar.add(resetButton);
            toolBar.add(Box.createHorizontalGlue());
            toolBar.add(zoomLabel);

            return toolBar;
        }

        private void updateZoomLabel() {
            int percent = (int) (imagePanel.getScale() * 100);
            zoomLabel.setText("Zoom: " + percent + "%");
        }

        private void addInteractiveListeners(JScrollPane scrollPane) {
            imagePanel.addMouseWheelListener(e -> {
                if (e.isControlDown()) {
                    double newScale = imagePanel.getScale();
                    int rotation = e.getWheelRotation();

                    if (rotation < 0) {
                        newScale *= 1.1;
                    } else {
                        newScale /= 1.1;
                    }

                    imagePanel.setScale(newScale);
                    updateZoomLabel();
                    e.consume();
                }
            });
        }
    }
}
