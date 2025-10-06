package com.prism.managers;

import java.util.ArrayList;
import java.util.List;

import com.prism.Prism;
import com.prism.components.terminal.Terminal;

public class TerminalManager {
    public static Prism prism = Prism.getInstance();
    public static int terminalId = 0;

    public static List<Terminal> terminals = new ArrayList<>();

    public static void newTerminal(int shell) {
        Terminal terminal = new Terminal(FileManager.getDirectory(), shell);

        terminalId++;

        terminals.add(terminal);

        prism.terminalTabbedPane.addTerminalTab(terminal, shell, terminalId);
    }

    public static void closeAllTabs() {
        int size = terminals.size();

        for (int index = size - 1; index >= 0; index--) {
            Terminal terminal = terminals.get(index);

            terminal.closeProcess();

            prism.terminalTabbedPane.closeTabByIndex(index, true);
        }
    }

    public static void stopAllProcesses() {
        for (Terminal terminal : terminals) {
            terminal.closeProcess();
        }
    }
}
