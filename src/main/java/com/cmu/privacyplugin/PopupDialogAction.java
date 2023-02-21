package com.cmu.privacyplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;

public class PopupDialogAction extends AnAction {
    public PopupDialogAction() {
        super("Privacy Check");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Using the event, implement an action.
        // For example, create and show a dialog.
        TerminalCommand terminal = new TerminalCommand();
        BufferedReader outputReader = terminal.runCommand(new String[]{"cmd", "/c", "dir"});
        terminal.printTerminal(outputReader);
    }

    // Override getActionUpdateThread() when you target 2022.3 or later!

}