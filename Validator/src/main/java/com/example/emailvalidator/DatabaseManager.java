package com.example.emailvalidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/email_validator";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "sank1234";  

    static {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            // Create tables if not exist
            String createEmailsTable = "CREATE TABLE IF NOT EXISTS emails (id INT AUTO_INCREMENT PRIMARY KEY, email VARCHAR(255) UNIQUE)";
            String createFeedbackTable = "CREATE TABLE IF NOT EXISTS feedback (id INT AUTO_INCREMENT PRIMARY KEY, message TEXT)";
            
            stmt.execute(createEmailsTable);
            stmt.execute(createFeedbackTable);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a valid email to the database.
     * @param email The email to save.
     * @return True if saved successfully, false otherwise.
     */
    public static boolean saveEmail(String email) {
        String query = "INSERT INTO emails (email) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all saved emails from the database.
     * @return List of emails.
     */
    public static List<String> getEmails() {
        List<String> emails = new ArrayList<>();
        String query = "SELECT email FROM emails";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                emails.add(rs.getString("email"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving emails: " + e.getMessage());
        }
        return emails;
    }

    /**
     * Saves user feedback to the database.
     * @param feedback The feedback message to save.
     * @return True if saved successfully, false otherwise.
     */
    public static boolean saveFeedback(String feedback) {
        String query = "INSERT INTO feedback (message) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, feedback);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving feedback: " + e.getMessage());
            return false;
        }
    }

    public static List<String> getFeedback() {
        List<String> feedbackList = new ArrayList<>();
        String query = "SELECT message FROM feedback";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                feedbackList.add(rs.getString("message"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving feedback: " + e.getMessage());
        }
        return feedbackList;
    }
}
