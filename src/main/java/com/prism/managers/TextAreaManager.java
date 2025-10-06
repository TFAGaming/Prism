package com.prism.managers;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.fife.ui.rtextarea.FoldIndicatorStyle;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.IconRowEvent;
import org.fife.ui.rtextarea.IconRowListener;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.prism.Prism;
import com.prism.components.files.PrismFile;
import com.prism.components.textarea.TextArea;
import com.prism.config.Config;
import com.prism.utils.ResourceUtil;

public class TextAreaManager {

    public static Prism prism = Prism.getInstance();

    public static void setGutter(RTextScrollPane scrollPane) {
        Gutter gutter = scrollPane.getGutter();

        Font lineNumberFont = gutter.getLineNumberFont();
        gutter.setLineNumberFont(lineNumberFont.deriveFont((float) prism.config.getInt(Config.Key.TEXTAREA_ZOOM, 12)));

        if (prism.config.getBoolean(Config.Key.BOOK_MARKS, true)) {
            gutter.setBookmarkingEnabled(true);
            gutter.setBookmarkIcon(ResourceUtil.getIcon("icons/bookmark.gif"));

            gutter.addIconRowListener(new IconRowListener() {
                @Override
                public void bookmarkAdded(IconRowEvent e) {
                    prism.bookmarks.updateTreeData(TextAreaManager.getBookmarksOfAllFiles());
                }

                @Override
                public void bookmarkRemoved(IconRowEvent e) {
                    prism.bookmarks.updateTreeData(TextAreaManager.getBookmarksOfAllFiles());
                }
            });
        }

        gutter.setFoldIndicatorStyle(FoldIndicatorStyle.CLASSIC);
    }

    public static List<BookmarkInfo> getBookmarksOfFile(PrismFile file) {
        RTextScrollPane scrollPane = file.getScrollPane();
        List<BookmarkInfo> bookmarks = new ArrayList<>();

        if (scrollPane == null) {
            return bookmarks;
        }

        Gutter gutter = scrollPane.getGutter();

        for (GutterIconInfo iconInfo : gutter.getBookmarks()) {
            try {
                int line;

                line = file.getTextArea().getLineOfOffset(iconInfo.getMarkedOffset());

                bookmarks.add(new BookmarkInfo(line));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        return bookmarks;
    }

    public static List<Bookmark> getBookmarksOfAllFiles() {
        List<Bookmark> bookmarks = new ArrayList<>();

        for (PrismFile file : FileManager.files) {
            List<BookmarkInfo> fileBookmarks = getBookmarksOfFile(file);

            bookmarks.add(new Bookmark(file, fileBookmarks));
        }

        return bookmarks;
    }

    public static void zoomIn() {
        if (FileManager.files.size() > 0) {
            for (PrismFile file : FileManager.files) {
                TextArea textArea = file.getTextArea();

                Font font = textArea.getFont();
                float size = font.getSize() + 1.0f;

                if (size > 35.0f) {
                    return;
                }

                textArea.setFont(font.deriveFont(size));

                RTextScrollPane scrollPane = (RTextScrollPane) SwingUtilities.getAncestorOfClass(RTextScrollPane.class,
                        textArea);
                if (scrollPane != null) {
                    Gutter gutter = scrollPane.getGutter();

                    Font lineNumberFont = gutter.getLineNumberFont();

                    gutter.setLineNumberFont(lineNumberFont.deriveFont(size));
                }

                prism.config.set(Config.Key.TEXTAREA_ZOOM, Math.round(size));
                prism.updateStatusBar();
            }
        }
    }

    public static void zoomOut() {
        if (FileManager.files.size() > 0) {
            for (PrismFile file : FileManager.files) {
                TextArea textArea = file.getTextArea();

                Font font = textArea.getFont();
                float size = font.getSize() - 1.0f;

                if (size < 5.0f) {
                    return;
                }

                textArea.setFont(font.deriveFont(size));

                RTextScrollPane scrollPane = (RTextScrollPane) SwingUtilities.getAncestorOfClass(RTextScrollPane.class,
                        textArea);
                if (scrollPane != null) {
                    Gutter gutter = scrollPane.getGutter();

                    Font lineNumberFont = gutter.getLineNumberFont();

                    gutter.setLineNumberFont(lineNumberFont.deriveFont(size));
                }

                prism.config.set(Config.Key.TEXTAREA_ZOOM, Math.round(size));
                prism.updateStatusBar();
            }
        }
    }

    public static class Bookmark {

        public PrismFile file;
        public List<BookmarkInfo> bookmarks = new ArrayList<>();

        public Bookmark(PrismFile file, List<BookmarkInfo> bookmarks) {
            this.file = file;
            this.bookmarks = bookmarks;
        }

        public PrismFile getFile() {
            return this.file;
        }

        public List<BookmarkInfo> getBookmarks() {
            return this.bookmarks;
        }
    }

    public static class BookmarkInfo {

        public int line;

        public BookmarkInfo(int line) {
            this.line = line;
        }

        public int getLine() {
            return this.line;
        }
    }
}
