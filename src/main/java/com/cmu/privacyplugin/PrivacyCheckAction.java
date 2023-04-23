package com.cmu.privacyplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class PrivacyCheckAction extends AnAction {

    private PrivadoScanner privadoScanner;
    AnActionEvent event;
    public PrivacyCheckAction() {
        super("Privacy Check");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        this.event = event;
        // Using the event, implement an action.
        // For example, create and show a dialog.
        TerminalCommand terminal = new TerminalCommand();
        if (!terminal.testCommand()) {
            return;
        }

        privadoScanner = new PrivadoScanner(event, this);
        privadoScanner.scan();


    }

    public void scanComplete(ArrayList<String> filenameStr, ArrayList<Integer> lines) {
        ProjectModel projectModel = new ProjectModel(event);
        projectModel.highLightResult(filenameStr, lines);
    }



}