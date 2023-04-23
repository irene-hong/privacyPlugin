package com.cmu.privacyplugin;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
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

    /**
     * Highlight a specific line in a given file.
     * @param filePath an absolute file path string
     * @param lineNumber the line number to be highlighted
     */
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

    public static void underline(VirtualFile file, int lineNumber) {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        // Get the document for the file
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document == null) {
            return;
        }

        // Create an editor for the document
        Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, file), true);

        // Get the markup model for the editor
        MarkupModel markupModel = editor.getMarkupModel();
        // Get the start and end offsets for the line
        int startOffset = editor.getDocument().getLineStartOffset(lineNumber - 1);
        int endOffset = editor.getDocument().getLineEndOffset(lineNumber + 1);

        // set underline style
        final TextAttributes attr = new TextAttributes();
//        attr.setForegroundColor(JBColor.RED);
        attr.setEffectColor(JBColor.GRAY);
        attr.setEffectType(EffectType.WAVE_UNDERSCORE);

        // Create a range highlighter for the line
        markupModel.addRangeHighlighter(startOffset, endOffset, HighlighterLayer.SELECTION - 1,
                attr, HighlighterTargetArea.EXACT_RANGE);

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
