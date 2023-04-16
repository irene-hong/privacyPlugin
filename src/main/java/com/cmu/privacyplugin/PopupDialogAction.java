package com.cmu.privacyplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.google.gson.stream.JsonReader;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;



import java.lang.reflect.Array;
import java.util.ArrayList;

import com.google.gson.stream.JsonReader;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;



import java.lang.reflect.Array;
import java.util.ArrayList;

import com.google.gson.stream.JsonReader;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;



public class PopupDialogAction extends AnAction {

    static ArrayList<String> filenameStr = new ArrayList<String>();
    static ArrayList<Integer> lines = new ArrayList<Integer>();
    public PopupDialogAction() {
        super("Privacy Check");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Using the event, implement an action.
        // For example, create and show a dialog.
        TerminalCommand terminal = new TerminalCommand();
        String basePath = event.getProject().getBasePath();
        ProjectModel projectModel = new ProjectModel(event);

        if (!terminal.testCommand()) {
            return;
        }
// TODO: uncomment this after debugging
//        terminal.scan(event.getProject());
        System.out.println("===scan completed===");

        // parse result, mapping to filenameStr and lines array list
        deserialize(basePath);

        // get current open file
        String openFilePath = projectModel.getCurrentOpenFile().getPath();

        for(int i = 0; i < filenameStr.size(); i++){
            String currentPath = basePath + filenameStr.get(i).substring(9);
            if (currentPath.equals(openFilePath)){
                highlightLine(event.getProject(), openFilePath, lines.get(i));
            }
        }

        projectModel.highLightTrace();

    }

    public void highlightLine(Project project, String filePath, int lineNumber) {
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
        //RangeHighlighter highlighter = markupModel.addRangeHighlighter(startOffset, endOffset, HighlighterLayer.ERROR, null, HighlighterTargetArea.EXACT_RANGE);

        // Change the highlighter's color
        markupModel.addRangeHighlighter(startOffset, endOffset, HighlighterLayer.SELECTION - 1,
                editor.getColorsScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES), HighlighterTargetArea.EXACT_RANGE);

        // Save the changes
        FileDocumentManager.getInstance().saveDocument(editor.getDocument());
    }
    private static void deserialize(String basePath) {
        System.out.println("===start deserialize===");
        try {
            InputStream inputStream = Files.newInputStream(Path.of(basePath + "/.privado/privado.json").toAbsolutePath());
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
            PrivadoOutput output = new Gson().fromJson(reader, PrivadoOutput.class);
            PrivadoOutput.DataFlow df = output.dataFlow;
            for(int i = 0; i < df.leakages.size(); i++){
                ArrayList<PrivadoOutput.Sink> sinks = df.leakages.get(i).sinks;
                for (int j = 0; j < sinks.size(); j++){
                    ArrayList<PrivadoOutput.Path> paths = sinks.get(j).paths;

                    for(int k = 0; k < paths.size(); k++){
                        ArrayList<PrivadoOutput.Path2> path2s = paths.get(k).path;
                        for(int m = 0; m < path2s.size(); m++){
                            filenameStr.add(path2s.get(m).fileName);
                            lines.add(path2s.get(m).lineNumber);
//                            System.out.println("detected filename: " + path2s.get(m).fileName + " in line " + path2s.get(m).lineNumber);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("===end deserialize===");
    }

    // Override getActionUpdateThread() when you target 2022.3 or later!

}