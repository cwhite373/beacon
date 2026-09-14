package org.example.commands;

import picocli.CommandLine;

public abstract class AbstractCommand implements Runnable{

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    protected boolean helpRequested;

    @Override
    public final void run() {
        try {
            execute();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    protected abstract void execute() throws Exception;
}
