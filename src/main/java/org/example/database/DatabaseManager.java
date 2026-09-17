package org.example.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_DIR = System.getProperty("user.dir") + File.separator + ".np";
    private static final String DB_URL = "jdbc:sqlite:" + DB_DIR + File.separator + "np.db";

    public static Connection getConnection() throws SQLException {
        ensureDatabaseDirectoryExists();
        return DriverManager.getConnection(DB_URL);
    }

    private static void ensureDatabaseDirectoryExists() {
        File folder = new File(DB_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void initialize() {
        String createItemsTable = """
                CREATE TABLE IF NOT EXISTS items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type VARCHAR(10) NOT NULL,
                    title TEXT,
                    content TEXT NOT NULL,
                    file_path TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );""";

        String createTagsTable = """
                CREATE TABLE IF NOT EXISTS tags (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name VARCHAR(50) NOT NULL UNIQUE
                );
                """;

        String createItemTagsTable = """
                CREATE TABLE IF NOT EXISTS item_tags (
                    item_id INTEGER REFERENCES items(id) ON DELETE CASCADE,
                    tag_id INTEGER REFERENCES tags(id) ON DELETE CASCADE,
                    PRIMARY KEY (item_id, tag_id)
                );""";

        String createHistoryTable = """
                CREATE TABLE IF NOT EXISTS action_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    command_name VARCHAR(50) NOT NULL,
                    undo_data TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );""";

        String createProjectTable = """
                CREATE TABLE IF NOT EXISTS projects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    project_name VARCHAR(50) UNIQUE NOT NULL,
                    root_path TEXT,
                    port INTEGER,
                    env_vars TEXT,
                    is_active BOOLEAN DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """;
        String createSessionsTable = """
                CREATE TABLE IF NOT EXISTS sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE,
                    description TEXT NOT NULL,
                    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    ended_at TIMESTAMP,
                    duration INTEGER
                );
                """;

        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();

            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute(createItemsTable);
            stmt.execute(createTagsTable);
            stmt.execute(createItemTagsTable);
            stmt.execute(createHistoryTable);
            stmt.execute(createProjectTable);
            stmt.execute(createSessionsTable);

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

}
