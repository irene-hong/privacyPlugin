package com.cmu.privacyplugin;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProjectModel {

    private final String PRIVADO_CODE_PATH = "/app/code";

    AnActionEvent event;
    PsiFileIterator psiIterator;
    Project project;

    ProjectModel(AnActionEvent event) {
        this.event = event;
        this.project = this.event.getProject();
        psiIterator = new PsiFileIterator(project);
    }

    public void highLightResult(ArrayList<String> detectedFiles, ArrayList<Integer> detectedLines) {
        ArrayList<String> filePaths = getFilePaths(detectedFiles);
        while (psiIterator.hasNext()) {
            PsiFile file = psiIterator.next();

            // Do something with the file
            if (file != null && file.getName().contains(".java")) {
                String currentFilePath = file.getOriginalFile().getVirtualFile().getPath();
//                System.out.println("vfile path: " + currentFilePath);
                if (filePaths.contains(currentFilePath)) {
                    int ind = filePaths.indexOf(currentFilePath);
                    highlightLine(filePaths.get(ind), detectedLines.get(ind));
                }
            }
        }
    }

    /**
     * For an array of filenames, map them to an array of absolute file paths.
     * The order of each item will remain unchanged in the new arraylist.
     *
     * Specifically, privado.json uses filenames like "/app/code/projectpath/file.java".
     * The function will first strip the "/app/code" part, and then combine the rest with project's
     * base path to form an absolute file path.
     *
     * @param filenames
     * @return an arraylist of absolute file paths.
     */
    private ArrayList<String> getFilePaths(ArrayList<String> filenames) {
        String basePath = project.getBasePath();
        ArrayList<String> filepaths = new ArrayList<>();
        for (String filename: filenames) {
            filepaths.add(basePath + filename.split(PRIVADO_CODE_PATH)[1]);
        }
        return filepaths;
    }

    public void highlightLine(String filePath, int lineNumber) {
        System.out.println("**highlight line " + lineNumber + " in " + filePath);
        if (lineNumber < 0) {
            return;
        }
        // Get the editor for the file
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filePath);
        Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, file), true);

        // Highlight the specified line
        MarkupModel markupModel = editor.getMarkupModel();
        int startOffset = editor.getDocument().getLineStartOffset(lineNumber - 1);
        int endOffset = editor.getDocument().getLineEndOffset(lineNumber + 1);
        // RangeHighlighter highlighter = markupModel.addRangeHighlighter(startOffset, endOffset, HighlighterLayer.ERROR, null, HighlighterTargetArea.EXACT_RANGE);

        // Change the highlighter's color
        markupModel.addRangeHighlighter(startOffset, endOffset, HighlighterLayer.SELECTION - 1,
                editor.getColorsScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES), HighlighterTargetArea.EXACT_RANGE);

        // Save the changes
        FileDocumentManager.getInstance().saveDocument(editor.getDocument());
    }


    public VirtualFile getCurrentOpenFile() {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        String filePath = file.getPath();
        return file;
    }

    public Project getCurrentProject() {
        return event.getProject();
    }
}
