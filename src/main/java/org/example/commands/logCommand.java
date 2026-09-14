package org.example.commands;

import org.example.database.DatabaseManager;

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
        name = "log",
        description = "Add a log with a timestamp to the current workspace.",
        subcommands = {
            viewSubcommand.class
        }
)

public class logCommand extends AbstractCommand {

    @Parameters(index="0", arity = "0...1", description = "The log content to be added.")
    private String message;

    @Option(
            names = {"-t", "--tag"},
            split = ",",
            description = "Comma separated lists of tags to associate with the log entry."
    )
    private List<String> tags;

    public void execute() {
        if (tags == null) {
            String sql = "INSERT INTO items (type, content) VALUES ('LOG', ?)";
            try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, message);
                pstmt.executeUpdate();
                System.out.println("Log entry saved successfully.");
            } catch (SQLException e) {
                System.out.println("Error saving log entry: " + e.getMessage());
            }
        } else {
            String sqlInsertItem = "INSERT INTO items (type, content) VALUES ('LOG', ?)";
            String sqlInsertTag = "INSERT INTO tags (name) VALUES (?) ON CONFLICT(name) DO UPDATE SET name=name";
            String getTagIdSql = "SELECT id FROM tags WHERE name = ?";
            String insertItemTagSql = "INSERT OR IGNORE INTO item_tags (item_id, tag_id) VALUES (?, ?)";

            try (Connection conn = DatabaseManager.getConnection()) {
                conn.setAutoCommit(false);

                long itemId;
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsertItem, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, message);
                    pstmt.executeUpdate();
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        rs.next();
                        itemId = rs.getLong(1);
                    }
                }

                if (tags != null && !tags.isEmpty()) {
                    for (String rawTag : tags) {
                        String tag = rawTag.trim();
                        if (tag.isEmpty()) continue;
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsertTag)) {
                            pstmt.setString(1, tag);
                            pstmt.executeUpdate();
                        }

                        long tagId;
                        try (PreparedStatement pstmt = conn.prepareStatement(getTagIdSql)) {
                            pstmt.setString(1, tag);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                rs.next();
                                tagId = rs.getLong(1);
                            }
                        }

                        try (PreparedStatement pstmt = conn.prepareStatement(insertItemTagSql)) {
                            pstmt.setLong(1, itemId);
                            pstmt.setLong(2, tagId);
                            pstmt.executeUpdate();
                        }
                    }
                }
                conn.commit();
                System.out.println("Log entry and tags saved successfully.");

            } catch (SQLException e) {
                System.out.println("Failed to save log entry: " + e.getMessage());
            }
        }
    }
}
