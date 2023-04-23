package com.cmu.privacyplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class PrivacyUncheckAction extends AnAction {

    private PrivadoScanner privadoScanner;
    AnActionEvent event;
    public PrivacyUncheckAction() {
        super("Privacy Uncheck");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        System.out.println("===uncheck===");
        ProjectModel.cancelHighlight();
    }
    



}