package org.example;

import org.example.commands.logCommand;
import org.example.commands.projectCommand;

import org.example.database.DatabaseManager;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "np",
        mixinStandardHelpOptions = true,
        version = "notepad 1.0.0",
        description = "A simple command-line notepad application.",
        subcommands = {
                logCommand.class,
                projectCommand.class
        }
)
public class Notepad implements Runnable {


    public static void main(String[] args) {
        DatabaseManager.initialize();

        int exitCode = new CommandLine(new Notepad()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println(ConsoleColors.BLUE_BOLD + "Notepad can be used to modify text files. If you need help, run np --help to see subcommands." + ConsoleColors.RESET);
    }
}
