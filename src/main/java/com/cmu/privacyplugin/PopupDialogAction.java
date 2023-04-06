package com.cmu.privacyplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
        if (!terminal.testCommand()) {
            return;
        }
        terminal.scan(event.getProject());
        System.out.println("===scan completed===");
        Editor editor = FileEditorManager.getInstance(event.getProject()).getSelectedTextEditor();
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        String filePath = file.getPath();
        String basePath = event.getProject().getBasePath();
        deserialize(event);
        System.out.println(basePath+filenameStr.get(0).substring(9));
        System.out.println(filePath);


        for(int i = 0; i<filenameStr.size(); i++){
            if( (basePath + filenameStr.get(i).substring(9)).equals(filePath)){
                terminal.highlightLine(event.getProject(), filePath, lines.get(i));
            }
        }

    }


    private static void deserialize(AnActionEvent event) {
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