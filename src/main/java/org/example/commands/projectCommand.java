package org.example.commands;

import org.example.database.DatabaseManager;
import org.example.commands.switchCommand;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Command(
        name = "project",
        description = "Allows for project management.",
        subcommands = {
                switchCommand.class
        }
)

public class projectCommand extends AbstractCommand {



    @Parameters(index="0", arity = "0...1", description = "The name of the project.")
    String name;

    @Override
    protected void execute() throws Exception {
        createProject(name);
    }

    public static void createProject(String name) {
        String sql = "INSERT OR IGNORE INTO projects (project_name, root_path) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, System.getProperty("user.dir"));
        } catch (Exception e) {
            System.err.println("Failed to fetch/create project: " + e.getMessage());
        }
    }
}
