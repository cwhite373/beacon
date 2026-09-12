package org.example;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.Console;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

@CommandLine.Command(
        name = "add",
        mixinStandardHelpOptions = true,
        version = "add 1.0",
        description = "Allows you to add text to a text file. If the file does not exist, it will be created."
)

public class EditTxt implements Runnable {


    @CommandLine.Parameters(index = "0", description = "The text we are adding to the txt file.")
    private String text;
    
    @CommandLine.Parameters(index = "1", description = "The name of the txt file.")
    private String filename;

    //@CommandLine.ParentCommand
    //private Notepad parent;

    @Override
    public void run() {
        // Implement the logic to add text to the specified txt file
        System.out.printf("Adding text '%s' to file '%s'%n", text, filename);
        // Here you would add the code to actually write to the file
        Path path = Path.of(filename + ".txt");
        String existingContent = "";
        if (Files.exists(path)) {
            try {
                existingContent = Files.readString(path);
                System.out.println(ConsoleColors.YELLOW + "Existing content: " + existingContent + ConsoleColors.RESET);
            } catch (IOException e) {
                System.out.println(ConsoleColors.RED + "Failed to read existing content: " + e.getMessage() + ConsoleColors.RESET);
            }
        }
        String to_write = existingContent + text;
        try {
            Files.writeString(path, to_write + System.lineSeparator());
            System.out.printf(ConsoleColors.GREEN + "Successfully added text to '%s'%n", filename + ".txt" + ConsoleColors.RESET);
        } catch (IOException e) {
            System.out.println(ConsoleColors.RED + "Failed to write to file: " + e.getMessage() + ConsoleColors.RESET);
        }
    }
    
    public static void main (String[] args) {
        int exitCode = new CommandLine(new EditTxt()).execute(args);
        System.exit(exitCode);
    }
}
