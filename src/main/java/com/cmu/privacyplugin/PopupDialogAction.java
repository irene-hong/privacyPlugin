package com.cmu.privacyplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class PopupDialogAction extends AnAction {
    public PopupDialogAction() {
        super("Privacy Check");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Using the event, implement an action.
        // For example, create and show a dialog.
        TerminalCommand terminal = new TerminalCommand();
        TerminalCommand.StandardIo outputReader = terminal.runCommand(new String[]{"cmd", "/c", "dir"});
        terminal.printTerminal(outputReader.stdout);
        terminal.printTerminal(outputReader.stderr);
    }

    // Override getActionUpdateThread() when you target 2022.3 or later!

}