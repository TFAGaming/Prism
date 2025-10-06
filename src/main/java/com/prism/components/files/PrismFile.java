package com.prism.components.files;

import java.io.File;

import javax.swing.Icon;

import org.fife.ui.rtextarea.RTextScrollPane;

import com.prism.components.textarea.TextArea;
import com.prism.utils.Languages;
import com.prism.utils.ResourceUtil;

public class PrismFile {
    public File file = null;
    public TextArea textArea;
    public RTextScrollPane scrollPane = null;
    public boolean isSaved = true;

    public PrismFile(File file, TextArea textArea) {
        this.file = file;
        this.textArea = textArea;
    }

    public File setFile(File file) {
        return this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public String getFileName() {
        return this.file == null ? "Untitled" : this.file.getName();
    }

    public String getPath() {
        return this.file == null ? null : this.file.getAbsolutePath();
    }

    public Icon getIcon() {
        return this.file == null ? ResourceUtil.getIcon("icons/file.png") : Languages.getIcon(this.file);
    }

    public TextArea getTextArea() {
        return this.textArea;
    }

    public RTextScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public void setScrollPane(RTextScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }
    
    public boolean isSaved() {
        return this.isSaved;
    }

    public void setSaved(boolean value) {
        this.isSaved = value;
    }
}
