package com.cmu.privacyplugin;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyListenerClass implements BulkFileListener {

    AccDataRegex data = new AccDataRegex();
    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        // handle the events
        for(int i = 0; i < events.size(); i++){
            VirtualFile file = events.get(i).getFile();
                Document document = FileDocumentManager.getInstance().getDocument(file);
            if(document != null) {
                String content = document.getText();
                String[] lines = content.split("\\r?\\n");
                for (String line : lines) {
                    String type = data.getType(line);
                    if (type != "None") {
                        System.out.println(type);
                    }
                }
            }
        }
    }
}