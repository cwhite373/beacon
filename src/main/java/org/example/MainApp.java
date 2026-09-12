package org.example;

import picocli.CommandLine;

public class MainApp {
    public static void main (String[] args) {
        CommandLine commandLine = new CommandLine(new Notepad()).addSubcommand("add", new EditTxt());
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }
}
