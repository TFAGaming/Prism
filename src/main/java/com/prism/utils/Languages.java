package com.prism.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public class Languages {
    public static String getFullName(File file) {
        String extension = getFileExtension(file);

        switch (extension.toLowerCase()) {
            case "as":
                return "ActionScript";
            case "asm":
            case "s":
            case "inc":
                return "Assembly";
            case "c":
                return "The C Programming Language";
            case "clj":
            case "cljs":
            case "cljc":
                return "Clojure";
            case "cpp":
                return "C++";
            case "cs":
                return "C#";
            case "css":
                return "CSS";
            case "d":
                return "D";
            case "dart":
                return "Dart";
            case "f90":
            case "for":
            case "f":
                return "Fortran";
            case "go":
                return "Golang";
            case "groovy":
                return "Groovy";
            case "htm":
            case "html":
            case "ejs":
                return "HTML";
            case "ini":
            case "properties":
            case "prop":
            case "config":
                return "Properties";
            case "java":
                return "Java";
            case "js":
            case "mjs":
            case "cjs":
                return "JavaScript";
            case "json":
                return "JSON";
            case "jsonc":
                return "JSON with Comments";
            case "kt":
                return "Kotlin";
            case "tex":
                return "LaTeX";
            case "less":
                return "Less";
            case "lisp":
            case "lsp":
            case "cl":
                return "Lisp";
            case "lua":
                return "Lua";
            case "md":
            case "markdown":
                return "Markdown";
            case "mxml":
                return "MXML";
            case "plx":
            case "pls":
            case "pl":
            case "pm":
            case "xs":
            case "t":
            case "pod":
            case "psgi":
                return "The Perl Programming Language";
            case "php":
            case "phar":
            case "pht":
            case "phtml":
            case "phs":
                return "PHP";
            case "py":
            case "pyw":
            case "pyz":
            case "pyi":
            case "pyc":
            case "pyd":
                return "Python";
            case "rb":
            case "ru":
                return "Ruby";
            case "rs":
            case "rlib":
                return "Rust";
            case "sas":
                return "SAS Language";
            case "scala":
            case "sc":
                return "Scala";
            case "sql":
            case "sqlite":
            case "db":
                return "SQL";
            case "ts":
            case "tsx":
            case "mts":
            case "cts":
                return "TypeScript";
            case "sh":
                return "Unix Shell";
            case "bat":
                return "Windows Batch";
            case "vb":
                return "Visual Basic";
            case "xml":
                return "XML";
            case "yml":
            case "yaml":
                return "YAML";
            case "txt":
                return "Text File";
            default:
                return "Plain Text";
        }
    }

    public static String getHighlighter(File file) {
        String extension = getFileExtension(file);

        switch (extension) {
            case "as":
                return SyntaxConstants.SYNTAX_STYLE_ACTIONSCRIPT;
            case "asm":
            case "s":
            case "inc":
                return SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86;
            case "c":
                return SyntaxConstants.SYNTAX_STYLE_C;
            case "clj":
            case "cljs":
            case "cljc":
                return SyntaxConstants.SYNTAX_STYLE_CLOJURE;
            case "cpp":
                return SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS;
            case "cs":
                return SyntaxConstants.SYNTAX_STYLE_CSHARP;
            case "css":
                return SyntaxConstants.SYNTAX_STYLE_CSS;
            case "d":
                return SyntaxConstants.SYNTAX_STYLE_D;
            case "dart":
                return SyntaxConstants.SYNTAX_STYLE_DART;
            case "f90":
            case "for":
            case "f":
                return SyntaxConstants.SYNTAX_STYLE_FORTRAN;
            case "go":
                return SyntaxConstants.SYNTAX_STYLE_GO;
            case "groovy":
                return SyntaxConstants.SYNTAX_STYLE_GROOVY;
            case "htm":
            case "html":
            case "ejs":
                return SyntaxConstants.SYNTAX_STYLE_HTML;
            case "ini":
            case "properties":
            case "prop":
            case "config":
                return SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE;
            case "java":
                return SyntaxConstants.SYNTAX_STYLE_JAVA;
            case "js":
            case "mjs":
            case "cjs":
                return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
            case "json":
                return SyntaxConstants.SYNTAX_STYLE_JSON;
            case "jsonc":
                return SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS;
            case "kt":
                return SyntaxConstants.SYNTAX_STYLE_KOTLIN;
            case "tex":
                return SyntaxConstants.SYNTAX_STYLE_LATEX;
            case "less":
                return SyntaxConstants.SYNTAX_STYLE_LESS;
            case "lisp":
            case "lsp":
            case "cl":
                return SyntaxConstants.SYNTAX_STYLE_LISP;
            case "lua":
                return SyntaxConstants.SYNTAX_STYLE_LUA;
            case "md":
            case "markdown":
                return SyntaxConstants.SYNTAX_STYLE_MARKDOWN;
            case "mxml":
                return SyntaxConstants.SYNTAX_STYLE_MXML;
            case "plx":
            case "pls":
            case "pl":
            case "pm":
            case "xs":
            case "t":
            case "pod":
            case "psgi":
                return SyntaxConstants.SYNTAX_STYLE_PERL;
            case "php":
            case "phar":
            case "pht":
            case "phtml":
            case "phs":
                return SyntaxConstants.SYNTAX_STYLE_PHP;
            case "py":
            case "pyw":
            case "pyz":
            case "pyi":
            case "pyc":
            case "pyd":
                return SyntaxConstants.SYNTAX_STYLE_PYTHON;
            case "rb":
            case "ru":
                return SyntaxConstants.SYNTAX_STYLE_RUBY;
            case "rs":
            case "rlib":
                return SyntaxConstants.SYNTAX_STYLE_RUST;
            case "sas":
                return SyntaxConstants.SYNTAX_STYLE_SAS;
            case "scala":
            case "sc":
                return SyntaxConstants.SYNTAX_STYLE_SCALA;
            case "sql":
            case "db":
                return SyntaxConstants.SYNTAX_STYLE_SQL;
            case "ts":
            case "tsx":
            case "mts":
            case "cts":
                return SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT;
            case "sh":
                return SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
            case "bat":
                return SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH;
            case "vb":
                return SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC;
            case "xml":
                return SyntaxConstants.SYNTAX_STYLE_XML;
            case "yml":
            case "yaml":
            case "yarn": // .yarn's syntax looks like YAML syntax.
                return SyntaxConstants.SYNTAX_STYLE_YAML;
            default:
                return SyntaxConstants.SYNTAX_STYLE_NONE;
        }
    }

    public static String getIconName(File file) {
        String extension = getFileExtension(file);

        switch (extension) {
            case "as":
                return "as.png";
            case "asm":
            case "s":
            case "inc":
                return "asm.png";
            case "c":
                return "c.png";
            case "clj":
            case "cljs":
            case "cljc":
                return "clojure.png";
            case "cpp":
                return "cpp.png";
            case "cs":
                return "cs.png";
            case "css":
                return "css.png";
            case "d":
                return "d.png";
            case "dart":
                return "dart.png";
            case "f90":
            case "for":
            case "f":
                return "f90.png";
            case "go":
                return "go.png";
            case "groovy":
                return "groovy.png";
            case "htm":
            case "html":
            case "ejs":
                return "html.png";
            case "ini":
            case "properties":
            case "prop":
            case "config":
                return "config.png";
            case "java":
                return "java.png";
            case "js":
            case "mjs":
            case "cjs":
                return "javascript.png";
            case "json":
                return "json.png";
            case "jsonc":
                return "jsonc.png";
            case "kt":
                return "kotlin.png";
            case "tex":
                return "tex.png";
            case "less":
                return "less.png";
            case "lisp":
            case "lsp":
            case "cl":
                return "lisp.png";
            case "lua":
                return "lua.png";
            case "md":
            case "markdown":
                return "markdown.png";
            case "mxml":
                return "mxml.png";
            case "plx":
            case "pls":
            case "pl":
            case "pm":
            case "xs":
            case "t":
            case "pod":
            case "psgi":
                return "perl.png";
            case "php":
            case "phar":
            case "pht":
            case "phtml":
            case "phs":
                return "php.png";
            case "py":
            case "pyw":
            case "pyz":
            case "pyi":
            case "pyc":
            case "pyd":
                return "python.png";
            case "rb":
            case "ru":
                return "ruby.png";
            case "rs":
            case "rlib":
                return "rust.png";
            case "sas":
                return "sas.png";
            case "scala":
            case "sc":
                return "scala.png";
            case "sql":
            case "db":
            case "sqlite":
                return "sql.png";
            case "ts":
            case "tsx":
            case "mts":
            case "cts":
                return "typescript.png";
            case "sh":
                return "shell.png";
            case "bat":
                return "batch.png";
            case "vb":
                return "visualbasic.png";
            case "xml":
                return "xml.png";
            case "yml":
            case "yaml":
                return "yaml.png";
            default:
                return "file.png";
        }
    }

    public static List<String> getSupported() {
        List<String> extensions = new ArrayList<String>();
        String[] supportedExtensions = {
            "as",
            "asm",
            "s",
            "inc",
            "c",
            "clj",
            "cljs",
            "cljc",
            "cpp",
            "cs",
            "css",
            "d",
            "dart",
            "f90",
            "for",
            "f",
            "go",
            "groovy",
            "htm",
            "html",
            "ejs",
            "ini",
            "properties",
            "prop",
            "config",
            "java",
            "js",
            "mjs",
            "cjs",
            "json",
            "jsonc",
            "kl",
            "kt",
            "lex",
            "less",
            "lisp",
            "lsp",
            "cl",
            "lua",
            "md",
            "markdown",
            "mxml",
            "plx",
            "pls",
            "pl",
            "pm",
            "xs",
            "t",
            "pod",
            "psgi",
            "php",
            "phar",
            "pht",
            "phtml",
            "phs",
            "py",
            "pyw",
            "rb",
            "ru",
            "rs",
            "rlib",
            "sas",
            "scala",
            "sc",
            "ts",
            "tsx",
            "mts",
            "cts",
            "sh",
            "bat",
            "vb",
            "xml",
            "yml",
            "yaml",
            "log",
            "txt",
            "env",
            "gitignore",
            "git",
            "npmignore",
            "yarn"
        };

        for (String ext : supportedExtensions) {
            extensions.add(ext);
        }

        return extensions;
    }

    public static boolean isSupported(File file) {
        String extension = getFileExtension(file);
        return getSupported().contains(extension);
    }

    public static Icon getIcon(File file) {
        String iconName = getIconName(file);

        Icon icon = ResourceUtil.getIcon("icons/" + iconName);

        if (icon != null) {
            return icon;
        } else {
            return ResourceUtil.getIcon("icons/file.png");
        }
    }

    private static String getFileExtension(File file) {
        if (file == null || file.getName() == null) {
            return "";
        }

        String name = file.getName();

        int lastDot = name.lastIndexOf('.');

        if (lastDot == -1 || lastDot == name.length() - 1) {
            return "";
        }

        return name.substring(lastDot + 1).toLowerCase();
    }
}
