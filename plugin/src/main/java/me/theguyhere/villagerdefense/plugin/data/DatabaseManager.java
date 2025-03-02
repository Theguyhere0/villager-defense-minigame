package me.theguyhere.villagerdefense.plugin.data;

import lombok.Getter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.data.exceptions.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // Connection to the database
    @Getter
    private static boolean connected;
    private static Connection connection;

    // Information related to the specific table
    private final String tableName;

    public DatabaseManager(String tableName) {
        this.tableName = tableName;
    }

    public static void connect() {
        // Gather database info
        String host = Main.plugin.getConfig().getString("database.host");
        String port = Main.plugin.getConfig().getString("database.port");
        String database = Main.plugin.getConfig().getString("database.database");
        String username = Main.plugin.getConfig().getString("database.username");
        String password = Main.plugin.getConfig().getString("database.password");
        boolean disableSSL = Main.plugin.getConfig().getBoolean("database.disableSSL");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
            "?rewriteBatchedStatements=true&autoReconnect=true" + (disableSSL ? "&useSSL=false" : "");

        try {
            connection = DriverManager.getConnection(url, username, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
            CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, "Database connection failed. Check if the database is online and try again.");
        }
    }

    public static void createTable(String tableName, String columnConfig) throws DatabaseConnectionException {
        // Check for connection
        if (!connected) {
            throw new DatabaseConnectionException();
        }

        // Try executing statement to create table if it doesn't exist
        try {
            Statement s = connection.createStatement();
            s.execute("CREATE TABLE IF NOT EXISTS `" + tableName + "` ( " + columnConfig + " ) ENGINE = InnoDB;");
        } catch (SQLException e) {
            connected = false;
            throw new DatabaseConnectionException();
        }
    }
}
