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


public class PopupDialogAction extends AnAction {

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
        Editor editor = FileEditorManager.getInstance(event.getProject()).getSelectedTextEditor();
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        String filePath = file.getPath();
        terminal.highlightLine(event.getProject(), filePath, 3);

    }



    // Override getActionUpdateThread() when you target 2022.3 or later!

}