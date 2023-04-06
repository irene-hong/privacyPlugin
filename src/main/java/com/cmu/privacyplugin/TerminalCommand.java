package com.cmu.privacyplugin;
import com.intellij.openapi.project.Project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.colors.EditorColors;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.*;

public class TerminalCommand {
    final String[] windowsCommandTest = new String[]{"cmd", "/c", "dir"};
    final String[] macCommandTest = new String[]{"pwd"};

    public class StandardIo {
        BufferedReader stdout, stderr;

        public StandardIo(InputStream stdoutStream, InputStream stderrStream) {
            this.stdout = new BufferedReader(new InputStreamReader(stdoutStream));
            this.stderr = new BufferedReader(new InputStreamReader(stderrStream));
        }
    }

    public TerminalCommand() {}


    public boolean testCommand() {
        try {
            String[] testCmd = System.getProperty("os.name").startsWith("Windows")
                    ? windowsCommandTest : macCommandTest;
            System.out.println("Current OS: " + System.getProperty("os.name"));
            StandardIo outputReader = runCommand(testCmd);
            printTerminal(outputReader.stdout);
            printTerminal(outputReader.stderr);
            return true;
        } catch (Exception e) {
            System.out.println("Error occurs while running command test.");
            e.printStackTrace();
            return false;
        }
    }


    public void highlightLine(Project project, String filePath, int lineNumber) {
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

    /**
     * Call "privado scan" command to scan the currently opened project.
     * By default, it will overwrite any existing result.
     *
     * This local version requires users to have Docker installed and opened
     * while using the plugin.
     *
     * @param project
     */
    public void scan(Project project) {
        String projectPath = project.getBasePath();
        System.out.println("Scan project: " + project.getName());
        System.out.println("Project base path: " + project.getBasePath());


        if(System.getProperty("os.name").startsWith("Windows")){
            projectPath = "/mnt/" + projectPath.substring(0,1).toLowerCase()  +
                    projectPath.substring(2);
        }

        String[] scanCmd = new String[]{"privado", "scan", projectPath, "--overwrite"};
        // --overwrite: If specified, the warning prompt for existing scan results
        // is disabled and any existing results are overwritten

        if(System.getProperty("os.name").startsWith("Windows")){
            System.out.println("Please make sure Docker running and wsl installed.");
            System.out.println("Run \ncurl -o- https://raw.githubusercontent.com/Privado-Inc/privado-cli/main/install.sh" +
                    " | bash\n to install privado");
            scanCmd = new String[]{"wsl", "/home/chenlyu/.privado/bin/privado", "scan", projectPath, "--overwrite"};
        }

        runCommandRealtime(scanCmd);
    }

    /**
     * Execute command specified in cmdArray, and print the output
     * in terminal in real-time.
     * @param cmdArray
     */
    public void runCommandRealtime(String[] cmdArray) {
        // ref: https://stackoverflow.com/questions/58272702/get-process-output-in-real-time-with-java
        try {
            System.out.println("**Executing: " + String.join(" ", cmdArray));

            // Create the process builder and set the command
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.redirectErrorStream(true);
            processBuilder.command(cmdArray);
            Process process = processBuilder.start();

            // print output in real time
            try(InputStreamReader isr = new InputStreamReader(process.getInputStream())) {
                int c;
                while((c = isr.read()) >= 0) {
                    System.out.print((char) c);
                    System.out.flush();
                }
            }
            System.out.println("**Execution completed**");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute command specified in cmdArray, and return the result
     * as a StandardIo object. If you want to print output in realtime,
     * use runCommandRealtime() instead.
     * @param cmdArray
     */
    public StandardIo runCommand(String[] cmdArray) {
        try {
            System.out.println("**Executing: " + String.join(" ", cmdArray));
            // Create the process builder and set the command
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(cmdArray);

            // Start the process
            Process process = processBuilder.start();

            // Get the output of the command
            StandardIo standardIo = new StandardIo(process.getInputStream(), process.getErrorStream());

            // Wait for the command to complete
            int exitCode = process.waitFor();
            System.out.println("Command exited with code " + exitCode);
            return standardIo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void printTerminal(BufferedReader reader) {
        try{
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}