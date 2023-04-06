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
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.google.gson.stream.JsonReader;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;


public class JSONParser {

    public static void main(String[] args) throws IOException {
        String jsonStr = readJsonFile(args[0]);
        JsonArray dataflow = JsonParser.parseString(jsonStr).getAsJsonObject().
                getAsJsonArray("dataflow");

        //storages, misc, internal, leakage.. under dataflow
        for (JsonElement dataflowElement : dataflow) {
            JsonArray pathArr = dataflowElement.getAsJsonArray();
            //index under storage, misc, leakage...
            for (JsonElement pathElem : pathArr) {
                JsonArray pathObj = pathElem.getAsJsonArray('sinks');
                // sinks under indexes
                for (JsonElement i : pathObj) {
                    JsonArray pathnameArr = i.getAsJsonArray("paths");

                    for (JsonElement filenameElem : pathnameArr) {
                        String filename = filenameElem.getAsString('fileName');
                        System.out.println(filename);
                    }
                }
            }
        }
    }

    private static String readJsonFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }
}



public class PopupDialogAction extends AnAction {




    private PrivadoScanner privadoScanner;
    AnActionEvent event;
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





    // Override getActionUpdateThread() when you target 2022.3 or later!

}