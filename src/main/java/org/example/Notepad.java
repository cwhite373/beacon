package org.example;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

@Command(
        name = "notepad",
        mixinStandardHelpOptions = true,
        version = "notepad 1.0",
        description = "A simple command-line notepad application."
        //subcommands = {EditTxt.class}
)
public class Notepad implements Runnable {


    public static void main(String[] args) {
        int exitCode = new CommandLine(new Notepad()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println(ConsoleColors.BLUE_BOLD + "Notepad can be used to modify text files. If you need help, use the --help command." + ConsoleColors.RESET);
    }
}
