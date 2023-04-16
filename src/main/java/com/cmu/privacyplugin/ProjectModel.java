package com.cmu.privacyplugin;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class ProjectModel {

    AnActionEvent event;
    PsiFileIterator psiIterator;

    ProjectModel(AnActionEvent event) {
        this.event = event;
        psiIterator = new PsiFileIterator(this.event.getProject());

    }

    public void highLightTrace() {
        while (psiIterator.hasNext()) {
            PsiFile file = psiIterator.next();
            // Do something with the file
            if (file != null && file.getName().contains(".java")) {
                System.out.println("current file: " + file.getName());
            }
        }
    }


    public VirtualFile getCurrentOpenFile() {
        Editor editor = FileEditorManager.getInstance(event.getProject()).getSelectedTextEditor();
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        String filePath = file.getPath();
        return file;
    }

    public Project getCurrentProject() {
        return event.getProject();
    }
}
