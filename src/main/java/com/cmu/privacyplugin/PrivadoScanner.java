package com.cmu.privacyplugin;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrivadoScanner {
    AnActionEvent event;
    Project project;
    static ArrayList<String> filenameStr = new ArrayList<String>();
    static ArrayList<Integer> lines = new ArrayList<Integer>();
    AnAction actionCallback;
    private Lock lock = new ReentrantLock();

    PrivadoScanner(AnActionEvent event, AnAction action) {
        this.event = event;
        this.project = this.event.getProject();
        this.actionCallback = action;
    }
    public void scan() {
        new BackgroundTask().execute();

    }

    private class BackgroundTask {
        private void execute() {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    // run privado scan to get results
                    TerminalCommand terminal = new TerminalCommand();
                    // terminal.scan(project);
                    // parse result, mapping to filenameStr and lines array list
                    String basePath = event.getProject().getBasePath();
                    lock.lock();
                    try {
                        deserialize(basePath);
                    } finally {
                        lock.unlock();
                    }

                    // use UI thread to highlight files
                    ApplicationManager.getApplication().invokeLater(() -> {
                        lock.lock();
                        try {
                            ((PrivacyCheckAction) actionCallback).scanComplete(filenameStr, lines);
                        } finally {
                            lock.unlock();
                        }
                    });

                }
            });

        }
    }

    private static void deserialize(String basePath) {
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
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
