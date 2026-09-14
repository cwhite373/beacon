package org.example.commands;

import org.example.database.DatabaseManager;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Command(
        name = "view",
        description = "Use this command to view all log entries for the current day."
)

public class viewSubcommand extends AbstractCommand {



    @Override
    protected void execute() throws Exception {
        String sql = """
                SELECT id, content, created_at FROM items WHERE type = 'LOG' AND DATE(created_at, 'localtime') = DATE('now', 'localtime') ORDER BY created_at asc;
                """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("content");
                String time = rs.getString("created_at");

                System.out.printf("[%d] %s - %s\n", id, time, content);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch log entries: " + e.getMessage());
        }
    }
}
