package com.cmu.privacyplugin;
import com.intellij.openapi.project.Project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


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


    public void scan(Project project) {
        // TODO: dynamically fetch open project path
        System.out.println(project.getBasePath());
        String projectPath = project.getBasePath();
        System.out.println(project.getName());
//        String[] cdCmd = new String[]{"cd", project.getBasePath()};
//
//        outputReader = runCommand(cdCmd);
//        printTerminal(outputReader.stdout);
//        printTerminal(outputReader.stderr);
//        testCommand();


        String[] scanCmd = new String[]{"privado", "scan", projectPath, "--overwrite"};
        // --overwrite: If specified, the warning prompt for existing scan results
        // is disabled and any existing results are overwritten
        StandardIo outputReader = runCommand(scanCmd);
        printTerminal(outputReader.stdout);
        printTerminal(outputReader.stderr);

    }

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