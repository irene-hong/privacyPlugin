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
        } catch (Exception e) {
            e.printStackTrace();
        }
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