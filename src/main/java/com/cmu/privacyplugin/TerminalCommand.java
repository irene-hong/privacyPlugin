package com.cmu.privacyplugin;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TerminalCommand {
    public TerminalCommand() {}

    public BufferedReader runCommand(String[] cmdArray) {
        try {
            // Create the process builder and set the command
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(cmdArray);

            // Start the process
            Process process = processBuilder.start();

            // Get the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // Wait for the command to complete
            int exitCode = process.waitFor();
            System.out.println("Command exited with code " + exitCode);
            return reader;
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