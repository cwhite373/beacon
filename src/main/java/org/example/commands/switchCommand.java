package org.example.commands;

import org.example.database.DatabaseManager;
import org.example.commands.projectCommand;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.List;

@Command(
        name = "switch",
        description = "used to switch projects",
        subcommands = {

        }
)

public class switchCommand extends AbstractCommand {



    @Parameters(index="0", description = "project you are working in")
    String project;

    @Override
    protected void execute() throws Exception {
        projectCommand.createProject(project);
        setActiveProject(project);
    }

    public void setActiveProject(String project_name) {
        String sqlSetInactive = "UPDATE projects SET is_active = 0 WHERE is_active = 1";
        String setActiveSql = "UPDATE projects SET is_active = 1 WHERE project_name = ?";
        try (Connection conn = DatabaseManager.getConnection();) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement(); PreparedStatement pstmt = conn.prepareStatement(setActiveSql)) {
                stmt.executeUpdate(sqlSetInactive);
                pstmt.setString(1, project_name);
                int updated = pstmt.executeUpdate();
                if (updated == 0) {
                    projectCommand.createProject(project_name);
                    System.out.println("Created new project: " + project_name);
                }

                conn.commit();
                System.out.println("Switched active project to " + project_name);
            } catch(SQLException e) {
                conn.rollback();
                System.err.println("Couldn't update the active project: " + e.getMessage());
            }

        } catch (SQLException e) {

            System.err.println("Couldn't set active project inactive.");
        }
    }
}
