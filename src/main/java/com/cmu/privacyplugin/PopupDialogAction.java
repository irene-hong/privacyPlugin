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
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
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




public class PopupDialogAction extends AnAction {




    private PrivadoScanner privadoScanner;
    AnActionEvent event;
    static ArrayList<String> filenameStr = new ArrayList<String>();
    static ArrayList<Integer> lines = new ArrayList<Integer>();
    public PopupDialogAction() {
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




    private static void serialization(AnActionEvent event) {
        try {
            InputStream inputStream = Files.newInputStream(Path.of(event.getProject().getBasePath()
                    + "/.privado/privado.json").toAbsolutePath());
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
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Override getActionUpdateThread() when you target 2022.3 or later!

}