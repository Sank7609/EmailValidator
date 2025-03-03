package com.example.emailvalidator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class EmailValidatorApp {
    private JFrame frame;
    private JTextField emailField;
    private JLabel resultLabel;
    private DefaultTableModel emailTableModel, feedbackTableModel;
    private JTable emailTable, feedbackTable;
    private JTextArea feedbackArea;
    private JPanel emailTablePanel, feedbackTablePanel;
    private JButton hideEmailsButton, hideFeedbackButton;

    public EmailValidatorApp() {
        frame = new JFrame("📧 Email Validator & Feedback");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(214, 234, 248)); // Light blue background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // 🔹 Input Section
        JPanel inputPanel = createStyledPanel("Enter Email");
        emailField = new JTextField(20);
        JButton validateButton = createStyledButton("✔ Validate & Save");

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 12));

        inputPanel.add(new JLabel("📩 Email: "));
        inputPanel.add(emailField);
        inputPanel.add(validateButton);
        inputPanel.add(resultLabel);

        gbc.gridx = 0; gbc.gridy = 0;
        frame.add(inputPanel, gbc);

        // 🔹 Buttons Section
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(214, 234, 248));

        JButton viewEmailsButton = createStyledButton("📜 View Saved Emails");
        hideEmailsButton = createStyledButton("❌ Hide Emails");
        JButton viewFeedbackButton = createStyledButton("💬 View Feedback");
        hideFeedbackButton = createStyledButton("❌ Hide Feedback");

        hideEmailsButton.setVisible(false);
        hideFeedbackButton.setVisible(false);

        buttonPanel.add(viewEmailsButton);
        buttonPanel.add(hideEmailsButton);
        buttonPanel.add(viewFeedbackButton);
        buttonPanel.add(hideFeedbackButton);

        gbc.gridy = 1;
        frame.add(buttonPanel, gbc);

        // 🔹 Email Table (Initially Hidden)
        emailTableModel = new DefaultTableModel(new String[]{"📧 Email"}, 0);
        emailTable = new JTable(emailTableModel);
        emailTable.setBackground(new Color(240, 240, 240));
        JScrollPane emailScrollPane = new JScrollPane(emailTable);

        emailTablePanel = createStyledPanel("📜 Saved Emails");
        emailTablePanel.add(emailScrollPane);
        emailTablePanel.setVisible(false);

        gbc.gridy = 2;
        frame.add(emailTablePanel, gbc);

        // 🔹 Feedback Section
        feedbackArea = new JTextArea(3, 30);
        JButton submitFeedbackButton = createStyledButton("📤 Submit Feedback");

        JPanel feedbackPanel = createStyledPanel("💬 Feedback");
        feedbackPanel.add(new JScrollPane(feedbackArea));
        feedbackPanel.add(submitFeedbackButton);

        gbc.gridy = 3;
        frame.add(feedbackPanel, gbc);

        // 🔹 Feedback Table (Initially Hidden)
        feedbackTableModel = new DefaultTableModel(new String[]{"💡 Feedback"}, 0);
        feedbackTable = new JTable(feedbackTableModel);
        feedbackTable.setBackground(new Color(240, 240, 240));
        JScrollPane feedbackScrollPane = new JScrollPane(feedbackTable);

        feedbackTablePanel = createStyledPanel("💬 Feedback Reports");
        feedbackTablePanel.add(feedbackScrollPane);
        feedbackTablePanel.setVisible(false);

        gbc.gridy = 4;
        frame.add(feedbackTablePanel, gbc);

        // 🔹 Button Actions
        validateButton.addActionListener(e -> new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { validateAndSaveEmail(); return null; }
        }.execute());

        submitFeedbackButton.addActionListener(e -> new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { submitFeedback(); return null; }
        }.execute());

        viewEmailsButton.addActionListener(e -> new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { displaySavedEmails(); return null; }
            @Override protected void done() {
                emailTablePanel.setVisible(true);
                hideEmailsButton.setVisible(true);
                frame.revalidate();
            }
        }.execute());

        viewFeedbackButton.addActionListener(e -> new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { displayFeedback(); return null; }
            @Override protected void done() {
                feedbackTablePanel.setVisible(true);
                hideFeedbackButton.setVisible(true);
                frame.revalidate();
            }
        }.execute());

        hideEmailsButton.addActionListener(e -> {
            emailTablePanel.setVisible(false);
            hideEmailsButton.setVisible(false);
            frame.revalidate();
        });

        hideFeedbackButton.addActionListener(e -> {
            feedbackTablePanel.setVisible(false);
            hideFeedbackButton.setVisible(false);
            frame.revalidate();
        });

        frame.setVisible(true);
    }

    // ✅ Create Styled Panel with Border
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), title, TitledBorder.LEADING, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.BLACK));
        return panel;
    }

    // ✅ Create Styled Button
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(100, 149, 237)); // Soft blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    // ✅ Validate & Save Email
    private void validateAndSaveEmail() {
        String email = emailField.getText().trim();
        boolean isValid = EmailValidator.isValidEmail(email);
        boolean isSaved = isValid && DatabaseManager.saveEmail(email);

        SwingUtilities.invokeLater(() -> {
            if (isSaved) {
                resultLabel.setText("✔ Valid & Saved");
                resultLabel.setForeground(new Color(34, 139, 34)); // Green
            } else {
                resultLabel.setText(isValid ? "⚠ Duplicate or DB Error" : "❌ Invalid Email");
                resultLabel.setForeground(Color.RED);
            }
        });
    }

    // ✅ Display Saved Emails in Table
    private void displaySavedEmails() {
        new SwingWorker<List<String>, Void>() {
            @Override protected List<String> doInBackground() { return DatabaseManager.getEmails(); }
            @Override protected void done() {
                try {
                    emailTableModel.setRowCount(0);
                    for (String email : get()) emailTableModel.addRow(new Object[]{email});
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    // ✅ Submit User Feedback
    private void submitFeedback() {
        String feedback = feedbackArea.getText().trim();
        if (!feedback.isEmpty()) {
            DatabaseManager.saveFeedback(feedback);
            SwingUtilities.invokeLater(() -> feedbackArea.setText(""));
        }
    }

    // ✅ Display Feedback in Table
    private void displayFeedback() {
        new SwingWorker<List<String>, Void>() {
            @Override protected List<String> doInBackground() { return DatabaseManager.getFeedback(); }
            @Override protected void done() {
                try {
                    feedbackTableModel.setRowCount(0);
                    for (String feedback : get()) feedbackTableModel.addRow(new Object[]{feedback});
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmailValidatorApp::new);
    }
}
